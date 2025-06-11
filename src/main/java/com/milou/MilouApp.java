package com.milou;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class MilouApp {
    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        sessionFactory.close();
    }
}