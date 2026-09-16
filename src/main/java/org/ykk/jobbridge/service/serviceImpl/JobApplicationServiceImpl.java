package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.mapper.JobApplicationMapper;
import org.ykk.jobbridge.service.JobApplicationService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final DataSource dataSource;
    private volatile boolean schemaReady;

    public JobApplicationServiceImpl(JobApplicationMapper jobApplicationMapper, DataSource dataSource) {
        this.jobApplicationMapper = jobApplicationMapper;
        this.dataSource = dataSource;
    }

    @Override
    public List<JobApplicationDTO> getApplications(Long memberId) {
        ensureSchema();
        return jobApplicationMapper.findAllByMemberId(memberId);
    }

    @Override
    @Transactional
    public JobApplicationDTO apply(Long memberId, JobApplicationDTO application) {
        validate(application);
        ensureSchema();

        application.setMemberId(memberId);
        application.setJobId(application.getJobId().trim().replaceFirst("^job-", ""));
        application.setCompanyName(application.getCompanyName().trim());
        application.setJobTitle(application.getJobTitle().trim());
        application.setApplicantName(application.getApplicantName().trim());
        application.setEmail(application.getEmail().trim());
        application.setPhone(application.getPhone().trim());
        application.setStatus("APPLIED");

        if (jobApplicationMapper.countByMemberAndJob(memberId, application.getJobId()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 지원한 채용공고입니다.");
        }

        jobApplicationMapper.insert(application);
        return application;
    }

    private synchronized void ensureSchema() {
        if (schemaReady) {
            return;
        }

        jobApplicationMapper.ensureTable();
        removeLegacyJobForeignKey();
        normalizeLegacyJobIdColumn();
        jobApplicationMapper.ensureCompanyNameColumn();
        jobApplicationMapper.ensureJobTitleColumn();
        jobApplicationMapper.ensureApplicantNameColumn();
        jobApplicationMapper.ensurePhoneColumn();
        jobApplicationMapper.ensureEmailColumn();
        jobApplicationMapper.ensureEmploymentTypeColumn();
        jobApplicationMapper.ensureStatusColumn();
        jobApplicationMapper.ensureCreatedAtColumn();
        schemaReady = true;
    }

    private void removeLegacyJobForeignKey() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet foreignKeys = metadata.getImportedKeys(
                    connection.getCatalog(), null, "job_application")) {
                while (foreignKeys.next()) {
                    boolean jobIdForeignKey = "job_id".equalsIgnoreCase(
                            foreignKeys.getString("FKCOLUMN_NAME"));
                    boolean interestJobTable = "interest_job".equalsIgnoreCase(
                            foreignKeys.getString("PKTABLE_NAME"));
                    if (!jobIdForeignKey || !interestJobTable) {
                        continue;
                    }

                    String product = metadata.getDatabaseProductName().toLowerCase();
                    String dropClause;
                    if (product.contains("mariadb") || product.contains("mysql")) {
                        dropClause = "DROP FOREIGN KEY";
                    } else {
                        dropClause = "DROP CONSTRAINT";
                    }

                    try (Statement statement = connection.createStatement()) {
                        String sql = "ALTER TABLE job_application " + dropClause
                                + " " + foreignKeys.getString("FK_NAME");
                        statement.executeUpdate(sql);
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
            try (ResultSet columns = metadata.getColumns(
                    connection.getCatalog(), null, "job_application", "job_id")) {
                if (!columns.next()) {
                    return;
                }
                int type = columns.getInt("DATA_TYPE");
                if (type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR) {
                    return;
                }
            }

            String product = metadata.getDatabaseProductName().toLowerCase();
            String alterSql;
            if (product.contains("mariadb") || product.contains("mysql")) {
                alterSql = "ALTER TABLE job_application MODIFY COLUMN job_id VARCHAR(50) NOT NULL";
            } else {
                alterSql = "ALTER TABLE job_application ALTER COLUMN job_id VARCHAR(50) NOT NULL";
            }

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(alterSql);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("지원서 테이블의 공고 ID 형식을 정리하지 못했습니다.", exception);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void validate(JobApplicationDTO application) {
        if (application == null
                || isBlank(application.getJobId())
                || isBlank(application.getCompanyName())
                || isBlank(application.getJobTitle())
                || isBlank(application.getApplicantName())
                || isBlank(application.getPhone())
                || isBlank(application.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원서 필수 정보가 누락되었습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
