package ru.rufus20145.messenger.messages;

import ru.rufus20145.messenger.users.Self;

public class StopMessage extends AbsMessage {

    public StopMessage(Self self) {
        super(MessageType.STOP, self);
    }

    @Override
    protected String getPrepared() {
        return type + ":" + sender.getUsername();
    }
}
