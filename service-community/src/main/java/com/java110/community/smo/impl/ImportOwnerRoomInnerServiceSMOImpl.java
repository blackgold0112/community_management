package com.java110.community.smo.impl;

import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.log.LoggerFactory;
import com.java110.dto.FloorDto;
import com.java110.dto.RoomDto;
import com.java110.dto.UnitDto;
import com.java110.dto.owner.OwnerDto;
import com.java110.entity.assetImport.ImportOwnerRoomDto;
import com.java110.intf.community.IFloorV1InnerServiceSMO;
import com.java110.intf.community.IImportOwnerRoomInnerServiceSMO;
import com.java110.intf.community.IRoomV1InnerServiceSMO;
import com.java110.intf.community.IUnitV1InnerServiceSMO;
import com.java110.po.floor.FloorPo;
import com.java110.po.room.RoomPo;
import com.java110.po.unit.UnitPo;
import com.java110.utils.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 小区服务内部类
 */
@RestController
public class ImportOwnerRoomInnerServiceSMOImpl extends BaseServiceSMO implements IImportOwnerRoomInnerServiceSMO {
    private static Logger logger = LoggerFactory.getLogger(CommunityServiceSMOImpl.class);


    @Autowired
    private IFloorV1InnerServiceSMO floorV1InnerServiceSMOImpl;

    @Autowired
    private IUnitV1InnerServiceSMO unitV1InnerServiceSMOImpl;

    @Autowired
    private IRoomV1InnerServiceSMO roomV1InnerServiceSMOImpl;

    @Autowired
    public int saveOwnerRooms(@RequestBody List<ImportOwnerRoomDto> importOwnerRoomDtos) {

        int successCount = 0;
        if (importOwnerRoomDtos == null || importOwnerRoomDtos.size() < 1) {
            return 0;
        }

        List<ImportOwnerRoomDto> importOwnerRoomDtosed = new ArrayList<>();
        // 1.0 查看 楼栋是否存在
        for (ImportOwnerRoomDto importOwnerRoomDto : importOwnerRoomDtos) {
            successCount += doSaveOwnerRooms(importOwnerRoomDto, importOwnerRoomDtosed);
        }


        return successCount;
    }

    /**
     * 导入数据
     *
     * @param importOwnerRoomDto
     * @param importOwnerRoomDtosed
     * @return
     */
    private int doSaveOwnerRooms(ImportOwnerRoomDto importOwnerRoomDto, List<ImportOwnerRoomDto> importOwnerRoomDtosed) {

        //1.0 保存楼栋信息
        String floorId = doSaveFloor(importOwnerRoomDto);


        //2.0 保存单元信息
        String unitId = doSaveUnit(importOwnerRoomDto, floorId);

        //3.0 保存 房屋
        String roomId = doSaveRoom(importOwnerRoomDto, unitId);

        return 1;

    }

    private String doSaveRoom(ImportOwnerRoomDto importOwnerRoomDto, String unitId) {
        RoomDto roomDto = new RoomDto();
        roomDto.setCommunityId(importOwnerRoomDto.getCommunityId());
        roomDto.setRoomNum(importOwnerRoomDto.getRoomNum());
        roomDto.setUnitId(unitId);
        List<RoomDto> roomDtos = roomV1InnerServiceSMOImpl.queryRooms(roomDto);
        RoomPo roomPo = null;
        String roomId = "";

        int flag = 0;
        if (roomDtos == null || roomDtos.size() < 1) {
            roomPo = new RoomPo();
            roomPo.setState(StringUtil.isEmpty(importOwnerRoomDto.getOwnerName()) ? RoomDto.STATE_FREE : RoomDto.STATE_SELL);
            roomPo.setRoomId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_roomId));
            roomPo.setApartment(importOwnerRoomDto.getSection());
            roomPo.setSection("1");
            roomPo.setCommunityId(importOwnerRoomDto.getCommunityId());
            roomPo.setBuiltUpArea(importOwnerRoomDto.getBuiltUpArea());
            roomPo.setFeeCoefficient("1.00");
            roomPo.setLayer(importOwnerRoomDto.getLayer());
            roomPo.setRoomArea(importOwnerRoomDto.getRoomArea());
            roomPo.setRoomNum(importOwnerRoomDto.getRoomNum());
            roomPo.setRoomRent(importOwnerRoomDto.getRoomRent());
            roomPo.setRoomSubType(importOwnerRoomDto.getRoomSubType());
            roomPo.setRoomType("0".equals(importOwnerRoomDto.getUnitNum()) ? RoomDto.ROOM_TYPE_SHOPS : RoomDto.ROOM_TYPE_ROOM);
            roomPo.setUnitId(unitId);
            roomPo.setRemark("房产导入");
            roomPo.setUserId("-1");
            flag = roomV1InnerServiceSMOImpl.saveRoom(roomPo);

            if (flag < 1) {
                throw new IllegalArgumentException("导入房屋失败");
            }

            roomId = roomPo.getRoomId();
        } else {
            roomId = roomDtos.get(0).getRoomId();
            if (OwnerDto.OWNER_TYPE_CD_OWNER.equals(importOwnerRoomDto.getOwnerTypeCd())) {
                roomPo = new RoomPo();
                roomPo.setState(StringUtil.isEmpty(importOwnerRoomDto.getOwnerName()) ? RoomDto.STATE_FREE : RoomDto.STATE_SELL);
                roomPo.setRoomId(roomId);
                roomPo.setApartment(importOwnerRoomDto.getSection());
                roomPo.setSection("1");
                roomPo.setCommunityId(importOwnerRoomDto.getCommunityId());
                roomPo.setBuiltUpArea(importOwnerRoomDto.getBuiltUpArea());
                roomPo.setFeeCoefficient("1.00");
                roomPo.setLayer(importOwnerRoomDto.getLayer());
                roomPo.setRoomArea(importOwnerRoomDto.getRoomArea());
                roomPo.setRoomNum(importOwnerRoomDto.getRoomNum());
                roomPo.setRoomRent(importOwnerRoomDto.getRoomRent());
                roomPo.setRoomSubType(importOwnerRoomDto.getRoomSubType());
                roomPo.setRoomType("0".equals(importOwnerRoomDto.getUnitNum()) ? RoomDto.ROOM_TYPE_SHOPS : RoomDto.ROOM_TYPE_ROOM);
                roomPo.setUnitId(unitId);
                roomPo.setRemark("房产导入");
                roomPo.setUserId("-1");
                flag = roomV1InnerServiceSMOImpl.updateRoom(roomPo);
                if (flag < 1) {
                    throw new IllegalArgumentException("导入房屋失败");
                }
            }
        }
        return roomId;

    }

    /**
     * 保存单元信息
     *
     * @param importOwnerRoomDto
     * @param floorId
     * @return
     */
    private String doSaveUnit(ImportOwnerRoomDto importOwnerRoomDto, String floorId) {

        UnitDto unitDto = new UnitDto();
        unitDto.setCommunityId(importOwnerRoomDto.getCommunityId());
        unitDto.setUnitNum(importOwnerRoomDto.getUnitNum());
        unitDto.setFloorId(floorId);
        List<UnitDto> unitDtos = unitV1InnerServiceSMOImpl.queryUnits(unitDto);
        UnitPo unitPo = null;
        String unitId = "";

        int flag = 0;
        if (unitDtos == null || unitDtos.size() < 1) {
            unitPo = new UnitPo();
            unitPo.setFloorId(floorId);
            unitPo.setLayerCount(importOwnerRoomDto.getLayerCount());
            unitPo.setLift(importOwnerRoomDto.getLift());
            unitPo.setUnitId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_unitId));
            unitPo.setUnitArea("1");
            unitPo.setRemark("房产导入");
            unitPo.setUserId("-1");
            flag = unitV1InnerServiceSMOImpl.saveUnit(unitPo);

            if (flag < 1) {
                throw new IllegalArgumentException("导入单元失败");
            }

            unitId = unitPo.getFloorId();
        } else {
            unitId = unitDtos.get(0).getUnitId();
            if (OwnerDto.OWNER_TYPE_CD_OWNER.equals(importOwnerRoomDto.getOwnerTypeCd())) {
                unitPo = new UnitPo();
                unitPo.setFloorId(floorId);
                unitPo.setLayerCount(importOwnerRoomDto.getLayerCount());
                unitPo.setLift(importOwnerRoomDto.getLift());
                unitPo.setUnitId(unitId);
                unitPo.setUnitArea("1");
                unitPo.setRemark("房产导入");
                unitPo.setUserId("-1");
                flag = unitV1InnerServiceSMOImpl.updateUnit(unitPo);
                if (flag < 1) {
                    throw new IllegalArgumentException("导入单元失败");
                }
            }
        }
        return unitId;
    }

    /**
     * 保存楼栋信息
     *
     * @param importOwnerRoomDto
     * @return
     */
    private String doSaveFloor(ImportOwnerRoomDto importOwnerRoomDto) {
        FloorDto floorDto = new FloorDto();
        floorDto.setCommunityId(importOwnerRoomDto.getCommunityId());
        floorDto.setFloorNum(importOwnerRoomDto.getFloorNum());
        List<FloorDto> floorDtos = floorV1InnerServiceSMOImpl.queryFloors(floorDto);
        FloorPo floorPo = null;
        String floorId = "";

        int flag = 0;
        if (floorDtos == null || floorDtos.size() < 1) {
            floorPo = new FloorPo();
            floorPo.setbId("-1");
            floorPo.setCommunityId(importOwnerRoomDto.getCommunityId());
            floorPo.setFloorArea("1");
            floorPo.setFloorId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_floorId));
            floorPo.setFloorNum(importOwnerRoomDto.getFloorNum());
            floorPo.setRemark("房产导入");
            floorPo.setUserId("-1");
            flag = floorV1InnerServiceSMOImpl.saveFloor(floorPo);

            if (flag < 1) {
                throw new IllegalArgumentException("导入楼栋失败");
            }

            floorId = floorPo.getFloorId();
        } else {
            floorId = floorDtos.get(0).getFloorId();
            if (OwnerDto.OWNER_TYPE_CD_OWNER.equals(importOwnerRoomDto.getOwnerTypeCd())) {
                floorPo = new FloorPo();
                floorPo.setbId("-1");
                floorPo.setCommunityId(importOwnerRoomDto.getCommunityId());
                floorPo.setFloorArea("1");
                floorPo.setFloorId(floorId);
                floorPo.setFloorNum(importOwnerRoomDto.getFloorNum());
                floorPo.setRemark("房产导入");
                floorPo.setUserId("-1");
                flag = floorV1InnerServiceSMOImpl.updateFloor(floorPo);
                if (flag < 1) {
                    throw new IllegalArgumentException("导入楼栋失败");
                }
            }
        }
        return floorId;
    }


}
