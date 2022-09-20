package org.schar.cybersecurity.server;

public class User {

    private final String password;
    private final String id;
    private int count;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.count = 0;
    }

    public int getCount() {
        return count;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void increase(int amount) {
        this.count += amount;
    }

    public void decrease(int amount) {
        this.count -= amount;
    }
}
