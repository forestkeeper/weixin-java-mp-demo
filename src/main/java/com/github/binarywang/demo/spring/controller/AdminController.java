package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.dao.AdminDao;
import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wen on 2017/4/30.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private WeixinService wxService;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AdminDao adminDao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/doLogin")
    public String login(HttpSession session, @RequestParam String userName , @RequestParam String passWord){
        if (adminDao.validate(userName,passWord)) {
            session.setAttribute("admin_user", userName);
            return "redirect:/admin";
        }else{
            return "redirect:/admin/login.html";
        }
    }

    @RequestMapping("/isLogin")
    @ResponseBody
    public String isLogin(HttpSession session){
        return session.getAttribute("admin_user")!=null?"true":"false";
    }

    @RequestMapping("/appointments")
    @ResponseBody
    public Map<String,Object> listAppointments(@RequestParam(required = true) int offset, @RequestParam(required = true) int limit){
        List<Appointment> list = appointmentDao.list(offset,limit);
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("appoints", list);
        map.put("count", appointmentDao.count());
        return map;
    }

    @RequestMapping("/passAppointment")
    @ResponseBody
    public String passAppointment(@RequestParam(required = true) long id) throws WxErrorException {
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setToUser(appointmentDao.find(id).getOpenId());
        wxMpTemplateMessage.setTemplateId("gngDECBVRqQun6OoWFD--ihshqWQBL9zryfctsQIoLs");
        wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        appointmentDao.updateStatus(id, 2);
        return "success";
    }

    @RequestMapping("/notPassAppointment")
    @ResponseBody
    public String notPassAppointment(@RequestParam(required = true) long id) throws WxErrorException {
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setToUser(appointmentDao.find(id).getOpenId());
        wxMpTemplateMessage.setTemplateId("45y2dsypCtmcw9hkkfi1IcfnVrg918YaPZHvltYEMis");
        wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        appointmentDao.updateStatus(id, 3);
        return "success";
    }
}
