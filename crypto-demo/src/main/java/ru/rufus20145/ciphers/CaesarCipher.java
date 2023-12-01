package ru.rufus20145.ciphers;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CaesarCipher extends AbsCipher {
    public static final int ALPHABET_SIZE = 33;
    private static final String LOWERCASE_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String UPPERCASE_ALPHABET = LOWERCASE_ALPHABET.toUpperCase();

    private Integer shift;

    public CaesarCipher(Integer shift) {
        super("Шифр Цезаря");

        this.shift = shift % ALPHABET_SIZE;
    }

    public void updateKey(int shift) {
        this.shift = shift % ALPHABET_SIZE;
    }

    @Override
    public String encrypt(String plainText) {
        StringBuilder encryptedText = new StringBuilder();
        for (char ch : plainText.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                int index = (LOWERCASE_ALPHABET.indexOf(ch) + shift) % ALPHABET_SIZE;
                encryptedText.append(LOWERCASE_ALPHABET.charAt(index));
            } else if (Character.isUpperCase(ch)) {
                int index = (UPPERCASE_ALPHABET.indexOf(ch) + shift) % ALPHABET_SIZE;
                encryptedText.append(UPPERCASE_ALPHABET.charAt(index));
            } else {
                encryptedText.append(ch);
            }
        }
        return encryptedText.toString();
    }

    @Override
    public String decrypt(String cipherText) {
        StringBuilder decryptedText = new StringBuilder();
        for (char ch : cipherText.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                int index = (LOWERCASE_ALPHABET.indexOf(ch) - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
                decryptedText.append(LOWERCASE_ALPHABET.charAt(index));
            } else if (Character.isUpperCase(ch)) {
                int index = (UPPERCASE_ALPHABET.indexOf(ch) - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
                decryptedText.append(UPPERCASE_ALPHABET.charAt(index));
            } else {
                decryptedText.append(ch);
            }
        }
        return decryptedText.toString();
    }
}
