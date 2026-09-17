package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JobPostingDTO;

import java.util.List;

public interface JobPostingMapper {

    void ensureTable();

    void ensureCompanyNameColumn();

    List<JobPostingDTO> findOpenJobs(@Param("limit") int limit);

    JobPostingDTO findById(@Param("id") Long id);

    List<JobPostingDTO> findByCompanyMemberId(@Param("companyMemberId") Long companyMemberId);

    int insert(JobPostingDTO jobPosting);

    int update(JobPostingDTO jobPosting);

    int close(@Param("id") Long id, @Param("companyMemberId") Long companyMemberId);

    int delete(@Param("id") Long id, @Param("companyMemberId") Long companyMemberId);

    long countAll();
}
