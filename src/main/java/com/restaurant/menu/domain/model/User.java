package com.restaurant.menu.domain.model;

public class User {
    private final UserId id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
    private boolean active;

    public User(UserId id, String email, String password, String name, UserRole role, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.active = active;
    }

    public static User create(UserId id, String email, String password, String name, UserRole role) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        return new User(id, email.trim().toLowerCase(), password, name.trim(), role, true);
    }

    public UserId id() { return id; }
    public String email() { return email; }
    public String password() { return password; }
    public String name() { return name; }
    public UserRole role() { return role; }
    public boolean active() { return active; }

    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
}
