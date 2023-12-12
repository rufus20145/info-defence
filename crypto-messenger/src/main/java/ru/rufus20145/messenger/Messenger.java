package ru.rufus20145.messenger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ru.rufus20145.messenger.messages.Message;
import ru.rufus20145.messenger.messages.StartMessage;
import ru.rufus20145.messenger.messages.StopMessage;
import ru.rufus20145.messenger.messages.TextMessage;
import ru.rufus20145.messenger.network.Receiver;
import ru.rufus20145.messenger.network.Sender;
import ru.rufus20145.messenger.ui.MainController;
import ru.rufus20145.messenger.users.Self;
import ru.rufus20145.messenger.users.User;

public class Messenger {
    private Receiver receiver;
    private Sender sender;
    private ConcurrentMap<String, User> users;
    private MainController controller;
    private Self self;

    public Messenger(int port, Self self, MainController controller) {
        this.self = self;
        this.controller = controller;
        this.users = new ConcurrentHashMap<>();
        receiver = new Receiver(self.getIpAddress(), port, self, this);
        sender = new Sender(port);

        receiver.start();
        sender.start();

        sender.submitMessage(new StartMessage(self));
    }

    public void stop() {
        receiver.stopWorking();
        sender.stopWorking();
        sender.submitMessage(new StopMessage(self));
    }

    public void sendText(String text) {
        System.out.println("Sending text " + text);
        for (User user : users.values()) {
            Message msg = new TextMessage(text, self, user);
            sender.submitMessage(msg);
        }
        showNewMessage(new TextMessage(text, self, self));
    }

    public void addUser(User newUser) {
        users.put(newUser.getUsername(), newUser);
        Message msg = new StartMessage(self, newUser);
        sender.submitMessage(msg);
    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

    public void showNewMessage(TextMessage msg) {
        controller.showNewMessage(msg);
    }

    public void removeUserByUsername(String username) {
        users.remove(username);
    }
}
