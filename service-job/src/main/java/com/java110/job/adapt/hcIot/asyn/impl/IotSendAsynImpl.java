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
package com.java110.job.adapt.hcIot.asyn.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.client.RestTemplate;
import com.java110.dto.machine.MachineDto;
import com.java110.intf.common.IMachineAttrInnerServiceSMO;
import com.java110.intf.common.IMachineInnerServiceSMO;
import com.java110.job.adapt.hcIot.GetToken;
import com.java110.job.adapt.hcIot.IotConstant;
import com.java110.job.adapt.hcIot.asyn.IIotSendAsyn;
import com.java110.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * IOT信息异步同步处理实现类
 * <p>
 * 接口协议地址： https://gitee.com/java110/MicroCommunityThings/blob/master/back/docs/api.md
 *
 * @desc add by 吴学文 11:55
 */
@Service
public class IotSendAsynImpl implements IIotSendAsyn {
    private static final Logger logger = LoggerFactory.getLogger(IotSendAsynImpl.class);


    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private IMachineInnerServiceSMO machineInnerServiceSMOImpl;

    @Autowired
    private IMachineAttrInnerServiceSMO machineAttrInnerServiceSMOImpl;

    /**
     * 封装头信息
     *
     * @return
     */
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access_token", GetToken.get(outRestTemplate));
        //httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");

        return httpHeaders;
    }

    @Override
    @Async
    public void addCommunity(JSONObject postParameters) {
        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.ADD_COMMUNITY_URL, HttpMethod.POST, httpEntity, String.class);
        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    @Async
    public void editCommunity(JSONObject postParameters) {
        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.UPDATE_COMMUNITY_URL, HttpMethod.POST, httpEntity, String.class);
        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    @Async
    public void deleteCommunity(JSONObject postParameters) {
        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.DELETE_COMMUNITY_URL, HttpMethod.POST, httpEntity, String.class);
        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    /**
     * 添加设备
     *
     * @param postParameters
     * @param ownerDtos
     */
    @Override
    @Async
    public void addMachine(JSONObject postParameters, List<JSONObject> ownerDtos) {
        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.ADD_MACHINE_URL, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return;
        }
        JSONObject tokenObj = JSONObject.parseObject(responseEntity.getBody());

        if (!tokenObj.containsKey("code") || ResultVo.CODE_OK != tokenObj.getInteger("code")) {
            return;
        }

        MachineDto machinePo = new MachineDto();
        machinePo.setMachineId(postParameters.getString("extMachineId"));
        machinePo.setCommunityId(postParameters.getString("extCommunityId"));
        machinePo.setState("1700");
        machineInnerServiceSMOImpl.updateMachineState(machinePo);

        for (JSONObject owner : ownerDtos) {
            addOwner(owner);
        }
    }

    @Override
    @Async
    public void updateMachine(JSONObject postParameters) {

        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.UPDATE_MACHINE_URL, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    public void deleteSend(JSONObject postParameters) {

        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.DELETE_MACHINE_URL, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    public void addOwner(JSONObject postParameters) {

        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.ADD_OWNER, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    public void sendUpdateOwner(JSONObject postParameters) {

        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.EDIT_OWNER, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);
    }

    @Override
    public void sendDeleteOwner(JSONObject postParameters) {

        HttpEntity httpEntity = new HttpEntity(postParameters, getHeaders());
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(IotConstant.DELETE_OWNER, HttpMethod.POST, httpEntity, String.class);

        logger.debug("调用HC IOT信息：" + responseEntity);
    }
}
