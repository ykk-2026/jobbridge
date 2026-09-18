package org.ykk.jobbridge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * 화면(React)에서 Ajax로 호출하여 결과를 제공하는 경우 사용
 * 등록되었습니다. 삭제되었습니다. 메시지를 Ajax에 전달함
 *
 * @JsonInclude(JsonInclude.Include.NON_DEFAULT)
 * DTO를 JSON 형태로 변환할 때, 값이 기본값(빈값, 초기화값)이 아닌 변수만 JSON 형태로 변환
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MsgDTO {

    private int result; // 성공 : 1 / 실패 : 그 외
    private String msg; // 메시지
}
