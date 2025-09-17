package com.clavis.controller;

import com.clavis.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository userRepo;

    public AdminController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "admin_users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long id) {
        userRepo.deleteById(id);
        return "redirect:/admin/users";
    }
}
