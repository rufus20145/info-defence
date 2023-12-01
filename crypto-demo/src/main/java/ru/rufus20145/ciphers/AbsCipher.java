package ru.rufus20145.ciphers;

import lombok.Data;

@Data
public abstract class AbsCipher implements StringCipher {
    private final String name;
}
