package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.HibernateUtil;
import com.satupadu.satupaduerp.model.Customer;
import java.util.List;
import org.hibernate.Session;

public class CustomerDAO {

    public List<Customer> getAll(String keyword) {
        String hql = "FROM Customer c WHERE c.customerName LIKE :kata OR c.phone LIKE :kata "
                   + "ORDER BY c.customerName";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Customer.class)
                .setParameter("kata", "%" + keyword + "%")
                .list();
        }
    }

    public void insert(Customer c) {
        HibernateUtil.getSessionFactory().inTransaction(session -> session.persist(c));
    }

    public void update(Customer c) {
        HibernateUtil.getSessionFactory().inTransaction(session -> session.merge(c));
    }

    public void delete(int customerId) {
        HibernateUtil.getSessionFactory().inTransaction(session ->
            session.remove(session.find(Customer.class, customerId)));
    }
}
