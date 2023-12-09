package ru.rufus20145.demo.ciphers;

/**
 * Абстрактный класс шифров перестановки
 */
public abstract class AbsTranspositionCipher extends AbsCipher {
    protected AbsTranspositionCipher(String cipherName) {
        super(cipherName);
    }
}
