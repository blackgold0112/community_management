package com.java110.acct.bmo.accountWithdrawalApply;

import com.java110.po.accountWithdrawalApply.AccountWithdrawalApplyPo;
import org.springframework.http.ResponseEntity;
public interface ISaveAccountWithdrawalApplyBMO {


    /**
     * 添加账户提现
     * add by wuxw
     * @param accountWithdrawalApplyPo
     * @return
     */
    ResponseEntity<String> save(AccountWithdrawalApplyPo accountWithdrawalApplyPo);


}
