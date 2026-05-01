/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package foodhub;

import Entity.Categories;
import Entity.Customers;
import Entity.Invoice;
import Entity.Meals;
import Entity.OrderItems;
import Entity.Orders;
import Enums.InvoiceStatus;
import Enums.OrderStatus;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author Dragon
 */
public class FoodHub {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FoodHubPU");
        EntityManager em = emf.createEntityManager();

       try {
        // --- حالة الـ INSERT (أوردر جديد) ---[cite: 7]
        Orders newOrder = new Orders("ORD-99", OrderStatus.pending);
        newOrder.setCustomerId(em.find(Customers.class, 1)); //[cite: 10]
        

        Set<OrderItems> items = new HashSet<>();
        OrderItems item1 = new OrderItems(newOrder.getId(), "M-01");
        item1.setMeals(em.find(Meals.class, "M-01"));
        item1.setQuantity(2);
        item1.setPriceAtTime(item1.getMeals().getPrice()); //[cite: 5]
        items.add(item1);

        newOrder.setOrderItemsSet(items);
        newOrder.updateForOrederItems(em); // هتسيف الأوردر والآيتمز لأول مرة[cite: 7]

        // --- حالة الـ UPDATE (تعديل أوردر موجود) ---[cite: 7]
        Orders existingOrder = em.find(Orders.class, "ORD-99");
        if (existingOrder != null) {
            // إضافة صنف جديد للأوردر القديم
            OrderItems item2 = new OrderItems(existingOrder.getId(), "M-02");
            item2.setMeals(em.find(Meals.class, "M-02"));
            item2.setQuantity(1);
            item2.setPriceAtTime(item2.getMeals().getPrice());
            
            existingOrder.getOrderItemsSet().add(item2);
            existingOrder.updateForOrederItems(em); // هتحدث السعر وتسيف الصنف الجديد[cite: 7]
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        em.close();
        emf.close();
    }
    }
}
