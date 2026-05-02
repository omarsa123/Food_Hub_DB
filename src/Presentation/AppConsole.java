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
            System.out.println("     WELCOME TO FOODHUB SYSTEM      ");
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
            System.out.println("1. System Reports (Analytics)");
            System.out.println("2. Manage Customers (Add/Edit/Delete)");
            System.out.println("3. Manage Meals (Menu)");
            System.out.println("4. Manage Orders (Status/Delete)");
            System.out.println("5. Search Portal (Search by ID)");
            System.out.println("6. Back");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            if (choice.equals("6")) break;

            switch (choice) {
                case "1": reportsMenu(em); break;
                case "2": manageCustomersMenu(em); break;
                case "3": manageMealsMenu(em); break;
                case "4": manageOrdersMenu(em); break;
                case "5": searchPortal(em); break;
            }
        }
    }

    private static void reportsMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- REPORTS & ANALYTICS ---");
            System.out.println("1. KPIs Summary");
            System.out.println("2. Top Selling Meals");
            System.out.println("3. Monthly Completed Orders");
            System.out.println("4. Monthly Canceled Orders");
            System.out.println("5. Top Spending Customer");
            System.out.println("6. Orders Count Per Category");
            System.out.println("7. View Unpaid Invoices");
            System.out.println("8. List All Registered Customers");
            System.out.println("9. View All Delivered Orders");
            System.out.println("10. Back");
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
                case "8": em.createQuery("SELECT c FROM Customers c", Customers.class).getResultList().forEach(c -> System.out.println(c.getName() + " - " + c.getPhone())); break;
                case "9": em.createQuery("SELECT o FROM Orders o WHERE o.status = :s", Orders.class).setParameter("s", OrderStatus.delivered).getResultList().forEach(o -> System.out.println("Order: " + o.getId() + " | Sum: " + o.getSubtotal())); break;
            }
            System.out.println("--------------");
        }
    }

    private static void manageCustomersMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CUSTOMER MANAGEMENT ---");
            System.out.println("1. List Customers | 2. Add | 3. Update | 4. Delete | 5. Back");
            String op = scanner.nextLine();
            if (op.equals("5")) break;

            if (op.equals("1")) {
                em.createQuery("SELECT c FROM Customers c", Customers.class).getResultList()
                  .forEach(c -> System.out.println(c.getName() + " | " + c.getPhone() + " | " + c.getAddress()));
            } else if (op.equals("2")) {
                addNewCustomer(em);
            } else if (op.equals("3")) {
                System.out.print("Enter Phone: "); String p = scanner.nextLine();
                List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
                if (!res.isEmpty()) {
                    Customers c = res.get(0);
                    System.out.print("New Name (empty to skip): "); String n = scanner.nextLine();
                    if (!n.isEmpty()) c.setName(n);
                    System.out.print("New Address: "); String a = scanner.nextLine();
                    if (!a.isEmpty()) c.setAddress(a);
                    em.getTransaction().begin(); em.merge(c); em.getTransaction().commit();
                    System.out.println("Updated.");
                }
            } else if (op.equals("4")) {
                System.out.print("Enter Phone to Delete: "); String p = scanner.nextLine();
                List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
                if (!res.isEmpty()) {
                    try {
                        em.getTransaction().begin(); em.remove(res.get(0)); em.getTransaction().commit();
                        System.out.println("Deleted.");
                    } catch (Exception e) { System.out.println("Error: Linked orders exist."); em.getTransaction().rollback(); }
                }
            }
        }
    }
private static void searchPortal(EntityManager em) {
        System.out.println("\n1. Search Order ID | 2. Search Invoice ID");
        String choice = scanner.nextLine();
        
        if (choice.equals("1")) {
            System.out.print("Order ID: "); String id = scanner.nextLine();
            Orders o = em.find(Orders.class, id);
            if (o != null) {
                System.out.println("\n--- ORDER INFO ---");
                System.out.println("Customer: " + o.getCustomerId().getName() + " | Phone: " + o.getCustomerId().getPhone());
                System.out.println("Total: " + o.getSubtotal() + " | Status: " + o.getStatus());
                System.out.println("Items:");
                o.getOrderItemsSet().forEach(i -> System.out.println("- " + i.getMeals().getName() + " x" + i.getQuantity()));
            } else System.out.println("Not Found.");
            
        } else if (choice.equals("2")) {
            System.out.print("Invoice ID: "); String id = scanner.nextLine();
            Invoice inv = em.find(Invoice.class, id);
            
            if (inv != null) {
                System.out.println("\n--- INVOICE INFO ---");
                System.out.println("Invoice ID: " + inv.getId());
                System.out.println("Status: " + inv.getStatus());
                System.out.println("Total Price (with fees): " + inv.getTotalPrice() + " EGP");
                Orders relatedOrder = inv.getOrderId();
                if (relatedOrder != null) {
                    System.out.println("---------------------------");
                    System.out.println("Related Order ID: " + relatedOrder.getId());
                    System.out.println("Customer Name:    " + relatedOrder.getCustomerId().getName());
                    System.out.println("Customer Phone:   " + relatedOrder.getCustomerId().getPhone());
                    System.out.println("Order Subtotal:   " + relatedOrder.getSubtotal() + " EGP");
                    System.out.println("---------------------------");
                } else {
                    System.out.println("No related order found for this invoice.");
                }
            } else {
                System.out.println("Invoice Not Found.");
            }
        }
    }
    private static void customerMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CUSTOMER PORTAL ---");
            System.out.println("1. Menu | 2. Order (Login) | 3. Register | 4. Back");
            String choice = scanner.nextLine();
            if (choice.equals("4")) break;
            if (choice.equals("1")) showMenu(em);
            else if (choice.equals("2")) createNewOrder(em);
            else if (choice.equals("3")) addNewCustomer(em);
        }
    }

    private static void createNewOrder(EntityManager em) {
        System.out.print("Enter Phone: ");
        String p = scanner.nextLine();
        List<Customers> res = em.createQuery("SELECT c FROM Customers c WHERE c.phone = :p", Customers.class).setParameter("p", p).getResultList();
        if (res.isEmpty()) { System.out.println("Register first!"); return; }
        
        Orders order = new Orders(generateOrderId(), OrderStatus.pending);
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

    private static void addNewCustomer(EntityManager em) {
        System.out.print("Name: "); String n = scanner.nextLine();
        System.out.print("Address: "); String a = scanner.nextLine();
        System.out.print("Phone: "); String p = scanner.nextLine();
        Customers c = new Customers(); c.setName(n); c.setAddress(a); c.setPhone(p);
        c.insert(em);
    }

    private static void addNewMeal(EntityManager em) {
        System.out.print("Name: "); String n = scanner.nextLine();
        System.out.print("Price: "); BigDecimal pr = new BigDecimal(scanner.nextLine());
        Meals m = new Meals(generateMealId()); m.setName(n); m.setPrice(pr);
        em.getTransaction().begin(); em.persist(m); em.getTransaction().commit();
    }

    private static void showMenu(EntityManager em) {
        Meals.getAllMeals(em).forEach(m -> System.out.println("["+m.getId()+"] "+m.getName()+" - "+m.getPrice()+" EGP"));
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

    private static void manageMealsMenu(EntityManager em) {
        System.out.println("1. Show Menu | 2. Add New Meal");
        String op = scanner.nextLine();
        if (op.equals("1")) showMenu(em); else if (op.equals("2")) addNewMeal(em);
    }
}