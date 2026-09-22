package org.ykk.jobbridge.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ykk.jobbridge.dto.MsgDTO;

@Slf4j
@RequestMapping(value = "/api")
@Controller
public class ApiStatusController {

    @ResponseBody
    @GetMapping(value = "status")
    public MsgDTO status() {

        log.info(this.getClass().getName() + ".status Start!");

        MsgDTO dto = new MsgDTO();
        dto.setResult(1);
        dto.setMsg("UP");

        log.info(this.getClass().getName() + ".status End!");

        return dto;
    }
}
