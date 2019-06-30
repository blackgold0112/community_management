package com.java110.dto.community;

import com.java110.dto.PageDto;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName FloorDto
 * @Description 小区数据层封装
 * @Author wuxw
 * @Date 2019/4/24 8:52
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public class CommunityDto extends PageDto implements Serializable {

    private String address;
private String nearbyLandmarks;
private String cityCode;
private String name;
private String communityId;
private String mapY;
private String mapX;


    private Date createTime;

    private String statusCd = "0";


    public String getAddress() {
        return address;
    }
public void setAddress(String address) {
        this.address = address;
    }
public String getNearbyLandmarks() {
        return nearbyLandmarks;
    }
public void setNearbyLandmarks(String nearbyLandmarks) {
        this.nearbyLandmarks = nearbyLandmarks;
    }
public String getCityCode() {
        return cityCode;
    }
public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
public String getName() {
        return name;
    }
public void setName(String name) {
        this.name = name;
    }
public String getCommunityId() {
        return communityId;
    }
public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
public String getMapY() {
        return mapY;
    }
public void setMapY(String mapY) {
        this.mapY = mapY;
    }
public String getMapX() {
        return mapX;
    }
public void setMapX(String mapX) {
        this.mapX = mapX;
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
