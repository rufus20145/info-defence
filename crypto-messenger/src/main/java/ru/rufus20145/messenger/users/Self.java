package ru.rufus20145.messenger.users;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.Getter;

@Getter
public final class Self extends User {
    private final PrivateKey privateKey;
    private InterfaceAddress network;
    private final Cipher decipher;

    public Self(String username, InterfaceAddress network, KeyPair keyPair) {
        super(username, network.getAddress(), keyPair.getPublic());
        this.network = network;
        this.privateKey = keyPair.getPrivate();
        try {
            this.decipher = Cipher.getInstance("RSA");
            this.decipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

    public InetAddress getBroadcast() {
        return network.getBroadcast();
    }

    public String decryptMessage(String encryptedMessage) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = decipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

}
