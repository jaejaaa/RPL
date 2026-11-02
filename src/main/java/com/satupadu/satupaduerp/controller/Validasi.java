package com.satupadu.satupaduerp.controller;

// aturan isian yang sama untuk data pelanggan dan supplier
class Validasi {

    static void mitra(String labelNama, String nama, String telepon, String email) {
        if (nama.isBlank()) {
            throw new IllegalArgumentException(labelNama + " wajib diisi.");
        }
        if (nama.trim().length() > 100) {
            throw new IllegalArgumentException(labelNama + " maksimal 100 karakter.");
        }
        if (!telepon.trim().matches("[0-9+() -]{0,20}")) {
            throw new IllegalArgumentException("Nomor telepon hanya boleh angka, maksimal 20 karakter.");
        }
        String mail = email.trim();
        if (mail.length() > 100 || (!mail.isEmpty() && !mail.matches("[^@ ]+@[^@ ]+"))) {
            throw new IllegalArgumentException("Format email tidak valid.");
        }
    }
}
