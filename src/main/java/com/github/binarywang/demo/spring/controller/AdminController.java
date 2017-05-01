package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import com.github.binarywang.demo.spring.service.WeixinService;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/login")
    public void login(HttpSession session, @RequestParam String userName , @RequestParam String passWord){

    }

    @RequestMapping("/islogin")
    @ResponseBody
    public String isLogin(HttpSession session){
        return "success";
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
    public String passAppointment(@RequestParam(required = true) long id){
        appointmentDao.updateStatus(id, 2);
        return "success";
    }

    @RequestMapping("/notPassAppointment")
    @ResponseBody
    public String notPassAppointment(@RequestParam(required = true) long id){
        appointmentDao.updateStatus(id, 3);
        return "success";
    }
}
