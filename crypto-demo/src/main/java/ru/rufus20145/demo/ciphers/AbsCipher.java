package ru.rufus20145.demo.ciphers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class AbsCipher implements StringCipher {
    private final String name;
}
