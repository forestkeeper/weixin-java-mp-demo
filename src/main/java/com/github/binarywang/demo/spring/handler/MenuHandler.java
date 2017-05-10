package com.github.binarywang.demo.spring.handler;

import java.util.List;
import java.util.Map;

import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.demo.spring.builder.AbstractBuilder;
import com.github.binarywang.demo.spring.builder.ImageBuilder;
import com.github.binarywang.demo.spring.builder.TextBuilder;
import com.github.binarywang.demo.spring.dto.WxMenuKey;
import com.github.binarywang.demo.spring.service.WeixinService;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * 
 * @author Binary Wang
 *
 */
@Component
public class MenuHandler extends AbstractHandler {

  @Autowired
  AppointmentDao appointmentDao;

  @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
      Map<String, Object> context, WxMpService wxMpService,
      WxSessionManager sessionManager) {
    WeixinService weixinService = (WeixinService) wxMpService;

    String key = wxMessage.getEventKey();
    WxMenuKey menuKey = null;
    if (key.equals("order_status")){
      List<Appointment> list = appointmentDao.findInOrderingAppointmentByOpenId(wxMessage.getFromUser());
      if (list.size() == 0){
          list = appointmentDao.findPassedAppointmentByOpenId(wxMessage.getFromUser());
          if (list.size() > 0){
              return WxMpXmlOutMessage.TEXT().content("请在日期：" + list.get(0).getDate() + " 到监测站检测，车牌为" +
              list.get(0).getChepai()).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
          }else {
              return WxMpXmlOutMessage.TEXT().content("目前没有在进行中的预约").fromUser(wxMessage.getToUser())
                      .toUser(wxMessage.getFromUser()).build();
          }
      }else {
        return  WxMpXmlOutMessage.TEXT().content("日期为" + list.get(0).getDate() + "的预约正在审核中，请等候").fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
      }
    }
    try {
      menuKey = JSON.parseObject(key, WxMenuKey.class);
    } catch (Exception e) {
      return WxMpXmlOutMessage.TEXT().content(key)
          .fromUser(wxMessage.getToUser())
          .toUser(wxMessage.getFromUser()).build();
    }

    AbstractBuilder builder = null;
    switch (menuKey.getType()) {
    case WxConsts.XML_MSG_TEXT:
      builder = new TextBuilder();
      break;
    case WxConsts.XML_MSG_IMAGE:
      builder = new ImageBuilder();
      break;
    case WxConsts.XML_MSG_VOICE:
      break;
    case WxConsts.XML_MSG_VIDEO:
      break;
    case WxConsts.XML_MSG_NEWS:
      break;
    default:
      break;
    }

    if (builder != null) {
      try {
        return builder.build(menuKey.getContent(), wxMessage, weixinService);
      } catch (Exception e) {
        this.logger.error(e.getMessage(), e);
      }
    }

    return null;

  }

}
