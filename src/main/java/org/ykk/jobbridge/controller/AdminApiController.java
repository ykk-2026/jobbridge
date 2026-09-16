package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.AdminOverviewDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    public AdminApiController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/overview")
    public AdminOverviewDTO overview(HttpSession session) {
        requireAdmin(session);

        return adminService.getOverview();
    }

    private void requireAdmin(HttpSession session) {
        SessionMember loginMember = (SessionMember) session.getAttribute("loginMember");
        if (loginMember == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!"ADMIN".equals(loginMember.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
        }
    }
}
