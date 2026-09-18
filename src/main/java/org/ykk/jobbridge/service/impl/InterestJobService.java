package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.mapper.IInterestJobMapper;
import org.ykk.jobbridge.service.IInterestJobService;
import org.ykk.jobbridge.util.CmmUtil;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InterestJobService implements IInterestJobService {

    private final IInterestJobMapper interestJobMapper;

    @Override
    public List<InterestJobDTO> getInterestJobList(InterestJobDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getInterestJobList Start!");

        return interestJobMapper.getInterestJobList(pDTO);
    }

    @Transactional
    @Override
    public int insertInterestJobInfo(InterestJobDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertInterestJobInfo Start!");

        // 저장 성공 : 1, 이미 저장됨 : 2, 오류 : 0
        int res = 0;

        // 이미 저장된 관심 공고인지 확인
        InterestJobDTO existsDTO = interestJobMapper.getInterestJobExists(pDTO);

        if (CmmUtil.nvl(existsDTO.getExistsYn()).equals("Y")) {
            log.info("이미 저장된 관심 공고 : " + pDTO.getTitle());
            res = 2;

        } else {
            int success = interestJobMapper.insertInterestJobInfo(pDTO);

            if (success > 0) {
                res = 1;
            }
        }

        log.info(this.getClass().getName() + ".insertInterestJobInfo End!");

        return res;
    }

    @Transactional
    @Override
    public void deleteInterestJobInfo(InterestJobDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteInterestJobInfo Start!");

        interestJobMapper.deleteInterestJobInfo(pDTO);
    }
}
