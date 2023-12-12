package ru.rufus20145.messenger.messages;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import ru.rufus20145.messenger.users.Self;
import ru.rufus20145.messenger.users.User;

@Getter
public abstract class AbsMessage implements Message {
    protected final MessageType type;
    protected final String text;
    protected final User sender;
    protected final User receiver;

    protected AbsMessage(MessageType type, String text, User sender, User receiver) {
        this.type = type;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    protected AbsMessage(MessageType type, Self self) {
        this.type = type;
        this.text = "";
        this.sender = self;
        this.receiver = new User("broadcast", self.getBroadcast(), self.getPublicKey());
    }

    public byte[] getForSend() {
        return getPrepared().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public InetAddress getIpAddress() {
        return receiver.getIpAddress();
    }

    @Override
    public String getReceiverUsername() {
        return receiver.getUsername();
    }

    public String getType() {
        return type.name();
    }

    protected abstract String getPrepared();
}
