package Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "customers")
@NamedQueries({
    @NamedQuery(name = "Customers.findAll", query = "SELECT c FROM Customers c"),
    @NamedQuery(name = "Customers.findById", query = "SELECT c FROM Customers c WHERE c.id = :id"),
    @NamedQuery(name = "Customers.findByName", query = "SELECT c FROM Customers c WHERE c.name = :name"),
    @NamedQuery(name = "Customers.findByEmail", query = "SELECT c FROM Customers c WHERE c.email = :email")
})
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

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    // Constructors
    public Customers() {
    }

    public Customers(Integer id) {
        this.id = id;
    }

    public Customers(Integer id, String name) {
        this.id = id;
        this.name = name;
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

public void update(EntityManager em, String newName, String newEmail, String newPhone) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Customers existingCustomer = em.find(Customers.class, this.id);
            
            if (existingCustomer != null) {
                existingCustomer.setName(newName);
                existingCustomer.setEmail(newEmail);
                existingCustomer.setPhone(newPhone);
            }
            
            tx.commit();
            this.name = newName;
            this.email = newEmail;
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


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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
        return "Customer[ id=" + id + ", name=" + name + " ]";
    }
}