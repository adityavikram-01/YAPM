package com.yapm.android;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class AuthenticationManager {

    private static final String PREF_NAME = "YAPM_AUTH";
    private static final String MASTER_KEY = "master_phrase";

    private static final int WORD_COUNT = 12;

    private static final Random random = new Random();


    // Read the 2048 words from the assets file.
    private static List<String> getWordList(Context context)
            throws IOException {

        List<String> words = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        context.getAssets().open("bip39_english.txt")
                )
        );

        String word;

        while ((word = reader.readLine()) != null) {
            word = word.trim();

            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        reader.close();

        if (words.size() != 2048) {
            throw new IOException("Invalid dictionary file.");
        }

        return words;
    }


    // Generate a random 12-word master phrase.
    public static String generateMasterPhrase(Context context)
            throws IOException {

        List<String> words = getWordList(context);

        StringBuilder phrase = new StringBuilder();

        for (int i = 0; i < WORD_COUNT; i++) {

            int index = random.nextInt(words.size());

            phrase.append(words.get(index));

            if (i < WORD_COUNT - 1) {
                phrase.append(" ");
            }
        }

        return phrase.toString();
    }


    // Check whether the master phrase has already been saved.
    public static boolean isSetup(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME, Context.MODE_PRIVATE
                );

        return preferences.contains(MASTER_KEY);
    }


    // Save the master phrase in plaintext.
    public static boolean saveMasterPhrase(
            Context context, String phrase) {

        if (phrase == null) {
            return false;
        }

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME, Context.MODE_PRIVATE
                );

        return preferences.edit()
                .putString(MASTER_KEY, phrase.trim())
                .commit();
    }


    // Verify the phrase entered by the user.
    public static boolean verifyMasterPhrase(
            Context context, String enteredPhrase) {

        if (enteredPhrase == null) {
            return false;
        }

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME, Context.MODE_PRIVATE
                );

        String savedPhrase =
                preferences.getString(MASTER_KEY, null);

        if (savedPhrase == null) {
            return false;
        }

        return savedPhrase.equalsIgnoreCase(
                enteredPhrase.trim().replaceAll("\\s+", " ")
        );
    }


    // Check that the phrase contains 12 words from the dictionary.
    public static boolean isValidPhrase(
            Context context, String phrase) {

        if (phrase == null) {
            return false;
        }

        String[] enteredWords =
                phrase.trim().toLowerCase().split("\\s+");

        if (enteredWords.length != WORD_COUNT) {
            return false;
        }

        try {

            Set<String> dictionary =
                    new HashSet<>(getWordList(context));

            for (String word : enteredWords) {

                if (!dictionary.contains(word)) {
                    return false;
                }
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }
}
