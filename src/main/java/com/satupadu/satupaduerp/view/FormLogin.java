package com.satupadu.satupaduerp.view;

import com.satupadu.satupaduerp.controller.LoginController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class FormLogin extends JFrame {
    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JButton btnLogin = Tema.tombol("Login", true);
    private final LoginController controller = new LoginController();

    public FormLogin() {
        setTitle("SIMERP - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(Tema.MAROON);
        header.setBorder(new EmptyBorder(22, 28, 20, 28));

        JLabel lblJudul = new JLabel("SIMERP");
        lblJudul.setFont(Tema.font(Font.BOLD, 26));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Sistem Informasi Manajemen ERP - PT Aljera");
        lblSub.setFont(Tema.font(Font.PLAIN, 13));
        lblSub.setForeground(new Color(0xE9D3D3));
        header.add(lblJudul, BorderLayout.NORTH);
        header.add(lblSub, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 28, 26, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Username"), g);
        g.insets = new Insets(0, 0, 14, 0);
        form.add(input(txtUsername), g);
        g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Password"), g);
        g.insets = new Insets(0, 0, 20, 0);
        form.add(input(txtPassword), g);
        g.insets = new Insets(0, 0, 0, 0);
        form.add(btnLogin, g);

        btnLogin.addActionListener(e -> login());
        getRootPane().setDefaultButton(btnLogin);   // tekan Enter = klik Login

        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
    }

    private JLabel label(String teks) {
        JLabel l = new JLabel(teks);
        l.setFont(Tema.font(Font.PLAIN, 13));
        l.setForeground(Tema.TEKS_PUDAR);
        return l;
    }

    private JTextField input(JTextField t) {
        t.setFont(Tema.font(Font.PLAIN, 14));
        t.setPreferredSize(new Dimension(320, 32));
        return t;
    }

    private void login() {
        try {
            controller.login(txtUsername.getText().trim(), new String(txtPassword.getPassword()));
            new MenuUtama().setVisible(true);
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Login Gagal", JOptionPane.WARNING_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
