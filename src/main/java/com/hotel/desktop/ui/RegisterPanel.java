package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;
import com.hotel.desktop.util.Validator;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class RegisterPanel extends JPanel {

    // Пастельно-бирюзовая цветовая гамма
    private static final Color BG = new Color(0xE3, 0xEE, 0xF2); // Мягкий фон за карточками
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6); // Молочно-белый фон для контента
    private static final Color TEXT = new Color(0x7D, 0xA2, 0xA6); // Основной бирюзовый тон для текста
    private static final Color TEXT_SEC = new Color(0x9A, 0xB3, 0xB6); // Вспомогательный светло-бирюзовый
    private static final Color BORDER = new Color(0xC2, 0xDC, 0xDF); // Мягкие границы
    private static final Color ERROR = new Color(0xD9, 0x8E, 0x8E); // Пастельный нежно-красный для ошибок
    private static final Color ACCENT = new Color(0x99, 0xC4, 0xC7); // Фирменный бирюзовый акцент
    private static final Color ACCENT_HOVER = new Color(0x83, 0xB0, 0xB3);
    private static final Color SUCCESS_BG = new Color(0xED, 0xF6, 0xF4); // Мягкий фон успеха

    private final MainFrame mainFrame;
    private JTextField lastNameField, firstNameField, middleNameField;
    private JTextField emailField, phoneField, loginField;
    private JPasswordField passwordField, confirmPasswordField;
    private JLabel errorLabel;
    private Map<String, JComponent> fieldMap;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG);
        initComponents();
    }

    private void initComponents() {
        // Карточка с честными скруглениями краев
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
        c.insets = new Insets(4, 0, 4, 0);

        // Заголовок в стиле референса
        JLabel title = new JLabel("Регистрация", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.PLAIN, 26));
        title.setForeground(TEXT);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(title, c);

        // Метка ошибки
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR);
        errorLabel.setVisible(false);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 12, 0);
        card.add(errorLabel, c);

        fieldMap = new java.util.LinkedHashMap<>();

        c.gridwidth = 1;
        int row = 2;

        // Поля ввода ФИО
        row = addField(
            card,
            "Фамилия *",
            c,
            row,
            lastNameField = new JTextField(16)
        );
        row = addField(
            card,
            "Имя *",
            c,
            row,
            firstNameField = new JTextField(16)
        );
        row = addField(
            card,
            "Отчество",
            c,
            row,
            middleNameField = new JTextField(16)
        );

        // Контактные данные
        c.gridwidth = 2;
        row = addField(
            card,
            "Логин *",
            c,
            row,
            loginField = new JTextField(16)
        );
        row = addField(
            card,
            "Email *",
            c,
            row,
            emailField = new JTextField(16)
        );

        c.gridwidth = 1;
        row = addField(
            card,
            "Телефон *",
            c,
            row,
            phoneField = new JTextField(16)
        );
        row = addField(
            card,
            "Пароль *",
            c,
            row,
            passwordField = new JPasswordField(16)
        );
        row = addField(
            card,
            "Подтвердите пароль *",
            c,
            row,
            confirmPasswordField = new JPasswordField(16)
        );

        // Кнопка регистрации (капсульная)
        c.gridwidth = 2;
        JButton registerBtn = createPrimaryButton("Зарегистрироваться");
        registerBtn.addActionListener(e -> doRegister());
        c.gridy = row++;
        c.insets = new Insets(24, 0, 14, 0);
        card.add(registerBtn, c);

        // Ссылка на вход
        JLabel loginLink = new JLabel(
            "<html><div style='text-align:center;'>Уже есть аккаунт? <span style='color:#7DA2A6; font-weight:bold;'>Войти</span></div></html>",
            SwingConstants.CENTER
        );
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setForeground(TEXT_SEC);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(
            new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    mainFrame.showLogin();
                }
            }
        );
        c.gridy = row++;
        c.insets = new Insets(4, 0, 10, 0);
        card.add(loginLink, c);

        // Политика конфиденциальности
        JLabel policyLabel = new JLabel(
            "<html><div style='text-align:center;font-size:11px;color:#9AB3B6;'>" +
                "Нажимая «Зарегистрироваться», вы соглашаетесь с " +
                "<span style='color:#7DA2A6; font-weight:bold;'>Политикой конфиденциальности</span></div></html>",
            SwingConstants.CENTER
        );
        c.gridy = row;
        c.insets = new Insets(8, 0, 0, 0);
        card.add(policyLabel, c);

        // Скролл-панель без рамок
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0)); // Скрываем дефолтный толстый скроллбар
        scrollPane.getViewport().setBackground(BG);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 20, 20, 20);
        add(scrollPane, gbc);
    }

    private int addField(
        JPanel panel,
        String labelText,
        GridBagConstraints c,
        int row,
        JComponent field
    ) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT);
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.insets = new Insets(8, 4, 3, 4);
        panel.add(label, c);

        styleField(field);
        c.gridy = row + 1;
        c.insets = new Insets(2, 0, 6, 0);
        panel.add(field, c);

        String key = labelText.replace(" *", "").toLowerCase();
        fieldMap.put(key, field);
        return row + 2;
    }

    private void styleField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setOpaque(false); // Для корректного скругления кастомного RoundBorder

        setFieldNormalBorder(field);
    }

    private void setFieldNormalBorder(JComponent field) {
        field.setBorder(
            BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER, 20),
                new EmptyBorder(10, 14, 10, 14)
            )
        );
    }

    private void setFieldErrorBorder(JComponent field) {
        field.setBorder(
            BorderFactory.createCompoundBorder(
                new RoundBorder(ERROR, 20),
                new EmptyBorder(10, 14, 10, 14)
            )
        );
    }

    // Кастомная капсульная кнопка со скруглением краев
    private JButton createPrimaryButton(String text) {
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
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(12, 10, 12, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        return btn;
    }

    private void doRegister() {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        errorLabel.setVisible(false);
        resetFieldStyles();

        List<String> errors = Validator.validateRegister(
            lastName,
            firstName,
            middleName,
            email,
            phone,
            login,
            password,
            confirmPassword
        );

        if (DatabaseManager.getInstance().existsByEmail(email)) {
            errors.add("Данная почта уже используется!");
        }
        if (DatabaseManager.getInstance().existsByLogin(login)) {
            errors.add("Данный логин уже используется!");
        }
        if (DatabaseManager.getInstance().existsByPhone(phone)) {
            errors.add("Данный телефон уже используется!");
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                "<html><div style='text-align:center;'>"
            );
            for (String err : errors) {
                sb.append("• ").append(err).append("<br>");
            }
            sb.append("</div></html>");
            errorLabel.setText(sb.toString());
            errorLabel.setVisible(true);
            highlightErrors(errors);
            return;
        }

        try {
            User user = new User();
            user.setLastName(lastName);
            user.setFirstName(firstName);
            if (!middleName.isEmpty()) user.setMiddleName(middleName);
            user.setEmail(email);
            user.setPhone(phone.replaceAll("[\\s-()]", ""));
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(User.ROLE_USER);
            user.setEnabled(true);

            DatabaseManager.getInstance().save(user);

            showSuccessDialog(
                "Регистрация прошла успешно! Теперь вы можете войти."
            );
            mainFrame.showLogin();
        } catch (Exception e) {
            showError("Ошибка при регистрации: " + e.getMessage());
        }
    }

    private void resetFieldStyles() {
        for (JComponent field : fieldMap.values()) {
            setFieldNormalBorder(field);
        }
    }

    private void highlightErrors(List<String> errors) {
        String all = String.join(" ", errors).toLowerCase();

        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            JComponent field = entry.getValue();

            if (key.equals("пароль") || key.equals("подтвердите пароль")) {
                if (all.contains("парол")) {
                    setFieldErrorBorder(field);
                }
            } else if (all.contains(key)) {
                setFieldErrorBorder(field);
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(
            "<html><div style='text-align:center;'>" + message + "</div></html>"
        );
        errorLabel.setVisible(true);
    }

    // Круглое кастомное модальное окно успешной регистрации
    private void showSuccessDialog(String message) {
        JDialog dialog = new JDialog(mainFrame, "", true);
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
        content.setBackground(SUCCESS_BG);
        content.setOpaque(false);
        content.setBorder(
            BorderFactory.createCompoundBorder(
                new RoundBorder(ACCENT, 30),
                new EmptyBorder(25, 35, 25, 35)
            )
        );

        // Сглаженная иконка галочки в круге
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(ACCENT);
                g2d.fillOval(0, 0, 42, 42);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth("✓");
                int textHeight = fm.getAscent();
                g2d.drawString(
                    "✓",
                    (42 - textWidth) / 2,
                    (42 + textHeight) / 2 - 2
                );
                g2d.dispose();
            }
        };
        iconPanel.setPreferredSize(new Dimension(42, 42));
        iconPanel.setOpaque(false);

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconWrap.setOpaque(false);
        iconWrap.add(iconPanel);
        content.add(iconWrap, BorderLayout.NORTH);

        JLabel msg = new JLabel(message, SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msg.setForeground(TEXT);
        content.add(msg, BorderLayout.CENTER);

        JButton okBtn = createPrimaryButton("OK");
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

    // Вспомогательный класс для отрисовки мягких закругленных контуров полей ввода
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
