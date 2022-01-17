package com.java110.community.listener.unitAttr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.po.unitAttr.UnitAttrPo;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.ResponseConstant;
import com.java110.utils.constant.StatusConstant;
import com.java110.utils.exception.ListenerExecuteException;
import com.java110.utils.util.Assert;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.entity.center.Business;
import com.java110.community.dao.IUnitAttrServiceDao;
import org.slf4j.Logger;
import com.java110.core.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 删除单元属性信息 侦听
 *
 * 处理节点
 * 1、businessUnitAttr:{} 单元属性基本信息节点
 * 2、businessUnitAttrAttr:[{}] 单元属性属性信息节点
 * 3、businessUnitAttrPhoto:[{}] 单元属性照片信息节点
 * 4、businessUnitAttrCerdentials:[{}] 单元属性证件信息节点
 * 协议地址 ：https://github.com/java110/MicroCommunity/wiki/%E5%88%A0%E9%99%A4%E5%95%86%E6%88%B7%E4%BF%A1%E6%81%AF-%E5%8D%8F%E8%AE%AE
 * Created by wuxw on 2018/5/18.
 */
@Java110Listener("deleteUnitAttrInfoListener")
@Transactional
public class DeleteUnitAttrInfoListener extends AbstractUnitAttrBusinessServiceDataFlowListener {

    private final static Logger logger = LoggerFactory.getLogger(DeleteUnitAttrInfoListener.class);
    @Autowired
    IUnitAttrServiceDao unitAttrServiceDaoImpl;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public String getBusinessTypeCd() {
        return BusinessTypeConstant.BUSINESS_TYPE_DELETE_UNIT_ATTR_INFO;
    }

    /**
     * 根据删除信息 查出Instance表中数据 保存至business表 （状态写DEL） 方便撤单时直接更新回去
     * @param dataFlowContext 数据对象
     * @param business 当前业务对象
     */
    @Override
    protected void doSaveBusiness(DataFlowContext dataFlowContext, Business business) {
        JSONObject data = business.getDatas();

        Assert.notEmpty(data,"没有datas 节点，或没有子节点需要处理");

            //处理 businessUnitAttr 节点
            if(data.containsKey(UnitAttrPo.class.getSimpleName())){
                Object _obj = data.get(UnitAttrPo.class.getSimpleName());
                JSONArray businessUnitAttrs = null;
                if(_obj instanceof JSONObject){
                    businessUnitAttrs = new JSONArray();
                    businessUnitAttrs.add(_obj);
                }else {
                    businessUnitAttrs = (JSONArray)_obj;
                }
                //JSONObject businessUnitAttr = data.getJSONObject(UnitAttrPo.class.getSimpleName());
                for (int _unitAttrIndex = 0; _unitAttrIndex < businessUnitAttrs.size();_unitAttrIndex++) {
                    JSONObject businessUnitAttr = businessUnitAttrs.getJSONObject(_unitAttrIndex);
                    doBusinessUnitAttr(business, businessUnitAttr);
                    if(_obj instanceof JSONObject) {
                        dataFlowContext.addParamOut("attrId", businessUnitAttr.getString("attrId"));
                    }
                }

        }


    }

    /**
     * 删除 instance数据
     * @param dataFlowContext 数据对象
     * @param business 当前业务对象
     */
    @Override
    protected void doBusinessToInstance(DataFlowContext dataFlowContext, Business business) {
        String bId = business.getbId();
        //Assert.hasLength(bId,"请求报文中没有包含 bId");

        //单元属性信息
        Map info = new HashMap();
        info.put("bId",business.getbId());
        info.put("operate",StatusConstant.OPERATE_DEL);

        //单元属性信息
        List<Map> businessUnitAttrInfos = unitAttrServiceDaoImpl.getBusinessUnitAttrInfo(info);
        if( businessUnitAttrInfos != null && businessUnitAttrInfos.size() >0) {
            for (int _unitAttrIndex = 0; _unitAttrIndex < businessUnitAttrInfos.size();_unitAttrIndex++) {
                Map businessUnitAttrInfo = businessUnitAttrInfos.get(_unitAttrIndex);
                flushBusinessUnitAttrInfo(businessUnitAttrInfo,StatusConstant.STATUS_CD_INVALID);
                unitAttrServiceDaoImpl.updateUnitAttrInfoInstance(businessUnitAttrInfo);
                dataFlowContext.addParamOut("attrId",businessUnitAttrInfo.get("attr_id"));
            }
        }

    }

    /**
     * 撤单
     * 从business表中查询到DEL的数据 将instance中的数据更新回来
     * @param dataFlowContext 数据对象
     * @param business 当前业务对象
     */
    @Override
    protected void doRecover(DataFlowContext dataFlowContext, Business business) {
        String bId = business.getbId();
        //Assert.hasLength(bId,"请求报文中没有包含 bId");
        Map info = new HashMap();
        info.put("bId",bId);
        info.put("statusCd",StatusConstant.STATUS_CD_INVALID);

        Map delInfo = new HashMap();
        delInfo.put("bId",business.getbId());
        delInfo.put("operate",StatusConstant.OPERATE_DEL);
        //单元属性信息
        List<Map> unitAttrInfo = unitAttrServiceDaoImpl.getUnitAttrInfo(info);
        if(unitAttrInfo != null && unitAttrInfo.size() > 0){

            //单元属性信息
            List<Map> businessUnitAttrInfos = unitAttrServiceDaoImpl.getBusinessUnitAttrInfo(delInfo);
            //除非程序出错了，这里不会为空
            if(businessUnitAttrInfos == null ||  businessUnitAttrInfos.size() == 0){
                throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_INNER_ERROR,"撤单失败（unitAttr），程序内部异常,请检查！ "+delInfo);
            }
            for (int _unitAttrIndex = 0; _unitAttrIndex < businessUnitAttrInfos.size();_unitAttrIndex++) {
                Map businessUnitAttrInfo = businessUnitAttrInfos.get(_unitAttrIndex);
                flushBusinessUnitAttrInfo(businessUnitAttrInfo,StatusConstant.STATUS_CD_VALID);
                unitAttrServiceDaoImpl.updateUnitAttrInfoInstance(businessUnitAttrInfo);
            }
        }
    }



    /**
     * 处理 businessUnitAttr 节点
     * @param business 总的数据节点
     * @param businessUnitAttr 单元属性节点
     */
    private void doBusinessUnitAttr(Business business,JSONObject businessUnitAttr){

        Assert.jsonObjectHaveKey(businessUnitAttr,"attrId","businessUnitAttr 节点下没有包含 attrId 节点");

        if(businessUnitAttr.getString("attrId").startsWith("-")){
            throw new ListenerExecuteException(ResponseConstant.RESULT_PARAM_ERROR,"attrId 错误，不能自动生成（必须已经存在的attrId）"+businessUnitAttr);
        }
        //自动插入DEL
        autoSaveDelBusinessUnitAttr(business,businessUnitAttr);
    }
    @Override
    public IUnitAttrServiceDao getUnitAttrServiceDaoImpl() {
        return unitAttrServiceDaoImpl;
    }

    public void setUnitAttrServiceDaoImpl(IUnitAttrServiceDao unitAttrServiceDaoImpl) {
        this.unitAttrServiceDaoImpl = unitAttrServiceDaoImpl;
    }
}
