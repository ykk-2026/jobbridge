package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileApiController {

    private final ProfileService profileService;

    @GetMapping("/{memberId}")
    public JobSeekerProfileDTO getProfile(@PathVariable Long memberId) {
        JobSeekerProfileDTO profile = profileService.getProfile(memberId);
        if (profile == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "회원 정보를 찾을 수 없습니다. memberId=" + memberId
            );
        }
        return profile;
    }

    @PutMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveProfile(@PathVariable Long memberId,
                            @RequestBody JobSeekerProfileDTO profile,
                            HttpSession session) {
        SessionMember member = requireMember(session);
        if (!member.getId().equals(memberId) && !"ADMIN".equalsIgnoreCase(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 프로필만 수정할 수 있습니다.");
        }
        profile.setMemberId(memberId);
        normalizeBooleanValues(profile);
        profileService.saveProfile(profile);
        refreshSessionMemberName(profile, session);
    }

    private void refreshSessionMemberName(JobSeekerProfileDTO profile, HttpSession session) {
        if (session == null || profile.getName() == null || profile.getName().isBlank()) return;
        Object value = session.getAttribute("loginMember");
        if (!(value instanceof SessionMember member) || !member.getId().equals(profile.getMemberId())) return;
        session.setAttribute("loginMember", new SessionMember(
                member.getId(),
                member.getLoginId(),
                profile.getName().trim(),
                member.getRole()
        ));
    }

    private void normalizeBooleanValues(JobSeekerProfileDTO profile) {
        if (profile.getRemotePreferred() == null) profile.setRemotePreferred(false);
        if (profile.getFlexiblePreferred() == null) profile.setFlexiblePreferred(false);
        if (profile.getHybridPreferred() == null) profile.setHybridPreferred(false);
        if (profile.getOnsitePreferred() == null) profile.setOnsitePreferred(false);
        if (profile.getProfilePublic() == null) profile.setProfilePublic(false);
    }

    private SessionMember requireMember(HttpSession session) {
        SessionMember member = session == null ? null : (SessionMember) session.getAttribute("loginMember");
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return member;
    }
}
