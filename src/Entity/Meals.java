package Entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "meals")
@NamedQueries({
    @NamedQuery(name = "Meals.findAll", query = "SELECT m FROM Meals m"),
    @NamedQuery(name = "Meals.findById", query = "SELECT m FROM Meals m WHERE m.id = :id"),
    @NamedQuery(name = "Meals.findByName", query = "SELECT m FROM Meals m WHERE m.name = :name"),
    @NamedQuery(name = "Meals.findByPrice", query = "SELECT m FROM Meals m WHERE m.price = :price"),
    @NamedQuery(name = "Meals.findByDescription", query = "SELECT m FROM Meals m WHERE m.description = :description")})
public class Meals implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @Basic(optional = false)
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "description")
    private String description;
    
    @JoinColumn(name = "category_id", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Categories categoryId;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meals")
    private Set<OrderItems> orderItemsSet;

    public Meals() {
    }

    public Meals(String id) {
        this.id = id;
    }

    public Meals(String id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Meals(String id, String name, BigDecimal price, Categories category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = category;
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

    public void update(EntityManager em, String newName, BigDecimal newPrice, String newDesc, Categories newCategory) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Meals existingMeal = em.find(Meals.class, this.id);
            if (existingMeal != null) {
                existingMeal.setName(newName);
                existingMeal.setPrice(newPrice);
                existingMeal.setDescription(newDesc);
                existingMeal.setCategoryId(newCategory);
                
                this.name = newName;
                this.price = newPrice;
                this.description = newDesc;
                this.categoryId = newCategory;
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static void deleteById(EntityManager em, String id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Meals meal = em.find(Meals.class, id);
            if (meal != null) {
                em.remove(meal);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static List<Meals> getAllMeals(EntityManager em) {
        return em.createNamedQuery("Meals.findAll", Meals.class).getResultList();
    }

    public static Meals findById(EntityManager em, String id) {
        return em.find(Meals.class, id);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Categories getCategoryId() { return categoryId; }
    public void setCategoryId(Categories categoryId) { this.categoryId = categoryId; }
    public Set<OrderItems> getOrderItemsSet() { return orderItemsSet; }
    public void setOrderItemsSet(Set<OrderItems> orderItemsSet) { this.orderItemsSet = orderItemsSet; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Meals)) {
            return false;
        }
        Meals other = (Meals) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Meals[ id=" + id + ", name=" + name + " ]";
    }
}