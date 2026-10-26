package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

// penghubung antara FormProduk (view) dengan ProductDAO (model)
// jika gagal, setiap method melempar IllegalArgumentException berisi pesan untuk ditampilkan view
public class ProductController {
    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> cari(String keyword) {
        try {
            return productDAO.getAll(keyword.trim());
        } catch (SQLException e) {
            throw new IllegalArgumentException("Gagal memuat data produk: " + e.getMessage());
        }
    }

    // productId 0 = tambah produk baru, selain itu = ubah produk
    public String simpan(int productId, String kode, String nama, String satuan, String hargaBeli,
                         String hargaJual, String stok, String stokMin) {
        Product p = validasi(kode, nama, satuan, hargaBeli, hargaJual, stok, stokMin);
        try {
            if (productId == 0) {
                productDAO.insert(p);
                return "Produk berhasil ditambahkan.";
            }
            p.setProductId(productId);
            productDAO.update(p);
            return "Produk berhasil diubah.";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {   // duplicate entry
                throw new IllegalArgumentException("Kode produk " + p.getProductCode() + " sudah dipakai.");
            }
            throw new IllegalArgumentException("Gagal menyimpan: " + e.getMessage());
        }
    }

    public String hapus(int productId) {
        try {
            productDAO.delete(productId);
            return "Produk berhasil dihapus.";
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {   // masih dipakai tabel lain (foreign key)
                throw new IllegalArgumentException("Produk tidak bisa dihapus karena sudah dipakai di transaksi.");
            }
            throw new IllegalArgumentException("Gagal menghapus: " + e.getMessage());
        }
    }

    private Product validasi(String kode, String nama, String satuan, String hargaBeli,
                             String hargaJual, String stok, String stokMin) {
        if (kode.isBlank() || nama.isBlank() || satuan.isBlank()) {
            throw new IllegalArgumentException("Kode, nama, dan satuan wajib diisi.");
        }
        Product p = new Product();
        try {
            p.setProductCode(kode.trim());
            p.setProductName(nama.trim());
            p.setUnit(satuan.trim());
            p.setPurchasePrice(new BigDecimal(hargaBeli.trim()));
            p.setSellingPrice(new BigDecimal(hargaJual.trim()));
            p.setStock(Integer.parseInt(stok.trim()));
            p.setMinimumStock(Integer.parseInt(stokMin.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harga dan stok harus berupa angka (tanpa titik/koma).");
        }
        if (p.getPurchasePrice().signum() < 0 || p.getSellingPrice().signum() < 0
                || p.getStock() < 0 || p.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Harga dan stok tidak boleh negatif.");
        }
        return p;
    }
}
