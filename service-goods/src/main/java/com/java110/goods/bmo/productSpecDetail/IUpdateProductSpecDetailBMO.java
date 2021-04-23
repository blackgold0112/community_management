package com.java110.goods.bmo.productSpecDetail;
import com.java110.po.product.ProductSpecDetailPo;
import org.springframework.http.ResponseEntity;

public interface IUpdateProductSpecDetailBMO {


    /**
     * 修改产品规格明细
     * add by wuxw
     * @param productSpecDetailPo
     * @return
     */
    ResponseEntity<String> update(ProductSpecDetailPo productSpecDetailPo);


}
