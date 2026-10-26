package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.Koneksi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// query ringkasan untuk halaman dashboard
public class DashboardDAO {

    public int jumlahProduk() throws SQLException {
        return angka("SELECT COUNT(*) FROM products").intValue();
    }

    public int jumlahPelanggan() throws SQLException {
        return angka("SELECT COUNT(*) FROM customers").intValue();
    }

    public int jumlahSupplier() throws SQLException {
        return angka("SELECT COUNT(*) FROM suppliers").intValue();
    }

    public BigDecimal penjualanBulanIni() throws SQLException {
        return angka("SELECT COALESCE(SUM(total_amount), 0) FROM sales_orders "
                   + "WHERE status = 'Dikonfirmasi' "
                   + "AND MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())");
    }

    public BigDecimal saldoKas() throws SQLException {
        return angka("SELECT COALESCE(SUM(CASE WHEN type = 'Pemasukan' THEN amount ELSE -amount END), 0) "
                   + "FROM transactions");
    }

    private BigDecimal angka(String sql) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getBigDecimal(1);
        }
    }
}
