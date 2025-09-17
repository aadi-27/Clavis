package com.clavis.service;

import com.clavis.model.Puzzle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PuzzleService {
    private final Random random = new Random();

    private final List<Puzzle> easyPuzzles = List.of(
            new Puzzle("Clavis is fun", "ivsCla si nfu", "Fun intro puzzle"),
            new Puzzle("Java is powerful", "avaJ si wrupefol", "Programming language"),
            new Puzzle("I love coding", "veoI ldoc ing", "Something devs enjoy")
    );

    private final List<Puzzle> mediumPuzzles = List.of(
            new Puzzle("Knowledge is power", "wledgeKno is reopw", "Famous quote"),
            new Puzzle("Spring Boot makes life easier", "Boot Spring makes life easier", "Java framework"),
            new Puzzle("Security is important", "ySecurit is portantim", "Needed for safety")
    );

    private final List<Puzzle> hardPuzzles = List.of(
            new Puzzle("The quick brown fox jumps over the lazy dog",
                    "quick The fox brown over jumps dog lazy the", "Contains all letters"),
            new Puzzle("To be or not to be that is the question",
                    "or To the not be question is that to be", "Shakespeare"),
            new Puzzle("With great power comes great responsibility",
                    "power With comes great great sityreponbils", "Spider-Man quote")
    );

    public Puzzle getPuzzle(String difficulty) {
        switch (difficulty) {
            case "MEDIUM":
                return mediumPuzzles.get(random.nextInt(mediumPuzzles.size()));
            case "HARD":
                return hardPuzzles.get(random.nextInt(hardPuzzles.size()));
            default:
                return easyPuzzles.get(random.nextInt(easyPuzzles.size()));
        }
    }
}
