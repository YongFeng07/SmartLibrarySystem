package model;

public class Book extends LibraryItem {
    public Book(String isbn, String title, String author, String publisher, 
                int year, int totalCopies, String genre) {
        super(isbn, title, author, publisher, year, totalCopies, genre);
    }

    @Override
    public String getItemType() { return "Book"; }
}