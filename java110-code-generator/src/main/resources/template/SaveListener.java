package com.java110.api.listener.@@templateCode@@;

import com.java110.api.listener.AbstractServiceApiListener;
import com.java110.utils.util.Assert;
import com.java110.core.context.DataFlowContext;
import com.java110.entity.center.AppService;
import com.java110.event.service.api.ServiceDataFlowEvent;
import com.java110.utils.constant.CommonConstant;
import com.java110.utils.constant.ServiceCodeConstant;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.ServiceCode
import org.springframework.beans.factory.annotation.Autowired;

@@TemplateCode@@Constant;




import com.java110.core.annotation.Java110Listener;
/**
 * 保存小区侦听
 * add by wuxw 2019-06-30
 */
@Java110Listener("save@@TemplateCode@@Listener")
public class Save@@TemplateCode@@Listener extends AbstractServiceApiListener {

    @Autowired
    private I@@TemplateCode@@BMO @@templateCode@@BMOImpl;

    @Override
    protected void validate(ServiceDataFlowEvent event, JSONObject reqJson) {
        //Assert.hasKeyAndValue(reqJson, "xxx", "xxx");

        @@validateTemplateColumns@@
    }

    @Override
    protected void doSoService(ServiceDataFlowEvent event, DataFlowContext context, JSONObject reqJson) {

        HttpHeaders header = new HttpHeaders();
        context.getRequestCurrentHeaders().put(CommonConstant.HTTP_ORDER_TYPE_CD, "D");
        JSONArray businesses = new JSONArray();

        AppService service = event.getAppService();

        //添加单元信息
        businesses.add(@@templateCode@@BMOImpl.add@@TemplateCode@@(reqJson, context));

        ResponseEntity<String> responseEntity = @@templateCode@@BMOImpl.callService(context, service.getServiceCode(), businesses);

        context.setResponseEntity(responseEntity);
    }

    @Override
    public String getServiceCode() {
        return ServiceCode@@TemplateCode@@Constant.ADD_@@TEMPLATECODE@@;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

}
