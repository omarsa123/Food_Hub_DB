package ReportsQueries;

import Entity.Invoice;
import Enums.InvoiceStatus;
import Enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;

public class ReportsQueries {

    public static BigDecimal getTotalRevenue(EntityManager em) {
    String jpql = "SELECT SUM(i.totalPrice) FROM Invoice i WHERE i.status = :status";
    
    BigDecimal result = em.createQuery(jpql, BigDecimal.class)
                         .setParameter("status", InvoiceStatus.paid) 
                         .getSingleResult();
    return result != null ? result : BigDecimal.ZERO;
    }
    public static Long getPaidInvoicesCount(EntityManager em) {
        String jpql = "SELECT COUNT(i) FROM Invoice i WHERE i.status = :status";

        Long count = em.createQuery(jpql, Long.class)
                       .setParameter("status", InvoiceStatus.paid)
                       .getSingleResult();

        return count != null ? count : 0L;
    }
public static List<Invoice> getUnpaidInvoicesByEmployee(EntityManager em, String employeeId) {
    String jpql = "SELECT i FROM Invoice i " +
                  "JOIN i.orderId o " +      
                  "JOIN o.employee e " +       
                  "WHERE i.status = :status " +
                  "AND e.ID = :empId";

    return em.createQuery(jpql, Invoice.class)
             .setParameter("status", InvoiceStatus.unpaid)
             .setParameter("empId", employeeId)
             .getResultList();
}
    public static Long getTotalOrdersCount(EntityManager em) {
        String jpql = "SELECT COUNT(o) FROM Orders o";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    public static Long getTotalMealsCount(EntityManager em) {
        String jpql = "SELECT COUNT(m) FROM Meals m";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    public static Long getTotalCustomersCount(EntityManager em) {
        String jpql = "SELECT COUNT(c) FROM Customers c";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }


    public static List<Object[]> getTopSellingMeals(EntityManager em) {
        String jpql = "SELECT oi.meals.name, SUM(oi.quantity) as totalSales " +
                     "FROM OrderItems oi " +
                     "GROUP BY oi.meals.name " +
                     "ORDER BY totalSales DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }

public static List<Object[]> getCompletedOrdersPerMonth(EntityManager em) {
    String jpql = "SELECT FUNCTION('YEAR', o.completedAt), " +
                  "       FUNCTION('MONTH', o.completedAt), " +
                  "       COUNT(o.id) " +
                  "FROM Orders o " +
                  "WHERE o.completedAt IS NOT NULL " +
                  "  AND o.status = :status " +
                  "GROUP BY FUNCTION('YEAR', o.completedAt), FUNCTION('MONTH', o.completedAt) " +
                  "ORDER BY FUNCTION('YEAR', o.completedAt) DESC, FUNCTION('MONTH', o.completedAt) DESC";

    return em.createQuery(jpql, Object[].class)
             .setParameter("status", OrderStatus.delivered)
             .getResultList();
}

// Cancelled Orders Per Month
public static List<Object[]> getCanceledOrdersPerMonth(EntityManager em) {
    String jpql = "SELECT FUNCTION('YEAR', o.completedAt), " +
                  "       FUNCTION('MONTH', o.completedAt), " +
                  "       COUNT(o.id) " +
                  "FROM Orders o " +
                  "WHERE o.status = :status " +
                  "GROUP BY FUNCTION('YEAR', o.completedAt), FUNCTION('MONTH', o.completedAt) " +
                  "ORDER BY FUNCTION('YEAR', o.completedAt) DESC, FUNCTION('MONTH', o.completedAt) DESC";

    return em.createQuery(jpql, Object[].class)
             .setParameter("status", OrderStatus.cancelled)
             .getResultList();
}

    public static Object[] getTopCustomer(EntityManager em) {
        String jpql = "SELECT o.customerId.name, SUM(o.subtotal) as totalSpending " +
                     "FROM Orders o " +
                     "WHERE o.status = :status " +
                     "GROUP BY o.customerId.name " + 
                     "ORDER BY totalSpending DESC";
                     
        List<Object[]> result = em.createQuery(jpql, Object[].class)
                 .setParameter("status", OrderStatus.delivered)
                 .setMaxResults(1) 
                 .getResultList();
                 
        return result.isEmpty() ? null : result.get(0);
    }

    public static List<Invoice> getUnpaidInvoices(EntityManager em) {
        return em.createQuery("SELECT i FROM Invoice i WHERE i.status = :status", Invoice.class)
                 .setParameter("status", InvoiceStatus.unpaid)
                 .getResultList();
    }

    public static List<Object[]> getOrdersPerCategory(EntityManager em) {
        String jpql = "SELECT m.categoryId.name, COUNT(DISTINCT oi.orders.id) as orderCount " +
                     "FROM OrderItems oi JOIN oi.meals m " +
                     "GROUP BY m.categoryId.name " + 
                     "ORDER BY orderCount DESC";
        return em.createQuery(jpql, Object[].class).getResultList();
    }
    public static List<Entity.Meals> getMealsByCategory(EntityManager em, int categoryId) {
        String jpql = "SELECT m FROM Meals m WHERE m.categoryId.id = :catId";
        return em.createQuery(jpql, Entity.Meals.class)
                 .setParameter("catId", categoryId)
                 .getResultList();
    }
}