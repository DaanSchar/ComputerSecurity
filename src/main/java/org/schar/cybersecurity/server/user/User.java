package org.schar.cybersecurity.server.user;

public class User {

    private final String password;
    private final String id;
    private int count;
    private int totalConnected;
    public static final int MAX_VALUE = 1000000;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.count = 0;
        this.totalConnected = 1;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getPassword() {
        return password;
    }

    public int getTotalConnected() {
        return totalConnected;
    }

    public void increase(int amount) {
        this.count += amount;
    }

    public void decrease(int amount) {
        this.count -= amount;
    }

    public void connect() {
        this.totalConnected += 1;
    }

    public void disconnect() {
        this.totalConnected -= 1;
    }

    public boolean isDisconnected() {
        return this.totalConnected <= 0;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
