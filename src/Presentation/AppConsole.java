package Presentation;

import Entity.*;
import Enums.*;
import OrderManager.OrderManager;
import ReportsQueries.ReportsQueries;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.*;

public class AppConsole {

    private static final Scanner scanner = new Scanner(System.in);
    private static Employye currentEmployee = null;

    private static String generateOrderId() {
        return "ORD-" + System.currentTimeMillis();
    }

    private static String generateMealId() {
        return "ML-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public static void startMenu(EntityManager em) {
        while (true) {
            System.out.println("\n====================================");
            System.out.println("      WELCOME TO FOODHUB SYSTEM      ");
            System.out.println("====================================");
            System.out.println("1. Staff Login (Management)");
            System.out.println("2. Customer Portal (Ordering)");
            System.out.println("3. Exit");
            System.out.print("Select: ");

            String input = scanner.nextLine();

            if (input.equals("1")) {
                if (employeeLogin(em)) employeeMenu(em);
            } else if (input.equals("2")) {
                customerMenu(em);
            } else if (input.equals("3")) {
                break;
            }
        }
    }

    private static boolean employeeLogin(EntityManager em) {
        System.out.print("Enter Employee ID: ");
        String id = scanner.nextLine();

        Employye emp = em.find(Employye.class, id);

        if (emp != null) {
            currentEmployee = emp;
            System.out.println("Welcome " + emp.getName() + " [" + emp.getType() + "]");
            return true;
        }

        System.out.println("Invalid Employee ID");
        return false;
    }

    private static void employeeMenu(EntityManager em) {

        while (true) {

            EmpType role = currentEmployee.getType();

            System.out.println("\n--- DASHBOARD (" + role + ") ---");
            System.out.println("1. Reports (Manager)");
            System.out.println("2. Customers");
            System.out.println("3. Categories");
            System.out.println("4. Meals");
            System.out.println("5. Orders");
            System.out.println("6. Unpaid Invoices");
            System.out.println("7. Employees (Manager)");
            System.out.println("8. Logout");

            String choice = scanner.nextLine();

            if (choice.equals("8")) {
                currentEmployee = null;
                break;
            }

            switch (choice) {

                case "1":
                    if (role == EmpType.Manager) reportsMenu(em);
                    else System.out.println("Access Denied");
                    break;

                case "2":
                    manageCustomersMenu(em);
                    break;

                case "3":
                    manageCategoriesMenu(em);
                    break;

                case "4":
                    manageMealsMenu(em);
                    break;

                case "5":
                    manageOrdersMenu(em);
                    break;
                case "6":
                    System.out.println("\n--- MY UNPAID INVOICES ---");
                    List<Invoice> unpaid = ReportsQueries.getUnpaidInvoicesByEmployee(em, currentEmployee.getID());
    
                    if (unpaid.isEmpty()) {
                        System.out.println("No unpaid invoices found for you.");
                    } else {
                        unpaid.forEach(inv -> {
                            System.out.println("Invoice ID : " + inv.getId());
                            System.out.println("Order ID   : " + inv.getOrderId().getId());
                            System.out.println("Customer   : " + inv.getOrderId().getCustomerId().getName());
                            System.out.println("Amount     : " + inv.getTotalPrice() + " EGP");
                            System.out.println("─".repeat(50));
                        });
                    }
                    break;
                case "7":
                    if (role == EmpType.Manager) manageEmployeesCRUD(em);
                    else System.out.println("Access Denied");
                    break;
            }
        }
    }

    private static void manageEmployeesCRUD(EntityManager em) {

        while (true) {
            System.out.println("\n--- EMPLOYEES ---");
            System.out.println("1. List | 2. Add | 3. Update | 4. Delete | 5. Back");

            String op = scanner.nextLine();
            if (op.equals("5")) break;

            switch (op) {

                case "1":
                    Employye.getAllEmployees(em)
                            .forEach(e -> System.out.println(e.getID() + " | " + e.getName() + " | " + e.getType()));
                    break;

                case "2":
                    System.out.print("Name: ");
                    String n = scanner.nextLine();

                    System.out.print("Phone: ");
                    String p = scanner.nextLine();

                    System.out.print("Address: ");
                    String a = scanner.nextLine();

                    System.out.print("Type (Manager/Employee): ");
                    String t = scanner.nextLine();

                    Employye emp = new Employye(Employye.generateID(), n, p, a);

                    if (t.equalsIgnoreCase("manager"))
                        emp.setType(EmpType.Manager);
                    else
                        emp.setType(EmpType.Employee);

                    emp.insert(em);
                    System.out.println("Employee Added");
                    break;

                case "3":
                    System.out.print("ID: ");
                    String uid = scanner.nextLine();

                    System.out.print("New Name: ");
                    String nn = scanner.nextLine();

                    System.out.print("New Phone: ");
                    String np = scanner.nextLine();

                    Employye.updateEmployee(em, uid, nn, np);
                    break;

                case "4":
                    System.out.print("ID: ");
                    Employye.deleteById(em, scanner.nextLine());
                    break;
            }
        }
    }

    private static void reportsMenu(EntityManager em) {
    while (true) {
        System.out.println("\n--- REPORTS MENU ---");
        System.out.println("1. KPI");
        System.out.println("2. Top Selling Meals");
        System.out.println("3. Monthly Orders Report");
        System.out.println("4. Top Customer");
        System.out.println("5. Unpaid Invoices");
        System.out.println("6. Orders Per Category");
        System.out.println("7. Back");
        System.out.print("Choose: ");
        
        String r = scanner.nextLine();
        if (r.equals("7")) break;

        switch (r) {
            case "1":
                System.out.println("\n--- KPI ---");
                System.out.println("Revenue: " + ReportsQueries.getTotalRevenue(em));
                System.out.println("Total Orders: " + ReportsQueries.getTotalOrdersCount(em));
                System.out.println("Paid Invoices:   " + ReportsQueries.getPaidInvoicesCount(em));
                System.out.println("Customers: " + ReportsQueries.getTotalCustomersCount(em));
                break;

            case "2":
                System.out.println("\n--- Top Selling Meals ---");
                ReportsQueries.getTopSellingMeals(em)
                        .forEach(x -> System.out.println(x[0] + " -> " + x[1] + " orders"));
                break;

            case "3":
                monthlyOrdersReport(em);
                break;

            case "4":   
                System.out.println("\n--- TOP CUSTOMER ---");
                Object[] topCust = ReportsQueries.getTopCustomer(em);
                if (topCust != null && topCust.length >= 2) {
                    System.out.println("Customer Name : " + topCust[0]);
                    System.out.println("Total Spending: " + topCust[1]);
                } else {
                    System.out.println("No completed orders yet.");
                }
                break;

            case "5":  
                System.out.println("\n--- UNPAID INVOICES ---");
                List<Invoice> unpaid = ReportsQueries.getUnpaidInvoices(em);
                if (unpaid.isEmpty()) {
                    System.out.println("No unpaid invoices found.");
                } else {
                    unpaid.forEach(inv -> {
                        System.out.println("Invoice ID : " + inv.getId());
                        System.out.println("Order ID   : " + inv.getOrderId().getId());
                        System.out.println("Customer   : " + inv.getOrderId().getCustomerId().getName());
                        String empName = (inv.getOrderId().getEmployee() != null) 
                             ? inv.getOrderId().getEmployee().getName() 
                             : "Not Assigned Yet";
                        System.out.println("Assigned To: " + empName);
                        System.out.println("Amount     : " + inv.getTotalPrice());
                        System.out.println("─".repeat(50));
                    });
                }
                break;

            case "6":   
                System.out.println("\n--- ORDERS PER CATEGORY ---");
                List<Object[]> categories = ReportsQueries.getOrdersPerCategory(em);
                if (categories.isEmpty()) {
                    System.out.println("No data found.");
                } else {
                    categories.forEach(cat -> {
                        System.out.println(cat[0] + " : " + cat[1] + " orders");
                    });
                }
                break;

            default:
                System.out.println("Invalid option!");
        }
    }
}
private static boolean hasPermissionOnOrder(Orders order) {
    if (order == null || currentEmployee == null) return false;

    boolean isManager = currentEmployee.getType() == EmpType.Manager;
    boolean isAssignedToMe = (order.getEmployee() != null) && 
                             order.getEmployee().getID().equals(currentEmployee.getID());

    return isManager || isAssignedToMe;
}
  private static void updateOrderStatusWithPermission(EntityManager em) 
 {
    System.out.print("Enter Order ID: ");
    String oid = scanner.nextLine();
    
    Orders order = em.find(Orders.class, oid);
    if (order == null) {
        System.out.println("Order not found!");
        return;
    }

    if (!hasPermissionOnOrder(order)) {
        System.out.println("Access Denied: This order is not assigned to you!");
        return;
    }

    System.out.println("Current Status: " + order.getStatus());
    System.out.println("1. Delivered");
    System.out.println("2. Cancelled");
    System.out.print("Choose: ");
    String choice = scanner.nextLine();

    OrderStatus newStatus = null;
    if (choice.equals("1")) newStatus = OrderStatus.delivered;
    else if (choice.equals("2")) newStatus = OrderStatus.cancelled;
    else {
        System.out.println("Invalid choice!");
        return;
    }

    try {
        order.updateOrderStatus(em, newStatus);
        if (newStatus == OrderStatus.delivered) {
            autoMarkInvoiceAsPaid(em, order);
        }
        
        System.out.println("Order status updated successfully");
    } catch (Exception e) {
        System.out.println("Error updating order status");
        e.printStackTrace();
    }
}
  private static void autoMarkInvoiceAsPaid(EntityManager em, Orders order) {
    try {
        Invoice invoice = order.getInvoice();
        if (invoice != null && invoice.getStatus() != InvoiceStatus.paid) {
            
            EntityTransaction tx = em.getTransaction();
            boolean isNewTx = !tx.isActive();
            if (isNewTx) tx.begin();
            
            invoice.setStatus(InvoiceStatus.paid);
            em.merge(invoice);
            
            if (isNewTx) tx.commit();
            
            System.out.println("Invoice automatically marked as PAID (Order Delivered)");
        }
    } catch (Exception e) {
        System.out.println("Order updated but failed to mark invoice as paid");
    }
}
    private static void deleteOrderWithPermission(EntityManager em) {
    System.out.print("Enter Order ID: ");
    String delId = scanner.nextLine();
    
    Orders order = em.find(Orders.class, delId);
    if (order == null) {
        System.out.println("Order not found!");
        return;
    }
    if (!hasPermissionOnOrder(order)) {
        System.out.println("Access Denied: This order is not assigned to you!");
        return;
    }
    if (order.getStatus() == OrderStatus.delivered) {
        System.out.println("Cannot delete a delivered order!");
        return;
    }

    try {
        Orders.deleteById(em, delId);   
        System.out.println("Order deleted successfully");
    } catch (Exception e) {
        System.out.println("Error deleting order");
        e.printStackTrace();
    }
}
    private static void monthlyOrdersReport(EntityManager em) {
    while (true) {
        System.out.println("\n--- Monthly Orders Report ---");
        System.out.println("1. Completed Orders Per Month");
        System.out.println("2. Cancelled Orders Per Month");
        System.out.println("3. Back");
        System.out.print("Choose: ");
        String choice = scanner.nextLine();

        if (choice.equals("3")) break;

        List<Object[]> result = null;
        String title = "";

        if (choice.equals("1")) {
            result = ReportsQueries.getCompletedOrdersPerMonth(em);
            title = "COMPLETED ORDERS PER MONTH";
        } else if (choice.equals("2")) {
            result = ReportsQueries.getCanceledOrdersPerMonth(em);  
            title = "CANCELLED ORDERS PER MONTH";
        } else {
            System.out.println("Invalid choice!");
            continue;
        }

        System.out.println("\n" + title);
        System.out.println("=".repeat(40));

        if (result.isEmpty()) {
            System.out.println("No data found.");
        } else {
            result.forEach(x -> {
                String monthYear = (x[0] != null) ? x[0].toString() : "Unknown";
                Long count = (x[1] != null) ? (Long) x[1] : 0L;
                System.out.println(monthYear + " : " + count + " orders");
            });
        }
        System.out.println("=".repeat(40));
    }
}
    private static void createNewOrder(EntityManager em) {

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        List<Customers> customer = em.createQuery(
                        "SELECT c FROM Customers c WHERE c.phone = :p", Customers.class)
                .setParameter("p", phone)
                .getResultList();

        if (customer.isEmpty()) {
            System.out.println("Customer not found");
            return;
        }
        Customers targetCustomer = customer.get(0);

        String orderId = generateOrderId();
    List<String[]> itemsList = new ArrayList<>();

    while (true) {
        System.out.print("Meal ID (n to stop): ");
        String mid = scanner.nextLine();
        if (mid.equalsIgnoreCase("n")) break;

        System.out.print("Quantity: ");
        String qty = scanner.nextLine();
        itemsList.add(new String[]{mid, qty});
    }

    if (itemsList.isEmpty()) return;
    try {
        OrderManager.processNewOrder(em, orderId, targetCustomer, currentEmployee, itemsList);
        System.out.println("Success!");
    } catch (Exception e) {
        System.out.println("System Error: Failed to create order.");
    }
    }

    private static void customerMenu(EntityManager em) {

        while (true) {
            System.out.println("\n1. Menu | 2. Order | 3. Register | 4. Back");

            String c = scanner.nextLine();

            if (c.equals("4")) break;
            if (c.equals("1")) showMenu(em);
            else if (c.equals("2")) createNewOrder(em);
            else if (c.equals("3")) addNewCustomer(em);
        }
    }

    private static void manageCustomersMenu(EntityManager em) {
    while (true) {
        System.out.println("\n--- MANAGE CUSTOMERS ---");
        System.out.println("1. List Customers");
        System.out.println("2. Add New Customer");
        System.out.println("3. Update Customer");
        System.out.println("4. Delete Customer");
        System.out.println("5. Back");
        System.out.print("Choose: ");
        
        String op = scanner.nextLine();
        if (op.equals("5")) break;

        switch (op) {
            case "1":
                System.out.println("\n--- All Customers ---");
                Customers.getAllCustomers(em)
                        .forEach(c -> System.out.println(c.getName() + " | " + c.getPhone()));
                break;

            case "2":
                addNewCustomer(em);
                break;

            case "3":
                updateCustomer(em);
                break;

            case "4":
                deleteCustomer(em);
                break;

            default:
                System.out.println("Invalid option!");
        }
    }
}
private static void updateCustomer(EntityManager em) {
    System.out.print("Enter Customer Phone: ");
    String phone = scanner.nextLine();

    List<Customers> list = em.createQuery(
            "SELECT c FROM Customers c WHERE c.phone = :p", Customers.class)
            .setParameter("p", phone)
            .getResultList();

    if (list.isEmpty()) {
        System.out.println("Customer not found!");
        return;
    }
    Customers customer = list.get(0);
    System.out.println("Current Data -> Name: " + customer.getName() + 
                       " | Address: " + customer.getAddress() + 
                       " | Phone: " + customer.getPhone());
    System.out.print("New Name (leave empty to keep): ");
    String newName = scanner.nextLine();
    System.out.print("New Address (leave empty to keep): ");
    String newAddress = scanner.nextLine();
    System.out.print("New Phone (leave empty to keep): ");
    String newPhone = scanner.nextLine();
    String finalName = newName.trim().isEmpty() ? customer.getName() : newName;
    String finalAddress = newAddress.trim().isEmpty() ? customer.getAddress() : newAddress;
    String finalPhone = newPhone.trim().isEmpty() ? customer.getPhone() : newPhone;

    try {
        customer.update(em, finalName, finalAddress, finalPhone);
        System.out.println("Customer updated successfully");
    } catch (Exception e) {
        System.out.println("Error updating customer");
        e.printStackTrace();
    }
}

// ====================== Delete Customer ======================
private static void deleteCustomer(EntityManager em) {
    System.out.print("Enter Customer Phone: ");
    String phone = scanner.nextLine();

    List<Customers> list = em.createQuery(
            "SELECT c FROM Customers c WHERE c.phone = :p", Customers.class)
            .setParameter("p", phone)
            .getResultList();

    if (list.isEmpty()) {
        System.out.println("Customer not found!");
        return;
    }

    Customers customer = list.get(0);
    
    System.out.println("Customer Found: " + customer.getName() + " | Phone: " + customer.getPhone());
    System.out.print("Are you sure you want to delete this customer? (y/n): ");
    String confirm = scanner.nextLine();

    if (!confirm.equalsIgnoreCase("y")) {
        System.out.println("Operation cancelled.");
        return;
    }
    try {
        Customers.deleteById(em, customer.getId());
        System.out.println("Customer deleted successfully");
    } catch (Exception e) {
        System.out.println("Cannot delete customer (probably has orders)");
        e.printStackTrace();
    }
}

    private static void manageMealsMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- MEALS ---");
            System.out.println("1. Add | 2. Edit | 3. Delete | 4. Back");

            String op = scanner.nextLine();
            if (op.equals("4")) break;

            switch (op) {
                case "1": addNewMeal(em); break;
                case "2": editMeal(em); break;
                case "3": Meals.deleteById(em, scanner.nextLine()); break;
            }
        }
    }

    private static void manageCategoriesMenu(EntityManager em) {
        while (true) {
            System.out.println("\n--- CATEGORIES ---");
            System.out.println("1. Add | 2. Edit | 3. Delete | 4. Back");

            String op = scanner.nextLine();
            if (op.equals("4")) break;

            switch (op) {
                case "1": addCategory(em); break;
                case "2": editCategory(em); break;
                case "3": Categories.deleteById(em, Integer.parseInt(scanner.nextLine())); break;
            }
        }
    }

private static void manageOrdersMenu(EntityManager em) {
    while (true) {
        System.out.println("\n--- MANAGE ORDERS ---");
        System.out.println("1. View All Orders");
        System.out.println("2. Assign Order to Employee (Manager)");
        System.out.println("3. Update Order Status (Delivered/Cancelled)");
        System.out.println("4. Delete Order");
        System.out.println("5. Back");
        System.out.print("Choose: ");
        String op = scanner.nextLine();

        if (op.equals("5")) break;

        switch (op) {
            case "1":
                viewAllOrders(em);
                break;

            case "2":   
                if (currentEmployee.getType() == EmpType.Manager) {
                    assignOrderToEmployee(em);
                } else {
                    System.out.println("Access Denied: Only Manager can assign orders");
                }
                break;

           case "3":
                updateOrderStatusWithPermission(em);
                break;

            case "4":
                deleteOrderWithPermission(em);
                break;
            default:
                System.out.println("Invalid option!");
        }
    }
}
private static void assignOrderToEmployee(EntityManager em) {
    System.out.print("Enter Order ID: ");
    String orderId = scanner.nextLine();

    Orders order = em.find(Orders.class, orderId);
    if (order == null) {
        System.out.println("Order not found!");
        return;
    }

    System.out.println("Current Employee: " + 
        (order.getEmployee() != null ? order.getEmployee().getName() : "Not Assigned"));
    System.out.println("\nAvailable Employees:");
    List<Employye> employees = Employye.getAllEmployees(em);
    for (Employye emp : employees) {
        System.out.println(emp.getID() + " | " + emp.getName() + " [" + emp.getType() + "]");
    }

    System.out.print("\nEnter Employee ID to assign: ");
    String empId = scanner.nextLine();

    Employye newEmployee = em.find(Employye.class, empId);
    if (newEmployee == null) {
        System.out.println("Employee not found!");
        return;
    }

    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        order.setEmployee(newEmployee);
        em.merge(order);         
        tx.commit();

        System.out.println("Order successfully assigned to: " + newEmployee.getName());
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
        System.out.println("Error while assigning order");
        e.printStackTrace();
    }
}

    private static void addNewCustomer(EntityManager em) {
        System.out.print("Name: ");
        String n = scanner.nextLine();

        System.out.print("Phone: ");
        String p = scanner.nextLine();

        Customers c = new Customers();
        c.setName(n);
        c.setPhone(p);
        c.insert(em);
    }
    private static void addNewMeal(EntityManager em) {

    System.out.print("Meal Name: ");
    String n = scanner.nextLine();

    System.out.print("Price: ");
    BigDecimal pr = new BigDecimal(scanner.nextLine());

    Categories.getAllCategories(em)
            .forEach(cat -> System.out.println(cat.getId() + " - " + cat.getName()));

    System.out.print("Select Category ID: ");
    int cid = Integer.parseInt(scanner.nextLine());

    Categories selectedCat = em.find(Categories.class, cid);

    if (selectedCat != null) {
        Meals m = new Meals(generateMealId());
        m.setName(n);
        m.setPrice(pr);
        m.setCategoryId(selectedCat);
        m.insert(em);
    }
}
    private static void showMenu(EntityManager em) {
        Categories.getAllCategories(em).forEach(cat -> {
            System.out.println("\n--- " + cat.getName() + " ---");
            ReportsQueries.getMealsByCategory(em, cat.getId())
                    .forEach(m -> System.out.println(m.getId() + " " + m.getName() + " " + m.getPrice()));
        });
    }

    private static void addCategory(EntityManager em) {
        System.out.print("Name: ");
        Categories c = new Categories();
        c.setName(scanner.nextLine());
        c.insert(em);
    }
    private static void editMeal(EntityManager em) {
        System.out.print("ID: ");
        Meals m = em.find(Meals.class, scanner.nextLine());

        if (m != null) {
            System.out.print("New Price: ");
            m.setPrice(new BigDecimal(scanner.nextLine()));

            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.merge(m);
            tx.commit();
        }
    }
    private static void editCategory(EntityManager em) {
        System.out.print("ID: ");
        Categories c = em.find(Categories.class, Integer.parseInt(scanner.nextLine()));

        if (c != null) {
            System.out.print("New Name: ");
            c.setName(scanner.nextLine());

            EntityTransaction tx = em.getTransaction();
            tx.begin();
            em.merge(c);
            tx.commit();
        }
    }
    private static void viewAllOrders(EntityManager em) {
    System.out.println("\n====================================");
    System.out.println("              ALL ORDERS");
    System.out.println("====================================");

    List<Orders> orders;

    if (currentEmployee.getType() == EmpType.Manager) {
        String jpql = "SELECT o FROM Orders o ORDER BY o.orderPlacedAt DESC";
        orders = em.createQuery(jpql, Orders.class).getResultList();
        System.out.println("Showing All Orders (Manager View)\n");
    } else {
        String jpql = "SELECT o FROM Orders o WHERE o.employee.ID = :empId ORDER BY o.orderPlacedAt DESC";
        
        orders = em.createQuery(jpql, Orders.class)
                   .setParameter("empId", currentEmployee.getID())
                   .getResultList();
        
        System.out.println("Showing Your Assigned Orders Only\n");
    }

    if (orders.isEmpty()) {
        System.out.println("No orders found.");
        return;
    }

    for (Orders o : orders) {
        String empName = "Not Assigned";
        
        if (o.getEmployee() != null) {
            empName = o.getEmployee().getName() + " (" + o.getEmployee().getID() + ")";
        }

        System.out.println("────────────────────────────────────");
        System.out.println("Order ID       : " + o.getId());
        System.out.println("Placed At      : " + o.getOrderPlacedAt());
        System.out.println("Status         : " + o.getStatus());
        System.out.println("Subtotal       : " + o.getSubtotal());
        System.out.println("Customer       : " + o.getCustomerId().getName() + 
                          " (" + o.getCustomerId().getPhone() + ")");
        System.out.println("Assigned To    : " + empName);
        System.out.println("Completed At   : " + 
            (o.getCompletedAt() != null ? o.getCompletedAt() : "Still Open"));
    }
    
    System.out.println("====================================");
    System.out.println("Total Orders Shown: " + orders.size());
}
}