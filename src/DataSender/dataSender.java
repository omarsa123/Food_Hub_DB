package DataSender;

import Entity.*;
import Enums.*;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

public class dataSender {

    public static void populateAll(EntityManager em) {
        try {
            String[] catNames = {"Burgers", "Pizza", "Pasta", "Drinks", "Desserts"};
            List<Categories> catList = new ArrayList<>();
            for (String name : catNames) {
                Categories c = new Categories();
                c.setName(name);
                c.insert(em);
                catList.add(c);
            }

            String[][] mealData = {
                {"M1", "Big Burger", "150", "0"}, {"M2", "Cheese Burger", "120", "0"}, {"M3", "Chicken Burger", "110", "0"},
                {"M4", "Margherita Pizza", "140", "1"}, {"M5", "Pepperoni", "180", "1"}, {"M6", "Veggie Pizza", "130", "1"},
                {"M7", "Spaghetti", "90", "2"}, {"M8", "Fettuccine", "140", "2"}, {"M9", "Lasagna", "160", "2"},
                {"M10", "Coke", "30", "3"}, {"M11", "Pepsi", "30", "3"}, {"M12", "Water", "15", "3"},
                {"M13", "Molten Cake", "90", "4"}, {"M14", "Fruit Salad", "60", "4"}, {"M15", "Ice Cream", "40", "4"}
            };
            for (String[] m : mealData) {
                Meals meal = new Meals(m[0], m[1], new BigDecimal(m[2]), catList.get(Integer.parseInt(m[3])));
                meal.insert(em);
            }

            String[] names = {"Omar", "Shahd", "Ziad", "Laila", "Ahmed", "Mona", "Hassan", "Nour"};
            List<Customers> customersList = new ArrayList<>();
            for (String n : names) {
                Customers c = new Customers();
                c.setName(n);
                c.insert(em);
                customersList.add(c);
            }

            createOrderScenario(em, "ORD-001", customersList.get(0), new String[][]{{"M1", "2"}, {"M10", "2"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-002", customersList.get(0), new String[][]{{"M5", "1"}, {"M13", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-003", customersList.get(0), new String[][]{{"M1", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);

            createOrderScenario(em, "ORD-004", customersList.get(1), new String[][]{{"M4", "2"}, {"M11", "2"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-005", customersList.get(1), new String[][]{{"M1", "3"}}, OrderStatus.delivered, InvoiceStatus.paid);

            createOrderScenario(em, "ORD-006", customersList.get(2), new String[][]{{"M2", "1"}}, OrderStatus.cancelled, InvoiceStatus.unpaid);
            createOrderScenario(em, "ORD-007", customersList.get(2), new String[][]{{"M12", "5"}}, OrderStatus.cancelled, InvoiceStatus.unpaid);

            createOrderScenario(em, "ORD-008", customersList.get(3), new String[][]{{"M9", "2"}}, OrderStatus.delivered, InvoiceStatus.unpaid);
            createOrderScenario(em, "ORD-009", customersList.get(3), new String[][]{{"M15", "3"}}, OrderStatus.delivered, InvoiceStatus.unpaid);

            createOrderScenario(em, "ORD-010", customersList.get(4), new String[][]{{"M1", "1"}, {"M7", "1"}, {"M13", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-011", customersList.get(5), new String[][]{{"M5", "1"}, {"M10", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-012", customersList.get(6), new String[][]{{"M2", "2"}, {"M11", "2"}}, OrderStatus.preparing, InvoiceStatus.unpaid);
            createOrderScenario(em, "ORD-013", customersList.get(7), new String[][]{{"M3", "1"}, {"M12", "1"}}, OrderStatus.pending, InvoiceStatus.unpaid);

            createOrderScenario(em, "ORD-014", customersList.get(1), new String[][]{{"M1", "1"}, {"M4", "1"}}, OrderStatus.delivered, InvoiceStatus.paid);
            createOrderScenario(em, "ORD-015", customersList.get(4), new String[][]{{"M8", "2"}}, OrderStatus.delivered, InvoiceStatus.paid);

            System.out.println("All Diverse Data Injected Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createOrderScenario(EntityManager em, String orderId, Customers customer, 
                                          String[][] items, OrderStatus oStatus, InvoiceStatus iStatus) {
        
        Orders order = new Orders(orderId, oStatus);
        order.setCustomerId(customer);
        order.saveOrder(em);

        Set<OrderItems> orderItemsSet = new HashSet<>();
        for (String[] item : items) {
            OrderItems oi = new OrderItems(orderId, item[0]);
            oi.setQuantity(Integer.parseInt(item[1]));
            oi.setOrders(order);
            Meals m = em.find(Meals.class, item[0]);
            oi.setMeals(m);
            oi.fixPriceAtTime();
            oi.saveItem(em);
            orderItemsSet.add(oi);
        }

        order.setOrderItemsSet(orderItemsSet);
        order.updateForOrederItems(em);

        Invoice inv = new Invoice("INV-" + orderId.substring(4));
        inv.setOrderId(order);
        inv.setStatus(iStatus);
        inv.insert(em, 14.0, new BigDecimal("20.00"));
    }
}