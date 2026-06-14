package com.hotel.desktop.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    public static final String STATUS_PENDING = "не подтверждён";
    public static final String STATUS_CONFIRMED = "подтверждён";
    public static final String STATUS_PAID = "оплачен";
    public static final String STATUS_CANCELLED = "отменён";

    private long id;
    private long userId;
    private long roomId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private String roomInfo;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private LocalDateTime bookingDate;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getRoomId() { return roomId; }
    public void setRoomId(long roomId) { this.roomId = roomId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRoomInfo() { return roomInfo; }
    public void setRoomInfo(String roomInfo) { this.roomInfo = roomInfo; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public boolean canConfirm() {
        return STATUS_PENDING.equals(status);
    }

    public boolean canMarkPaid() {
        return STATUS_CONFIRMED.equals(status);
    }
}
