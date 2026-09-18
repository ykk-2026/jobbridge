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

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // memberMapper 변수에 이미 메모리에 올라간 IMemberMapper 객체를 넣어줌
    private final IMemberMapper memberMapper;

    @Override
    public MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getLoginIdExists Start!");

        // DB 조회하기
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

        // 회원가입 성공 : 1, 기가입자로 인한 가입 취소 : 2, 오류로 인한 가입 취소 : 0
        int res = 0;

        // 회원 아이디가 이미 가입되었는지 확인
        MemberDTO idDTO = memberMapper.getLoginIdExists(pDTO);

        // 이메일이 이미 가입되었는지 확인
        MemberDTO emailDTO = memberMapper.getEmailExists(pDTO);

        if (CmmUtil.nvl(idDTO.getExistsYn()).equals("Y") || CmmUtil.nvl(emailDTO.getExistsYn()).equals("Y")) {
            log.info("이미 가입된 아이디 또는 이메일 : " + pDTO.getLoginId() + " / " + pDTO.getEmail());
            res = 2;

        } else {
            // MEMBER 테이블에 회원정보 저장
            int success = memberMapper.insertMemberInfo(pDTO);

            // 구직자라면 JOB_SEEKER_PROFILE 테이블에 프로필 기본 정보도 저장
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

        // 아이디, 암호화된 비밀번호가 일치하는 회원 조회
        MemberDTO rDTO = memberMapper.getLogin(pDTO);

        log.info(this.getClass().getName() + ".getLogin End!");

        return rDTO;
    }
}
