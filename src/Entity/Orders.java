/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import Enums.OrderStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Dragon
 */
@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findById", query = "SELECT o FROM Orders o WHERE o.id = :id"),
    @NamedQuery(name = "Orders.findByOrderPlacedAt", query = "SELECT o FROM Orders o WHERE o.orderPlacedAt = :orderPlacedAt"),
    @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status"),
    @NamedQuery(name = "Orders.findByCompletedAt", query = "SELECT o FROM Orders o WHERE o.completedAt = :completedAt"),
    @NamedQuery(name = "Orders.findBySubtotal", query = "SELECT o FROM Orders o WHERE o.subtotal = :subtotal")})
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Column(name = "order_placed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderPlacedAt;
    @Basic(optional = false)
    @Column(name = "status")
    @Enumerated(EnumType.STRING) 
    private OrderStatus status;
    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedAt;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @JoinColumn(name = "customer_id", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Customers customerId;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Invoice invoice;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orders")
    private Set<OrderItems> orderItemsSet;

    public Orders() {
    }

    public Orders(String id) {
        this.id = id;
    }

    public Orders(String id, OrderStatus status) {
        this.id = id;
        this.status = status;
    }

   @PrePersist
    protected void onCreate() {
        this.orderPlacedAt = new java.util.Date();
    }
        // الbigDecimal مبعرفش اعمله += او الباقي يعني
  public void calculateSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (orderItemsSet != null) {
            for (OrderItems item : orderItemsSet) {
                total = total.add(item.calculateItemTotal());
            }
        }
        this.subtotal = total;
    }
      public void saveOrder(EntityManager em) {
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
  public void updateOrderStatus(EntityManager em, Enums.OrderStatus newStatus) {
    EntityTransaction tx = em.getTransaction(); 
    try {
        tx.begin();
        this.status = newStatus;
        tx.commit();
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
            throw e;
    }
}
  public void updateItemQuantity(EntityManager em, String mealId, int newQuantity) {
    
    if (this.orderItemsSet == null) {
        return; 
    }

    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        
        for (OrderItems item : this.orderItemsSet) {
            if (item.getOrderItemsPK().getMealId().equals(mealId)) {
                //معملتش ميثود ابديت هناك عشان مش مفيدة
                item.setQuantity(newQuantity);
                this.calculateSubtotal(); 
                break; 
            }
        }
        tx.commit();
    } catch (Exception e) {
        if (tx != null && tx.isActive()) tx.rollback();
        throw e;
    }
}
  //لازم نسيف الاوردر حتى لو مفيش ايتمز وعملنا الميثود دي عشان الفورين كي
  public void updateForOrederItems(EntityManager em) {
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        //عشان الاوردر يكون في الوقت اللي اتحطت فيه الايتمز
        if (this.orderPlacedAt == null) {
            this.orderPlacedAt = new Date(); 
        }
        this.calculateSubtotal();
        if (this.orderItemsSet != null) {
            for (OrderItems item : this.orderItemsSet) {
                item.setOrders(this); 
                if (item.getOrderItemsPK() != null) {
                    item.getOrderItemsPK().setOrderId(this.id); 
                }
            }
        }
        em.merge(this);
        tx.commit();
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
        throw e;
    }
}
   public static void deleteById(EntityManager em, int id) {
    Orders order = em.find(Orders.class, id);
    if (order == null) {
        System.out.println("Order with ID " + id + " not found.");
        return; 
    }
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        em.remove(order);
        tx.commit();
    } catch (Exception e) {
        if (tx.isActive())tx.rollback();
        throw e;
    }
}

   public static List<Orders> getAllOrders(EntityManager em) {
        return em.createNamedQuery("Orders.findAll", Orders.class).getResultList();
    }
  
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getOrderPlacedAt() {
        return orderPlacedAt;
    }

    public void setOrderPlacedAt(Date orderPlacedAt) {
        this.orderPlacedAt = orderPlacedAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Customers getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Customers customerId) {
        this.customerId = customerId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice= invoice;
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
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    

    @Override
    public String toString() {
        return "foodhub.Orders[ id=" + id + " ]";
    }
    
}
