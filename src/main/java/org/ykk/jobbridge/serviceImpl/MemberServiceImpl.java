package org.ykk.jobbridge.ServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.LoginRequest;
import org.ykk.jobbridge.dto.MemberLoginResult;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.MemberMapper;
import org.ykk.jobbridge.service.MemberService;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void join(JoinRequest request) {
        validateJoinRequest(request);

        String loginId = normalizeLoginId(request.getLoginId());
        String email = request.getEmail().trim();

        if (memberMapper.countByLoginId(loginId) > 0) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (memberMapper.countByEmail(email) > 0) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        request.setLoginId(loginId);
        request.setEmail(email);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        memberMapper.insertMember(request);

        if ("JOB_SEEKER".equals(request.getRole())) {
            memberMapper.insertJobSeekerProfile(request);
        }
    }

    @Override
    public SessionMember login(LoginRequest request) {
        if (request == null || isBlank(request.getLoginId()) || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력해 주세요.");
        }

        MemberLoginResult member = memberMapper.findByLoginId(normalizeLoginId(request.getLoginId()));
        if (member == null || !passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalArgumentException("사용할 수 없는 회원 계정입니다.");
        }

        return new SessionMember(member.getId(), member.getLoginId(), member.getName(), member.getRole());
    }

    @Override
    public boolean isLoginIdAvailable(String loginId) {
        return !isBlank(loginId) && memberMapper.countByLoginId(normalizeLoginId(loginId)) == 0;
    }

    private String normalizeLoginId(String loginId) {
        return loginId.trim().toLowerCase(Locale.ROOT);
    }

    private void validateJoinRequest(JoinRequest request) {
        if (request == null || isBlank(request.getLoginId()) || isBlank(request.getPassword())
                || isBlank(request.getName()) || request.getBirthDate() == null
                || isBlank(request.getGender()) || isBlank(request.getEmail())
                || isBlank(request.getPhone()) || isBlank(request.getRole())) {
            throw new IllegalArgumentException("필수 회원 정보를 모두 입력해 주세요.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
