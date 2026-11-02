package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.HibernateUtil;
import java.math.BigDecimal;
import org.hibernate.Session;

// query ringkasan untuk halaman dashboard
public class DashboardDAO {

    public long jumlahProduk() {
        return hitung("SELECT COUNT(p) FROM Product p");
    }

    public long jumlahPelanggan() {
        return hitung("SELECT COUNT(c) FROM Customer c");
    }

    public long jumlahSupplier() {
        return hitung("SELECT COUNT(s) FROM Supplier s");
    }

    // tabel sales_orders & transactions belum dipetakan ke class, jadi masih memakai native SQL
    public BigDecimal penjualanBulanIni() {
        return total("SELECT COALESCE(SUM(total_amount), 0) FROM sales_orders "
                   + "WHERE status = 'Dikonfirmasi' "
                   + "AND MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())");
    }

    public BigDecimal saldoKas() {
        return total("SELECT COALESCE(SUM(CASE WHEN type = 'Pemasukan' THEN amount ELSE -amount END), 0) "
                   + "FROM transactions");
    }

    private long hitung(String hql) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Long.class).getSingleResult();
        }
    }

    private BigDecimal total(String sql) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createNativeQuery(sql, BigDecimal.class).getSingleResult();
        }
    }
}
