package com.java110.api.configuration;

import com.java110.api.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wuxw on 2018/5/2.
 */
@Configuration
public class ServiceConfiguration {
    @Bean
    public FilterRegistrationBean jwtFilter() {
        StringBuffer exclusions = new StringBuffer();
        exclusions.append("/callComponent/login/*,");
        exclusions.append("/callComponent/register/*,");
        exclusions.append("/callComponent/validate-code/*,");
        exclusions.append("/callComponent/validate-tel/*,");
        exclusions.append("/flow/login,");
        exclusions.append("/flow/register,");
        exclusions.append("/flow/advertVedioFlow,");//放开 广告页面是不需要登录的
        exclusions.append("/callComponent/advertVedioView/*,");//放开 广告页面是不需要登录的
        exclusions.append("/callComponent/download/getFile/file,");//放开 下载图片也不需要登录
        exclusions.append("/callComponent/download/getFile/fileByObjId,");//放开 下载图片也不需要登录
        exclusions.append("/callComponent/upload/uploadVedio/upload,");
        exclusions.append("/app/payment/notify,");//微信支付通知
        exclusions.append("/app/payment/notifyChinaUms,");//银联支付回调
        exclusions.append("/app/payment/rentingNotify,");//微信支付通知
        exclusions.append("/app/payment/oweFeeNotify,");//欠费微信支付通知
        exclusions.append("/app/payment/oweFeeNotifyChinaUms,");//欠费银联支付回调
        exclusions.append("/app/payment/toPayTempCarFee,");//欠费银联支付回调
        exclusions.append("/app/payment/tempCarFeeNotifyUrl,");//欠费银联支付回调
        exclusions.append("/app/notice.listNotices,");//通知放开登录
        exclusions.append("/app/tempCarFee.getTempCarFeeOrder,");//通知放开登录
        exclusions.append("/goods/notify,");//商品购买通知
        exclusions.append("/app/loginWx,");// 登录跳过
        exclusions.append("/app/getWxPhoto,");// 登录跳过
        exclusions.append("/app/loginProperty,");// 物业APP登录跳过
        exclusions.append("/app/loginOwner,");// 业主APP登录跳过
        exclusions.append("/app/loginOwnerByKey,");// 根据key登录业主
        exclusions.append("/app/area.listAreas,");// 加载地区
        exclusions.append("/app/community.listCommunitys,");// 加载小区
        exclusions.append("/app/user.userSendSms,");// 发送短信验证码
        exclusions.append("/app/owner.ownerRegister,");// 业主注册
        exclusions.append("/app/owner.ownerRegisterWxPhoto,");// 业主注册
        exclusions.append("/app/activities.listActivitiess,");//小区广告
        exclusions.append("/app/advert.listAdvertPhoto,");//小区广告图片
        exclusions.append("/app/junkRequirement.listJunkRequirements,");//市场
        exclusions.append("/app/wechat/gateway,");//微信公众号对接接口
        exclusions.append("/app/loginOwnerWechatAuth,");//微信公众号对接接口
        exclusions.append("/app/refreshToken,");//微信公众号对接接口
        exclusions.append("/app/refreshOpenId,");//微信公众号对接接口
        exclusions.append("/app/openServiceNotifyOpenId,");//微信公众号对接接口
        exclusions.append("/app/renting/queryRentingPool,");//微信公众号对接接口
        exclusions.append("/app/activitiesType/queryActivitiesType,");//查询大类
        exclusions.append("/app/product/queryProductLabel,");//查询大类
        exclusions.append("/app/loginStaffWechatAuth,");//查询大类
        exclusions.append("/app/reportInfoSetting/queryReportInfoSetting,");//查询疫情
        exclusions.append("/app/reportInfoSettingTitle/querySettingTitle,");//查询疫情问卷详情
        exclusions.append("/app/reportInfoBackCity/saveReportInfoBackCity,");//返省人员信息上报
        exclusions.append("/app/reportInfoAnswerValue/saveReportInfoAnswerValue,");//保存疫情问卷
        exclusions.append("/app/staffAuth,");//查询大类
        exclusions.append("/app/floor.queryFloors,");
        exclusions.append("/app/unit.queryUnits,");
        exclusions.append("/app/room.queryRooms,");
        exclusions.append("/app/productCategory/queryMainCategoryAllGoods,");
        exclusions.append("/app/shop/queryShopCommunity,");
        exclusions.append("/app/shopType/queryShopType,");
        exclusions.append("/app/housekeepingType/queryHousekeepingType,");
        exclusions.append("/callComponent/propertyRightRegistration.savePropertyRightRegistration");


        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/");
        registrationBean.addUrlPatterns("/callComponent/*");
        registrationBean.addUrlPatterns("/flow/*");
        registrationBean.addUrlPatterns("/app/*");
        registrationBean.addInitParameter("excludedUri",exclusions.toString());

        return registrationBean;
    }

}
