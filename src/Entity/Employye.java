package Entity;

import Enums.InvoiceStatus;
import Entity.Orders;
import Enums.EmpType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "Employye")
public class Employye {
    private static int counter = 1;
    @Id
    private String ID;
    @Enumerated(EnumType.STRING)
    @Column(name = "EmpType")
    private EmpType empType;
    private String name;
    private String phone;
    private String address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
    private Set<Orders> ordersSet = new HashSet<>();
    public Employye() {
    }

    public Set<Orders> getOrdersSet() {
        return ordersSet;
    }

    public EmpType getType() {
        return empType;
    }
    public void setType(EmpType type) {
        this.empType = type;
    }
    public Employye(String ID, String name, String phone, String address ) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
    public Employye(String ID, String name, String phone, String address , EmpType type) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.empType = type;
    }


public static String generateID() {
    return "EMP-" + System.currentTimeMillis() % 1000000; 
}
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void insert(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(this);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    public static List<Employye> getAllEmployees(EntityManager em) {
        return em.createQuery("SELECT e FROM Employye e", Employye.class).getResultList();
    }
    public static void updateEmployee(EntityManager em, String id, String newName, String newPhone) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employye e = em.find(Employye.class, id);
            if (e != null) {
                if (newName != null) e.setName(newName);
                if (newPhone != null) e.setPhone(newPhone);
                em.merge(e);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    public static void deleteById(EntityManager em, String id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Employye e = em.find(Employye.class, id);
            if (e != null) em.remove(e);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}