package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.sql.Date;


/**
 * Created by wen on 2017/4/13.
 */

@Controller
public class WebController {

    @Autowired
    private WeixinService wxService;

    @Autowired
    private AppointmentDao appointmentDao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/auth")
    public String auth(HttpSession session, @RequestParam(name = "code", required = true) String code,
                           @RequestParam(name = "state", required = true) String state){
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxService.oauth2getAccessToken(code);
            WxMpUser wxMpUser = wxService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
            session.setAttribute("userOpenId", wxMpUser);
            logger.info("login successful" + wxMpUser.getNickname());
            return "redirect:/" + state;
        } catch (WxErrorException e) {
            return "redirect:/error";
        }
    }

    @RequestMapping("/yuyue")
    @ResponseBody
    public String yuyue(@RequestParam(name = "file1", required = true) MultipartFile file1,
                      @RequestParam(name = "name", required = true) String name,
                      @RequestParam(name = "chepai", required = true) String chepai,
                      @RequestParam(name = "date", required = true) String date,
                      @RequestParam(name = "driverLicense", required = true) String driverLicense,
                      @RequestParam(name = "tel", required = true) String tel,
                      @RequestParam(name = "file2", required = true) MultipartFile file2
                      ) {
        Appointment appointment = new Appointment();
        appointment.setName(name);
        appointment.setChepai(chepai);
        appointment.setDate(Date.valueOf(date));
        appointment.setDriverLicense(driverLicense);
        appointment.setTel(tel);
        appointmentDao.save(appointment);
        return "success";
    }


    @ResponseBody
    @RequestMapping("/error")
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String error(){
       return "error !";
    }

    @ResponseBody
    @RequestMapping("/user")
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String getUser(HttpSession session){
        WxMpUser wxMpUser = (WxMpUser) session.getAttribute("userOpenId");
        if (wxMpUser != null)
            return wxMpUser.getNickname();
        return "auth failed";
    }
}
