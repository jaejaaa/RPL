package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.dao.SupplierDAO;
import com.satupadu.satupaduerp.model.Supplier;
import jakarta.persistence.PersistenceException;
import java.util.List;

// penghubung antara FormSupplier (view) dengan SupplierDAO (model)
public class SupplierController {
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public List<Supplier> cari(String keyword) {
        try {
            return supplierDAO.getAll(keyword.trim());
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Gagal memuat data supplier: " + e.getMessage());
        }
    }

    // supplierId 0 = tambah supplier baru, selain itu = ubah supplier
    public String simpan(int supplierId, String nama, String telepon, String email, String alamat) {
        Validasi.mitra("Nama supplier", nama, telepon, email);

        Supplier s = new Supplier();
        s.setSupplierId(supplierId);
        s.setSupplierName(nama.trim());
        s.setPhone(telepon.trim());
        s.setEmail(email.trim());
        s.setAddress(alamat.trim());
        try {
            if (supplierId == 0) {
                supplierDAO.insert(s);
                return "Supplier berhasil ditambahkan.";
            }
            supplierDAO.update(s);
            return "Supplier berhasil diubah.";
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Gagal menyimpan: " + e.getMessage());
        }
    }

    public String hapus(int supplierId) {
        try {
            supplierDAO.delete(supplierId);
            return "Supplier berhasil dihapus.";
        } catch (PersistenceException e) {
            if (KodeError.dari(e) == KodeError.MASIH_DIPAKAI) {
                throw new IllegalArgumentException("Supplier tidak bisa dihapus karena sudah dipakai di purchase order.");
            }
            throw new IllegalArgumentException("Gagal menghapus: " + e.getMessage());
        }
    }
}
