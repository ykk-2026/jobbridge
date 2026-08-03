package org.ykk.jobbridge.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.LoginRequest;
import org.ykk.jobbridge.dto.MemberLoginResult;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.MemberMapper;

import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class MemberService {

    private static final Set<String> SIGNUP_ROLES =
            Set.of("JOB_SEEKER", "COMPANY");

    private static final Set<String> GENDERS =
            Set.of("MALE", "FEMALE", "OTHER");

    private static final Pattern LOGIN_ID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberService(
            MemberMapper memberMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입
     */
    @Transactional
    public void join(JoinRequest request) {

        normalize(request);
        validate(request);

        if (memberMapper.countByLoginId(
                request.getLoginId()) > 0) {

            throw new IllegalArgumentException(
                    "이미 사용 중인 아이디입니다."
            );
        }

        if (memberMapper.countByEmail(
                request.getEmail()) > 0) {

            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        request.setPassword(encodedPassword);

        int memberResult =
                memberMapper.insertMember(request);

        if (memberResult != 1
                || request.getId() == null) {

            throw new IllegalStateException(
                    "회원정보 저장에 실패했습니다."
            );
        }

        /*
         * 구직자만 job_seeker_profile 생성
         */
        if ("JOB_SEEKER".equals(request.getRole())) {

            int profileResult =
                    memberMapper.insertJobSeekerProfile(
                            request
                    );

            if (profileResult != 1) {
                throw new IllegalStateException(
                        "구직자 프로필 저장에 실패했습니다."
                );
            }
        }
    }

    /**
     * 로그인
     */
    public SessionMember login(
            LoginRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "로그인 정보가 없습니다."
            );
        }

        String loginId =
                trim(request.getLoginId());

        String password =
                request.getPassword();

        if (isBlank(loginId)
                || isBlank(password)) {

            throw new IllegalArgumentException(
                    "아이디와 비밀번호를 입력해주세요."
            );
        }

        MemberLoginResult member =
                memberMapper.findByLoginId(loginId);

        if (member == null) {
            throw new IllegalArgumentException(
                    "아이디 또는 비밀번호가 일치하지 않습니다."
            );
        }

        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalArgumentException(
                    "현재 로그인할 수 없는 계정입니다."
            );
        }

        if (!passwordEncoder.matches(
                password,
                member.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "아이디 또는 비밀번호가 일치하지 않습니다."
            );
        }

        return new SessionMember(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getRole()
        );
    }

    /**
     * 로그인 아이디 중복 확인
     */
    public boolean isLoginIdAvailable(
            String loginId
    ) {

        String value = trim(loginId);

        if (!LOGIN_ID_PATTERN
                .matcher(value)
                .matches()) {

            return false;
        }

        return memberMapper.countByLoginId(value) == 0;
    }

    private void normalize(JoinRequest request) {

        if (request == null) {
            return;
        }

        request.setLoginId(
                trim(request.getLoginId())
        );

        request.setName(
                trim(request.getName())
        );

        request.setEmail(
                trim(request.getEmail()).toLowerCase()
        );

        request.setPhone(
                normalizePhone(request.getPhone())
        );

        request.setGender(
                trim(request.getGender()).toUpperCase()
        );

        request.setRole(
                trim(request.getRole()).toUpperCase()
        );

        request.setDesiredJob(
                trim(request.getDesiredJob())
        );
    }

    private void validate(JoinRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "회원가입 정보가 없습니다."
            );
        }

        if (!LOGIN_ID_PATTERN
                .matcher(request.getLoginId())
                .matches()) {

            throw new IllegalArgumentException(
                    "아이디는 영문, 숫자, 밑줄을 사용해 4~20자로 입력해주세요."
            );
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException(
                    "비밀번호를 입력해주세요."
            );
        }

        if (request.getPassword().length() < 8
                || request.getPassword().length() > 30) {

            throw new IllegalArgumentException(
                    "비밀번호는 8~30자로 입력해주세요."
            );
        }

        if (!request.getPassword().equals(
                request.getPasswordConfirm()
        )) {
            throw new IllegalArgumentException(
                    "비밀번호 확인이 일치하지 않습니다."
            );
        }

        if (isBlank(request.getName())) {
            throw new IllegalArgumentException(
                    "이름을 입력해주세요."
            );
        }

        LocalDate birthDate =
                request.getBirthDate();

        if (birthDate == null) {
            throw new IllegalArgumentException(
                    "생년월일을 입력해주세요."
            );
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "올바른 생년월일을 입력해주세요."
            );
        }

        if (!GENDERS.contains(
                request.getGender()
        )) {
            throw new IllegalArgumentException(
                    "성별을 선택해주세요."
            );
        }

        if (isBlank(request.getEmail())
                || !request.getEmail().contains("@")) {

            throw new IllegalArgumentException(
                    "올바른 이메일을 입력해주세요."
            );
        }

        if (isBlank(request.getPhone())) {
            throw new IllegalArgumentException(
                    "전화번호를 입력해주세요."
            );
        }

        /*
         * ADMIN은 회원가입 화면에서 선택할 수 없도록 막음
         */
        if (!SIGNUP_ROLES.contains(
                request.getRole()
        )) {
            throw new IllegalArgumentException(
                    "올바른 회원 유형을 선택해주세요."
            );
        }

        if ("JOB_SEEKER".equals(request.getRole())
                && isBlank(request.getDesiredJob())) {

            throw new IllegalArgumentException(
                    "구직자는 희망 직무를 입력해야 합니다."
            );
        }

        if (request.getDesiredJob().length() > 100) {
            throw new IllegalArgumentException(
                    "희망 직무는 100자 이하로 입력해주세요."
            );
        }
    }

    private String normalizePhone(String phone) {

        if (phone == null) {
            return "";
        }

        return phone.replaceAll("[^0-9]", "");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null
                || value.trim().isEmpty();
    }
}