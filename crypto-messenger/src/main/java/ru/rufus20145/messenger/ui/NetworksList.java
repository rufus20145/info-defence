package ru.rufus20145.messenger.ui;

import java.net.InterfaceAddress;

import javafx.scene.control.ListCell;

final class NetworksList extends ListCell<InterfaceAddress> {
    @Override
    public void updateItem(InterfaceAddress ia, boolean empty) {
        super.updateItem(ia, empty);
        if (empty || ia == null) {
            setText(null);
        } else {
            setText(ia.getAddress().getHostAddress() + "/" + ia.getNetworkPrefixLength());
        }
    }
}