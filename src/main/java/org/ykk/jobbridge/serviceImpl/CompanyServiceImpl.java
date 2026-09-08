package org.ykk.jobbridge.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.dto.MemberLoginResult;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.CompanyMapper;
import org.ykk.jobbridge.service.CompanyService;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyMapper companyMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signup(CompanySignupDTO signup) {
        validateSignup(signup);

        String loginId = normalizeLoginId(signup.getLoginId());
        String email = signup.getEmail().trim();
        String businessNumber = signup.getBusinessNumber().trim();

        if (companyMapper.countByLoginId(loginId) > 0) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (companyMapper.countByEmail(email) > 0) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        if (companyMapper.countByBusinessNumber(businessNumber) > 0) {
            throw new IllegalStateException("이미 등록된 사업자등록번호입니다.");
        }

        signup.setLoginId(loginId);
        signup.setEmail(email);
        signup.setBusinessNumber(businessNumber);
        signup.setPassword(passwordEncoder.encode(signup.getPassword()));

        companyMapper.insertMember(signup);
        companyMapper.insertCompanyProfile(signup);
    }

    @Override
    public CompanyLoginResult login(CompanyLoginDTO login) {
        if (login == null || isBlank(login.getLoginId()) || isBlank(login.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        MemberLoginResult member = companyMapper.findMemberByLoginId(normalizeLoginId(login.getLoginId()));
        if (member == null || !passwordEncoder.matches(login.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!"COMPANY".equals(member.getRole())) {
            throw new IllegalArgumentException("기업회원 계정이 아닙니다.");
        }
        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalArgumentException("사용할 수 없는 계정입니다.");
        }

        CompanyProfileDTO profile = companyMapper.findProfileByMemberId(member.getId());
        if (profile == null) {
            throw new IllegalStateException("기업 프로필 정보를 찾을 수 없습니다.");
        }

        SessionMember sessionMember = new SessionMember(
                member.getId(), member.getLoginId(), member.getName(), member.getRole()
        );
        return new CompanyLoginResult(sessionMember, profile);
    }

    private void validateSignup(CompanySignupDTO signup) {
        if (signup == null || isBlank(signup.getLoginId()) || isBlank(signup.getPassword())
                || isBlank(signup.getName()) || isBlank(signup.getEmail()) || isBlank(signup.getPhone())
                || isBlank(signup.getCompanyName()) || isBlank(signup.getBusinessNumber())
                || isBlank(signup.getRepresentativeName()) || isBlank(signup.getCompanyAddress())) {
            throw new IllegalArgumentException("필수 기업회원 정보를 모두 입력해 주세요.");
        }
        if (!signup.getPassword().equals(signup.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (signup.getEmployeeCount() != null && signup.getEmployeeCount() < 0) {
            throw new IllegalArgumentException("직원 수는 0 이상이어야 합니다.");
        }
    }

    private String normalizeLoginId(String loginId) {
        return loginId.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
