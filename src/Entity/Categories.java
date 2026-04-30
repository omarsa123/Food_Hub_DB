package Entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author Dragon
 */
@Entity
@Table(name = "categories")
@NamedQueries({
    @NamedQuery(name = "Categories.findAll", query = "SELECT c FROM Categories c"),
    @NamedQuery(name = "Categories.findById", query = "SELECT c FROM Categories c WHERE c.id = :id"),
    @NamedQuery(name = "Categories.findByName", query = "SELECT c FROM Categories c WHERE c.name = :name")})
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryId")
    private Set<Meals> mealsSet;

    public Categories() {
    }

    public Categories(Integer id) {
        this.id = id;
    }

    public Categories(Integer id, String name) {
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

    public void update(EntityManager em, String newName) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categories existingCategory = em.find(Categories.class, this.id);
            
            if (existingCategory != null) {
                existingCategory.setName(newName);
            }
            
            tx.commit();
            this.name = newName; 
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static void deleteById(EntityManager em, int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categories category = em.find(Categories.class, id);
            if (category != null) {
                em.remove(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static List<Categories> getAllCategories(EntityManager em) {
        return em.createNamedQuery("Categories.findAll", Categories.class).getResultList();
    }
    public static List<Categories> findByName(EntityManager em, String name) {
        return em.createNamedQuery("Categories.findByName", Categories.class)
                 .setParameter("name", name)
                 .getResultList();
    }

    public static Categories findById(EntityManager em, int id) {
        return em.find(Categories.class, id);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Meals> getMealsSet() { return mealsSet; }
    public void setMealsSet(Set<Meals> mealsSet) { this.mealsSet = mealsSet; }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Categories)) {
            return false;
        }
        Categories other = (Categories) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Category[ id=" + id + ", name=" + name + " ]";
    }
}