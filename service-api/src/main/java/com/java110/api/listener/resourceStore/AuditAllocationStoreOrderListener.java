package com.java110.api.listener.resourceStore;

import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.auditApplyOrder.IApplyOrderBMO;
import com.java110.api.listener.AbstractServiceApiPlusListener;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.event.service.api.ServiceDataFlowEvent;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.dto.allocationStorehouse.AllocationStorehouseDto;
import com.java110.dto.allocationStorehouseApply.AllocationStorehouseApplyDto;
import com.java110.dto.resourceStore.ResourceStoreDto;
import com.java110.intf.common.IAllocationStorehouseUserInnerServiceSMO;
import com.java110.intf.store.IAllocationStorehouseApplyInnerServiceSMO;
import com.java110.intf.store.IAllocationStorehouseInnerServiceSMO;
import com.java110.intf.store.IResourceStoreInnerServiceSMO;
import com.java110.po.allocationStorehouse.AllocationStorehousePo;
import com.java110.po.allocationStorehouseApply.AllocationStorehouseApplyPo;
import com.java110.po.purchase.ResourceStorePo;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.ServiceCodePurchaseApplyConstant;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


/**
 * 调拨单审核
 */
@Java110Listener("auditAllocationStoreOrderListener")
public class AuditAllocationStoreOrderListener extends AbstractServiceApiPlusListener {

    @Autowired
    private IApplyOrderBMO iApplyOrderBMOImpl;

    @Autowired
    private IAllocationStorehouseUserInnerServiceSMO allocationStorehouseUserInnerServiceSMOImpl;


    @Autowired
    private IAllocationStorehouseInnerServiceSMO allocationStorehouseInnerServiceSMOImpl;


    @Autowired
    private IAllocationStorehouseApplyInnerServiceSMO allocationStorehouseApplyInnerServiceSMOImpl;

    @Autowired
    private IResourceStoreInnerServiceSMO resourceStoreInnerServiceSMOImpl;

    @Override
    public String getServiceCode() {
        return ServiceCodePurchaseApplyConstant.AUDIT_ALLOCATION_STORE;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }


    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }


    @Override
    protected void validate(ServiceDataFlowEvent event, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "asId", "订单号不能为空");
        Assert.hasKeyAndValue(reqJson, "taskId", "必填，请填写任务ID");
        Assert.hasKeyAndValue(reqJson, "state", "必填，请填写审核状态");
        Assert.hasKeyAndValue(reqJson, "remark", "必填，请填写批注");
    }

    @Override
    protected void doSoService(ServiceDataFlowEvent event, DataFlowContext context, JSONObject reqJson) {
        AllocationStorehouseApplyDto allocationStorehouseDto = new AllocationStorehouseApplyDto();
        allocationStorehouseDto.setTaskId(reqJson.getString("taskId"));
        allocationStorehouseDto.setApplyId(reqJson.getString("applyId"));
        allocationStorehouseDto.setStoreId(reqJson.getString("storeId"));
        allocationStorehouseDto.setAuditCode(reqJson.getString("state"));
        allocationStorehouseDto.setAuditMessage(reqJson.getString("remark"));
        allocationStorehouseDto.setCurrentUserId(reqJson.getString("userId"));

        AllocationStorehouseApplyDto tmpAllocationStorehouseDto = new AllocationStorehouseApplyDto();
        tmpAllocationStorehouseDto.setApplyId(reqJson.getString("applyId"));
        tmpAllocationStorehouseDto.setStoreId(reqJson.getString("storeId"));
        List<AllocationStorehouseApplyDto> allocationStorehouseDtos = allocationStorehouseApplyInnerServiceSMOImpl.queryAllocationStorehouseApplys(tmpAllocationStorehouseDto);
        Assert.listOnlyOne(allocationStorehouseDtos, "调拨申请单存在多条");
        allocationStorehouseDto.setStartUserId(allocationStorehouseDtos.get(0).getStartUserId());

        boolean isLastTask = allocationStorehouseUserInnerServiceSMOImpl.completeTask(allocationStorehouseDto);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>("成功", HttpStatus.OK);
        if (isLastTask) {
            updateAllocationStorehouse(reqJson, context);
        }

    }


    /**
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    private void updateAllocationStorehouse(JSONObject paramInJson, DataFlowContext dataFlowContext) {

        AllocationStorehouseApplyDto tmpAllocationStorehouseApplyDto = new AllocationStorehouseApplyDto();
        tmpAllocationStorehouseApplyDto.setApplyId(paramInJson.getString("applyId"));
        tmpAllocationStorehouseApplyDto.setStoreId(paramInJson.getString("storeId"));
        List<AllocationStorehouseApplyDto> allocationStorehouseApplyDtos = allocationStorehouseApplyInnerServiceSMOImpl.queryAllocationStorehouseApplys(tmpAllocationStorehouseApplyDto);

        Assert.listOnlyOne(allocationStorehouseApplyDtos, "存在多条记录，或不存在数据" + tmpAllocationStorehouseApplyDto.getApplyId());

        JSONObject businessComplaint = new JSONObject();
        businessComplaint.putAll(BeanConvertUtil.beanCovertMap(allocationStorehouseApplyDtos.get(0)));
        businessComplaint.put("state", AllocationStorehouseDto.STATE_SUCCESS);
        AllocationStorehouseApplyPo allocationStorehouseApplyPo = BeanConvertUtil.covertBean(businessComplaint, AllocationStorehouseApplyPo.class);

        super.update(dataFlowContext, allocationStorehouseApplyPo, BusinessTypeConstant.BUSINESS_TYPE_UPDATE_ALLOCATION_STOREHOUSE_APPLY);

        AllocationStorehouseDto tmpAllocationStorehouseDto = new AllocationStorehouseDto();
        tmpAllocationStorehouseDto.setApplyId(paramInJson.getString("applyId"));
        tmpAllocationStorehouseDto.setStoreId(paramInJson.getString("storeId"));
        List<AllocationStorehouseDto> allocationStorehouseDtos = allocationStorehouseInnerServiceSMOImpl.queryAllocationStorehouses(tmpAllocationStorehouseDto);

        for(AllocationStorehouseDto allocationStorehouseDto : allocationStorehouseDtos) {
            //查询是否新仓库有此物品
            ResourceStoreDto resourceStoreDto = new ResourceStoreDto();
            resourceStoreDto.setShId(allocationStorehouseDto.getShIdz());
            resourceStoreDto.setResName(allocationStorehouseDto.getResName());
            List<ResourceStoreDto> resourceStoreDtos = resourceStoreInnerServiceSMOImpl.queryResourceStores(resourceStoreDto);
            ResourceStorePo resourceStorePo = new ResourceStorePo();

            if (resourceStoreDtos != null && resourceStoreDtos.size() > 0) {
                resourceStorePo.setShId(allocationStorehouseDto.getShIdz());
                resourceStorePo.setResId(allocationStorehouseDto.getResId());
                resourceStorePo.setStock((Integer.parseInt(allocationStorehouseDto.getStock()) + Integer.parseInt(allocationStorehouseDto.getStock())) + "");
                super.update(dataFlowContext, resourceStorePo, BusinessTypeConstant.BUSINESS_TYPE_UPDATE_RESOURCE_STORE);
            } else {
                //查询是否新仓库有此物品
                resourceStoreDto = new ResourceStoreDto();
                resourceStoreDto.setShId(allocationStorehouseDto.getShIda());
                resourceStoreDto.setResId(allocationStorehouseDto.getResId());
                resourceStoreDtos = resourceStoreInnerServiceSMOImpl.queryResourceStores(resourceStoreDto);
                Assert.listOnlyOne(resourceStoreDtos, "原仓库记录不存在");
                resourceStorePo = BeanConvertUtil.covertBean(allocationStorehouseDto, resourceStorePo);

                resourceStorePo.setResId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_resId));
                resourceStorePo.setStock(allocationStorehouseDto.getStock());
                resourceStorePo.setShId(allocationStorehouseDto.getShIdz());

                super.insert(dataFlowContext, resourceStorePo, BusinessTypeConstant.BUSINESS_TYPE_SAVE_RESOURCE_STORE);
            }
        }
    }


}
