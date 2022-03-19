package com.java110.api.smo.payment.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.api.properties.WechatAuthProperties;
import com.java110.api.smo.AppAbstractComponentSMO;
import com.java110.api.smo.payment.IToPaySMO;
import com.java110.api.smo.payment.adapt.IPayAdapt;
import com.java110.core.context.IPageData;
import com.java110.core.context.PageData;
import com.java110.core.log.LoggerFactory;
import com.java110.dto.app.AppDto;
import com.java110.dto.owner.OwnerAppUserDto;
import com.java110.dto.smallWeChat.SmallWeChatDto;
import com.java110.utils.cache.MappingCache;
import com.java110.utils.constant.CommonConstant;
import com.java110.utils.constant.WechatConstant;
import com.java110.utils.factory.ApplicationContextFactory;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.utils.util.DateUtil;
import com.java110.utils.util.StringUtil;
import com.java110.vo.ResultVo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("toPaySMOImpl")
public class ToPaySMOImpl extends AppAbstractComponentSMO implements IToPaySMO {
    private static final Logger logger = LoggerFactory.getLogger(AppAbstractComponentSMO.class);


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplate outRestTemplate;


    @Autowired
    private WechatAuthProperties wechatAuthProperties;

    @Override
    public ResponseEntity<String> toPay(IPageData pd) {
        return super.businessProcess(pd);
    }

    @Override
    protected void validate(IPageData pd, JSONObject paramIn) {

        Assert.jsonObjectHaveKey(paramIn, "communityId", "请求报文中未包含communityId节点");
        Assert.jsonObjectHaveKey(paramIn, "cycles", "请求报文中未包含cycles节点");
        Assert.jsonObjectHaveKey(paramIn, "receivedAmount", "请求报文中未包含receivedAmount节点");
        Assert.jsonObjectHaveKey(paramIn, "feeId", "请求报文中未包含feeId节点");
        Assert.jsonObjectHaveKey(paramIn, "feeName", "请求报文中未包含feeName节点");
        Assert.jsonObjectHaveKey(paramIn, "appId", "请求报文中未包含appId节点");

    }

    @Override
    protected ResponseEntity<String> doBusinessProcess(IPageData pd, JSONObject paramIn) throws Exception {

        ResponseEntity<String> responseEntity = null;

        SmallWeChatDto smallWeChatDto = getSmallWechat(pd, paramIn);

        if (smallWeChatDto == null) { //从配置文件中获取 小程序配置信息
            smallWeChatDto = new SmallWeChatDto();
            smallWeChatDto.setAppId(wechatAuthProperties.getAppId());
            smallWeChatDto.setAppSecret(wechatAuthProperties.getSecret());
            smallWeChatDto.setMchId(wechatAuthProperties.getMchId());
            smallWeChatDto.setPayPassword(wechatAuthProperties.getKey());
        }

        //查询用户ID
        paramIn.put("userId", pd.getUserId());
        String url = "fee.payFeePre";
        responseEntity = super.callCenterService(restTemplate, pd, paramIn.toJSONString(), url, HttpMethod.POST);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }
        JSONObject orderInfo = JSONObject.parseObject(responseEntity.getBody().toString());
        String orderId = orderInfo.getString("oId");
        String feeName = orderInfo.getString("feeName");
        double money = Double.parseDouble(orderInfo.getString("receivedAmount"));
        //需要判断金额是否 == 0 等于0 直接掉缴费通知接口
        if (money <= 0) {
            JSONObject paramOut = new JSONObject();
            paramOut.put("oId", orderId);
            String urlOut = "fee.payFeeConfirm";
            responseEntity = this.callCenterService(getHeaders("-1", pd.getAppId()), paramOut.toJSONString(), urlOut, HttpMethod.POST);
            JSONObject param = new JSONObject();
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                param.put("code", "101");
                param.put("msg", "扣费为0回调失败");
                return new ResponseEntity(JSONObject.toJSONString(param), HttpStatus.OK);
            }
            JSONObject result = JSONObject.parseObject(responseEntity.getBody());
            if (ResultVo.CODE_OK != result.getInteger("code")) {
                return responseEntity;
            }
            param.put("code", "100");
            param.put("msg", "扣费为0回调成功");
            return new ResponseEntity(JSONObject.toJSONString(param), HttpStatus.OK);
        }
        String appType = OwnerAppUserDto.APP_TYPE_WECHAT_MINA;
        if (AppDto.WECHAT_OWNER_APP_ID.equals(pd.getAppId())) {
            appType = OwnerAppUserDto.APP_TYPE_WECHAT;
        } else if (AppDto.WECHAT_MINA_OWNER_APP_ID.equals(pd.getAppId())) {
            appType = OwnerAppUserDto.APP_TYPE_WECHAT_MINA;
        } else {
            appType = OwnerAppUserDto.APP_TYPE_APP;
        }

        Map tmpParamIn = new HashMap();
        tmpParamIn.put("userId", pd.getUserId());
        tmpParamIn.put("appType", appType);
        responseEntity = super.getOwnerAppUser(pd, restTemplate, tmpParamIn);
        logger.debug("查询用户信息返回报文：" + responseEntity);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("未查询用户信息异常" + tmpParamIn);
        }
        JSONObject userResult = JSONObject.parseObject(responseEntity.getBody().toString());
        int total = userResult.getIntValue("total");
        if (total < 1) {
            //未查询到用户信息
            throw new IllegalArgumentException("未查询微信用户");
        }

        JSONObject realUserInfo = userResult.getJSONArray("data").getJSONObject(0);
        String openId = realUserInfo.getString("openId");
        String payAdapt = MappingCache.getValue(WechatConstant.WECHAT_DOMAIN, WechatConstant.PAY_ADAPT);
        payAdapt = StringUtil.isEmpty(payAdapt) ? DEFAULT_PAY_ADAPT : payAdapt;
        //支付适配器
        IPayAdapt tPayAdapt = ApplicationContextFactory.getBean(payAdapt, IPayAdapt.class);
        Map result = tPayAdapt.java110Payment(outRestTemplate, feeName, paramIn.getString("tradeType"), orderId, money, openId, smallWeChatDto);
        responseEntity = new ResponseEntity(JSONObject.toJSONString(result), HttpStatus.OK);

        return responseEntity;
    }

    private Map<String, String> getHeaders(String userId, String appId) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CommonConstant.HTTP_APP_ID.toLowerCase(), appId);
        headers.put(CommonConstant.HTTP_USER_ID.toLowerCase(), userId);
        headers.put(CommonConstant.HTTP_TRANSACTION_ID.toLowerCase(), UUID.randomUUID().toString());
        headers.put(CommonConstant.HTTP_REQ_TIME.toLowerCase(), DateUtil.getDefaultFormateTimeString(new Date()));
        headers.put(CommonConstant.HTTP_SIGN.toLowerCase(), "");
        return headers;
    }

    private SmallWeChatDto getSmallWechat(IPageData pd, JSONObject paramIn) {

        ResponseEntity responseEntity = null;

        pd = PageData.newInstance().builder(pd.getUserId(), "", "", pd.getReqData(),
                "", "", "", "",
                pd.getAppId());
        responseEntity = this.callCenterService(restTemplate, pd, "",
                "smallWeChat.listSmallWeChats?appId="
                        + paramIn.getString("appId") + "&page=1&row=1&communityId=" + paramIn.getString("communityId"), HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return null;
        }
        JSONObject smallWechatObj = JSONObject.parseObject(responseEntity.getBody().toString());
        JSONArray smallWeChats = smallWechatObj.getJSONArray("smallWeChats");

        if (smallWeChats == null || smallWeChats.size() < 1) {
            return null;
        }

        return BeanConvertUtil.covertBean(smallWeChats.get(0), SmallWeChatDto.class);
    }


}
