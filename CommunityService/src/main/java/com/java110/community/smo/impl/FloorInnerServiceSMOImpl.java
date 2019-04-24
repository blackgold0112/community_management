package com.java110.community.smo.impl;

import com.java110.common.util.BeanConvertUtil;
import com.java110.community.dao.IFloorServiceDao;
import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.core.smo.floor.IFloorInnerServiceSMO;
import com.java110.core.smo.user.IUserInnerServiceSMO;
import com.java110.dto.FloorDto;
import com.java110.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 小区内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class FloorInnerServiceSMOImpl extends BaseServiceSMO implements IFloorInnerServiceSMO {

    @Autowired
    private IFloorServiceDao floorServiceDaoImpl;

    @Autowired
    private IUserInnerServiceSMO userInnerServiceSMOImpl;

    /**
     * 查询 信息
     *
     * @param page        封装查询条件
     * @param row         行数
     * @param communityId 小区ID
     * @return 小区对应的楼
     */
    @Override
    public List<FloorDto> queryFloors(@RequestParam("page") int page, @RequestParam("row") int row, @RequestParam("communityId") String communityId) {
        Map<String, Object> floorInfo = new HashMap<String, Object>();
        floorInfo.put("page", page);
        floorInfo.put("row", row);
        floorInfo.put("communityId", communityId);
        List<FloorDto> floors = BeanConvertUtil.covertBeanList(floorServiceDaoImpl.queryFloors(floorInfo), FloorDto.class);

        String[] userIds = getUserIds(floors);
        //根据 userId 查询用户信息
        List<UserDto> users = userInnerServiceSMOImpl.getUserInfo(userIds);

        for (FloorDto floor : floors) {
            refreshFloor(floor, users);
        }
        return floors;
    }

    /**
     * 查询小区对应总记录数
     *
     * @param communityId 小区ID
     * @return 小区对应的楼总记录数
     */
    @Override
    public int queryFloorsCount(@RequestParam("communityId") String communityId) {
        return floorServiceDaoImpl.queryFloorsCount(communityId);
    }

    /**
     * 从用户列表中查询用户，将用户中的信息 刷新到 floor对象中
     *
     * @param floor 小区楼信息
     * @param users 用户列表
     */
    private void refreshFloor(FloorDto floor, List<UserDto> users) {
        for (UserDto user : users) {
            if (floor.getUserId().equals(user.getUserId())) {
                BeanConvertUtil.covertBean(user, floor);
            }
        }
    }

    /**
     * 获取批量userId
     *
     * @param floors 小区楼信息
     * @return 批量userIds 信息
     */
    private String[] getUserIds(List<FloorDto> floors) {
        List<String> userIds = new ArrayList<>();
        for (FloorDto floor : floors) {
            userIds.add(floor.getUserId());
        }

        return (String[]) userIds.toArray();
    }

    public IFloorServiceDao getFloorServiceDaoImpl() {
        return floorServiceDaoImpl;
    }

    public void setFloorServiceDaoImpl(IFloorServiceDao floorServiceDaoImpl) {
        this.floorServiceDaoImpl = floorServiceDaoImpl;
    }

    public IUserInnerServiceSMO getUserInnerServiceSMOImpl() {
        return userInnerServiceSMOImpl;
    }

    public void setUserInnerServiceSMOImpl(IUserInnerServiceSMO userInnerServiceSMOImpl) {
        this.userInnerServiceSMOImpl = userInnerServiceSMOImpl;
    }
}
