package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.dao.CustomerDAO;
import com.satupadu.satupaduerp.model.Customer;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CustomerDAOImpl implements CustomerDAO {
    private final SessionFactory sessionFactory;

    public CustomerDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAll(String keyword) {
        String hql = "FROM Customer c WHERE c.customerName LIKE :kata OR c.phone LIKE :kata "
                   + "ORDER BY c.customerName";

        return sessionFactory.getCurrentSession().createQuery(hql, Customer.class)
            .setParameter("kata", "%" + keyword + "%")
            .list();
    }

    @Override
    public Customer findById(int customerId) {
        return sessionFactory.getCurrentSession().find(Customer.class, customerId);
    }

    @Override
    public void insert(Customer c) {
        sessionFactory.getCurrentSession().persist(c);
    }

    @Override
    public void update(Customer c) {
        sessionFactory.getCurrentSession().merge(c);
    }

    @Override
    public void delete(int customerId) {
        sessionFactory.getCurrentSession().remove(findById(customerId));
    }
}
