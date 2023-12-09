package ru.rufus20145.demo.ciphers;

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
     * @param encryptedText Зашифрованный текст, который необходимо дешифровать.
     * @param key        Ключ, используемый для дешифрования.
     * @return Расшифрованный текст.
     */
    String decrypt(String encryptedText);

    /**
     * Возвращает название алгоритма шифрования
     * 
     * @return название алгоритма шифрования
     */
    String getName();
}
