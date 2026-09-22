package org.ykk.jobbridge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MemberDTO {

    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String birthDate;
    private String gender;
    private String email;
    private String phone;
    private String role;
    private String status;

    private String desiredJob;

    private String existsYn;

}
