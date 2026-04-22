package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    private static int idCounter = 1000;
    private int recordId;
    private String userId;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private String status; // BORROWED, RETURNED, OVERDUE

    public BorrowRecord(String userId, String isbn, int loanDays) {
        this.recordId = ++idCounter;
        this.userId = userId;
        this.isbn = isbn;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanDays);
        this.status = "BORROWED";
        this.fine = 0;
    }

    public BorrowRecord(int recordId, String userId, String isbn, String borrowDate, String dueDate, String returnDate, double fine, String status) {
        this.recordId = recordId;
        this.userId = userId;
        this.isbn = isbn;
        this.borrowDate = LocalDate.parse(borrowDate);
        this.dueDate = LocalDate.parse(dueDate);
        this.returnDate = returnDate != null && !returnDate.equals("null") ? LocalDate.parse(returnDate) : null;
        this.fine = fine;
        this.status = status;
        if (recordId > idCounter) idCounter = recordId;
    }

    public double returnItem(double finePerDay) {
        this.returnDate = LocalDate.now();
        if (returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            this.fine = daysLate * finePerDay;
            this.status = "OVERDUE";
        } else {
            this.status = "RETURNED";
        }
        return this.fine;
    }

    public void payFine() {
        this.fine = 0;
        if (status.equals("OVERDUE")) {
            status = "RETURNED";
        }
    }

    // Getters
    public int getRecordId() { 
        return recordId; 
    }
    public String getUserId() { 
        return userId; 
    }
    public String getIsbn() { 
        return isbn; 
    }
    public LocalDate getBorrowDate() { 
        return borrowDate; 
    }
    public LocalDate getDueDate() { 
        return dueDate; 
    }
    public LocalDate getReturnDate() { 
        return returnDate; 
    }
    public double getFine() { 
        return fine; 
    }
    public String getStatus() { 
        return status; 
    }
    public boolean isActive() {
        return status.equals("BORROWED") || status.equals("OVERDUE"); 
    }

    @Override
    public String toString() {
        return recordId + "," + userId + "," + isbn + "," + borrowDate + "," + dueDate + "," + (returnDate != null ? returnDate : "null") + "," + fine + "," + status;
    }
}