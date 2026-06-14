package com.hotel.desktop.ui;

import com.hotel.desktop.model.Booking;
import com.hotel.desktop.model.Room;
import com.hotel.desktop.model.User;
import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.util.Validator;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {

    // Цветовая гамма
    private static final Color BG = new Color(0xE3, 0xEE, 0xF2);
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6);
    private static final Color SIDEBAR = new Color(0xED, 0xF4, 0xF6);
    private static final Color HEADER_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6);
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6);
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF);
    private static final Color ACCENT = new Color(0x99, 0xC4, 0xC7);
    private static final Color ACCENT_HOVER = new Color(0x83, 0xB0, 0xB3);
    private static final Color SIDEBAR_HOVER = Color.WHITE;
    private static final Color GREEN = new Color(0x8E, 0xBC, 0x9F);
    private static final Color ERROR = new Color(0xD9, 0x8E, 0x8E);

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private JPanel dashboardPanel;
    private User currentUser;

    // Маппинг для номеров
    private static final Map<Long, String> IMAGE_MAP = new HashMap<>();
    private static final Map<Long, String> TITLE_MAP = new HashMap<>();
    private static final Map<Long, Integer> PRICE_MAP = new HashMap<>();
    private static final Map<Long, String> DESC_MAP = new HashMap<>();

    static {
        IMAGE_MAP.put(1L, "/images/image1.jpg");
        TITLE_MAP.put(1L, "Уютный люкс \"Canava\"");
        PRICE_MAP.put(1L, 16000);
        DESC_MAP.put(1L, "Просторный уютный люкс с видом на море. King-size кровать, кондиционер, телевизор, мини-бар, Wi-Fi, джакузи.");

        IMAGE_MAP.put(2L, "/images/image2.jpg");
        TITLE_MAP.put(2L, "Делюкс с террасой \"Lemon Garden\"");
        PRICE_MAP.put(2L, 24500);
        DESC_MAP.put(2L, "Роскошный делюкс с собственной террасой. Королевская кровать, кондиционер, телевизор, мини-бар, Wi-Fi, джакузи.");

        IMAGE_MAP.put(3L, "/images/image3.jpg");
        TITLE_MAP.put(3L, "\"The Heritage Cave\" с бассейном");
        PRICE_MAP.put(3L, 25500);
        DESC_MAP.put(3L, "Уникальный номер в пещерном стиле с частным бассейном. Большая кровать, кондиционер, телевизор, Wi-Fi.");

        IMAGE_MAP.put(4L, "/images/image4.jpg");
        TITLE_MAP.put(4L, "Панорамный люкс \"Aegean\"");
        PRICE_MAP.put(4L, 17500);
        DESC_MAP.put(4L, "Номер с панорамным остеклением. King-size кровать, кондиционер, телевизор, мини-бар, Wi-Fi, джакузи.");
    }

    public MainFrame() {
        setTitle("Отель - Бронирование номеров");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(450, 550));

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

    // ==================== ЛИЧНЫЙ КАБИНЕТ ПОЛЬЗОВАТЕЛЯ ====================

    private JPanel createUserDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Личный кабинет"), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

        JPanel avatarPanel = createAvatarPanel(user.getFullName());
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(avatarPanel, c);

        JLabel title = new JLabel("Информация о профиле", SwingConstants.CENTER);
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

        JButton logoutBtn = createStyledButton("Выйти из аккаунта", false);
        logoutBtn.addActionListener(e -> { dashboardPanel = null; showLogin(); });

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

    private JPanel createAvatarPanel(String fullName) {
        String raw = "";
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.trim().split(" ");
            if (parts.length >= 2) {
                raw = String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[1].charAt(0));
            } else {
                raw = String.valueOf(fullName.charAt(0));
            }
        }
        final String initials = raw.toUpperCase();

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ACCENT);
                g2d.fillOval(0, 0, 75, 75);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 26));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getAscent();
                g2d.drawString(initials, (75 - textWidth) / 2, (75 + textHeight) / 2 - 3);
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

    private int addInfoRow(JPanel panel, GridBagConstraints c, int row, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SEC);
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0.3;
        panel.add(lbl, c);

        JLabel val = new JLabel(value != null && !value.isEmpty() ? value : "—");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(TEXT);
        c.gridx = 1;
        c.gridy = row;
        c.weightx = 0.7;
        panel.add(val, c);
        return row + 1;
    }

    // ==================== ПАНЕЛЬ УПРАВЛЕНИЯ ДЛЯ СОТРУДНИКОВ ====================

    private JPanel createStaffDashboard(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.add(createHeader("Панель управления"), BorderLayout.NORTH);

        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(null);
        sidebarScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setPreferredSize(new Dimension(220, 0));

        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.gridx = 0;
        sc.weightx = 1.0;

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

        // Меню
        String[][] items = user.isAdmin()
                ? new String[][]{
                {"home", "🏠 Главная"},
                {"profile", "👤 Личный кабинет"},
                {"bookings", "📅 Бронирования"},
                {"rooms", "🏨 Номера"},
                {"daily_report", "📊 Отчёт за день"},
                {"reviews", "⭐ Отзывы"},
                {"payments", "💰 Платежи"},
                {"users", "👥 Сотрудники"},
        }
                : new String[][]{
                {"home", "🏠 Главная"},
                {"profile", "👤 Личный кабинет"},
                {"bookings", "📅 Бронирования"},
                {"rooms", "🏨 Номера"},
                {"daily_report", "📊 Отчёт за день"},
                {"reviews", "⭐ Отзывы"},
                {"payments", "💰 Платежи"},
        };

        CardLayout contentCards = new CardLayout();
        JPanel contentArea = new JPanel(contentCards);
        contentArea.setBackground(CARD_BG);

        // Создаём панели
        JPanel homePanel = createHomePanel(user);
        JPanel profilePanel = createProfilePanel(user);
        BookingManagementPanel bookingPanel = new BookingManagementPanel(this, user);
        JPanel dailyReportPanel = createDailyReportPanel();

        contentArea.add(homePanel, "home");
        contentArea.add(profilePanel, "profile");
        contentArea.add(bookingPanel, "bookings");
        contentArea.add(dailyReportPanel, "daily_report");

        int btnIndex = 0;
        for (String[] item : items) {
            String key = item[0];
            String label = item[1];
            JButton btn = createSidebarButton(label);
            btn.addActionListener(e -> {
                if ("home".equals(key)) {
                    contentArea.remove(homePanel);
                    contentArea.add(createHomePanel(user), "home");
                    contentCards.show(contentArea, "home");
                } else if ("profile".equals(key)) {
                    contentArea.remove(profilePanel);
                    contentArea.add(createProfilePanel(currentUser), "profile");
                    contentCards.show(contentArea, "profile");
                } else if ("bookings".equals(key)) {
                    bookingPanel.loadData();
                    contentCards.show(contentArea, "bookings");
                } else if ("rooms".equals(key)) {
                    showRoomsDialog();
                } else if ("daily_report".equals(key)) {
                    contentCards.show(contentArea, "daily_report");
                } else {
                    showNotification("Раздел \"" + label.substring(2) + "\" в разработке.");
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

        JButton logoutBtn = createSidebarButton("🚪 Выйти");
        logoutBtn.addActionListener(e -> { dashboardPanel = null; showLogin(); });
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

    // ==================== ОТЧЁТ ЗА ДЕНЬ ====================

    private JPanel createDailyReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 25, 25, 25));

        JPanel mainCard = createRoundedPanel(CARD_BG);
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 20), new EmptyBorder(20, 25, 25, 25)));

        // Заголовок
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("📊 Отчёт за день");
        title.setFont(new Font("Georgia", Font.PLAIN, 22));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        // Выбор даты
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        datePanel.setOpaque(false);

        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        dateLabel.setForeground(ACCENT);

        JButton prevBtn = createSmallButton("◀");
        JButton nextBtn = createSmallButton("▶");
        JButton todayBtn = createSmallButton("Сегодня");

        datePanel.add(prevBtn);
        datePanel.add(dateLabel);
        datePanel.add(nextBtn);
        datePanel.add(todayBtn);
        header.add(datePanel, BorderLayout.EAST);
        mainCard.add(header, BorderLayout.NORTH);

        // Таблица
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"№ брони", "Клиент", "Телефон", "Номер", "Заезд", "Выезд"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setShowGrid(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_SEC);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        mainCard.add(scrollPane, BorderLayout.CENTER);

        // Итого
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JLabel totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalLabel.setForeground(GREEN);
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        mainCard.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(mainCard, BorderLayout.CENTER);

        // Загрузка данных
        java.util.function.Consumer<LocalDate> loadData = (date) -> {
            dateLabel.setText(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            List<Booking> allBookings = DatabaseManager.getInstance().getAllBookings();
            List<Booking> dailyBookings = allBookings.stream()
                    .filter(b -> Booking.STATUS_CONFIRMED.equals(b.getStatus()))
                    .filter(b -> b.getBookingDate() != null && b.getBookingDate().toLocalDate().equals(date))
                    .collect(Collectors.toList());

            tableModel.setRowCount(0);
            for (Booking b : dailyBookings) {
                tableModel.addRow(new Object[]{
                        "#" + b.getId(), b.getUserName(),
                        b.getUserPhone() != null ? b.getUserPhone() : "—",
                        b.getRoomInfo(),
                        b.getCheckInDate() != null ? b.getCheckInDate().toString() : "—",
                        b.getCheckOutDate() != null ? b.getCheckOutDate().toString() : "—"
                });
            }
            totalLabel.setText("✅ Всего подтверждённых броней: " + dailyBookings.size());
        };

        LocalDate[] currentDate = {LocalDate.now()};
        loadData.accept(currentDate[0]);

        prevBtn.addActionListener(e -> { currentDate[0] = currentDate[0].minusDays(1); loadData.accept(currentDate[0]); });
        nextBtn.addActionListener(e -> { currentDate[0] = currentDate[0].plusDays(1); loadData.accept(currentDate[0]); });
        todayBtn.addActionListener(e -> { currentDate[0] = LocalDate.now(); loadData.accept(currentDate[0]); });

        return panel;
    }

    // ==================== ЛИЧНЫЙ КАБИНЕТ С РЕДАКТИРОВАНИЕМ ====================

    private JPanel createProfilePanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 25, 25, 25));

        JPanel card = createRoundedPanel(CARD_BG);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 20), new EmptyBorder(30, 35, 35, 35)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);
        c.gridx = 0;
        c.gridwidth = 2;

        JLabel title = new JLabel("Личный кабинет");
        title.setFont(new Font("Georgia", Font.PLAIN, 22));
        title.setForeground(TEXT);
        c.gridy = 0;
        card.add(title, c);

        JPanel avatarPanel = createAvatarPanel(user.getFullName());
        c.gridy = 1;
        c.insets = new Insets(10, 0, 20, 0);
        card.add(avatarPanel, c);

        String roleDisplay = switch (user.getRole()) {
            case User.ROLE_ADMIN -> "Администратор";
            case User.ROLE_MANAGER -> "Менеджер";
            default -> "Клиент";
        };
        JLabel roleLabel = new JLabel("Роль: " + roleDisplay);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(ACCENT);
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 25, 0);
        card.add(roleLabel, c);

        // Режим просмотра
        JPanel viewPanel = new JPanel(new GridBagLayout());
        viewPanel.setOpaque(false);
        GridBagConstraints vc = new GridBagConstraints();
        vc.fill = GridBagConstraints.HORIZONTAL;
        vc.insets = new Insets(6, 0, 6, 0);
        vc.gridx = 0;
        vc.gridwidth = 2;

        String[][] fields = {{"Логин", user.getLogin()}, {"Email", user.getEmail() != null ? user.getEmail() : "—"}};
        for (int i = 0; i < fields.length; i++) {
            vc.gridy = i * 2;
            JLabel label = new JLabel(fields[i][0] + ":");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SEC);
            viewPanel.add(label, vc);

            vc.gridy = i * 2 + 1;
            JLabel value = new JLabel(fields[i][1]);
            value.setName("value_" + i);
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            value.setForeground(TEXT);
            viewPanel.add(value, vc);
        }
        c.gridy = 3;
        card.add(viewPanel, c);

        // Режим редактирования
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setOpaque(false);
        editPanel.setVisible(false);
        GridBagConstraints ec = new GridBagConstraints();
        ec.fill = GridBagConstraints.HORIZONTAL;
        ec.insets = new Insets(4, 0, 4, 0);
        ec.gridx = 0;
        ec.gridwidth = 2;

        JLabel editTitle = new JLabel("Редактирование профиля");
        editTitle.setFont(new Font("Georgia", Font.PLAIN, 14));
        editTitle.setForeground(TEXT);
        ec.gridy = 0;
        ec.insets = new Insets(0, 0, 15, 0);
        editPanel.add(editTitle, ec);

        JTextField loginField = new JTextField(user.getLogin(), 20);
        JTextField emailField = new JTextField(user.getEmail(), 20);
        JComponent[] editFields = {loginField, emailField};
        String[] editLabels = {"Логин:", "Email:"};

        ec.gridwidth = 1;
        ec.insets = new Insets(8, 0, 2, 0);
        for (int i = 0; i < editLabels.length; i++) {
            ec.gridy = i * 2 + 1;
            JLabel label = new JLabel(editLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SEC);
            editPanel.add(label, ec);

            ec.gridy = i * 2 + 2;
            ec.insets = new Insets(2, 0, 8, 0);
            editFields[i].setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 15), new EmptyBorder(8, 12, 8, 12)));
            editPanel.add(editFields[i], ec);
            ec.insets = new Insets(8, 0, 2, 0);
        }

        ec.gridwidth = 2;
        ec.gridy = editLabels.length * 2 + 1;
        ec.insets = new Insets(20, 0, 5, 0);
        JLabel passTitle = new JLabel("Смена пароля");
        passTitle.setFont(new Font("Georgia", Font.PLAIN, 13));
        passTitle.setForeground(TEXT);
        editPanel.add(passTitle, ec);

        ec.gridwidth = 1;
        ec.gridy = editLabels.length * 2 + 2;
        JLabel newPassLabel = new JLabel("Новый пароль:");
        newPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newPassLabel.setForeground(TEXT_SEC);
        editPanel.add(newPassLabel, ec);

        ec.gridy = editLabels.length * 2 + 3;
        JPasswordField newPasswordField = new JPasswordField(20);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 15), new EmptyBorder(8, 12, 8, 12)));
        editPanel.add(newPasswordField, ec);

        ec.gridy = editLabels.length * 2 + 4;
        JLabel confirmLabel = new JLabel("Подтверждение:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmLabel.setForeground(TEXT_SEC);
        editPanel.add(confirmLabel, ec);

        ec.gridy = editLabels.length * 2 + 5;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 15), new EmptyBorder(8, 12, 8, 12)));
        editPanel.add(confirmPasswordField, ec);

        c.gridy = 4;
        card.add(editPanel, c);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton editBtn = createStyledButton("✏️ Редактировать", false);
        JButton saveBtn = createStyledButton("💾 Сохранить", true);
        JButton cancelBtn = createStyledButton("❌ Отмена", false);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);

        editBtn.addActionListener(e -> {
            viewPanel.setVisible(false);
            editPanel.setVisible(true);
            editBtn.setVisible(false);
            saveBtn.setVisible(true);
            cancelBtn.setVisible(true);
        });

        cancelBtn.addActionListener(e -> {
            viewPanel.setVisible(true);
            editPanel.setVisible(false);
            editBtn.setVisible(true);
            saveBtn.setVisible(false);
            cancelBtn.setVisible(false);
            loginField.setText(user.getLogin());
            emailField.setText(user.getEmail());
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        });

        saveBtn.addActionListener(e -> {
            String newLogin = loginField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            java.util.List<String> errors = new java.util.ArrayList<>();

            if (!newLogin.equals(user.getLogin())) {
                errors.addAll(Validator.validateLogin(newLogin));
                if (DatabaseManager.getInstance().existsByLogin(newLogin)) errors.add("Логин уже занят");
            }
            if (!newEmail.equals(user.getEmail())) {
                errors.addAll(Validator.validateEmail(newEmail));
                if (DatabaseManager.getInstance().existsByEmail(newEmail)) errors.add("Email уже используется");
            }
            if (!newPassword.isEmpty()) {
                errors.addAll(Validator.validatePassword(newPassword));
                if (!newPassword.equals(confirmPassword)) errors.add("Пароли не совпадают");
            }

            if (!errors.isEmpty()) {
                StringBuilder sb = new StringBuilder("<html>");
                for (String err : errors) sb.append("• ").append(err).append("<br>");
                sb.append("</html>");
                showNotification(sb.toString());
                return;
            }

            try {
                user.setLogin(newLogin);
                user.setEmail(newEmail);
                DatabaseManager.getInstance().updateUser(user);
                if (!newPassword.isEmpty()) {
                    DatabaseManager.getInstance().updateUserPassword(user.getId(), newPassword);
                }
                showNotification("Профиль успешно обновлён!");

                viewPanel.setVisible(true);
                editPanel.setVisible(false);
                editBtn.setVisible(true);
                saveBtn.setVisible(false);
                cancelBtn.setVisible(false);

                Component[] viewComponents = viewPanel.getComponents();
                String[] newValues = {newLogin, newEmail};
                int idx = 0;
                for (Component comp : viewComponents) {
                    if (comp instanceof JLabel && ((JLabel) comp).getName() != null && ((JLabel) comp).getName().startsWith("value_")) {
                        ((JLabel) comp).setText(newValues[idx++]);
                    }
                }
                currentUser = user;
            } catch (Exception ex) {
                showNotification("Ошибка сохранения: " + ex.getMessage());
            }
        });

        buttonPanel.add(editBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        c.gridy = 5;
        card.add(buttonPanel, c);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    // ==================== ГЛАВНАЯ СТРАНИЦА ====================

    private JPanel createHomePanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 25, 25, 25));

        JLabel welcomeLabel = new JLabel("Добро пожаловать, " + user.getFullName() + "!");
        welcomeLabel.setFont(new Font("Georgia", Font.PLAIN, 22));
        welcomeLabel.setForeground(TEXT);
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel searchCard = createRoundedPanel(CARD_BG);
        searchCard.setLayout(new BorderLayout());
        searchCard.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 20), new EmptyBorder(25, 30, 30, 30)));

        JLabel searchTitle = new JLabel("🔍 Быстрый поиск бронирования");
        searchTitle.setFont(new Font("Georgia", Font.BOLD, 16));
        searchTitle.setForeground(TEXT);
        searchCard.add(searchTitle, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

        GridBagConstraints sc = new GridBagConstraints();
        sc.fill = GridBagConstraints.HORIZONTAL;
        sc.insets = new Insets(5, 5, 5, 5);

        JLabel idLabel = new JLabel("Введите ID бронирования:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idLabel.setForeground(TEXT_SEC);
        sc.gridx = 0;
        sc.gridy = 0;
        sc.gridwidth = 2;
        searchPanel.add(idLabel, sc);

        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 15), new EmptyBorder(10, 15, 10, 15)));
        sc.gridy = 1;
        sc.gridwidth = 1;
        sc.weightx = 0.7;
        searchPanel.add(searchField, sc);

        JButton searchBtn = createStyledButton("Найти", true);
        sc.gridx = 1;
        sc.weightx = 0.3;
        searchPanel.add(searchBtn, sc);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setOpaque(false);
        resultPanel.setVisible(false);
        resultPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        searchBtn.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) { showNotification("Введите ID бронирования"); return; }
            try {
                long bookingId = Long.parseLong(text);
                Booking booking = DatabaseManager.getInstance().getAllBookings().stream().filter(b -> b.getId() == bookingId).findFirst().orElse(null);
                if (booking != null) {
                    resultPanel.removeAll();
                    JPanel resultCard = createRoundedPanel(new Color(0xF0, 0xF8, 0xF8));
                    resultCard.setLayout(new GridBagLayout());
                    resultCard.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(ACCENT, 15), new EmptyBorder(15, 20, 15, 20)));
                    GridBagConstraints rc = new GridBagConstraints();
                    rc.fill = GridBagConstraints.HORIZONTAL;
                    rc.insets = new Insets(4, 0, 4, 0);
                    rc.gridx = 0;
                    rc.gridwidth = 2;

                    JLabel foundTitle = new JLabel("✅ Бронирование найдено");
                    foundTitle.setFont(new Font("Georgia", Font.BOLD, 14));
                    foundTitle.setForeground(GREEN);
                    rc.gridy = 0;
                    resultCard.add(foundTitle, rc);

                    rc.gridwidth = 1;
                    String[][] data = {
                            {"ID брони:", String.valueOf(booking.getId())},
                            {"Клиент:", booking.getUserName()},
                            {"Телефон:", booking.getUserPhone() != null ? booking.getUserPhone() : "—"},
                            {"Номер:", booking.getRoomInfo() != null ? booking.getRoomInfo() : "№" + booking.getRoomId()},
                            {"Статус:", formatStatus(booking.getStatus())},
                    };
                    for (int i = 0; i < data.length; i++) {
                        rc.gridy = i + 1;
                        JPanel row = new JPanel(new BorderLayout(10, 0));
                        row.setOpaque(false);
                        JLabel label = new JLabel(data[i][0]);
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        label.setForeground(TEXT_SEC);
                        JLabel value = new JLabel(data[i][1]);
                        value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        value.setForeground(TEXT);
                        row.add(label, BorderLayout.WEST);
                        row.add(value, BorderLayout.EAST);
                        resultCard.add(row, rc);
                    }
                    resultPanel.add(resultCard, BorderLayout.CENTER);
                    resultPanel.setVisible(true);
                } else {
                    showNotification("Бронь с ID " + bookingId + " не найдена");
                    resultPanel.setVisible(false);
                }
            } catch (NumberFormatException ex) {
                showNotification("Введите корректный числовой ID");
                resultPanel.setVisible(false);
            }
        });

        searchCard.add(searchPanel, BorderLayout.CENTER);
        searchCard.add(resultPanel, BorderLayout.SOUTH);
        panel.add(searchCard, BorderLayout.CENTER);
        return panel;
    }

    private String formatStatus(String status) {
        if (Booking.STATUS_PENDING.equals(status)) return "Не подтверждён";
        if (Booking.STATUS_CONFIRMED.equals(status)) return "Подтвержден";
        if (Booking.STATUS_PAID.equals(status)) return "Оплачен";
        if (Booking.STATUS_CANCELLED.equals(status)) return "Отменён";
        return status;
    }

    // ==================== НОМЕРА ====================

    private void showRoomsDialog() {
        JDialog dialog = new JDialog(this, "Каталог номеров", true);
        dialog.setSize(1000, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel mainCard = createRoundedPanel(CARD_BG);
        mainCard.setLayout(new BorderLayout());
        mainCard.setBorder(new EmptyBorder(20, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Наши номера");
        title.setFont(new Font("Georgia", Font.PLAIN, 24));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        closeBtn.setForeground(TEXT_SEC);
        closeBtn.setBackground(CARD_BG);
        closeBtn.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 20), new EmptyBorder(6, 12, 6, 12)));
        closeBtn.addActionListener(e -> dialog.dispose());
        header.add(closeBtn, BorderLayout.EAST);
        mainCard.add(header, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 25, 25));
        cardsPanel.setBackground(CARD_BG);

        for (Room room : DatabaseManager.getInstance().getAllRooms()) {
            String imagePath = IMAGE_MAP.getOrDefault(room.getId(), null);
            String roomTitle = TITLE_MAP.getOrDefault(room.getId(), room.getComfortLevel() + " №" + room.getId());
            int roomPrice = PRICE_MAP.getOrDefault(room.getId(), room.getPricePerNight().intValue());
            String roomDesc = DESC_MAP.getOrDefault(room.getId(), "");

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 20), new EmptyBorder(0, 0, 0, 0)));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setPreferredSize(new Dimension(400, 250));
            ImageIcon icon = loadImage(imagePath);
            if (icon != null) {
                imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(400, 250, Image.SCALE_SMOOTH)));
            } else {
                imageLabel.setIcon(createPlaceholderImage(room, 400, 250));
            }
            card.add(imageLabel, BorderLayout.CENTER);

            JPanel captionPanel = new JPanel(new GridBagLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0x2C, 0x3E, 0x50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
                    g2d.dispose();
                }
            };
            captionPanel.setOpaque(false);
            captionPanel.setBorder(new EmptyBorder(15, 18, 15, 18));
            GridBagConstraints gp = new GridBagConstraints();
            gp.fill = GridBagConstraints.HORIZONTAL;
            gp.gridx = 0;
            gp.gridwidth = 2;

            JLabel nameLabel = new JLabel(roomTitle);
            nameLabel.setFont(new Font("Georgia", Font.BOLD, 15));
            nameLabel.setForeground(Color.WHITE);
            gp.gridy = 0;
            captionPanel.add(nameLabel, gp);

            JLabel priceLabel = new JLabel(String.format("%,d", roomPrice) + " ₽ / ночь");
            priceLabel.setFont(new Font("Georgia", Font.BOLD, 18));
            priceLabel.setForeground(Color.WHITE);
            gp.gridy = 1;
            gp.insets = new Insets(5, 0, 5, 0);
            captionPanel.add(priceLabel, gp);

            JLabel idLabel = new JLabel("ID номера: " + room.getId());
            idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            idLabel.setForeground(new Color(0xCC, 0xCC, 0xCC));
            gp.gridy = 2;
            captionPanel.add(idLabel, gp);

            card.add(captionPanel, BorderLayout.SOUTH);
            cardsPanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(CARD_BG);
        mainCard.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(mainCard, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    private JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER), new EmptyBorder(16, 24, 16, 24)));
        JLabel label = new JLabel(title);
        label.setFont(new Font("Georgia", Font.PLAIN, 18));
        label.setForeground(TEXT);
        header.add(label, BorderLayout.WEST);
        return header;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (getBackground() == SIDEBAR_HOVER) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); btn.setForeground(ACCENT_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR); btn.setForeground(TEXT); }
        });
        return btn;
    }

    private JButton createStyledButton(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_HOVER); }
                public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT); }
            });
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXT);
            btn.setBorder(BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 35), new EmptyBorder(10, 22, 10, 22)));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(SIDEBAR); }
                public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(Color.WHITE); }
            });
        }
        return btn;
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(TEXT);
        btn.setBackground(CARD_BG);
        btn.setBorder(BorderFactory.createLineBorder(BORDER));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createRoundedPanel(Color bg) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
            }
        };
    }

    private ImageIcon loadImage(String path) {
        if (path == null) return null;
        try {
            java.net.URL imgUrl = getClass().getResource(path);
            if (imgUrl != null) return new ImageIcon(imgUrl);
            return null;
        } catch (Exception e) { return null; }
    }

    private ImageIcon createPlaceholderImage(Room room, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xE8, 0xF0, 0xEE));
        g.fillRect(0, 0, width, height);
        g.setColor(TEXT_SEC);
        g.setFont(new Font("Georgia", Font.PLAIN, 18));
        String text = room.getComfortLevel();
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = height / 2;
        g.drawString(text, x, y);
        g.dispose();
        return new ImageIcon(img);
    }

    public void showNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Уведомление", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radii;
        RoundBorder(Color color, int radii) { this.color = color; this.radii = radii; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radii, radii);
            g2d.dispose();
        }
    }
}