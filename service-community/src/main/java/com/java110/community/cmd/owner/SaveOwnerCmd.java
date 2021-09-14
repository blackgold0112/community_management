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
package com.java110.community.cmd.owner;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.event.cmd.AbstractServiceCmdListener;
import com.java110.core.event.cmd.CmdEvent;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.intf.community.IOwnerV1InnerServiceSMO;
import com.java110.po.owner.OwnerPo;
import com.java110.utils.exception.CmdException;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类表述：保存
 * 服务编码：owner.saveOwner
 * 请求路劲：/app/owner.SaveOwner
 * add by 吴学文 at 2021-09-14 18:56:16 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "owner.SaveOwner")
public class SaveOwnerCmd extends AbstractServiceCmdListener {

    private static Logger logger = LoggerFactory.getLogger(SaveOwnerCmd.class);

    public static final String CODE_PREFIX_ID = "10";

    @Autowired
    private IOwnerV1InnerServiceSMO ownerV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "memberId", "请求报文中未包含memberId");
Assert.hasKeyAndValue(reqJson, "ownerId", "请求报文中未包含ownerId");
Assert.hasKeyAndValue(reqJson, "name", "请求报文中未包含name");
Assert.hasKeyAndValue(reqJson, "sex", "请求报文中未包含sex");
Assert.hasKeyAndValue(reqJson, "age", "请求报文中未包含age");
Assert.hasKeyAndValue(reqJson, "link", "请求报文中未包含link");
Assert.hasKeyAndValue(reqJson, "userId", "请求报文中未包含userId");
Assert.hasKeyAndValue(reqJson, "statusCd", "请求报文中未包含statusCd");
Assert.hasKeyAndValue(reqJson, "ownerTypeCd", "请求报文中未包含ownerTypeCd");
Assert.hasKeyAndValue(reqJson, "idCard", "请求报文中未包含idCard");
Assert.hasKeyAndValue(reqJson, "state", "请求报文中未包含state");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

       OwnerPo ownerPo = BeanConvertUtil.covertBean(reqJson, OwnerPo.class);
        ownerPo.setMemberId(GenerateCodeFactory.getGeneratorId(CODE_PREFIX_ID));
        int flag = ownerV1InnerServiceSMOImpl.saveOwner(ownerPo);

        if (flag < 1) {
            throw new CmdException("保存数据失败");
        }

        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }
}
