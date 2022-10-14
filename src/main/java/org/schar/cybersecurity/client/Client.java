package org.schar.cybersecurity.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schar.cybersecurity.common.EncryptedChannel;
import org.schar.cybersecurity.common.io.Utils;

import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Runnable {

    private final JSONObject configuration;
    private EncryptedChannel channel;

    public Client(String configuration) throws Exception {
        this.configuration = new JSONObject(Utils.readFile(Utils.getFile(configuration)));
    }

    @Override
    public void run() {
        try {
            this.connect();
            this.sendUserInfo();

            if (channel.receiveMessageJSON().getBoolean("accept")) {
                System.out.println("[Client] Logged in successfully!");

                this.sendActions();
                System.out.println("[Client] Done!");
            }
        } catch (JSONException e) {
            System.out.println("[Client] Configuration Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void connect() throws Exception {
        String ip = configuration.getJSONObject("server").getString("ip");
        String port = configuration.getJSONObject("server").getString("port");
        Socket socket = new Socket(ip, Integer.parseInt(port));

        channel = new EncryptedChannel(socket);
        channel.establishSecureServerConnection();

        System.out.println("[Client] Starting client!");
    }

    public void sendUserInfo() throws Exception {
        String id = configuration.getString("id");
        String password = configuration.getString("password");

        channel.sendMessage(new JSONObject().put("id", id).put("password", password));
    }

    public void sendActions() throws Exception {
        JSONObject actions = (JSONObject) configuration.get("actions");
        int delay = actions.getInt("delay");
        JSONArray steps = actions.getJSONArray("steps");

        for (int i = 0; i < steps.length(); i++) {
            String action = steps.getString(i);

            if (!isValidAction(action)) {
                throw new JSONException("\"" + action + "\"" + " is not a valid action.");
            }

            sleep(delay);
            channel.sendMessage(new JSONObject().put("action", action));
        }
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

    public static void main(String[] args) throws Exception {
        Client client1 = new Client("configs/configuration.json");
        Client client2 = new Client("configs/configuration2.json");

        Thread client1Thread = new Thread(client1);
        Thread client2Thread = new Thread(client2);
        client1Thread.start();
        Thread.sleep(100);
        client2Thread.start();
    }

}
