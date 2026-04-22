package model;

public class Faculty extends User {
    public Faculty(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    
    public int getBorrowLimit() { 
        return 12; 
    }

    
    public int getLoanDays() { 
        return 30; 
    }

    
    public double getFinePerDay() { 
        return 0.20; 
    }
}