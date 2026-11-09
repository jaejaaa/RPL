package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.dao.DashboardDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class DashboardDAOImpl implements DashboardDAO {
    private final SessionFactory sessionFactory;

    public DashboardDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public long jumlahProduk() {
        return hitung("SELECT COUNT(p) FROM Product p");
    }

    @Override
    public long jumlahPelanggan() {
        return hitung("SELECT COUNT(c) FROM Customer c");
    }

    @Override
    public long jumlahSupplier() {
        return hitung("SELECT COUNT(s) FROM Supplier s");
    }

    @Override
    public BigDecimal penjualanBulanIni() {
        LocalDate awalBulan = LocalDate.now().withDayOfMonth(1);
        return sessionFactory.getCurrentSession().createQuery(
                "SELECT COALESCE(SUM(so.totalAmount), 0) FROM SalesOrder so "
              + "WHERE so.status = 'Dikonfirmasi' AND so.orderDate >= :awal AND so.orderDate < :akhir",
                BigDecimal.class)
            .setParameter("awal", awalBulan)
            .setParameter("akhir", awalBulan.plusMonths(1))
            .getSingleResult();
    }

    @Override
    public BigDecimal saldoKas() {
        return sessionFactory.getCurrentSession().createQuery(
                "SELECT COALESCE(SUM(CASE WHEN t.type = 'Pemasukan' THEN t.amount ELSE -t.amount END), 0) "
              + "FROM CashTransaction t", BigDecimal.class)
            .getSingleResult();
    }

    private long hitung(String hql) {
        return sessionFactory.getCurrentSession().createQuery(hql, Long.class).getSingleResult();
    }
}
