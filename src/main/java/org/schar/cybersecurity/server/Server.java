package org.schar.cybersecurity.server;

import org.schar.cybersecurity.common.io.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final ServerSocket serverSocket;
    private final static Map<String, User> connectedUsers;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.info("[Server] Starting server");
    }

    static {
        connectedUsers = new HashMap<>();
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            Logger.info("[Server] Client connected.");

            handleClient(socket);
        }
    }

    private void handleClient(Socket socket) throws IOException {
        Thread clientHandlerThread = new Thread(new ClientHandler(socket));
        clientHandlerThread.start();
    }

    public static Map<String, User> getUsers() {
        return connectedUsers;
    }

    public static User getUser(String id) {
        return connectedUsers.get(id);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(1234);
        server.start();
    }

}
