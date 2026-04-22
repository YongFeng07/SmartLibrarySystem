package model;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String userId;
    protected String name;
    protected String phone;
    protected String email;
    protected String password;

    public User(String userId, String name, String phone, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public abstract int getBorrowLimit();
    public abstract int getLoanDays();
    public abstract double getFinePerDay();

    // Getters
    public String getUserId() { 
        return userId; 
    }
    public String getName() { 
        return name; 
    }
    public String getPhone() { 
        return phone; 
    }
    public String getEmail() { 
        return email; 
    }
    public String getPassword() { 
        return password; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    
    public String toString() {
        return userId + "," + name + "," + phone + "," + email + "," + password;
    }
}