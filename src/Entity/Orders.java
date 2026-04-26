/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import Enums.OrderStatus;
import Enums.Invoice;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    
    public void calculateSubtotal() {
    BigDecimal total = BigDecimal.ZERO;
    if (orderItemsSet != null) {
        for (OrderItems item : orderItemsSet) {
            item.fixPriceAtTime(); 
            if (item.getPriceAtTime() != null) {
                BigDecimal itemTotal = item.getPriceAtTime().multiply(new BigDecimal(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
    }
    this.subtotal = total;
}

    @Override
    public String toString() {
        return "foodhub.Orders[ id=" + id + " ]";
    }
    
}
