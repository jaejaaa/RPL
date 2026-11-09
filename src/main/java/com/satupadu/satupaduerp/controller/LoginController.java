package com.satupadu.satupaduerp.controller;

import com.satupadu.satupaduerp.Session;
import com.satupadu.satupaduerp.dao.UserDAO;
import com.satupadu.satupaduerp.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;

// penghubung antara FormLogin (view) dengan UserDAO (model)
@Component
public class LoginController {
    private final UserDAO userDAO;

    // dependency injection lewat constructor: objek DAO disediakan oleh Spring
    public LoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

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
        } catch (DataAccessException | TransactionException e) {
            throw new IllegalArgumentException("Gagal terhubung ke database.\n"
                + "Pastikan MySQL di XAMPP sudah berjalan.");
        }
    }

    public void logout() {
        Session.clear();
    }
}
