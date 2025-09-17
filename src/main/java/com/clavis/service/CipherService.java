package com.clavis.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CipherService {

    public static class Puzzle {
        public String sentence;
        public String scrambled;
        public String hint;
    }

    private final Map<String, List<Puzzle>> puzzlesByDifficulty = new HashMap<>();

    public CipherService() {
        // EASY puzzles
        puzzlesByDifficulty.put("EASY", Arrays.asList(
            make("hello world", "Common greeting"),
            make("java spring", "Popular backend framework")
        ));

        // MEDIUM puzzles
        puzzlesByDifficulty.put("MEDIUM", Arrays.asList(
            make("encryption is fun", "Something about security"),
            make("database management", "What holds your data")
        ));

        // HARD puzzles
        puzzlesByDifficulty.put("HARD", Arrays.asList(
            make("cryptography unlocks secrets", "A science of secrecy"),
            make("permutation and combination", "Math behind arrangements")
        ));
    }

    private Puzzle make(String sentence, String hint) {
        Puzzle p = new Puzzle();
        p.sentence = sentence;
        p.hint = hint;
        p.scrambled = scramble(sentence);
        return p;
    }

   private String scramble(String sentence) {
    String[] words = sentence.split(" ");
    Random rand = new Random();
    List<String> scrambledWords = new ArrayList<>();

    for (String word : words) {
        if (word.length() <= 3) {
            // keep very short words readable
            scrambledWords.add(word);
        } else {
            List<Character> chars = new ArrayList<>();
            for (char c : word.toCharArray()) {
                chars.add(c);
            }
            Collections.shuffle(chars, rand);
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                sb.append(c);
            }
            scrambledWords.add(sb.toString());
        }
    }

    return String.join(" ", scrambledWords);
}


    // 🔹 NEW overloaded method
    public Puzzle getRandomPuzzle(String difficulty) {
        List<Puzzle> puzzles = puzzlesByDifficulty.getOrDefault(difficulty.toUpperCase(), puzzlesByDifficulty.get("EASY"));
        return puzzles.get(new Random().nextInt(puzzles.size()));
    }

    // Keep old method for backward compatibility
    public Puzzle getRandomPuzzle() {
        return getRandomPuzzle("EASY");
    }
}
