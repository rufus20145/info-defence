package ru.rufus20145.messenger.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
    private Button changeViewButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usersOnlineLabel;

    private Messenger messenger;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersOnlineLabel.setText("0");
        messagesListView.setCellFactory(new DecryptedMessageView());
        sendMessageButton.setOnMouseClicked(this::sendMessage);
        changeViewButton.setOnMouseClicked(this::changeView);
        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessageByEnter();
            }
        });

        Self self = showCreatingSelfDialog();
        if (self != null) {
            messenger = new Messenger(10435, self, this);
            usernameLabel.setText(self.getUsername());
        } else {
            throw new IllegalStateException("User data was not entered");
        }
    }

    private void changeView(MouseEvent event) {
        if (messagesListView.getCellFactory().getClass().equals(DecryptedMessageView.class)) {
            messagesListView.setCellFactory(new EncryptedMessageView());
            changeViewButton.setText("Show dec");
        } else {
            messagesListView.setCellFactory(new DecryptedMessageView());
            changeViewButton.setText("Show dec");
        }
    }

    private void sendMessageByEnter() {
        messenger.sendText(messageTextArea.getText());
        messageTextArea.clear();
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
        Platform.runLater(() -> messagesListView.getItems().add(msg));
    }

    private void sendMessage(MouseEvent event) {
        messenger.sendText(messageTextArea.getText());
        messageTextArea.clear();
    }

    public void stopMessenger() {
        messenger.stop();
    }

    public void updateNumberOfUsers(int number) {
        usersOnlineLabel.setText(String.valueOf(number));
    }
}
