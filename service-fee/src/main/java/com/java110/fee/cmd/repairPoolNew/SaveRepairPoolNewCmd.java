/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.fee.cmd.repairPoolNew;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.event.cmd.AbstractServiceCmdListener;
import com.java110.core.event.cmd.CmdEvent;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.intf.fee.IRepairPoolNewV1InnerServiceSMO;
import com.java110.po.owner.RepairPoolPo;
import com.java110.utils.exception.CmdException;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import com.java110.core.log.LoggerFactory;

/**
 * 类表述：保存
 * 服务编码：repairPoolNew.saveRepairPoolNew
 * 请求路劲：/app/repairPoolNew.SaveRepairPoolNew
 * add by 吴学文 at 2021-12-06 22:28:18 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "repairPoolNew.saveRepairPoolNew")
public class SaveRepairPoolNewCmd extends AbstractServiceCmdListener {

    private static Logger logger = LoggerFactory.getLogger(SaveRepairPoolNewCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IRepairPoolNewV1InnerServiceSMO repairPoolNewV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "repairId", "请求报文中未包含repairId");
        Assert.hasKeyAndValue(reqJson, "communityId", "请求报文中未包含communityId");
        Assert.hasKeyAndValue(reqJson, "repairType", "请求报文中未包含repairType");
        Assert.hasKeyAndValue(reqJson, "repairName", "请求报文中未包含repairName");
        Assert.hasKeyAndValue(reqJson, "tel", "请求报文中未包含tel");
        Assert.hasKeyAndValue(reqJson, "context", "请求报文中未包含context");
        Assert.hasKeyAndValue(reqJson, "state", "请求报文中未包含state");
        Assert.hasKeyAndValue(reqJson, "repairObjType", "请求报文中未包含repairObjType");
        Assert.hasKeyAndValue(reqJson, "repairObjId", "请求报文中未包含repairObjId");
        Assert.hasKeyAndValue(reqJson, "repairObjName", "请求报文中未包含repairObjName");
        Assert.hasKeyAndValue(reqJson, "appointmentTime", "请求报文中未包含appointmentTime");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        RepairPoolPo repairPoolNewPo = BeanConvertUtil.covertBean(reqJson, RepairPoolPo.class);
        repairPoolNewPo.setRepairId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = repairPoolNewV1InnerServiceSMOImpl.saveRepairPoolNew(repairPoolNewPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}