package com.java110.listener.merchant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.context.AppContext;
import com.java110.event.AppListener;
import com.java110.event.merchant.AppMerchantEvent;
import com.java110.event.order.Ordered;

/**
 * Created by wuxw on 2017/4/14.
 */
public class MerchantDispatchListener implements AppListener<AppMerchantEvent>,Ordered {

    private final static int order = Ordered.dafultValue+1;

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * 商户信息受理
     * @param event the event to respond to
     */
    @Override
    public void soDataService(AppMerchantEvent event) {
        //这里处理 商户相关信息

        AppContext context = event.getContext();

        //获取商户相关的信息
        JSONArray dataMerchantInfos = event.getData();

    }

    @Override
    public JSONObject queryDataInfo(AppMerchantEvent event) {
        return null;
    }

    @Override
    public JSONObject queryNeedDeleteDataInfo(AppMerchantEvent event) {
        return null;
    }
}
