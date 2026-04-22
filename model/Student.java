package model;

public class Student extends User {
    public Student(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    
    public int getBorrowLimit() { 
        return 5; 
    }

    
    public int getLoanDays() { 
        return 14; 
    }

    
    public double getFinePerDay() { 
        return 0.50; 
    }
}