package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.dao.SupplierDAO;
import com.satupadu.satupaduerp.model.Supplier;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

// penghubung antara FormSupplier (view) dengan SupplierDAO (model)
@Component
public class SupplierController {
    private final SupplierDAO supplierDAO;

    // dependency injection lewat constructor: objek DAO disediakan oleh Spring
    public SupplierController(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public List<Supplier> cari(String keyword) {
        try {
            return supplierDAO.getAll(keyword.trim());
        } catch (DataAccessException | TransactionException e) {
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
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal menyimpan: " + e.getMessage());
        }
    }

    public String hapus(int supplierId) {
        try {
            supplierDAO.delete(supplierId);
            return "Supplier berhasil dihapus.";
        } catch (DataAccessException | TransactionException e) {
            if (KodeError.dari(e) == KodeError.MASIH_DIPAKAI) {
                throw new IllegalArgumentException("Supplier tidak bisa dihapus karena sudah dipakai di purchase order.");
            }
            throw new IllegalArgumentException("Gagal menghapus: " + e.getMessage());
        }
    }
}
