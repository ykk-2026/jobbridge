package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanyLoginResultDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.service.CompanyService;

import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    public static final String LOGIN_MEMBER = "loginMember";
    public static final String LOGIN_MEMBER_ID = "loginMemberId";
    public static final String LOGIN_ID = "loginId";
    public static final String LOGIN_ROLE = "loginRole";
    public static final String COMPANY_NAME = "companyName";

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> signup(@RequestBody CompanySignupDTO signup) {
        try {
            companyService.signup(signup);
            return ResponseEntity.ok(Map.of(
                    "memberId", signup.getId(),
                    "loginId", signup.getLoginId(),
                    "companyName", signup.getCompanyName(),
                    "message", "기업회원 가입이 완료되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody CompanyLoginDTO login,
            HttpServletRequest request
    ) {
        try {
            CompanyLoginResultDTO result = companyService.login(login);
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            saveCompanySession(newSession, result);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentCompany(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !"COMPANY".equals(session.getAttribute(LOGIN_ROLE))) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(Map.of(
                "loginMemberId", session.getAttribute(LOGIN_MEMBER_ID),
                "loginId", session.getAttribute(LOGIN_ID),
                "loginRole", session.getAttribute(LOGIN_ROLE),
                "companyName", session.getAttribute(COMPANY_NAME),
                "member", session.getAttribute(LOGIN_MEMBER)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    public static void saveCompanySession(HttpSession session, CompanyLoginResultDTO result) {
        session.setAttribute(LOGIN_MEMBER, result.getMember());
        session.setAttribute(LOGIN_MEMBER_ID, result.getMember().getId());
        session.setAttribute(LOGIN_ID, result.getMember().getLoginId());
        session.setAttribute(LOGIN_ROLE, result.getMember().getRole());
        session.setAttribute(COMPANY_NAME, result.getProfile().getCompanyName());
    }
}
