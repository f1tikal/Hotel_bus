package com.hotel.desktop;

import com.hotel.desktop.ui.MainFrame;

import javax.swing.*;

public class HotelApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
