package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.AiJobRecommendationMapper;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.service.AiJobRecommendationService;
import org.ykk.jobbridge.service.JobPostingService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AiJobRecommendationServiceImpl implements AiJobRecommendationService {

    private final AiJobRecommendationMapper recommendationMapper;
    private final JobRecommendationCalculator calculator;
    private final JobPostingService jobPostingService;
    private final DataSource dataSource;
    private volatile boolean schemaReady;

    public AiJobRecommendationServiceImpl(AiJobRecommendationMapper recommendationMapper,
                                          JobRecommendationCalculator calculator,
                                          JobPostingService jobPostingService,
                                          DataSource dataSource) {
        this.recommendationMapper = recommendationMapper;
        this.calculator = calculator;
        this.jobPostingService = jobPostingService;
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public List<AiJobRecommendationDTO> getRecommendations(Long memberId) {
        if (memberId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        jobPostingService.countJobs();
        ensureSchema();
        JobSeekerProfileDTO profile = recommendationMapper.findProfile(memberId);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "구직자 프로필을 먼저 등록해 주세요.");
        }

        List<JobPostingDTO> jobs = recommendationMapper.findEligibleJobs();
        List<AiJobRecommendationDTO> recommendations = new ArrayList<>();

        for (JobPostingDTO job : jobs) {
            AiJobRecommendationDTO recommendation = calculator.calculate(memberId, profile, job);
            recommendations.add(recommendation);
        }

        Collections.sort(recommendations, new Comparator<AiJobRecommendationDTO>() {
            @Override
            public int compare(AiJobRecommendationDTO first, AiJobRecommendationDTO second) {
                int scoreComparison = second.getTotalScore().compareTo(first.getTotalScore());
                if (scoreComparison != 0) {
                    return scoreComparison;
                }
                return first.getJobId().compareTo(second.getJobId());
            }
        });

        for (AiJobRecommendationDTO recommendation : recommendations) {
            saveOrUpdate(recommendation);
        }
        return recommendations;
    }

    private void saveOrUpdate(AiJobRecommendationDTO recommendation) {
        if (recommendationMapper.updateRecommendation(recommendation) == 0) {
            recommendationMapper.insertRecommendation(recommendation);
        }
    }

    private synchronized void ensureSchema() {
        if (schemaReady) {
            return;
        }
        if (!recommendationTableExists()) {
            recommendationMapper.ensureTable();
        }
        schemaReady = true;
    }

    private boolean recommendationTableExists() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"})) {
                while (tables.next()) {
                    if ("ai_job_posting_recommendation".equalsIgnoreCase(tables.getString("TABLE_NAME"))) {
                        return true;
                    }
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
