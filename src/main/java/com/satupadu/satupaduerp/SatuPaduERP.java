/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.satupadu.satupaduerp;

import com.satupadu.satupaduerp.dao.UserDAO;
import com.satupadu.satupaduerp.model.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Lenovo
 */
public class SatuPaduERP {

    public static void main(String[] args) {
        System.out.println("=== SIMERP - Tes Koneksi Database ===");

        // 1. tes koneksi dan hitung jumlah tabel
        try (Connection c = Koneksi.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'simerp'")) {
            System.out.println("Koneksi ke database berhasil!");
            System.out.println("Database     : " + c.getCatalog());
            rs.next();
            System.out.println("Jumlah tabel : " + rs.getInt(1));
        } catch (SQLException e) {
            System.out.println("Gagal: " + e.getMessage());
            return;
        }

        // 2. tes login memakai class User dan UserDAO
        UserDAO userDAO = new UserDAO();
        cekLogin(userDAO, "admin", "admin123");
        cekLogin(userDAO, "admin", "passwordsalah");
    }

    private static void cekLogin(UserDAO userDAO, String username, String password) {
        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                System.out.println("Login '" + username + "' berhasil -> " + user);
            } else {
                System.out.println("Login '" + username + "' gagal -> username atau password salah");
            }
        } catch (SQLException e) {
            System.out.println("Gagal: " + e.getMessage());
        }
    }
}
