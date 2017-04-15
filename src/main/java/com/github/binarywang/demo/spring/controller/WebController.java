package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wen on 2017/4/13.
 */

@RestController
@RequestMapping("/auth")
public class WebController {

    @Autowired
    private WeixinService wxService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String testAuth(@RequestParam(name = "code", required = true) String code,
                           @RequestParam(name = "state", required = true) String state){
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
            return "your nickname is :" + wxMpUser.getNickname();
        } catch (WxErrorException e) {
            return "auth failed";
        }
    }
}
