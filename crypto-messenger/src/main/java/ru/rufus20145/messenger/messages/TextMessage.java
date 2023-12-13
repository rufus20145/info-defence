package ru.rufus20145.messenger.messages;

import java.util.Base64;

import lombok.Getter;
import ru.rufus20145.messenger.users.Self;
import ru.rufus20145.messenger.users.User;

public class TextMessage extends AbsMessage {
    @Getter
    private String encryptedText;

    /**
     * Конструктор для создания сообщения из зашифрованного текста
     * 
     * @param sender        отправитель
     * @param self          получатель (локальный аккаунт)
     * @param encryptedText зашифрованный текст
     */
    public TextMessage(User sender, Self self, String encryptedText) {
        super(MessageType.TEXT, self.decryptMessage(encryptedText), sender, self);
        this.encryptedText = encryptedText;
    }

    /**
     * Конструктор для создания сообщения из незашифрованного текста
     * 
     * @param text     открытый текст
     * @param sender   отправитель
     * @param receiver получатель
     */
    public TextMessage(String text, User sender, User receiver) {
        super(MessageType.TEXT, text, sender, receiver);
        this.encryptedText = Base64.getEncoder().encodeToString(receiver.encrypt(text));
    }

    @Override
    protected String getPrepared() {
        return type + ":" + sender.getUsername() + ":" + getTextEncrypted();
    }

    private String getTextEncrypted() {
        return Base64.getEncoder().encodeToString(receiver.encrypt(text));
    }
}
