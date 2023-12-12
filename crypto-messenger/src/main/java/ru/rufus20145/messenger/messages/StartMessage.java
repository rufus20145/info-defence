package ru.rufus20145.messenger.messages;

import java.util.Base64;

import ru.rufus20145.messenger.users.Self;
import ru.rufus20145.messenger.users.User;

public class StartMessage extends AbsMessage {

    public StartMessage(Self self) {
        super(MessageType.START, self);
    }

    public StartMessage(User me, User newUser) {
        super(MessageType.START, "", me, newUser);
    }

    @Override
    protected String getPrepared() {
        return type + ":" + sender.getUsername() + ":" + getPublicKeyInBase64();
    }

    private String getPublicKeyInBase64() {
        return Base64.getEncoder().encodeToString(sender.getPublicKey().getEncoded());
    }
}
