/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Dragon
 */
@Entity
@Table(name = "order_items")
@NamedQueries({
    @NamedQuery(name = "OrderItems.findAll", query = "SELECT o FROM OrderItems o"),
    @NamedQuery(name = "OrderItems.findByOrderId", query = "SELECT o FROM OrderItems o WHERE o.orderItemsPK.orderId = :orderId"),
    @NamedQuery(name = "OrderItems.findByMealId", query = "SELECT o FROM OrderItems o WHERE o.orderItemsPK.mealId = :mealId"),
    @NamedQuery(name = "OrderItems.findByQuantity", query = "SELECT o FROM OrderItems o WHERE o.quantity = :quantity"),
    @NamedQuery(name = "OrderItems.findByPriceAtTime", query = "SELECT o FROM OrderItems o WHERE o.priceAtTime = :priceAtTime")})
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrderItemsPK orderItemsPK;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "price_at_time")
    private BigDecimal priceAtTime;
    @JoinColumn(name = "meal_id", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Meals meals;
    @JoinColumn(name = "order_id", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Orders orders;

    public OrderItems() {
    }

    public OrderItems(OrderItemsPK orderItemsPK) {
        this.orderItemsPK = orderItemsPK;
    }

    public OrderItems(OrderItemsPK orderItemsPK, int quantity, BigDecimal priceAtTime) {
        this.orderItemsPK = orderItemsPK;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    public OrderItems(String orderId, String mealId) {
        this.orderItemsPK = new OrderItemsPK(orderId, mealId);
    }

    public OrderItemsPK getOrderItemsPK() {
        return orderItemsPK;
    }

    public void setOrderItemsPK(OrderItemsPK orderItemsPK) {
        this.orderItemsPK = orderItemsPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public Meals getMeals() {
        return meals;
    }

    public void setMeals(Meals meals) {
        this.meals = meals;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderItemsPK != null ? orderItemsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderItems)) {
            return false;
        }
        OrderItems other = (OrderItems) object;
        if ((this.orderItemsPK == null && other.orderItemsPK != null) || (this.orderItemsPK != null && !this.orderItemsPK.equals(other.orderItemsPK))) {
            return false;
        }
        return true;
    }

   
    public void fixPriceAtTime() {
    if (this.meals != null && this.priceAtTime == null) {
        this.priceAtTime = this.meals.getPrice();
    }
}
    @Override
    public String toString() {
        return "foodhub.OrderItems[ orderItemsPK=" + orderItemsPK + " ]";
    }
    
}
