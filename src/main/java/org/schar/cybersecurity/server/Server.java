package org.schar.cybersecurity.server;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(1234);


        while (true) {
            Socket socket = serverSocket.accept();

            System.out.println("Client connected!");

            InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

            BufferedReader buffReader = new BufferedReader(inputReader);
            BufferedWriter buffWriter = new BufferedWriter(outputWriter);

            String receivedMessage = buffReader.readLine();
            System.out.println("client : " + receivedMessage);

        }
    }

}
