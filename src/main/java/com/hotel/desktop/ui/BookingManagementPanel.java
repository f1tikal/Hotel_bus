package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.Booking;
import com.hotel.desktop.model.User;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate; // ДОБАВИЛИ ИМПОРТ ДЛЯ РАБОТЫ С ДАТАМИ
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BookingManagementPanel extends JPanel {

    // Единая пастельно-бирюзовая цветовая палитра
    private static final Color BG = new Color(0xE3, 0xEE, 0xF2); // Мягкий фон панели
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6); // Плиточный фон контента
    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6); // Текст основного тона
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6); // Вспомогательный текст
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF); // Границы

    // Мягкие статусные цвета
    private static final Color GREEN = new Color(0x8E, 0xBC, 0x9F); // Пастельный зеленый
    private static final Color BLUE = new Color(0x8E, 0xAB, 0xBC); // Пастельный синий
    private static final Color ERROR = new Color(0xD9, 0x8E, 0x8E); // Пастельный красный для предупреждений

    private final MainFrame mainFrame;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel infoLabel;
    private JPanel actionPanel;
    private List<Booking> bookings;

    public BookingManagementPanel(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(BG);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Главный контейнер-карточка со скругленными углами
        JPanel mainCard = new JPanel(new BorderLayout()) {
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
        mainCard.setBackground(CARD_BG);
        mainCard.setOpaque(false);

        // Шапка панели управления
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 24, 16, 24));

        JLabel title = new JLabel("Управление бронированиями");
        title.setFont(new Font("Georgia", Font.PLAIN, 20));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        infoLabel = new JLabel();
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_SEC);
        header.add(infoLabel, BorderLayout.EAST);

        mainCard.add(header, BorderLayout.NORTH);

        // Модель таблицы (запрет редактирования ячеек)
        tableModel = new DefaultTableModel(
                new String[] {
                        "№ брони",
                        "Клиент",
                        "Телефон",
                        "Email",
                        "Номер",
                        "Заезд",
                        "Выезд",
                        "Статус",
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38); // Увеличенная высота для воздушности строк
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0xEB, 0xF2, 0xF2));
        table.setSelectionForeground(TEXT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Кастомизация заголовков таблицы
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_SEC);
        table.getTableHeader().setReorderingAllowed(false);
        table
                .getTableHeader()
                .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Внутренние отступы для заголовков
        (
                (DefaultTableCellRenderer) table
                        .getTableHeader()
                        .getDefaultRenderer()
        ).setHorizontalAlignment(SwingConstants.LEFT);

        // Кастомный рендеринг ячеек (отступы и чистый цвет)
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(CARD_BG);
                    setForeground(TEXT);
                }
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateActionButtons();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD_BG);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.setBorder(new EmptyBorder(0, 24, 0, 24));
        tableWrap.add(scroll, BorderLayout.CENTER);
        mainCard.add(tableWrap, BorderLayout.CENTER);

        // Нижняя панель действий
        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(16, 24, 24, 24));
        mainCard.add(actionPanel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);
    }

    public void loadData() {
        bookings = DatabaseManager.getInstance().getAllBookings();
        tableModel.setRowCount(0);

        for (Booking b : bookings) {
            tableModel.addRow(new Object[] {
                    "#" + b.getId(),
                    b.getUserName(),
                    b.getUserPhone() != null ? b.getUserPhone() : "—",
                    b.getUserEmail() != null ? b.getUserEmail() : "—",
                    b.getRoomInfo(),
                    b.getCheckInDate() != null
                            ? b.getCheckInDate().toString()
                            : "—",
                    b.getCheckOutDate() != null
                            ? b.getCheckOutDate().toString()
                            : "—",
                    formatStatus(b.getStatus()),
            });
        }

        infoLabel.setText("Всего записей: " + bookings.size());
        updateActionButtons();
    }

    private String formatStatus(String status) {
        if (Booking.STATUS_PENDING.equals(status)) return "Не подтверждён";
        if (Booking.STATUS_CONFIRMED.equals(status)) return "Подтверждён";
        if (Booking.STATUS_PAID.equals(status)) return "Оплачен";
        if (Booking.STATUS_CANCELLED.equals(status)) return "Отменён";
        return status;
    }

    private void updateActionButtons() {
        actionPanel.removeAll();

        JButton addBtn = createStyledButton("Добавить бронь", BLUE);
        addBtn.addActionListener(e -> showAddBookingDialog());
        actionPanel.add(addBtn);

        int row = table.getSelectedRow();
        if (row >= 0) {
            Booking b = bookings.get(row);

            JButton deleteBtn = createStyledButton("Удалить бронь", new Color(0xC0, 0x39, 0x2B)); // Красный цвет
            deleteBtn.addActionListener(e -> deleteBooking(b));
            actionPanel.add(deleteBtn);

            if (b.canConfirm()) {
                JButton confirmBtn = createStyledButton("Подтвердить", GREEN);
                confirmBtn.addActionListener(e -> confirmBooking(b));
                actionPanel.add(confirmBtn);
            }

            if (b.canMarkPaid() && currentUser.isAdmin()) {
                JButton paidBtn = createStyledButton("Оплачен", BLUE);
                paidBtn.addActionListener(e -> markPaid(b));
                actionPanel.add(paidBtn);
            }

            if (b.canMarkPaid() && !currentUser.isAdmin()) {
                JLabel restricted = new JLabel(
                        "<html><span style='color:#c0392b;font-size:11px; margin-left:10px;'>"
                                + "(Только админ отмечает оплату)</span></html>");
                actionPanel.add(restricted);
            }
        } else {
            JLabel hint = new JLabel("← Выберите бронь для удаления или изменения статуса");
            hint.setFont(new Font("Arial", Font.PLAIN, 12));
            hint.setForeground(TEXT_SEC);
            actionPanel.add(hint);
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void deleteBooking(Booking b) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Вы уверены, что хотите полностью удалить бронь №" + b.getId() + "?",
                "Удаление бронирования", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseManager.getInstance().deleteBooking(b.getId());
                showNotification("Бронь №" + b.getId() + " успешно удалена.");
                loadData(); // Перезагружаем таблицу
            } catch (Exception e) {
                showNotification("Ошибка удаления: " + e.getMessage());
            }
        }
    }

    private void showAddBookingDialog() {
        JDialog dialog = new JDialog(mainFrame, "Создание бронирования", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 12, 6, 12);

        // Поле выбора клиента
        c.gridx = 0; c.gridy = 0;
        dialog.add(new JLabel("Выберите клиента:"), c);

        List<User> clients = DatabaseManager.getInstance().getAllClients();
        JComboBox<User> clientCombo = new JComboBox<>(clients.toArray(new User[0]));
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean sel, boolean foc) {
                super.getListCellRendererComponent(list, val, idx, sel, foc);
                if (val instanceof User u) {
                    setText(u.getLastName() + " " + u.getFirstName() + " (" + u.getLogin() + ")");
                }
                return this;
            }
        });
        c.gridy = 1;
        dialog.add(clientCombo, c);

        // Поле выбора комнаты
        c.gridy = 2;
        dialog.add(new JLabel("Выберите доступный номер:"), c);

        List<com.hotel.desktop.model.Room> rooms = DatabaseManager.getInstance().getAllRooms();
        JComboBox<com.hotel.desktop.model.Room> roomCombo = new JComboBox<>(rooms.toArray(new com.hotel.desktop.model.Room[0]));
        c.gridy = 3;
        dialog.add(roomCombo, c);

        // Поля дат
        c.gridy = 4;
        dialog.add(new JLabel("Дата заезда (ГГГГ-ММ-ДД):"), c);
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        c.gridy = 5;
        dialog.add(checkInField, c);

        c.gridy = 6;
        dialog.add(new JLabel("Дата выезда (ГГГГ-ММ-ДД):"), c);
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());
        c.gridy = 7;
        dialog.add(checkOutField, c);

        // Кнопка сохранения
        JButton saveBtn = new JButton("Оформить бронь");
        saveBtn.setBackground(BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.addActionListener(e -> {
            User selectedUser = (User) clientCombo.getSelectedItem();
            com.hotel.desktop.model.Room selectedRoom = (com.hotel.desktop.model.Room) roomCombo.getSelectedItem();

            if (selectedUser == null || selectedRoom == null) {
                JOptionPane.showMessageDialog(dialog, "Необходимо выбрать клиента и номер!");
                return;
            }

            try {
                LocalDate cin = LocalDate.parse(checkInField.getText().trim());
                LocalDate cout = LocalDate.parse(checkOutField.getText().trim());

                if (!cout.isAfter(cin)) {
                    JOptionPane.showMessageDialog(dialog, "Ошибка: Дата выезда должна быть позже даты заезда!");
                    return;
                }

                DatabaseManager.getInstance().addBooking(selectedUser.getId(), selectedRoom.getId(), cin, cout);
                dialog.dispose();
                showNotification("Бронь успешно создана!");
                loadData(); // обновляем список
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Неверный формат даты! Вводите в формате: ГГГГ-ММ-ДД");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка БД: " + ex.getMessage());
            }
        });

        c.gridy = 8; c.insets = new Insets(15, 12, 12, 12);
        dialog.add(saveBtn, c);

        dialog.setVisible(true);
    }

    private void confirmBooking(Booking b) {
        DatabaseManager.getInstance().updateBookingStatus(
                b.getId(),
                Booking.STATUS_CONFIRMED
        );
        showNotification("Бронь №" + b.getId() + " успешно подтверждена");
        loadData();
    }

    private void markPaid(Booking b) {
        DatabaseManager.getInstance().updateBookingStatus(
                b.getId(),
                Booking.STATUS_PAID
        );
        showNotification(
                "Статус брони №" + b.getId() + " изменён на «Оплачен»"
        );
        loadData();
    }

    // Изящная капсульная контурная кнопка
    private JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Эффект Hover изменения фона
                if (getModel().isRollover()) {
                    g2d.setColor(
                            new Color(
                                    baseColor.getRed(),
                                    baseColor.getGreen(),
                                    baseColor.getBlue(),
                                    30
                            )
                    );
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                }

                g2d.setColor(baseColor);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(
                        new RoundRectangle2D.Float(
                                1,
                                1,
                                getWidth() - 3,
                                getHeight() - 3,
                                25,
                                25
                        )
                );
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(baseColor);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Центрированное минималистичное диалоговое окно уведомлений
    private void showNotification(String message) {
        JDialog dialog = new JDialog(mainFrame, "", true);
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2d.dispose();
            }
        };
        content.setBackground(CARD_BG);
        content.setOpaque(false);
        content.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundBorder(BORDER, 24),
                        new EmptyBorder(24, 32, 20, 32)
                )
        );

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msg.setForeground(TEXT);
        content.add(msg, BorderLayout.CENTER);

        // Капсульная кнопка подтверждения внутри уведомления
        JButton okBtn = new JButton("Прекрасно") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        okBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        okBtn.setForeground(Color.WHITE);
        okBtn.setBackground(TEXT);
        okBtn.setContentAreaFilled(false);
        okBtn.setBorderPainted(false);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(new EmptyBorder(8, 24, 8, 24));
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(okBtn);
        content.add(btnRow, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    // Класс для прорисовки плавных границ компонентов
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
            g2d.draw(
                    new RoundRectangle2D.Float(
                            x + 1,
                            y + 1,
                            width - 3,
                            height - 3,
                            radii,
                            radii
                    )
            );
            g2d.dispose();
        }
    }
}