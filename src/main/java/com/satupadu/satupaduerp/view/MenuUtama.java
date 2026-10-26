package com.satupadu.satupaduerp.view;

import com.satupadu.satupaduerp.Session;
import com.satupadu.satupaduerp.dao.DashboardDAO;
import com.satupadu.satupaduerp.dao.ProductDAO;
import com.satupadu.satupaduerp.model.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MenuUtama extends JFrame {
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private final JLabel lblPenjualan = nilai();
    private final JLabel lblKas = nilai();
    private final JLabel lblProduk = nilai();
    private final JLabel lblMitra = nilai();

    private final DefaultTableModel modelMenipis = new DefaultTableModel(
            new Object[]{"Kode", "Nama Produk", "Stok", "Stok Minimum"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public MenuUtama() {
        setTitle("SIMERP - Menu Utama");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setJMenuBar(buatMenu());
        initComponents();
        loadDashboard();
    }

    // ---------------- menu sesuai struktur navigasi & hak akses ----------------
    private JMenuBar buatMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu master = new JMenu("Master Data");
        master.add(item("Data Produk", e -> bukaProduk(), "Purchasing"));
        master.add(item("Data Pelanggan", e -> belumTersedia(), "Sales"));
        master.add(item("Data Supplier", e -> belumTersedia(), "Purchasing"));

        JMenu penjualan = new JMenu("Penjualan");
        penjualan.add(item("Sales Order", e -> belumTersedia(), "Sales"));

        JMenu pembelian = new JMenu("Pembelian");
        pembelian.add(item("Purchase Order", e -> belumTersedia(), "Purchasing"));

        JMenu stok = new JMenu("Stok");
        stok.add(item("Data Stok", e -> belumTersedia(), "Sales", "Purchasing", "Manajer"));

        JMenu keuangan = new JMenu("Keuangan");
        keuangan.add(item("Transaksi Keuangan", e -> belumTersedia(), "Keuangan"));
        keuangan.add(item("Laporan Keuangan", e -> belumTersedia(), "Keuangan", "Manajer"));

        JMenu pengguna = new JMenu("Pengguna");
        pengguna.add(item("Data Pengguna", e -> belumTersedia()));
        pengguna.setEnabled(Session.hasRole("Admin"));

        JMenu akun = new JMenu("Akun");
        akun.add(item("Refresh Dashboard", e -> loadDashboard(), "Sales", "Purchasing", "Keuangan", "Manajer"));
        akun.addSeparator();
        akun.add(item("Logout", e -> logout(), "Sales", "Purchasing", "Keuangan", "Manajer"));

        bar.add(master);
        bar.add(penjualan);
        bar.add(pembelian);
        bar.add(stok);
        bar.add(keuangan);
        bar.add(pengguna);
        bar.add(akun);
        return bar;
    }

    // menu hanya aktif untuk role yang disebutkan (Admin selalu aktif)
    private JMenuItem item(String teks, ActionListener aksi, String... roles) {
        JMenuItem m = new JMenuItem(teks);
        m.addActionListener(aksi);
        m.setEnabled(Session.hasRole(roles.length == 0 ? new String[]{"Admin"} : roles));
        return m;
    }

    // ---------------- tampilan dashboard ----------------
    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Tema.MAROON);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel lblJudul = new JLabel("Dashboard");
        lblJudul.setFont(Tema.font(Font.BOLD, 20));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Login sebagai: " + Session.getUser());
        lblUser.setFont(Tema.font(Font.PLAIN, 13));
        lblUser.setForeground(new Color(0xE9D3D3));
        header.add(lblJudul, BorderLayout.WEST);
        header.add(lblUser, BorderLayout.EAST);

        JPanel ringkasan = new JPanel(new GridLayout(1, 4, 12, 0));
        ringkasan.setOpaque(false);
        ringkasan.add(kotak("Penjualan bulan ini", lblPenjualan));
        ringkasan.add(kotak("Saldo kas", lblKas));
        ringkasan.add(kotak("Jumlah produk", lblProduk));
        ringkasan.add(kotak("Pelanggan / Supplier", lblMitra));

        JTable tblMenipis = new JTable(modelMenipis);
        Tema.tabel(tblMenipis);
        JScrollPane scroll = new JScrollPane(tblMenipis);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.GARIS));

        JLabel lblMenipis = new JLabel("Produk dengan stok menipis");
        lblMenipis.setFont(Tema.font(Font.BOLD, 14));
        lblMenipis.setBorder(new EmptyBorder(20, 0, 8, 0));

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.add(lblMenipis, BorderLayout.NORTH);
        bawah.add(scroll, BorderLayout.CENTER);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setBackground(Tema.LATAR);
        isi.setBorder(new EmptyBorder(20, 24, 24, 24));
        isi.add(ringkasan, BorderLayout.NORTH);
        isi.add(bawah, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(isi, BorderLayout.CENTER);
    }

    private JPanel kotak(String judul, JLabel nilai) {
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(Tema.font(Font.PLAIN, 13));
        lblJudul.setForeground(Tema.TEKS_PUDAR);

        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Tema.GARIS),
            new EmptyBorder(12, 14, 12, 14)));
        p.add(lblJudul, BorderLayout.NORTH);
        p.add(nilai, BorderLayout.CENTER);
        return p;
    }

    private static JLabel nilai() {
        JLabel l = new JLabel("-");
        l.setFont(Tema.font(Font.BOLD, 18));
        l.setForeground(Tema.MAROON);
        return l;
    }

    private void loadDashboard() {
        try {
            lblPenjualan.setText(Tema.rupiah(dashboardDAO.penjualanBulanIni()));
            lblKas.setText(Tema.rupiah(dashboardDAO.saldoKas()));
            lblProduk.setText(String.valueOf(dashboardDAO.jumlahProduk()));
            lblMitra.setText(dashboardDAO.jumlahPelanggan() + " / " + dashboardDAO.jumlahSupplier());

            modelMenipis.setRowCount(0);
            for (Product p : productDAO.getStokMenipis()) {
                modelMenipis.addRow(new Object[]{
                    p.getProductCode(), p.getProductName(), p.getStock(), p.getMinimumStock()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat dashboard: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- aksi menu ----------------
    private void bukaProduk() {
        new FormProduk(this).setVisible(true);   // modal: baris ini lanjut setelah form ditutup
        loadDashboard();
    }

    private void belumTersedia() {
        JOptionPane.showMessageDialog(this, "Fitur ini akan dibuat pada pertemuan berikutnya.",
            "Belum Tersedia", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int pilih = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Logout",
            JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            Session.clear();
            dispose();
            new FormLogin().setVisible(true);
        }
    }
}
