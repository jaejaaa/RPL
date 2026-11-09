package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.PasswordUtil;
import com.satupadu.satupaduerp.dao.UserDAO;
import com.satupadu.satupaduerp.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;

    // dependency injection lewat constructor: SessionFactory diberikan oleh Spring
    public UserDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public User login(String username, String password) {
        String hql = "FROM User u JOIN FETCH u.role "
                   + "WHERE u.username = :username AND u.password = :password AND u.active = true";

        return sessionFactory.getCurrentSession().createQuery(hql, User.class)
            .setParameter("username", username)
            .setParameter("password", PasswordUtil.hash(password))
            .uniqueResult();
    }

    @Override
    public User getReference(int userId) {
        return sessionFactory.getCurrentSession().getReference(User.class, userId);
    }
}
