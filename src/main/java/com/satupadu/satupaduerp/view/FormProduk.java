package com.satupadu.satupaduerp.view;

import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.model.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormProduk extends JDialog {
    private final ProductDAO productDAO = new ProductDAO();
    private List<Product> daftarProduk = new ArrayList<>();
    private int selectedId = 0;   // 0 = mode tambah, selain itu = mode ubah

    private final JTextField txtKode = new JTextField();
    private final JTextField txtNama = new JTextField();
    private final JComboBox<String> cmbSatuan =
        new JComboBox<>(new String[]{"pcs", "lusin", "kodi", "box", "kg", "meter"});
    private final JTextField txtHargaBeli = new JTextField();
    private final JTextField txtHargaJual = new JTextField();
    private final JTextField txtStok = new JTextField();
    private final JTextField txtStokMin = new JTextField();
    private final JTextField txtCari = new JTextField();

    private final JButton btnSimpan = Tema.tombol("Simpan", true);
    private final JButton btnHapus = Tema.tombol("Hapus", false);
    private final JButton btnBersih = Tema.tombol("Bersihkan", false);
    private final JButton btnCari = Tema.tombol("Cari", false);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode", "Nama Produk", "Satuan", "Harga Beli", "Harga Jual", "Stok", "Min"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);

    public FormProduk(JFrame owner) {
        super(owner, "SIMERP - Data Produk", true);
        setSize(1060, 560);
        setLocationRelativeTo(owner);
        initComponents();
        bersihkan();
        loadData();
    }

    private void initComponents() {
        cmbSatuan.setEditable(true);

        // ----- form input (kiri) -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.GARIS), new EmptyBorder(16, 16, 16, 16)));
        form.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        baris(form, g, "Kode Produk", txtKode);
        baris(form, g, "Nama Produk", txtNama);
        baris(form, g, "Satuan", cmbSatuan);
        baris(form, g, "Harga Beli (Rp)", txtHargaBeli);
        baris(form, g, "Harga Jual (Rp)", txtHargaJual);
        baris(form, g, "Stok Awal", txtStok);
        baris(form, g, "Stok Minimum", txtStokMin);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tombol.setOpaque(false);
        tombol.add(btnSimpan);
        tombol.add(jarak(8));
        tombol.add(btnHapus);
        tombol.add(jarak(8));
        tombol.add(btnBersih);
        g.insets = new Insets(10, 0, 0, 0);
        form.add(tombol, g);
        g.weighty = 1;
        form.add(new JLabel(), g);   // pendorong agar isi form rapat ke atas

        // ----- pencarian + tabel (kanan) -----
        JPanel cari = new JPanel(new BorderLayout(8, 0));
        cari.setOpaque(false);
        cari.setBorder(new EmptyBorder(0, 0, 10, 0));
        txtCari.setFont(Tema.font(Font.PLAIN, 13));
        txtCari.setToolTipText("Cari berdasarkan kode atau nama produk");
        cari.add(txtCari, BorderLayout.CENTER);
        cari.add(btnCari, BorderLayout.EAST);

        Tema.tabel(tabel);
        int[] lebar = {80, 220, 60, 100, 100, 50, 50};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                pilihBaris();
            }
        });
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.GARIS));

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setOpaque(false);
        kanan.add(cari, BorderLayout.NORTH);
        kanan.add(scroll, BorderLayout.CENTER);

        JPanel isi = new JPanel(new BorderLayout(16, 0));
        isi.setBackground(Tema.LATAR);
        isi.setBorder(new EmptyBorder(16, 16, 16, 16));
        isi.add(form, BorderLayout.WEST);
        isi.add(kanan, BorderLayout.CENTER);
        add(isi);

        btnSimpan.addActionListener(e -> simpan());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());
        btnCari.addActionListener(e -> loadData());
        txtCari.addActionListener(e -> loadData());   // Enter di kolom cari
    }

    private static JComponent jarak(int lebar) {
        JLabel jarak = new JLabel();
        jarak.setPreferredSize(new Dimension(lebar, 1));
        return jarak;
    }

    private void baris(JPanel p, GridBagConstraints g, String label, JComponent input) {
        JLabel l = new JLabel(label);
        l.setFont(Tema.font(Font.PLAIN, 12));
        l.setForeground(Tema.TEKS_PUDAR);
        input.setFont(Tema.font(Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(0, 28));
        g.insets = new Insets(0, 0, 2, 0);
        p.add(l, g);
        g.insets = new Insets(0, 0, 8, 0);
        p.add(input, g);
    }

    // ---------------- data ----------------
    private void loadData() {
        try {
            daftarProduk = productDAO.getAll(txtCari.getText().trim());
            model.setRowCount(0);
            for (Product p : daftarProduk) {
                model.addRow(new Object[]{
                    p.getProductCode(), p.getProductName(), p.getUnit(),
                    Tema.rupiah(p.getPurchasePrice()), Tema.rupiah(p.getSellingPrice()),
                    p.getStock(), p.getMinimumStock()
                });
            }
        } catch (SQLException e) {
            error("Gagal memuat data produk: " + e.getMessage());
        }
    }

    private void pilihBaris() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            return;
        }
        Product p = daftarProduk.get(row);
        selectedId = p.getProductId();
        txtKode.setText(p.getProductCode());
        txtNama.setText(p.getProductName());
        cmbSatuan.setSelectedItem(p.getUnit());
        txtHargaBeli.setText(p.getPurchasePrice().toBigInteger().toString());
        txtHargaJual.setText(p.getSellingPrice().toBigInteger().toString());
        txtStok.setText(String.valueOf(p.getStock()));
        txtStok.setEnabled(false);   // stok hanya berubah lewat transaksi
        txtStokMin.setText(String.valueOf(p.getMinimumStock()));
        btnSimpan.setText("Ubah");
        btnHapus.setEnabled(true);
    }

    private void bersihkan() {
        selectedId = 0;
        txtKode.setText("");
        txtNama.setText("");
        cmbSatuan.setSelectedIndex(0);
        txtHargaBeli.setText("");
        txtHargaJual.setText("");
        txtStok.setText("0");
        txtStok.setEnabled(true);
        txtStokMin.setText("0");
        btnSimpan.setText("Simpan");
        btnHapus.setEnabled(false);
        tabel.clearSelection();
        txtKode.requestFocus();
    }

    private void simpan() {
        Product p = ambilInput();
        if (p == null) {
            return;
        }
        try {
            if (selectedId == 0) {
                productDAO.insert(p);
                info("Produk berhasil ditambahkan.");
            } else {
                p.setProductId(selectedId);
                productDAO.update(p);
                info("Produk berhasil diubah.");
            }
            bersihkan();
            loadData();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {   // duplicate entry
                error("Kode produk " + p.getProductCode() + " sudah dipakai.");
            } else {
                error("Gagal menyimpan: " + e.getMessage());
            }
        }
    }

    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        int pilih = JOptionPane.showConfirmDialog(this,
            "Hapus produk " + txtNama.getText() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            productDAO.delete(selectedId);
            info("Produk berhasil dihapus.");
            bersihkan();
            loadData();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {   // masih dipakai tabel lain (foreign key)
                error("Produk tidak bisa dihapus karena sudah dipakai di transaksi.");
            } else {
                error("Gagal menghapus: " + e.getMessage());
            }
        }
    }

    // validasi input, mengembalikan null jika ada yang salah
    private Product ambilInput() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        Object satuan = cmbSatuan.getSelectedItem();

        if (kode.isEmpty() || nama.isEmpty() || satuan == null || satuan.toString().isBlank()) {
            error("Kode, nama, dan satuan wajib diisi.");
            return null;
        }
        try {
            Product p = new Product();
            p.setProductCode(kode);
            p.setProductName(nama);
            p.setUnit(satuan.toString().trim());
            p.setPurchasePrice(new BigDecimal(txtHargaBeli.getText().trim()));
            p.setSellingPrice(new BigDecimal(txtHargaJual.getText().trim()));
            p.setStock(Integer.parseInt(txtStok.getText().trim()));
            p.setMinimumStock(Integer.parseInt(txtStokMin.getText().trim()));

            if (p.getPurchasePrice().signum() < 0 || p.getSellingPrice().signum() < 0
                    || p.getStock() < 0 || p.getMinimumStock() < 0) {
                error("Harga dan stok tidak boleh negatif.");
                return null;
            }
            return p;
        } catch (NumberFormatException e) {
            error("Harga dan stok harus berupa angka (tanpa titik/koma).");
            return null;
        }
    }

    private void info(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
