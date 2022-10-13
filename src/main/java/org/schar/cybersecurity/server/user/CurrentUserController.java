package org.schar.cybersecurity.server.user;

import java.util.Map;

public class CurrentUserController {

    private final CurrentUserModel model;

    public CurrentUserController() {
        this.model = CurrentUserModel.getInstance();
    }

    public Map<String, User> getUsers() {
        return model.getConnectedUsers();
    }

    public User getUser(String id) {
        return getUsers().get(id);
    }

    public int getUserCount(String id) {
        return getUser(id).getCount();
    }

    public void addNewUser(String id, String password) {
        getUsers().put(id, new User(id, password));
    }

    public void increaseUserCount(String id, int amount) {
        getUser(id).increase(amount);
    }

    public void decreaseUserCount(String id, int amount) {
        getUser(id).decrease(amount);
    }

    public void disconnectUser(String id) {
        getUser(id).disconnect();
    }

    public void removeUser(String currentUserId) {
        getUsers().remove(currentUserId);
    }

    public boolean userIsDisconnected(String id) {
        return getUser(id).isDisconnected();
    }

    public boolean userCurrentlyExists(String id) {
        return getUsers().containsKey(id);
    }

    public boolean userHasValidPassword(String id, String password) {
        return getUsers().get(id).getPassword().equals(password);
    }

    public int getTotalConnectionsOfUser(String id) {
        return getUser(id).getTotalConnected();
    }
}
