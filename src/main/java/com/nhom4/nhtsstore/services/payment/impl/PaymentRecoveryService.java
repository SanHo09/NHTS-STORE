package com.nhom4.nhtsstore.services.payment.impl;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentMethod;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import com.nhom4.nhtsstore.repositories.OrderRepository;
import com.nhom4.nhtsstore.services.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class PaymentRecoveryService {

    private final OrderRepository orderRepository;
    private final IPaymentService paymentService;
    private final ApplicationContext applicationContext;
    private final PlatformTransactionManager transactionManager;
    private static final int CONCURRENT_BATCHES = 4;
    private static final int BATCH_SIZE = 50;

    @Autowired
    public PaymentRecoveryService(OrderRepository orderRepository,
                                  IPaymentService paymentService,
                                  ApplicationContext applicationContext, PlatformTransactionManager transactionManager) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.applicationContext = applicationContext;
        this.transactionManager = transactionManager;
    }
    // Get proxy instance to avoid self-invocation transaction issues
    private PaymentRecoveryService getSelf() {
        return applicationContext.getBean(PaymentRecoveryService.class);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void recoverPendingPayments() {
        log.info("Starting payment recovery process");
        getSelf().processPendingPaymentsInBatches();
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void checkStalePayments() {
        log.info("Checking for stale payments in the background");
        getSelf().processPendingPaymentsInBatches();
    }

    @Transactional
    public void processPendingPaymentsInBatches() {
        log.info("Starting concurrent batch processing with virtual threads");

        long totalOrderCount = orderRepository.countByPaymentStatusIsNullOrPaymentStatus(PaymentStatus.PENDING);
        if (totalOrderCount == 0) {
            log.info("No pending orders to process");
            return;
        }

        log.info("Found {} orders to process", totalOrderCount);
        int totalPages = (int) Math.ceil((double) totalOrderCount / BATCH_SIZE);
        AtomicInteger processedBatches = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            for (int pageNumber = 0; pageNumber < totalPages; pageNumber++) {
                final int currentPage = pageNumber;

                Future<?> future = executor.submit(() -> {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(status -> {
                        processBatch(currentPage);
                        int completed = processedBatches.incrementAndGet();
                        log.info("Completed batch {}/{}", completed, totalPages);
                        return null;
                    });
                });

                futures.add(future);
            }

            // Wait for all batches to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Batch processing interrupted", e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("Error in batch processing", e.getCause());
                }
            }
        }

        log.info("Completed processing all {} batches", totalPages);
    }

    private void processBatch(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
        Page<Order> orderPage = orderRepository.findByPaymentStatusIsNullOrPaymentStatusPaged(
                PaymentStatus.PENDING, pageable);

        List<Order> batchToUpdate = new ArrayList<>();

        for (Order order : orderPage.getContent()) {
            // Skip already completed orders
            if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
                continue;
            }

            if (shouldUpdateOrder(order)) {
                batchToUpdate.add(order);
            }
        }

        if (!batchToUpdate.isEmpty()) {
            log.info("Batch {}: Saving {} updated orders", pageNumber, batchToUpdate.size());
            orderRepository.saveAll(batchToUpdate);
        }
    }

    private boolean shouldUpdateOrder(Order order) {
        try {

            LocalDateTime orderTime = order.getCreatedOn().toLocalDateTime();
            LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

            if (orderTime.isBefore(fifteenMinutesAgo)) {
                updateOrderStatus(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error processing recovery for order {}: {}", order.getId(), e.getMessage());
            return false;
        }
    }

    private void updateOrderStatus(Order order) {
        if (order.getPaymentMethod() != PaymentMethod.CASH) {
            try {
                PaymentStatus actualStatus = paymentService.checkPaymentStatus(order);
                if (actualStatus != PaymentStatus.COMPLETED) {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    log.info("Payment ID {} marked as FAILED after timeout", order.getId());
                }
            } catch (Exception e) {
                order.setPaymentStatus(PaymentStatus.FAILED);
                log.warn("Payment ID {} couldn't be verified, marking as FAILED", order.getId());
            }
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            log.info("Payment ID {} marked as FAILED (cash)", order.getId());
        }
    }
}