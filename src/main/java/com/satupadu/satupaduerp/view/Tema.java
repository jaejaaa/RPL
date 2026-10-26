package com.satupadu.satupaduerp.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;

// warna, font, dan format yang dipakai bersama oleh semua form
public class Tema {
    public static final Color MAROON = new Color(0x620000);
    public static final Color MAROON_MUDA = new Color(0xF3E6E6);
    public static final Color LATAR = new Color(0xF7F5F5);
    public static final Color GARIS = new Color(0xDDD5D5);
    public static final Color TEKS_PUDAR = new Color(0x6B6B6B);

    private static final DecimalFormat RUPIAH =
        new DecimalFormat("Rp #,##0", DecimalFormatSymbols.getInstance(Locale.of("id", "ID")));

    public static Font font(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    public static String rupiah(BigDecimal nilai) {
        return RUPIAH.format(nilai == null ? BigDecimal.ZERO : nilai);
    }

    public static JButton tombol(String teks, boolean utama) {
        JButton b = new JButton(teks);
        b.setFont(font(Font.PLAIN, 13));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (utama) {
            b.setBackground(MAROON);
            b.setForeground(Color.WHITE);
            b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(MAROON);
            b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MAROON),
                BorderFactory.createEmptyBorder(6, 15, 6, 15)));
        }
        return b;
    }

    public static void tabel(JTable t) {
        t.setFont(font(Font.PLAIN, 13));
        t.setRowHeight(26);
        t.setGridColor(GARIS);
        t.setSelectionBackground(MAROON_MUDA);
        t.setSelectionForeground(Color.BLACK);
        t.getTableHeader().setFont(font(Font.BOLD, 13));
        t.getTableHeader().setReorderingAllowed(false);
    }
}
