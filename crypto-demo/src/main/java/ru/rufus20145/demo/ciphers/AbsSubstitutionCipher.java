package ru.rufus20145.demo.ciphers;

/**
 * Абстрактный класс шифров подстановки
 */
public abstract class AbsSubstitutionCipher extends AbsCipher {
    /**
     * Поддерживаемый алфавит
     */
    protected static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя1234567890 _,;:.!?+-*/%^()=@#\n\"";
    protected static final int ALPHABET_SIZE = ALPHABET.length();

    protected AbsSubstitutionCipher(String cipherName) {
        super(cipherName);
    }
}
