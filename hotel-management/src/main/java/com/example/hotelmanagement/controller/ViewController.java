package com.example.hotelmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/owner/dashboard")
    public String ownerDashboard() {
        return "owner/dashboard";
    }

    @GetMapping("/reception/dashboard")
    public String receptionDashboard() {
        return "reception/dashboard";
    }

    @GetMapping("/housekeeping/dashboard")
    public String housekeepingDashboard() {
        return "housekeeping/dashboard";
    }

    @GetMapping("/{role}/rooms")
    public String rooms(@PathVariable String role, org.springframework.ui.Model model) {
        model.addAttribute("layout", "layout/" + role + "_layout");
        return "pages/rooms";
    }

    @GetMapping("/{role}/reservations")
    public String reservations(@PathVariable String role, org.springframework.ui.Model model) {
        model.addAttribute("layout", "layout/" + role + "_layout");
        return "pages/reservations";
    }

    @GetMapping("/{role}/guests")
    public String guests(@PathVariable String role, org.springframework.ui.Model model) {
        model.addAttribute("layout", "layout/" + role + "_layout");
        return "pages/guests";
    }

    @GetMapping("/{role}/payments")
    public String payments(@PathVariable String role, org.springframework.ui.Model model) {
        model.addAttribute("layout", "layout/" + role + "_layout");
        return "pages/payments";
    }

    @GetMapping("/{role}/reports")
    public String reports(@PathVariable String role, org.springframework.ui.Model model) {
        model.addAttribute("layout", "layout/" + role + "_layout");
        return "pages/reports";
    }
}
