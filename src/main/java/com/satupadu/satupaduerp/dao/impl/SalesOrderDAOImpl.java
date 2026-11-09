package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.dao.SalesOrderDAO;
import com.satupadu.satupaduerp.model.CashTransaction;
import com.satupadu.satupaduerp.model.SalesOrder;
import com.satupadu.satupaduerp.model.StockMovement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SalesOrderDAOImpl implements SalesOrderDAO {
    private final SessionFactory sessionFactory;

    public SalesOrderDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesOrder> getAll() {
        return sessionFactory.getCurrentSession().createQuery(
                "FROM SalesOrder so JOIN FETCH so.customer ORDER BY so.soId DESC", SalesOrder.class)
            .list();
    }

    @Override
    public SalesOrder findById(int soId) {
        return sessionFactory.getCurrentSession().find(SalesOrder.class, soId);
    }

    @Override
    public String nomorBerikutnya(LocalDate tanggal) {
        long jumlah = sessionFactory.getCurrentSession().createQuery(
                "SELECT COUNT(so) FROM SalesOrder so WHERE so.orderDate = :tgl", Long.class)
            .setParameter("tgl", tanggal)
            .getSingleResult();
        return "SO-" + tanggal.format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + String.format("%03d", jumlah + 1);
    }

    @Override
    public void save(SalesOrder so) {
        sessionFactory.getCurrentSession().persist(so);   // barang-barangnya ikut tersimpan (cascade)
    }

    @Override
    public void saveStockMovement(StockMovement m) {
        sessionFactory.getCurrentSession().persist(m);
    }

    @Override
    public void saveCashTransaction(CashTransaction t) {
        sessionFactory.getCurrentSession().persist(t);
    }
}
