package com.satupadu.satupaduerp.controller;

import java.sql.SQLException;

// mencari kode error MySQL di dalam DataAccessException dari Spring
class KodeError {
    static final int DATA_KEMBAR = 1062;        // nilai UNIQUE sudah dipakai
    static final int MASIH_DIPAKAI = 1451;      // masih dirujuk tabel lain (foreign key)

    static int dari(Throwable e) {
        while (e != null) {
            if (e instanceof SQLException sql) {
                return sql.getErrorCode();
            }
            e = e.getCause();
        }
        return 0;
    }
}
