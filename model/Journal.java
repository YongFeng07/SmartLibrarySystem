package model;

public class Journal extends LibraryItem {
    public Journal(String isbn, String title, String author, String publisher, 
                   int year, int totalCopies, String genre) {
        super(isbn, title, author, publisher, year, totalCopies, genre);
    }

    @Override
    public String getItemType() { return "Journal"; }
}