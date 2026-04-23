package model;

import java.io.Serializable;
import java.util.Objects;

public abstract class LibraryItem implements Serializable {
    protected String isbn;
    protected String title;
    protected String author;
    protected String publisher;
    protected int year;
    protected int totalCopies;
    protected int availableCopies;
    protected boolean isDamaged;
    protected String genre;

    public LibraryItem(String isbn, String title, String author, String publisher, 
                       int year, int totalCopies, String genre) {
        if (isbn == null || !isbn.matches("^[0-9]{10}$|^[0-9]{13}$")) {
            throw new IllegalArgumentException("Invalid ISBN format. Must be 10 or 13 digits.");
        }
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.isDamaged = false;
        this.genre = genre;
    }

    public abstract String getItemType();

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public int getYear() { return year; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public boolean isDamaged() { return isDamaged; }
    public String getGenre() { return genre; }
    public boolean isAvailable() { return availableCopies > 0 && !isDamaged; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setYear(int year) { this.year = year; }
    public void setDamaged(boolean damaged) { isDamaged = damaged; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.totalCopies = totalCopies;
        if (this.availableCopies > totalCopies) {
            this.availableCopies = totalCopies;
        }
    }
    
    // Method Overloading - demonstrates polymorphism
    public void setTotalCopies(int totalCopies, boolean forceUpdate) {
        setTotalCopies(totalCopies);
        if (forceUpdate) {
            this.availableCopies = totalCopies;
        }
    }

    public boolean borrowCopy() {
        if (availableCopies > 0 && !isDamaged) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public void removeDamagedCopy() {
        if (totalCopies > 0) {
            totalCopies--;
            if (availableCopies > totalCopies) {
                availableCopies = totalCopies;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LibraryItem that = (LibraryItem) obj;
        return Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return getItemType() + "," + isbn + "," + title + "," + author + "," + publisher + "," 
               + year + "," + totalCopies + "," + isDamaged + "," + genre;
    }
}