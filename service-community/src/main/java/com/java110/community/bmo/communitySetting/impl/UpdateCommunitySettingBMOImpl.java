package com.java110.community.bmo.communitySetting.impl;

import com.java110.community.bmo.communitySetting.IUpdateCommunitySettingBMO;
import com.java110.core.annotation.Java110Transactional;
import com.java110.intf.community.ICommunitySettingInnerServiceSMO;
import com.java110.po.communitySetting.CommunitySettingPo;
import com.java110.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("updateCommunitySettingBMOImpl")
public class UpdateCommunitySettingBMOImpl implements IUpdateCommunitySettingBMO {

    @Autowired
    private ICommunitySettingInnerServiceSMO communitySettingInnerServiceSMOImpl;

    /**
     *
     *
     * @param communitySettingPo
     * @return 订单服务能够接受的报文
     */
    @Java110Transactional
    public ResponseEntity<String> update(CommunitySettingPo communitySettingPo) {

        int flag = communitySettingInnerServiceSMOImpl.updateCommunitySetting(communitySettingPo);

        if (flag > 0) {
        return ResultVo.createResponseEntity(ResultVo.CODE_OK, "保存成功");
        }

        return ResultVo.createResponseEntity(ResultVo.CODE_ERROR, "保存失败");
    }

}
