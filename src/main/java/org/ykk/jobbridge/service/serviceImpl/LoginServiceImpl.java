package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.LoginDTO;
import org.ykk.jobbridge.dto.MemberLoginResult;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.LoginMapper;
import org.ykk.jobbridge.service.LoginService;

import java.util.Locale;

@Service
public class LoginServiceImpl implements LoginService {

    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;

    // Spring이 LoginMapper와 PasswordEncoder 객체를 넣어준다.
    public LoginServiceImpl(LoginMapper loginMapper, PasswordEncoder passwordEncoder) {
        this.loginMapper = loginMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SessionMember login(LoginDTO loginDTO) {
        // 1. 입력값이 비어 있는지 확인한다.
        if (loginDTO == null || isBlank(loginDTO.getLoginId()) || isBlank(loginDTO.getPassword())) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력해 주세요.");
        }

        // 2. Mapper를 이용해 DB에서 회원을 조회한다.
        String loginId = loginDTO.getLoginId().trim().toLowerCase(Locale.ROOT);
        MemberLoginResult member = loginMapper.findByLoginId(loginId);

        // 3. 화면에서 받은 비밀번호와 DB의 암호화된 비밀번호를 비교한다.
        if (member == null || !passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 4. 사용 가능한 계정인지 확인한다.
        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalArgumentException("사용할 수 없는 회원 계정입니다.");
        }

        // 5. 비밀번호를 제외한 회원 정보만 Controller에 돌려준다.
        return new SessionMember(member.getId(), member.getLoginId(), member.getName(), member.getRole());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
