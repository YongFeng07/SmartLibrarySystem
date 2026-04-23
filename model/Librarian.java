package model;

import java.util.Objects;

public class Librarian {
    private String staffId;
    private String name;
    private String password;

    public Librarian(String staffId, String name, String password) {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be null or empty");
        }
        this.staffId = staffId;
        this.name = name;
        this.password = password;
    }

    public String getStaffId() { return staffId; }
    public String getName() { return name; }
    public boolean checkPassword(String input) { return this.password.equals(input); }
    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Librarian librarian = (Librarian) obj;
        return Objects.equals(staffId, librarian.staffId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId);
    }

    @Override
    public String toString() {
        return staffId + "," + name + "," + password;
    }
}