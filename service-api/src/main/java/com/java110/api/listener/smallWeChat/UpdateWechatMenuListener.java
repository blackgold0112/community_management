package com.java110.api.listener.smallWeChat;

import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.smallWeChat.IWechatMenuBMO;
import com.java110.api.listener.AbstractServiceApiPlusListener;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.event.service.api.ServiceDataFlowEvent;
import com.java110.utils.constant.ServiceCodeWechatMenuConstant;
import com.java110.utils.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;


/**
 * 保存公众号菜单侦听
 * add by wuxw 2019-06-30
 */
@Java110Listener("updateWechatMenuListener")
public class UpdateWechatMenuListener extends AbstractServiceApiPlusListener {

    @Autowired
    private IWechatMenuBMO wechatMenuBMOImpl;

    @Override
    protected void validate(ServiceDataFlowEvent event, JSONObject reqJson) {

        Assert.hasKeyAndValue(reqJson, "wechatMenuId", "wechatMenuId不能为空");
        Assert.hasKeyAndValue(reqJson, "menuName", "请求报文中未包含menuName");
        Assert.hasKeyAndValue(reqJson, "menuType", "请求报文中未包含menuType");
        Assert.hasKeyAndValue(reqJson, "menuLevel", "请求报文中未包含menuLevel");
        Assert.hasKeyAndValue(reqJson, "menuValue", "请求报文中未包含menuValue");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");

    }

    @Override
    protected void doSoService(ServiceDataFlowEvent event, DataFlowContext context, JSONObject reqJson) {

        wechatMenuBMOImpl.updateWechatMenu(reqJson, context);
    }

    @Override
    public String getServiceCode() {
        return ServiceCodeWechatMenuConstant.UPDATE_WECHATMENU;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
