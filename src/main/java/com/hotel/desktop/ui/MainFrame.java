package com.hotel.desktop.ui;

import com.hotel.desktop.ui.ReportPanel;
import com.hotel.desktop.model.User;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    // Пастельно-бирюзовая цветовая гамма, соответствующая стилю референса
    private static final Color BG = new Color(0xE3, 0xEE, 0xF2); // Мягкий фон за карточками
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6); // Молочно-белый фон для контента
    private static final Color SIDEBAR = new Color(0xED, 0xF4, 0xF6); // Светлый бирюзово-серый сайдбар
    private static final Color HEADER_BG = Color.WHITE;

    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6); // Основной бирюзовый тон для текста
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6); // Вспомогательный светло-бирюзовый
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF); // Границы компонентов

    private static final Color ACCENT = new Color(0x99, 0xC4, 0xC7); // Фирменный бирюзовый акцент
    private static final Color ACCENT_HOVER = new Color(0x83, 0xB0, 0xB3);
    private static final Color SIDEBAR_HOVER = Color.WHITE;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel dashboardPanel;
    private User currentUser;
    private NewBookingsPanel newBookingsPanel;
    private RoomsManagementPanel roomsManagementPanel;

    public MainFrame() {
        setTitle("Отель - Бронирование номеров");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(450, 550));

        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName()
            );
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
        dashboardPanel = user.isStaff()
                ? createStaffDashboard(user)
                : createUserDashboard(user);
        mainPanel.add(dashboardPanel, "dashboard");
        cardLayout.show(mainPanel, "dashboard");
    }

    // Личный кабинет пользователя (с мягкой капсульной карточкой)
    private JPanel createUserDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Личный кабинет", true), BorderLayout.NORTH);

        // Карточка с честными закруглениями краев
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2d.dispose();
            }
        };
        card.setBackground(CARD_BG);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(35, 45, 35, 45));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.weightx = 1.0;

        // Круглая аватарка
        JPanel avatarPanel = createAvatarPanel(user.getFullName());
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(avatarPanel, c);

        JLabel title = new JLabel(
                "Информация о профиле",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Georgia", Font.PLAIN, 20));
        title.setForeground(TEXT);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 25, 0);
        card.add(title, c);

        c.gridwidth = 1;
        c.insets = new Insets(8, 0, 8, 0);
        int row = 2;
        row = addInfoRow(card, c, row, "ФИО:", user.getFullName());
        row = addInfoRow(card, c, row, "Email:", user.getEmail());
        row = addInfoRow(card, c, row, "Телефон:", user.getPhone());

        // Закругленная кнопка "Выйти"
        JButton logoutBtn = createStyledButton("Выйти из аккаунта", false);
        logoutBtn.addActionListener(e -> {
            dashboardPanel = null;
            showLogin();
        });

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.insets = new Insets(30, 0, 0, 0);
        card.add(logoutBtn, c);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(BG);
        wrap.add(card);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    // Создание круглой аватарки с инициалами в пастельной гамме
    private JPanel createAvatarPanel(String fullName) {
        String raw = "";
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.trim().split(" ");
            if (parts.length >= 2) {
                raw =
                        String.valueOf(parts[0].charAt(0)) +
                                String.valueOf(parts[1].charAt(0));
            } else {
                raw = String.valueOf(fullName.charAt(0));
            }
        }
        final String initials = raw.toUpperCase();

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(ACCENT);
                g2d.fillOval(0, 0, 75, 75);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 26));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getAscent();
                g2d.drawString(
                        initials,
                        (75 - textWidth) / 2,
                        (75 + textHeight) / 2 - 3
                );
                g2d.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(75, 75));
        avatar.setOpaque(false);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(avatar);
        return wrapper;
    }

    private int addInfoRow(
            JPanel panel,
            GridBagConstraints c,
            int row,
            String label,
            String value
    ) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SEC);
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.3;
        panel.add(lbl, c);

        JLabel val = new JLabel(
                value != null && !value.isEmpty() ? value : "—"
        );
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(TEXT);
        c.gridx = 1;
        c.gridy = row;
        c.weightx = 0.7;
        panel.add(val, c);
        return row + 1;
    }

    // Панель управления для сотрудников (Сайдбар + Контент)
    private JPanel createStaffDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Панель управления", false), BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(null);
        sidebarScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        sidebarScroll.setPreferredSize(new Dimension(220, 0));

        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.gridx = 0;
        sc.weightx = 1.0;

        // Блок приветствия в боковом меню
        JLabel welcomeSide = new JLabel("Сотрудник:", SwingConstants.LEFT);
        welcomeSide.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcomeSide.setForeground(TEXT_SEC);
        welcomeSide.setBorder(new EmptyBorder(20, 20, 2, 20));
        sc.gridy = 0;
        sidebar.add(welcomeSide, sc);

        JLabel nameSide = new JLabel(user.getFullName());
        nameSide.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameSide.setForeground(TEXT);
        nameSide.setBorder(new EmptyBorder(0, 20, 6, 20));
        sc.gridy = 1;
        sidebar.add(nameSide, sc);

        String roleDisplay = switch (user.getRole()) {
            case User.ROLE_ADMIN -> "Администратор";
            case User.ROLE_MANAGER -> "Менеджер";
            default -> "Сотрудник";
        };
        JLabel roleBadge = new JLabel(roleDisplay);
        roleBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleBadge.setForeground(ACCENT_HOVER);
        roleBadge.setBorder(new EmptyBorder(0, 20, 20, 20));
        sc.gridy = 2;
        sidebar.add(roleBadge, sc);

        JSeparator topSep = new JSeparator();
        topSep.setForeground(BORDER);
        sc.gridy = 3;
        sidebar.add(topSep, sc);

        // Полный объединенный список элементов меню для администратора и менеджера
        String[][] items = user.isAdmin()
                ? new String[][] {
                { "newBookings", "🔔  Новые брони" },
                { "bookings", "📅  Бронирования" },
                { "reports", "📊  Отчёты за день" },
                { "rooms", "🏨  Номера" },
                { "reviews", "⭐  Отзывы" },
                { "payments", "💰  Платежи" },
                { "users", "👥  Сотрудники" },
        }
                : new String[][] {
                { "newBookings", "🔔  Новые брони" },
                { "bookings", "📅  Бронирования" },
                { "reports", "📊  Отчёты за день" },
                { "rooms", "🏨  Номера" },
                { "reviews", "⭐  Отзывы" },
                { "payments", "💰  Платежи" },
        };

        CardLayout contentCards = new CardLayout();
        JPanel contentArea = new JPanel(contentCards);
        contentArea.setBackground(CARD_BG);

        JPanel welcomePanel = createWelcomePanel(user);
        contentArea.add(welcomePanel, "welcome");

        BookingManagementPanel bookingPanel = new BookingManagementPanel(this, user);
        contentArea.add(bookingPanel, "bookings");

        newBookingsPanel = new NewBookingsPanel(this);
        contentArea.add(newBookingsPanel, "newBookings");

        roomsManagementPanel = new RoomsManagementPanel(this);
        contentArea.add(roomsManagementPanel, "rooms");

        ReportPanel reportPanel = new ReportPanel(this, user);
        contentArea.add(reportPanel, "reports");

        contentCards.show(contentArea, "welcome");

        int btnIndex = 0;
        for (String[] item : items) {
            String key = item[0];
            String label = item[1];
            JButton btn = createSidebarButton(label);
            btn.addActionListener(e -> {
                if ("bookings".equals(key)) {
                    bookingPanel.loadData();
                    contentCards.show(contentArea, "bookings");
                } else if ("newBookings".equals(key)) {
                    newBookingsPanel.refreshData();
                    contentCards.show(contentArea, "newBookings");
                } else if ("rooms".equals(key)) {
                    roomsManagementPanel.loadData();
                    contentCards.show(contentArea, "rooms");
                } else if ("reports".equals(key)) {
                    reportPanel.loadReportData();
                    contentCards.show(contentArea, "reports");
                } else {
                    showNotification("Раздел \"" + label.substring(3) + "\" находится в разработке.");
                }
            });
            sc.gridy = 4 + btnIndex;
            sc.insets = new Insets(4, 10, 4, 10);
            sidebar.add(btn, sc);
            btnIndex++;
        }

        JSeparator bottomSep = new JSeparator();
        bottomSep.setForeground(BORDER);
        sc.gridy = 4 + btnIndex;
        sc.insets = new Insets(15, 10, 15, 10);
        sidebar.add(bottomSep, sc);

        JButton logoutBtn = createSidebarButton("🚪  Выйти");
        logoutBtn.addActionListener(e -> {
            dashboardPanel = null;
            showLogin();
        });
        sc.gridy = 5 + btnIndex;
        sc.insets = new Insets(4, 10, 20, 10);
        sidebar.add(logoutBtn, sc);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CARD_BG);
        contentWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentWrapper.add(contentArea, BorderLayout.CENTER);

        panel.add(sidebarScroll, BorderLayout.WEST);
        panel.add(contentWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWelcomePanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcome = new JLabel(
                "Добро пожаловать, " + user.getFullName() + "!"
        );
        welcome.setFont(new Font("Georgia", Font.PLAIN, 22));
        welcome.setForeground(TEXT);

        JLabel hint = new JLabel(
                "Выберите нужный пункт меню в левой панели для начала работы."
        );
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(TEXT_SEC);
        hint.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(welcome, BorderLayout.NORTH);
        panel.add(hint, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHeader(String title, boolean withBorder) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                        new EmptyBorder(16, 24, 16, 24)
                )
        );

        JLabel label = new JLabel(title);
        label.setFont(new Font("Georgia", Font.PLAIN, 18));
        label.setForeground(TEXT);
        header.add(label, BorderLayout.WEST);
        return header;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getBackground() == SIDEBAR_HOVER) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2d.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(TEXT);
        btn.setBackground(SIDEBAR);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        btn.setBackground(SIDEBAR_HOVER);
                        btn.setForeground(ACCENT_HOVER);
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        btn.setBackground(SIDEBAR);
                        btn.setForeground(TEXT);
                    }
                }
        );
        return btn;
    }

    private JButton createStyledButton(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));

        if (primary) {
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.addMouseListener(
                    new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            btn.setBackground(ACCENT_HOVER);
                        }

                        public void mouseExited(java.awt.event.MouseEvent e) {
                            btn.setBackground(ACCENT);
                        }
                    }
            );
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT);
            btn.setBorder(
                    BorderFactory.createCompoundBorder(
                            new RoundBorder(BORDER, 35),
                            new EmptyBorder(10, 22, 10, 22)
                    )
            );
            btn.addMouseListener(
                    new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            btn.setBackground(SIDEBAR);
                        }

                        public void mouseExited(java.awt.event.MouseEvent e) {
                            btn.setBackground(Color.WHITE);
                        }
                    }
            );
        }
        return btn;
    }

    public void showNotification(String message) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
            }
        };
        content.setBackground(CARD_BG);
        content.setOpaque(false);
        content.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundBorder(BORDER, 30),
                        new EmptyBorder(25, 35, 25, 35)
                )
        );

        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgLabel.setForeground(TEXT);
        content.add(msgLabel, BorderLayout.CENTER);

        JButton okBtn = createStyledButton("OK", true);
        okBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(okBtn);
        content.add(btnRow, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radii;

        RoundBorder(Color color, int radii) {
            this.color = color;
            this.radii = radii;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(
                    x + 1,
                    y + 1,
                    width - 3,
                    height - 3,
                    radii,
                    radii
            );
            g2d.dispose();
        }
    }
}