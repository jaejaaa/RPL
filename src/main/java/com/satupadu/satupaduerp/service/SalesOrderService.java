package com.satupadu.satupaduerp.service;

import com.satupadu.satupaduerp.dao.CustomerDAO;
import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.dao.SalesOrderDAO;
import com.satupadu.satupaduerp.dao.UserDAO;
import com.satupadu.satupaduerp.model.CashTransaction;
import com.satupadu.satupaduerp.model.Customer;
import com.satupadu.satupaduerp.model.Product;
import com.satupadu.satupaduerp.model.SalesOrder;
import com.satupadu.satupaduerp.model.SalesOrderItem;
import com.satupadu.satupaduerp.model.StockMovement;
import com.satupadu.satupaduerp.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// alur bisnis sales order; setiap method berjalan dalam satu transaksi (declarative transaction):
// jika ada langkah yang gagal, semua perubahan ke database dibatalkan (rollback)
@Service
@Transactional
public class SalesOrderService {
    private final SalesOrderDAO salesOrderDAO;
    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;
    private final UserDAO userDAO;

    public SalesOrderService(SalesOrderDAO salesOrderDAO, ProductDAO productDAO,
                             CustomerDAO customerDAO, UserDAO userDAO) {
        this.salesOrderDAO = salesOrderDAO;
        this.productDAO = productDAO;
        this.customerDAO = customerDAO;
        this.userDAO = userDAO;
    }

    // barang: productId -> jumlah
    public String buatPesanan(int customerId, Map<Integer, Integer> barang, int userId, boolean langsungKonfirmasi) {
        if (barang.isEmpty()) {
            throw new IllegalArgumentException("Tambahkan minimal satu barang.");
        }
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Pelanggan tidak ditemukan.");
        }

        SalesOrder so = new SalesOrder();
        so.setSoNumber(salesOrderDAO.nomorBerikutnya(LocalDate.now()));
        so.setCustomer(customer);
        so.setUser(userDAO.getReference(userId));
        so.setOrderDate(LocalDate.now());
        so.setStatus("Draft");

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> b : barang.entrySet()) {
            Product product = productDAO.findById(b.getKey());
            SalesOrderItem item = new SalesOrderItem(so, product, b.getValue());
            so.getItems().add(item);
            total = total.add(item.getSubtotal());
        }
        so.setTotalAmount(total);
        salesOrderDAO.save(so);

        if (langsungKonfirmasi) {
            konfirmasi(so, userId);
        }
        return so.getSoNumber();
    }

    public void konfirmasi(int soId, int userId) {
        konfirmasi(salesOrderDAO.findById(soId), userId);
    }

    // stok berkurang, riwayat stok dicatat, dan pemasukan masuk ke keuangan
    private void konfirmasi(SalesOrder so, int userId) {
        if (!"Draft".equals(so.getStatus())) {
            throw new IllegalArgumentException("Hanya pesanan berstatus Draft yang bisa dikonfirmasi.");
        }
        for (SalesOrderItem item : so.getItems()) {
            Product p = item.getProduct();
            if (p.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Stok " + p.getProductName() + " tidak cukup (sisa "
                    + p.getStock() + ", dipesan " + item.getQuantity() + ").");
            }
            p.setStock(p.getStock() - item.getQuantity());
            salesOrderDAO.saveStockMovement(new StockMovement(p, "Keluar", item.getQuantity(), "SO", so.getSoId()));
        }
        so.setStatus("Dikonfirmasi");

        User user = userDAO.getReference(userId);
        salesOrderDAO.saveCashTransaction(new CashTransaction("Pemasukan", "Penjualan", so.getTotalAmount(),
            "Penjualan " + so.getSoNumber(), "SO", so.getSoId(), user));
    }

    public void batalkan(int soId) {
        SalesOrder so = salesOrderDAO.findById(soId);
        if (!"Draft".equals(so.getStatus())) {
            throw new IllegalArgumentException("Hanya pesanan berstatus Draft yang bisa dibatalkan.");
        }
        so.setStatus("Dibatalkan");
    }
}
