package com.yapm.android;

public class EncryptionManager {

    public static String ROT13(String text) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);

            if (ch >= 'a' && ch <= 'z') {
                ch = (char) ('a' + (ch - 'a' + 13) % 26);
            }
            else if (ch >= 'A' && ch <= 'Z') {
                ch = (char) ('A' + (ch - 'A' + 13) % 26);
            }

            result.append(ch);
        }

        return result.toString();
    }
}
