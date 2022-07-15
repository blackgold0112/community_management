package com.java110.store.cmd.store;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.event.cmd.Cmd;
import com.java110.core.event.cmd.CmdEvent;
import com.java110.service.context.DataQuery;
import com.java110.service.smo.IQueryServiceSMO;
import com.java110.utils.exception.CmdException;
import com.java110.utils.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 查询用户是否存在 商户信息
 */
@Java110Cmd(serviceCode = "query.store.byuser")
public class UserHasStoreCmd extends Cmd {
    @Autowired
    private IQueryServiceSMO queryServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {

    }

    @Override
    public void doCmd(CmdEvent event, ICmdDataFlowContext context, JSONObject reqJson) throws CmdException {
        String userId = event.getCmdDataFlowContext().getReqHeaders().get("user-id");

        DataQuery dataQuery = new DataQuery();
        dataQuery.setServiceCode("query.store.byuser");
        JSONObject param = new JSONObject();
        param.put("userId", userId);
        dataQuery.setRequestParams(param);
        queryServiceSMOImpl.commonQueryService(dataQuery);
        ResponseEntity<String> responseEntity = dataQuery.getResponseEntity();
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            context.setResponseEntity(new ResponseEntity<>("初始化商户", HttpStatus.FORBIDDEN));
            return;
        }

        String storeInfo = responseEntity.getBody();
        if (Assert.isJsonObject(storeInfo) && JSONObject.parseObject(storeInfo).containsKey("storeId")) {
            context.setResponseEntity(new ResponseEntity<>("有商户信息", HttpStatus.OK));
            return;
        }
        context.setResponseEntity(new ResponseEntity<>("初始化商户", HttpStatus.FORBIDDEN));
    }
}
