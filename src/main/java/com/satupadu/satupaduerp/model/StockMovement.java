package com.satupadu.satupaduerp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// riwayat keluar-masuk barang
@Entity
@Table(name = "stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private int movementId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "movement_type")
    private String movementType;    // Masuk, Keluar

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "reference_type")
    private String referenceType;   // SO, PO, Penyesuaian

    @Column(name = "reference_id")
    private Integer referenceId;

    public StockMovement() {
    }

    public StockMovement(Product product, String movementType, int quantity,
                         String referenceType, Integer referenceId) {
        this.product = product;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }
}
