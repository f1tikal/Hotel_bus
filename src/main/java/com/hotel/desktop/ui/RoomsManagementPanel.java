package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.Room;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class RoomsManagementPanel extends JPanel {

    // Цветовая гамма
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

    public RoomsManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Основная подложка-карточка
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

        // Заголовок панели
        JLabel title = new JLabel("Управление каталогом номеров");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(title, BorderLayout.NORTH);

        // Настройка таблицы номеров
        String[] cols = {"ID Номера", "Вместимость (чел.)", "Комфорт", "Цена за ночь"};
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

        // Блок кнопок действий
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionRow.setOpaque(false);
        actionRow.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Кнопка добавления
        JButton addBtn = createStyledButton("Добавить номер");
        addBtn.addActionListener(e -> showRoomForm(null));
        actionRow.add(addBtn);

        // Кнопка редактирования
        JButton editBtn = createStyledButton("Редактировать");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showNotice("Выберите номер для редактирования.");
                return;
            }
            long id = (long) tableModel.getValueAt(row, 0);
            Room room = new Room();
            room.setId(id);
            room.setCapacity((int) tableModel.getValueAt(row, 1));
            room.setComfortLevel((String) tableModel.getValueAt(row, 2));
            room.setPricePerNight((BigDecimal) tableModel.getValueAt(row, 3));
            showRoomForm(room);
        });
        actionRow.add(editBtn);

        // Кнопка удаления
        JButton deleteBtn = createStyledButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showNotice("Выберите номер для удаления.");
                return;
            }
            long id = (long) tableModel.getValueAt(row, 0);
            try {
                DatabaseManager.getInstance().deleteRoom(id);
                loadData();
                showNotice("Номер успешно удален.");
            } catch (Exception ex) {
                showNotice("Ошибка удаления. Возможно, номер забронирован.");
            }
        });
        actionRow.add(deleteBtn);

        card.add(actionRow, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        loadData();
    }

    // Загрузка данных из БД в таблицу
    public void loadData() {
        tableModel.setRowCount(0);
        List<Room> list = DatabaseManager.getInstance().getAllRooms();
        for (Room r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getCapacity(), r.getComfortLevel(), r.getPricePerNight()
            });
        }
    }

    // Окно формы создания/редактирования номера
    private void showRoomForm(Room existingRoom) {
        JDialog dialog = new JDialog(mainFrame, existingRoom == null ? "Добавить номер" : "Редактировать номер", true);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Поля ввода
        JLabel capLabel = new JLabel("Вместимость:");
        capLabel.setForeground(TEXT);
        JTextField capField = new JTextField(existingRoom == null ? "" : String.valueOf(existingRoom.getCapacity()), 15);

        JLabel comLabel = new JLabel("Комфорт:");
        comLabel.setForeground(TEXT);
        String[] levels = {"Эконом", "Стандарт", "Комфорт", "Люкс", "Семейный"};
        JComboBox<String> comBox = new JComboBox<>(levels);
        if (existingRoom != null) {
            comBox.setSelectedItem(existingRoom.getComfortLevel());
        }

        JLabel priceLabel = new JLabel("Цена за ночь:");
        priceLabel.setForeground(TEXT);
        JTextField priceField = new JTextField(existingRoom == null ? "" : existingRoom.getPricePerNight().toString(), 15);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(capLabel, gbc);
        gbc.gridx = 1; panel.add(capField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(comLabel, gbc);
        gbc.gridx = 1; panel.add(comBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(priceLabel, gbc);
        gbc.gridx = 1; panel.add(priceField, gbc);

        // Кнопка сохранения в БД
        JButton saveBtn = createStyledButton("Сохранить");
        saveBtn.addActionListener(e -> {
            try {
                int capacity = Integer.parseInt(capField.getText().trim());
                String comfort = (String) comBox.getSelectedItem();
                BigDecimal price = new BigDecimal(priceField.getText().trim());

                Room r = new Room();
                r.setCapacity(capacity);
                r.setComfortLevel(comfort);
                r.setPricePerNight(price);

                if (existingRoom == null) {
                    DatabaseManager.getInstance().addRoom(r);
                } else {
                    r.setId(existingRoom.getId());
                    DatabaseManager.getInstance().updateRoom(r);
                }
                dialog.dispose();
                loadData();
                showNotice("Данные успешно сохранены.");
            } catch (Exception ex) {
                showNotice("Заполните поля корректно.");
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 0, 8);
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    // Создание стилизованной кнопки
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

    // Окно всплывающего уведомления
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

    // Отрисовка скругленных границ кнопок
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