package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.Session;
import com.satupadu.satupaduerp.dao.UserDAO;
import com.satupadu.satupaduerp.model.User;
import java.sql.SQLException;

// penghubung antara FormLogin (view) dengan UserDAO (model)
public class LoginController {
    private final UserDAO userDAO = new UserDAO();

    // jika gagal, melempar IllegalArgumentException berisi pesan untuk ditampilkan view
    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username dan password wajib diisi.");
        }
        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                throw new IllegalArgumentException("Username atau password salah.");
            }
            Session.setUser(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Gagal terhubung ke database.\n"
                + "Pastikan MySQL di XAMPP sudah berjalan.");
        }
    }

    public void logout() {
        Session.clear();
    }
}
