package ru.rufus20145.ciphers;

/**
 * Интерфейс StringCipher представляет методы для шифрования и дешифрования
 * строк.
 */
public interface StringCipher {
    /**
     * Шифрует переданный текст.
     *
     * @param plainText Исходный текст, который необходимо зашифровать.
     * @param key       Ключ, используемый для шифрования.
     * @return Зашифрованный текст.
     */
    String encrypt(String plainText);

    /**
     * Дешифрует переданный зашифрованный текст.
     *
     * @param cipherText Зашифрованный текст, который необходимо дешифровать.
     * @param key        Ключ, используемый для дешифрования.
     * @return Расшифрованный текст.
     */
    String decrypt(String cipherText);

    /**
     * Возвращает название алгоритма шифрования
     * 
     * @return название алгоритма шифрования
     */
    String getName();
}
