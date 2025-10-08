package server.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordManager {
    private static List<String> words = new ArrayList<>();

    static {
        try {
            words = Files.readAllLines(Paths.get("res/words.txt"));
            words.replaceAll(String::trim);
            words.removeIf(word -> word.length() == 0);
            System.out.println("Loaded " + words.size() + " words");
        } catch (IOException e) {
            System.err.println("Error loading words: " + e.getMessage());
        }
    }

    public static String getRandomWord(List<String> usedWords) {
        List<String> availableWords = new ArrayList<>(words);
        availableWords.removeAll(usedWords);

        if (availableWords.isEmpty()) {
            availableWords = new ArrayList<>(words);
        }

        Collections.shuffle(availableWords);
        return availableWords.get(0).toUpperCase();
    }
}
