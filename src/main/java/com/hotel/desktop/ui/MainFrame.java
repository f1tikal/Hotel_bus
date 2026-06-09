package com.hotel.desktop.ui;

import com.hotel.desktop.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Color BG = new Color(0xF0, 0xF0, 0xF0);
    private static final Color SIDEBAR = new Color(0xE8, 0xE8, 0xE8);
    private static final Color HEADER_BG = new Color(0xE0, 0xE0, 0xE0);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x33, 0x33, 0x33);
    private static final Color TEXT_SEC = new Color(0x88, 0x88, 0x88);
    private static final Color BORDER = new Color(0xD0, 0xD0, 0xD0);

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel dashboardPanel;
    private User currentUser;

    public MainFrame() {
        setTitle("Отель - Бронирование номеров");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 620);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(520, 480));

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "login");
    }

    public void showRegister() {
        cardLayout.show(mainPanel, "register");
    }

    public void showDashboard(User user) {
        this.currentUser = user;
        if (dashboardPanel != null) {
            mainPanel.remove(dashboardPanel);
        }
        dashboardPanel = user.isStaff() ? createStaffDashboard(user) : createUserDashboard(user);
        mainPanel.add(dashboardPanel, "dashboard");
        cardLayout.show(mainPanel, "dashboard");
    }

    private JPanel createUserDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Личный кабинет"), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;

        JLabel title = new JLabel("Информация о пользователе");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT);
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(0, 0, 16, 0);
        content.add(title, c);

        c.gridwidth = 1;
        c.insets = new Insets(4, 0, 4, 0);
        int row = 1;
        row = addInfoRow(content, c, row, "Логин:", user.getLogin());
        row = addInfoRow(content, c, row, "Email:", user.getEmail());
        row = addInfoRow(content, c, row, "Телефон:", user.getPhone());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btnRow.setBackground(CARD_BG);
        JButton logoutBtn = grayButton("Выйти");
        logoutBtn.addActionListener(e -> { dashboardPanel = null; showLogin(); });
        btnRow.add(logoutBtn);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        c.insets = new Insets(20, 0, 0, 0);
        content.add(btnRow, c);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(30, 40, 30, 40));
        wrap.add(content, BorderLayout.CENTER);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private int addInfoRow(JPanel panel, GridBagConstraints c, int row, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(TEXT_SEC);
        c.gridx = 0; c.gridy = row;
        panel.add(lbl, c);

        JLabel val = new JLabel(value != null ? value : "—");
        val.setFont(new Font("Arial", Font.BOLD, 14));
        val.setForeground(TEXT);
        c.gridx = 1; c.gridy = row;
        panel.add(val, c);
        return row + 1;
    }

    private JPanel createStaffDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Панель управления"), BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));

        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.gridx = 0;

        String roleDisplay = switch (user.getRole()) {
            case User.ROLE_ADMIN -> "Администратор";
            case User.ROLE_MANAGER -> "Менеджер";
            default -> "Сотрудник";
        };

        JLabel roleLabel = new JLabel(roleDisplay, SwingConstants.RIGHT);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(TEXT_SEC);
        roleLabel.setBorder(new EmptyBorder(12, 16, 4, 16));
        sc.gridy = 0;
        sc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(roleLabel, sc);

        String[][] items = user.isAdmin()
            ? new String[][]{
                {"bookings", "Управление бронями"},
                {"rooms", "Управление номерами"},
                {"reviews", "Просмотр отзывов"},
                {"payments", "Просмотр платежей"},
                {"users", "Пользователи"}
              }
            : new String[][]{
                {"bookings", "Управление бронями"},
                {"rooms", "Управление номерами"},
                {"reviews", "Просмотр отзывов"},
                {"payments", "Просмотр платежей"}
              };

        CardLayout contentCards = new CardLayout();
        JPanel contentArea = new JPanel(contentCards);
        contentArea.setBackground(CARD_BG);

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(CARD_BG);
        welcomePanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        JLabel welcome = new JLabel(user.getFullName());
        welcome.setFont(new Font("Arial", Font.BOLD, 20));
        welcome.setForeground(TEXT);
        welcomePanel.add(welcome, BorderLayout.NORTH);
        JLabel hint = new JLabel("Выберите раздел в меню справа");
        hint.setFont(new Font("Arial", Font.PLAIN, 13));
        hint.setForeground(TEXT_SEC);
        hint.setBorder(new EmptyBorder(8, 0, 0, 0));
        welcomePanel.add(hint, BorderLayout.CENTER);
        contentArea.add(welcomePanel, "welcome");

        BookingManagementPanel bookingPanel = new BookingManagementPanel(this, user);
        contentArea.add(bookingPanel, "bookings");

        contentCards.show(contentArea, "welcome");

        for (int i = 0; i < items.length; i++) {
            String key = items[i][0];
            String label = items[i][1];
            JButton btn = sidebarButton(label);
            btn.addActionListener(e -> {
                if ("bookings".equals(key)) {
                    bookingPanel.loadData();
                    contentCards.show(contentArea, "bookings");
                } else {
                    showNotification("Раздел \"" + label + "\" будет доступен в следующей версии.");
                }
            });
            sc.gridy = i * 2 + 1;
            sidebar.add(btn, sc);

            JSeparator sep = new JSeparator();
            sep.setForeground(BORDER);
            sep.setBackground(BORDER);
            sc.gridy = i * 2 + 2;
            sidebar.add(sep, sc);
        }

        JButton logoutBtn = sidebarButton("Выйти");
        logoutBtn.addActionListener(e -> { dashboardPanel = null; showLogin(); });
        sc.gridy = items.length * 2 + 1;
        sidebar.add(logoutBtn, sc);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBackground(BORDER);
        sc.gridy = items.length * 2 + 2;
        sidebar.add(sep, sc);

        panel.add(sidebar, BorderLayout.EAST);
        panel.add(contentArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(TEXT);
        header.add(label, BorderLayout.WEST);
        return header;
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(TEXT);
        btn.setBackground(SIDEBAR);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(0x00, 0x00, 0x00));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(TEXT);
            }
        });
        return btn;
    }

    private JButton grayButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setForeground(TEXT);
        btn.setBackground(CARD_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 18, 8, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void showNotification(String message) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(24, 32, 24, 32)
        ));
        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        msgLabel.setForeground(TEXT);
        content.add(msgLabel, BorderLayout.CENTER);
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        okBtn.setForeground(TEXT);
        okBtn.setBackground(CARD_BG);
        okBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(6, 24, 6, 24)
        ));
        okBtn.setFocusPainted(false);
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setBackground(CARD_BG);
        btnRow.add(okBtn);
        content.add(btnRow, BorderLayout.SOUTH);
        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
