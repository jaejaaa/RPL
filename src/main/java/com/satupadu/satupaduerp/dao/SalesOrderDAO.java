package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.model.CashTransaction;
import com.satupadu.satupaduerp.model.SalesOrder;
import com.satupadu.satupaduerp.model.StockMovement;
import java.time.LocalDate;
import java.util.List;

public interface SalesOrderDAO {
    List<SalesOrder> getAll();

    SalesOrder findById(int soId);

    // format nomor: SO-20261109-001 (urutan per hari)
    String nomorBerikutnya(LocalDate tanggal);

    void save(SalesOrder so);

    void saveStockMovement(StockMovement m);

    void saveCashTransaction(CashTransaction t);
}
