package com.satupadu.satupaduerp;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// menyediakan satu SessionFactory untuk seluruh aplikasi
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    // dibuat saat pertama kali dipakai; kalau gagal (misal MySQL mati) bisa dicoba lagi
    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        return sessionFactory;
    }
}
