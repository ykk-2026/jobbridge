package org.ykk.jobbridge.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.SessionMember;
import org.ykk.jobbridge.mapper.JobApplicationMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-applications")
public class JobApplicationApiController {
    private final JobApplicationMapper mapper;
    private final DataSource dataSource;
    private volatile boolean schemaReady;

    @GetMapping
    public List<JobApplicationDTO> applications(HttpSession session) {
        ensureSchema();
        return mapper.findAllByMemberId(requireJobSeeker(session).getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public JobApplicationDTO apply(@RequestBody JobApplicationDTO application, HttpSession session) {
        SessionMember member = requireJobSeeker(session);
        validate(application);
        ensureSchema();
        application.setMemberId(member.getId());
        application.setJobId(application.getJobId().trim().replaceFirst("^job-", ""));
        application.setCompanyName(application.getCompanyName().trim());
        application.setJobTitle(application.getJobTitle().trim());
        application.setApplicantName(application.getApplicantName().trim());
        application.setEmail(application.getEmail().trim());
        application.setPhone(application.getPhone().trim());
        application.setStatus("APPLIED");
        if (mapper.countByMemberAndJob(member.getId(), application.getJobId()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 지원한 채용공고입니다.");
        }
        mapper.insert(application);
        return application;
    }

    private synchronized void ensureSchema() {
        if (schemaReady) return;

        mapper.ensureTable();
        removeLegacyJobForeignKey();
        normalizeLegacyJobIdColumn();
        mapper.ensureCompanyNameColumn();
        mapper.ensureJobTitleColumn();
        mapper.ensureApplicantNameColumn();
        mapper.ensurePhoneColumn();
        mapper.ensureEmailColumn();
        mapper.ensureEmploymentTypeColumn();
        mapper.ensureStatusColumn();
        mapper.ensureCreatedAtColumn();
        schemaReady = true;
    }

    private void removeLegacyJobForeignKey() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet foreignKeys = metadata.getImportedKeys(connection.getCatalog(), null, "job_application")) {
                while (foreignKeys.next()) {
                    if (!"job_id".equalsIgnoreCase(foreignKeys.getString("FKCOLUMN_NAME"))
                            || !"interest_job".equalsIgnoreCase(foreignKeys.getString("PKTABLE_NAME"))) {
                        continue;
                    }

                    String product = metadata.getDatabaseProductName().toLowerCase();
                    String dropClause = product.contains("mariadb") || product.contains("mysql")
                            ? "DROP FOREIGN KEY"
                            : "DROP CONSTRAINT";
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("ALTER TABLE job_application " + dropClause
                                + " " + foreignKeys.getString("FK_NAME"));
                    }
                    return;
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("지원서 테이블의 이전 외래키를 정리하지 못했습니다.", exception);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void normalizeLegacyJobIdColumn() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet columns = metadata.getColumns(connection.getCatalog(), null, "job_application", "job_id")) {
                if (!columns.next()) return;
                int type = columns.getInt("DATA_TYPE");
                if (type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR) return;
            }

            String product = metadata.getDatabaseProductName().toLowerCase();
            String alterSql = product.contains("mariadb") || product.contains("mysql")
                    ? "ALTER TABLE job_application MODIFY COLUMN job_id VARCHAR(50) NOT NULL"
                    : "ALTER TABLE job_application ALTER COLUMN job_id VARCHAR(50) NOT NULL";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(alterSql);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("지원서 테이블의 공고 ID 형식을 정리하지 못했습니다.", exception);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void validate(JobApplicationDTO value) {
        if (value == null || blank(value.getJobId()) || blank(value.getCompanyName())
                || blank(value.getJobTitle()) || blank(value.getApplicantName())
                || blank(value.getPhone()) || blank(value.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원서 필수 정보가 누락되었습니다.");
        }
    }

    private SessionMember requireJobSeeker(HttpSession session) {
        SessionMember member = (SessionMember) session.getAttribute("loginMember");
        if (member == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        if (!"JOB_SEEKER".equals(member.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "구직자 계정만 지원할 수 있습니다.");
        }
        return member;
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
}
