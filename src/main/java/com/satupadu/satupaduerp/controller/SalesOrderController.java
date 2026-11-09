package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.Session;
import com.satupadu.satupaduerp.dao.CustomerDAO;
import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.dao.SalesOrderDAO;
import com.satupadu.satupaduerp.model.Customer;
import com.satupadu.satupaduerp.model.Product;
import com.satupadu.satupaduerp.model.SalesOrder;
import com.satupadu.satupaduerp.service.SalesOrderService;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

// penghubung antara FormSalesOrder (view) dengan SalesOrderService & DAO (model)
@Component
public class SalesOrderController {
    private final SalesOrderService salesOrderService;
    private final SalesOrderDAO salesOrderDAO;
    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;

    public SalesOrderController(SalesOrderService salesOrderService, SalesOrderDAO salesOrderDAO,
                                ProductDAO productDAO, CustomerDAO customerDAO) {
        this.salesOrderService = salesOrderService;
        this.salesOrderDAO = salesOrderDAO;
        this.productDAO = productDAO;
        this.customerDAO = customerDAO;
    }

    public List<SalesOrder> daftarPesanan() {
        try {
            return salesOrderDAO.getAll();
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal memuat sales order: " + e.getMessage());
        }
    }

    public List<Product> daftarProduk() {
        try {
            return productDAO.getAll("");
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal memuat produk: " + e.getMessage());
        }
    }

    public List<Customer> daftarPelanggan() {
        try {
            return customerDAO.getAll("");
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal memuat pelanggan: " + e.getMessage());
        }
    }

    // barang: productId -> jumlah
    public String simpan(Customer pelanggan, Map<Integer, Integer> barang, boolean konfirmasi) {
        if (pelanggan == null) {
            throw new IllegalArgumentException("Pilih pelanggan terlebih dahulu.");
        }
        try {
            String nomor = salesOrderService.buatPesanan(pelanggan.getCustomerId(), barang,
                Session.getUser().getUserId(), konfirmasi);
            return konfirmasi
                ? "Sales order " + nomor + " tersimpan dan dikonfirmasi.\nStok berkurang dan pemasukan tercatat."
                : "Sales order " + nomor + " tersimpan sebagai Draft.";
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal menyimpan sales order: " + e.getMessage());
        }
    }

    public String konfirmasi(int soId) {
        try {
            salesOrderService.konfirmasi(soId, Session.getUser().getUserId());
            return "Sales order dikonfirmasi.\nStok berkurang dan pemasukan tercatat.";
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal mengonfirmasi: " + e.getMessage());
        }
    }

    public String batalkan(int soId) {
        try {
            salesOrderService.batalkan(soId);
            return "Sales order dibatalkan.";
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal membatalkan: " + e.getMessage());
        }
    }
}
