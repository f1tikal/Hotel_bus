package com.hotel.desktop.model;

import java.math.BigDecimal;

public class Room {
    private long id;
    private int capacity;
    private String comfortLevel;
    private BigDecimal pricePerNight;
    private boolean booked;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getComfortLevel() { return comfortLevel; }
    public void setComfortLevel(String comfortLevel) { this.comfortLevel = comfortLevel; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public boolean isBooked() { return booked; }
    public void setBooked(boolean booked) { this.booked = booked; }

    public String getStatusText() { return booked ? "Занят" : "Свободен"; }

    @Override
    public String toString() {
        return "№" + id + " — " + comfortLevel + " (" + capacity + " чел.) — " + pricePerNight + "₽/ночь";
    }
}
