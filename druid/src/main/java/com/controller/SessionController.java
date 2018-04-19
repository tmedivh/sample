package com.controller;

import com.utils.HttpSessionUtil;
import com.vo.ResponseVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/session")
public class SessionController {

    /**
     * 获取当前session
     *
     * @return
     */
    @GetMapping
    public ResponseVO getHttpSession() {
        return ResponseVO.response().setData(HttpSessionUtil.getHttpSession().getId()).build();
    }
}
