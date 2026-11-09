package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.dao.SupplierDAO;
import com.satupadu.satupaduerp.model.Supplier;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SupplierDAOImpl implements SupplierDAO {
    private final SessionFactory sessionFactory;

    public SupplierDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAll(String keyword) {
        String hql = "FROM Supplier s WHERE s.supplierName LIKE :kata OR s.phone LIKE :kata "
                   + "ORDER BY s.supplierName";

        return sessionFactory.getCurrentSession().createQuery(hql, Supplier.class)
            .setParameter("kata", "%" + keyword + "%")
            .list();
    }

    @Override
    public void insert(Supplier s) {
        sessionFactory.getCurrentSession().persist(s);
    }

    @Override
    public void update(Supplier s) {
        sessionFactory.getCurrentSession().merge(s);
    }

    @Override
    public void delete(int supplierId) {
        sessionFactory.getCurrentSession().remove(
            sessionFactory.getCurrentSession().find(Supplier.class, supplierId));
    }
}
