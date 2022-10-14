package org.schar.cybersecurity.server;

import org.json.JSONObject;
import org.schar.cybersecurity.common.io.EncryptedChannel;
import org.schar.cybersecurity.common.io.Logger;
import org.schar.cybersecurity.server.user.CurrentUserController;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final CurrentUserController userController;
    private String currentUserId;

    private final EncryptedChannel channel;

    public ClientHandler(Socket socket) throws Exception {
        this.channel = new EncryptedChannel(socket);
        this.userController = new CurrentUserController();
    }

    @Override
    public void run() {
        Logger.info("[Server] Starting ClientHandler on a new thread.");

        try {
            channel.establishSecureClientConnection();
            JSONObject clientMsg = channel.receiveMessageJSON();

            String id = clientMsg.getString("id");
            String password = clientMsg.getString("password");

            Logger.info("[Server] Identified user with id: %s and password: %s.", id, password);

            if (loginUser(id, password)) {

                try {
                    channel.sendMessage(new JSONObject().put("accept", true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                while (true) {
                    try {
                        listenForUserActions();
                    } catch (NumberFormatException e) {
                        Logger.info("[Server] Error: " + e.getMessage());
                        Logger.info("[Server] Ignoring this request");
                    } catch (Exception e) {
                        Logger.info("[Server] Client disconnected.");
                        logoutCurrentUser();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs the user in.
     *
     * @return True if a new user was created or if the same id is currently connected,
     * but uses the correct password.
     */
    private boolean loginUser(String id, String password) {
        if (userController.userCurrentlyExists(id)) {
            if (userController.userHasValidPassword(id, password)) {
                Logger.info("[Server] Logging in user %s", id);
                currentUserId = id;
                userController.getUser(id).connect();
                Logger.info("[Server] There is currently %d instance(s) of user %s connected.", userController.getTotalConnectionsOfUser(id), id);
                return true;
            }

            Logger.info("[Server] user %s used wrong password.", id);
            return false;
        }

        Logger.info("[Server] Creating new user %s with password %s.", id, password);

        currentUserId = id;
        userController.addNewUser(id, password);

        return true;
    }

    /**
     * Listens for any messages containing actions from the client-user.
     */
    private void listenForUserActions() throws Exception {
        JSONObject clientMsg = channel.receiveMessageJSON();

        String[] actionAndAmount = clientMsg.getString("action").split(" ");

        String action = actionAndAmount[0];
        int amount = Integer.parseInt(actionAndAmount[1]);

        performUserAction(action, amount);
    }

    /**
     * Performs the actions received from the verified client-user.
     */
    private void performUserAction(String action, int amount) {
        Logger.info("[Server] %s performs action %s with amount: %d.", currentUserId, action, amount);

        if (action.equalsIgnoreCase("increase")) {
            userController.increaseUserCount(currentUserId, amount);
        } else if (action.equalsIgnoreCase("decrease")) {
            userController.decreaseUserCount(currentUserId, amount);
        }

        int newUserCount = userController.getUserCount(currentUserId);
        Logger.info("[Server] new count of user %s = %d.", currentUserId, newUserCount);
    }

    private void logoutCurrentUser() {
        userController.disconnectUser(currentUserId);

        if (userController.userIsDisconnected(currentUserId)) {
            userController.removeUser(currentUserId);
        }
    }


}
