package ru.rufus20145.messenger.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.extern.log4j.Log4j2;
import ru.rufus20145.messenger.Messenger;
import ru.rufus20145.messenger.messages.MessageType;
import ru.rufus20145.messenger.messages.TextMessage;
import ru.rufus20145.messenger.users.Self;
import ru.rufus20145.messenger.users.User;

@Log4j2
public class Receiver extends Thread implements Stoppable {

    private final InetAddress ipAddress;
    private final int port;
    private boolean isRunning = true;
    private Messenger messenger;
    private Self self;

    public Receiver(InetAddress ipAddress, int port, Self self, Messenger messenger) {
        super("Receiver");
        this.ipAddress = ipAddress;
        this.port = port;
        this.self = self;
        this.messenger = messenger;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port, ipAddress)) {
            byte[] buffer = new byte[1024];
            while (isRunning) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String messageText = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                String[] parts = messageText.split(":");
                MessageType msgType = MessageType.valueOf(parts[0]);
                switch (msgType) {
                    case MessageType.START:
                        processStartMessage(parts[1], packet.getAddress(), parts[2]);
                        break;
                    case MessageType.TEXT:
                        processTextMessage(parts[1], parts[2]);
                        break;
                    case MessageType.STOP:
                        processStopMessage(parts[1]);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processStartMessage(String username, InetAddress ip, String keyString) {
        PublicKey publicKey = parsePublicKey(keyString);
        User newUser = new User(username, ip, publicKey);
        messenger.addUser(newUser);
        log.error("Got new user: {} on {}.", newUser.getUsername(), newUser.getIpAddress().getHostAddress());
    }

    private void processTextMessage(String username, String encryptedMessage) {
        User sender = messenger.getUserByUsername(username);
        String text = self.decryptMessage(encryptedMessage);
        TextMessage msg = new TextMessage(text, sender, self);
        messenger.showNewMessage(msg);
    }

    private void processStopMessage(String username) {
        messenger.removeUserByUsername(username);
    }

    private PublicKey parsePublicKey(String keyInBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyInBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error while parsing public key", e);
        }
    }

    @Override
    public void stopWorking() {
        System.out.println("Receiver.stopWorking()");
        isRunning = false;
        Thread.currentThread().interrupt();
    }
}
