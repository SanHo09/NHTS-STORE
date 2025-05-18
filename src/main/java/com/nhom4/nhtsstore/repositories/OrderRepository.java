package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Order;
import com.nhom4.nhtsstore.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends GenericRepository<Order,Long>{

    long countByPaymentStatusIsNullOrPaymentStatus(PaymentStatus status);
    @Query("SELECT o FROM Order o WHERE o.paymentStatus IS NULL OR o.paymentStatus = :status")
    Page<Order> findByPaymentStatusIsNullOrPaymentStatusPaged(PaymentStatus status, Pageable pageable);

    List<Order> findByCreateDateBetween(Date startDate, Date endDate);

    @Query("SELECT FUNCTION('FORMAT',o.createDate, 'MM') as month, " +
            "YEAR(o.createDate) as year, " +
            "SUM(o.totalAmount) as revenue " +
            "FROM Order o " +
            "WHERE o.createDate BETWEEN :startDate AND :endDate AND o.deliveryStatus = 'COMPLETED'" +
            "GROUP BY month,year "+
            "ORDER BY year, month")
    List<Object[]> getRevenueByMonthAndYear(Date startDate, Date endDate);

    @Query("SELECT FUNCTION('FORMAT', o.createDate, 'MM/yyyy') as month, SUM(o.totalAmount) as revenue " +
            "FROM Order o WHERE o.createDate BETWEEN :startDate AND :endDate AND o.deliveryStatus = 'COMPLETED'" +
            "GROUP BY FUNCTION('FORMAT', o.createDate, 'MM/yyyy') " +
            "ORDER BY month")
    List<Object[]> getRevenueByTimeFrame(Date startDate, Date endDate);

    @Query("SELECT p.category.name as Name, SUM(od.unitPrice * od.product.quantity) as revenue " +
            "FROM OrderDetail od JOIN od.product p JOIN od.order o " +
            "WHERE o.deliveryStatus = 'COMPLETED' GROUP BY p.category.name")
    List<Map<String, Object>> getCategoryRevenue();

    @Query("SELECT p.name, SUM(od.product.quantity) FROM OrderDetail od " +
            "JOIN od.product p JOIN od.order o " +
            "WHERE o.deliveryStatus = 'COMPLETED' GROUP BY p.name")
    List<Object[]> getProductSalesCount();

    @Query("SELECT o.deliveryStatus, COUNT(o) FROM Order o GROUP BY o.deliveryStatus")
    List<Object[]> getOrderStatusCounts();

    @Query("SELECT FUNCTION('FORMAT', o.createDate, 'MM/yyyy') as month, " +
            "SUM(od.quantity * (od.unitPrice - od.unitCost)) as profit " +
            "FROM Order o JOIN o.orderDetails od " +
            "WHERE o.createDate BETWEEN :startDate AND :endDate " +
            "AND o.deliveryStatus = 'COMPLETED' " +
            "GROUP BY FUNCTION('FORMAT', o.createDate, 'MM/yyyy') " +
            "ORDER BY month")
    List<Object[]> getMonthlyProfitByTimeFrame(Date startDate, Date endDate);

    @Query("SELECT SUM(od.quantity * (od.unitPrice - od.unitCost)) FROM Order o JOIN o.orderDetails od WHERE o.deliveryStatus = 'COMPLETED'")
    Double getTotalCompletedProfit();

    @Query("SELECT SUM(od.quantity * (od.unitPrice - od.unitCost)) FROM Order o JOIN o.orderDetails od WHERE o.deliveryStatus = 'COMPLETED' AND YEAR(o.createDate) = :year")
    Double getTotalCompletedProfitForYear(int year);

    @Query("SELECT s.name, SUM(od.quantity* (od.unitPrice-od.unitCost)) as totalSales " +
            "FROM OrderDetail od JOIN od.product p JOIN p.supplier s JOIN od.order o " +
            "WHERE o.deliveryStatus = 'COMPLETED' " +
            "GROUP BY s.name ORDER BY totalSales DESC limit 15")
    List<Object[]> getSalesBySupplier();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.deliveryStatus = 'COMPLETED'")
    Double getTotalCompletedRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.deliveryStatus = 'COMPLETED' AND YEAR(o.createDate) = :year")
    Double getTotalCompletedRevenueForYear(int year);


    @Query("""
    SELECT DISTINCT o FROM Order o
    LEFT JOIN FETCH o.orderDetails od
    LEFT JOIN FETCH od.product p
    WHERE o.user.id = :userId
    """)
    List<Order> findByUserId(@Param("userId") Long userId);
}
