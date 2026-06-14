package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends JPanel {

    // Цветовая гамма
    private static final Color BG_COLOR = new Color(0xE3, 0xEE, 0xF2);
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6);
    private static final Color TEXT_MAIN = new Color(0x7D, 0xA2, 0xA6);
    private static final Color TEXT_HINT = new Color(0x9A, 0xB3, 0xB6);
    private static final Color COMPONENT_BORDER = new Color(0xA9, 0xCE, 0xD1);
    private static final Color BUTTON_BG = new Color(0x99, 0xC4, 0xC7);
    private static final Color BUTTON_HOVER = new Color(0x83, 0xB0, 0xB3);
    private static final Color ERROR = new Color(0xD3, 0x2F, 0x2F);

    private final MainFrame mainFrame;
    private HintTextField loginField;
    private HintPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);
        initComponents();
    }

    private void initComponents() {
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
        card.setBorder(new EmptyBorder(45, 50, 45, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;

        // Аватар
        JLabel avatarLabel = new JLabel(
                new ImageIcon() {
                    @Override
                    public void paintIcon(Component component, Graphics g, int x, int y) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(TEXT_MAIN);
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawOval(x + 10, y, 40, 40);
                        g2d.fillOval(x + 20, y + 10, 20, 20);
                        g2d.drawArc(x + 15, y + 28, 30, 20, 0, 180);
                        g2d.dispose();
                    }
                    @Override
                    public int getIconWidth() { return 60; }
                    @Override
                    public int getIconHeight() { return 45; }
                },
                SwingConstants.CENTER
        );
        c.insets = new Insets(0, 0, 10, 0);
        card.add(avatarLabel, c);

        // Заголовок
        JLabel title = new JLabel("Вход в аккаунт", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.PLAIN, 28));
        title.setForeground(TEXT_MAIN);
        c.insets = new Insets(0, 0, 20, 0);
        card.add(title, c);

        // Метка ошибки
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ERROR);
        errorLabel.setVisible(false);
        c.insets = new Insets(0, 0, 10, 0);
        card.add(errorLabel, c);

        // Поле Логин с плейсхолдером
        loginField = new HintTextField("Введите логин");
        loginField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(COMPONENT_BORDER, 35),
                new EmptyBorder(12, 20, 12, 20)
        ));
        c.insets = new Insets(0, 0, 15, 0);
        card.add(loginField, c);

        // Поле Пароль с плейсхолдером
        passwordField = new HintPasswordField("Введите пароль");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(COMPONENT_BORDER, 35),
                new EmptyBorder(12, 20, 12, 20)
        ));
        c.insets = new Insets(0, 0, 25, 0);
        card.add(passwordField, c);

        // Кнопка входа
        JButton loginBtn = new JButton("Войти") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(BUTTON_BG);
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBorderPainted(false);
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { loginBtn.setBackground(BUTTON_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { loginBtn.setBackground(BUTTON_BG); }
        });
        loginBtn.addActionListener(e -> doLogin());
        c.insets = new Insets(0, 0, 15, 0);
        card.add(loginBtn, c);

        // Ссылка на регистрацию
        String linkText = "<html><span style='color:#9AB3B6;'>Зарегистрироваться,</span> " +
                "<span style='color:#9AB3B6; text-decoration: underline;'>если нет аккаунта</span></html>";
        JLabel registerLink = new JLabel(linkText, SwingConstants.CENTER);
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { mainFrame.showRegister(); }
        });
        c.insets = new Insets(5, 0, 0, 0);
        card.add(registerLink, c);

        // Ссылка "Забыли пароль или логин?"
        JLabel hintLink = new JLabel("<html><u>Забыли пароль или логин?</u></html>", SwingConstants.CENTER);
        hintLink.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLink.setForeground(TEXT_HINT);
        hintLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hintLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { showCredentialsHint(); }
        });
        c.insets = new Insets(8, 0, 0, 0);
        card.add(hintLink, c);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    private void doLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getPasswordText();

        loginField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(COMPONENT_BORDER, 35),
                new EmptyBorder(12, 20, 12, 20)
        ));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(COMPONENT_BORDER, 35),
                new EmptyBorder(12, 20, 12, 20)
        ));
        errorLabel.setVisible(false);

        if (login.isEmpty() || password.isEmpty()) {
            showError("Заполните все поля");
            return;
        }

        try {
            User user = DatabaseManager.getInstance().authenticate(login, password);

            JOptionPane.showMessageDialog(this,
                    "✅ Вход выполнен успешно!\n\n" +
                            "👤 Логин: " + user.getLogin() + "\n" +
                            "📧 Email: " + (user.getEmail() != null ? user.getEmail() : "не указан") + "\n" +
                            "🔑 Роль: " + (user.isAdmin() ? "Администратор" : user.isManager() ? "Менеджер" : "Клиент") + "\n" +
                            "🆔 ID: " + user.getId(),
                    "Добро пожаловать",
                    JOptionPane.INFORMATION_MESSAGE);

            mainFrame.showDashboard(user);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showCredentialsHint() {
        String[][] users = {
                {"admin", "Admin123!", "Администратор"},
                {"manager", "Manager123!", "Менеджер"},
                {"client", "Client123!", "Клиент"}
        };

        StringBuilder message = new StringBuilder("📋 Тестовые данные для входа:\n\n");
        for (String[] user : users) {
            message.append("👤 ").append(user[2]).append(":\n");
            message.append("   Логин: ").append(user[0]).append("\n");
            message.append("   Пароль: ").append(user[1]).append("\n\n");
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Подсказка", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        RoundBorder errorBorder = new RoundBorder(ERROR, 35);
        EmptyBorder padding = new EmptyBorder(12, 20, 12, 20);
        loginField.setBorder(BorderFactory.createCompoundBorder(errorBorder, padding));
        passwordField.setBorder(BorderFactory.createCompoundBorder(errorBorder, padding));
    }

    // Поле с плейсхолдером для логина (ИСПРАВЛЕНО)
    private static class HintTextField extends JTextField {
        private final String hint;
        private boolean showingHint = true;

        HintTextField(String hint) {
            this.hint = hint;
            setForeground(TEXT_HINT);
            setText(hint);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingHint) {
                        setText("");
                        setForeground(Color.BLACK);
                        showingHint = false;
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(hint);
                        setForeground(TEXT_HINT);
                        showingHint = true;
                    }
                }
            });
        }

        @Override
        public String getText() {
            return showingHint ? "" : super.getText();
        }
    }

    // Поле пароля с плейсхолдером
    private static class HintPasswordField extends JPasswordField {
        private final String hint;
        private boolean showingHint = true;

        HintPasswordField(String hint) {
            this.hint = hint;
            setEchoChar((char) 0);
            setForeground(TEXT_HINT);
            setText(hint);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (showingHint) {
                        setText("");
                        setEchoChar('•');
                        setForeground(Color.BLACK);
                        showingHint = false;
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setEchoChar((char) 0);
                        setText(hint);
                        setForeground(TEXT_HINT);
                        showingHint = true;
                    }
                }
            });
        }

        public String getPasswordText() {
            return showingHint ? "" : new String(getPassword());
        }
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
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radii, radii);
            g2d.dispose();
        }
    }
}