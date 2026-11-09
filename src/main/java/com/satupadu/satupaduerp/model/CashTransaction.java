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
import java.time.LocalDate;

// pemasukan dan pengeluaran kas (tabel transactions)
@Entity
@Table(name = "transactions")
public class CashTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "type")
    private String type;            // Pemasukan, Pengeluaran

    @Column(name = "category")
    private String category;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "reference_type")
    private String referenceType;   // SO, PO, Manual

    @Column(name = "reference_id")
    private Integer referenceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CashTransaction() {
    }

    public CashTransaction(String type, String category, BigDecimal amount, String description,
                           String referenceType, Integer referenceId, User user) {
        this.transactionDate = LocalDate.now();
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.user = user;
    }
}
