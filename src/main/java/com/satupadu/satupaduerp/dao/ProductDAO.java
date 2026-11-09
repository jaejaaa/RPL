package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.model.Product;
import java.util.List;

public interface ProductDAO {
    // ambil semua produk, bisa difilter berdasarkan kode atau nama
    List<Product> getAll(String keyword);

    Product findById(int productId);

    void insert(Product p);

    // stok tidak ikut diubah, karena stok hanya berubah lewat sales order / purchase order
    void update(Product p);

    void delete(int productId);

    List<Product> getStokMenipis();
}
