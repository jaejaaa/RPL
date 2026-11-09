package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.model.Customer;
import java.util.List;

public interface CustomerDAO {
    List<Customer> getAll(String keyword);

    Customer findById(int customerId);

    void insert(Customer c);

    void update(Customer c);

    void delete(int customerId);
}
