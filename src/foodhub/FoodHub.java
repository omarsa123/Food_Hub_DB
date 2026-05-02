package foodhub;

import Presentation.AppConsole;
import DataSender.dataSender;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FoodHub {

    public static void main(String[] args) {
        // 1. إنشاء مصنع مدير الكيانات (استبدل الاسم بالـ PU الخاص بك)
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // حاول فتح الاتصال
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
            // 4. إغلاق الموارد بأمان عند قفل البرنامج
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