/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.satupadu.satupaduerp;

import com.satupadu.satupaduerp.view.FormLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Lenovo
 */
public class SatuPaduERP {

    public static void main(String[] args) {
        try {
            // tampilan mengikuti Windows
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // kalau gagal, pakai tampilan bawaan Java
        }
        SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
    }
}
