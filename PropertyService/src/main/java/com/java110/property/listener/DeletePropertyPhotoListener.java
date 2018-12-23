package com.java110.property.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.property.dao.IPropertyServiceDao;
import com.java110.common.constant.ResponseConstant;
import com.java110.common.constant.ServiceCodeConstant;
import com.java110.common.constant.StatusConstant;
import com.java110.common.exception.ListenerExecuteException;
import com.java110.common.util.Assert;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.entity.center.Business;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 删除物业信息 侦听
 *
 * 处理节点
 * 3、businessPropertyPhoto:[{}] 物业照片信息节点
 * Created by wuxw on 2018/5/18.
 */
@Java110Listener("deletePropertyPhotoListener")
@Transactional
public class DeletePropertyPhotoListener extends AbstractPropertyBusinessServiceDataFlowListener {

    private final static Logger logger = LoggerFactory.getLogger(DeletePropertyPhotoListener.class);
    @Autowired
    IPropertyServiceDao propertyServiceDaoImpl;

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_DELETE_PROPERTY_PHOTO;
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
        if(data.containsKey("businessPropertyPhoto")){
            JSONArray businessPropertyPhotos = data.getJSONArray("businessPropertyPhoto");
            doBusinessPropertyPhoto(business,businessPropertyPhotos);
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

        //物业信息
        Map info = new HashMap();
        info.put("bId",business.getbId());
        info.put("operate",StatusConstant.OPERATE_DEL);
        //物业照片
        List<Map> businessPropertyPhotos = propertyServiceDaoImpl.getBusinessPropertyPhoto(info);
        if(businessPropertyPhotos != null && businessPropertyPhotos.size() >0){
            for(Map businessPropertyPhoto : businessPropertyPhotos) {
                flushBusinessPropertyPhoto(businessPropertyPhoto,StatusConstant.STATUS_CD_INVALID);
                propertyServiceDaoImpl.updatePropertyPhotoInstance(businessPropertyPhoto);
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

        //物业照片
        List<Map> propertyPhotos = propertyServiceDaoImpl.getPropertyPhoto(info);
        if(propertyPhotos != null && propertyPhotos.size()>0){
            List<Map> businessPropertyPhotos = propertyServiceDaoImpl.getBusinessPropertyPhoto(delInfo);
            //除非程序出错了，这里不会为空
            if(businessPropertyPhotos == null || businessPropertyPhotos.size() ==0 ){
                throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_INNER_ERROR,"撤单失败(property_photo)，程序内部异常,请检查！ "+delInfo);
            }
            for(Map businessPropertyPhoto : businessPropertyPhotos) {
                flushBusinessPropertyPhoto(businessPropertyPhoto,StatusConstant.STATUS_CD_VALID);
                propertyServiceDaoImpl.updatePropertyPhotoInstance(businessPropertyPhoto);
            }
        }

    }

    /**
     * 保存物业照片
     * @param business 业务对象
     * @param businessPropertyPhotos 物业照片
     */
    private void doBusinessPropertyPhoto(Business business, JSONArray businessPropertyPhotos) {

        for(int businessPropertyPhotoIndex = 0 ;businessPropertyPhotoIndex < businessPropertyPhotos.size();businessPropertyPhotoIndex++) {
            JSONObject businessPropertyPhoto = businessPropertyPhotos.getJSONObject(businessPropertyPhotoIndex);
            Assert.jsonObjectHaveKey(businessPropertyPhoto, "propertyPhotoId", "businessPropertyPhoto 节点下没有包含 propertyPhotoId 节点");

            if (businessPropertyPhoto.getString("propertyPhotoId").startsWith("-")) {
                throw new ListenerExecuteException(ResponseConstant.RESULT_PARAM_ERROR,"propertyPhotoId 错误，不能自动生成（必须已经存在的propertyPhotoId）"+businessPropertyPhoto);
            }

            autoSaveDelBusinessPropertyPhoto(business,businessPropertyPhoto);
        }
    }

    public IPropertyServiceDao getPropertyServiceDaoImpl() {
        return propertyServiceDaoImpl;
    }

    public void setPropertyServiceDaoImpl(IPropertyServiceDao propertyServiceDaoImpl) {
        this.propertyServiceDaoImpl = propertyServiceDaoImpl;
    }
}
