package ru.rufus20145.demo.exceptions;

public class EncryptionException extends IllegalArgumentException {

    public EncryptionException() {
        super();
    }

    public EncryptionException(String message) {
        super(message);
    }
}
