package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.MemberDTO;

public interface IMemberService {

    /**
     * 회원 아이디 중복 체크
     *
     * @param pDTO 중복 체크할 loginId 값
     * @return 조회 결과(existsYn : Y 존재 / N 없음)
     */
    MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception;

    /**
     * 이메일 중복 체크
     *
     * @param pDTO 중복 체크할 email 값
     * @return 조회 결과(existsYn : Y 존재 / N 없음)
     */
    MemberDTO getEmailExists(MemberDTO pDTO) throws Exception;

    /**
     * 회원 가입
     *
     * @param pDTO 화면에서 입력된 회원가입 값들
     * @return 1 : 성공 / 2 : 이미 가입된 아이디 또는 이메일 / 0 : 실패
     */
    int insertMemberInfo(MemberDTO pDTO) throws Exception;

    /**
     * 로그인
     *
     * @param pDTO 아이디, 암호화된 비밀번호
     * @return 로그인 성공 시 회원정보, 실패 시 null
     */
    MemberDTO getLogin(MemberDTO pDTO) throws Exception;

}
