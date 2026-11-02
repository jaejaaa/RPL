package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.HibernateUtil;
import com.satupadu.satupaduerp.PasswordUtil;
import com.satupadu.satupaduerp.model.User;
import org.hibernate.Session;

public class UserDAO {

    // mengembalikan objek User jika username & password cocok, null jika tidak
    public User login(String username, String password) {
        String hql = "FROM User u JOIN FETCH u.role "
                   + "WHERE u.username = :username AND u.password = :password AND u.active = true";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, User.class)
                .setParameter("username", username)
                .setParameter("password", PasswordUtil.hash(password))
                .uniqueResult();
        }
    }
}
