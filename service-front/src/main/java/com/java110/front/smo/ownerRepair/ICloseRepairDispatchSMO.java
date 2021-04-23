package com.java110.front.smo.ownerRepair;

import com.java110.core.context.IPageData;
import org.springframework.http.ResponseEntity;

/**
 * 添加报修派单接口
 *
 * add by wuxw 2019-06-30
 */
public interface ICloseRepairDispatchSMO {

    /**
     * 添加报修派单
     * @param pd 页面数据封装
     * @return ResponseEntity 对象
     */
    ResponseEntity<String> close(IPageData pd);
}
