package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


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

    @RequestMapping("/signature")
    @ResponseBody
    public WxJsapiSignature getSignature(@RequestParam(name = "url", required = true) String url) throws WxErrorException {
        return wxService.createJsapiSignature(url);
    }

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

    /**
     * 8:30 ~ 11:30
     * 2:00 ~ 4:00
     * 5 vehicles per hour
     * 一个电话号码一年内最多2辆，最多预约两次
     * 一个微信号最多2辆
     * 过时不候
     * @param session
     * @param file1
     * @param name
     * @param chepai
     * @param date
     * @param tel
     * @param file2
     * @return
     */
    @RequestMapping("/uploader/yuyue")
    @ResponseBody
    public Map<String, Object> yuyue(HttpSession session, @RequestParam(name = "file1" ,required = false) MultipartFile file1,
                                     @RequestParam(name = "name", required = true) String name,
                                     @RequestParam(name = "chepai", required = true) String chepai,
                                     @RequestParam(name = "date", required = true) String date,
                                     @RequestParam(name = "tel", required = true) String tel,
                                     @RequestParam(name = "file2", required = false) MultipartFile file2,
                                     @RequestParam(name = "serverId", required = true) String serverId,
                                     @RequestParam(name = "time", required = true) int time
                      ) throws ParseException {
        Map<String, Object> ret = new HashMap<String, Object>();
        Date date1 = Date.valueOf(date);
        if (date1.before(Calendar.getInstance().getTime())
                || date1.equals(Calendar.getInstance().getTime())){
            ret.put("success", false);
            ret.put("reason", "只能选择提前1-7个工作日的日期");
            return ret;
        }
        if (wxService.getDayCounter().getCount(Calendar.getInstance().getTime(), date1) > 7){
            ret.put("success", false);
            ret.put("reason", "不能预约超过7工作日的日期");
            return ret;
        }
        if (wxService.getDayCounter().getCount(Calendar.getInstance().getTime(), date1) < 1){
            ret.put("success", false);
            ret.put("reason", "请提前一个工作日");
            return ret;
        }
        if (wxService.getDayCounter().isHoliday(date1)){
            ret.put("success", false);
            ret.put("reason", "法定节假日无法提供服务");
            return ret;
        }
        WxMpUser wxMpUser = (WxMpUser) session.getAttribute("userOpenId");
        if (wxMpUser == null){
            ret.put("success", false);
            ret.put("reason", "无法递交，请通过微信访问");
            return ret;
        }
        if (appointmentDao.findInOrderingAppointmentByOpenId(wxMpUser.getOpenId()).size()>0){
            ret.put("success", false);
            ret.put("reason", "现有预约正在进行中，不能同时预约两个车");
            return ret;
        }
        if (appointmentDao.isInBlackList(tel, wxMpUser.getOpenId())){
            ret.put("success", false);
            ret.put("reason",  "3次预约不出现，已进入黑名单");
        }
        if (appointmentDao.findCountByOpenId(wxMpUser.getOpenId()) > 2){
            ret.put("success", false);
            ret.put("reason", "同一个微信号，一年只能预约两次");
            return ret;
        }
        if (appointmentDao.findCountByTel(tel) > 2){
            ret.put("success", false);
            ret.put("reason", "同一个电话，一年只能预约两次");
            return ret;
        }

        if (appointmentDao.countForDay(date,time) >= 15 && time == 0){
            ret.put("success", false);
            ret.put("reason", "当天上午预约数量已满");
            return ret;
        }
        if (appointmentDao.countForDay(date,time) >= 10 && time == 1){
            ret.put("success", false);
            ret.put("reason", "当天下午预约数量已满");
            return ret;
        }

        Appointment appointment = new Appointment();
        appointment.setName(wxMpUser.getNickname());
        appointment.setOpenId(wxMpUser.getOpenId());
        appointment.setRealName(name);
        appointment.setChepai(chepai);
        appointment.setDate(date1);
        appointment.setDriverLicense("");
        appointment.setTel(tel);
        appointment.setServerId(serverId);
        appointment.setTime(time);
        appointmentDao.save(appointment);
        ret.put("success", true);
        return ret;
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
