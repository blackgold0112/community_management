package com.java110.common.listener.msg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.common.dao.IMsgServiceDao;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.entity.center.Business;
import com.java110.po.message.MsgPo;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.ResponseConstant;
import com.java110.utils.constant.StatusConstant;
import com.java110.utils.exception.ListenerExecuteException;
import com.java110.utils.util.Assert;
import org.slf4j.Logger;
import com.java110.core.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改消息信息 侦听
 * <p>
 * 处理节点
 * 1、businessMsg:{} 消息基本信息节点
 * 2、businessMsgAttr:[{}] 消息属性信息节点
 * 3、businessMsgPhoto:[{}] 消息照片信息节点
 * 4、businessMsgCerdentials:[{}] 消息证件信息节点
 * 协议地址 ：https://github.com/java110/MicroCommunity/wiki/%E4%BF%AE%E6%94%B9%E5%95%86%E6%88%B7%E4%BF%A1%E6%81%AF-%E5%8D%8F%E8%AE%AE
 * Created by wuxw on 2018/5/18.
 */
@Java110Listener("updateMsgInfoListener")
@Transactional
public class UpdateMsgInfoListener extends AbstractMsgBusinessServiceDataFlowListener {

    private static Logger logger = LoggerFactory.getLogger(UpdateMsgInfoListener.class);
    @Autowired
    private IMsgServiceDao msgServiceDaoImpl;

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String getBusinessTypeCd() {
        return BusinessTypeConstant.BUSINESS_TYPE_UPDATE_MSG;
    }

    /**
     * business过程
     *
     * @param dataFlowContext 上下文对象
     * @param business        业务对象
     */
    @Override
    protected void doSaveBusiness(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Assert.notEmpty(data, "没有datas 节点，或没有子节点需要处理");


        //处理 businessMsg 节点
        if (data.containsKey(MsgPo.class.getSimpleName())) {
            Object _obj = data.get(MsgPo.class.getSimpleName());
            JSONArray businessMsgs = null;
            if (_obj instanceof JSONObject) {
                businessMsgs = new JSONArray();
                businessMsgs.add(_obj);
            } else {
                businessMsgs = (JSONArray) _obj;
            }
            //JSONObject businessMsg = data.getJSONObject("businessMsg");
            for (int _msgIndex = 0; _msgIndex < businessMsgs.size(); _msgIndex++) {
                JSONObject businessMsg = businessMsgs.getJSONObject(_msgIndex);
                doBusinessMsg(business, businessMsg);
                if (_obj instanceof JSONObject) {
                    dataFlowContext.addParamOut("msgId", businessMsg.getString("msgId"));
                }
            }

        }
    }


    /**
     * business to instance 过程
     *
     * @param dataFlowContext 数据对象
     * @param business        当前业务对象
     */
    @Override
    protected void doBusinessToInstance(DataFlowContext dataFlowContext, Business business) {

        JSONObject data = business.getDatas();

        Map info = new HashMap();
        info.put("bId", business.getbId());
        info.put("operate", StatusConstant.OPERATE_ADD);

        //消息信息
        List<Map> businessMsgInfos = msgServiceDaoImpl.getBusinessMsgInfo(info);
        if (businessMsgInfos != null && businessMsgInfos.size() > 0) {
            for (int _msgIndex = 0; _msgIndex < businessMsgInfos.size(); _msgIndex++) {
                Map businessMsgInfo = businessMsgInfos.get(_msgIndex);
                flushBusinessMsgInfo(businessMsgInfo, StatusConstant.STATUS_CD_VALID);
                msgServiceDaoImpl.updateMsgInfoInstance(businessMsgInfo);
                if (businessMsgInfo.size() == 1) {
                    dataFlowContext.addParamOut("msgId", businessMsgInfo.get("msg_id"));
                }
            }
        }

    }

    /**
     * 撤单
     *
     * @param dataFlowContext 数据对象
     * @param business        当前业务对象
     */
    @Override
    protected void doRecover(DataFlowContext dataFlowContext, Business business) {

        String bId = business.getbId();
        //Assert.hasLength(bId,"请求报文中没有包含 bId");
        Map info = new HashMap();
        info.put("bId", bId);
        info.put("statusCd", StatusConstant.STATUS_CD_VALID);
        Map delInfo = new HashMap();
        delInfo.put("bId", business.getbId());
        delInfo.put("operate", StatusConstant.OPERATE_DEL);
        //消息信息
        List<Map> msgInfo = msgServiceDaoImpl.getMsgInfo(info);
        if (msgInfo != null && msgInfo.size() > 0) {

            //消息信息
            List<Map> businessMsgInfos = msgServiceDaoImpl.getBusinessMsgInfo(delInfo);
            //除非程序出错了，这里不会为空
            if (businessMsgInfos == null || businessMsgInfos.size() == 0) {
                throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_INNER_ERROR, "撤单失败（msg），程序内部异常,请检查！ " + delInfo);
            }
            for (int _msgIndex = 0; _msgIndex < businessMsgInfos.size(); _msgIndex++) {
                Map businessMsgInfo = businessMsgInfos.get(_msgIndex);
                flushBusinessMsgInfo(businessMsgInfo, StatusConstant.STATUS_CD_VALID);
                msgServiceDaoImpl.updateMsgInfoInstance(businessMsgInfo);
            }
        }

    }


    /**
     * 处理 businessMsg 节点
     *
     * @param business    总的数据节点
     * @param businessMsg 消息节点
     */
    private void doBusinessMsg(Business business, JSONObject businessMsg) {

        Assert.jsonObjectHaveKey(businessMsg, "msgId", "businessMsg 节点下没有包含 msgId 节点");

        if (businessMsg.getString("msgId").startsWith("-")) {
            throw new ListenerExecuteException(ResponseConstant.RESULT_PARAM_ERROR, "msgId 错误，不能自动生成（必须已经存在的msgId）" + businessMsg);
        }
        //自动保存DEL
        autoSaveDelBusinessMsg(business, businessMsg);

        businessMsg.put("bId", business.getbId());
        businessMsg.put("operate", StatusConstant.OPERATE_ADD);
        //保存消息信息
        msgServiceDaoImpl.saveBusinessMsgInfo(businessMsg);

    }


    public IMsgServiceDao getMsgServiceDaoImpl() {
        return msgServiceDaoImpl;
    }

    public void setMsgServiceDaoImpl(IMsgServiceDao msgServiceDaoImpl) {
        this.msgServiceDaoImpl = msgServiceDaoImpl;
    }


}
