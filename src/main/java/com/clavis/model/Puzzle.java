package com.clavis.model;

public class Puzzle {
    private String original;
    private String scrambled;
    private String hint;

    public Puzzle(String original, String scrambled, String hint) {
        this.original = original;
        this.scrambled = scrambled;
        this.hint = hint;
    }

    public String getOriginal() { return original; }
    public String getScrambled() { return scrambled; }
    public String getHint() { return hint; }
}
