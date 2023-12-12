package ru.rufus20145.messenger.messages;

import ru.rufus20145.messenger.users.User;

public class StopMessage extends AbsMessage {

    public StopMessage(String text, User sender, User receiver) {
        super(MessageType.STOP, text, sender, receiver);
    }

    @Override
    protected String getPrepared() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPrepared'");
    }

}
