package ru.rufus20145.messenger.messages;

import java.util.Base64;

import ru.rufus20145.messenger.users.Self;

public class StartMessage extends AbsMessage {

    public StartMessage(Self self) {
        super(MessageType.START, "", self);
    }

    @Override
    protected String getPrepared() {
        return type + ":" + sender.getUsername() + ":" + getPublicKeyInBase64();
    }

    public String getPublicKeyInBase64() {
        return Base64.getEncoder().encodeToString(sender.getPublicKey().getEncoded());
    }

}
