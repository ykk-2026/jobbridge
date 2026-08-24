package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JobApplicationDTO;

import java.util.List;

public interface JobApplicationMapper {
    void ensureTable();
    void ensureCompanyNameColumn();
    void ensureJobTitleColumn();
    void ensureApplicantNameColumn();
    void ensurePhoneColumn();
    void ensureEmailColumn();
    void ensureEmploymentTypeColumn();
    void ensureStatusColumn();
    void ensureCreatedAtColumn();
    int countByMemberAndJob(@Param("memberId") Long memberId, @Param("jobId") String jobId);
    int insert(JobApplicationDTO application);
    List<JobApplicationDTO> findAllByMemberId(@Param("memberId") Long memberId);
    long countAll();
}
