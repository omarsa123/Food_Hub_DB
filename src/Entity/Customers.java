/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Dragon
 */
@Entity
@Table(name = "customers")
@NamedQueries({
    @NamedQuery(name = "Customers.findAll", query = "SELECT c FROM Customers c"),
    @NamedQuery(name = "Customers.findById", query = "SELECT c FROM Customers c WHERE c.id = :id"),
    @NamedQuery(name = "Customers.findByName", query = "SELECT c FROM Customers c WHERE c.name = :name"),
    @NamedQuery(name = "Customers.findByAddress", query = "SELECT c FROM Customers c WHERE c.address = :address"),
    @NamedQuery(name = "Customers.findByPhone", query = "SELECT c FROM Customers c WHERE c.phone = :phone")})
public class Customers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Address")
    private String address;
    @Basic(optional = false)
    @Column(name = "phone")
    private String phone;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerId")
    private Set<Orders> ordersSet;

    public Customers() {
    }

    public Customers(Integer id) {
        this.id = id;
    }

    public Customers(Integer id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    public void insert(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(this);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

public void update(EntityManager em, String newName, String newAddress, String newPhone) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Customers existingCustomer = em.find(Customers.class, this.id);
            
            if (existingCustomer != null) {
                existingCustomer.setName(newName);
                existingCustomer.setAddress(newAddress);
                existingCustomer.setPhone(newPhone);
            }
            
            tx.commit();
            this.name = newName;
            this.address = newAddress;
            this.phone = newPhone;
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }


    public static void deleteById(EntityManager em, int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Customers customer = em.find(Customers.class, id);
            if (customer != null) {
                em.remove(customer);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }


    public static Customers findById(EntityManager em, int id) {
        return em.find(Customers.class, id);
    }


    public static List<Customers> getAllCustomers(EntityManager em) {
        return em.createNamedQuery("Customers.findAll", Customers.class).getResultList();
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Orders> getOrdersSet() {
        return ordersSet;
    }

    public void setOrdersSet(Set<Orders> ordersSet) {
        this.ordersSet = ordersSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customers)) {
            return false;
        }
        Customers other = (Customers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "foodhub.Customers[ id=" + id + " ]";
    }
    
}
