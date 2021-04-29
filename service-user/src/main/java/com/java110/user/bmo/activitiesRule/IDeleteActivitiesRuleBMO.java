package com.java110.user.bmo.activitiesRule;
import com.java110.po.activitiesRule.ActivitiesRulePo;
import org.springframework.http.ResponseEntity;

public interface IDeleteActivitiesRuleBMO {


    /**
     * 修改活动规则
     * add by wuxw
     * @param activitiesRulePo
     * @return
     */
    ResponseEntity<String> delete(ActivitiesRulePo activitiesRulePo);


}
