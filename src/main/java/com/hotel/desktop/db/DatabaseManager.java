package com.hotel.desktop.db;

import com.hotel.desktop.model.Booking;
import com.hotel.desktop.model.Room;
import com.hotel.desktop.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String H2_URL = "jdbc:h2:~/hotel_data;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(H2_URL, "sa", "");
            createTables();
            seedDefaultUsers();
            seedRooms();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot connect to database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    login VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    phone VARCHAR(255),
                    full_name VARCHAR(255),
                    email VARCHAR(255) UNIQUE,
                    role VARCHAR(255) DEFAULT 'ROLE_USER',
                    enabled BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    capacity INTEGER NOT NULL,
                    comfort_level VARCHAR(255) NOT NULL,
                    price_per_night DECIMAL(10,2) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bookings (
                    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    room_id BIGINT NOT NULL,
                    check_in_date DATE,
                    check_out_date DATE,
                    status VARCHAR(255) NOT NULL DEFAULT 'не подтверждён',
                    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
                )
            """);
        }
    }

    private void seedDefaultUsers() {
        try {
            if (!existsByLogin("admin")) {
                String sql = "INSERT INTO users (login, password, full_name, email, phone, role, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "admin");
                    ps.setString(2, BCrypt.hashpw("Admin123!", BCrypt.gensalt()));
                    ps.setString(3, "Administrator");
                    ps.setString(4, "admin@hotel.com");
                    ps.setString(5, "+79999999999");
                    ps.setString(6, User.ROLE_ADMIN);
                    ps.setBoolean(7, true);
                    ps.executeUpdate();
                }
            }
            if (!existsByLogin("manager")) {
                String sql = "INSERT INTO users (login, password, full_name, email, phone, role, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "manager");
                    ps.setString(2, BCrypt.hashpw("Manager123!", BCrypt.gensalt()));
                    ps.setString(3, "Manager");
                    ps.setString(4, "manager@hotel.com");
                    ps.setString(5, "+78888888888");
                    ps.setString(6, User.ROLE_MANAGER);
                    ps.setBoolean(7, true);
                    ps.executeUpdate();
                }
            }
            if (!existsByLogin("client")) {
                String sql = "INSERT INTO users (login, password, full_name, email, phone, role, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, "client");
                    ps.setString(2, BCrypt.hashpw("Client123!", BCrypt.gensalt()));
                    ps.setString(3, "Client");
                    ps.setString(4, "client@hotel.com");
                    ps.setString(5, "+77777777777");
                    ps.setString(6, User.ROLE_USER);
                    ps.setBoolean(7, true);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seedRooms() {
        try {
            String check = "SELECT COUNT(*) FROM rooms";
            try (PreparedStatement ps = connection.prepareStatement(check)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) return;
            }

            String[][] data = {
                    {"1", "Стандарт", "2500"}, {"2", "Стандарт", "3000"},
                    {"2", "Комфорт", "4500"}, {"3", "Комфорт", "5500"},
                    {"2", "Люкс", "8500"}, {"4", "Люкс", "12000"},
                    {"6", "Семейный", "7000"}, {"3", "Стандарт", "3500"},
                    {"1", "Эконом", "1500"}, {"2", "Эконом", "2000"}
            };
            String sql = "INSERT INTO rooms (capacity, comfort_level, price_per_night) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (String[] row : data) {
                    ps.setInt(1, Integer.parseInt(row[0]));
                    ps.setString(2, row[1]);
                    ps.setBigDecimal(3, new BigDecimal(row[2]));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isRoomBooked(long roomId) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND status != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            ps.setString(2, Booking.STATUS_CANCELLED);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setId(rs.getLong("room_id"));
                r.setCapacity(rs.getInt("capacity"));
                r.setComfortLevel(rs.getString("comfort_level"));
                r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                r.setBooked(isRoomBooked(r.getId()));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = """
            SELECT b.*, u.full_name AS user_name, u.phone AS user_phone, u.email AS user_email,
                   r.comfort_level || ' №' || r.room_id AS room_info
            FROM bookings b
            JOIN users u ON u.user_id = b.user_id
            JOIN rooms r ON r.room_id = b.room_id
            ORDER BY b.booking_date DESC
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateBookingStatus(long bookingId, String newStatus) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update booking status", e);
        }
    }

    // ИСПРАВЛЕННЫЙ МЕТОД updateUser
    public void updateUser(User user) {
        String sql = "UPDATE users SET login=?, email=?, phone=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setLong(4, user.getId());
            int rows = ps.executeUpdate();
            System.out.println("Обновлено строк: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user: " + e.getMessage());
        }
    }

    // Отдельный метод для смены пароля
    public void updateUserPassword(long userId, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            ps.setLong(2, userId);
            ps.executeUpdate();
            System.out.println("Пароль обновлён");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update password: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getLong("booking_id"));
        b.setUserId(rs.getLong("user_id"));
        b.setRoomId(rs.getLong("room_id"));
        b.setUserName(rs.getString("user_name"));
        b.setUserPhone(rs.getString("user_phone"));
        b.setUserEmail(rs.getString("user_email"));
        b.setRoomInfo(rs.getString("room_info"));
        Date ci = rs.getDate("check_in_date");
        if (ci != null) b.setCheckInDate(ci.toLocalDate());
        Date co = rs.getDate("check_out_date");
        if (co != null) b.setCheckOutDate(co.toLocalDate());
        b.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("booking_date");
        if (ts != null) b.setBookingDate(ts.toLocalDateTime());
        return b;
    }

    public User findByLogin(String login) {
        return findUserBy("login", login);
    }

    public User findByEmail(String email) {
        return findUserBy("email", email);
    }

    public User findByPhone(String phone) {
        return findUserBy("phone", phone);
    }

    private User findUserBy(String field, String value) {
        String sql = "SELECT * FROM users WHERE " + field + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByCredential(String credential) {
        User user = findByLogin(credential);
        if (user != null) return user;
        user = findByEmail(credential);
        if (user != null) return user;
        return findByPhone(credential.replaceAll("[\\s-]", ""));
    }

    public boolean existsByLogin(String login) {
        return findByLogin(login) != null;
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    public boolean existsByPhone(String phone) {
        return findByPhone(phone) != null;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (login, password, phone, full_name, email, role, enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRole() != null ? user.getRole() : User.ROLE_USER);
            ps.setBoolean(7, user.isEnabled());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
            user.setCreatedAt(LocalDateTime.now());
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public User authenticate(String credential, String password) {
        User user = findByCredential(credential);
        if (user == null) throw new RuntimeException("Пользователь не найден");
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Неверно введен Логин/Пароль");
        }
        return user;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setEnabled(rs.getBoolean("enabled"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) user.setCreatedAt(ts.toLocalDateTime());
        return user;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}