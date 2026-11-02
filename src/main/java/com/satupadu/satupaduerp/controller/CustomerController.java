package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.dao.CustomerDAO;
import com.satupadu.satupaduerp.model.Customer;
import jakarta.persistence.PersistenceException;
import java.util.List;

// penghubung antara FormPelanggan (view) dengan CustomerDAO (model)
public class CustomerController {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> cari(String keyword) {
        try {
            return customerDAO.getAll(keyword.trim());
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Gagal memuat data pelanggan: " + e.getMessage());
        }
    }

    // customerId 0 = tambah pelanggan baru, selain itu = ubah pelanggan
    public String simpan(int customerId, String nama, String telepon, String email, String alamat) {
        Validasi.mitra("Nama pelanggan", nama, telepon, email);

        Customer c = new Customer();
        c.setCustomerId(customerId);
        c.setCustomerName(nama.trim());
        c.setPhone(telepon.trim());
        c.setEmail(email.trim());
        c.setAddress(alamat.trim());
        try {
            if (customerId == 0) {
                customerDAO.insert(c);
                return "Pelanggan berhasil ditambahkan.";
            }
            customerDAO.update(c);
            return "Pelanggan berhasil diubah.";
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Gagal menyimpan: " + e.getMessage());
        }
    }

    public String hapus(int customerId) {
        try {
            customerDAO.delete(customerId);
            return "Pelanggan berhasil dihapus.";
        } catch (PersistenceException e) {
            if (KodeError.dari(e) == KodeError.MASIH_DIPAKAI) {
                throw new IllegalArgumentException("Pelanggan tidak bisa dihapus karena sudah dipakai di sales order.");
            }
            throw new IllegalArgumentException("Gagal menghapus: " + e.getMessage());
        }
    }
}
