package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JoinDTO;
import org.ykk.jobbridge.mapper.JoinMapper;
import org.ykk.jobbridge.service.JoinService;

import java.util.Locale;

@Service
public class JoinServiceImpl implements JoinService {

    private final JoinMapper joinMapper;
    private final PasswordEncoder passwordEncoder;

    public JoinServiceImpl(JoinMapper joinMapper, PasswordEncoder passwordEncoder) {
        this.joinMapper = joinMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void join(JoinDTO joinDTO) {
        validateJoinRequest(joinDTO);

        String loginId = normalizeLoginId(joinDTO.getLoginId());
        String email = joinDTO.getEmail().trim();

        if (joinMapper.countByLoginId(loginId) > 0) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (joinMapper.countByEmail(email) > 0) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        joinDTO.setLoginId(loginId);
        joinDTO.setEmail(email);
        joinDTO.setPassword(passwordEncoder.encode(joinDTO.getPassword()));
        joinMapper.insertMember(joinDTO);

        if ("JOB_SEEKER".equals(joinDTO.getRole())) {
            joinMapper.insertJobSeekerProfile(joinDTO);
        }
    }

    @Override
    public boolean isLoginIdAvailable(String loginId) {
        return !isBlank(loginId) && joinMapper.countByLoginId(normalizeLoginId(loginId)) == 0;
    }

    private String normalizeLoginId(String loginId) {
        return loginId.trim().toLowerCase(Locale.ROOT);
    }

    private void validateJoinRequest(JoinDTO joinDTO) {
        if (joinDTO == null || isBlank(joinDTO.getLoginId()) || isBlank(joinDTO.getPassword())
                || isBlank(joinDTO.getName()) || joinDTO.getBirthDate() == null
                || isBlank(joinDTO.getGender()) || isBlank(joinDTO.getEmail())
                || isBlank(joinDTO.getPhone()) || isBlank(joinDTO.getRole())) {
            throw new IllegalArgumentException("필수 회원 정보를 모두 입력해 주세요.");
        }
        if (!joinDTO.getPassword().equals(joinDTO.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
