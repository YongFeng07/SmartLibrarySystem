package model;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private String libraryName;
    private String address;
    private List<LibraryItem> items;  // Aggregation: Library has Books
    
    public Library(String libraryName, String address) {
        this.libraryName = libraryName;
        this.address = address;
        this.items = new ArrayList<>();
    }
    
    public void addItem(LibraryItem item) {
        items.add(item);
    }
    
    public void removeItem(LibraryItem item) {
        items.remove(item);
    }
    
    public List<LibraryItem> getItems() {
        return items;
    }
    
    // Getters and Setters
    public String getLibraryName() { return libraryName; }
    public String getAddress() { return address; }
}

