package com.satupadu.satupaduerp;

import com.satupadu.satupaduerp.model.User;

// menyimpan user yang sedang login selama aplikasi berjalan
public class Session {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }

    public static void clear() {
        user = null;
    }

    // Admin boleh mengakses semua menu
    public static boolean hasRole(String... roles) {
        if (user == null) {
            return false;
        }
        if (user.getRoleName().equals("Admin") || roles.length == 0) {
            return true;
        }
        for (String role : roles) {
            if (user.getRoleName().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
