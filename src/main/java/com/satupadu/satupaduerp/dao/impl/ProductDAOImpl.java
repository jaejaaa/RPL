package com.satupadu.satupaduerp.dao.impl;

import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.model.Product;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ProductDAOImpl implements ProductDAO {
    private final SessionFactory sessionFactory;

    public ProductDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAll(String keyword) {
        String hql = "FROM Product p WHERE p.productCode LIKE :kata OR p.productName LIKE :kata "
                   + "ORDER BY p.productCode";

        return sessionFactory.getCurrentSession().createQuery(hql, Product.class)
            .setParameter("kata", "%" + keyword + "%")
            .list();
    }

    @Override
    public Product findById(int productId) {
        return sessionFactory.getCurrentSession().find(Product.class, productId);
    }

    @Override
    public void insert(Product p) {
        sessionFactory.getCurrentSession().persist(p);
    }

    @Override
    public void update(Product p) {
        Product lama = findById(p.getProductId());
        if (lama == null) {
            throw new IllegalArgumentException("Produk sudah tidak ada di database.");
        }
        lama.setProductCode(p.getProductCode());
        lama.setProductName(p.getProductName());
        lama.setUnit(p.getUnit());
        lama.setPurchasePrice(p.getPurchasePrice());
        lama.setSellingPrice(p.getSellingPrice());
        lama.setMinimumStock(p.getMinimumStock());
    }

    @Override
    public void delete(int productId) {
        sessionFactory.getCurrentSession().remove(findById(productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getStokMenipis() {
        String hql = "FROM Product p WHERE p.stock <= p.minimumStock ORDER BY p.stock";
        return sessionFactory.getCurrentSession().createQuery(hql, Product.class).list();
    }
}
