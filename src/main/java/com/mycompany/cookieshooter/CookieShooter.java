package com.mycompany.cookieshooter;

import MyApp.Frontend;
import javax.swing.SwingUtilities;

public class CookieShooter {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frontend gui = new Frontend();
            gui.setVisible(true);
        });
    }
}