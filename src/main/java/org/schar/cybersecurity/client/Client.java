package org.schar.cybersecurity.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.schar.cybersecurity.common.io.Utils;

import java.io.*;
import java.net.*;

public class Client {

    private Socket socket;
    private final JSONObject configuration;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String configuration) throws IOException, URISyntaxException {
        this.configuration = new JSONObject(Utils.readFile(Utils.getFile(configuration)));
    }

    public void connect() throws IOException {
        String ip = configuration.getJSONObject("server").getString("ip");
        String port = configuration.getJSONObject("server").getString("port");
        socket = new Socket(ip, Integer.parseInt(port));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendUserInfo() throws IOException {
        String id = configuration.getString("id");
        String password = configuration.getString("password");

        sendMessage(new JSONObject().put("id", id).put("password", password));
    }

    public void sendMessage(JSONObject json) throws IOException {
        sendMessage(json.toString());
    }

    public void sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void sendActions() throws IOException {
        JSONObject actions = (JSONObject) configuration.get("actions");
        int delay = actions.getInt("delay");
        JSONArray steps = actions.getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++) {
            String action = steps.getString(i);
            sleep(delay);
            sendMessage(new JSONObject().put("action", action));
        }
    }

    private JSONObject getResponse() throws IOException {
        return new JSONObject(bufferedReader.readLine());
    }

    private void sleep(int time) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < time * 1000L) {
            // wait until time of delay has passed
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Client client = new Client("config/configuration.json");
        client.connect();
        client.sendUserInfo();

        if (client.getResponse().getBoolean("accept")) {
            client.sendActions();
        }

        client.socket.close();
    }

}
