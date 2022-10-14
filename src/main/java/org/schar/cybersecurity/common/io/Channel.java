package org.schar.cybersecurity.common.io;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Channel {

    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Socket socket;

    public Channel(Socket socket) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMessage(String message) throws Exception {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendMessage(JSONObject json) throws Exception {
        sendMessage(json.toString());
    }

    public String receiveMessage() throws Exception {
        return bufferedReader.readLine();
    }

    public JSONObject receiveMessageJSON() throws Exception {
        return new JSONObject(receiveMessage());
    }

    public Socket getSocket() {
        return socket;
    }
}
