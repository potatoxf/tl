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

package pxf.tlx.mybatis.page.dialect;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;
import pxf.tl.api.PageList;
import pxf.tl.help.Whether;
import pxf.tlx.mybatis.page.PageHelper;
import pxf.tlx.mybatis.page.PageRowBounds;
import pxf.tlx.mybatis.page.parser.OrderByParser;
import pxf.tlx.mybatis.page.util.ExecutorUtil;
import pxf.tlx.mybatis.page.util.MetaObjectUtil;

import java.util.*;

/**
 * 针对 PageHelper 的实现
 *
 * @author potatoxf
 * @since 2016-12-04 14:32
 */
public abstract class AbstractHelperDialect extends AbstractDialect {
    //分页的id后缀
    public static final String SUFFIX_PAGE = "_PageHelper";
    //count查询的id后缀
    public static final String SUFFIX_COUNT = SUFFIX_PAGE + "_Count";
    //第一个分页参数
    public static final String PAGEPARAMETER_FIRST = "First" + SUFFIX_PAGE;
    //第二个分页参数
    public static final String PAGEPARAMETER_SECOND = "Second" + SUFFIX_PAGE;

    /**
     * 获取分页参数
     *
     * @param <T>
     * @return
     */
    public <T> PageList<T> getLocalPage() {
        return PageHelper.getLocalPage();
    }

    @Override
    public final boolean skip(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        //该方法不会被调用
        return true;
    }

    @Override
    public boolean beforeCount(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        PageList<?> pageList = getLocalPage();
        return !pageList.isOrderByOnly() && pageList.isCount();
    }

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
        PageList<Object> pageList = getLocalPage();
        String countColumn = pageList.getCountColumn();
        if (Whether.noEmpty(countColumn)) {
            return countSqlParser.getSmartCountSql(boundSql.getSql(), countColumn);
        }
        return countSqlParser.getSmartCountSql(boundSql.getSql());
    }

    @Override
    public boolean afterCount(int count, Object parameterObject, RowBounds rowBounds) {
        PageList<?> pageList = getLocalPage();
        pageList.setTotal(count);
        if (rowBounds instanceof PageRowBounds) {
            ((PageRowBounds) rowBounds).setTotal(count);
        }
        //pageSize < 0 的时候，不执行分页查询
        //pageSize = 0 的时候，还需要执行后续查询，但是不会分页
        if (pageList.getPageSizeZero() != null) {
            //PageSizeZero=false&&pageSize<=0
            if (!pageList.getPageSizeZero() && pageList.getPageSize() <= 0) {
                return false;
            }
            //PageSizeZero=true&&pageSize<0 返回 false，只有>=0才需要执行后续的
            else if (pageList.getPageSizeZero() && pageList.getPageSize() < 0) {
                return false;
            }
        }
        //页码>0 && 开始行数<总行数即可，不需要考虑 pageSize（上面的 if 已经处理不符合要求的值了）
        return pageList.getPageNum() > 0 && count > pageList.getStartRow();
    }

    @Override
    public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql, CacheKey pageKey) {
        //处理参数
        PageList<?> pageList = getLocalPage();
        //如果只是 order by 就不必处理参数
        if (pageList.isOrderByOnly()) {
            return parameterObject;
        }
        Map<String, Object> paramMap = null;
        if (parameterObject == null) {
            paramMap = new HashMap<String, Object>();
        } else if (parameterObject instanceof Map) {
            //解决不可变Map的情况
            paramMap = new HashMap<String, Object>();
            paramMap.putAll((Map) parameterObject);
        } else {
            paramMap = new HashMap<String, Object>();
            // sqlSource为ProviderSqlSource时，处理只有1个参数的情况
            if (ms.getSqlSource() instanceof ProviderSqlSource) {
                String[] providerMethodArgumentNames = ExecutorUtil.getProviderMethodArgumentNames((ProviderSqlSource) ms.getSqlSource());
                if (providerMethodArgumentNames != null && providerMethodArgumentNames.length == 1) {
                    paramMap.put(providerMethodArgumentNames[0], parameterObject);
                    paramMap.put("param1", parameterObject);
                }
            }
            //动态sql时的判断条件不会出现在ParameterMapping中，但是必须有，所以这里需要收集所有的getter属性
            //TypeHandlerRegistry可以直接处理的会作为一个直接使用的对象进行处理
            boolean hasTypeHandler = ms.getConfiguration().getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass());
            MetaObject metaObject = MetaObjectUtil.forObject(parameterObject);
            //需要针对注解形式的MyProviderSqlSource保存原值
            if (!hasTypeHandler) {
                for (String name : metaObject.getGetterNames()) {
                    paramMap.put(name, metaObject.getValue(name));
                }
            }
            //下面这段方法，主要解决一个常见类型的参数时的问题
            if (boundSql.getParameterMappings() != null && boundSql.getParameterMappings().size() > 0) {
                for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
                    String name = parameterMapping.getProperty();
                    if (!name.equals(PAGEPARAMETER_FIRST)
                            && !name.equals(PAGEPARAMETER_SECOND)
                            && paramMap.get(name) == null) {
                        if (hasTypeHandler
                                || parameterMapping.getJavaType().equals(parameterObject.getClass())) {
                            paramMap.put(name, parameterObject);
                            break;
                        }
                    }
                }
            }
        }
        return processPageParameter(ms, paramMap, pageList, boundSql, pageKey);
    }

    /**
     * 处理分页参数
     *
     * @param ms
     * @param paramMap
     * @param pageList
     * @param boundSql
     * @param pageKey
     * @return
     */
    public abstract Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, PageList<?> pageList, BoundSql boundSql, CacheKey pageKey);

    @Override
    public boolean beforePage(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        PageList<?> pageList = getLocalPage();
        return pageList.isOrderByOnly() || pageList.getPageSize() > 0;
    }

    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
        PageList<?> pageList = getLocalPage();
        //支持 order by
        String orderBy = pageList.getOrderBy();
        if (Whether.noEmpty(orderBy)) {
            pageKey.update(orderBy);
            sql = OrderByParser.converToOrderBySql(sql, orderBy);
        }
        if (pageList.isOrderByOnly()) {
            return sql;
        }
        return getPageSql(sql, pageList, pageKey);
    }

    /**
     * 单独处理分页部分
     *
     * @param sql
     * @param pageList
     * @param pageKey
     * @return
     */
    public abstract String getPageSql(String sql, PageList<?> pageList, CacheKey pageKey);

    @Override
    public Object afterPage(List pageList, Object parameterObject, RowBounds rowBounds) {
        PageList<?> page = getLocalPage();
        if (page == null) {
            return pageList;
        }
        page.addAll(pageList);
        if (!page.isCount()) {
            page.setTotal(-1);
        } else if ((page.getPageSizeZero() != null && page.getPageSizeZero()) && page.getPageSize() == 0) {
            page.setTotal(pageList.size());
        } else if (page.isOrderByOnly()) {
            page.setTotal(pageList.size());
        }
        return page;
    }

    @Override
    public void afterAll() {

    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * @param boundSql
     * @param ms
     * @deprecated use {@code handleParameter(BoundSql boundSql, MappedStatement ms, Class<?> firstClass, Class<?> secondClass)}
     */
    @Deprecated
    protected void handleParameter(BoundSql boundSql, MappedStatement ms) {
        if (boundSql.getParameterMappings() != null) {
            handleParameter(boundSql, ms, long.class, long.class);
        }
    }

    protected void handleParameter(BoundSql boundSql, MappedStatement ms, Class<?> firstClass, Class<?> secondClass) {
        if (boundSql.getParameterMappings() != null) {
            List<ParameterMapping> newParameterMappings = new ArrayList<ParameterMapping>(boundSql.getParameterMappings());
            newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_FIRST, firstClass).build());
            newParameterMappings.add(new ParameterMapping.Builder(ms.getConfiguration(), PAGEPARAMETER_SECOND, secondClass).build());
            MetaObject metaObject = MetaObjectUtil.forObject(boundSql);
            metaObject.setValue("parameterMappings", newParameterMappings);
        }
    }
}
