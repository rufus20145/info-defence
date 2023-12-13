package ru.rufus20145.messenger.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import ru.rufus20145.messenger.messages.TextMessage;

final class DecryptedMessageView implements Callback<ListView<TextMessage>, ListCell<TextMessage>> {
    @Override
    public ListCell<TextMessage> call(ListView<TextMessage> param) {
        return new ListCell<>() {
            @Override
            public void updateItem(TextMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText(message.getSender().getUsername() + " — " + message.getText());
                }
            }
        };
    }
}