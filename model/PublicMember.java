package model;

public class PublicMember extends User {
    public PublicMember(String userId, String name, String phone, String email, String password) {
        super(userId, name, phone, email, password);
    }

    @Override
    public int getBorrowLimit() { return 3; }
    @Override
    public int getLoanDays() { return 7; }
    @Override
    public double getFinePerDay() { return 1.00; }
}