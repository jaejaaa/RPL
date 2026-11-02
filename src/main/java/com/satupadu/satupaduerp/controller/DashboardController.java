package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.dao.DashboardDAO;
import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.model.Product;
import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.util.List;

// penghubung antara MenuUtama (view) dengan DashboardDAO & ProductDAO (model)
public class DashboardController {
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private BigDecimal penjualanBulanIni;
    private BigDecimal saldoKas;
    private long jumlahProduk;
    private long jumlahPelanggan;
    private long jumlahSupplier;
    private List<Product> stokMenipis;

    // ambil semua data ringkasan sekaligus, lalu view tinggal membaca lewat getter
    public void muatData() {
        try {
            penjualanBulanIni = dashboardDAO.penjualanBulanIni();
            saldoKas = dashboardDAO.saldoKas();
            jumlahProduk = dashboardDAO.jumlahProduk();
            jumlahPelanggan = dashboardDAO.jumlahPelanggan();
            jumlahSupplier = dashboardDAO.jumlahSupplier();
            stokMenipis = productDAO.getStokMenipis();
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Gagal memuat dashboard: " + e.getMessage());
        }
    }

    public BigDecimal getPenjualanBulanIni() {
        return penjualanBulanIni;
    }

    public BigDecimal getSaldoKas() {
        return saldoKas;
    }

    public long getJumlahProduk() {
        return jumlahProduk;
    }

    public long getJumlahPelanggan() {
        return jumlahPelanggan;
    }

    public long getJumlahSupplier() {
        return jumlahSupplier;
    }

    public List<Product> getStokMenipis() {
        return stokMenipis;
    }
}
