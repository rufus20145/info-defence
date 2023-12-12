package ru.rufus20145.messenger.messages;

import java.net.InetAddress;

public interface Message {
    byte[] getForSend();

    InetAddress getIpAddress();

    String getReceiverUsername();
}
