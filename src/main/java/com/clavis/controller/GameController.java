package com.clavis.controller;

import com.clavis.model.Score;
import com.clavis.model.User;
import com.clavis.repository.ScoreRepository;
import com.clavis.repository.UserRepository;
import com.clavis.service.CipherService;
import com.clavis.service.CipherService.Puzzle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Controller
public class GameController {

    private final UserRepository userRepo;
    private final ScoreRepository scoreRepo;
    private final CipherService cipherService;

    public GameController(UserRepository userRepo, ScoreRepository scoreRepo, CipherService cipherService) {
        this.userRepo = userRepo;
        this.scoreRepo = scoreRepo;
        this.cipherService = cipherService;
    }

    // session attribute keys
    private static final String PUZZLE_KEY = "clavis.puzzle";
    private static final String START_MS_KEY = "clavis.startMs";
    private static final String ATTEMPTS_KEY = "clavis.attempts";
    private static final String DIFFICULTY_KEY = "clavis.difficulty";

    // show /game page
    @GetMapping({"/", "/game"})
    public String gamePage(@RequestParam(value = "difficulty", required = false) String difficulty,
                           Model model, HttpSession session, Principal principal) {
        if (principal == null) return "redirect:/login";

        // default to EASY if not chosen
        if (difficulty == null) {
            difficulty = (String) session.getAttribute(DIFFICULTY_KEY);
            if (difficulty == null) {
                difficulty = "EASY";
            }
        }
        session.setAttribute(DIFFICULTY_KEY, difficulty);

        // If no puzzle in session, create one
        if (session.getAttribute(PUZZLE_KEY) == null) {
            Puzzle p = cipherService.getRandomPuzzle(difficulty);
            session.setAttribute(PUZZLE_KEY, p);
            session.setAttribute(START_MS_KEY, Instant.now().toEpochMilli());
            session.setAttribute(ATTEMPTS_KEY, 0);
        }

        Puzzle p = (Puzzle) session.getAttribute(PUZZLE_KEY);
        model.addAttribute("scrambled", p.scrambled);
        model.addAttribute("hint", p.hint);
        model.addAttribute("attempts", session.getAttribute(ATTEMPTS_KEY));
        model.addAttribute("username", principal.getName());
        model.addAttribute("difficulty", difficulty);
        return "game";
    }

    // handle guess submission
    @PostMapping("/game/guess")
    public String guess(@RequestParam("answer") String answer, Model model,
                        HttpSession session, Principal principal) {
        if (principal == null) return "redirect:/login";
        Object obj = session.getAttribute(PUZZLE_KEY);
        if (obj == null) {
            model.addAttribute("message", "No active puzzle — a new one has started.");
            return "redirect:/game";
        }

        Puzzle puzzle = (Puzzle) obj;

        // increment attempts
        Integer attempts = Optional.ofNullable((Integer) session.getAttribute(ATTEMPTS_KEY)).orElse(0);
        attempts++;
        session.setAttribute(ATTEMPTS_KEY, attempts);

        // normalize both strings: remove punctuation, lower-case, collapse whitespace
        String normAnswer = normalize(answer);
        String normTarget = normalize(puzzle.sentence);

        if (normAnswer.equals(normTarget)) {
            // win -> compute score
            long startMs = (Long) session.getAttribute(START_MS_KEY);
            long elapsedSec = Duration.between(Instant.ofEpochMilli(startMs), Instant.now()).getSeconds();
            int score = Math.max(0, 100 - attempts * 5 - (int) elapsedSec);

            // save to DB
            User u = userRepo.findByUsername(principal.getName()).orElseThrow();
            Score sc = new Score();
            sc.setUser(u);
            sc.setPoints(score);
            sc.setAttempts(attempts);
            sc.setTimeTakenSeconds(elapsedSec);
            scoreRepo.save(sc);

            // clear session puzzle
            session.removeAttribute(PUZZLE_KEY);
            session.removeAttribute(START_MS_KEY);
            session.removeAttribute(ATTEMPTS_KEY);

            model.addAttribute("score", score);
            model.addAttribute("attempts", attempts);
            model.addAttribute("elapsed", elapsedSec);
            model.addAttribute("sentence", puzzle.sentence);
            model.addAttribute("difficulty", session.getAttribute(DIFFICULTY_KEY));
            return "win";
        } else {
            model.addAttribute("scrambled", puzzle.scrambled);
            model.addAttribute("hint", puzzle.hint);
            model.addAttribute("attempts", attempts);
            model.addAttribute("error", "Not quite — try again!");
            model.addAttribute("difficulty", session.getAttribute(DIFFICULTY_KEY));
            return "game";
        }
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("top", scoreRepo.findTop10ByOrderByPointsDesc());
        return "leaderboard";
    }

    // Normalize helper
    private String normalize(String s) {
        if (s == null) return "";
        // remove punctuation, collapse spaces, lowercase
        return s.replaceAll("[^\\p{L}\\p{Nd}\\s]", "")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
