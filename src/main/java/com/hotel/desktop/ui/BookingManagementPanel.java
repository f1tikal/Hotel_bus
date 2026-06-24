package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.Booking;
import com.hotel.desktop.model.User;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
        int row = table.getSelectedRow();
        if (row < 0) {
            JLabel hint = new JLabel(
                "Выберите бронирование из таблицы для управления статусом"
            );
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            hint.setForeground(TEXT_SEC);
            actionPanel.add(hint);
            actionPanel.revalidate();
            actionPanel.repaint();
            return;
        }

        Booking b = bookings.get(row);

        if (b.canConfirm()) {
            JButton confirmBtn = createStyledButton("Подтвердить", GREEN);
            confirmBtn.addActionListener(e -> confirmBooking(b));
            actionPanel.add(confirmBtn);
        }

        if (b.canMarkPaid() && currentUser.isAdmin()) {
            JButton paidBtn = createStyledButton("Отметить оплату", BLUE);
            paidBtn.addActionListener(e -> markPaid(b));
            actionPanel.add(paidBtn);
        }

        if (b.canMarkPaid() && !currentUser.isAdmin()) {
            JLabel restricted = new JLabel(
                "<html><span style='color:#D98E8E; font-size:12px; font-weight:500;'>" +
                    "• Недостаточно прав для отметки оплаты (требуется Администратор)</span></html>"
            );
            actionPanel.add(restricted);
        }

        if (actionPanel.getComponentCount() == 0) {
            JLabel done = new JLabel(
                "Для выбранного бронирования нет доступных действий"
            );
            done.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            done.setForeground(TEXT_SEC);
            actionPanel.add(done);
        }

        actionPanel.revalidate();
        actionPanel.repaint();
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
