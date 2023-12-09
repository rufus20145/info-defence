package ru.rufus20145.ciphers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.rufus20145.exceptions.EncryptionException;

//TODO провести рефакторинг, раскидать классы по файлам, устранить дублирование кода
public class WhitstonCipher extends AbsSubstitutionCipher {

    @RequiredArgsConstructor
    private class Bigram {
        final char first;
        final boolean firstCase;
        final char second;
        final boolean secondCase;

        String getPlain() {
            char firstChar = firstCase ? Character.toUpperCase(first) : first;
            char secondChar = secondCase ? Character.toUpperCase(second) : second;
            return String.valueOf(firstChar) + secondChar;
        }

        String getEnc() {
            StringBuilder sb = new StringBuilder(firstCase ? "&" : "");
            sb.append(first).append(secondCase ? "&" : "").append(second);
            return sb.toString();
        }
    }

    @RequiredArgsConstructor
    private class Coordinates {
        final int row;
        final int col;
    }

    @RequiredArgsConstructor
    private class Table {
        private final char[][] elements;

        public Coordinates getCoordinates(char ch) {
            for (int i = 0; i < elements.length; i++) {
                for (int j = 0; j < elements[i].length; j++) {
                    if (elements[i][j] == Character.toLowerCase(ch)) {
                        return new Coordinates(i, j/* , Character.isUpperCase(ch) */);
                    }
                }
            }
            throw new EncryptionException("Symbol %c is not supported".formatted(ch));
        }

        public char getChar(int col, int row) {
            return elements[col][row];
        }

        public String getFormatted() {
            StringBuilder formattedTable = new StringBuilder();
            for (int i = 0; i < elements.length; i++) {
                for (int j = 0; j < elements[i].length; j++) {
                    formattedTable.append(elements[i][j] == '\n' ? "\\n" : elements[i][j]).append(" ");
                }
                formattedTable.append("\n");
            }
            return formattedTable.toString();
        }
    }

    @AllArgsConstructor
    private class WhitstonKey {
        private Table table1;
        private Table table2;
    }

    private Table table1;
    private Table table2;

    public WhitstonCipher(Path sourceFile) {
        this();
        updateKey(sourceFile);
    }

    public WhitstonCipher() {
        super("Шифр Уитстона");
    }

    public void updateKey() {
        this.table1 = generateTable();
        this.table2 = generateTable();

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("C:\\projects\\key.json"),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            Gson gson = new Gson();
            String json = gson.toJson(new WhitstonKey(this.table1, this.table2));
            writer.write(json);
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
        }
    }

    public void updateKey(Path sourceFile) {
        Gson gson = new Gson();
        try (BufferedReader reader = Files.newBufferedReader(sourceFile)) {
            WhitstonKey key = gson.fromJson(reader, WhitstonKey.class);
            this.table1 = key.table1;
            this.table2 = key.table2;
        } catch (IOException e) {
            System.out.println("IOException while reading file " + e.getMessage());
        }
    }

    public String[] getKey() {
        String[] tables = new String[2];

        tables[0] = table1.getFormatted();
        tables[1] = table2.getFormatted();

        return tables;
    }

    @Override
    public String encrypt(String plainText) {
        if (plainText.length() % 2 != 0) {
            plainText = plainText + " ";
        }
        List<Bigram> plainBigrams = splitIntoBigrams(plainText);
        Bigram[] encryptedBigrams = new Bigram[plainBigrams.size()];
        for (int i = 0; i < plainBigrams.size(); i++) {
            encryptedBigrams[i] = encryptBigram(plainBigrams.get(i));
        }

        return Arrays.stream(encryptedBigrams).map(Bigram::getEnc).collect(Collectors.joining(""));
    }

    @Override
    public String decrypt(String encryptedText) {
        List<Bigram> encryptedBigrams = splitIntoBigrams(encryptedText);
        Bigram[] decryptedBigrams = new Bigram[encryptedBigrams.size()];
        for (int i = 0; i < encryptedBigrams.size(); i++) {
            decryptedBigrams[i] = decryptBigram(encryptedBigrams.get(i));
        }

        return Arrays.stream(decryptedBigrams).map(Bigram::getPlain).collect(Collectors.joining("")).trim();
    }

    private List<Bigram> splitIntoBigrams(String input) {
        int length = input.length();
        List<Bigram> bigrams = new ArrayList<>();
        for (int i = 0; i < length; i += 2) {
            char first;
            boolean firstUpperCase;
            char second;
            boolean secondUpperCase;
            if (input.charAt(i) == '&') {
                firstUpperCase = true;
                first = input.charAt(++i);
            } else {
                first = input.charAt(i);
                firstUpperCase = Character.isUpperCase(first);
            }
            if (input.charAt(i + 1) == '&') {
                secondUpperCase = true;
                second = input.charAt(++i + 1);
            } else {
                second = input.charAt(i + 1);
                secondUpperCase = Character.isUpperCase(second);
            }
            bigrams.add(new Bigram(first, firstUpperCase, second, secondUpperCase));
        }
        return bigrams;
    }

    private Table generateTable() {
        int size = (int) Math.sqrt(ALPHABET.length());
        char[][] elements = new char[size][size];

        List<Character> charList = new String(ALPHABET.toCharArray()).chars().mapToObj(i1 -> (char) i1)
                .collect(Collectors.toList());
        Collections.shuffle(charList);

        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                elements[i][j] = charList.get(index++);
            }
        }

        return new Table(elements);
    }

    private Bigram encryptBigram(Bigram src) {
        Coordinates firstCoordinate = table1.getCoordinates(src.first);
        Coordinates secondCoordinate = table2.getCoordinates(src.second);

        char first = table1.getChar(firstCoordinate.col, secondCoordinate.row);
        char second = table2.getChar(secondCoordinate.col, firstCoordinate.row);

        return new Bigram(first, src.firstCase, second, src.secondCase);
    }

    private Bigram decryptBigram(Bigram src) {
        Coordinates firstCoordinate = table1.getCoordinates(src.first);
        Coordinates secondCoordinate = table2.getCoordinates(src.second);

        char first = table1.getChar(secondCoordinate.col, firstCoordinate.row);
        char second = table2.getChar(firstCoordinate.col, secondCoordinate.row);

        return new Bigram(first, src.firstCase, second, src.secondCase);
    }
}
