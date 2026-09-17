package org.ykk.jobbridge.service.serviceImpl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.mapper.JobPostingMapper;
import org.ykk.jobbridge.service.JobPostingService;

import java.time.LocalDate;
import java.util.List;

@Service
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingMapper jobPostingMapper;
    private boolean schemaReady;

    public JobPostingServiceImpl(JobPostingMapper jobPostingMapper) {
        this.jobPostingMapper = jobPostingMapper;
    }

    @Override
    public List<JobPostingDTO> getOpenJobs(int limit) {
        ensureSchema();
        int safeLimit = limit;
        if (safeLimit < 1) {
            safeLimit = 1;
        }
        if (safeLimit > 100) {
            safeLimit = 100;
        }
        return jobPostingMapper.findOpenJobs(safeLimit);
    }

    @Override
    public JobPostingDTO getJob(Long jobId) {
        ensureSchema();
        JobPostingDTO jobPosting = jobPostingMapper.findById(jobId);
        if (jobPosting == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채용공고를 찾을 수 없습니다.");
        }
        return jobPosting;
    }

    @Override
    public List<JobPostingDTO> getMyJobs(Long companyMemberId) {
        ensureSchema();
        return jobPostingMapper.findByCompanyMemberId(companyMemberId);
    }

    @Override
    @Transactional
    public JobPostingDTO createJob(Long companyMemberId, JobPostingDTO jobPosting) {
        validate(jobPosting);
        normalize(jobPosting);
        ensureSchema();

        jobPosting.setCompanyMemberId(companyMemberId);
        jobPosting.setStatus("OPEN");
        jobPostingMapper.insert(jobPosting);
        return jobPostingMapper.findById(jobPosting.getId());
    }

    @Override
    @Transactional
    public JobPostingDTO updateJob(Long companyMemberId, Long jobId, JobPostingDTO jobPosting) {
        validate(jobPosting);
        normalize(jobPosting);
        ensureSchema();

        jobPosting.setId(jobId);
        jobPosting.setCompanyMemberId(companyMemberId);
        if (jobPostingMapper.update(jobPosting) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "본인 회사의 채용공고만 수정할 수 있습니다.");
        }
        return jobPostingMapper.findById(jobId);
    }

    @Override
    @Transactional
    public void closeJob(Long companyMemberId, Long jobId) {
        ensureSchema();
        if (jobPostingMapper.close(jobId, companyMemberId) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "본인 회사의 진행 중인 공고만 마감할 수 있습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteJob(Long companyMemberId, Long jobId) {
        ensureSchema();
        if (jobPostingMapper.delete(jobId, companyMemberId) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "본인 회사의 채용공고만 삭제할 수 있습니다.");
        }
    }

    @Override
    public long countJobs() {
        ensureSchema();
        return jobPostingMapper.countAll();
    }

    private void validate(JobPostingDTO jobPosting) {
        if (jobPosting == null
                || isBlank(jobPosting.getCompanyName())
                || isBlank(jobPosting.getTitle())
                || isBlank(jobPosting.getJobCategory())
                || isBlank(jobPosting.getEmploymentType())
                || isBlank(jobPosting.getLocation())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "회사명, 공고 제목, 직무, 고용형태, 근무지는 필수입니다.");
        }
        if (jobPosting.getSalaryMin() != null && jobPosting.getSalaryMin() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최소 급여는 0 이상이어야 합니다.");
        }
        if (jobPosting.getSalaryMax() != null && jobPosting.getSalaryMax() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "최대 급여는 0 이상이어야 합니다.");
        }
        if (jobPosting.getSalaryMin() != null && jobPosting.getSalaryMax() != null
                && jobPosting.getSalaryMin() > jobPosting.getSalaryMax()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "최소 급여는 최대 급여보다 클 수 없습니다.");
        }
        if (jobPosting.getDeadline() != null && jobPosting.getDeadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "마감일은 오늘 이후여야 합니다.");
        }
    }

    private void normalize(JobPostingDTO jobPosting) {
        jobPosting.setCompanyName(jobPosting.getCompanyName().trim());
        jobPosting.setTitle(jobPosting.getTitle().trim());
        jobPosting.setJobCategory(jobPosting.getJobCategory().trim());
        jobPosting.setEmploymentType(jobPosting.getEmploymentType().trim());
        jobPosting.setLocation(jobPosting.getLocation().trim());

        if (jobPosting.getWheelchairAccessible() == null) {
            jobPosting.setWheelchairAccessible(false);
        }
        if (jobPosting.getAccessibleRestroom() == null) {
            jobPosting.setAccessibleRestroom(false);
        }
        if (jobPosting.getDisabledParking() == null) {
            jobPosting.setDisabledParking(false);
        }
        if (jobPosting.getRemoteAvailable() == null) {
            jobPosting.setRemoteAvailable(false);
        }
        if (jobPosting.getFlexibleWorkAvailable() == null) {
            jobPosting.setFlexibleWorkAvailable(false);
        }
        if (jobPosting.getAssistiveDeviceSupport() == null) {
            jobPosting.setAssistiveDeviceSupport(false);
        }
    }

    private synchronized void ensureSchema() {
        if (schemaReady) {
            return;
        }
        jobPostingMapper.ensureTable();
        jobPostingMapper.ensureCompanyNameColumn();
        schemaReady = true;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
