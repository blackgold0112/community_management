package com.java110.store.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.dto.allocationStorehouse.AllocationStorehouseDto;
import com.java110.dto.allocationStorehouseApply.AllocationStorehouseApplyDto;
import com.java110.dto.purchaseApply.PurchaseApplyDto;
import com.java110.dto.resourceStore.ResourceStoreDto;
import com.java110.dto.storehouse.StorehouseDto;
import com.java110.dto.user.UserDto;
import com.java110.intf.store.*;
import com.java110.intf.user.IUserV1InnerServiceSMO;
import com.java110.po.purchase.PurchaseApplyDetailPo;
import com.java110.po.purchase.PurchaseApplyPo;
import com.java110.po.purchase.ResourceStorePo;
import com.java110.store.bmo.purchase.IPurchaseApplyBMO;
import com.java110.store.bmo.purchase.IResourceEnterBMO;
import com.java110.utils.cache.MappingCache;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.utils.util.DateUtil;
import com.java110.utils.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping(value = "/purchase")
public class PurchaseApi {

    @Autowired
    private IPurchaseApplyBMO purchaseApplyBMOImpl;

    @Autowired
    private IResourceEnterBMO resourceEnterBMOImpl;

    @Autowired
    private IResourceStoreInnerServiceSMO resourceStoreInnerServiceSMOImpl;

    @Autowired
    private IStorehouseInnerServiceSMO storehouseInnerServiceSMOImpl;

    @Autowired
    private IAllocationStorehouseInnerServiceSMO allocationStorehouseInnerServiceSMOImpl;

    @Autowired
    private IAllocationStorehouseApplyInnerServiceSMO allocationStorehouseApplyInnerServiceSMOImpl;

    @Autowired
    private IPurchaseApplyInnerServiceSMO purchaseApplyInnerServiceSMOImpl;

    @Autowired
    private IUserV1InnerServiceSMO userV1InnerServiceSMOImpl;

    //域
    public static final String DOMAIN_COMMON = "DOMAIN.COMMON";

    //键
    public static final String URGRNT_NUMBER = "URGRNT_NUMBER";

    /**
     * 采购申请
     * <p>
     * {"resourceStores":[{"resId":"852020061636590016","resName":"橡皮擦","resCode":"003","price":"100.00","stock":"0","description":"ada","quantity":"1"},
     * {"resId":"852020061729120031","resName":"文档柜","resCode":"002","price":"33.00","stock":"0","description":"蓝色","quantity":"1"}],
     * "description":"123123","endUserName":"1","endUserTel":"17797173942","file":"","resOrderType":"10000","staffId":"","staffName":""}
     *
     * @param reqJson
     * @return
     */
    @RequestMapping(value = "/purchaseApply", method = RequestMethod.POST)
    public ResponseEntity<String> purchaseApply(@RequestBody JSONObject reqJson,
                                                @RequestHeader(value = "user-id") String userId,
                                                @RequestHeader(value = "store-id") String storeId) {
        Assert.hasKeyAndValue(reqJson, "resourceStores", "必填，请填写申请采购的物资");
        Assert.hasKeyAndValue(reqJson, "description", "必填，请填写采购申请说明");
        Assert.hasKeyAndValue(reqJson, "resOrderType", "必填，请填写申请类型");
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos,"未包含用户");


        String userName  = userDtos.get(0).getName();
        PurchaseApplyPo purchaseApplyPo = new PurchaseApplyPo();
        purchaseApplyPo.setApplyOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_applyOrderId));
        purchaseApplyPo.setDescription(reqJson.getString("description"));
        purchaseApplyPo.setUserId(userId);
        purchaseApplyPo.setUserName(userName);
        purchaseApplyPo.setEndUserName(reqJson.getString("endUserName"));
        purchaseApplyPo.setEndUserTel(reqJson.getString("endUserTel"));
        purchaseApplyPo.setStoreId(storeId);
        purchaseApplyPo.setResOrderType(PurchaseApplyDto.RES_ORDER_TYPE_ENTER);
        purchaseApplyPo.setState(PurchaseApplyDto.STATE_WAIT_DEAL);
        purchaseApplyPo.setCreateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        purchaseApplyPo.setCreateUserId(userId);
        purchaseApplyPo.setCreateUserName(userName);
        purchaseApplyPo.setWarehousingWay(PurchaseApplyDto.WAREHOUSING_TYPE_APPLY);
        purchaseApplyPo.setCommunityId(reqJson.getString("communityId"));
        JSONArray resourceStores = reqJson.getJSONArray("resourceStores");
        List<PurchaseApplyDetailPo> purchaseApplyDetailPos = new ArrayList<>();
        for (int resourceStoreIndex = 0; resourceStoreIndex < resourceStores.size(); resourceStoreIndex++) {
            JSONObject resourceStore = resourceStores.getJSONObject(resourceStoreIndex);
            resourceStore.remove("price");//采购价格默认空
            resourceStore.put("originalStock", resourceStore.getString("stock"));
            PurchaseApplyDetailPo purchaseApplyDetailPo = BeanConvertUtil.covertBean(resourceStore, PurchaseApplyDetailPo.class);
            purchaseApplyDetailPo.setId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_applyOrderId));
            purchaseApplyDetailPos.add(purchaseApplyDetailPo);
        }
        purchaseApplyPo.setPurchaseApplyDetailPos(purchaseApplyDetailPos);
        return purchaseApplyBMOImpl.apply(purchaseApplyPo,reqJson);
    }

    //调整为cmd 模式
//    @RequestMapping(value = "/resourceEnter", method = RequestMethod.POST)
//    public ResponseEntity<String> resourceEnter(@RequestBody JSONObject reqJson) {
//        Assert.hasKeyAndValue(reqJson, "applyOrderId", "订单ID为空");
//        PurchaseApplyDto purchaseApplyDto = new PurchaseApplyDto();
//        purchaseApplyDto.setApplyOrderId(reqJson.getString("applyOrderId"));
//        purchaseApplyDto.setStatusCd("0");
//        List<PurchaseApplyDto> purchaseApplyDtoList = purchaseApplyInnerServiceSMOImpl.queryPurchaseApplys(purchaseApplyDto);
//        if(purchaseApplyDtoList!=null && PurchaseApplyDto.STATE_AUDITED.equals(purchaseApplyDtoList.get(0).getState())){
//            throw new IllegalArgumentException("该订单已经处理，请刷新确认订单状态！");
//        }
//        JSONArray purchaseApplyDetails = reqJson.getJSONArray("purchaseApplyDetailVo");
//        List<PurchaseApplyDetailPo> purchaseApplyDetailPos = new ArrayList<>();
//        for (int detailIndex = 0; detailIndex < purchaseApplyDetails.size(); detailIndex++) {
//            JSONObject purchaseApplyDetail = purchaseApplyDetails.getJSONObject(detailIndex);
//            Assert.hasKeyAndValue(purchaseApplyDetail, "purchaseQuantity", "采购数量未填写");
//            Assert.hasKeyAndValue(purchaseApplyDetail, "price", "采购单价未填写");
//            Assert.hasKeyAndValue(purchaseApplyDetail, "id", "明细ID为空");
//            PurchaseApplyDetailPo purchaseApplyDetailPo = BeanConvertUtil.covertBean(purchaseApplyDetail, PurchaseApplyDetailPo.class);
//            purchaseApplyDetailPos.add(purchaseApplyDetailPo);
//        }
//        PurchaseApplyPo purchaseApplyPo = new PurchaseApplyPo();
//        purchaseApplyPo.setApplyOrderId(reqJson.getString("applyOrderId"));
//        purchaseApplyPo.setPurchaseApplyDetailPos(purchaseApplyDetailPos);
//        return resourceEnterBMOImpl.enter(purchaseApplyPo);
//    }

    /**
     * 直接入库操作
     * <p>
     * {"resourceStores":[{"resId":"852020061636590016","resName":"橡皮擦","resCode":"003","price":"100.00","stock":"0","description":"ada","quantity":"1"},
     * {"resId":"852020061729120031","resName":"文档柜","resCode":"002","price":"33.00","stock":"0","description":"蓝色","quantity":"1"}],
     * "description":"123123","endUserName":"1","endUserTel":"17797173942","file":"","resOrderType":"10000","staffId":"","staffName":""}
     *
     * @param reqJson
     * @return
     */
    @RequestMapping(value = "/purchaseStorage", method = RequestMethod.POST)
    public ResponseEntity<String> purchaseStorage(@RequestBody JSONObject reqJson,
                                                  @RequestHeader(value = "user-id") String userId,

                                                  @RequestHeader(value = "store-id") String storeId) {
        Assert.hasKeyAndValue(reqJson, "resourceStores", "必填，请填写申请采购的物资");
        Assert.hasKeyAndValue(reqJson, "description", "必填，请填写采购申请说明");
        Assert.hasKeyAndValue(reqJson, "resOrderType", "必填，请填写申请类型");
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        List<UserDto> userDtos = userV1InnerServiceSMOImpl.queryUsers(userDto);

        Assert.listOnlyOne(userDtos,"未包含用户");


        String userName  = userDtos.get(0).getName();
        PurchaseApplyPo purchaseApplyPo = new PurchaseApplyPo();
        purchaseApplyPo.setApplyOrderId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_applyOrderId));
        purchaseApplyPo.setDescription(reqJson.getString("description"));
        purchaseApplyPo.setUserId(userId);
        purchaseApplyPo.setUserName(userName);
        purchaseApplyPo.setEndUserName(reqJson.getString("endUserName"));
        purchaseApplyPo.setEndUserTel(reqJson.getString("endUserTel"));
        purchaseApplyPo.setStoreId(storeId);
        purchaseApplyPo.setResOrderType(PurchaseApplyDto.RES_ORDER_TYPE_ENTER);
        purchaseApplyPo.setState(PurchaseApplyDto.STATE_END);
        purchaseApplyPo.setCreateTime(DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        purchaseApplyPo.setDescription("直接采购入库");
        purchaseApplyPo.setCreateUserId(userId);
        purchaseApplyPo.setCreateUserName(userName);
        purchaseApplyPo.setWarehousingWay(PurchaseApplyDto.WAREHOUSING_TYPE_DIRECT);
        purchaseApplyPo.setCommunityId(reqJson.getString("communityId"));
        JSONArray resourceStores = reqJson.getJSONArray("resourceStores");
        List<PurchaseApplyDetailPo> purchaseApplyDetailPos = new ArrayList<>();
        for (int resourceStoreIndex = 0; resourceStoreIndex < resourceStores.size(); resourceStoreIndex++) {
            JSONObject resourceStore = resourceStores.getJSONObject(resourceStoreIndex);
            PurchaseApplyDetailPo purchaseApplyDetailPo = BeanConvertUtil.covertBean(resourceStore, PurchaseApplyDetailPo.class);
            purchaseApplyDetailPo.setId(GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_applyOrderId));
            purchaseApplyDetailPo.setRemark("直接采购入库");
            purchaseApplyDetailPo.setOriginalStock(resourceStore.getString("stock"));
            purchaseApplyDetailPo.setQuantity(purchaseApplyDetailPo.getPurchaseQuantity());
            purchaseApplyDetailPos.add(purchaseApplyDetailPo);
            //增加库存
            ResourceStorePo resourceStorePo = new ResourceStorePo();
            resourceStorePo.setPurchasePrice(purchaseApplyDetailPo.getPrice());
            resourceStorePo.setResId(purchaseApplyDetailPo.getResId());
            resourceStorePo.setStock(purchaseApplyDetailPo.getPurchaseQuantity());
            resourceStorePo.setResOrderType(PurchaseApplyDto.RES_ORDER_TYPE_ENTER);
            //获取采购数量
            BigDecimal purchaseQuantity = new BigDecimal(purchaseApplyDetailPo.getPurchaseQuantity());
            //获取原有最小计量总数
            BigDecimal miniStock = new BigDecimal(resourceStore.getString("miniStock"));
            //获取最小单位数量
            BigDecimal newMiniStock = new BigDecimal(0);
            if (StringUtil.isEmpty(resourceStore.getString("miniUnitStock"))) {
                throw new IllegalArgumentException("最小计量单位数量不能为空！");
            }
            BigDecimal miniUnitStock = new BigDecimal(resourceStore.getString("miniUnitStock"));
            //计算最小计量总数
            if (StringUtil.isEmpty(resourceStore.getString("miniStock"))) {
                newMiniStock = purchaseQuantity.multiply(miniUnitStock);
            } else {
                newMiniStock = (purchaseQuantity.multiply(miniUnitStock)).add(miniStock);
            }
            resourceStorePo.setMiniStock(String.valueOf(newMiniStock));
            resourceStoreInnerServiceSMOImpl.updateResourceStore(resourceStorePo);
        }
        purchaseApplyPo.setPurchaseApplyDetailPos(purchaseApplyDetailPos);
        return purchaseApplyBMOImpl.apply(purchaseApplyPo,reqJson);
    }
}
