package model;

import java.time.LocalDate;
import java.util.Objects;

public class Reservation {
    private static int idCounter = 100;
    private int reservationId;
    private String userId;
    private String isbn;
    private LocalDate reservationDate;
    private String status;

    public Reservation(String userId, String isbn) {
        this.reservationId = ++idCounter;
        this.userId = userId;
        this.isbn = isbn;
        this.reservationDate = LocalDate.now();
        this.status = "PENDING";
    }

    public Reservation(int reservationId, String userId, String isbn, String reservationDate, String status) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.isbn = isbn;
        this.reservationDate = LocalDate.parse(reservationDate);
        this.status = status;
        if (reservationId > idCounter) idCounter = reservationId;
    }

    public void fulfill() { this.status = "FULFILLED"; }
    public void cancel() { this.status = "CANCELLED"; }
    public void expire() { this.status = "EXPIRED"; }

    public int getReservationId() { return reservationId; }
    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
    public LocalDate getReservationDate() { return reservationDate; }
    public String getStatus() { return status; }
    public boolean isPending() { return status.equals("PENDING"); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation that = (Reservation) obj;
        return reservationId == that.reservationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return reservationId + "," + userId + "," + isbn + "," + reservationDate + "," + status;
    }
}