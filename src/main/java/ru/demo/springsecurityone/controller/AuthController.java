package ru.demo.springsecurityone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Связали наши урлы с шаблонами
 * */

@Controller
@RequestMapping("/auth/")
public class AuthController {
    @GetMapping("login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("success")
    public String getSuccessPage() {
        return "success";
    }
}