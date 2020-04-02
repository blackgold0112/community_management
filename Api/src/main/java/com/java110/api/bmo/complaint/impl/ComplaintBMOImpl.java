package com.java110.api.bmo.complaint.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.ApiBaseBMO;
import com.java110.api.bmo.IApiBaseBMO;
import com.java110.api.bmo.complaint.IComplaintBMO;
import com.java110.core.context.DataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.smo.complaint.IComplaintInnerServiceSMO;
import com.java110.core.smo.complaintUser.IComplaintUserInnerServiceSMO;
import com.java110.dto.complaint.ComplaintDto;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.CommonConstant;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ComplaintBMOImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/3/9 22:13
 * @Version 1.0
 * add by wuxw 2020/3/9
 **/
@Service("complaintBMOImpl")
public class ComplaintBMOImpl extends ApiBaseBMO implements IComplaintBMO {


    @Autowired
    private IComplaintInnerServiceSMO complaintInnerServiceSMOImpl;

    /**
     * 添加投诉建议信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject updateComplaint(JSONObject paramInJson, DataFlowContext dataFlowContext) {

        ComplaintDto complaintDto = new ComplaintDto();
        complaintDto.setStoreId(paramInJson.getString("storeId"));
        complaintDto.setCommunityId(paramInJson.getString("communityId"));
        complaintDto.setComplaintId(paramInJson.getString("complaintId"));
        List<ComplaintDto> complaintDtos = complaintInnerServiceSMOImpl.queryComplaints(complaintDto);

        Assert.listOnlyOne(complaintDtos, "存在多条记录，或不存在数据" + complaintDto.getComplaintId());


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_UPDATE_COMPLAINT);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessComplaint = new JSONObject();
        businessComplaint.putAll(BeanConvertUtil.beanCovertMap(complaintDtos.get(0)));
        businessComplaint.put("state", "10002");
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessComplaint", businessComplaint);
        return business;
    }

    /**
     * 添加小区信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject deleteComplaint(JSONObject paramInJson, DataFlowContext dataFlowContext) {


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_DELETE_COMPLAINT);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessComplaint = new JSONObject();
        businessComplaint.putAll(paramInJson);
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessComplaint", businessComplaint);
        return business;
    }

    /**
     * 添加小区信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject addComplaint(JSONObject paramInJson, DataFlowContext dataFlowContext) {

        paramInJson.put("complaintId", GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_complaintId));
        paramInJson.put("state", "10001");

        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_SAVE_COMPLAINT);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessComplaint = new JSONObject();
        businessComplaint.putAll(paramInJson);
        //businessComplaint.put("complaintId", "-1");
        //businessComplaint.put("state", "10001");
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessComplaint", businessComplaint);
        return business;
    }

    /**
     * 添加投诉建议信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject upComplaint(JSONObject paramInJson, DataFlowContext dataFlowContext) {

        ComplaintDto complaintDto = new ComplaintDto();
        complaintDto.setStoreId(paramInJson.getString("storeId"));
        complaintDto.setComplaintId(paramInJson.getString("complaintId"));
        List<ComplaintDto> complaintDtos = complaintInnerServiceSMOImpl.queryComplaints(complaintDto);

        Assert.listOnlyOne(complaintDtos, "存在多条记录，或不存在数据" + complaintDto.getComplaintId());


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_UPDATE_COMPLAINT);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessComplaint = new JSONObject();
        businessComplaint.putAll(paramInJson);
        businessComplaint.put("state", complaintDtos.get(0).getState());
        businessComplaint.put("roomId", complaintDtos.get(0).getRoomId());
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessComplaint", businessComplaint);
        return business;
    }
}
