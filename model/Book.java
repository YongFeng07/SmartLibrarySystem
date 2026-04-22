package model;

public class Book extends LibraryItem {
    public Book(String isbn, String title, String author, String publisher, int year, int totalCopies) {
        super(isbn, title, author, publisher, year, totalCopies);
    }

    @Override
    public String getItemType() { 
        return "Book"; 
    }
}