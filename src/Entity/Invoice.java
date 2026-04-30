package Entity;

import Enums.InvoiceStatus;
import Entity.Orders;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "invoice")
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i"),
    @NamedQuery(name = "Invoice.findById", query = "SELECT i FROM Invoice i WHERE i.id = :id"),
    @NamedQuery(name = "Invoice.findByStatus", query = "SELECT i FROM Invoice i WHERE i.status = :status")
})
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Basic(optional = false)
    @Column(name = "fees")
    private BigDecimal fees;

    @Basic(optional = false)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Basic(optional = false)
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @JoinColumn(name = "order_id", referencedColumnName = "ID")
    @OneToOne(optional = false)
    private Orders orderId;

    // Constructors
    public Invoice() {
    }

    public Invoice(String id) {
        this.id = id;
    }

    public BigDecimal calculateFees(double taxPercentage, BigDecimal deliveryFee) {
        if (this.orderId != null) {
            this.orderId.calculateSubtotal();
            BigDecimal orderSubtotal = this.orderId.getSubtotal();
            
            if (orderSubtotal != null) {
                BigDecimal taxDec = BigDecimal.valueOf(taxPercentage / 100.0);
                BigDecimal taxAmount = orderSubtotal.multiply(taxDec);
                this.fees = taxAmount.add(deliveryFee);
                return this.fees;
            }
        }
        return BigDecimal.ZERO;
    }

    public void calculateFinalTotal(double taxPercentage, BigDecimal deliveryFee) {
        if (this.orderId != null && this.orderId.getSubtotal() != null) {
            BigDecimal computedFees = calculateFees(taxPercentage, deliveryFee);
            this.totalPrice = this.orderId.getSubtotal().add(computedFees);
        }
    }

    public void insert(EntityManager em, double tax, BigDecimal delivery) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            this.calculateFinalTotal(tax, delivery); 
            em.persist(this);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

public void update(EntityManager em, InvoiceStatus newStatus, Date newPaymentDate) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Invoice existingInvoice = em.find(Invoice.class, this.id);
            if (existingInvoice != null) {
                existingInvoice.setStatus(newStatus);
                existingInvoice.setPaymentDate(newPaymentDate);
                
                this.status = newStatus;
                this.paymentDate = newPaymentDate;
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
            Invoice i = em.find(Invoice.class, id);
            if (i != null) em.remove(i);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static BigDecimal getDailyRevenue(EntityManager em) {
        String jpql = "SELECT SUM(i.totalPrice) FROM Invoice i WHERE i.paymentDate >= :today AND i.status = :status";
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        Date today = cal.getTime();

        try {
            return em.createQuery(jpql, BigDecimal.class)
                    .setParameter("today", today)
                    .setParameter("status", InvoiceStatus.PAID)
                    .getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setOrderId(Orders orderId) { this.orderId = orderId; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    @Override
    public String toString() {
        return "Invoice[ id=" + id + ", total=" + totalPrice + " ]";
    }
}