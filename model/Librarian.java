package model;

public class Librarian {
    private String staffId;
    private String name;
    private String password;

    public Librarian(String staffId, String name, String password) {
        this.staffId = staffId;
        this.name = name;
        this.password = password;
    }

    public String getStaffId() { 
        return staffId; 
    }
    public String getName() { 
        return name; 
    }
    public boolean checkPassword(String input) { 
        return this.password.equals(input); 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }

    @Override
    public String toString() {
        return staffId + "," + name + "," + password;
    }
}