package com.java110.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 费用明细数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class FeeDetailDto extends PageDto implements Serializable {

    private String primeRate;
private String detailId;
private String receivableAmount;
private String cycles;
private String remark;
private String receivedAmount;
private String communityId;
private String feeId;


    private Date createTime;

    private String statusCd = "0";


    public String getPrimeRate() {
        return primeRate;
    }
public void setPrimeRate(String primeRate) {
        this.primeRate = primeRate;
    }
public String getDetailId() {
        return detailId;
    }
public void setDetailId(String detailId) {
        this.detailId = detailId;
    }
public String getReceivableAmount() {
        return receivableAmount;
    }
public void setReceivableAmount(String receivableAmount) {
        this.receivableAmount = receivableAmount;
    }
public String getCycles() {
        return cycles;
    }
public void setCycles(String cycles) {
        this.cycles = cycles;
    }
public String getRemark() {
        return remark;
    }
public void setRemark(String remark) {
        this.remark = remark;
    }
public String getReceivedAmount() {
        return receivedAmount;
    }
public void setReceivedAmount(String receivedAmount) {
        this.receivedAmount = receivedAmount;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getFeeId() {
        return feeId;
    }
public void setFeeId(String feeId) {
        this.feeId = feeId;
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
}
