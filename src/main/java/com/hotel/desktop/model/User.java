package com.hotel.desktop.model;

import java.time.LocalDateTime;

public class User {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_USER = "ROLE_USER";

    private long id;
    private String login;
    private String password;
    private String phone;
    private String fullName;
    private String email;
    private String role;
    private String lastName;
    private String firstName;
    private String middleName;
    private boolean enabled;
    private LocalDateTime createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() {
        if (fullName != null && !fullName.isBlank()) return fullName;
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName).append(" ");
        if (firstName != null) sb.append(firstName);
        if (middleName != null && !middleName.isBlank()) sb.append(" ").append(middleName);
        return sb.toString().trim();
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isManager() {
        return ROLE_MANAGER.equals(role);
    }

    public boolean isStaff() {
        return isAdmin() || isManager();
    }
}
