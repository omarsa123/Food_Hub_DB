package CRUD;

import Entity.Customers;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class CustomerDAO {

    private EntityManager em;

    public CustomerDAO(EntityManager em) {
        this.em = em;
    }
    public void insert(Customers customer) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(customer);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    public Customers findById(int id) {
        return em.find(Customers.class, id);
    }
    public List<Customers> findAll() {
        return em.createNamedQuery("Customers.findAll", Customers.class)
                 .getResultList();
    }
    public void update(Customers customer) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(customer);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    public void delete(int id) {
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
}