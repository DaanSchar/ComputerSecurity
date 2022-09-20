package org.schar.cybersecurity.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import jdk.jshell.execution.Util;
import org.schar.cybersecurity.common.io.Utils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Optional;

public class Client {

    private Socket socket;
    private JsonNode configuration;
    private InputStreamReader inputReader;
    private OutputStreamWriter outputWriter;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(String configuration) throws IOException, URISyntaxException {
        this.configuration = Utils.parseJson(Utils.getFile(configuration));
    }

    public void connect() throws IOException {
        String ip = configuration.get("server").get("ip").asText();
        String port = configuration.get("server").get("port").asText();
        socket = new Socket(ip, Integer.parseInt(port));
        this.inputReader = new InputStreamReader(socket.getInputStream());
        this.outputWriter = new OutputStreamWriter(socket.getOutputStream());
        this.bufferedReader = new BufferedReader(inputReader);
        this.bufferedWriter = new BufferedWriter(outputWriter);
    }

    public void sendId() throws IOException {
        sendMessage(configuration.get("id").asText());
    }

    public void sendMessage(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public String receiveServerMessage() throws IOException {
        return bufferedReader.readLine();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Client client = new Client("configuration.json");
        client.connect();
        client.sendMessage("Hello dude!");
        client.socket.close();
//        while (true) {
//
//        }

//        Socket socket;
//        InputStreamReader inputReader;
//        OutputStreamWriter outputWriter;
//        BufferedReader bufferedReader;
//        BufferedWriter bufferedWriter;
//
//        try {
//            socket = new Socket("localhost", Repository.PORT);
//            inputReader = new InputStreamReader(socket.getInputStream());
//            outputWriter = new OutputStreamWriter(socket.getOutputStream());
//
//            bufferedReader = new BufferedReader(inputReader);
//            bufferedWriter = new BufferedWriter(outputWriter);
//
//            Scanner scanner = new Scanner(System.in);
//
//            while (true) {
//
//                String message = scanner.nextLine();
//                bufferedWriter.write(message);
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//
//                System.out.println("Server : " + bufferedReader.readLine());
//
//                if (message.equalsIgnoreCase("bye")) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

    }

}
