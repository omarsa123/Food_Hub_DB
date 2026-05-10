package OrderManager;

import Entity.*;
import Enums.OrderStatus;
import Enums.InvoiceStatus;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

public class OrderManager {
    public static void processNewOrder(EntityManager em, String orderId, Customers customer, Employye employee, List<String[]> itemsData) {
        
        if (em.find(Orders.class, orderId) != null) return;

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Orders order = new Orders(orderId);
            order.setCustomerId(customer);
            order.setEmployee(employee);
            order.setOrderItemsSet(new HashSet<>());

            BigDecimal subtotal = BigDecimal.ZERO;

            for (String[] row : itemsData) {
                Meals meal = em.find(Meals.class, row[0]); 
                int qty = Integer.parseInt(row[1]); 

                if (meal != null) {
                    OrderItems oi = new OrderItems(orderId, meal.getId());
                    oi.setQuantity(qty);
                    oi.setMeals(meal);
                    oi.setOrders(order);
                    order.getOrderItemsSet().add(oi);

                    subtotal = subtotal.add(meal.getPrice().multiply(BigDecimal.valueOf(qty)));
                }
            }
            order.setSubtotal(subtotal);
            em.persist(order);
            Invoice invoice = new Invoice("INV-" + orderId.replace("ORD-", ""));
            invoice.setOrderId(order);
            em.persist(invoice);

            tx.commit();
            System.out.println("Order Processed Successfully");

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e; 
        }
    }
}