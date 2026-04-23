package model;

import java.io.Serializable;
import java.util.Objects;

public abstract class User implements Serializable {
    protected String userId;
    protected String name;
    protected String phone;
    protected String email;
    protected String password;

    public User(String userId, String name, String phone, String email, String password) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public abstract int getBorrowLimit();
    public abstract int getLoanDays();
    public abstract double getFinePerDay();

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
        this.password = password;
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    public void updateProfile(String name, String phone, String email) {
        setName(name);
        setPhone(phone);
        setEmail(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return userId + "," + name + "," + phone + "," + email + "," + password;
    }
}