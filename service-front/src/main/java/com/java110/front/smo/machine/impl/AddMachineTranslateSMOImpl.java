package com.java110.front.smo.machine.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.component.AbstractComponentSMO;
import com.java110.core.context.IPageData;
import com.java110.utils.constant.PrivilegeCodeConstant;
import com.java110.utils.constant.ServiceConstant;
import com.java110.utils.util.Assert;
import com.java110.front.smo.machine.IAddMachineTranslateSMO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



/**
 * 添加小区服务实现类
 * add by wuxw 2019-06-30
 */
@Service("addMachineTranslateSMOImpl")
public class AddMachineTranslateSMOImpl extends AbstractComponentSMO implements IAddMachineTranslateSMO {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected void validate(IPageData pd, JSONObject paramIn) {

        //super.validatePageInfo(pd);

        //Assert.hasKeyAndValue(paramIn, "xxx", "xxx");
        Assert.hasKeyAndValue(paramIn, "machineCode", "必填，请填写设备编码");
        Assert.hasKeyAndValue(paramIn, "machineId", "必填，请填写设备版本号");
        Assert.hasKeyAndValue(paramIn, "typeCd", "必填，请选择对象类型");
        Assert.hasKeyAndValue(paramIn, "objName", "必填，请填写设备名称");
        Assert.hasKeyAndValue(paramIn, "objId", "必填，请填写对象Id");
        Assert.hasKeyAndValue(paramIn, "state", "必填，请选择状态");


        super.checkUserHasPrivilege(pd, restTemplate, PrivilegeCodeConstant.AGENT_HAS_LIST_MACHINE_TRANSLATE);

    }

    @Override
    protected ResponseEntity<String> doBusinessProcess(IPageData pd, JSONObject paramIn) {
        ResponseEntity<String> responseEntity = null;
        super.validateStoreStaffCommunityRelationship(pd, restTemplate);

        responseEntity = this.callCenterService(restTemplate, pd, paramIn.toJSONString(),
                ServiceConstant.SERVICE_API_URL + "/api/machineTranslate.saveMachineTranslate",
                HttpMethod.POST);
        return responseEntity;
    }

    @Override
    public ResponseEntity<String> saveMachineTranslate(IPageData pd) {
        return super.businessProcess(pd);
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
