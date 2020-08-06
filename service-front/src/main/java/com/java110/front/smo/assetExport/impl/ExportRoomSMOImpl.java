package com.java110.front.smo.assetExport.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.component.BaseComponentSMO;
import com.java110.core.context.IPageData;
import com.java110.entity.component.ComponentValidateResult;
import com.java110.front.smo.assetExport.IExportRoomSMO;
import com.java110.utils.constant.ServiceConstant;
import com.java110.utils.util.Assert;
import com.java110.utils.util.DateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName AssetImportSmoImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2019/9/23 23:14
 * @Version 1.0
 * add by wuxw 2019/9/23
 **/
@Service("exportRoomSMOImpl")
public class ExportRoomSMOImpl extends BaseComponentSMO implements IExportRoomSMO {
    private final static Logger logger = LoggerFactory.getLogger(ExportRoomSMOImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<Object> exportExcelData(IPageData pd) throws Exception {

        ComponentValidateResult result = this.validateStoreStaffCommunityRelationship(pd, restTemplate);

        Assert.hasKeyAndValue(JSONObject.parseObject(pd.getReqData()), "communityId", "请求中未包含小区");

        Workbook workbook = null;  //工作簿
        //工作表
        workbook = new HSSFWorkbook();
        //获取楼信息
        getRooms(pd, result, workbook);


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MultiValueMap headers = new HttpHeaders();
        headers.add("content-type", "application/octet-stream;charset=UTF-8");
        headers.add("Content-Disposition", "attachment;filename=" + DateUtil.getyyyyMMddhhmmssDateString() + ".xls");
        headers.add("Pargam", "no-cache");
        headers.add("Cache-Control", "no-cache");
        //headers.add("Content-Disposition", "attachment; filename=" + outParam.getString("fileName"));
        headers.add("Accept-Ranges", "bytes");
        byte[] context = null;
        try {
            workbook.write(os);
            context = os.toByteArray();
            os.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            // 保存数据
            return new ResponseEntity<Object>("导出失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // 保存数据
        return new ResponseEntity<Object>(context, headers, HttpStatus.OK);
    }


    /**
     * 查询存在的房屋信息
     * room.queryRooms
     *
     * @param pd
     * @param result
     * @return
     */
    private JSONArray getExistsRoom(IPageData pd, ComponentValidateResult result) {
        String apiUrl = "";
        ResponseEntity<String> responseEntity = null;
        apiUrl = ServiceConstant.SERVICE_API_URL + "/api/room.queryRooms?page=1&row=10000&communityId=" + result.getCommunityId();
        responseEntity = this.callCenterService(restTemplate, pd, "", apiUrl, HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) { //跳过 保存单元信息
            return null;
        }

        JSONObject savedRoomInfoResults = JSONObject.parseObject(responseEntity.getBody());


        if (!savedRoomInfoResults.containsKey("rooms")) {
            return null;
        }


        return savedRoomInfoResults.getJSONArray("rooms");

    }


    private JSONArray getExistsUnit(IPageData pd, ComponentValidateResult result, String floorId) {
        String apiUrl = "";
        ResponseEntity<String> responseEntity = null;
        apiUrl = ServiceConstant.SERVICE_API_URL + "/api/unit.queryUnits?communityId=" + result.getCommunityId() + "&floorId=" + floorId;
        responseEntity = this.callCenterService(restTemplate, pd, "", apiUrl, HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) { //跳过 保存单元信息
            return null;
        }

        JSONArray savedFloorInfoResults = JSONArray.parseArray(responseEntity.getBody());


        return savedFloorInfoResults;
    }

    private JSONArray getExistsFloor(IPageData pd, ComponentValidateResult result) {
        String apiUrl = "";
        ResponseEntity<String> responseEntity = null;
        apiUrl = ServiceConstant.SERVICE_API_URL + "/api/floor.queryFloors?page=1&row=100&communityId=" + result.getCommunityId();

        responseEntity = this.callCenterService(restTemplate, pd, "", apiUrl, HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) { //跳过 保存单元信息
            return null;
        }

        JSONObject savedFloorInfoResult = JSONObject.parseObject(responseEntity.getBody());

        if (!savedFloorInfoResult.containsKey("apiFloorDataVoList")) {
            return null;
        }

        return savedFloorInfoResult.getJSONArray("apiFloorDataVoList");

    }


    /**
     * 获取 房屋信息
     *
     * @param componentValidateResult
     * @param workbook
     */
    private void getRooms(IPageData pd, ComponentValidateResult componentValidateResult, Workbook workbook) {
        Sheet sheet = workbook.createSheet("房屋信息");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("楼栋编号");
        row.createCell(1).setCellValue("单元编号");
        row.createCell(2).setCellValue("房屋编码");
        row.createCell(3).setCellValue("费用名称");
        row.createCell(4).setCellValue("开始时间");
        row.createCell(5).setCellValue("结束时间");
        row.createCell(6).setCellValue("收费金额");

        //查询楼栋信息
        JSONArray rooms = this.getExistsRoom(pd, componentValidateResult);
        if (rooms == null) {
            return;
        }
        for (int roomIndex = 0; roomIndex < rooms.size(); roomIndex++) {
            row = sheet.createRow(roomIndex + 1);
            row.createCell(0).setCellValue(rooms.getJSONObject(roomIndex).getString("floorNum"));
            row.createCell(1).setCellValue(rooms.getJSONObject(roomIndex).getString("unitNum"));
            row.createCell(2).setCellValue(rooms.getJSONObject(roomIndex).getString("roomNum"));
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue("");
            row.createCell(6).setCellValue("");
        }
    }


    /**
     * 获取小区
     *
     * @param workbook
     */
    private void getFloors(IPageData pd, ComponentValidateResult componentValidateResult, Workbook workbook) {
        Sheet sheet = workbook.createSheet("楼栋单元");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("楼栋号");
        row.createCell(1).setCellValue("单元编号");
        row.createCell(2).setCellValue("总层数");
        row.createCell(3).setCellValue("是否有电梯");

        //查询楼栋信息
        JSONArray floors = this.getExistsFloor(pd, componentValidateResult);

        if (floors == null) {
            return;
        }
        for (int floorIndex = 0; floorIndex < floors.size(); floorIndex++) {
            JSONArray units = this.getExistsUnit(pd, componentValidateResult, floors.getJSONObject(floorIndex).getString("floorId"));
            for (int unitIndex = 0; unitIndex < units.size(); unitIndex++) {
                row = sheet.createRow(floorIndex + 1);
                row.createCell(0).setCellValue(floors.getJSONObject(floorIndex).getString("floorNum"));
                row.createCell(1).setCellValue(units.getJSONObject(unitIndex).getString("unitNum"));
                row.createCell(2).setCellValue(units.getJSONObject(unitIndex).getString("layerCount"));
                row.createCell(3).setCellValue("1010".equals(units.getJSONObject(unitIndex).getString("lift")) ? "有" : "无");
            }
        }


    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
