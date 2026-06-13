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

public class NewBookingsPanel extends JPanel {

    private static final Color BG = new Color(0xE3, 0xEE, 0xF2);
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6);
    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6);
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6);
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF);
    private static final Color ACCENT = new Color(0x99, 0xC4, 0xC7);
    private static final Color ACCENT_HOVER = new Color(0x83, 0xB0, 0xB3);

    private final MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel tableModel;

    public NewBookingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Новые бронирования");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"№ Брони", "Клиент", "Телефон", "Email", "Номер комнаты", "Дата заезда", "Дата выезда"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_SEC);
        table.setGridColor(BORDER);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, renderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton approveBtn = createStyledButton("Подтвердить бронь");
        approveBtn.addActionListener(e -> processStatus("подтверждён"));
        actionRow.add(approveBtn);

        JButton cancelBtn = createStyledButton("Отменить бронь");
        cancelBtn.addActionListener(e -> processStatus("отменён"));
        actionRow.add(cancelBtn);

        card.add(actionRow, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Booking> list = DatabaseManager.getInstance().getPendingBookings();
        for (Booking b : list) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getUserName(), b.getUserPhone(), b.getUserEmail(),
                    b.getRoomInfo(), b.getCheckInDate(), b.getCheckOutDate()
            });
        }
    }

    private void processStatus(String status) {
        int row = table.getSelectedRow();
        if (row == -1) {
            showNotice("Пожалуйста, выберите бронирование из таблицы.");
            return;
        }
        long id = (long) tableModel.getValueAt(row, 0);
        DatabaseManager.getInstance().updateBookingStatus(id, status);
        refreshData();
        showNotice("Статус брони №" + id + " успешно изменен на: " + status);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    private void showNotice(String msg) {
        JDialog dialog = new JDialog(mainFrame, "Уведомление", true);
        dialog.setResizable(false);
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel text = new JLabel("<html><div style='text-align: center; width: 250px;'>" + msg + "</div></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(TEXT);
        content.add(text, BorderLayout.CENTER);

        JButton okBtn = createStyledButton("OK");
        okBtn.addActionListener(e -> dialog.dispose());
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        row.setOpaque(false);
        row.add(okBtn);
        content.add(row, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radii;
        RoundBorder(Color color, int radii) { this.color = color; this.radii = radii; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getBackground());
            g2d.fill(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radii, radii));
            g2d.setColor(color);
            g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radii, radii));
            g2d.dispose();
        }
    }
}