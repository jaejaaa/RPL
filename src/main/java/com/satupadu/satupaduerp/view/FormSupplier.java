package com.satupadu.satupaduerp.view;

import com.satupadu.satupaduerp.SpringContext;
import com.satupadu.satupaduerp.controller.SupplierController;
import com.satupadu.satupaduerp.model.Supplier;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormSupplier extends JDialog {
    private final SupplierController controller = SpringContext.getBean(SupplierController.class);
    private List<Supplier> daftarSupplier = new ArrayList<>();
    private int selectedId = 0;   // 0 = mode tambah, selain itu = mode ubah

    private final JTextField txtNama = new JTextField();
    private final JTextField txtTelepon = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextArea txtAlamat = new JTextArea(4, 20);
    private final JTextField txtCari = new JTextField();

    private final JButton btnSimpan = Tema.tombol("Simpan", true);
    private final JButton btnHapus = Tema.tombol("Hapus", false);
    private final JButton btnBersih = Tema.tombol("Bersihkan", false);
    private final JButton btnCari = Tema.tombol("Cari", false);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Nama Supplier", "Telepon", "Email", "Alamat"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(model);

    public FormSupplier(JFrame owner) {
        super(owner, "SIMERP - Data Supplier", true);
        setSize(1060, 560);
        setLocationRelativeTo(owner);
        initComponents();
        bersihkan();
        loadData();
    }

    private void initComponents() {
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
        baris(form, g, "Nama Supplier", txtNama, 28);
        baris(form, g, "Telepon", txtTelepon, 28);
        baris(form, g, "Email", txtEmail, 28);

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setFont(Tema.font(Font.PLAIN, 13));
        baris(form, g, "Alamat", new JScrollPane(txtAlamat), 92);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnSimpan);
        tombol.add(btnHapus);
        tombol.add(btnBersih);
        g.insets = new Insets(10, -8, 0, 0);
        form.add(tombol, g);
        g.weighty = 1;
        form.add(new JLabel(), g);   // pendorong agar isi form rapat ke atas

        // ----- pencarian + tabel (kanan) -----
        JPanel cari = new JPanel(new BorderLayout(8, 0));
        cari.setOpaque(false);
        cari.setBorder(new EmptyBorder(0, 0, 10, 0));
        txtCari.setFont(Tema.font(Font.PLAIN, 13));
        txtCari.setToolTipText("Cari berdasarkan nama atau telepon supplier");
        cari.add(txtCari, BorderLayout.CENTER);
        cari.add(btnCari, BorderLayout.EAST);

        Tema.tabel(tabel);
        int[] lebar = {180, 110, 170, 220};
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

    private void baris(JPanel p, GridBagConstraints g, String label, JComponent input, int tinggi) {
        JLabel l = new JLabel(label);
        l.setFont(Tema.font(Font.PLAIN, 12));
        l.setForeground(Tema.TEKS_PUDAR);
        input.setFont(Tema.font(Font.PLAIN, 13));
        input.setPreferredSize(new Dimension(0, tinggi));
        g.insets = new Insets(0, 0, 2, 0);
        p.add(l, g);
        g.insets = new Insets(0, 0, 8, 0);
        p.add(input, g);
    }

    // ---------------- data ----------------
    private void loadData() {
        try {
            daftarSupplier = controller.cari(txtCari.getText());
            model.setRowCount(0);
            for (Supplier s : daftarSupplier) {
                model.addRow(new Object[]{
                    s.getSupplierName(), s.getPhone(), s.getEmail(), s.getAddress()
                });
            }
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void pilihBaris() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            return;
        }
        Supplier s = daftarSupplier.get(row);
        selectedId = s.getSupplierId();
        txtNama.setText(s.getSupplierName());
        txtTelepon.setText(s.getPhone());
        txtEmail.setText(s.getEmail());
        txtAlamat.setText(s.getAddress());
        btnSimpan.setText("Ubah");
        btnHapus.setEnabled(true);
    }

    private void bersihkan() {
        selectedId = 0;
        txtNama.setText("");
        txtTelepon.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        btnSimpan.setText("Simpan");
        btnHapus.setEnabled(false);
        tabel.clearSelection();
        txtNama.requestFocus();
    }

    private void simpan() {
        try {
            String pesan = controller.simpan(selectedId, txtNama.getText(), txtTelepon.getText(),
                txtEmail.getText(), txtAlamat.getText());
            info(pesan);
            bersihkan();
            loadData();
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        }
    }

    private void hapus() {
        if (selectedId == 0) {
            return;
        }
        int pilih = JOptionPane.showConfirmDialog(this,
            "Hapus supplier " + txtNama.getText() + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            info(controller.hapus(selectedId));
            bersihkan();
            loadData();
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
