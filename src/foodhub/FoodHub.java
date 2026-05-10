package foodhub;

import Presentation.AppConsole;
import DataSender.dataSender;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FoodHub {

    public static void main(String[] args) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("FoodHubPU");
            em = emf.createEntityManager();

            System.out.println(">>> Database Connected Successfully!");
            System.out.println(">>> Initializing Default Data...");
            dataSender.seedDatabase(em);
            AppConsole.startMenu(em);

        } catch (Exception e) {
            System.err.println("!!! Critical Error during startup !!!");
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
            System.out.println(">>> Connection Closed. Goodbye!");
        }
    }
}