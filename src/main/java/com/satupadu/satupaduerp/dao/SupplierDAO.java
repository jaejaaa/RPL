package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.HibernateUtil;
import com.satupadu.satupaduerp.model.Supplier;
import java.util.List;
import org.hibernate.Session;

public class SupplierDAO {

    public List<Supplier> getAll(String keyword) {
        String hql = "FROM Supplier s WHERE s.supplierName LIKE :kata OR s.phone LIKE :kata "
                   + "ORDER BY s.supplierName";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Supplier.class)
                .setParameter("kata", "%" + keyword + "%")
                .list();
        }
    }

    public void insert(Supplier s) {
        HibernateUtil.getSessionFactory().inTransaction(session -> session.persist(s));
    }

    public void update(Supplier s) {
        HibernateUtil.getSessionFactory().inTransaction(session -> session.merge(s));
    }

    public void delete(int supplierId) {
        HibernateUtil.getSessionFactory().inTransaction(session ->
            session.remove(session.find(Supplier.class, supplierId)));
    }
}
