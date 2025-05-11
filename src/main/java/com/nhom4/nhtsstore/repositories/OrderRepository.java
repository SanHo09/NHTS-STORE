package com.nhom4.nhtsstore.repositories;

import com.nhom4.nhtsstore.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByCreateDateBetween(Date startDate, Date endDate);
    @Query("SELECT FUNCTION('FORMAT', o.createDate, 'MM/yyyy') as month, SUM(o.totalAmount) as revenue " +
            "FROM Order o WHERE o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('FORMAT', o.createDate, 'MM/yyyy') " +
            "ORDER BY month")
    List<Object[]> getRevenueByTimeFrame(Date startDate, Date endDate);
    @Query("SELECT p.category.name as Name, SUM(od.product.salePrice * od.product.quantity) as revenue " +
            "FROM OrderDetail od JOIN od.product p JOIN od.order o " +
            "WHERE o.status = 'COMPLETED' GROUP BY p.category.name")
    List<Map<String, Object>> getCategoryRevenue();

    @Query("SELECT p.name, SUM(od.product.quantity) FROM OrderDetail od " +
            "JOIN od.product p JOIN od.order o " +
            "WHERE o.status = 'COMPLETED' GROUP BY p.name")
    List<Object[]> getProductSalesCount();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusCounts();
    @Query("SELECT FUNCTION('FORMAT', o.createDate, 'MM/yyyy') as month, AVG(o.totalAmount) as average " +
            "FROM Order o WHERE o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('FORMAT', o.createDate, 'MM/yyyy') " +
            "ORDER BY month")
    List<Object[]> getAverageOrderValueByMonth(Date startDate, Date endDate);


    @Query("SELECT s.name, SUM(p.salePrice * p.quantity) as totalSales " +
            "FROM OrderDetail od JOIN od.product p JOIN p.supplier s JOIN od.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY s.name ORDER BY totalSales DESC")
    List<Object[]> getSalesBySupplier();

    @Query("""
    SELECT DISTINCT o FROM Order o
    LEFT JOIN FETCH o.orderDetails od
    LEFT JOIN FETCH od.product p
    WHERE o.user.id = :userId
    """)
    List<Order> findByUserId(@Param("userId") Long userId);
}
