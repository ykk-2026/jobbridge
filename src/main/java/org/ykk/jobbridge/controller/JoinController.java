package org.ykk.jobbridge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ykk.jobbridge.dto.JoinDTO;
import org.ykk.jobbridge.service.JoinService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @GetMapping("/check-login-id")
    public Map<String, Object> checkLoginId(@RequestParam String loginId) {
        boolean available = joinService.isLoginIdAvailable(loginId);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        if (available) {
            response.put("message", "사용 가능한 아이디입니다.");
        } else {
            response.put("message", "사용할 수 없거나 이미 사용 중인 아이디입니다.");
        }
        return response;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> join(@RequestBody JoinDTO joinDTO) {
        try {
            joinService.join(joinDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("memberId", joinDTO.getId());
            response.put("loginId", joinDTO.getLoginId());
            response.put("name", joinDTO.getName());
            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }
}
