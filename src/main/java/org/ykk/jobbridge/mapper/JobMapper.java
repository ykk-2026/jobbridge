package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.JobDTO;

import java.util.List;

@Mapper
public interface JobMapper {

    List<JobDTO> findAll();

    JobDTO findById(Long id);
}
