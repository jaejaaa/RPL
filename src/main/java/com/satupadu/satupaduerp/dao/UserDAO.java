package com.satupadu.satupaduerp.dao;

import com.satupadu.satupaduerp.model.User;

public interface UserDAO {
    // mengembalikan objek User jika username & password cocok, null jika tidak
    User login(String username, String password);

    User getReference(int userId);
}
