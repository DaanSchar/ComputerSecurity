package org.schar.cybersecurity.server;

import org.json.JSONObject;
import org.schar.cybersecurity.common.io.Logger;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final ServerSocket serverSocket;
    private BufferedReader buffReader;
    private BufferedWriter buffWriter;

    private final Map<String, User> users;

    private User currentUser;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        users = new HashMap<>();

        Logger.info("[Server] Starting server");
    }

    private void start() throws IOException {
        while (true) {
            run();
        }
    }

    /**
     * Listens to messages from the client.
     * May the client-user fail to be verified by the server,
     * No actions will be performed.
     */
    private void run() throws IOException {
        if (waitAndAcceptClientSocket()) {
            while (true) {
                try {
                    ListenForUserActions();
                } catch (Exception e) {
                    Logger.info("[Server] Client disconnected");
                    break;
                }
            }
        }
    }

    /**
     * Waits until a client connects and logs the user in.
     * @return True if user was logged in successfully.
     */
    private boolean waitAndAcceptClientSocket() throws IOException {
        Socket socket = serverSocket.accept();
        resetBuffers(socket);

        Logger.info("[Server] Client connected");

        JSONObject clientMsg = new JSONObject(buffReader.readLine());

        Logger.info("[Client] " + clientMsg);

        String id = clientMsg.getString("id");
        String password = clientMsg.getString("password");

        Logger.info("[Server] identified user with id: %s and password: %s", id, password);

        boolean success = loginUser(id, password);
        sendMsg(new JSONObject().put("accept", success));

        return success;
    }

    /**
     *  Listens for any messages from the client-user containing actions
     */
    private void ListenForUserActions() throws IOException {
        JSONObject clientMsg = new JSONObject(buffReader.readLine());
        Logger.info("[Client] " + clientMsg);

        String[] actionAndAmount = clientMsg.getString("action").split(" ");

        String action = actionAndAmount[0];
        int amount = Integer.parseInt(actionAndAmount[1]);

        performUserAction(action, amount);
    }

    /**
     * Logs the user in.
     * @return True if a new user was created or an old user was verified.
     */
    private boolean loginUser(String id, String password) {
        if (users.containsKey(id)) {
            if (users.get(id).getPassword().equals(password)) {
                Logger.info("[Server] logging in user %s", id);
                currentUser = users.get(id);
                return true;
            }

            Logger.info("[Server] user %s used wrong password", id);
            return false;
        }

        Logger.info("[Server] Creating new user %s with password %s", id, password);
        currentUser = new User(id, password);
        users.put(id, currentUser);

        return true;
    }

    /**
     * Performs the actions received from the verified client-user
     */
    private void performUserAction(String action, int amount) {
        Logger.info("[Server] %s performs action %s with amount: %d", action, currentUser.getId(), amount);

        if (action.equalsIgnoreCase("increase")) {
            currentUser.increase(amount);
        } else if (action.equalsIgnoreCase("decrease")) {
            currentUser.decrease(amount);
        }
        Logger.info("[Server] new count of user %s = %d", currentUser.getId(), currentUser.getCount());
    }


    private void sendMsg(JSONObject json) throws IOException {
        sendMsg(json.toString());
    }

    private void sendMsg(String string) throws IOException {
        buffWriter.write(string);
        buffWriter.newLine();
        buffWriter.flush();
    }

    private void resetBuffers(Socket socket) throws IOException {
        buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(1234);
        server.start();
    }

}
