package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.service.ProfileService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    // 프로필 조회 및 수정 화면
    @GetMapping
    public String profile(HttpSession session, Model model) {

        Long memberId = (Long) session.getAttribute("loginMemberId");

        if (memberId == null) {
            return "redirect:/member/login";
        }

        JobSeekerProfileDTO profile = profileService.getProfile(memberId);

        model.addAttribute("profile", profile);

        return "profile/edit";
    }

    // 프로필 저장 및 수정
    @PostMapping("/save")
    public String saveProfile(JobSeekerProfileDTO profileDTO,
                              HttpSession session) {

        Long memberId = (Long) session.getAttribute("loginMemberId");

        if (memberId == null) {
            return "redirect:/member/login";
        }

        profileDTO.setMemberId(memberId);

        normalizeBooleanValues(profileDTO);

        profileService.saveProfile(profileDTO);

        return "redirect:/profile";
    }

    // 체크박스 값이 전달되지 않을 경우 false 처리
    private void normalizeBooleanValues(JobSeekerProfileDTO profileDTO) {

        if (profileDTO.getRemotePreferred() == null) {
            profileDTO.setRemotePreferred(false);
        }

        if (profileDTO.getFlexiblePreferred() == null) {
            profileDTO.setFlexiblePreferred(false);
        }

        if (profileDTO.getHybridPreferred() == null) {
            profileDTO.setHybridPreferred(false);
        }

        if (profileDTO.getOnsitePreferred() == null) {
            profileDTO.setOnsitePreferred(false);
        }

        if (profileDTO.getProfilePublic() == null) {
            profileDTO.setProfilePublic(false);
        }
    }
}