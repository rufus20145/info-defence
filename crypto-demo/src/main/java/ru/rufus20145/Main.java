package ru.rufus20145;

public class Main {
    public static void main(String[] args) {
        System.out.println("Application start");
    }

    static class StringsDoNotMatchException extends RuntimeException {
        public StringsDoNotMatchException(String message) {
            super(message);
        }
    }
}
