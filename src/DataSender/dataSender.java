package DataSender;

import Entity.*;
import Enums.*;
import static Enums.EmpType.Manager;
import javax.persistence.EntityManager;
import OrderManager.OrderManager;
import java.math.BigDecimal;
import java.util.*;

public class dataSender {

    public static void seedDatabase(EntityManager em) {
        try {
            em.getTransaction().begin();
            List<Employye> emps = em.createQuery("SELECT e FROM Employye e", Employye.class).getResultList();
            Employye activeEmp;
            if (emps.isEmpty()) {
                activeEmp = new Employye("EMP001", "Default Admin", "01000000000", "Main Branch", Manager);
                em.persist(activeEmp);
            } else {
                activeEmp = emps.get(0);
            }
            String[] catNames = {"Burgers", "Pizza", "Pasta", "Drinks"};
            Map<String, Categories> catMap = new HashMap<>();
            for (String name : catNames) {
                List<Categories> existing = em.createQuery("SELECT c FROM Categories c WHERE c.name = :n", Categories.class)
                                             .setParameter("n", name).getResultList();
                if (existing.isEmpty()) {
                    Categories c = new Categories();
                    c.setName(name);
                    em.persist(c);
                    catMap.put(name, c);
                } else {
                    catMap.put(name, existing.get(0));
                }
            }
            Object[][] mealData = {
                {"M1", "Big Burger", 150.0, "Burgers"},
                {"M2", "Margherita", 140.0, "Pizza"},
                {"M3", "Spaghetti", 100.0, "Pasta"},
                {"M4", "Coke", 30.0, "Drinks"}
            };
            for (Object[] m : mealData) {
                if (em.find(Meals.class, (String)m[0]) == null) {
                    Meals meal = new Meals((String)m[0], (String)m[1], new BigDecimal(m[2].toString()), catMap.get((String)m[3]));
                    em.persist(meal);
                }
            }
            String[][] customerData = {{"Omar", "01012345678", "Maadi"}, {"Shahd", "01122334455", "Nasr City"}};
            List<Customers> customersList = new ArrayList<>();
            for (String[] cData : customerData) {
                List<Customers> existing = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class)
                                             .setParameter("p", cData[1]).getResultList();
                if (existing.isEmpty()) {
                    Customers c = new Customers();
                    c.setName(cData[0]); c.setPhone(cData[1]); c.setAddress(cData[2]);
                    em.persist(c);
                    customersList.add(c);
                } else {
                    customersList.add(existing.get(0));
                }
            }

            em.getTransaction().commit();
            if (!customersList.isEmpty()) {

                List<String[]> order1Items = new ArrayList<>();
                order1Items.add(new String[]{"M1", "2"});
                OrderManager.processNewOrder(em, "ORD-101", customersList.get(0), activeEmp, order1Items);

                List<String[]> order2Items = new ArrayList<>();
                order2Items.add(new String[]{"M2", "1"});
                order2Items.add(new String[]{"M4", "2"});
                OrderManager.processNewOrder(em, "ORD-102", customersList.get(1), activeEmp, order2Items);
            }

            System.out.println("Database Seeded with PENDING orders successfully!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("Execution Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}