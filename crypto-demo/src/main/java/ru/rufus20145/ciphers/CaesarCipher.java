package ru.rufus20145.ciphers;

import ru.rufus20145.exceptions.EncryptionException;

public class CaesarCipher extends AbsSubstitutionCipher {

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
            int indexOf = ALPHABET.indexOf(Character.toLowerCase(ch));
            if (indexOf == -1) {
                throw new EncryptionException("Symbol %c is not supported".formatted(ch));
            } else {
                int index = (indexOf + shift) % ALPHABET_SIZE;
                char newChar = ALPHABET.charAt(index);
                encryptedText.append(Character.isUpperCase(ch) ? Character.toUpperCase(newChar) : newChar);
            }
        }
        return encryptedText.toString();
    }

    @Override
    public String decrypt(String encryptedText) {
        StringBuilder decryptedText = new StringBuilder();
        for (char ch : encryptedText.toCharArray()) {
            int index = (ALPHABET.indexOf(Character.toLowerCase(ch)) - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
            char newChar = ALPHABET.charAt(index);
            decryptedText.append(Character.isUpperCase(ch) ? Character.toUpperCase(newChar) : newChar);
        }
        return decryptedText.toString();
    }
}
