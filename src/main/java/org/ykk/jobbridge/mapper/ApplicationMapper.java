package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.ApplicationDTO;

import java.util.List;

@Mapper
public interface ApplicationMapper {

    int existsByJobIdAndEmail(@Param("jobId") Long jobId, @Param("email") String email);

    int insert(ApplicationDTO applicationDTO);

    List<ApplicationDTO> findByJobId(Long jobId);

    List<ApplicationDTO> findAll();

    ApplicationDTO findById(Long id);

    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
