package com.java110.dto.allocationStorehouse;

import com.java110.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 仓库调拨数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class AllocationStorehouseDto extends PageDto implements Serializable {

    public static final String STATE_AUDIT = "1201";//状态，1201 调拨审核 1202 调拨完成 1202 调拨失败
    public static final String STATE_SUCCESS = "1202";//状态，1201 调拨审核 1202 调拨完成 1202 调拨失败
    public static final String STATE_FAIL = "1203";//状态，1201 调拨审核 1202 调拨完成 1202 调拨失败
    private String asId;
    private String storeId;
    private String resId;
    private String shIdz;
    private String resName;
    private String startUserId;
    private String shIda;
    private String startUserName;
    private String state;
    private String stock;
    private String remark;


    private Date createTime;

    private String statusCd = "0";


    public String getAsId() {
        return asId;
    }

    public void setAsId(String asId) {
        this.asId = asId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getShIdz() {
        return shIdz;
    }

    public void setShIdz(String shIdz) {
        this.shIdz = shIdz;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public String getShIda() {
        return shIda;
    }

    public void setShIda(String shIda) {
        this.shIda = shIda;
    }

    public String getStartUserName() {
        return startUserName;
    }

    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
