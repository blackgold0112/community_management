package com.java110.order.api;

import com.java110.core.base.controller.BaseController;
import com.java110.event.center.DataFlowEventPublishing;
import com.java110.order.smo.IPrivilegeSMO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限API 处理类
 * Created by Administrator on 2019/4/1.
 */
@RestController
@RequestMapping(path = "/privilegeApi")
public class PrivilegeApi extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(PrivilegeApi.class);


    @Autowired
    private IPrivilegeSMO privilegeSMOImpl;

    @RequestMapping(path = "/saveUserDefaultPrivilege",method= RequestMethod.POST)
    @ApiOperation(value="添加用户默认权限", notes="test: 返回 200 表示服务受理成功，其他表示失败")
    @ApiImplicitParam(paramType="query", name = "privilegeInfo", value = "权限信息", required = true, dataType = "String")
    public ResponseEntity<String> saveUserDefaultPrivilege(@RequestBody String privilegeInfo, HttpServletRequest request){

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = privilegeSMOImpl.saveUserDefaultPrivilege(privilegeInfo);
        }catch (Exception e){
            logger.error("请求订单异常",e);
            responseEntity =  new ResponseEntity<String>("请求中心服务发生异常，"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            logger.debug("订单服务返回报文为: {}",responseEntity);
            return responseEntity;
        }

    }

    public IPrivilegeSMO getPrivilegeSMOImpl() {
        return privilegeSMOImpl;
    }

    public void setPrivilegeSMOImpl(IPrivilegeSMO privilegeSMOImpl) {
        this.privilegeSMOImpl = privilegeSMOImpl;
    }
}
