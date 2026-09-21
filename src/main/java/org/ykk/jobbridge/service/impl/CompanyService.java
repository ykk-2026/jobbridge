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


    private final IMemberMapper memberMapper;

    @Transactional
    @Override
    public int insertCompanyInfo(MemberDTO pDTO, CompanyProfileDTO cDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertCompanyInfo Start!");


        int res = 0;


        MemberDTO idDTO = memberMapper.getLoginIdExists(pDTO);
        MemberDTO emailDTO = memberMapper.getEmailExists(pDTO);
        CompanyProfileDTO bizDTO = companyMapper.getBusinessNumberExists(cDTO);

        if (CmmUtil.nvl(idDTO.getExistsYn()).equals("Y")
                || CmmUtil.nvl(emailDTO.getExistsYn()).equals("Y")
                || CmmUtil.nvl(bizDTO.getExistsYn()).equals("Y")) {
            log.info("이미 가입된 기업회원 : " + pDTO.getLoginId());
            res = 2;

        } else {

            int success = memberMapper.insertMemberInfo(pDTO);

            if (success > 0) {

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
