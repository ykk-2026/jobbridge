package org.ykk.jobbridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.ykk.jobbridge.dto.ApplicationDTO;
import org.ykk.jobbridge.dto.JobDTO;
import org.ykk.jobbridge.service.ApplicationService;
import org.ykk.jobbridge.service.JobService;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    public JobController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("jobs", jobService.findAll());
        return "job/jobList";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        JobDTO job = jobService.findById(id);
        if (job == null) {
            return "redirect:/jobs";
        }

        model.addAttribute("job", job);
        model.addAttribute("application", new ApplicationDTO());
        return "job/jobDetail";
    }

    @PostMapping("/{id}/applications")
    public String apply(@PathVariable Long id,
                        @ModelAttribute ApplicationDTO applicationDTO,
                        RedirectAttributes redirectAttributes) {
        applicationDTO.setJobId(id);

        try {
            applicationService.apply(applicationDTO);
            redirectAttributes.addFlashAttribute("message", "입사지원이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/jobs/" + id;
    }
}
