package com.satupadu.satupaduerp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_items")
public class SalesOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "so_item_id")
    private int soItemId;

    @ManyToOne
    @JoinColumn(name = "so_id")
    private SalesOrder salesOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "subtotal")
    private BigDecimal subtotal;

    public SalesOrderItem() {
    }

    // harga diambil dari harga jual produk saat pesanan dibuat
    public SalesOrderItem(SalesOrder salesOrder, Product product, int quantity) {
        this.salesOrder = salesOrder;
        this.product = product;
        this.quantity = quantity;
        this.price = product.getSellingPrice();
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
