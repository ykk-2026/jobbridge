package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.MemberDTO;

/**
 * MemberMapper.xml과 매핑되는 인터페이스
 * 함수명과 XML의 id는 1:1 매칭됨
 */
@Mapper
public interface IMemberMapper {

    // 회원 아이디 중복 체크
    MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception;

    // 이메일 중복 체크
    MemberDTO getEmailExists(MemberDTO pDTO) throws Exception;

    // 회원 가입(MEMBER 테이블 등록)
    int insertMemberInfo(MemberDTO pDTO) throws Exception;

    // 구직자 프로필 기본 정보 등록(JOB_SEEKER_PROFILE 테이블)
    int insertJobSeekerProfile(MemberDTO pDTO) throws Exception;

    // 로그인 (아이디, 암호화된 비밀번호로 회원 조회)
    MemberDTO getLogin(MemberDTO pDTO) throws Exception;

}
