package com.java110.user.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.common.constant.ResponseConstant;
import com.java110.common.constant.ServiceCodeConstant;
import com.java110.common.constant.StatusConstant;
import com.java110.common.exception.ListenerExecuteException;
import com.java110.common.log.LoggerEngine;
import com.java110.common.util.Assert;
import com.java110.core.context.DataFlowContext;
import com.java110.core.factory.DataTransactionFactory;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.entity.center.Business;
import com.java110.event.service.BusinessServiceDataFlowEvent;
import com.java110.event.service.BusinessServiceDataFlowListener;
import com.java110.user.dao.IUserServiceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存 用户信息 侦听
 * Created by wuxw on 2018/5/18.
 */
@Service("saveUserTag")
@Transactional
public class SaveUserTagListener extends LoggerEngine implements BusinessServiceDataFlowListener{

    private final static Logger logger = LoggerFactory.getLogger(SaveUserTagListener.class);

    @Autowired
    IUserServiceDao userServiceDaoImpl;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_SAVE_USER_TAG;
    }

    @Override
    public void soService(BusinessServiceDataFlowEvent event) {
        //这里处理业务逻辑数据
        DataFlowContext dataFlowContext = event.getDataFlowContext();
        doSaveUserTag(dataFlowContext);
    }

    private void doSaveUserTag(DataFlowContext dataFlowContext){
        String businessType = dataFlowContext.getOrder().getBusinessType();
        Business business = dataFlowContext.getCurrentBusiness();
        //Assert.hasLength(business.getbId(),"bId 不能为空");
        // Instance 过程
        if(StatusConstant.REQUEST_BUSINESS_TYPE_INSTANCE.equals(businessType)){
            //doComplateUserInfo(business);
            doSaveInstanceUserTag(dataFlowContext,business);
        }else if(StatusConstant.REQUEST_BUSINESS_TYPE_BUSINESS.equals(businessType)){ // Business过程
            doSaveBusinessUserTag(dataFlowContext,business);
        }else if(StatusConstant.REQUEST_BUSINESS_TYPE_DELETE.equals(businessType)){ //撤单过程
            doDeleteInstanceUserTag(dataFlowContext,business);
        }

        dataFlowContext.setResJson(DataTransactionFactory.createBusinessResponseJson(dataFlowContext,ResponseConstant.RESULT_CODE_SUCCESS,"成功",
                dataFlowContext.getParamOut()));
    }

    /**
     * 撤单
     * @param business
     */
    private void doDeleteInstanceUserTag(DataFlowContext dataFlowContext, Business business) {

        String bId = business.getbId();
        //Assert.hasLength(bId,"请求报文中没有包含 bId");
        Map info = new HashMap();
        info.put("bId",bId);
        Map userTag = userServiceDaoImpl.queryBusinessUserTag(info);
        if(userTag != null && !userTag.isEmpty()){
            info.put("bId",bId);
            info.put("userId",userTag.get("user_id").toString());
            info.put("tagId",userTag.get("tag_id").toString());
            info.put("statusCd",StatusConstant.STATUS_CD_INVALID);
            userServiceDaoImpl.updateUserTagInstance(userTag);
            dataFlowContext.addParamOut("userId",userTag.get("user_id"));
        }
    }

    /**
     * instance过程
     * @param business
     */
    private void doSaveInstanceUserTag(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Map info = new HashMap();
        info.put("bId",business.getbId());
        info.put("operate",StatusConstant.OPERATE_ADD);
        Map businessUserTag = userServiceDaoImpl.queryBusinessUserTag(info);
        if( businessUserTag != null && !businessUserTag.isEmpty()) {
            userServiceDaoImpl.saveUserTagInstance(businessUserTag);
            dataFlowContext.addParamOut("userId",businessUserTag.get("user_id"));
            return ;
        }

        throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_ERROR,"当前数据未找到business 数据"+info);
    }

    /**
     * 处理用户打标信息
     * @param business 业务信息
     */
    private void doSaveBusinessUserTag(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Assert.notEmpty(data,"没有datas 节点，或没有子节点需要处理");

        Assert.jsonObjectHaveKey(data,"businessUserTag","datas 节点下没有包含 businessUser 节点");

        JSONObject businessUser = data.getJSONObject("businessUserTag");

        Assert.jsonObjectHaveKey(businessUser,"userId","businessUser 节点下没有包含 userId 节点");

        if(businessUser.getLong("userId") <= 0){
            throw new ListenerExecuteException(ResponseConstant.RESULT_PARAM_ERROR,"用户打标（saveUserTag）保存失败，userId 不正确"+businessUser.getInteger("userId"));
        }
        dataFlowContext.addParamOut("userId",businessUser.getString("userId"));
        businessUser.put("bId",business.getbId());
        businessUser.put("operate", StatusConstant.OPERATE_ADD);
        //保存用户信息
        userServiceDaoImpl.saveBusinessUserTag(businessUser);

    }

    public IUserServiceDao getUserServiceDaoImpl() {
        return userServiceDaoImpl;
    }

    public void setUserServiceDaoImpl(IUserServiceDao userServiceDaoImpl) {
        this.userServiceDaoImpl = userServiceDaoImpl;
    }
}
