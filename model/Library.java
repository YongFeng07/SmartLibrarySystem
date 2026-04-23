package model;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private String libraryName;
    private String address;
    private List<LibraryItem> items;

    public Library(String libraryName, String address) {
        this.libraryName = libraryName;
        this.address = address;
        this.items = new ArrayList<>();
    }

    public void addItem(LibraryItem item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void removeItem(LibraryItem item) {
        items.remove(item);
    }

    public List<LibraryItem> getItems() {
        return items;
    }

    public String getLibraryName() { return libraryName; }
    public String getAddress() { return address; }
    public void setLibraryName(String libraryName) { this.libraryName = libraryName; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return libraryName + "," + address;
    }
}