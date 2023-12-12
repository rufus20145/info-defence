package ru.rufus20145.messenger.messages;

import java.util.Base64;

import ru.rufus20145.messenger.users.User;

public class TextMessage extends AbsMessage {
    public TextMessage(String text, User sender, User receiver) {
        super(MessageType.TEXT, text, sender, receiver);
    }

    @Override
    protected String getPrepared() {
        return type + ":" + sender.getUsername() + ":" + getEncryptedText();
    }

    private String getEncryptedText() {
        return Base64.getEncoder().encodeToString(receiver.encrypt(text));
    }
}
