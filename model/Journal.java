package model;

public class Journal extends LibraryItem {
    public Journal(String isbn, String title, String author, String publisher, int year, int totalCopies) {
        super(isbn, title, author, publisher, year, totalCopies);
    }

    @Override
    public String getItemType() { 
        return "Journal"; 
    }
}