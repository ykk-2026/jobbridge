package org.ykk.jobbridge.mapper;

import org.ykk.jobbridge.dto.AdminMemberDTO;

import java.util.List;

public interface AdminMapper {

    long countMembers();

    long countCompanies();

    List<AdminMemberDTO> findMembers();
}
