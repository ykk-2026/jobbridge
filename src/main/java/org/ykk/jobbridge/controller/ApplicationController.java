package org.ykk.jobbridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ykk.jobbridge.dto.ApplicationDTO;
import org.ykk.jobbridge.service.ApplicationService;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("applications", applicationService.findAll());
        return "application/applicationList";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ApplicationDTO application = applicationService.findById(id);
        if (application == null) {
            return "redirect:/applications";
        }

        model.addAttribute("application", application);
        return "application/applicationDetail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            applicationService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "지원 상태가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/applications/" + id;
    }
}
