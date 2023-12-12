package ru.rufus20145.messenger.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.log4j.Log4j2;
import ru.rufus20145.messenger.messages.Message;

@Log4j2
public class Sender extends Thread implements Stoppable {
    BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private boolean isRunning = true;
    private final int targetPort;

    public Sender(int targetPort) {
        super("Sender");
        this.targetPort = targetPort;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            while (isRunning) {
                Message msg = queue.take();
                byte[] data = msg.getForSend();
                DatagramPacket packet = new DatagramPacket(data, data.length, msg.getIpAddress(), targetPort);
                socket.send(packet);
                log.error("Message to {} was sent successfully.", msg.getReceiverUsername());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void submitMessage(Message msg) {
        queue.add(msg);
    }

    @Override
    public void stopWorking() {
        isRunning = false;
    }
}