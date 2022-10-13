package org.schar.cybersecurity.server.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Database of currently connected users.
 */
public class CurrentUserModel {

    private static CurrentUserModel instance;

    public static CurrentUserModel getInstance() {
        if (instance == null) {
            instance = new CurrentUserModel();
        }

        return instance;
    }

    private final Map<String, User> connectedUsers = new HashMap<>();

    public Map<String, User> getConnectedUsers() {
        return connectedUsers;
    }
}
