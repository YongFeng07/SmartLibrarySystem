package model;

public class DigitalResource extends LibraryItem {
    public DigitalResource(String isbn, String title, String author, String publisher, int year, int totalCopies) {
        super(isbn, title, author, publisher, year, totalCopies);
    }

    @Override
    public String getItemType() { 
        return "DigitalResource"; 
    }
}