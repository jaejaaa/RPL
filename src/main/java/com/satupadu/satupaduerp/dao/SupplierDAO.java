package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.model.Supplier;
import java.util.List;

public interface SupplierDAO {
    List<Supplier> getAll(String keyword);

    void insert(Supplier s);

    void update(Supplier s);

    void delete(int supplierId);
}
