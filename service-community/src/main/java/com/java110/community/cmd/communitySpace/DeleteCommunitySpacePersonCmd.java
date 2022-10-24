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
package com.java110.community.cmd.communitySpace;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.annotation.Java110Cmd;
import com.java110.core.annotation.Java110Transactional;
import com.java110.core.context.ICmdDataFlowContext;
import com.java110.core.event.cmd.Cmd;
import com.java110.core.event.cmd.CmdEvent;
import com.java110.dto.communitySpacePerson.CommunitySpacePersonDto;
import com.java110.dto.onlinePay.OnlinePayDto;
import com.java110.intf.acct.IOnlinePayV1InnerServiceSMO;
import com.java110.intf.community.ICommunitySpacePersonV1InnerServiceSMO;
import com.java110.po.communitySpacePerson.CommunitySpacePersonPo;
import com.java110.po.onlinePay.OnlinePayPo;
import com.java110.utils.exception.CmdException;
import com.java110.utils.util.Assert;
import com.java110.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类表述：删除
 * 服务编码：communitySpacePerson.deleteCommunitySpacePerson
 * 请求路劲：/app/communitySpacePerson.DeleteCommunitySpacePerson
 * add by 吴学文 at 2022-09-30 11:36:52 mail: 928255095@qq.com
 * open source address: https://gitee.com/wuxw7/MicroCommunity
 * 官网：http://www.homecommunity.cn
 * 温馨提示：如果您对此文件进行修改 请不要删除原有作者及注释信息，请补充您的 修改的原因以及联系邮箱如下
 * // modify by 张三 at 2021-09-12 第10行在某种场景下存在某种bug 需要修复，注释10至20行 加入 20行至30行
 */
@Java110Cmd(serviceCode = "communitySpace.deleteCommunitySpacePerson")
public class DeleteCommunitySpacePersonCmd extends Cmd {
    private static Logger logger = LoggerFactory.getLogger(DeleteCommunitySpacePersonCmd.class);

    @Autowired
    private ICommunitySpacePersonV1InnerServiceSMO communitySpacePersonV1InnerServiceSMOImpl;

    @Autowired
    private IOnlinePayV1InnerServiceSMO onlinePayV1InnerServiceSMOImpl;

    @Override
    public void validate(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) {
        Assert.hasKeyAndValue(reqJson, "cspId", "cspId不能为空");
        Assert.hasKeyAndValue(reqJson, "communityId", "communityId不能为空");

    }

    @Override
    @Java110Transactional
    public void doCmd(CmdEvent event, ICmdDataFlowContext cmdDataFlowContext, JSONObject reqJson) throws CmdException {

        // 是否线上支付
        CommunitySpacePersonDto communitySpacePersonDto = new CommunitySpacePersonDto();
        communitySpacePersonDto.setCspId(reqJson.getString("cspId"));
        communitySpacePersonDto.setState(CommunitySpacePersonDto.STATE_S);
        List<CommunitySpacePersonDto> communitySpacePersonDtos = communitySpacePersonV1InnerServiceSMOImpl.queryCommunitySpacePersons(communitySpacePersonDto);

        Assert.listOnlyOne(communitySpacePersonDtos, "预约订单不存在");

        returnOnlinePayMoney(communitySpacePersonDtos);

        for (CommunitySpacePersonDto communitySpacePersonDto1 : communitySpacePersonDtos) {
            CommunitySpacePersonPo communitySpacePersonPo = new CommunitySpacePersonPo();
            communitySpacePersonPo.setCspId(communitySpacePersonDto1.getCspId());
            communitySpacePersonPo.setState(CommunitySpacePersonDto.STATE_CL);
            int flag = communitySpacePersonV1InnerServiceSMOImpl.updateCommunitySpacePerson(communitySpacePersonPo);

            if (flag < 1) {
                throw new CmdException("删除数据失败");
            }
        }


        cmdDataFlowContext.setResponseEntity(ResultVo.success());
    }

    /**
     * 发起退款
     *
     * @param communitySpacePersonDtos
     */
    private void returnOnlinePayMoney(List<CommunitySpacePersonDto> communitySpacePersonDtos) {
        OnlinePayDto onlinePayDto = new OnlinePayDto();
        onlinePayDto.setOrderId(communitySpacePersonDtos.get(0).getOrderId());
        List<OnlinePayDto> onlinePayDtos = onlinePayV1InnerServiceSMOImpl.queryOnlinePays(onlinePayDto);
        if (onlinePayDtos == null || onlinePayDtos.size() < 1) {
            return;
        }

        OnlinePayPo onlinePayPo = new OnlinePayPo();
        onlinePayPo.setOrderId(onlinePayDtos.get(0).getOrderId());
        onlinePayPo.setPayId(onlinePayDtos.get(0).getPayId());
        onlinePayPo.setState(OnlinePayDto.STATE_WT);
        onlinePayPo.setRefundFee(onlinePayDtos.get(0).getTotalFee());
        onlinePayV1InnerServiceSMOImpl.updateOnlinePay(onlinePayPo);
    }
}
