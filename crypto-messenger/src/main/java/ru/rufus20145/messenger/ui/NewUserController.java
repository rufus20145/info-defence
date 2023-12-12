package ru.rufus20145.messenger.ui;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Setter;
import ru.rufus20145.messenger.users.Self;

public class NewUserController implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private ComboBox<InterfaceAddress> networksComboBox;

    @FXML
    public Label errorLabel;

    @Setter
    private Dialog<Self> dialog;

    public void processResults() {
        KeyPair keyPair = generateKeyPair();
        Self self = new Self(usernameTextField.getText(),
                networksComboBox.getSelectionModel().getSelectedItem(), keyPair);
        dialog.setResult(self);
        dialog.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networksComboBox.setButtonCell(new NetworksList());
        networksComboBox.setCellFactory(param -> new NetworksList());
        networksComboBox.getItems().addAll(getIpV4Interfaces());

    }

    private void showAlert(String message) {
        errorLabel.setText(message);
    }

    private List<InterfaceAddress> getIpV4Interfaces() {
        try {
            List<InterfaceAddress> result = new ArrayList<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (!networkInterface.isLoopback()) {
                    result.addAll(networkInterface.getInterfaceAddresses().stream()
                            .filter(ia -> ia.getAddress().getAddress().length == 4).toList());
                }
            }
            return result;
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
    }

    private KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean validateFields() {
        if (!usernameTextField.getText().matches("^[\\w]{3,15}$")) {
            showAlert("Проверьте имя пользователя");
            return false;
        } else if (networksComboBox.getSelectionModel().getSelectedItem() == null) {
            showAlert("Выберите подсеть, в которой будет работать приложение");
            return false;
        }
        return true;
    }
}
