package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;
import com.hotel.desktop.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class RegisterPanel extends JPanel {
    private static final Color BG = new Color(0xF0, 0xF0, 0xF0);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x33, 0x33, 0x33);
    private static final Color TEXT_SEC = new Color(0x77, 0x77, 0x77);
    private static final Color BORDER = new Color(0xE0, 0xE0, 0xE0);
    private static final Color ERROR = new Color(0xC0, 0x39, 0x2B);

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
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(28, 40, 28, 40)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 3, 0);

        JLabel title = new JLabel("Регистрация", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR);
        errorLabel.setVisible(false);
        c.gridy = 1;
        card.add(errorLabel, c);

        fieldMap = new java.util.LinkedHashMap<>();

        c.gridwidth = 1;
        int row = 2;

        row = addField(card, "Фамилия *", c, row, lastNameField = new JTextField(14));
        row = addField(card, "Имя *", c, row, firstNameField = new JTextField(14));
        row = addField(card, "Отчество", c, row, middleNameField = new JTextField(14));

        c.gridwidth = 2;
        row = addField(card, "Логин *", c, row, loginField = new JTextField(14));
        row = addField(card, "Почта *", c, row, emailField = new JTextField(14));

        c.gridwidth = 1;
        row = addField(card, "Телефон *", c, row, phoneField = new JTextField(14));
        row = addField(card, "Пароль *", c, row, passwordField = new JPasswordField(14));
        row = addField(card, "Подтвердите пароль *", c, row, confirmPasswordField = new JPasswordField(14));

        c.gridwidth = 2;
        JButton registerBtn = new JButton("Зарегистрироваться");
        registerBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        registerBtn.setForeground(CARD_BG);
        registerBtn.setBackground(TEXT);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(new EmptyBorder(10, 10, 10, 10));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> doRegister());
        c.gridy = row++; c.insets = new Insets(12, 0, 4, 0);
        card.add(registerBtn, c);

        JLabel loginLink = new JLabel("<html><u>Уже есть аккаунт? Войти</u></html>", SwingConstants.CENTER);
        loginLink.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLink.setForeground(TEXT_SEC);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showLogin();
            }
        });
        c.gridy = row++; c.insets = new Insets(4, 0, 2, 0);
        card.add(loginLink, c);

        JLabel policyLabel = new JLabel(
            "<html><div style='text-align:center;font-size:10px;color:#999;'>"
            + "Нажимая «Зарегистрироваться», вы соглашаетесь с "
            + "<span style='color:#555;'>Политикой конфиденциальности</span></div></html>",
            SwingConstants.CENTER);
        c.gridy = row; c.insets = new Insets(8, 0, 0, 0);
        card.add(policyLabel, c);

        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(CARD_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 60, 5, 60);
        add(scrollPane);
    }

    private int addField(JPanel panel, String labelText, GridBagConstraints c, int row, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(TEXT_SEC);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        panel.add(label, c);

        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(7, 10, 7, 10)
        ));
        c.gridy = row + 1;
        panel.add(field, c);

        String key = labelText.replace(" *", "").toLowerCase();
        fieldMap.put(key, field);
        return row + 2;
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
                lastName, firstName, middleName,
                email, phone, login,
                password, confirmPassword
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
            StringBuilder sb = new StringBuilder("<html>");
            for (String err : errors) {
                sb.append("&bull; ").append(err).append("<br>");
            }
            sb.append("</html>");
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
            user.setPhone(phone.replaceAll("[\\s-]", ""));
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(User.ROLE_USER);
            user.setEnabled(true);

            DatabaseManager.getInstance().save(user);

            showSuccess("Регистрация прошла успешно! Теперь вы можете войти в систему.");
            mainFrame.showLogin();
        } catch (Exception e) {
            showError("Ошибка при регистрации: " + e.getMessage());
        }
    }

    private void resetFieldStyles() {
        for (JComponent field : fieldMap.values()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 10, 7, 10)
            ));
        }
    }

    private void highlightErrors(List<String> errors) {
        String all = String.join(" ", errors).toLowerCase();

        for (Map.Entry<String, JComponent> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            JComponent field = entry.getValue();

            if (key.equals("пароль") || key.equals("подтвердите пароль")) {
                if (all.contains("парол")) {
                    field.setBorder(BorderFactory.createLineBorder(ERROR, 1));
                }
            } else if (all.contains(key)) {
                field.setBorder(BorderFactory.createLineBorder(ERROR, 1));
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        JDialog dialog = new JDialog(mainFrame, "", true);
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(new Color(0xF9, 0xF9, 0xF9));
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
        btnRow.setBackground(new Color(0xF9, 0xF9, 0xF9));
        btnRow.add(okBtn);
        content.add(btnRow, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
}
