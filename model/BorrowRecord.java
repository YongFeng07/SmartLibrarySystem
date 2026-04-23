package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class BorrowRecord {
    private static int idCounter = 1000;
    private int recordId;
    private String userId;
    private String isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private String status;
    private boolean isLost;
    private LocalDate lostDate;

    public BorrowRecord(String userId, String isbn, int loanDays) {
        this.recordId = ++idCounter;
        this.userId = userId;
        this.isbn = isbn;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(loanDays);
        this.status = "BORROWED";
        this.fine = 0;
        this.isLost = false;
        this.lostDate = null;
    }

    public BorrowRecord(int recordId, String userId, String isbn, String borrowDate, 
                        String dueDate, String returnDate, double fine, String status, 
                        boolean isLost, String lostDate) {
        this.recordId = recordId;
        this.userId = userId;
        this.isbn = isbn;
        this.borrowDate = LocalDate.parse(borrowDate);
        this.dueDate = LocalDate.parse(dueDate);
        this.returnDate = (returnDate != null && !returnDate.equals("null")) ? LocalDate.parse(returnDate) : null;
        this.fine = fine;
        this.status = status;
        this.isLost = isLost;
        this.lostDate = (lostDate != null && !lostDate.equals("null")) ? LocalDate.parse(lostDate) : null;
        if (recordId > idCounter) idCounter = recordId;
    }

    public double returnItem(double finePerDay) {
        if (isLost) {
            System.out.println("⚠️ This item is marked as lost. Cannot return.");
            return fine;
        }
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
        if (isLost) {
            System.out.println("⚠️ Lost book fine cannot be paid online. Please contact librarian.");
            return;
        }
        this.fine = 0;
        if (status.equals("OVERDUE")) {
            status = "RETURNED";
        }
    }

    // Lost book methods
    public void markAsLost(double lostBookFine) {
        if (!isLost && (status.equals("BORROWED") || status.equals("OVERDUE"))) {
            this.isLost = true;
            this.lostDate = LocalDate.now();
            this.status = "LOST";
            this.fine = lostBookFine;
            System.out.println("📖 Book marked as lost. Fine applied: RM " + lostBookFine);
        } else {
            System.out.println("⚠️ Cannot mark as lost. Book already returned or already lost.");
        }
    }

    public void markAsFound() {
        if (isLost) {
            this.isLost = false;
            this.status = "RETURNED";
            this.returnDate = LocalDate.now();
            this.fine = 0;
            System.out.println("📖 Book found and returned. Fine waived.");
        } else {
            System.out.println("⚠️ This book was not marked as lost.");
        }
    }

    public int getRecordId() { return recordId; }
    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }
    public String getStatus() { return status; }
    public boolean isLost() { return isLost; }
    public LocalDate getLostDate() { return lostDate; }
    
    public boolean isActive() { 
        return (status.equals("BORROWED") || status.equals("OVERDUE")) && !isLost; 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BorrowRecord that = (BorrowRecord) obj;
        return recordId == that.recordId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        return recordId + "," + userId + "," + isbn + "," + borrowDate + "," + dueDate + "," 
               + (returnDate != null ? returnDate : "null") + "," + fine + "," + status + "," 
               + isLost + "," + (lostDate != null ? lostDate : "null");
    }
}