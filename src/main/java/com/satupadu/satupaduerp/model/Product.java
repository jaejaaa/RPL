package com.satupadu.satupaduerp.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productCode;
    private String productName;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stock;
    private int minimumStock;

    public Product() {
    }

    public Product(int productId, String productCode, String productName, String unit,
                   BigDecimal purchasePrice, BigDecimal sellingPrice, int stock, int minimumStock) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
        this.minimumStock = minimumStock;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public boolean isStokMenipis() {
        return stock <= minimumStock;
    }
}
