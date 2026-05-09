package Presentation;

import Entity.*;
import Enums.*;
import ReportsQueries.ReportsQueries;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class AppConsole {
    private static final Scanner scanner = new Scanner(System.in);
    private static String generateOrderId() { return "ORD-" + System.currentTimeMillis(); }
    private static String generateMealId() { return "ML-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase(); }

    public static void startMenu(EntityManager em) {
        while (true) {
            System.out.println("\n====================================");
            System.out.println("      WELCOME TO FOODHUB SYSTEM      ");
            System.out.println("====================================");
            System.out.println("1. Staff Portal (Management)");
            System.out.println("2. Customer Portal (Ordering)");
            System.out.println("3. Exit");
            System.out.print("Select: ");

            String input = scanner.nextLine();
            if (input.equals("1")) employeeMenu(em);
            else if (input.equals("2")) customerMenu(em);
            else if (input.equals("3")) break;
        }
    }

    private static void employeeMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- STAFF DASHBOARD ---");
            System.out.println("1. System Reports | 2. Manage Customers | 3. Manage Categories");
            System.out.println("4. Manage Meals   | 5. Manage Orders    | 6. Search Portal");
            System.out.println("7. View All Records | 8. Back");
            System.out.print("Select: ");
            
            String choice = scanner.nextLine();
            if (choice.equals("8")) break;

            switch (choice) {
                case "1": reportsMenu(em); break;
                case "2": manageCustomersMenu(em); break;
                case "3": manageCategoriesMenu(em); break; 
                case "4": manageMealsMenu(em); break;      
                case "5": manageOrdersMenu(em); break;
                case "6": searchPortal(em); break;
                case "7": viewAllRecordsMenu(em); break;
            }
        }
    }

    private static void reportsMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- REPORTS & ANALYTICS ---");
            System.out.println("1. KPIs Summary | 2. Top Selling | 3. Monthly Success | 4. Monthly Canceled");
            System.out.println("5. Top Customer | 6. Per Category | 7. Unpaid Invoices ");
            System.out.println("8. All Delivered| 9. Back");
            String r = scanner.nextLine();
            if (r.equals("10")) break;

            System.out.println("\n--- RESULT ---");
            switch (r) {
                case "1":
                    System.out.println("Total Sales: " + ReportsQueries.getTotalRevenue(em) + " EGP");
                    System.out.println("Total Orders: " + ReportsQueries.getTotalOrdersCount(em));
                    System.out.println("Total Meals: " + ReportsQueries.getTotalMealsCount(em));
                    System.out.println("Total Customers: " + ReportsQueries.getTotalCustomersCount(em));
                    break;
                case "2": ReportsQueries.getTopSellingMeals(em).forEach(row -> System.out.println(row[0] + ": " + row[1] + " sold")); break;
                case "3": ReportsQueries.getCompletedOrdersPerMonth(em).forEach(row -> System.out.println("Month " + row[0] + ": " + row[1])); break;
                case "4": ReportsQueries.getCanceledOrdersPerMonth(em).forEach(row -> System.out.println("Month " + row[0] + ": " + row[1])); break;
                case "5":
                    Object[] top = ReportsQueries.getTopCustomer(em);
                    System.out.println(top != null ? "Customer: " + top[0] + " | Spent: " + top[1] : "No data");
                    break;
                case "6": ReportsQueries.getOrdersPerCategory(em).forEach(row -> System.out.println(row[0] + ": " + row[1] + " orders")); break;
                case "7": ReportsQueries.getUnpaidInvoices(em).forEach(inv -> System.out.println("Invoice: " + inv.getId())); break;
                case "8": em.createQuery("SELECT o FROM Orders o WHERE o.status = :s", Orders.class).setParameter("s", OrderStatus.delivered).getResultList().forEach(o -> System.out.println("Order: " + o.getId() + " | Sum: " + o.getSubtotal())); break;
            }
            System.out.println("--------------");
        }
    }

    private static void searchPortal(EntityManager em) {
        System.out.println("\n1. Search Order ID | 2. Search Invoice ID");
        String choice = scanner.nextLine();
        
        if (choice.equals("1")) {
            System.out.print("Order ID: "); String id = scanner.nextLine();
            Orders o = em.find(Orders.class, id);
            if (o != null) {
                em.refresh(o); 
                System.out.println("\n--- ORDER INFO ---");
                System.out.println("Customer: " + o.getCustomerId().getName());
                System.out.println("Status:   " + o.getStatus());
                System.out.println("Created:  " + o.getOrderPlacedAt());
                System.out.println("Completed At: " + (o.getCompletedAt() != null ? o.getCompletedAt() : "In Progress")); 
                System.out.println("Items:");
                o.getOrderItemsSet().forEach(i -> System.out.println("- " + i.getMeals().getName() + " x" + i.getQuantity()));
            } else System.out.println("Not Found.");
            
        } else if (choice.equals("2")) {
            System.out.print("Invoice ID: "); String id = scanner.nextLine();
            Invoice inv = em.find(Invoice.class, id);
            if (inv != null) {
                em.refresh(inv); 
                System.out.println("\n--- INVOICE INFO ---");
                System.out.println("Invoice ID: " + inv.getId());
                System.out.println("Status: " + inv.getStatus());
                System.out.println("Created At: " + inv.getCreatedAt());
                System.out.println("Payment Date: " + (inv.getPaymentDate() != null ? inv.getPaymentDate() : "Unpaid")); 
                System.out.println("Total (fees incl): " + inv.getTotalPrice() + " EGP");
                if (inv.getOrderId() != null) {
                    System.out.println("Related Order ID: " + inv.getOrderId().getId());
                }
            } else System.out.println("Invoice Not Found.");
        }
    }

    private static void viewAllRecordsMenu(EntityManager em) {
        System.out.println("\n--- SYSTEM EXPLORER ---");
        System.out.println("1. Customers | 2. Invoices | 3. Orders | 4. Categories | 5. Meals | 6. Back");
        String op = scanner.nextLine();
        switch (op) {
            case "1":
                Customers.getAllCustomers(em).forEach(c -> System.out.println(c.getName() + " | " + c.getPhone()));
                break;
            case "2":
                Invoice.getAllInvoices(em).forEach(i -> {
                    System.out.print("ID: " + i.getId() + " | Created: " + i.getCreatedAt());
                    if(i.getPaymentDate() != null) System.out.print(" | Paid: " + i.getPaymentDate());
                    System.out.println(" | Total: " + i.getTotalPrice());
                });
                break;
            case "3":
                Orders.getAllOrders(em).forEach(o -> {
                    System.out.print("ID: " + o.getId() + " | Date: " + o.getOrderPlacedAt());
                    if(o.getCompletedAt() != null) System.out.print(" | Completed: " + o.getCompletedAt());
                    System.out.println(" | Status: " + o.getStatus());
                });
                break;
            case "4":
                Categories.getAllCategories(em).forEach(c -> System.out.println("[" + c.getId() + "] " + c.getName()));
                break;
            case "5":
                showMenu(em);
                break;
        }
    }

    private static void manageCustomersMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CUSTOMER MANAGEMENT ---");
            System.out.println("1. List | 2. Add | 3. Update | 4. Delete | 5. Back");
            String op = scanner.nextLine();
            if (op.equals("5")) break;
            if (op.equals("1")) {
                em.createQuery("SELECT c FROM Customers c", Customers.class).getResultList().forEach(c -> System.out.println(c.getName() + " | " + c.getPhone()));
            } else if (op.equals("2")) {
                addNewCustomer(em);
            } else if (op.equals("3")) {
                System.out.print("Phone: "); String p = scanner.nextLine();
                List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
                if (!res.isEmpty()) {
                    Customers c = res.get(0);
                    System.out.print("New Name: "); String n = scanner.nextLine(); if(!n.isEmpty()) c.setName(n);
                    em.getTransaction().begin(); em.merge(c); em.getTransaction().commit();
                }
            } else if (op.equals("4")) {
                System.out.print("Phone: "); String p = scanner.nextLine();
                List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
                if (!res.isEmpty()) {
                    em.getTransaction().begin(); em.remove(res.get(0)); em.getTransaction().commit();
                }
            }
        }
    }

    private static void createNewOrder(EntityManager em) {
        System.out.print("Enter Phone: ");
        String p = scanner.nextLine();
        List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
        if (res.isEmpty()) { System.out.println("Register first!"); return; }
        
        Orders order = new Orders(generateOrderId());
        order.setCustomerId(res.get(0));
        order.setOrderItemsSet(new HashSet<>());
        
        while(true) {
            System.out.print("Meal ID (or 'n'): "); String mid = scanner.nextLine();
            if (mid.equals("n")) break;
            Meals m = em.find(Meals.class, mid);
            if (m != null) {
                System.out.print("Qty: "); int q = Integer.parseInt(scanner.nextLine());
                OrderItems item = new OrderItems(order.getId(), mid);
                item.setQuantity(q); item.setMeals(m); item.setOrders(order);
                order.getOrderItemsSet().add(item);
            }
        }
        order.updateForOrederItems(em);
        System.out.println("Success! Total: " + order.getSubtotal());
    }

    private static void manageMealsMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- MEAL MANAGEMENT ---");
            System.out.println("1. Add | 2. Edit | 3. Delete | 4. Back");
            String op = scanner.nextLine();
            if (op.equals("4")) break;
            switch (op) {
                case "1": addNewMeal(em); break;
                case "2": editMeal(em); break;
                case "3": System.out.print("ID: "); Meals.deleteById(em, scanner.nextLine()); break;
            }
        }
    }

    private static void manageCategoriesMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CATEGORY MANAGEMENT ---");
            System.out.println("1. Add | 2. Edit | 3. Delete | 4. Back");
            String op = scanner.nextLine();
            if (op.equals("4")) break;
            switch (op) {
                case "1": addCategory(em); break;
                case "2": editCategory(em); break;
                case "3": System.out.print("ID: "); Categories.deleteById(em, Integer.parseInt(scanner.nextLine())); break;
            }
        }
    }

    private static void manageOrdersMenu(EntityManager em) {
        System.out.print("Order ID: "); String oid = scanner.nextLine();
        Orders o = em.find(Orders.class, oid);
        if (o == null) return;
        System.out.println("1. Delivered | 2. Cancel | 3. Delete");
        String op = scanner.nextLine();
        if (op.equals("1")) o.updateOrderStatus(em, OrderStatus.delivered);
        else if (op.equals("2")) o.updateOrderStatus(em, OrderStatus.cancelled);
        else if (op.equals("3")) { em.getTransaction().begin(); em.remove(o); em.getTransaction().commit(); }
    }

    private static void addCategory(EntityManager em) {
        System.out.print("Category Name: "); String name = scanner.nextLine();
        Categories c = new Categories(); c.setName(name); c.insert(em);
    }

    private static void addNewMeal(EntityManager em) {
        System.out.print("Meal Name: "); String n = scanner.nextLine();
        System.out.print("Price: "); BigDecimal pr = new BigDecimal(scanner.nextLine());
        Categories.getAllCategories(em).forEach(cat -> System.out.println(cat.getId() + ". " + cat.getName()));
        Categories selectedCat = em.find(Categories.class, Integer.parseInt(scanner.nextLine()));
        if (selectedCat != null) {
            Meals m = new Meals(generateMealId());
            m.setName(n); m.setPrice(pr); m.setCategoryId(selectedCat); m.insert(em);
        }
    }

    private static void editMeal(EntityManager em) {
        System.out.print("Meal ID: "); Meals m = em.find(Meals.class, scanner.nextLine());
        if (m != null) {
            System.out.print("New Price: "); m.setPrice(new BigDecimal(scanner.nextLine()));
            em.getTransaction().begin(); em.merge(m); em.getTransaction().commit();
        }
    }

    private static void editCategory(EntityManager em) {
        System.out.print("Cat ID: "); Categories c = em.find(Categories.class, Integer.parseInt(scanner.nextLine()));
        if (c != null) {
            System.out.print("New Name: "); c.setName(scanner.nextLine());
            em.getTransaction().begin(); em.merge(c); em.getTransaction().commit();
        }
    }

    private static void showMenu(EntityManager em) {
        Categories.getAllCategories(em).forEach(cat -> {
            System.out.println("\n--- " + cat.getName() + " ---");
            ReportsQueries.getMealsByCategory(em, cat.getId()).forEach(m -> System.out.printf("[%s] %-15s %s EGP\n", m.getId(), m.getName(), m.getPrice()));
        });
    }

    private static void addNewCustomer(EntityManager em) {
        System.out.print("Name: "); String n = scanner.nextLine();
        System.out.print("Phone: "); String p = scanner.nextLine();
        Customers c = new Customers(); c.setName(n); c.setPhone(p); c.insert(em);
    }

    private static void customerMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CUSTOMER PORTAL ---");
            System.out.println("1. Menu | 2. Order | 3. Register | 4. Back");
            String choice = scanner.nextLine();
            if (choice.equals("4")) break;
            if (choice.equals("1")) showMenu(em);
            else if (choice.equals("2")) createNewOrder(em);
            else if (choice.equals("3")) addNewCustomer(em);
        }
    }
}