package ru.rufus20145.ciphers;

import ru.rufus20145.exceptions.EncryptionException;

public class CaesarCipher extends AbsSubstitutionCipher {

    private Integer shift;

    public CaesarCipher(Integer shift) {
        this();
        updateKey(shift);
    }

    public CaesarCipher() {
        super("Шифр Цезаря");
    }

    public void updateKey(int shift) {
        if (shift < 0) {
            throw new IllegalArgumentException("Shift must be >= 0");
        }

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
                encryptedText.append(Character.isUpperCase(ch) ? "&" : "").append(newChar);
            }
        }
        return encryptedText.toString();
    }

    @Override
    public String decrypt(String encryptedText) {
        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < encryptedText.length(); i++) {
            char ch = encryptedText.charAt(i);
            boolean isUpperCase = false;
            if (ch == '&') {
                isUpperCase = true;
                ch = encryptedText.charAt(++i);
            }
            int index = (ALPHABET.indexOf(Character.toLowerCase(ch)) - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
            char newChar = ALPHABET.charAt(index);
            decryptedText.append(isUpperCase ? Character.toUpperCase(newChar) : newChar);
        }
        return decryptedText.toString();
    }
}
