package ru.rufus20145.ciphers;

// TODO провести рефакторинг, удалить повторяющиеся фрагменты кода
public class DoublePermutationCipher extends TranspositionAbsCipher {
    private int[] sequence1;
    private int[] sequence2;

    public DoublePermutationCipher(String sequence1, String sequence2) {
        super("Шифр двойной перестановки");

        this.sequence1 = convertStringToIntArray(sequence1);
        this.sequence2 = convertStringToIntArray(sequence2);
    }

    public void updateKey(String sequence1, String sequence2) {
        this.sequence1 = convertStringToIntArray(sequence1);
        this.sequence2 = convertStringToIntArray(sequence2);
    }

    @Override
    public String encrypt(String plainText) {
        String[] plainParts = splitString(plainText, sequence1.length * sequence2.length);
        String[] encryptedParts = new String[plainParts.length];

        for (int i = 0; i < plainParts.length; i++) {
            encryptedParts[i] = encryptPart(plainParts[i]);
        }
        return String.join("", encryptedParts);
    }

    @Override
    public String decrypt(String encryptedText) {
        String[] encryptedParts = splitString(encryptedText, sequence1.length * sequence2.length);
        String[] decryptedParts = new String[encryptedParts.length];

        for (int i = 0; i < encryptedParts.length; i++) {
            decryptedParts[i] = decryptPart(encryptedParts[i]);
        }

        return String.join("", decryptedParts);
    }

    private String decryptPart(String string) {
        char[][] symbols = fillTable(string);

        char[][] midTable = new char[symbols.length][symbols[0].length];
        for (int i = 0; i < symbols.length; i++) {
            for (int j = 0; j < symbols[i].length; j++) {
                midTable[i][j] = symbols[i][sequence2[j]];
            }
        }

        char[][] resTable = new char[symbols.length][symbols[0].length];
        for (int i = 0; i < symbols.length; i++) {
            resTable[i] = midTable[sequence1[i]];
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resTable.length; i++) {
            sb.append(resTable[i]);
        }

        return sb.toString();
    }

    private String encryptPart(String string) {
        char[][] symbols = fillTable(string);

        char[][] midTable = new char[symbols.length][symbols[0].length];
        for (int i = 0; i < symbols.length; i++) {
            midTable[sequence1[i]] = symbols[i];
        }

        char[][] resTable = new char[symbols.length][symbols[0].length];
        for (int i = 0; i < midTable.length; i++) {
            char[] line = midTable[i];
            for (int j = 0; j < line.length; j++) {
                resTable[i][sequence2[j]] = midTable[i][j];
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resTable.length; i++) {
            sb.append(resTable[i]);
        }

        return sb.toString();
    }

    private int[] convertStringToIntArray(String inputString) {
        int[] intArray = new int[inputString.length()];

        for (int i = 0; i < inputString.length(); i++) {
            char digitChar = inputString.charAt(i);
            int digit = Character.getNumericValue(digitChar) - 1;
            intArray[i] = digit;
        }

        return intArray;
    }

    private char[][] fillTable(String string) {
        char[][] symbols = new char[sequence2.length][sequence1.length];
        for (int i = 0; i < string.length(); i++) {
            symbols[i / sequence1.length][i % sequence1.length] = string.charAt(i);
        }
        return symbols;
    }

    private String[] splitString(String inputString, int chunkSize) {
        int numberOfChunks = (int) Math.ceil((double) inputString.length() / chunkSize);
        String[] result = new String[numberOfChunks];

        int index = 0;
        for (int i = 0; i < inputString.length(); i += chunkSize) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < chunkSize * (index + 1); j++) {
                sb.append(inputString.charAt(j % inputString.length()));
            }
            result[index++] = sb.toString();
        }
        return result;
    }
}
