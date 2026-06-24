package com.hotel.desktop;

import com.hotel.desktop.ui.MainFrame;
import org.h2.tools.Server;

import javax.swing.*;

public class HotelApp {
    public static void main(String[] args) {
        // Запуск веб-консоли H2 (только для разработки)
        try {
            Server webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
            webServer.start();
            System.out.println(" H2 Console запущена: http://localhost:8082");
            System.out.println("   (Подключение: JDBC URL: jdbc:h2:file:./hotel_data, User: sa, Password: )");
        } catch (Exception e) {
            System.err.println(" Не удалось запустить H2 Console: " + e.getMessage());
            // Ошибка не критична — продолжаем запуск приложения
        }

        // Запуск основного GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}