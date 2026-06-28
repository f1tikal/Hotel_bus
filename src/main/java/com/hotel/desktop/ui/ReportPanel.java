package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel {

    private static final Color BG = new Color(0xE3, 0xEE, 0xF2);
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6);
    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6);
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6);
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF);
    private static final Color BLUE = new Color(0x8E, 0xAB, 0xBC);

    private final MainFrame mainFrame;
    private final User currentUser;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private JLabel revenueLabel;
    private JLabel countLabel;

    public ReportPanel(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(BG);
        initComponents();
        loadReportData();
    }

    private void initComponents() {
        setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel mainCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2d.dispose();
            }
        };
        mainCard.setBackground(CARD_BG);
        mainCard.setOpaque(false);

        // Верхняя панель: Заголовок + Выбор даты
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 24, 16, 24));

        JLabel title = new JLabel("Отчёт за день");
        title.setFont(new Font("Georgia", Font.PLAIN, 20));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        // Блок фильтра даты
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Дата (ГГГГ-ММ-ДД):"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        filterPanel.add(dateField);

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.setBackground(BLUE);
        refreshBtn.addActionListener(e -> loadReportData());
        filterPanel.add(refreshBtn);

        header.add(filterPanel, BorderLayout.EAST);
        mainCard.add(header, BorderLayout.NORTH);

        // Таблица по ТЗ
        tableModel = new DefaultTableModel(
                new String[] { "Время", "Тип операции", "Комната", "Гость", "Ответственный менеджер", "Сумма (руб.)" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setBackground(CARD_BG);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(0xEB, 0xF2, 0xF2));
        table.setSelectionForeground(TEXT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_SEC);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                super.getTableCellRendererComponent(table, val, isSel, hasFoc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSel) {
                    setBackground(CARD_BG);
                    setForeground(TEXT);
                }
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CARD_BG);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setOpaque(false);
        tableWrap.setBorder(new EmptyBorder(0, 24, 0, 24));
        tableWrap.add(scroll, BorderLayout.CENTER);
        mainCard.add(tableWrap, BorderLayout.CENTER);

        // Нижняя панель: Сводные аналитические итоги за день
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(16, 24, 24, 24));

        countLabel = new JLabel("Заселено номеров: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(TEXT);

        revenueLabel = new JLabel("Итого выручка: 0.00 руб.");
        revenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        revenueLabel.setForeground(TEXT);

        summaryPanel.add(countLabel);
        summaryPanel.add(revenueLabel);
        mainCard.add(summaryPanel, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.CENTER);
    }

    public void loadReportData() {
        try {
            LocalDate selectedDate = LocalDate.parse(dateField.getText().trim());
            tableModel.setRowCount(0);

            List<Object[]> data = DatabaseManager.getInstance().getDailyReport(selectedDate);
            double totalRevenue = 0;

            for (Object[] row : data) {
                totalRevenue += (Double) row[5];
                tableModel.addRow(row);
            }

            countLabel.setText("Операций за день: " + data.size());
            revenueLabel.setText(String.format("Итого выручка: %.2f руб.", totalRevenue));

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты! Используйте ГГГГ-ММ-ДД");
        }
    }
}