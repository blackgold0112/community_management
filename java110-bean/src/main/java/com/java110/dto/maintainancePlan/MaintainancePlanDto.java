package com.java110.dto.maintainancePlan;

import com.java110.dto.PageDto;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 保养计划数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class MaintainancePlanDto extends PageDto implements Serializable {

    private String maintainanceDay;
private String planPeriod;
private String createUserId;
private String endDate;
private String standardId;
private String planName;
private String remark;
private String createUserName;
private String maintainanceEveryday;
private String maintainanceMonth;
private String planId;
private String state;
private String communityId;
private String startDate;


    private Date createTime;

    private String statusCd = "0";


    public String getMaintainanceDay() {
        return maintainanceDay;
    }
public void setMaintainanceDay(String maintainanceDay) {
        this.maintainanceDay = maintainanceDay;
    }
public String getPlanPeriod() {
        return planPeriod;
    }
public void setPlanPeriod(String planPeriod) {
        this.planPeriod = planPeriod;
    }
public String getCreateUserId() {
        return createUserId;
    }
public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
public String getEndDate() {
        return endDate;
    }
public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
public String getStandardId() {
        return standardId;
    }
public void setStandardId(String standardId) {
        this.standardId = standardId;
    }
public String getPlanName() {
        return planName;
    }
public void setPlanName(String planName) {
        this.planName = planName;
    }
public String getRemark() {
        return remark;
    }
public void setRemark(String remark) {
        this.remark = remark;
    }
public String getCreateUserName() {
        return createUserName;
    }
public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
public String getMaintainanceEveryday() {
        return maintainanceEveryday;
    }
public void setMaintainanceEveryday(String maintainanceEveryday) {
        this.maintainanceEveryday = maintainanceEveryday;
    }
public String getMaintainanceMonth() {
        return maintainanceMonth;
    }
public void setMaintainanceMonth(String maintainanceMonth) {
        this.maintainanceMonth = maintainanceMonth;
    }
public String getPlanId() {
        return planId;
    }
public void setPlanId(String planId) {
        this.planId = planId;
    }
public String getState() {
        return state;
    }
public void setState(String state) {
        this.state = state;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getStartDate() {
        return startDate;
    }
public void setStartDate(String startDate) {
        this.startDate = startDate;
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
