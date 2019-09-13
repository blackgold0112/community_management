package com.java110.api.listener.owner;


import com.alibaba.fastjson.JSONObject;
import com.java110.api.listener.AbstractServiceApiDataFlowListener;
import com.java110.common.constant.ServiceCodeConstant;
import com.java110.common.util.Assert;
import com.java110.common.util.BeanConvertUtil;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.smo.owner.IOwnerInnerServiceSMO;
import com.java110.dto.OwnerDto;
import com.java110.event.service.api.ServiceDataFlowEvent;
import com.java110.vo.api.ApiOwnerDataVo;
import com.java110.vo.api.ApiOwnerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @ClassName OwnerDto
 * @Description 小区楼数据层侦听类
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@Java110Listener("queryOwnersListener")
public class QueryOwnersListener extends AbstractServiceApiDataFlowListener {

    @Autowired
    private IOwnerInnerServiceSMO ownerInnerServiceSMOImpl;

    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_QUERY_OWNER;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

    /**
     * 业务层数据处理
     *
     * @param event 时间对象
     */
    @Override
    public void soService(ServiceDataFlowEvent event) {
        DataFlowContext dataFlowContext = event.getDataFlowContext();
        //获取请求数据
        JSONObject reqJson = dataFlowContext.getReqJson();
        validateOwnerData(reqJson);


        if (reqJson.containsKey("name")) {
            queryByCondition(reqJson, dataFlowContext);
            return;
        }

        int row = reqJson.getInteger("row");

        ApiOwnerVo apiOwnerVo = new ApiOwnerVo();

        //查询总记录数
        int total = ownerInnerServiceSMOImpl.queryOwnersCount(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
        apiOwnerVo.setTotal(total);
        if (total > 0) {
            List<OwnerDto> ownerDtoList = ownerInnerServiceSMOImpl.queryOwners(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
            apiOwnerVo.setOwners(BeanConvertUtil.covertBeanList(ownerDtoList, ApiOwnerDataVo.class));
        }

        apiOwnerVo.setRecords((int) Math.ceil((double) total / (double) row));


        ResponseEntity<String> responseEntity = new ResponseEntity<String>(JSONObject.toJSONString(apiOwnerVo), HttpStatus.OK);
        dataFlowContext.setResponseEntity(responseEntity);
    }

    /**
     * 根据条件查询
     *
     * @param reqJson         查询信息
     * @param dataFlowContext 上下文
     */
    private void queryByCondition(JSONObject reqJson, DataFlowContext dataFlowContext) {

        int row = reqJson.getInteger("row");
        ApiOwnerVo apiOwnerVo = new ApiOwnerVo();
        int total = ownerInnerServiceSMOImpl.queryOwnerCountByCondition(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
        apiOwnerVo.setTotal(total);
        if (total > 0) {
            List<OwnerDto> ownerDtoList = ownerInnerServiceSMOImpl.queryOwnersByCondition(BeanConvertUtil.covertBean(reqJson, OwnerDto.class));
            apiOwnerVo.setOwners(BeanConvertUtil.covertBeanList(ownerDtoList, ApiOwnerDataVo.class));
        }

        apiOwnerVo.setRecords((int) Math.ceil((double) total / (double) row));

        ResponseEntity<String> responseEntity = new ResponseEntity<String>(JSONObject.toJSONString(apiOwnerVo), HttpStatus.OK);
        dataFlowContext.setResponseEntity(responseEntity);
    }

    /**
     * 校验查询条件是否满足条件
     *
     * @param reqJson 包含查询条件
     */
    private void validateOwnerData(JSONObject reqJson) {
        Assert.jsonObjectHaveKey(reqJson, "page", "请求中未包含page信息");
        Assert.jsonObjectHaveKey(reqJson, "row", "请求中未包含row信息");
        Assert.jsonObjectHaveKey(reqJson, "communityId", "请求中未包含communityId信息");
        Assert.jsonObjectHaveKey(reqJson, "ownerTypeCd", "请求中未包含ownerTypeCd信息");
        Assert.isInteger(reqJson.getString("page"), "不是有效数字");
        Assert.isInteger(reqJson.getString("row"), "不是有效数字");

    }

    @Override
    public int getOrder() {
        return super.DEFAULT_ORDER;
    }


    public IOwnerInnerServiceSMO getOwnerInnerServiceSMOImpl() {
        return ownerInnerServiceSMOImpl;
    }

    public void setOwnerInnerServiceSMOImpl(IOwnerInnerServiceSMO ownerInnerServiceSMOImpl) {
        this.ownerInnerServiceSMOImpl = ownerInnerServiceSMOImpl;
    }
}
