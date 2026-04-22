package model;

import java.io.Serializable;

public abstract class LibraryItem implements Serializable {
    protected String isbn;
    protected String title;
    protected String author;
    protected String publisher;
    protected int year;
    protected int totalCopies;
    protected int availableCopies;
    protected boolean isDamaged;

    public LibraryItem(String isbn, String title, String author, String publisher, int year, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.isDamaged = false;
    }

    public abstract String getItemType();

    // Getters
    public String getIsbn() { 
        return isbn; 
    }
    public String getTitle() {
        return title; 
    }
    public String getAuthor() {
        return author; 
    }
    public String getPublisher() {
        return publisher; 
    }
    public int getYear() {
        return year; 
    }
    public int getTotalCopies() {
        return totalCopies; 
    }
    public int getAvailableCopies() {
        return availableCopies; 
    }
    public boolean isDamaged() {
        return isDamaged; 
    }
    public boolean isAvailable() {
        return availableCopies > 0 && !isDamaged; 
    }

    // Setters
    public void setTitle(String title) {
        this.title = title; 
    }
    public void setAuthor(String author) {
        this.author = author; 
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher; 
    }
    public void setYear(int year) {
        this.year = year; 
    }
    public void setDamaged(boolean damaged) {
        isDamaged = damaged; 
    }
    
    public void setTotalCopies(int totalCopies) { 
        this.totalCopies = totalCopies;
        if (this.availableCopies > totalCopies) {
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
    public String toString() {
        return getItemType() + "," + isbn + "," + title + "," + author + "," + publisher + "," + year + "," + totalCopies + "," + availableCopies + "," + isDamaged;
    }
}