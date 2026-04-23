package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class RoomBooking {
    private static int idCounter = 500;
    private int bookingId;
    private String userId;
    private String roomId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;

    public RoomBooking(String userId, String roomId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        this.bookingId = ++idCounter;
        this.userId = userId;
        this.roomId = roomId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "CONFIRMED";
    }

    public RoomBooking(int bookingId, String userId, String roomId, String bookingDate, 
                       String startTime, String endTime, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.bookingDate = LocalDate.parse(bookingDate);
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.status = status;
        if (bookingId > idCounter) idCounter = bookingId;
    }

    public void cancel() { this.status = "CANCELLED"; }
    public void complete() { this.status = "COMPLETED"; }

    public int getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getRoomId() { return roomId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public boolean isActive() { return status.equals("CONFIRMED"); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoomBooking that = (RoomBooking) obj;
        return bookingId == that.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return bookingId + "," + userId + "," + roomId + "," + bookingDate + "," + startTime + "," + endTime + "," + status;
    }
}