package org.schar.cybersecurity.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schar.cybersecurity.common.io.Utils;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Runnable {

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
        System.out.println("[Client] Starting client!");
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

    public void sendActions() throws IOException, JSONException {
        JSONObject actions = (JSONObject) configuration.get("actions");
        int delay = actions.getInt("delay");
        JSONArray steps = actions.getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++) {
            String action = steps.getString(i);

            if (!isValidAction(action)) {
                throw new JSONException("\"" + action +"\"" + " is not a valid action.");
            }

            sleep(delay);
            sendMessage(new JSONObject().put("action", action));
        }
    }

    @Override
    public void run() {
        try {
            this.connect();
            this.sendUserInfo();

            if (this.getResponse().getBoolean("accept")) {
                System.out.println("[Client] Logged in successfully!");

                this.sendActions();
                System.out.println("[Client] Done!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            System.out.println("[Client] Configuration Error: " + e.getMessage());
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

    private boolean isValidAction(String action) {
        return regex(action, "increase [0-9]") || regex(action, "decrease [0-9]");
    }

    private boolean regex(String string, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);

        return matcher.find();
    }

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Client client1 = new Client("config/configuration.json");
        Client client2 = new Client("config/configuration2.json");

        Thread client1Thread = new Thread(client1);
        Thread client2Thread = new Thread(client2);
        client1Thread.start();
        Thread.sleep(100);
        client2Thread.start();
    }

}
