package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.MemberDTO;
import org.ykk.jobbridge.mapper.ICompanyMapper;
import org.ykk.jobbridge.mapper.IMemberMapper;
import org.ykk.jobbridge.service.ICompanyService;
import org.ykk.jobbridge.util.CmmUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService implements ICompanyService {

    private final ICompanyMapper companyMapper;

    // 기업회원도 MEMBER 테이블에 저장되기 때문에 회원 Mapper도 함께 사용함
    private final IMemberMapper memberMapper;

    @Transactional
    @Override
    public int insertCompanyInfo(MemberDTO pDTO, CompanyProfileDTO cDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCompanyInfo Start!");

        // 가입 성공 : 1, 기가입자로 인한 가입 취소 : 2, 오류로 인한 가입 취소 : 0
        int res = 0;

        // 아이디, 이메일, 사업자등록번호가 이미 가입되었는지 확인
        MemberDTO idDTO = memberMapper.getLoginIdExists(pDTO);
        MemberDTO emailDTO = memberMapper.getEmailExists(pDTO);
        CompanyProfileDTO bizDTO = companyMapper.getBusinessNumberExists(cDTO);

        if (CmmUtil.nvl(idDTO.getExistsYn()).equals("Y")
                || CmmUtil.nvl(emailDTO.getExistsYn()).equals("Y")
                || CmmUtil.nvl(bizDTO.getExistsYn()).equals("Y")) {
            log.info("이미 가입된 기업회원 : " + pDTO.getLoginId());
            res = 2;

        } else {
            // 1. MEMBER 테이블에 담당자 회원정보 저장 (role : COMPANY)
            int success = memberMapper.insertMemberInfo(pDTO);

            if (success > 0) {
                // 2. 저장된 회원 고유번호(AUTO_INCREMENT)를 기업정보에 넣고 COMPANY_PROFILE 테이블 저장
                cDTO.setMemberId(pDTO.getId());
                companyMapper.insertCompanyProfile(cDTO);

                res = 1;
            }
        }

        log.info(this.getClass().getName() + ".insertCompanyInfo End!");

        return res;
    }

    @Override
    public CompanyProfileDTO getCompanyInfo(CompanyProfileDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getCompanyInfo Start!");

        CompanyProfileDTO rDTO = companyMapper.getCompanyInfo(pDTO);

        log.info(this.getClass().getName() + ".getCompanyInfo End!");

        return rDTO;
    }
}
