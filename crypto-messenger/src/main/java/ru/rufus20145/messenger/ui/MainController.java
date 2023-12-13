package ru.rufus20145.messenger.ui;

import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
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
    private Button showKeysButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usersOnlineLabel;

    private Messenger messenger;

    private String buffer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usersOnlineLabel.setText("0");
        messagesListView.setCellFactory(new DecryptedMessageView());
        sendMessageButton.setOnMouseClicked(e -> sendMessage());
        changeViewButton.setOnMouseClicked(e -> changeView());
        showKeysButton.setOnMouseClicked(e -> showKeys());
        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        Self self = createSelf();
        if (self != null) {
            messenger = new Messenger(10435, self, this);
            usernameLabel.setText(self.getUsername());
        } else {
            throw new IllegalStateException("User data was not entered");
        }
    }

    private void showKeys() {
        if (!messageTextArea.getText().matches(
                "^Public key: Sun RSA public key,(.*[\n\r]){5}Private key: SunRsaSign RSA private CRT key(.*[\n\r]){3}.*$")) {
            buffer = messageTextArea.getText();
            KeyPair pair = messenger.getMyKeyPair();
            String formattedKey = "Public key: %s%n%nPrivate key: %s".formatted(
                    pair.getPublic().toString(), pair.getPrivate());
            messageTextArea.setText(formattedKey);
            showKeysButton.setText("Вернуть текст сообщения");
        } else {
            messageTextArea.setText(buffer);
            showKeysButton.setText("Показать пару ключей");
        }
    }

    private void sendMessage() {
        messenger.sendTextMessage(messageTextArea.getText());
        messageTextArea.clear();
    }

    private void changeView() {
        if (messagesListView.getCellFactory().getClass().equals(DecryptedMessageView.class)) {
            messagesListView.setCellFactory(new EncryptedMessageView());
            changeViewButton.setText("Расшифрованный вид");
        } else {
            messagesListView.setCellFactory(new DecryptedMessageView());
            changeViewButton.setText("Зашифрованный вид");
        }
    }

    private Self createSelf() {
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

    public void stopMessenger() {
        messenger.stop();
    }

    public void updateNumberOfUsers(int number) {
        usersOnlineLabel.setText(String.valueOf(number));
    }
}
