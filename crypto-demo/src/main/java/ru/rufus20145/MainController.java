package ru.rufus20145;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ru.rufus20145.ciphers.CaesarCipher;
import ru.rufus20145.ciphers.DoublePermutationCipher;
import ru.rufus20145.ciphers.StringCipher;
import ru.rufus20145.ciphers.WhitstonCipher;
import ru.rufus20145.exceptions.EncryptionException;

public class MainController implements Initializable {
    private static final String[] variants = { "Шифр двойной перестановки", "Шифр Цезаря", "Шифр Уитстона" };

    @FXML
    private Button chooseFileButton;

    @FXML
    private ComboBox<String> cipherVariants;

    @FXML
    private Button decryptButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button generateNewKeyButton;

    @FXML
    private Button showKeyButton;

    @FXML
    private TextArea leftTextArea;

    @FXML
    private TextArea rightTextArea;

    @FXML
    private TextField textField1;

    @FXML
    private TextField textField2;

    @FXML
    private Label warningLabel;

    private StringCipher cipher;

    private DoublePermutationCipher doublePermutationCipher = new DoublePermutationCipher();
    private CaesarCipher caesarCipher = new CaesarCipher();
    private WhitstonCipher whitstonCipher = new WhitstonCipher();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> cipherNames = FXCollections.observableArrayList(variants);
        cipherVariants.setItems(cipherNames);
        cipherVariants.setOnAction(this::showKeyControls);
        encryptButton.setOnMouseClicked(this::encrypt);
        decryptButton.setOnMouseClicked(this::decrypt);
        generateNewKeyButton.setOnMouseClicked(this::generateNewKey);
        chooseFileButton.setOnMouseClicked(this::selectExistingKey);
        showKeyButton.setOnMouseClicked(this::showKey);
    }

    private void showKeyControls(ActionEvent actionEvent) {
        hideKeyControls();

        switch (cipherVariants.getSelectionModel().getSelectedItem()) {
            case "Шифр Цезаря":
                textField1.setVisible(true);
                textField1.setPromptText("Введите ключ");
                break;
            case "Шифр двойной перестановки":
                textField1.setVisible(true);
                textField1.setPromptText("Введите первую последовательность");
                textField2.setVisible(true);
                textField2.setPromptText("Введите вторую последовательность");
                break;
            case "Шифр Уитстона":
                chooseFileButton.setVisible(true);
                generateNewKeyButton.setVisible(true);
                break;
            default:
                break;
        }
    }

    private void hideKeyControls() {
        chooseFileButton.setVisible(false);
        generateNewKeyButton.setVisible(false);
        showKeyButton.setVisible(false);
        textField1.setVisible(false);
        textField2.setVisible(false);
        warningLabel.setText("");
    }

    private void encrypt(MouseEvent event) {
        warningLabel.setText("");
        if (cipherVariants.getSelectionModel().isEmpty()) {
            cipherVariants.requestFocus();
            warningLabel.setText("Выберите шифр");
            return;
        }
        try {
            cipher = getCipherByVariant(cipherVariants.getSelectionModel().getSelectedItem());
            String encText = cipher.encrypt(leftTextArea.getText());
            rightTextArea.setText(encText);
            leftTextArea.setText("");
        } catch (EncryptionException e) {
            System.out.println(e.getMessage());
        }
    }

    private void decrypt(MouseEvent event) {
        warningLabel.setText("");
        if (cipherVariants.getSelectionModel().isEmpty()) {
            cipherVariants.requestFocus();
            warningLabel.setText("Выберите шифр");
            return;
        }
        try {
            cipher = getCipherByVariant(cipherVariants.getSelectionModel().getSelectedItem());
            String decryptedText = cipher.decrypt(rightTextArea.getText());
            leftTextArea.setText(decryptedText);
            rightTextArea.setText("");
        } catch (EncryptionException e) {
            System.out.println(e.getMessage());
        }
    }

    // общий план: добавить в интерфейс метод проверки готовности шифратора
    private StringCipher getCipherByVariant(String variant) throws EncryptionException {
        switch (variant) {
            case "Шифр двойной перестановки":
                validateFieldForDoublePermutation(textField1);
                validateFieldForDoublePermutation(textField2);
                doublePermutationCipher.updateKey(textField1.getText(), textField2.getText());
                warningLabel.setText("");
                return doublePermutationCipher;
            case "Шифр Цезаря":
                int shift = validateFieldForCaesar(textField1);
                caesarCipher.updateKey(shift);
                warningLabel.setText("");
                return caesarCipher;
            case "Шифр Уитстона":
                prepareWhitstonCipher(whitstonCipher);
                warningLabel.setText("");
                return whitstonCipher;
            default:
                throw new IllegalArgumentException("Unknown variant");
        }
    }

    private void validateFieldForDoublePermutation(TextField textField) throws EncryptionException {
        String text = textField.getText();
        if (text == null || text.isEmpty()) {
            warningLabel.setText("Введите ключ");
            textField.requestFocus();
            throw new EncryptionException("%s is empty".formatted(textField.getId()));
        }
        String regex = "\\d+";
        if (!text.matches(regex)) {
            warningLabel.setText("Проверьте введенный ключ");
            textField.requestFocus();
            throw new EncryptionException("Text '%s' from %s doesn`t match format".formatted(text, textField.getId()));
        }

        char[] charArray = text.toCharArray();
        int[] intArray = new int[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            intArray[i] = Character.getNumericValue(charArray[i]);
        }

        // Проверяем, что нет пропусков
        for (int i = 1; i <= intArray.length; i++) {
            boolean found = false;
            for (int num : intArray) {
                if (num == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                warningLabel.setText("Проверьте введенный ключ");
                textField.requestFocus();
                throw new EncryptionException("%d is missing in input string".formatted(i));
            }
        }
        System.out.printf("%s is successfully validated.%n", textField.getId());
    }

    private int validateFieldForCaesar(TextField textField) {
        String text = textField.getText();
        if (text == null || text.isEmpty()) {
            warningLabel.setText("Введите ключ");
            textField.requestFocus();
            throw new EncryptionException("%s is empty".formatted(textField.getId()));
        }
        int number;
        try {
            number = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            warningLabel.setText("Проверьте введенный ключ");
            textField.requestFocus();
            throw new EncryptionException("%s doesn`t contain number".formatted(textField.getId()));
        }
        if (number < 0) {
            warningLabel.setText("Проверьте введенный ключ");
            textField.requestFocus();
            throw new EncryptionException(
                    "Number in %s must be >= 0. Now it`s %d".formatted(textField.getId(), number));
        }
        return number;
    }

    private void prepareWhitstonCipher(WhitstonCipher whitstonCipher) throws EncryptionException {
        if (whitstonCipher.isReady()) {
            warningLabel.setVisible(false);
            warningLabel.setText("");
            showKeyButton.setVisible(true);
            return;
        }

        warningLabel.setVisible(true);
        warningLabel.setText("Выберите файл ключа или создайте новый.");
        showKeyButton.setVisible(false);
        throw new EncryptionException("Whitston cipher is not ready");
    }

    private void generateNewKey(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите место сохранения файла с ключом");
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        File desktopFolder = new File(desktopPath);
        fileChooser.setInitialDirectory(desktopFolder);
        String randomFileName = "key_" + UUID.randomUUID().toString().substring(0, 8);
        fileChooser.setInitialFileName(randomFileName);
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Файлы ключа", "*.json"));
        File targetFile = fileChooser.showSaveDialog(null);
        if (targetFile == null) {
            System.out.println("TargetFile wasn`t chosen");
            return;
        }
        System.out.println(targetFile.getAbsolutePath());
        whitstonCipher.generateNewKey(targetFile.toPath());
        warningLabel.setVisible(false);
        warningLabel.setText("");
        showKeyButton.setVisible(true);
    }

    private void selectExistingKey(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл с ключом");
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        File desktopFolder = new File(desktopPath);
        fileChooser.setInitialDirectory(desktopFolder);
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Файлы ключа", "*.json"));
        File keyFile = fileChooser.showOpenDialog(null);
        if (keyFile == null) {
            System.out.println("KeyFile wasn`t chosen");
            return;
        }
        System.out.println(keyFile.getAbsolutePath());
        whitstonCipher.updateKey(keyFile.toPath());
        warningLabel.setVisible(false);
        warningLabel.setText("");
        showKeyButton.setVisible(true);
    }

    private void showKey(MouseEvent event) {
        String[] tables = whitstonCipher.getKey();
        leftTextArea.setText(tables[0]);
        rightTextArea.setText(tables[1]);
    }
}
