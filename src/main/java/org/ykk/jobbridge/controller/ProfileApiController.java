package org.ykk.jobbridge.controller;

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
                            @RequestBody JobSeekerProfileDTO profile) {
        profile.setMemberId(memberId);
        normalizeBooleanValues(profile);
        profileService.saveProfile(profile);
    }

    private void normalizeBooleanValues(JobSeekerProfileDTO profile) {
        if (profile.getRemotePreferred() == null) profile.setRemotePreferred(false);
        if (profile.getFlexiblePreferred() == null) profile.setFlexiblePreferred(false);
        if (profile.getHybridPreferred() == null) profile.setHybridPreferred(false);
        if (profile.getOnsitePreferred() == null) profile.setOnsitePreferred(false);
        if (profile.getProfilePublic() == null) profile.setProfilePublic(false);
    }
}
