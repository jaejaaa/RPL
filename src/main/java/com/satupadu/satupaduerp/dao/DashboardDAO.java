package com.satupadu.satupaduerp.dao;

import java.math.BigDecimal;

// query ringkasan untuk halaman dashboard
public interface DashboardDAO {
    long jumlahProduk();

    long jumlahPelanggan();

    long jumlahSupplier();

    BigDecimal penjualanBulanIni();

    BigDecimal saldoKas();
}
