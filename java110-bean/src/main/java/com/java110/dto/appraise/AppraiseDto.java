package com.java110.dto.appraise;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价实体类
 */
public class AppraiseDto implements Serializable {


    private String appraiseId;
    private int appraiseScore;
    private String appraiseType;
    private String context;
    private String parentAppraiseId;
    private Date createTime;
    private String appraiseUserId;
    private String appraiseUserName;
    private String objType;
    private String objId;


    public String getAppraiseId() {
        return appraiseId;
    }

    public void setAppraiseId(String appraiseId) {
        this.appraiseId = appraiseId;
    }

    public int getAppraiseScore() {
        return appraiseScore;
    }

    public void setAppraiseScore(int appraiseScore) {
        this.appraiseScore = appraiseScore;
    }

    public String getAppraiseType() {
        return appraiseType;
    }

    public void setAppraiseType(String appraiseType) {
        this.appraiseType = appraiseType;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getParentAppraiseId() {
        return parentAppraiseId;
    }

    public void setParentAppraiseId(String parentAppraiseId) {
        this.parentAppraiseId = parentAppraiseId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAppraiseUserId() {
        return appraiseUserId;
    }

    public void setAppraiseUserId(String appraiseUserId) {
        this.appraiseUserId = appraiseUserId;
    }

    public String getAppraiseUserName() {
        return appraiseUserName;
    }

    public void setAppraiseUserName(String appraiseUserName) {
        this.appraiseUserName = appraiseUserName;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }
}
