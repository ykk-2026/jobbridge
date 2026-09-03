package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.service.CompanyService;

import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private static final String LOGIN_MEMBER = "loginMember";

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
            CompanyService.CompanyLoginResult result = companyService.login(login);
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            newSession.setAttribute(LOGIN_MEMBER, result.member());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }
}
