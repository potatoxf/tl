/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pxf.tlx.mybatis.page.util;

import org.apache.ibatis.reflection.MetaObject;
import pxf.tl.api.PageList;
import pxf.tl.help.Whether;
import pxf.tl.api.Page;
import pxf.tlx.mybatis.page.PageException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 分页参数对象工具类
 *
 * @author potatoxf
 */
public abstract class PageObjectUtil {
    //request获取方法
    protected static Boolean hasRequest;
    protected static Class<?> requestClass;
    protected static Method getParameterMap;
    protected static Map<String, String> PARAMS = new HashMap<>(6, 1);

    static {
        try {
            requestClass = Class.forName("javax.servlet.ServletRequest");
            getParameterMap = requestClass.getMethod("getParameterMap");
            hasRequest = true;
        } catch (Throwable e) {
            hasRequest = false;
        }
        PARAMS.put("pageNum", "pageNum");
        PARAMS.put("pageSize", "pageSize");
        PARAMS.put("count", "countSql");
        PARAMS.put("orderBy", "orderBy");
        PARAMS.put("reasonable", "reasonable");
        PARAMS.put("pageSizeZero", "pageSizeZero");
    }

    /**
     * 对象中获取分页参数
     *
     * @param params
     * @return
     */
    public static PageList<?> getPageFromObject(Object params, boolean required) {
        if (params == null) {
            throw new PageException("无法获取分页查询参数!");
        }
        PageList<?> pageList = null;
        if (params instanceof Page pageParams) {
            if (pageParams.getPageNum() != null && pageParams.getPageSize() != null) {
                pageList = new PageList<>(pageParams.getPageNum(), pageParams.getPageSize());
            }
            if (Whether.noEmpty(pageParams.getOrderBy())) {
                if (pageList != null) {
                    pageList.setOrderBy(pageParams.getOrderBy());
                } else {
                    pageList = new PageList<>();
                    pageList.setOrderBy(pageParams.getOrderBy());
                    pageList.setOrderByOnly(true);
                }
            }
            return pageList;
        }
        int pageNum;
        int pageSize;
        MetaObject paramsObject = null;
        if (hasRequest && requestClass.isAssignableFrom(params.getClass())) {
            try {
                paramsObject = MetaObjectUtil.forObject(getParameterMap.invoke(params));
            } catch (Exception e) {
                //忽略
            }
        } else {
            paramsObject = MetaObjectUtil.forObject(params);
        }
        if (paramsObject == null) {
            throw new PageException("分页查询参数处理失败!");
        }
        Object orderBy = getParamValue(paramsObject, "orderBy", false);
        boolean hasOrderBy = orderBy != null && orderBy.toString().length() > 0;
        try {
            Object _pageNum = getParamValue(paramsObject, "pageNum", required);
            Object _pageSize = getParamValue(paramsObject, "pageSize", required);
            if (_pageNum == null || _pageSize == null) {
                if (hasOrderBy) {
                    pageList = new PageList<>();
                    pageList.setOrderBy(orderBy.toString());
                    pageList.setOrderByOnly(true);
                    return pageList;
                }
                return null;
            }
            pageNum = Integer.parseInt(String.valueOf(_pageNum));
            pageSize = Integer.parseInt(String.valueOf(_pageSize));
        } catch (NumberFormatException e) {
            throw new PageException("分页参数不是合法的数字类型!", e);
        }
        pageList = new PageList<>(pageNum, pageSize);
        //count查询
        Object _count = getParamValue(paramsObject, "count", false);
        if (_count != null) {
            pageList.setCount(Boolean.parseBoolean(String.valueOf(_count)));
        }
        //排序
        if (hasOrderBy) {
            pageList.setOrderBy(orderBy.toString());
        }
        //分页合理化
        Object reasonable = getParamValue(paramsObject, "reasonable", false);
        if (reasonable != null) {
            pageList.setReasonable(Boolean.valueOf(String.valueOf(reasonable)));
        }
        //查询全部
        Object pageSizeZero = getParamValue(paramsObject, "pageSizeZero", false);
        if (pageSizeZero != null) {
            pageList.setPageSizeZero(Boolean.valueOf(String.valueOf(pageSizeZero)));
        }
        return pageList;
    }

    /**
     * 从对象中取参数
     *
     * @param paramsObject
     * @param paramName
     * @param required
     * @return
     */
    protected static Object getParamValue(MetaObject paramsObject, String paramName, boolean required) {
        Object value = null;
        if (paramsObject.hasGetter(PARAMS.get(paramName))) {
            value = paramsObject.getValue(PARAMS.get(paramName));
        }
        if (value != null && value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            if (values.length == 0) {
                value = null;
            } else {
                value = values[0];
            }
        }
        if (required && value == null) {
            throw new PageException("分页查询缺少必要的参数:" + PARAMS.get(paramName));
        }
        return value;
    }

    public static void setParams(String params) {
        if (Whether.noEmpty(params)) {
            String[] ps = params.split("[;|,|&]");
            for (String s : ps) {
                String[] ss = s.split("[=|:]");
                if (ss.length == 2) {
                    PARAMS.put(ss[0], ss[1]);
                }
            }
        }
    }

}
