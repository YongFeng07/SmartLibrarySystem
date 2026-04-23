package model;

public class Faculty extends User {
    public Faculty(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    @Override
    public int getBorrowLimit() { return 12; }
    @Override
    public int getLoanDays() { return 30; }
    @Override
    public double getFinePerDay() { return 0.20; }
}