package org.ykk.jobbridge.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.AiJobRecommendationMapper;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.service.AiJobRecommendationService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiJobRecommendationServiceImpl implements AiJobRecommendationService {

    private final AiJobRecommendationMapper recommendationMapper;
    private final JobRecommendationCalculator calculator;
    private final DataSource dataSource;
    private volatile boolean schemaReady;

    @Override
    @Transactional
    public List<AiJobRecommendationDTO> getRecommendations(Long memberId) {
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        ensureSchema();
        JobSeekerProfileDTO profile = recommendationMapper.findProfile(memberId);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "구직자 프로필을 먼저 등록해 주세요.");
        }

        List<AiJobRecommendationDTO> recommendations = recommendationMapper.findEligibleJobs().stream()
                .map(job -> calculator.calculate(memberId, profile, job))
                .sorted(Comparator.comparing(AiJobRecommendationDTO::getTotalScore).reversed()
                        .thenComparing(AiJobRecommendationDTO::getJobId))
                .toList();

        recommendations.forEach(this::saveOrUpdate);
        return recommendations;
    }

    private void saveOrUpdate(AiJobRecommendationDTO recommendation) {
        if (recommendationMapper.updateRecommendation(recommendation) == 0) {
            recommendationMapper.insertRecommendation(recommendation);
        }
    }

    private synchronized void ensureSchema() {
        if (schemaReady) return;
        if (!recommendationTableExists()) recommendationMapper.ensureTable();
        schemaReady = true;
    }

    private boolean recommendationTableExists() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"})) {
                while (tables.next()) {
                    if ("ai_job_recommendation".equalsIgnoreCase(tables.getString("TABLE_NAME"))) return true;
                }
            }
            return false;
        } catch (SQLException exception) {
            throw new IllegalStateException("추천 테이블 정보를 확인하지 못했습니다.", exception);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
