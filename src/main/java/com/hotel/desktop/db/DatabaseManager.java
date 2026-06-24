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
    private static final String DB_TYPE = System.getProperty("db.type", "h2");
    private static final String H2_URL = "jdbc:h2:file:./hotel_data;DB_CLOSE_DELAY=-1";
    private static final String PG_URL = System.getProperty("db.url", "jdbc:postgresql://localhost:5432/hotel");
    private static final String PG_USER = System.getProperty("db.user", "postgres");
    private static final String PG_PASS = System.getProperty("db.pass", "postgres");

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            if ("postgresql".equals(DB_TYPE)) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(PG_URL, PG_USER, PG_PASS);
            } else {
                connection = DriverManager.getConnection(H2_URL, "sa", "");
            }
            createTables();
            seedDefaultUsers();
            seedRooms();
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private String serialType() {
        return "postgresql".equals(DB_TYPE) ? "SERIAL" : "BIGINT AUTO_INCREMENT";
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(String.format("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id %s PRIMARY KEY,
                    login VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    phone VARCHAR(255),
                    full_name VARCHAR(255),
                    email VARCHAR(255) UNIQUE,
                    role VARCHAR(255) DEFAULT 'ROLE_USER',
                    last_name VARCHAR(25),
                    first_name VARCHAR(20),
                    middle_name VARCHAR(30),
                    enabled BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """, serialType()));

            stmt.execute(String.format("""
                CREATE TABLE IF NOT EXISTS rooms (
                    room_id %s PRIMARY KEY,
                    capacity INTEGER NOT NULL,
                    comfort_level VARCHAR(255) NOT NULL,
                    price_per_night DECIMAL(10,2) NOT NULL
                )
            """, serialType()));

            stmt.execute(String.format("""
                CREATE TABLE IF NOT EXISTS bookings (
                    booking_id %s PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    room_id INTEGER NOT NULL,
                    check_in_date DATE,
                    check_out_date DATE,
                    status VARCHAR(255) NOT NULL DEFAULT 'не подтверждён',
                    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
                )
            """, serialType()));
        }
    }

    private void seedDefaultUsers() throws SQLException {
        if (!existsByLogin("admin")) {
            User u = new User(); u.setLogin("admin"); u.setPassword("Admin123!");
            u.setFullName("Administrator"); u.setEmail("admin@hotel.com");
            u.setPhone("+79999999999"); u.setRole(User.ROLE_ADMIN); u.setEnabled(true);
            save(u);
        }
        if (!existsByLogin("manager")) {
            User u = new User(); u.setLogin("manager"); u.setPassword("Manager123!");
            u.setFullName("Manager"); u.setEmail("manager@hotel.com");
            u.setPhone("+78888888888"); u.setRole(User.ROLE_MANAGER); u.setEnabled(true);
            save(u);
        }
        if (!existsByLogin("client")) {
            User u = new User(); u.setLogin("client"); u.setPassword("Client123!");
            u.setFullName("Client"); u.setEmail("client@hotel.com");
            u.setPhone("+77777777777"); u.setRole(User.ROLE_USER); u.setEnabled(true);
            save(u);
        }
    }

    private void seedRooms() throws SQLException {
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
    public List<Booking> getPendingBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name AS user_name, u.phone AS user_phone, u.email AS user_email, r.comfort_level || ' №' || r.room_id AS room_info FROM bookings b JOIN users u ON u.user_id = b.user_id JOIN rooms r ON r.room_id = b.room_id WHERE b.status = 'не подтверждён' ORDER BY b.booking_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapBooking(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (capacity, comfort_level, price_per_night) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, room.getCapacity());
            ps.setString(2, room.getComfortLevel());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET capacity = ?, comfort_level = ?, price_per_night = ? WHERE room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, room.getCapacity());
            ps.setString(2, room.getComfortLevel());
            ps.setBigDecimal(3, room.getPricePerNight());
            ps.setLong(4, room.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRoom(long roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    // ===== User methods (unchanged) =====

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
        String sql = "INSERT INTO users (login, password, phone, full_name, email, role, last_name, first_name, middle_name, enabled) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRole() != null ? user.getRole() : User.ROLE_USER);
            ps.setString(7, user.getLastName());
            ps.setString(8, user.getFirstName());
            ps.setString(9, user.getMiddleName());
            ps.setBoolean(10, user.isEnabled());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setId("postgresql".equals(DB_TYPE) ? keys.getLong("user_id") : keys.getLong(1));
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
        user.setLastName(rs.getString("last_name"));
        user.setFirstName(rs.getString("first_name"));
        user.setMiddleName(rs.getString("middle_name"));
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
