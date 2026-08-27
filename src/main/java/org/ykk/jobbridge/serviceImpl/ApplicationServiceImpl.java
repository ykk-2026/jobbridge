package org.ykk.jobbridge.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.ykk.jobbridge.dto.ApplicationDTO;
import org.ykk.jobbridge.mapper.ApplicationMapper;
import org.ykk.jobbridge.service.ApplicationService;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final List<String> ALLOWED_STATUSES = List.of("RECEIVED", "REVIEWING", "ACCEPTED", "REJECTED");

    private final ApplicationMapper applicationMapper;

    public ApplicationServiceImpl(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    @Override
    @Transactional
    public void apply(ApplicationDTO applicationDTO) {
        validate(applicationDTO);

        if (applicationMapper.existsByJobIdAndEmail(applicationDTO.getJobId(), applicationDTO.getEmail()) > 0) {
            throw new IllegalArgumentException("이미 지원한 공고입니다.");
        }

        applicationMapper.insert(applicationDTO);
    }

    @Override
    public List<ApplicationDTO> findByJobId(Long jobId) {
        return applicationMapper.findByJobId(jobId);
    }

    @Override
    public List<ApplicationDTO> findAll() {
        return applicationMapper.findAll();
    }

    @Override
    public ApplicationDTO findById(Long id) {
        return applicationMapper.findById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("지원 정보가 없습니다.");
        }
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("변경할 수 없는 지원 상태입니다.");
        }
        if (applicationMapper.updateStatus(id, status) == 0) {
            throw new IllegalArgumentException("존재하지 않는 지원서입니다.");
        }
    }

    private void validate(ApplicationDTO applicationDTO) {
        if (applicationDTO.getJobId() == null) {
            throw new IllegalArgumentException("공고 정보가 없습니다.");
        }
        if (!StringUtils.hasText(applicationDTO.getApplicantName())) {
            throw new IllegalArgumentException("지원자 이름을 입력해 주세요.");
        }
        if (!StringUtils.hasText(applicationDTO.getEmail())) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }
        if (!StringUtils.hasText(applicationDTO.getPhone())) {
            throw new IllegalArgumentException("연락처를 입력해 주세요.");
        }
    }
}
