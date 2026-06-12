package com.hotel.desktop.ui;

import com.hotel.desktop.db.DatabaseManager;
import com.hotel.desktop.model.User;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends JPanel {

    // Цветовая гамма, точно соответствующая скриншотам
    private static final Color BG_COLOR = new Color(0xE3, 0xEE, 0xF2); // Имитация светлого фона за карточкой
    private static final Color CARD_BG = new Color(0xF7, 0xF9, 0xF6); // Молочно-белый цвет самой карточки
    private static final Color TEXT_MAIN = new Color(0x7D, 0xA2, 0xA6); // Приглушенный бирюзовый для заголовков
    private static final Color TEXT_HINT = new Color(0x9A, 0xB3, 0xB6); // Светло-бирюзовый для подсказок (placeholder)
    private static final Color COMPONENT_BORDER = new Color(0xA9, 0xCE, 0xD1); // Аквамариновая рамка полей
    private static final Color BUTTON_BG = new Color(0x99, 0xC4, 0xC7); // Заливка кнопки "Войти"
    private static final Color BUTTON_HOVER = new Color(0x83, 0xB0, 0xB3); // Эффект при наведении
    private static final Color ERROR = new Color(0xD3, 0x2F, 0x2F);

    private final MainFrame mainFrame;
    private JTextField credentialField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(BG_COLOR);
        initComponents();
    }

    private void initComponents() {
        // Главная карточка с закругленными углами
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                // Отрисовка мягкого закругленного прямоугольника карточки (радиус 40)
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2d.dispose();
            }
        };
        card.setBackground(CARD_BG);
        card.setOpaque(false); // Чтобы закругленные углы пропускали фон родителя
        card.setBorder(new EmptyBorder(45, 50, 45, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER; // Все компоненты идут строго друг под другом
        c.weightx = 1.0;

        // 1. Иконка пользователя (Аватар сверху)
        JLabel avatarLabel = new JLabel(
            new ImageIcon() {
                @Override
                public void paintIcon(
                    Component component,
                    Graphics g,
                    int x,
                    int y
                ) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                    );
                    g2d.setColor(TEXT_MAIN);
                    g2d.setStroke(new BasicStroke(3));
                    // Рисуем кольцо и силуэт
                    g2d.drawOval(x + 10, y, 40, 40);
                    g2d.fillOval(x + 20, y + 10, 20, 20);
                    g2d.drawArc(x + 15, y + 28, 30, 20, 0, 180);
                    g2d.dispose();
                }

                @Override
                public int getIconWidth() {
                    return 60;
                }

                @Override
                public int getIconHeight() {
                    return 45;
                }
            },
            SwingConstants.CENTER
        );

        c.insets = new Insets(0, 0, 10, 0);
        card.add(avatarLabel, c);

        // 2. Заголовок "Вход в аккаунт" (Используем изящный шрифт Serif/Georgia, как на макете)
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

        // 3. Поле Email с внутренним текстом-подсказкой (Кастомный класс)
        credentialField = new HintTextField("Введите email");
        styleField(credentialField);
        c.insets = new Insets(0, 0, 15, 0);
        card.add(credentialField, c);

        // 4. Поле Пароль с внутренним текстом-подсказкой
        passwordField = new HintPasswordField("Введите пароль");
        styleField(passwordField);
        c.insets = new Insets(0, 0, 25, 0);
        card.add(passwordField, c);

        // 5. Кнопка "Войти" (Круглая, бирюзовая заливка, белый текст)
        JButton loginBtn = new JButton("Войти") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35); // Высокое закругление ("капсула")
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(BUTTON_BG);
        loginBtn.setFocusPainted(false);
        loginBtn.setContentAreaFilled(false); // Кастомная отрисовка фона
        loginBtn.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBorderPainted(false);

        loginBtn.addMouseListener(
            new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    loginBtn.setBackground(BUTTON_HOVER);
                }

                public void mouseExited(java.awt.event.MouseEvent e) {
                    loginBtn.setBackground(BUTTON_BG);
                }
            }
        );
        loginBtn.addActionListener(e -> doLogin());
        c.insets = new Insets(0, 0, 15, 0);
        card.add(loginBtn, c);

        // 6. Интерактивная ссылка "Зарегистрироваться, если нет аккаунта" (Как на втором фото)
        String linkText =
            "<html><span style='color:#9AB3B6;'>Зарегистрироваться,</span> " +
            "<span style='color:#9AB3B6; text-decoration: underline;'>если нет аккаунта</span></html>";
        JLabel registerLink = new JLabel(linkText, SwingConstants.CENTER);
        registerLink.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(
            new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    mainFrame.showRegister();
                }
            }
        );
        c.insets = new Insets(5, 0, 0, 0);
        card.add(registerLink, c);

        // Центрирование всей карточки на панели
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setOpaque(false); // Чтобы работал закругленный кастомный бордер

        // Устанавливаем закругленные рамки с внутренними отступами текста
        field.setBorder(
            BorderFactory.createCompoundBorder(
                new RoundBorder(COMPONENT_BORDER, 35),
                new EmptyBorder(10, 20, 10, 20)
            )
        );
    }

    private void doLogin() {
        String credential = credentialField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Сброс рамок к исходным аквамариновым
        styleField(credentialField);
        styleField(passwordField);
        errorLabel.setVisible(false);

        if (
            credential.isEmpty() ||
            credential.equals("Введите email") ||
            password.isEmpty()
        ) {
            showError("Заполните все поля");
            return;
        }

        try {
            User user = DatabaseManager.getInstance().authenticate(
                credential,
                password
            );
            mainFrame.showDashboard(user);
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        // Подсвечиваем ошибки закругленным красным бордером
        RoundBorder errorBorder = new RoundBorder(ERROR, 35);
        EmptyBorder padding = new EmptyBorder(10, 20, 10, 20);
        credentialField.setBorder(
            BorderFactory.createCompoundBorder(errorBorder, padding)
        );
        passwordField.setBorder(
            BorderFactory.createCompoundBorder(errorBorder, padding)
        );
    }

    // --- ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ДЛЯ СТИЛИЗАЦИИ И ЭФФЕКТОВ ---

    // Класс для создания гладких закругленных рамок компонентов
    private static class RoundBorder extends AbstractBorder {

        private final Color color;
        private final int thickness = 2;
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
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(
                x + 1,
                y + 1,
                width - 3,
                height - 3,
                radii,
                radii
            );
            g2d.dispose();
        }
    }

    // Поле с поддержкой плейсхолдера (Текст "Введите email" исчезает при фокусе)
    private static class HintTextField extends JTextField {

        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
            setForeground(TEXT_HINT);
            setText(hint);
            addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (getText().equals(hint)) {
                            setText("");
                            setForeground(TEXT_MAIN);
                        }
                    }

                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        if (getText().isEmpty()) {
                            setText(hint);
                            setForeground(TEXT_HINT);
                        }
                    }
                }
            );
        }

        @Override
        public String getText() {
            String typed = super.getText();
            return typed.equals(hint) ? "" : typed;
        }
    }

    // Поле пароля с поддержкой отображения плейсхолдера обычным текстом
    private static class HintPasswordField extends JPasswordField {

        private final String hint;
        private boolean isHint = true;

        HintPasswordField(String hint) {
            this.hint = hint;
            setEchoChar((char) 0); // Сначала показываем текст "Введите пароль"
            setForeground(TEXT_HINT);
            setText(hint);
            addFocusListener(
                new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) {
                        if (isHint) {
                            setText("");
                            setEchoChar('•'); // Переключаем на точки при вводе пароля
                            setForeground(TEXT_MAIN);
                            isHint = false;
                        }
                    }

                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        if (getPassword().length == 0) {
                            setEchoChar((char) 0);
                            setText(hint);
                            setForeground(TEXT_HINT);
                            isHint = true;
                        }
                    }
                }
            );
        }
    }
}
