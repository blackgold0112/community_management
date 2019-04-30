package com.java110.common.util;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BeanConvertUtil
 * @Description bean 转化工具类
 * @Author wuxw
 * @Date 2019/4/24 12:53
 * @Version 1.0
 * add by wuxw 2019/4/24
 **/
public final class BeanConvertUtil {

    private BeanConvertUtil() {
    }


    /**
     * 对象A转为对象B
     * 这个也支持map转bean
     *
     * @param orgBean 原始对象
     * @param dstBean 目标对象类
     * @param <T1>    原始对象
     * @param <T2>    目标对象
     * @return 目标对象
     */
    public static <T1, T2> T2 covertBean(T1 orgBean, T2 dstBean) {

        try {
            BeanUtils.copyProperties(dstBean, orgBean);
        } catch (Exception e) {
            throw new RuntimeException("bean转换bean失败", e);
        }
        return dstBean;
    }

    /**
     * 对象A转为对象B (类)
     * 这个也支持map转bean
     *
     * @param orgBean 原始对象
     * @param t       目标对象类
     * @param <T1>    原始对象
     * @param <T2>    目标对象
     * @return 目标对象
     */
    public static <T1, T2> T2 covertBean(T1 orgBean, Class<T2> t) {

        T2 returnModel = null;
        try {
            returnModel = t.newInstance();
            BeanUtils.copyProperties(returnModel, orgBean);
        } catch (Exception e) {
            throw new RuntimeException("bean转换bean失败", e);
        }
        return returnModel;
    }


    /**
     * 对象A集合转为对象B集合
     *
     * @param orgBeans 原始对象列表
     * @param t        目标对象类
     * @param <T1>     原始对象
     * @param <T2>     目标对象
     * @return 目标对象
     */
    public static <T1, T2> List<T2> covertBeanList(List<T1> orgBeans, Class<T2> t) {
        List<T2> newBeanList = new ArrayList<T2>();
        for (T1 orgbean : orgBeans) {
            T2 newBean = covertBean(orgbean, t);
            newBeanList.add(newBean);
        }
        return newBeanList;
    }

    /**
     * bean转换为map对象
     *
     * @param orgBean 原始bean
     * @return map对象
     */
    public static  Map beanCovertMap(Object orgBean) {
        Map newMap = null;

        try {
            newMap = PropertyUtils.describe(orgBean);
        } catch (Exception e) {
            throw new RuntimeException("bean转换Map失败", e);
        }

        return newMap;
    }


    /**
     * bean集合转换为map对象集合
     *
     * @param orgBeans 原始bean 列表
     * @return map对象 列表
     */
    public static List<Map<String, Object>> beanCovertMapList(List<Object> orgBeans) {
        List<Map<String, Object>> newMaps = new ArrayList<Map<String, Object>>();
        Map<String, Object> newMap = null;
        for (Object orgbean : orgBeans) {
            newMap = beanCovertMap(orgbean);
            newMaps.add(newMap);
        }
        return newMaps;
    }
}
