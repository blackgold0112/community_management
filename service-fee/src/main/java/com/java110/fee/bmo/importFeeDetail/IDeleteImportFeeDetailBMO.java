package com.java110.fee.bmo.importFeeDetail;
import com.java110.po.importFeeDetail.ImportFeeDetailPo;
import org.springframework.http.ResponseEntity;

public interface IDeleteImportFeeDetailBMO {


    /**
     * 修改费用导入明细
     * add by wuxw
     * @param importFeeDetailPo
     * @return
     */
    ResponseEntity<String> delete(ImportFeeDetailPo importFeeDetailPo);


}
