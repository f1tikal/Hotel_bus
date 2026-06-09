package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.Booking;
import com.hotel.desktop.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingManagementPanel extends JPanel {
    private static final Color BG = new Color(0xF0, 0xF0, 0xF0);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x33, 0x33, 0x33);
    private static final Color TEXT_SEC = new Color(0x88, 0x88, 0x88);
    private static final Color BORDER = new Color(0xD0, 0xD0, 0xD0);
    private static final Color GREEN = new Color(0x27, 0xAE, 0x60);
    private static final Color ORANGE = new Color(0xE6, 0x7E, 0x22);
    private static final Color BLUE = new Color(0x29, 0x80, 0xB9);
    private static final Color GRAY = new Color(0x95, 0x95, 0x95);

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
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel title = new JLabel("Управление бронями");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        infoLabel = new JLabel();
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_SEC);
        header.add(infoLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
            "№ брони", "Клиент", "Телефон", "Email", "Номер", "Заезд", "Выезд", "Статус"
        }, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        table.setBorder(null);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
            .setBorder(new EmptyBorder(8, 10, 8, 10));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            protected void setValue(Object value) {
                super.setValue(value);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                setBackground(CARD_BG);
                setForeground(TEXT);
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateActionButtons();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        scroll.getViewport().setBackground(CARD_BG);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(CARD_BG);
        tableWrap.setBorder(new EmptyBorder(0, 20, 0, 20));
        tableWrap.add(scroll, BorderLayout.CENTER);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_BG);
        tableContainer.add(tableWrap, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setBackground(CARD_BG);
        actionPanel.setBorder(new EmptyBorder(12, 20, 16, 20));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(CARD_BG);
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        bookings = DatabaseManager.getInstance().getAllBookings();
        tableModel.setRowCount(0);

        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getUserName(),
                b.getUserPhone() != null ? b.getUserPhone() : "—",
                b.getUserEmail() != null ? b.getUserEmail() : "—",
                b.getRoomInfo(),
                b.getCheckInDate() != null ? b.getCheckInDate().toString() : "—",
                b.getCheckOutDate() != null ? b.getCheckOutDate().toString() : "—",
                formatStatus(b.getStatus())
            });
        }

        infoLabel.setText("Всего броней: " + bookings.size());
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
            JLabel hint = new JLabel("Выберите бронь из таблицы");
            hint.setFont(new Font("Arial", Font.PLAIN, 12));
            hint.setForeground(TEXT_SEC);
            actionPanel.add(hint);
            actionPanel.revalidate();
            actionPanel.repaint();
            return;
        }

        Booking b = bookings.get(row);

        if (b.canConfirm()) {
            JButton confirmBtn = actionButton("Подтвердить", GREEN);
            confirmBtn.addActionListener(e -> confirmBooking(b));
            actionPanel.add(confirmBtn);
        }

        if (b.canMarkPaid() && currentUser.isAdmin()) {
            JButton paidBtn = actionButton("Оплачен", BLUE);
            paidBtn.addActionListener(e -> markPaid(b));
            actionPanel.add(paidBtn);
        }

        if (b.canMarkPaid() && !currentUser.isAdmin()) {
            JLabel restricted = new JLabel(
                "<html><span style='color:#c0392b;font-size:11px;'>"
                + "У вашей роли нет прав на установку статуса «оплачен». "
                + "Только администратор может отметить оплату</span></html>");
            actionPanel.add(restricted);
        }

        if (actionPanel.getComponentCount() == 0) {
            JLabel done = new JLabel("Нет доступных действий для этой брони");
            done.setFont(new Font("Arial", Font.PLAIN, 12));
            done.setForeground(TEXT_SEC);
            actionPanel.add(done);
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void confirmBooking(Booking b) {
        DatabaseManager.getInstance().updateBookingStatus(b.getId(), Booking.STATUS_CONFIRMED);
        showNotification("Бронь №" + b.getId() + " подтверждена");
        loadData();
    }

    private void markPaid(Booking b) {
        DatabaseManager.getInstance().updateBookingStatus(b.getId(), Booking.STATUS_PAID);
        showNotification("Статус брони №" + b.getId() + " изменён на «оплачен»");
        loadData();
    }

    private JButton actionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setForeground(color);
        btn.setBackground(CARD_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            new EmptyBorder(7, 18, 7, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showNotification(String message) {
        JDialog dialog = new JDialog(mainFrame, "", true);
        dialog.setUndecorated(true);
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(24, 32, 24, 32)
        ));
        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Arial", Font.PLAIN, 13));
        msg.setForeground(TEXT);
        content.add(msg, BorderLayout.CENTER);
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
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
}
