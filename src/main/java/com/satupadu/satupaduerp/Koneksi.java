package com.satupadu.satupaduerp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String URL  = "jdbc:mysql://localhost:3306/simerp";
    private static final String USER = "root";
    private static final String PASS = ""; // default XAMPP kosong

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}