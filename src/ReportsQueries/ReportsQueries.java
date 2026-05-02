/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ReportsQueries;

import Entity.Invoice;
import Enums.InvoiceStatus;
import Enums.OrderStatus;
import java.util.List;
import javax.persistence.EntityManager;

public class ReportsQueries {

    
    public static List<Object[]> getTopSellingMeals(EntityManager em) {
        String jpql = "SELECT oi.meals.name as mealName, SUM(oi.quantity) as totalSales " +
                     "FROM OrderItems oi " +
                     "GROUP BY mealName " +
                     "ORDER BY totalSales DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

   
    public static List<Object[]> getCompletedOrdersPerMonth(EntityManager em) {
        String jpql = "SELECT FUNCTION('MONTH', o.orderPlacedAt), COUNT(o) as CompletedOrders" +
                     "FROM Orders o WHERE o.status = :status " +
                     "GROUP BY FUNCTION('MONTH', o.orderPlacedAt)";
        return em.createQuery(jpql, Object[].class)
                 .setParameter("status", OrderStatus.delivered)
                 .getResultList();
    }

   
    public static List<Object[]> getCanceledOrdersPerMonth(EntityManager em) {
        String jpql = "SELECT FUNCTION('MONTH', o.orderPlacedAt), COUNT(o) as CanceledOrders " +
                     "FROM Orders o WHERE o.status = :status " +
                     "GROUP BY FUNCTION('MONTH', o.orderPlacedAt)";
        return em.createQuery(jpql, Object[].class)
                 .setParameter("status", OrderStatus.cancelled)
                 .getResultList();
    }

  
    public static Object[] getTopCustomer(EntityManager em) {
        String jpql = "SELECT i.orderId.customerId.name as customerName, SUM(i.totalPrice) as totalSpending " +
                     "FROM Invoice i " +
                     "WHERE i.status = :status " +
                     "GROUP BY customerName " + 
                     "ORDER BY totalSpending DESC";
                     
        List<Object[]> result = em.createQuery(jpql, Object[].class)
                 .setParameter("status", InvoiceStatus.paid)
                 .setMaxResults(1) 
                 .getResultList();
                 
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }


    public static List<Invoice> getUnpaidInvoices(EntityManager em) {
        return em.createNamedQuery("Invoice.findByStatus", Invoice.class)
                 .setParameter("status", InvoiceStatus.unpaid)
                 .getResultList();
    }

   
    public static List<Object[]> getOrdersPerCategory(EntityManager em) {
        String jpql = "SELECT oi.meals.categoryId.name as categoryName, COUNT(DISTINCT oi.orders.id) as orderCount " +
                     "FROM OrderItems oi " +
                     "GROUP BY categoryName" + 
                     "ORDER BY orderCount DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }
}