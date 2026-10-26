package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.Koneksi;
import com.satupadu.satupaduerp.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ambil semua produk, bisa difilter berdasarkan kode atau nama
    public List<Product> getAll(String keyword) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_code LIKE ? OR product_name LIKE ? "
                   + "ORDER BY product_code";
        List<Product> list = new ArrayList<>();

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO products (product_code, product_name, unit, purchase_price, "
                   + "selling_price, stock, minimum_stock) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getProductCode());
            ps.setString(2, p.getProductName());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPurchasePrice());
            ps.setBigDecimal(5, p.getSellingPrice());
            ps.setInt(6, p.getStock());
            ps.setInt(7, p.getMinimumStock());
            ps.executeUpdate();
        }
    }

    // stok tidak ikut diubah, karena stok hanya berubah lewat sales order / purchase order
    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET product_code = ?, product_name = ?, unit = ?, "
                   + "purchase_price = ?, selling_price = ?, minimum_stock = ? WHERE product_id = ?";

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getProductCode());
            ps.setString(2, p.getProductName());
            ps.setString(3, p.getUnit());
            ps.setBigDecimal(4, p.getPurchasePrice());
            ps.setBigDecimal(5, p.getSellingPrice());
            ps.setInt(6, p.getMinimumStock());
            ps.setInt(7, p.getProductId());
            ps.executeUpdate();
        }
    }

    public void delete(int productId) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }

    public List<Product> getStokMenipis() throws SQLException {
        String sql = "SELECT * FROM products WHERE stock <= minimum_stock ORDER BY stock";
        List<Product> list = new ArrayList<>();

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Product map(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("product_code"),
            rs.getString("product_name"),
            rs.getString("unit"),
            rs.getBigDecimal("purchase_price"),
            rs.getBigDecimal("selling_price"),
            rs.getInt("stock"),
            rs.getInt("minimum_stock")
        );
    }
}
