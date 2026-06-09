package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private static final Color BG = new Color(0xF0, 0xF0, 0xF0);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT = new Color(0x33, 0x33, 0x33);
    private static final Color TEXT_SEC = new Color(0x77, 0x77, 0x77);
    private static final Color BORDER = new Color(0xE0, 0xE0, 0xE0);
    private static final Color ERROR = new Color(0xC0, 0x39, 0x2B);

    private final MainFrame mainFrame;
    private JTextField credentialField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
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
            new EmptyBorder(36, 40, 36, 40)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridwidth = 2;

        JLabel title = new JLabel("Вход в систему", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);
        c.gridx = 0; c.gridy = 0;
        card.add(title, c);

        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR);
        errorLabel.setVisible(false);
        c.gridy = 1;
        card.add(errorLabel, c);

        JLabel credLabel = new JLabel("Логин / Email / Телефон");
        credLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        credLabel.setForeground(TEXT_SEC);
        c.gridy = 2; c.insets = new Insets(12, 0, 2, 0);
        card.add(credLabel, c);

        credentialField = new JTextField(18);
        styleField(credentialField);
        c.gridy = 3; c.insets = new Insets(2, 0, 6, 0);
        card.add(credentialField, c);

        JLabel passLabel = new JLabel("Пароль");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        passLabel.setForeground(TEXT_SEC);
        c.gridy = 4; c.insets = new Insets(6, 0, 2, 0);
        card.add(passLabel, c);

        passwordField = new JPasswordField(18);
        styleField(passwordField);
        c.gridy = 5; c.insets = new Insets(2, 0, 6, 0);
        card.add(passwordField, c);

        JButton loginBtn = new JButton("Войти");
        loginBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        loginBtn.setForeground(CARD_BG);
        loginBtn.setBackground(TEXT);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(new EmptyBorder(10, 10, 10, 10));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());
        c.gridy = 6; c.insets = new Insets(12, 0, 4, 0);
        card.add(loginBtn, c);

        JLabel registerLink = new JLabel("<html><u>Зарегистрироваться</u></html>", SwingConstants.CENTER);
        registerLink.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLink.setForeground(TEXT_SEC);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showRegister();
            }
        });
        c.gridy = 7; c.insets = new Insets(4, 0, 2, 0);
        card.add(registerLink, c);

        JLabel policyLabel = new JLabel(
            "<html><div style='text-align:center;font-size:10px;color:#999;'>"
            + "Нажимая «Войти», вы соглашаетесь с "
            + "<span style='color:#555;'>Политикой конфиденциальности</span></div></html>",
            SwingConstants.CENTER);
        c.gridy = 8; c.insets = new Insets(8, 0, 0, 0);
        card.add(policyLabel, c);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 40, 5, 40);
        add(card);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private void doLogin() {
        String credential = credentialField.getText().trim();
        String password = new String(passwordField.getPassword());

        errorLabel.setVisible(false);
        credentialField.setBorder(BorderFactory.createLineBorder(BORDER));
        passwordField.setBorder(BorderFactory.createLineBorder(BORDER));

        if (credential.isEmpty() || password.isEmpty()) {
            showError("Заполните все поля");
            return;
        }

        try {
            User user = DatabaseManager.getInstance().authenticate(credential, password);
            mainFrame.showDashboard(user);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        credentialField.setBorder(BorderFactory.createLineBorder(ERROR, 1));
        passwordField.setBorder(BorderFactory.createLineBorder(ERROR, 1));
    }
}
