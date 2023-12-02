package ru.rufus20145.ciphers;

public abstract class SubstitutionAbsCipher extends AbsCipher {
    protected static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890 ,.!?:;";
    protected static final int ALPHABET_SIZE = ALPHABET.length();

    protected SubstitutionAbsCipher(String cipherName) {
        super(cipherName);
    }
}
