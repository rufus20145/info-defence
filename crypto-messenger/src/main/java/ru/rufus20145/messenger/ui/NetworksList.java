package ru.rufus20145.messenger.ui;

import java.net.InetAddress;
import java.net.InterfaceAddress;

import javafx.scene.control.ListCell;

final class NetworksList extends ListCell<InterfaceAddress> {
    @Override
    public void updateItem(InterfaceAddress ia, boolean empty) {
        super.updateItem(ia, empty);
        if (empty || ia == null) {
            setText(null);
        } else {
            setText(getNetwork(ia));
        }
    }

    private String getNetwork(InterfaceAddress ia) {
        InetAddress inetAddress = ia.getAddress();
        short networkPrefixLength = ia.getNetworkPrefixLength();
        byte[] addressBytes = inetAddress.getAddress();

        int fullBytes = networkPrefixLength / 8;
        byte[] networkBytes = new byte[addressBytes.length];
        System.arraycopy(addressBytes, 0, networkBytes, 0, fullBytes);

        int remainingBits = networkPrefixLength % 8;
        byte mask = (byte) (0xFF << (8 - remainingBits));
        networkBytes[fullBytes] = (byte) (addressBytes[fullBytes] & mask);

        StringBuilder networkSB = new StringBuilder();
        for (int i = 0; i < networkBytes.length; i++) {
            networkSB.append((networkBytes[i] & 0xFF));
            if (i < networkBytes.length - 1) {
                networkSB.append(".");
            }
        }

        return networkSB.toString() + "/" + networkPrefixLength;
    }
}