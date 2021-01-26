package com.java110.fee.smo.impl;

import com.java110.core.base.smo.BaseServiceSMO;
import com.java110.dto.PageDto;
import com.java110.dto.applyRoomDiscount.ApplyRoomDiscountDto;
import com.java110.dto.fee.FeeDetailDto;
import com.java110.dto.fee.FeeDto;
import com.java110.dto.feeDiscount.ComputeDiscountDto;
import com.java110.dto.feeDiscount.FeeDiscountDto;
import com.java110.dto.feeDiscountSpec.FeeDiscountSpecDto;
import com.java110.dto.payFeeConfigDiscount.PayFeeConfigDiscountDto;
import com.java110.fee.dao.IFeeDiscountServiceDao;
import com.java110.fee.discount.IComputeDiscount;
import com.java110.intf.fee.*;
import com.java110.po.feeDiscount.FeeDiscountPo;
import com.java110.utils.cache.MappingCache;
import com.java110.utils.factory.ApplicationContextFactory;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import com.java110.utils.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName FloorInnerServiceSMOImpl
 * @Description 费用折扣内部服务实现类
 * @Author wuxw
 * @Date 2019/4/24 9:20
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
@RestController
public class FeeDiscountInnerServiceSMOImpl extends BaseServiceSMO implements IFeeDiscountInnerServiceSMO {

    @Autowired
    private IFeeDiscountServiceDao feeDiscountServiceDaoImpl;

    @Autowired
    private IFeeInnerServiceSMO feeInnerServiceSMOImpl;

    @Autowired
    private IPayFeeConfigDiscountInnerServiceSMO payFeeConfigDiscountInnerServiceSMOImpl;

    @Autowired
    private IFeeDiscountSpecInnerServiceSMO feeDiscountSpecInnerServiceSMOImpl;

    @Autowired
    private IApplyRoomDiscountInnerServiceSMO applyRoomDiscountInnerServiceSMOImpl;

    //域
    public static final String DOMAIN_COMMON = "DOMAIN.COMMON";

    //键
    public static final String DISCOUNT_MODE = "DISCOUNT_MODE";


    @Override
    public int saveFeeDiscount(@RequestBody FeeDiscountPo feeDiscountPo) {
        int saveFlag = 1;
        feeDiscountServiceDaoImpl.saveFeeDiscountInfo(BeanConvertUtil.beanCovertMap(feeDiscountPo));
        return saveFlag;
    }

    @Override
    public int updateFeeDiscount(@RequestBody FeeDiscountPo feeDiscountPo) {
        int saveFlag = 1;
        feeDiscountServiceDaoImpl.updateFeeDiscountInfo(BeanConvertUtil.beanCovertMap(feeDiscountPo));
        return saveFlag;
    }

    @Override
    public int deleteFeeDiscount(@RequestBody FeeDiscountPo feeDiscountPo) {
        int saveFlag = 1;
        feeDiscountPo.setStatusCd("1");
        feeDiscountServiceDaoImpl.updateFeeDiscountInfo(BeanConvertUtil.beanCovertMap(feeDiscountPo));
        return saveFlag;
    }

    @Override
    public List<FeeDiscountDto> queryFeeDiscounts(@RequestBody FeeDiscountDto feeDiscountDto) {

        //校验是否传了 分页信息

        int page = feeDiscountDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            feeDiscountDto.setPage((page - 1) * feeDiscountDto.getRow());
        }

        List<FeeDiscountDto> feeDiscounts = BeanConvertUtil.covertBeanList(feeDiscountServiceDaoImpl.getFeeDiscountInfo(BeanConvertUtil.beanCovertMap(feeDiscountDto)), FeeDiscountDto.class);

        freshDiscountSpec(feeDiscounts);

        return feeDiscounts;
    }


    private void freshDiscountSpec(List<FeeDiscountDto> feeDiscounts) {

        if (feeDiscounts == null || feeDiscounts.size() < 1) {
            return;
        }

        List<String> discountIds = new ArrayList<>();
        for (FeeDiscountDto feeDiscount : feeDiscounts) {
            discountIds.add(feeDiscount.getDiscountId());
        }

        FeeDiscountSpecDto tmpFeeDiscountSpecDto = new FeeDiscountSpecDto();

        tmpFeeDiscountSpecDto.setDiscountIds(discountIds.toArray(new String[discountIds.size()]));
        tmpFeeDiscountSpecDto.setCommunityId(feeDiscounts.get(0).getCommunityId());

        List<FeeDiscountSpecDto> feeDiscountSpecDtos = feeDiscountSpecInnerServiceSMOImpl.queryFeeDiscountSpecs(tmpFeeDiscountSpecDto);

        if (feeDiscountSpecDtos == null || feeDiscountSpecDtos.size() < 1) {
            return;
        }
        List<FeeDiscountSpecDto> tmpSpecs = null;
        for (FeeDiscountDto feeDiscount : feeDiscounts) {
            tmpSpecs = new ArrayList<>();
            for (FeeDiscountSpecDto feeDiscountSpecDto : feeDiscountSpecDtos) {
                if (feeDiscount.getDiscountId().equals(feeDiscountSpecDto.getDiscountId())) {
                    tmpSpecs.add(feeDiscountSpecDto);
                }
            }
            feeDiscount.setFeeDiscountSpecs(tmpSpecs);
        }
    }

    /**
     * 计算折扣
     *
     * @param feeDetailDto
     * @return
     */
    public List<ComputeDiscountDto> computeDiscount(@RequestBody FeeDetailDto feeDetailDto) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        List<ComputeDiscountDto> computeDiscountDtos = new ArrayList<>();
        FeeDto feeDto = new FeeDto();
        feeDto.setFeeId(feeDetailDto.getFeeId());
        feeDto.setCommunityId(feeDetailDto.getCommunityId());
        feeDto.setState(FeeDto.STATE_DOING);
        List<FeeDto> feeDtos = feeInnerServiceSMOImpl.queryFees(feeDto);
        Assert.listOnlyOne(feeDtos, "费用不存在");
        PayFeeConfigDiscountDto payFeeConfigDiscountDto = new PayFeeConfigDiscountDto();
        payFeeConfigDiscountDto.setConfigId(feeDtos.get(0).getConfigId());
        payFeeConfigDiscountDto.setRow(feeDetailDto.getRow());
        payFeeConfigDiscountDto.setPage(feeDetailDto.getPage());
        payFeeConfigDiscountDto.setCommunityId(feeDetailDto.getCommunityId());
        payFeeConfigDiscountDto.setStatusCd("0");
        Date currentTime = new Date();
        payFeeConfigDiscountDto.setCurrentTime(currentTime);
        //根据费用查询折扣
        List<PayFeeConfigDiscountDto> payFeeConfigDiscountDtos =
                payFeeConfigDiscountInnerServiceSMOImpl.queryPayFeeConfigDiscounts(payFeeConfigDiscountDto);
        if (payFeeConfigDiscountDtos == null || payFeeConfigDiscountDtos.size() < 1) {
            return computeDiscountDtos;
        }
        c.setTime(feeDetailDto.getStartTime());
        double mon = Double.parseDouble(feeDetailDto.getCycles());
        c.add(Calendar.MONTH, (int) mon);
        //获取缴费结束时间
        Date finishTime = c.getTime();
        for (PayFeeConfigDiscountDto tmpPayFeeConfigDiscountDto : payFeeConfigDiscountDtos) {
            //获取缴费最大截止时间
            Date payMaxEndTime = tmpPayFeeConfigDiscountDto.getPayMaxEndTime();
            if (payMaxEndTime == null) {
                doCompute(tmpPayFeeConfigDiscountDto, Double.parseDouble(feeDetailDto.getCycles()), computeDiscountDtos, feeDetailDto.getFeeId());
            } else if (payMaxEndTime.getTime() >= finishTime.getTime()) {
                doCompute(tmpPayFeeConfigDiscountDto, Double.parseDouble(feeDetailDto.getCycles()), computeDiscountDtos, feeDetailDto.getFeeId());
            } else {
                continue;
            }
        }
        if (!StringUtil.isEmpty(feeDetailDto.getPayerObjType()) && feeDetailDto.getPayerObjType().equals("3333")) {
            //根据房屋ID,去折扣申请表查询是否有折扣
            ApplyRoomDiscountDto applyRoomDiscountDto = new ApplyRoomDiscountDto();
            //审核已通过
            applyRoomDiscountDto.setState("4");
            //小区ID
            applyRoomDiscountDto.setCommunityId(feeDetailDto.getCommunityId());
            //房屋id
            applyRoomDiscountDto.setRoomId(feeDetailDto.getPayerObjId());
            //开始时间
            applyRoomDiscountDto.setStartTime(simpleDateFormat.format(feeDetailDto.getStartTime()));
            //结束时间
            c.setTime(feeDetailDto.getStartTime());
            double month = Double.parseDouble(feeDetailDto.getCycles());
            c.add(Calendar.MONTH, (int) month);
            Date endTime = c.getTime();
            applyRoomDiscountDto.setEndTime(simpleDateFormat.format(endTime));
            //查询折扣申请表
            List<ApplyRoomDiscountDto> applyRoomDiscountDtos = applyRoomDiscountInnerServiceSMOImpl.queryApplyRoomDiscounts(applyRoomDiscountDto);
            //判断查询的折扣申请表是否有数据
            if (applyRoomDiscountDtos.size() > 0) {
                //获取优惠id
                String discountId = applyRoomDiscountDtos.get(0).getDiscountId();
                PayFeeConfigDiscountDto payFeeConfigDiscount = new PayFeeConfigDiscountDto();
                payFeeConfigDiscount.setCommunityId(applyRoomDiscountDtos.get(0).getCommunityId());
                payFeeConfigDiscount.setDiscountId(discountId);
                doCompute(payFeeConfigDiscount, Double.parseDouble(feeDetailDto.getCycles()), computeDiscountDtos, feeDetailDto.getFeeId());
            }
        }
        //取出开关映射的值
        String value = MappingCache.getValue(DOMAIN_COMMON, DISCOUNT_MODE);
        List<ComputeDiscountDto> computeDiscountDtoList = new ArrayList<>();
        for (ComputeDiscountDto computeDiscountDto : computeDiscountDtos) {
            computeDiscountDto.setValue(value);
            computeDiscountDtoList.add(computeDiscountDto);
        }
        return computeDiscountDtoList;
    }

    private void doCompute(PayFeeConfigDiscountDto tmpPayFeeConfigDiscountDto, double cycles, List<ComputeDiscountDto> computeDiscountDtos, String feeId) {
        FeeDiscountDto feeDiscountDto = new FeeDiscountDto();
        feeDiscountDto.setCommunityId(tmpPayFeeConfigDiscountDto.getCommunityId());
        feeDiscountDto.setDiscountId(tmpPayFeeConfigDiscountDto.getDiscountId());
        List<FeeDiscountDto> feeDiscountDtos = queryFeeDiscounts(feeDiscountDto);
        if (feeDiscountDtos == null || feeDiscountDtos.size() < 1) {
            return;
        }
        for (FeeDiscountDto tmpFeeDiscountDto : feeDiscountDtos) {
            tmpFeeDiscountDto.setFeeId(feeId);
            tmpFeeDiscountDto.setCycles(cycles);
        }
        IComputeDiscount computeDiscount = (IComputeDiscount) ApplicationContextFactory.getBean(feeDiscountDtos.get(0).getBeanImpl());
        ComputeDiscountDto computeDiscountDto = computeDiscount.compute(feeDiscountDtos.get(0));
        if (computeDiscountDto == null) {
            return;
        }
        computeDiscountDtos.add(computeDiscountDto);
    }


    @Override
    public int queryFeeDiscountsCount(@RequestBody FeeDiscountDto feeDiscountDto) {
        return feeDiscountServiceDaoImpl.queryFeeDiscountsCount(BeanConvertUtil.beanCovertMap(feeDiscountDto));
    }

    public IFeeDiscountServiceDao getFeeDiscountServiceDaoImpl() {
        return feeDiscountServiceDaoImpl;
    }

    public void setFeeDiscountServiceDaoImpl(IFeeDiscountServiceDao feeDiscountServiceDaoImpl) {
        this.feeDiscountServiceDaoImpl = feeDiscountServiceDaoImpl;
    }
}
