package com.github.binarywang.demo.spring.controller;

import com.github.binarywang.demo.spring.dao.AdminDao;
import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
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

    @RequestMapping("/login/doLogin")
    public String login(HttpSession session, @RequestParam String userName , @RequestParam String passWord){
        if (adminDao.validate(userName,passWord)) {
            session.setAttribute("admin_user", userName);
            return "redirect:/admin/manage";
        }else{
            return "redirect:/admin/login";
        }
    }

    @RequestMapping("/{from}/isLogin")
    @ResponseBody
    public String isLogin(HttpSession session){
        return session.getAttribute("admin_user")!=null?"true":"false";
    }

    @RequestMapping("/manage/appointments")
    @ResponseBody
    public Map<String,Object> listAppointments(@RequestParam(required = true) int offset, @RequestParam(required = true) int limit){
        List<Appointment> list = appointmentDao.list(offset,limit);
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("appoints", list);
        map.put("count", appointmentDao.count());
        return map;
    }

    @RequestMapping("/manage/getPic")
    public void getPic(HttpServletRequest request, HttpServletResponse response, @RequestParam String serverId) throws WxErrorException {
        File file = wxService.getMaterialService().mediaDownload(serverId);
        FileInputStream fis = null;
        response.setContentType("image/gif");
        try {
            OutputStream out = response.getOutputStream();
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/manage/passAppointment")
    @ResponseBody
    public Map<String, Object> passAppointment(@RequestParam(required = true) long id)  {
        Map<String, Object> ret = new HashMap<String,Object>();
        try {
            Appointment appointment = appointmentDao.find(id);
            if (appointment == null){
                ret.put("success", false);
                ret.put("reason", "该预约号不存在");
                return ret;
            }
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setToUser(appointmentDao.find(id).getOpenId());
            wxMpTemplateMessage.setTemplateId("A4MA_cZAvz9NBvBxiXOzMIbOYlz7G403X6oFO04wP64");
            WxMpTemplateData data = new WxMpTemplateData();
            data.setName("first");
            data.setValue(" 您好，您的预约已通过");
            wxMpTemplateMessage.addWxMpTemplateData(data);
            WxMpTemplateData data1 = new WxMpTemplateData();
            data1.setName("keyword1");
            data1.setValue(appointment.getDate().toString());
            wxMpTemplateMessage.addWxMpTemplateData(data1);
            WxMpTemplateData data2 = new WxMpTemplateData();
            data2.setName("keyword2");
            data2.setValue("车辆检测");
            wxMpTemplateMessage.addWxMpTemplateData(data2);
            WxMpTemplateData data3 = new WxMpTemplateData();
            data3.setName("keyword3");
            data3.setValue("等待上门检测");
            wxMpTemplateMessage.addWxMpTemplateData(data3);
            wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            appointmentDao.updateStatus(id, 2);
            ret.put("success" , true);
        } catch (WxErrorException e) {
            ret.put("success", false);
            ret.put("reason", e.getError().getErrorMsg());
        } catch (Exception e1){
            ret.put("success", false);
            ret.put("reason", e1.getMessage());
        }
        return ret;
    }

    @RequestMapping("/manage/notPassAppointment")
    @ResponseBody
    public Map<String, Object> notPassAppointment(@RequestParam(required = true) long id) throws WxErrorException {
        Map<String, Object> ret = new HashMap<String,Object>();
        try {
            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setToUser(appointmentDao.find(id).getOpenId());
            WxMpTemplateData data1 = new WxMpTemplateData();
            data1.setName("first");
            data1.setValue("您好，您的预约失败了");
            wxMpTemplateMessage.addWxMpTemplateData(data1);
            WxMpTemplateData data2 = new WxMpTemplateData();
            data2.setName("keyword1");
            data2.setValue("车辆检测");
            wxMpTemplateMessage.addWxMpTemplateData(data2);

            WxMpTemplateData data3 = new WxMpTemplateData();
            data3.setName("keyword2");
            data3.setValue(String.valueOf(id));
            wxMpTemplateMessage.addWxMpTemplateData(data3);

            wxMpTemplateMessage.setTemplateId("U3HinBl-5DQYjIyhgrEtvb95zXKqxFV_ZAaN_o_kK3E");
            wxService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            appointmentDao.updateStatus(id, 3);
            ret.put("success" , true);
        } catch (WxErrorException e) {
            ret.put("success", false);
            ret.put("reason", e.getError().getErrorMsg());
        } catch (Exception e1){
            ret.put("success", false);
            ret.put("reason", e1.getMessage());
        }
        return ret;
    }
}
