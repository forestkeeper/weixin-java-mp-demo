package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;


/**
 * Created by wen on 2017/4/13.
 */

@Controller
public class WebController {

    @Autowired
    private WeixinService wxService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/auth")
    public String testAuth(HttpSession session, @RequestParam(name = "code", required = true) String code,
                           @RequestParam(name = "state", required = true) String state){
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
            session.setAttribute("userOpenId", code);
            logger.info("login successful" + wxMpUser.getNickname());
            return "redirect:/uploader";
        } catch (WxErrorException e) {
            return "auth failed";
        }
    }

//    @ResponseBody
//    @RequestMapping("/user")
//    @GetMapping(produces = "text/plain;charset=utf-8")
//    public String getUser(HttpSession session){
//        String code = (String) session.getAttribute("userOpenId");
//        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
//        try {
//            wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
//            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
//            return wxMpUser.getNickname();
//        } catch (WxErrorException e) {
//            return "auth failed";
//        }
//    }
}
