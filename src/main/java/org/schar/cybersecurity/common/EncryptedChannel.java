package org.schar.cybersecurity.common;

import org.json.JSONObject;

import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class EncryptedChannel extends Channel {

    private final Encryption encryption;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    private PublicKey receiverKey;

    public EncryptedChannel(Socket socket) throws Exception {
        super(socket);

        generateKeyPair();
        this.encryption = new Encryption(privateKey);
    }

    @Override
    public void sendMessage(String message) throws Exception {
        super.sendMessage(encryption.encrypt(message, receiverKey));
    }

    @Override
    public String receiveMessage() throws Exception {
        return encryption.decrypt(super.receiveMessage());
    }

    /**
     * RSA encryption handshake from server to client.
     */
    public void establishSecureClientConnection() throws Exception {
        Channel unsecureChannel = new Channel(getSocket());

        this.receiverKey = receivePublicKey(unsecureChannel);
        sendPublicKey(unsecureChannel);
    }

    /**
     * RSA encryption handshake from client to server.
     *
     */
    public void establishSecureServerConnection() throws Exception {
        Channel unsecureChannel = new Channel(getSocket());

        sendPublicKey(unsecureChannel);
        this.receiverKey = receivePublicKey(unsecureChannel);
    }

    private PublicKey receivePublicKey(Channel channel) throws Exception {
        JSONObject message = channel.receiveMessageJSON();
        String keyString = message.getString("publicKey");
        byte[] keyBytes = Base64.getDecoder().decode(keyString);

        return Encryption.getPublicKey(keyBytes);
    }

    private void sendPublicKey(Channel channel) throws Exception {
        channel.sendMessage(new JSONObject().put(
                "publicKey",
                Base64.getEncoder().encodeToString(publicKey.getEncoded()))
        );
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

}
