package org.ykk.jobbridge.service;

import org.springframework.stereotype.Service;
import org.ykk.jobbridge.dto.JobSummaryDTO;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobCatalogService {

    private final List<JobSummaryDTO> jobs = List.of(
            new JobSummaryDTO("1", "Samsung SDS", "Java 백엔드 개발자", "서울 송파구", 98, 128, "진행중", LocalDate.of(2026, 7, 15)),
            new JobSummaryDTO("2", "Kakao", "프론트엔드 개발자 (React)", "경기 성남시", 94, 93, "진행중", LocalDate.of(2026, 7, 18)),
            new JobSummaryDTO("3", "LG CNS", "데이터 분석가", "서울 강서구", 92, 64, "진행중", LocalDate.of(2026, 7, 20)),
            new JobSummaryDTO("4", "SK C&C", "UI/UX 디자이너", "서울 중구", 89, 41, "마감임박", LocalDate.of(2026, 7, 22))
    );

    public List<JobSummaryDTO> findAll(int limit) {
        int safeLimit = Math.max(0, Math.min(limit, jobs.size()));
        return jobs.subList(0, safeLimit);
    }

    public List<JobSummaryDTO> findAll() {
        return jobs;
    }

    public long count() {
        return jobs.size();
    }
}
