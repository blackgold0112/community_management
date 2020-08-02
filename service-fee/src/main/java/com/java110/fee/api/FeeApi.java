package com.java110.fee.api;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.base.controller.BaseController;
import com.java110.core.context.BusinessServiceDataFlow;
import com.java110.core.factory.DataTransactionFactory;
import com.java110.dto.fee.FeeAttrDto;
import com.java110.fee.bmo.IQueryFeeByAttr;
import com.java110.fee.bmo.IQueryParkspaceFee;
import com.java110.fee.smo.IFeeServiceSMO;
import com.java110.utils.constant.ResponseConstant;
import com.java110.utils.exception.InitConfigDataException;
import com.java110.utils.exception.InitDataFlowContextException;
import com.java110.utils.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 * Created by wuxw on 2018/5/14.
 */
@RestController
@RequestMapping(value = "/feeApi")
public class FeeApi extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(FeeApi.class);

    @Autowired
    IFeeServiceSMO feeServiceSMOImpl;

    @Autowired
    private IQueryFeeByAttr queryFeeByAttrImpl;

    @Autowired
    private IQueryParkspaceFee queryParkspaceFeeImpl;

    @RequestMapping(path = "/service", method = RequestMethod.GET)
    public String serviceGet(HttpServletRequest request) {
        return DataTransactionFactory.createBusinessResponseJson(ResponseConstant.RESULT_CODE_ERROR, "不支持Get方法请求").toJSONString();
    }

    /**
     * 用户服务统一处理接口
     *
     * @param orderInfo
     * @param request
     * @return
     */
    @RequestMapping(path = "/service", method = RequestMethod.POST)
    public String servicePost(@RequestBody String orderInfo, HttpServletRequest request) {
        BusinessServiceDataFlow businessServiceDataFlow = null;
        JSONObject responseJson = null;
        try {
            Map<String, String> headers = new HashMap<String, String>();
            getRequestInfo(request, headers);
            //预校验
            preValiateOrderInfo(orderInfo);
            businessServiceDataFlow = this.writeDataToDataFlowContext(orderInfo, headers);
            responseJson = feeServiceSMOImpl.service(businessServiceDataFlow);
        } catch (InitDataFlowContextException e) {
            logger.error("请求报文错误,初始化 BusinessServiceDataFlow失败" + orderInfo, e);
            responseJson = DataTransactionFactory.createNoBusinessTypeBusinessResponseJson(orderInfo, ResponseConstant.RESULT_PARAM_ERROR, e.getMessage(), null);
        } catch (InitConfigDataException e) {
            logger.error("请求报文错误,加载配置信息失败" + orderInfo, e);
            responseJson = DataTransactionFactory.createNoBusinessTypeBusinessResponseJson(orderInfo, ResponseConstant.RESULT_PARAM_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            logger.error("请求订单异常", e);
            responseJson = DataTransactionFactory.createBusinessResponseJson(businessServiceDataFlow, ResponseConstant.RESULT_CODE_ERROR, e.getMessage() + e,
                    null);
        } finally {
            return responseJson.toJSONString();
        }
    }

    /**
     * 这里预校验，请求报文中不能有 dataFlowId
     *
     * @param orderInfo
     */
    private void preValiateOrderInfo(String orderInfo) {
       /* if(JSONObject.parseObject(orderInfo).getJSONObject("orders").containsKey("dataFlowId")){
            throw new BusinessException(ResponseConstant.RESULT_CODE_ERROR,"报文中不能存在dataFlowId节点");
        }*/
    }

    /**
     * 获取请求信息
     *
     * @param request
     * @param headers
     * @throws RuntimeException
     */
    private void getRequestInfo(HttpServletRequest request, Map headers) throws Exception {
        try {
            super.initHeadParam(request, headers);
            super.initUrlParam(request, headers);
        } catch (Exception e) {
            logger.error("加载头信息失败", e);
            throw new InitConfigDataException(ResponseConstant.RESULT_PARAM_ERROR, "加载头信息失败");
        }
    }

    public IFeeServiceSMO getFeeServiceSMOImpl() {
        return feeServiceSMOImpl;
    }

    public void setFeeServiceSMOImpl(IFeeServiceSMO feeServiceSMOImpl) {
        this.feeServiceSMOImpl = feeServiceSMOImpl;
    }


    /**
     * 停车费查询
     *
     * @param reqJson
     * @return
     */
    @RequestMapping(value = "/parkSpaceFee", method = RequestMethod.POST)
    public ResponseEntity<String> parkSpaceFee(@RequestBody JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "code", "未包含小区编码");
        return queryParkspaceFeeImpl.query(reqJson);
    }

    /**
     * 根据属性查询费用
     *
     * @param communityId
     * @return
     * @path /app/feeApi/listFeeByAttr
     */
    @RequestMapping(value = "/listFeeByAttr", method = RequestMethod.GET)
    public ResponseEntity<String> listFeeByAttr(@RequestParam(value = "communityId") String communityId,
                                                @RequestParam(value = "feeId", required = false) String feeId,
                                                @RequestParam(value = "specCd") String specCd,
                                                @RequestParam(value = "value") String value,
                                                @RequestParam(value = "row") int row,
                                                @RequestParam(value = "page") int page) {

        FeeAttrDto feeAttrDto = new FeeAttrDto();
        feeAttrDto.setCommunityId(communityId);
        feeAttrDto.setSpecCd(specCd);
        feeAttrDto.setValue(value);
        feeAttrDto.setFeeId(feeId);
        feeAttrDto.setRow(row);
        feeAttrDto.setPage(page);
        return queryFeeByAttrImpl.query(feeAttrDto);

    }
}
