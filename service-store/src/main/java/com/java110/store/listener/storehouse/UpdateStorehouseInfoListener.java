package com.java110.store.listener.storehouse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.po.storehouse.StorehousePo;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.ResponseConstant;
import com.java110.utils.constant.StatusConstant;
import com.java110.utils.exception.ListenerExecuteException;
import com.java110.utils.util.Assert;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.entity.center.Business;
import com.java110.store.dao.IStorehouseServiceDao;
import org.slf4j.Logger;
import com.java110.core.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改仓库信息 侦听
 *
 * 处理节点
 * 1、businessStorehouse:{} 仓库基本信息节点
 * 2、businessStorehouseAttr:[{}] 仓库属性信息节点
 * 3、businessStorehousePhoto:[{}] 仓库照片信息节点
 * 4、businessStorehouseCerdentials:[{}] 仓库证件信息节点
 * 协议地址 ：https://github.com/java110/MicroCommunity/wiki/%E4%BF%AE%E6%94%B9%E5%95%86%E6%88%B7%E4%BF%A1%E6%81%AF-%E5%8D%8F%E8%AE%AE
 * Created by wuxw on 2018/5/18.
 */
@Java110Listener("updateStorehouseInfoListener")
@Transactional
public class UpdateStorehouseInfoListener extends AbstractStorehouseBusinessServiceDataFlowListener {

    private static Logger logger = LoggerFactory.getLogger(UpdateStorehouseInfoListener.class);
    @Autowired
    private IStorehouseServiceDao storehouseServiceDaoImpl;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String getBusinessTypeCd() {
        return BusinessTypeConstant.BUSINESS_TYPE_UPDATE_STOREHOUSE;
    }

    /**
     * business过程
     * @param dataFlowContext 上下文对象
     * @param business 业务对象
     */
    @Override
    protected void doSaveBusiness(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Assert.notEmpty(data,"没有datas 节点，或没有子节点需要处理");


            //处理 businessStorehouse 节点
            if(data.containsKey(StorehousePo.class.getSimpleName())){
                Object _obj = data.get(StorehousePo.class.getSimpleName());
                JSONArray businessStorehouses = null;
                if(_obj instanceof JSONObject){
                    businessStorehouses = new JSONArray();
                    businessStorehouses.add(_obj);
                }else {
                    businessStorehouses = (JSONArray)_obj;
                }
                //JSONObject businessStorehouse = data.getJSONObject(StorehousePo.class.getSimpleName());
                for (int _storehouseIndex = 0; _storehouseIndex < businessStorehouses.size();_storehouseIndex++) {
                    JSONObject businessStorehouse = businessStorehouses.getJSONObject(_storehouseIndex);
                    doBusinessStorehouse(business, businessStorehouse);
                    if(_obj instanceof JSONObject) {
                        dataFlowContext.addParamOut("shId", businessStorehouse.getString("shId"));
                    }
                }
            }
    }


    /**
     * business to instance 过程
     * @param dataFlowContext 数据对象
     * @param business 当前业务对象
     */
    @Override
    protected void doBusinessToInstance(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Map info = new HashMap();
        info.put("bId",business.getbId());
        info.put("operate",StatusConstant.OPERATE_ADD);

        //仓库信息
        List<Map> businessStorehouseInfos = storehouseServiceDaoImpl.getBusinessStorehouseInfo(info);
        if( businessStorehouseInfos != null && businessStorehouseInfos.size() >0) {
            for (int _storehouseIndex = 0; _storehouseIndex < businessStorehouseInfos.size();_storehouseIndex++) {
                Map businessStorehouseInfo = businessStorehouseInfos.get(_storehouseIndex);
                flushBusinessStorehouseInfo(businessStorehouseInfo,StatusConstant.STATUS_CD_VALID);
                storehouseServiceDaoImpl.updateStorehouseInfoInstance(businessStorehouseInfo);
                if(businessStorehouseInfo.size() == 1) {
                    dataFlowContext.addParamOut("shId", businessStorehouseInfo.get("sh_id"));
                }
            }
        }

    }

    /**
     * 撤单
     * @param dataFlowContext 数据对象
     * @param business 当前业务对象
     */
    @Override
    protected void doRecover(DataFlowContext dataFlowContext, Business business) {

        String bId = business.getbId();
        //Assert.hasLength(bId,"请求报文中没有包含 bId");
        Map info = new HashMap();
        info.put("bId",bId);
        info.put("statusCd",StatusConstant.STATUS_CD_VALID);
        Map delInfo = new HashMap();
        delInfo.put("bId",business.getbId());
        delInfo.put("operate",StatusConstant.OPERATE_DEL);
        //仓库信息
        List<Map> storehouseInfo = storehouseServiceDaoImpl.getStorehouseInfo(info);
        if(storehouseInfo != null && storehouseInfo.size() > 0){

            //仓库信息
            List<Map> businessStorehouseInfos = storehouseServiceDaoImpl.getBusinessStorehouseInfo(delInfo);
            //除非程序出错了，这里不会为空
            if(businessStorehouseInfos == null || businessStorehouseInfos.size() == 0){
                throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_INNER_ERROR,"撤单失败（storehouse），程序内部异常,请检查！ "+delInfo);
            }
            for (int _storehouseIndex = 0; _storehouseIndex < businessStorehouseInfos.size();_storehouseIndex++) {
                Map businessStorehouseInfo = businessStorehouseInfos.get(_storehouseIndex);
                flushBusinessStorehouseInfo(businessStorehouseInfo,StatusConstant.STATUS_CD_VALID);
                storehouseServiceDaoImpl.updateStorehouseInfoInstance(businessStorehouseInfo);
            }
        }

    }



    /**
     * 处理 businessStorehouse 节点
     * @param business 总的数据节点
     * @param businessStorehouse 仓库节点
     */
    private void doBusinessStorehouse(Business business,JSONObject businessStorehouse){

        Assert.jsonObjectHaveKey(businessStorehouse,"shId","businessStorehouse 节点下没有包含 shId 节点");

        if(businessStorehouse.getString("shId").startsWith("-")){
            throw new ListenerExecuteException(ResponseConstant.RESULT_PARAM_ERROR,"shId 错误，不能自动生成（必须已经存在的shId）"+businessStorehouse);
        }
        //自动保存DEL
        autoSaveDelBusinessStorehouse(business,businessStorehouse);

        businessStorehouse.put("bId",business.getbId());
        businessStorehouse.put("operate", StatusConstant.OPERATE_ADD);
        //保存仓库信息
        storehouseServiceDaoImpl.saveBusinessStorehouseInfo(businessStorehouse);

    }



    @Override
    public IStorehouseServiceDao getStorehouseServiceDaoImpl() {
        return storehouseServiceDaoImpl;
    }

    public void setStorehouseServiceDaoImpl(IStorehouseServiceDao storehouseServiceDaoImpl) {
        this.storehouseServiceDaoImpl = storehouseServiceDaoImpl;
    }



}
