package CRUD;

import Entity.Categories;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class CategoryDAO {

    private EntityManager em;

    public CategoryDAO(EntityManager em) {
        this.em = em;
    }
    public void insert(Categories category) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public Categories findById(int id) {
        return em.find(Categories.class, id);
    }
    public List<Categories> findAll() {
        return em.createNamedQuery("Categories.findAll", Categories.class)
                 .getResultList();
    }

    public List<Categories> findByName(String name) {
        return em.createNamedQuery("Categories.findByName", Categories.class)
                 .setParameter("name", name)
                 .getResultList();
    }

    public void update(Categories category) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
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
}