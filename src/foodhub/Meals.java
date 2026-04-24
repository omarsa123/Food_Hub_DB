/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package foodhub;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Dragon
 */
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
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    public Set<OrderItems> getOrderItemsSet() {
        return orderItemsSet;
    }

    public void setOrderItemsSet(Set<OrderItems> orderItemsSet) {
        this.orderItemsSet = orderItemsSet;
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
        return "foodhub.Meals[ id=" + id + " ]";
    }
    
}
