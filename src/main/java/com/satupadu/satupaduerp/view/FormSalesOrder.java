package com.satupadu.satupaduerp.view;

import com.satupadu.satupaduerp.SpringContext;
import com.satupadu.satupaduerp.controller.SalesOrderController;
import com.satupadu.satupaduerp.model.Customer;
import com.satupadu.satupaduerp.model.Product;
import com.satupadu.satupaduerp.model.SalesOrder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormSalesOrder extends JDialog {
    private final SalesOrderController controller = SpringContext.getBean(SalesOrderController.class);
    private final Map<Integer, Integer> keranjang = new LinkedHashMap<>();   // productId -> qty pesanan baru
    private final Map<Integer, Product> produkKeranjang = new LinkedHashMap<>();
    private List<SalesOrder> daftarPesanan = new ArrayList<>();

    private final JComboBox<Customer> cmbPelanggan = new JComboBox<>();
    private final JComboBox<Product> cmbProduk = new JComboBox<>();
    private final JTextField txtQty = new JTextField("1");
    private final JLabel lblTotal = new JLabel();

    private final JButton btnTambah = Tema.tombol("Tambah Barang", false);
    private final JButton btnHapusBarang = Tema.tombol("Hapus Barang", false);
    private final JButton btnDraft = Tema.tombol("Simpan Draft", false);
    private final JButton btnSimpanKonfirmasi = Tema.tombol("Simpan & Konfirmasi", true);
    private final JButton btnKonfirmasi = Tema.tombol("Konfirmasi", true);
    private final JButton btnBatalkan = Tema.tombol("Batalkan", false);

    private final DefaultTableModel modelBarang = tabelModel("Kode", "Produk", "Qty", "Harga", "Subtotal");
    private final JTable tblBarang = new JTable(modelBarang);
    private final DefaultTableModel modelPesanan = tabelModel("No. SO", "Tanggal", "Pelanggan", "Total", "Status");
    private final JTable tblPesanan = new JTable(modelPesanan);

    public FormSalesOrder(JFrame owner) {
        super(owner, "SIMERP - Sales Order", true);
        setSize(1060, 660);
        setLocationRelativeTo(owner);
        initComponents();
        loadPilihan();
        loadPesanan();
        tampilkanKeranjang();
    }

    private static DefaultTableModel tabelModel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void initComponents() {
        // ----- pesanan baru (atas) -----
        cmbPelanggan.setRenderer(renderer(o -> ((Customer) o).getCustomerName()));
        cmbProduk.setRenderer(renderer(o -> {
            Product p = (Product) o;
            return p.getProductCode() + " - " + p.getProductName() + "  (stok " + p.getStock() + ", "
                + Tema.rupiah(p.getSellingPrice()) + ")";
        }));
        cmbPelanggan.setFont(Tema.font(Font.PLAIN, 13));
        cmbProduk.setFont(Tema.font(Font.PLAIN, 13));
        txtQty.setFont(Tema.font(Font.PLAIN, 13));
        cmbPelanggan.setPreferredSize(new Dimension(300, 28));
        cmbProduk.setPreferredSize(new Dimension(420, 28));
        txtQty.setPreferredSize(new Dimension(60, 28));

        JPanel baris1 = baris();
        baris1.add(label("Pelanggan"));
        baris1.add(cmbPelanggan);

        JPanel baris2 = baris();
        baris2.add(label("Produk"));
        baris2.add(cmbProduk);
        baris2.add(label("Qty"));
        baris2.add(txtQty);
        baris2.add(btnTambah);

        JPanel input = new JPanel(new BorderLayout(0, 6));
        input.setOpaque(false);
        input.add(baris1, BorderLayout.NORTH);
        input.add(baris2, BorderLayout.SOUTH);

        Tema.tabel(tblBarang);
        lebarKolom(tblBarang, 90, 300, 60, 120, 130);
        JScrollPane scrollBarang = new JScrollPane(tblBarang);
        scrollBarang.setPreferredSize(new Dimension(0, 130));
        scrollBarang.setBorder(BorderFactory.createLineBorder(Tema.GARIS));

        lblTotal.setFont(Tema.font(Font.BOLD, 16));
        lblTotal.setForeground(Tema.MAROON);
        JPanel aksiBaru = new JPanel(new BorderLayout());
        aksiBaru.setOpaque(false);
        JPanel kiri = baris();
        kiri.add(btnHapusBarang);
        kiri.add(lblTotal);
        JPanel kanan = baris();
        kanan.add(btnDraft);
        kanan.add(btnSimpanKonfirmasi);
        aksiBaru.add(kiri, BorderLayout.WEST);
        aksiBaru.add(kanan, BorderLayout.EAST);

        JPanel baru = kotakPutih();
        baru.add(input, BorderLayout.NORTH);
        baru.add(scrollBarang, BorderLayout.CENTER);
        baru.add(aksiBaru, BorderLayout.SOUTH);

        // ----- daftar sales order (bawah) -----
        Tema.tabel(tblPesanan);
        lebarKolom(tblPesanan, 140, 100, 300, 140, 120);
        tblPesanan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPesanan.getSelectionModel().addListSelectionListener(e -> aturTombolPesanan());
        JScrollPane scrollPesanan = new JScrollPane(tblPesanan);
        scrollPesanan.setBorder(BorderFactory.createLineBorder(Tema.GARIS));

        JPanel aksiDaftar = baris();
        aksiDaftar.add(btnKonfirmasi);
        aksiDaftar.add(btnBatalkan);
        JLabel info = new JLabel("Hanya pesanan berstatus Draft yang bisa dikonfirmasi atau dibatalkan.");
        info.setFont(Tema.font(Font.PLAIN, 12));
        info.setForeground(Tema.TEKS_PUDAR);
        aksiDaftar.add(info);

        JPanel daftar = kotakPutih();
        daftar.add(scrollPesanan, BorderLayout.CENTER);
        daftar.add(aksiDaftar, BorderLayout.SOUTH);

        JPanel isi = new JPanel(new BorderLayout(0, 14));
        isi.setBackground(Tema.LATAR);
        isi.setBorder(new EmptyBorder(16, 16, 16, 16));
        isi.add(berjudul("Pesanan baru", baru), BorderLayout.NORTH);
        isi.add(berjudul("Daftar sales order", daftar), BorderLayout.CENTER);
        add(isi);

        btnTambah.addActionListener(e -> tambahBarang());
        txtQty.addActionListener(e -> tambahBarang());   // Enter di kolom qty
        btnHapusBarang.addActionListener(e -> hapusBarang());
        btnDraft.addActionListener(e -> simpan(false));
        btnSimpanKonfirmasi.addActionListener(e -> simpan(true));
        btnKonfirmasi.addActionListener(e -> ubahStatus(true));
        btnBatalkan.addActionListener(e -> ubahStatus(false));
    }

    private interface Teks {
        String dari(Object o);
    }

    private static DefaultListCellRenderer renderer(Teks teks) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : teks.dari(value));
                return this;
            }
        };
    }

    private static JPanel baris() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        return p;
    }

    private static JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(Tema.font(Font.PLAIN, 13));
        l.setForeground(Tema.TEKS_PUDAR);
        return l;
    }

    private static JPanel kotakPutih() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.GARIS), new EmptyBorder(12, 14, 12, 14)));
        return p;
    }

    private static JPanel berjudul(String judul, JPanel isi) {
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(Tema.font(Font.BOLD, 14));
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.add(lblJudul, BorderLayout.NORTH);
        p.add(isi, BorderLayout.CENTER);
        return p;
    }

    private static void lebarKolom(JTable t, int... lebar) {
        for (int i = 0; i < lebar.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    // ---------------- data ----------------
    private void loadPilihan() {
        try {
            cmbPelanggan.removeAllItems();
            for (Customer c : controller.daftarPelanggan()) {
                cmbPelanggan.addItem(c);
            }
            cmbProduk.removeAllItems();
            for (Product p : controller.daftarProduk()) {
                cmbProduk.addItem(p);
            }
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void loadPesanan() {
        try {
            daftarPesanan = controller.daftarPesanan();
            modelPesanan.setRowCount(0);
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (SalesOrder so : daftarPesanan) {
                modelPesanan.addRow(new Object[]{
                    so.getSoNumber(), so.getOrderDate().format(f), so.getCustomer().getCustomerName(),
                    Tema.rupiah(so.getTotalAmount()), so.getStatus()
                });
            }
            aturTombolPesanan();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void tampilkanKeranjang() {
        modelBarang.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> b : keranjang.entrySet()) {
            Product p = produkKeranjang.get(b.getKey());
            BigDecimal subtotal = p.getSellingPrice().multiply(BigDecimal.valueOf(b.getValue()));
            total = total.add(subtotal);
            modelBarang.addRow(new Object[]{
                p.getProductCode(), p.getProductName(), b.getValue(),
                Tema.rupiah(p.getSellingPrice()), Tema.rupiah(subtotal)
            });
        }
        lblTotal.setText("   Total: " + Tema.rupiah(total));
    }

    private void tambahBarang() {
        Product p = (Product) cmbProduk.getSelectedItem();
        if (p == null) {
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
        } catch (NumberFormatException e) {
            error("Qty harus berupa angka.");
            return;
        }
        if (qty <= 0) {
            error("Qty harus lebih dari 0.");
            return;
        }
        keranjang.merge(p.getProductId(), qty, Integer::sum);   // produk yang sama dijumlahkan
        produkKeranjang.put(p.getProductId(), p);
        tampilkanKeranjang();
        txtQty.setText("1");
    }

    private void hapusBarang() {
        int row = tblBarang.getSelectedRow();
        if (row < 0) {
            error("Pilih barang yang akan dihapus.");
            return;
        }
        Integer productId = new ArrayList<>(keranjang.keySet()).get(row);
        keranjang.remove(productId);
        produkKeranjang.remove(productId);
        tampilkanKeranjang();
    }

    private void simpan(boolean konfirmasi) {
        try {
            info(controller.simpan((Customer) cmbPelanggan.getSelectedItem(), keranjang, konfirmasi));
            keranjang.clear();
            produkKeranjang.clear();
            tampilkanKeranjang();
            loadPilihan();   // stok di pilihan produk ikut diperbarui
            loadPesanan();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private SalesOrder pesananTerpilih() {
        int row = tblPesanan.getSelectedRow();
        return row < 0 ? null : daftarPesanan.get(row);
    }

    private void aturTombolPesanan() {
        SalesOrder so = pesananTerpilih();
        boolean draft = so != null && "Draft".equals(so.getStatus());
        btnKonfirmasi.setEnabled(draft);
        btnBatalkan.setEnabled(draft);
    }

    private void ubahStatus(boolean konfirmasi) {
        SalesOrder so = pesananTerpilih();
        if (so == null) {
            return;
        }
        String aksi = konfirmasi ? "Konfirmasi" : "Batalkan";
        int pilih = JOptionPane.showConfirmDialog(this, aksi + " sales order " + so.getSoNumber() + "?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            info(konfirmasi ? controller.konfirmasi(so.getSoId()) : controller.batalkan(so.getSoId()));
            loadPilihan();
            loadPesanan();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void info(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
