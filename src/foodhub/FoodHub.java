/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package foodhub;

import DataSender.dataSender;
import Entity.Categories;
import Entity.Customers;
import Entity.Invoice;
import Entity.Meals;
import Entity.OrderItems;
import Entity.Orders;
import Enums.InvoiceStatus;
import Enums.OrderStatus;
import ReportsQueries.ReportsQueries;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author Dragon
 */
public class FoodHub {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FoodHubPU");
        EntityManager em = emf.createEntityManager();

      System.out.println("⏳ جاري إدخال البيانات...");
        
        // 2. تشغيل الإنسرتات لملء الداتا بيز
        dataSender.populateAll(em);
        
        System.out.println("======================================");
        System.out.println("📊 تقرير أكثر الوجبات مبيعاً (Top-Selling Meals)");
        System.out.println("======================================");

        // 3. اختبار أول كويري
        List<Object[]> topSellingMeals = ReportsQueries.getTopSellingMeals(em);

        // 4. طباعة الناتج بشكل منظم
        // الكويري بترجع: result[0] = اسم الوجبة، result[1] = مجموع الكمية
        if (topSellingMeals.isEmpty()) {
            System.out.println("لا توجد مبيعات حتى الآن.");
        } else {
            for (Object[] result : topSellingMeals) {
                String mealName = String.valueOf(result[0]);
                String totalQuantity = String.valueOf(result[1]); 
                
                System.out.println("🍔 الوجبة: " + mealName 
                                 + " | 📈 إجمالي الكمية المباعة: " + totalQuantity);
            }
        }
        
        System.out.println("======================================");

        // 5. قفل الاتصال
        em.close();
        emf.close();
    }
    }

