package ru.rufus20145.messenger.users;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class User {
    private final String username;
    private final InetAddress ipAddress;
    private final PublicKey publicKey;
    private final Cipher cipher;

    public User(String username, InetAddress ipAddress, PublicKey publicKey) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.publicKey = publicKey;
        try {
            this.cipher = Cipher.getInstance("RSA");
            this.cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] encrypt(String text) {
        try {
            return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException(e);
        }
    }
}