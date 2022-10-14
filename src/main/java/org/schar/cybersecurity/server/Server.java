package org.schar.cybersecurity.server;

import org.schar.cybersecurity.common.io.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Logger.info("[Server] Starting server");
    }

    public void start() throws Exception {
        while (true) {
            Socket socket = serverSocket.accept();
            Logger.info("[Server] Client connected.");

            handleClient(socket);
        }
    }

    private void handleClient(Socket socket) throws Exception {
        Thread clientHandlerThread = new Thread(new ClientHandler(socket));
        clientHandlerThread.start();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(1234);
        server.start();
    }

}
