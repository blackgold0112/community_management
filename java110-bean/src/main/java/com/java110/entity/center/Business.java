package com.java110.entity.center;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 业务数据
 * Created by wuxw on 2018/4/13.
 */
public class Business {

    private String bId;

    //业务编码
    private String serviceCode;

    private String serviceName;

    private String remark;

    private JSONArray datas;

    private JSONArray attrs;
    //返回 编码
    private String code;

    private String message;


    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public JSONArray getDatas() {
        return datas;
    }

    public void setDatas(JSONArray datas) {
        this.datas = datas;
    }

    public JSONArray getAttrs() {
        return attrs;
    }

    public void setAttrs(JSONArray attrs) {
        this.attrs = attrs;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 构建成对象
     * @return
     * @throws Exception
     */
    public Business builder(JSONObject businessObj) throws Exception{

        try{
            this.setServiceCode(businessObj.getString("serviceCode"));
            this.setServiceName(businessObj.getString("serviceName"));
            this.setRemark(businessObj.getString("remark"));
            this.setDatas(businessObj.getJSONArray("datas"));
            this.setAttrs(businessObj.getJSONArray("attrs"));
        }catch (Exception e){
            throw e;
        }
        return this;
    }
}
