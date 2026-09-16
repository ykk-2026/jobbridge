package org.ykk.jobbridge.dto;

public class CompanyLoginResultDTO {

    private SessionMember member;
    private CompanyProfileDTO profile;

    public CompanyLoginResultDTO() {
    }

    public CompanyLoginResultDTO(SessionMember member, CompanyProfileDTO profile) {
        this.member = member;
        this.profile = profile;
    }

    public SessionMember getMember() {
        return member;
    }

    public void setMember(SessionMember member) {
        this.member = member;
    }

    public CompanyProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(CompanyProfileDTO profile) {
        this.profile = profile;
    }
}
