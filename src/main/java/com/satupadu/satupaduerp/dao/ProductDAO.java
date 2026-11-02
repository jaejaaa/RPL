package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.HibernateUtil;
import com.satupadu.satupaduerp.model.Product;
import java.util.List;
import org.hibernate.Session;

public class ProductDAO {

    // ambil semua produk, bisa difilter berdasarkan kode atau nama
    public List<Product> getAll(String keyword) {
        String hql = "FROM Product p WHERE p.productCode LIKE :kata OR p.productName LIKE :kata "
                   + "ORDER BY p.productCode";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Product.class)
                .setParameter("kata", "%" + keyword + "%")
                .list();
        }
    }

    public void insert(Product p) {
        HibernateUtil.getSessionFactory().inTransaction(session -> session.persist(p));
    }

    // stok tidak ikut diubah, karena stok hanya berubah lewat sales order / purchase order
    public void update(Product p) {
        HibernateUtil.getSessionFactory().inTransaction(session -> {
            Product lama = session.find(Product.class, p.getProductId());
            if (lama == null) {
                throw new IllegalArgumentException("Produk sudah tidak ada di database.");
            }
            lama.setProductCode(p.getProductCode());
            lama.setProductName(p.getProductName());
            lama.setUnit(p.getUnit());
            lama.setPurchasePrice(p.getPurchasePrice());
            lama.setSellingPrice(p.getSellingPrice());
            lama.setMinimumStock(p.getMinimumStock());
        });
    }

    public void delete(int productId) {
        HibernateUtil.getSessionFactory().inTransaction(session ->
            session.remove(session.find(Product.class, productId)));
    }

    public List<Product> getStokMenipis() {
        String hql = "FROM Product p WHERE p.stock <= p.minimumStock ORDER BY p.stock";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Product.class).list();
        }
    }
}
