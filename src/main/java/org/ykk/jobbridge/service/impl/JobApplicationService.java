package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.JobApplicationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.mapper.IJobApplicationMapper;
import org.ykk.jobbridge.mapper.IJobPostingMapper;
import org.ykk.jobbridge.service.IJobApplicationService;
import org.ykk.jobbridge.util.EmploymentTypeCodes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class JobApplicationService implements IJobApplicationService {

    private final IJobApplicationMapper jobApplicationMapper;


    private final IJobPostingMapper jobPostingMapper;

    @Override
    public List<JobApplicationDTO> getApplicationList(JobApplicationDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getApplicationList Start!");

        return jobApplicationMapper.getApplicationList(pDTO);
    }

    @Override
    public List<JobApplicationDTO> getCompanyApplicationList(JobApplicationDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getCompanyApplicationList Start!");

        return jobApplicationMapper.getCompanyApplicationList(pDTO);
    }

    @Transactional
    @Override
    public int insertApplicationInfo(JobApplicationDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertApplicationInfo Start!");


        int res = 0;
        pDTO.setEmploymentType(EmploymentTypeCodes.normalize(pDTO.getEmploymentType()));


        JobPostingDTO jDTO = new JobPostingDTO();
        jDTO.setId(pDTO.getJobId());

        JobPostingDTO jobDTO = jobPostingMapper.getJobInfo(jDTO);

        if (jobDTO == null || !"OPEN".equals(jobDTO.getStatus())) {
            log.info("마감되었거나 존재하지 않는 채용공고 : " + pDTO.getJobId());
            res = 3;

        } else {

            JobApplicationDTO existsDTO = jobApplicationMapper.getApplicationExists(pDTO);

            if ("Y".equals(existsDTO.getExistsYn())) {
                log.info("이미 지원한 채용공고 : " + pDTO.getJobId());
                res = 2;

            } else {

                int success = jobApplicationMapper.insertApplicationInfo(pDTO);

                if (success > 0) {
                    res = 1;
                }
            }
        }

        log.info(this.getClass().getName() + ".insertApplicationInfo End!");

        return res;
    }

    @Override
    public int getApplicationCount() throws Exception {

        log.info(this.getClass().getName() + ".getApplicationCount Start!");

        return jobApplicationMapper.getApplicationCount();
    }
}
