package org.schar.cybersecurity.client;

import org.json.JSONObject;
import org.schar.cybersecurity.common.io.Utils;

import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;
    private JSONObject configuration;
    private InputStreamReader inputReader;
    private OutputStreamWriter outputWriter;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String configuration) throws IOException, URISyntaxException {
        this.configuration = new JSONObject(Utils.readFile(Utils.getFile(configuration)));
    }

    public void connect() throws IOException {
        String ip = configuration.getJSONObject("server").getString("ip");
        String port = configuration.getJSONObject("server").getString("port");
        socket = new Socket(ip, Integer.parseInt(port));
        this.inputReader = new InputStreamReader(socket.getInputStream());
        this.outputWriter = new OutputStreamWriter(socket.getOutputStream());
        this.bufferedReader = new BufferedReader(inputReader);
        this.bufferedWriter = new BufferedWriter(outputWriter);
    }

    public JSONObject sendId() throws IOException {
        return sendMessage(new JSONObject().put("id", configuration.getString("id")));
    }

    public JSONObject sendMessage(JSONObject json) throws IOException {
        return sendMessage(json.toString(4));
    }

    public JSONObject sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();

        return receiveServerMessage();
    }

    private JSONObject receiveServerMessage() throws IOException {
        return new JSONObject(bufferedReader.readLine());
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Client client = new Client("configuration.json");
        client.connect();
        System.out.println(client.sendId());
        client.socket.close();
    }

}
