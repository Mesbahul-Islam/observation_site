package com.o4.observatory.controllers;

import com.o4.observatory.models.User;
import com.o4.observatory.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nickname,
            Model model) {
        
        System.out.println("Registering user: " + username + ", nickname: " + nickname);

        try {
            User user = userService.registerNewUser(username, password, nickname);
            System.out.println("User registered with ID: " + user.getId());
            return "redirect:/login?registered";
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}