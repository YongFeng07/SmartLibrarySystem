package model;

public class Student extends User {
    public Student(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    @Override
    public int getBorrowLimit() { return 5; }
    @Override
    public int getLoanDays() { return 14; }
    @Override
    public double getFinePerDay() { return 0.50; }
}