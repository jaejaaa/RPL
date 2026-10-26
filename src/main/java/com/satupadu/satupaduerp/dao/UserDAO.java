package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.Koneksi;
import com.satupadu.satupaduerp.PasswordUtil;
import com.satupadu.satupaduerp.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // mengembalikan objek User jika username & password cocok, null jika tidak
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.full_name, r.role_name "
                   + "FROM users u JOIN roles r ON u.role_id = r.role_id "
                   + "WHERE u.username = ? AND u.password = ? AND u.is_active = 1";

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role_name")
                    );
                }
            }
        }
        return null;
    }
}
