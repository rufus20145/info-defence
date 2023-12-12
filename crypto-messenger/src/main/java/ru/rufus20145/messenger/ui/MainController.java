package ru.rufus20145.messenger.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import ru.rufus20145.messenger.Messenger;
import ru.rufus20145.messenger.messages.TextMessage;
import ru.rufus20145.messenger.users.Self;

public class MainController implements Initializable {

    @FXML
    private TextArea messageTextArea;

    @FXML
    private ListView<TextMessage> messagesListView;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usersOnlineLabel;

    private Messenger messenger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Self self = showCreatingSelfDialog();
        if (self != null) {
            messenger = new Messenger(10435, self, this);
            messagesListView.setCellFactory(new MessageListView());
            usernameLabel.setText(self.getUsername());
            sendMessageButton.setOnAction(this::sendMessage);
        } else {
            throw new IllegalStateException("User data was not entered");
        }
    }

    private Self showCreatingSelfDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewUser.fxml"));
            Dialog<Self> dialog = new Dialog<>();
            dialog.setTitle("Укажите ваши данные");
            dialog.getDialogPane().setContent(loader.load());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                NewUserController controller = loader.getController();
                if (!controller.validateFields()) {
                    event.consume();
                } else {
                    controller.setDialog(dialog);
                    controller.processResults();
                }
            });
            dialog.setResultConverter(btn -> dialog.getResult());
            return dialog.showAndWait().orElse(null);
        } catch (IOException e) {
            throw new IllegalStateException("Error while loading fxml file.", e);
        }
    }

    public void showNewMessage(TextMessage msg) {
        messagesListView.getItems().add(msg);
    }

    private void sendMessage(ActionEvent event) {
        messenger.sendText(messageTextArea.getText());
        messageTextArea.clear();
    }

    public void stopMessenger() {
        messenger.stop();
    }
}
