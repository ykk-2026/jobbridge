package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.MemberDTO;
import org.ykk.jobbridge.mapper.IMemberMapper;
import org.ykk.jobbridge.service.IMemberService;
import org.ykk.jobbridge.util.CmmUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements IMemberService {

    private final IMemberMapper memberMapper;

    @Override
    public MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLoginIdExists Start!");

        MemberDTO rDTO = memberMapper.getLoginIdExists(pDTO);

        log.info(this.getClass().getName() + ".getLoginIdExists End!");

        return rDTO;
    }

    @Override
    public MemberDTO getEmailExists(MemberDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        MemberDTO rDTO = memberMapper.getEmailExists(pDTO);

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    @Transactional
    @Override
    public int insertMemberInfo(MemberDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertMemberInfo Start!");

        int res = 0;

        MemberDTO idDTO = memberMapper.getLoginIdExists(pDTO);

        MemberDTO emailDTO = memberMapper.getEmailExists(pDTO);

        if (CmmUtil.nvl(idDTO.getExistsYn()).equals("Y") || CmmUtil.nvl(emailDTO.getExistsYn()).equals("Y")) {
            log.info("이미 가입된 아이디 또는 이메일 : " + pDTO.getLoginId() + " / " + pDTO.getEmail());
            res = 2;

        } else {

            int success = memberMapper.insertMemberInfo(pDTO);

            if (success > 0 && CmmUtil.nvl(pDTO.getRole()).equals("JOB_SEEKER")) {
                memberMapper.insertJobSeekerProfile(pDTO);
            }

            if (success > 0) {
                res = 1;
            }
        }

        log.info(this.getClass().getName() + ".insertMemberInfo End!");

        return res;
    }

    @Override
    public MemberDTO getLogin(MemberDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLogin Start!");

        MemberDTO rDTO = memberMapper.getLogin(pDTO);

        log.info(this.getClass().getName() + ".getLogin End!");

        return rDTO;
    }
}
