package com.github.binarywang.demo.spring.handler;

import java.util.List;
import java.util.Map;

import com.github.binarywang.demo.spring.dao.AppointmentDao;
import com.github.binarywang.demo.spring.domain.Appointment;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
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
    }else if (key.equals("about_us")){
      WxMpXmlOutNewsMessage.Item item
              = new WxMpXmlOutNewsMessage.Item();
      item.setDescription("柯桥车辆检测中心安昌站简介");
      item.setPicUrl("https://mmbiz.qpic.cn/mmbiz_png/HkQHXpE6t3a2Pn0Ax6l3zD58DfSQ2l9mU4snj56jnt6GLn7Wgibtf8MLaBlqTQ4ibN76jZhcU0k2VzKIofspic8yQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1");
      item.setTitle("柯桥车辆检测中心安昌站简介");
      item.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000001&idx=1&sn=414fb98938b4dd12c9c317e3042485d0&chksm=7b96df474ce15651f5eb924b483627fbcae613cd90598eb233458c64924a86785de3dba53520&mpshare=1&scene=1&srcid=05117RDDDa7qn4JzAgAzylyR&key=7489dcaf600290efb63e0e0a735695c42057370bcd464f625614f36de76a5da7cbdf15d5856dc229ae3acce588f3c539c79926e6508f8d3555eacad12978b981314ad7a8a0835f0d14b7317a68c83dc7&ascene=0&uin=Mjk1NDU1&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.3+build(16D32)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=AweLurxw0uk9r6QnE3NC%2FniRJHrQYT94f%2Fo3ceRlIro%3D");
      return WxMpXmlOutMessage.NEWS().addArticle(item).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
    }else if (key.equals("faq")){
      WxMpXmlOutNewsMessage.Item item
              = new WxMpXmlOutNewsMessage.Item();
      item.setDescription("车检注意事项");
      item.setPicUrl("https://mp.weixin.qq.com/mp/qrcode?scene=10000004&size=102&__biz=MzU1MTAyOTU0NQ==&mid=100000002&idx=1&sn=d02c5cc562b3471d953a0c4788190a39&send_time=");
      item.setTitle("车检问答");
      item.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000002&idx=1&sn=d02c5cc562b3471d953a0c4788190a39&chksm=7b96df444ce15652cbe530aa082df0db6624d5a49df865ebe62a189a3776226a04dd2795ea93&mpshare=1&scene=1&srcid=0511QxqKdlumgypOY50BVosP&key=3eac712de8f103a91a5ea82d2f71a338ebfa1fab26eee45bf0650594677f8aa2c4d779078ce31f99cb9a40fd6bad1a8c9a8f77d165876cc6262da5829c06529087c81df9f2bdd76c3cbdb6512adb6491&ascene=0&uin=Mjk1NDU1&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.3+build(16D32)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=AweLurxw0uk9r6QnE3NC%2FniRJHrQYT94f%2Fo3ceRlIro%3D");
      return WxMpXmlOutMessage.NEWS().addArticle(item).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();

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
