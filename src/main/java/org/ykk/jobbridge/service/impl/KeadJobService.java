package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.KeadJobDTO;
import org.ykk.jobbridge.dto.KeadJobSyncResultDTO;
import org.ykk.jobbridge.external.kead.KeadJobApiClient;
import org.ykk.jobbridge.external.kead.KeadJobXmlParser;
import org.ykk.jobbridge.mapper.IJobPostingMapper;
import org.ykk.jobbridge.service.IKeadJobService;
import org.ykk.jobbridge.util.EducationLevelCodes;
import org.ykk.jobbridge.util.SalaryTypeCodes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HexFormat;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class KeadJobService implements IKeadJobService {

    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final KeadJobApiClient apiClient;
    private final IJobPostingMapper jobPostingMapper;

    @Value("${kead.api.page-size:100}")
    private int pageSize;

    @Value("${kead.api.max-pages:20}")
    private int maxPages;

    @Transactional
    @Override
    public KeadJobSyncResultDTO syncJobs() throws Exception {
        int safePageSize = Math.max(1, Math.min(pageSize, 1000));
        int safeMaxPages = Math.max(1, Math.min(maxPages, 100));
        int fetched = 0;
        int saved = 0;
        int pages = 0;

        for (int pageNo = 1; pageNo <= safeMaxPages; pageNo++) {
            KeadJobXmlParser.Page page = apiClient.getJobs(pageNo, safePageSize);
            pages++;
            fetched += page.jobs().size();

            for (KeadJobDTO source : page.jobs()) {
                JobPostingDTO job = toJobPosting(source);
                jobPostingMapper.upsertKeadJob(job);
                saved++;
            }

            if (page.jobs().isEmpty()
                    || page.jobs().size() < safePageSize
                    || page.totalCount() > 0 && fetched >= page.totalCount()) {
                break;
            }
        }

        KeadJobSyncResultDTO result = new KeadJobSyncResultDTO();
        result.setFetchedCount(fetched);
        result.setSavedCount(saved);
        result.setPageCount(pages);
        result.setMessage("KEAD 채용공고 동기화가 완료되었습니다.");
        log.info("KEAD jobs synchronized: fetched={}, saved={}, pages={}", fetched, saved, pages);
        return result;
    }

    private JobPostingDTO toJobPosting(KeadJobDTO source) throws Exception {
        JobPostingDTO job = new JobPostingDTO();
        Long salaryAmount = parseAmount(source.getSalary());
        String salaryType = SalaryTypeCodes.normalize(source.getSalaryType());
        String[] termDates = splitTermDate(source.getTermDate());

        job.setCompanyName(source.getBusinessPlaceName());
        job.setTitle(source.getJobName());
        job.setJobCategory(source.getJobName());
        job.setEmploymentType(normalizeEmploymentType(source.getEmploymentType()));
        job.setLocation(source.getCompanyAddress());
        job.setSalaryAmount(salaryAmount);
        job.setSalaryType(salaryType);
        job.setSalaryMin(SalaryTypeCodes.toAnnualTenThousandWon(salaryAmount, salaryType));
        // KEAD does not provide a work-location type. Keep the value unknown
        // instead of treating missing data as "no preference".
        job.setWorkType(null);
        job.setExperienceLevel(normalizeExperienceLevel(source.getRequiredCareer()));
        job.setEducationLevel(EducationLevelCodes.normalize(source.getRequiredEducation()));
        job.setRequirements(source.getEntryType());
        job.setAccessibilityInfo("편의시설 정보 미제공");
        job.setWheelchairAccessible(false);
        job.setAccessibleRestroom(false);
        job.setDisabledParking(false);
        job.setAssistiveDeviceSupport(false);
        job.setRestAreaAvailable(false);
        job.setElevatorAvailable(false);
        job.setAccessibilityVerified(false);
        job.setSource("KEAD");
        job.setExternalJobId(createExternalJobId(source));
        job.setContactNumber(source.getContactNumber());
        job.setEntryType(source.getEntryType());
        job.setManagingAgency(source.getManagingAgency());
        job.setRecruitmentStartDate(termDates[0]);
        job.setDeadline(termDates[1]);
        job.setExternalApplyDate(parseDate(source.getOfferRegisteredDate()));
        job.setExternalRegisteredDate(parseDate(source.getRegisteredDate()));
        job.setStatus("OPEN");
        return job;
    }

    private static String normalizeEmploymentType(String value) {
        String raw = value == null ? "" : value.trim();
        String upper = raw.toUpperCase(Locale.ROOT);
        if (raw.contains("무기계약")) return "PERMANENT_CONTRACT";
        if (raw.contains("계약")) return "CONTRACT";
        if (raw.contains("시간") || raw.contains("파트")) return "PART_TIME";
        if (raw.contains("인턴")) return "INTERN";
        if (raw.contains("파견")) return "DISPATCH";
        if (raw.contains("정규")) return "FULL_TIME";
        if (raw.contains("상용")) return "REGULAR_EMPLOYEE";
        return upper.isEmpty() ? "ANY" : "ANY";
    }

    static String normalizeExperienceLevel(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        String compact = trimmed.replaceAll("\\s+", "");
        return "무관".equals(trimmed) || compact.matches("0년0?개월") ? "ANY" : trimmed;
    }

    private static Long parseAmount(String value) {
        if (value == null) return null;
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;
        try {
            long amount = Long.parseLong(digits);
            return amount > 0 ? amount : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String[] splitTermDate(String value) {
        if (value == null || value.isBlank()) return new String[]{null, null};
        String[] values = value.trim().split("\\s*[~～]\\s*", 2);
        String start = parseDate(values[0]);
        String end = values.length > 1 ? parseDate(values[1]) : null;
        return new String[]{start, end};
    }

    private static String parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = value.trim().replace(".", "-").replace("/", "-");
        try {
            if (normalized.matches("\\d{8}")) return LocalDate.parse(normalized, BASIC_DATE).toString();
            return LocalDate.parse(normalized).toString();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String createExternalJobId(KeadJobDTO job) throws Exception {
        String source = String.join("|",
                safe(job.getBusinessPlaceName()),
                safe(job.getJobName()),
                safe(job.getCompanyAddress()),
                safe(job.getOfferRegisteredDate()));
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(source.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
