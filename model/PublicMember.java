package model;

public class PublicMember extends User {
    public PublicMember(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    
    public int getBorrowLimit() { 
        return 3; 
    }

    
    public int getLoanDays() { 
        return 7; 
    }

    
    public double getFinePerDay() { 
        return 1.00; 
    }
}