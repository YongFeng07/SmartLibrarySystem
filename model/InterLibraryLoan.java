package model;

import java.time.LocalDate;
import java.util.Objects;

public class InterLibraryLoan {
    private static int idCounter = 200;
    private int requestId;
    private String userId;
    private String isbn;
    private String bookTitle;
    private String requestingLibrary;
    private String owningLibrary;
    private LocalDate requestDate;
    private LocalDate expectedArrivalDate;
    private String status;
    private String notes;

    public InterLibraryLoan(String userId, String isbn, String bookTitle, 
                            String requestingLibrary, String owningLibrary) {
        this.requestId = ++idCounter;
        this.userId = userId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.requestingLibrary = requestingLibrary;
        this.owningLibrary = owningLibrary;
        this.requestDate = LocalDate.now();
        this.expectedArrivalDate = requestDate.plusDays(7);
        this.status = "PENDING";
        this.notes = "";
    }

    public InterLibraryLoan(int requestId, String userId, String isbn, String bookTitle,
                            String requestingLibrary, String owningLibrary, String requestDate,
                            String expectedArrivalDate, String status, String notes) {
        this.requestId = requestId;
        this.userId = userId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.requestingLibrary = requestingLibrary;
        this.owningLibrary = owningLibrary;
        this.requestDate = LocalDate.parse(requestDate);
        this.expectedArrivalDate = LocalDate.parse(expectedArrivalDate);
        this.status = status;
        this.notes = notes;
        if (requestId > idCounter) idCounter = requestId;
    }

    public int getRequestId() { return requestId; }
    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
    public String getBookTitle() { return bookTitle; }
    public String getRequestingLibrary() { return requestingLibrary; }
    public String getOwningLibrary() { return owningLibrary; }
    public LocalDate getRequestDate() { return requestDate; }
    public LocalDate getExpectedArrivalDate() { return expectedArrivalDate; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void approve() { this.status = "APPROVED"; }
    public void reject(String reason) { this.status = "REJECTED"; this.notes = reason; }
    public void markAsShipped() { this.status = "SHIPPED"; }
    public void markAsReceived() { this.status = "RECEIVED"; }
    public void markAsReturned() { this.status = "RETURNED"; }

    public boolean isPending() { return status.equals("PENDING"); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InterLibraryLoan that = (InterLibraryLoan) obj;
        return requestId == that.requestId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return requestId + "," + userId + "," + isbn + "," + bookTitle + "," +
               requestingLibrary + "," + owningLibrary + "," + requestDate + "," +
               expectedArrivalDate + "," + status + "," + notes;
    }
}