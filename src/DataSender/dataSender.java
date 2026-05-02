package DataSender;

import Entity.*;
import Enums.*;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

public class dataSender {

    public static void seedDatabase(EntityManager em) {
        Long count = em.createQuery("SELECT COUNT(c) FROM Categories c", Long.class).getSingleResult();
        if (count > 0) return;

        try {
            em.getTransaction().begin();
            String[] catNames = {"Burgers", "Pizza", "Pasta", "Drinks"};
            List<Categories> catList = new ArrayList<>();
            for (String name : catNames) {
                Categories c = new Categories();
                c.setName(name);
                em.persist(c);
                catList.add(c);
            }
            String[][] mealData = {
                {"M1", "Big Burger", "150", "0"}, {"M2", "Margherita", "140", "1"},
                {"M3", "Spaghetti", "100", "2"}, {"M4", "Coke", "30", "3"}
            };
            for (String[] m : mealData) {
                Meals meal = new Meals(m[0], m[1], new BigDecimal(m[2]), catList.get(Integer.parseInt(m[3])));
                em.persist(meal);
            }
        String[] names = {"Omar", "Shahd", "Ziad", "Laila", "Felo"};
        String[] addresses = {"Maadi, Cairo", "Nasr City, Cairo", "Smouha, Alexandria", "Dokki, Giza", "Zamalek, Cairo"};
        String[] phones = {"01012345678", "01122334455", "01233445566", "01555667788", "01099887766"};

        List<Customers> customersList = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            Customers c = new Customers();
            c.setName(names[i]);
            c.setAddress(addresses[i]);
            c.setPhone(phones[i]);    

            em.persist(c);
            customersList.add(c);
        }
            em.getTransaction().commit();
            createOrderScenario(em, "ORD-101", customersList.get(0), new String[][]{{"M1", "2"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-102", customersList.get(0), new String[][]{{"M4", "5"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-103", customersList.get(1), new String[][]{{"M2", "1"}, {"M3", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-104", customersList.get(2), new String[][]{{"M1", "1"}}, OrderStatus.cancelled, InvoiceStatus.unpaid);
            createOrderScenario(em, "ORD-105", customersList.get(3), new String[][]{{"M3", "2"}}, OrderStatus.delivered, InvoiceStatus.unpaid);
            System.out.println(">>> Database Seeded with 15 Diverse Scenarios!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void createOrderScenario(EntityManager em, String id, Customers c, String[][] items, OrderStatus os, InvoiceStatus is) {
        Orders order = new Orders(id, os);
        order.setCustomerId(c);
        order.saveOrder(em);

        Set<OrderItems> set = new HashSet<>();
        for (String[] item : items) {
            OrderItems oi = new OrderItems(id, item[0]);
            oi.setQuantity(Integer.parseInt(item[1]));
            oi.setOrders(order);
            oi.setMeals(em.find(Meals.class, item[0]));
            oi.fixPriceAtTime();
            oi.saveItem(em);
            set.add(oi);
        }
        order.setOrderItemsSet(set);
        order.updateForOrederItems(em);

        Invoice inv = new Invoice("INV-" + id.split("-")[1]);
        inv.setOrderId(order);
        inv.setStatus(is);
        inv.insert(em, 14.0, new BigDecimal("10.0"));
    }
}