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

    }else if (key.equals("pipeline")){
      WxMpXmlOutNewsMessage.Item item0
              = new WxMpXmlOutNewsMessage.Item();
      item0.setDescription("其中需要车主配合进行操作的：填写机动车检验委托书，领取检验报告，领取年检合格标志");
      item0.setPicUrl("https://mmbiz.qpic.cn/mmbiz_png/kD0Zgdl0sLA0yGzWjQzWWHukaXn05ic76Ra7JonCFHKzsFrwUR4qKBInrpwzuxPp1pUZIzb30a0X2ZbPfR08GZw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1");
      item0.setTitle("绍兴市柯桥区车辆检测中心有限公司机动车年检流程图");
      item0.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000005&idx=1&sn=3f54fcca56de89b5cf86fe8b3eadcabb&chksm=7b96df434ce15655032bd418a08d7587772583bb4a0123bced54fab92079f74e86bf64d5a5ad&scene=0&key=7d4eb0035f57fc878aacfc9259f730853004d5c29c91581f3c6e74804c041fe77a57b52951ad0b37a42e7ce945fb01655dabc1ac8414eeac74820f13d0426c6e5817c42f840367f331520fe90c37b0ae&ascene=0&uin=Nzk1MzQzNzIw&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.4+build(16E195)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=6MjILNTfI7SuEumDUSFxwLaN0drT4ARDaEYhN9ciQH0BmsRIk6tlHzmxCb9mtpya");
      WxMpXmlOutNewsMessage.Item item1
              = new WxMpXmlOutNewsMessage.Item();
      item1.setDescription("汽车年检前请提前处理好违章并缴纳好罚款，提前确认车牌外侧有无牌照框，如有请拆除，确认车辆内配备三角架。");
      item1.setPicUrl("https://mmbiz.qpic.cn/mmbiz_jpg/kD0Zgdl0sLA0yGzWjQzWWHukaXn05ic76JIO4ficdrVuRIMCtW50QRsUJ1koicliaorhUibtBq3nGR8l97hjAbGia0GQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
      item1.setTitle("预约车辆检测流程");
      item1.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000005&idx=2&sn=188fc32dfbea45e485ada0e8d12e2fb7&chksm=7b96df434ce15655c9179d4fa785834da3cb59f552448aa9bcc389f91c7aa802ff50151e91e1&scene=0&key=c5a58783da8d7ae1d90851a006a044829c32262fc1c30baa8622c3cb22590da1ddc8933ed0244b5f7643d9caec2555f80b7be8ba455d668675203f315ef67e4be53ed1a684fc6c20418251a045f3f8c9&ascene=0&uin=Nzk1MzQzNzIw&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.4+build(16E195)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=6MjILNTfI7SuEumDUSFxwLaN0drT4ARDaEYhN9ciQH0BmsRIk6tlHzmxCb9mtpya");
      WxMpXmlOutNewsMessage.Item item2
              = new WxMpXmlOutNewsMessage.Item();
      item2.setDescription("汽车年检前请提前处理好违章并缴纳好罚款，提前确认车牌外侧有无牌照框，如有请拆除，确认车辆内配备三角架。");
      item2.setTitle("非预约车辆检测流程");
      item2.setPicUrl("https://mmbiz.qpic.cn/mmbiz_jpg/kD0Zgdl0sLA0yGzWjQzWWHukaXn05ic76OT8WM0od4BT6I7gN4sTicLNN8eHjdwiaMia67OicwqnrgdgvlIJJ5iaFSjA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
      item2.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000005&idx=3&sn=96e9e704dc3158d2a62c4b07688260f3&chksm=7b96df434ce1565579db28cf7b9907d0d4d8bce8a145b3601e49dffa31fe976f522c12f29340&scene=0&key=5a2d2c76af59b6280b61aa6d80f6625171afe63f57718bc6669edf1c57f9c8d7784fc89f3fb815752959ec189a8d41b3587f1497d3e485f9df9b30955d59e3075fb09ddb41fde26ad7710783f421d617&ascene=0&uin=Nzk1MzQzNzIw&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.4+build(16E195)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=6MjILNTfI7SuEumDUSFxwLaN0drT4ARDaEYhN9ciQH0BmsRIk6tlHzmxCb9mtpya");
      WxMpXmlOutNewsMessage.Item item4
              = new WxMpXmlOutNewsMessage.Item();
      item4.setDescription("根据绍兴市发展和改革委员会文件，中小型车为120元一次，大型车为160元一次。");
      item4.setTitle("收费标准");
      item4.setPicUrl("https://mmbiz.qpic.cn/mmbiz_jpg/kD0Zgdl0sLA0yGzWjQzWWHukaXn05ic76vlzghFibicHh1dhbnJSdY2cpIb4iaak4L20huEYIwkaiahIInVAHh3UYIw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
      item4.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000005&idx=5&sn=261d1b5def6113a0bb4ea50cda67db09&chksm=7b96df434ce15655392f0b360db2d33066b5bc31b54bd7ae26307a356ca7151d655197ca6e1a&scene=0&key=a9a3d8941685a0099b99361365424e946555c82ca50580826ca47bbe4560eb7edf04913a6ff393f8b4033f93eaaa3911ee6a7262238cbff8ee4e97246771be9e091962e44456a517a739150c10ebe80d&ascene=0&uin=Nzk1MzQzNzIw&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.4+build(16E195)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=8cqU%2BWt2GZ2ilmI2ZBOn0u5hQD4X6ZeofwLAQmOGdYFB9qzon2Nmg3RU5W9i5CBa");
      WxMpXmlOutNewsMessage.Item item3
              = new WxMpXmlOutNewsMessage.Item();
      item3.setDescription("送检机动车应清洁，无明显漏油、漏水、漏气现象，轮胎完好，轮胎气压正常且胎冠花纹中无异物，发动机应运转平稳，怠速稳定，无异响；装有车载诊断系统（OBD）的车辆，不应有与防抱死制动系统（ABS）、电动助力转向系统（EPS）及其他与行车安全相关的故障信息。\n");
      item3.setTitle("年检须知");
      item3.setPicUrl("https://mmbiz.qpic.cn/mmbiz_jpg/kD0Zgdl0sLA0yGzWjQzWWHukaXn05ic76D8aTBEkaRj2ibNvA5wOMUnPSAcGsziazQXJyMapsO80p3pElNgKDVUqA/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1");
      item3.setUrl("https://mp.weixin.qq.com/s?__biz=MzU1MTAyOTU0NQ==&mid=100000005&idx=4&sn=444eb3a8d401fb0dbff9cc197cd72ed9&chksm=7b96df434ce15655ffd956042149054ca4d8273772e729feb315f3c9bf8aaabdad77c142c7c4&scene=0&key=519926ba1d3c3d7717492130cd33a3793ba26be4c2bf85eec58e41cc69aebd5969fb1e51f9b6bb2c2045f705955c76af4a22cd710f8f9da3631e12501eb30c134ac7c9c603a56ed5e6e4e2d061771abf&ascene=0&uin=Nzk1MzQzNzIw&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.12.4+build(16E195)&version=12020610&nettype=WIFI&fontScale=100&pass_ticket=8cqU%2BWt2GZ2ilmI2ZBOn0u5hQD4X6ZeofwLAQmOGdYFB9qzon2Nmg3RU5W9i5CBa");
      return WxMpXmlOutMessage.NEWS().addArticle(item0).addArticle(item1).addArticle(item2).addArticle(item3)
              .addArticle(item4).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
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
