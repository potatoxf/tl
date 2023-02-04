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

package pxf.tlx.mybatis.page;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import pxf.tl.api.PageList;
import pxf.tl.api.Triple;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolBytecode;
import pxf.tlx.mybatis.page.dialect.AbstractHelperDialect;
import pxf.tlx.mybatis.page.parser.CountSqlParser;
import pxf.tlx.mybatis.page.util.MSUtils;
import pxf.tlx.mybatis.page.util.PageObjectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Mybatis - 通用分页拦截器<br/>
 * 项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 *
 * @author potatoxf
 * @version 5.0.0
 */
public class PageHelper implements Dialect, BoundSqlInterceptor.Chain {

    protected static final ThreadLocal<Triple<PageList<?>, BoundSqlInterceptor.Chain, BoundSqlInterceptor>> LOCAL_PAGE = new ThreadLocal<>();
    protected static boolean DEFAULT_COUNT = true;
    private PageParams pageParams;
    private PageAutoDialect autoDialect;
    private BoundSqlInterceptor.Chain defaultChain;

    public static <T> PageList<T> getLocalPage() {
        Triple<PageList<?>, BoundSqlInterceptor.Chain, BoundSqlInterceptor> result = LOCAL_PAGE.get();
        return result != null ? (PageList<T>) result.getCatalog() : null;
    }

    /**
     * 设置 PageList<?> 参数
     *
     * @param pageList
     */
    public static void setLocalPage(PageList<?> pageList) {
        setLocal(pageList, null, null);
    }

    /**
     * 设置 PageList<?> 参数
     *
     * @param pageList
     * @param chain
     * @param interceptor
     */
    public static void setLocal(PageList<?> pageList, BoundSqlInterceptor.Chain chain, BoundSqlInterceptor interceptor) {
        LOCAL_PAGE.set(new Triple<>(pageList, chain, interceptor));
    }

    /**
     * 移除本地变量
     */
    public static void clearPage() {
        LOCAL_PAGE.remove();
    }

    /**
     * 开始分页
     *
     * @param params
     */
    public static <E> PageList<E> startPage(Object params) {
        PageList<E> pageList = (PageList<E>) PageObjectUtil.getPageFromObject(params, true);
        //当已经执行过orderBy的时候
        PageList<E> oldPageList = getLocalPage();
        if (oldPageList != null && oldPageList.isOrderByOnly()) {
            pageList.setOrderBy(oldPageList.getOrderBy());
        }
        setLocalPage(pageList);
        return pageList;
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     */
    public static <E> PageList<E> startPage(int pageNum, int pageSize) {
        return startPage(pageNum, pageSize, DEFAULT_COUNT);
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @param count    是否进行count查询
     */
    public static <E> PageList<E> startPage(int pageNum, int pageSize, boolean count) {
        return startPage(pageNum, pageSize, count, null, null);
    }

    /**
     * 开始分页
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数量
     * @param orderBy  排序
     */
    public static <E> PageList<E> startPage(int pageNum, int pageSize, String orderBy) {
        PageList<E> pageList = startPage(pageNum, pageSize);
        pageList.setOrderBy(orderBy);
        return pageList;
    }

    /**
     * 开始分页
     *
     * @param pageNum      页码
     * @param pageSize     每页显示数量
     * @param count        是否进行count查询
     * @param reasonable   分页合理化,null时用默认配置
     * @param pageSizeZero true且pageSize=0时返回全部结果，false时分页,null时用默认配置
     */
    public static <E> PageList<E> startPage(int pageNum, int pageSize, boolean count, Boolean reasonable, Boolean pageSizeZero) {
        PageList<E> pageList = new PageList<E>(pageNum, pageSize, count);
        pageList.setReasonable(reasonable);
        pageList.setPageSizeZero(pageSizeZero);
        //当已经执行过orderBy的时候
        PageList<E> oldPageList = getLocalPage();
        if (oldPageList != null && oldPageList.isOrderByOnly()) {
            pageList.setOrderBy(oldPageList.getOrderBy());
        }
        setLocalPage(pageList);
        return pageList;
    }

    /**
     * 开始分页
     *
     * @param offset 起始位置，偏移位置
     * @param limit  每页显示数量
     */
    public static <E> PageList<E> offsetPage(int offset, int limit) {
        return offsetPage(offset, limit, DEFAULT_COUNT);
    }

    /**
     * 开始分页
     *
     * @param offset 起始位置，偏移位置
     * @param limit  每页显示数量
     * @param count  是否进行count查询
     */
    public static <E> PageList<E> offsetPage(int offset, int limit, boolean count) {
        PageList<E> pageList = new PageList<>(new int[]{offset, limit}, count);
        //当已经执行过orderBy的时候
        PageList<E> oldPageList = getLocalPage();
        if (oldPageList != null && oldPageList.isOrderByOnly()) {
            pageList.setOrderBy(oldPageList.getOrderBy());
        }
        setLocalPage(pageList);
        return pageList;
    }

    /**
     * 排序
     *
     * @param orderBy
     */
    public static void orderBy(String orderBy) {
        PageList<?> pageList = getLocalPage();
        if (pageList != null) {
            pageList.setOrderBy(orderBy);
        } else {
            pageList = new PageList<>();
            pageList.setOrderBy(orderBy);
            pageList.setOrderByOnly(true);
            setLocalPage(pageList);
        }
    }

    /**
     * 设置参数
     *
     * @param properties 插件属性
     */
    protected static void setStaticProperties(Properties properties) {
        //defaultCount，这是一个全局生效的参数，多数据源时也是统一的行为
        if (properties != null) {
            DEFAULT_COUNT = Boolean.parseBoolean(properties.getProperty("defaultCount", "true"));
        }
    }

    @Override
    public boolean skip(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        if (ms.getId().endsWith(MSUtils.COUNT)) {
            throw new RuntimeException("在系统中发现了多个分页插件，请检查系统配置!");
        }
        PageList<?> pageList = pageParams.getPage(parameterObject, rowBounds);
        if (pageList == null) {
            return true;
        } else {
            //设置默认的 count 列
            if (Whether.empty(pageList.getCountColumn())) {
                pageList.setCountColumn(pageParams.getCountColumn());
            }
            autoDialect.initDelegateDialect(ms);
            return false;
        }
    }

    @Override
    public boolean beforeCount(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        return autoDialect.getDelegate().beforeCount(ms, parameterObject, rowBounds);
    }

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
        return autoDialect.getDelegate().getCountSql(ms, boundSql, parameterObject, rowBounds, countKey);
    }

    @Override
    public boolean afterCount(int count, Object parameterObject, RowBounds rowBounds) {
        return autoDialect.getDelegate().afterCount(count, parameterObject, rowBounds);
    }

    @Override
    public Object processParameterObject(MappedStatement ms, Object parameterObject, BoundSql boundSql, CacheKey pageKey) {
        return autoDialect.getDelegate().processParameterObject(ms, parameterObject, boundSql, pageKey);
    }

    @Override
    public boolean beforePage(MappedStatement ms, Object parameterObject, RowBounds rowBounds) {
        return autoDialect.getDelegate().beforePage(ms, parameterObject, rowBounds);
    }

    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        return autoDialect.getDelegate().getPageSql(ms, boundSql, parameterObject, rowBounds, pageKey);
    }

    public String getPageSql(String sql, PageList<?> pageList, RowBounds rowBounds, CacheKey pageKey) {
        return autoDialect.getDelegate().getPageSql(sql, pageList, pageKey);
    }

    @Override
    public Object afterPage(List pageList, Object parameterObject, RowBounds rowBounds) {
        //这个方法即使不分页也会被执行，所以要判断 null
        AbstractHelperDialect delegate = autoDialect.getDelegate();
        if (delegate != null) {
            return delegate.afterPage(pageList, parameterObject, rowBounds);
        }
        return pageList;
    }

    @Override
    public void afterAll() {
        //这个方法即使不分页也会被执行，所以要判断 null
        AbstractHelperDialect delegate = autoDialect.getDelegate();
        if (delegate != null) {
            delegate.afterAll();
            autoDialect.clearDelegate();
        }
        clearPage();
    }

    @Override
    public BoundSql doBoundSql(BoundSqlInterceptor.Type type, BoundSql boundSql, CacheKey cacheKey) {
        Triple<PageList<?>, BoundSqlInterceptor.Chain, BoundSqlInterceptor> triple
                = LOCAL_PAGE.get();
        PageList<?> localPageList = triple.getCatalog();
        BoundSqlInterceptor.Chain chain = triple.getKey();
        if (chain == null) {
            BoundSqlInterceptor boundSqlInterceptor = triple.getValue();
            if (boundSqlInterceptor != null) {
                chain = new BoundSqlInterceptor.ChainImpl(defaultChain, List.of(boundSqlInterceptor));
            } else if (defaultChain != null) {
                chain = defaultChain;
            }
            if (chain == null) {
                chain = DO_NOTHING;
            }
        } else if (chain instanceof BoundSqlInterceptor.ChainImpl) {
            ((BoundSqlInterceptor.ChainImpl) chain).reset();
        }
        return chain.doBoundSql(type, boundSql, cacheKey);
    }

    @Override
    public void setProperties(Properties properties) {
        setStaticProperties(properties);
        pageParams = new PageParams();
        autoDialect = new PageAutoDialect();
        pageParams.setProperties(properties);
        autoDialect.setProperties(properties);
        defaultChain = buildChain(properties);
        //20180902新增 aggregateFunctions, 允许手动添加聚合函数（影响行数）
        CountSqlParser.addAggregateFunctions(properties.getProperty("aggregateFunctions"));
    }


    private static BoundSqlInterceptor.Chain buildChain(Properties properties) {
        //初始化 boundSqlInterceptorChain
        String boundSqlInterceptorStr = properties.getProperty("boundSqlInterceptors");
        if (Whether.noEmpty(boundSqlInterceptorStr)) {
            String[] boundSqlInterceptors = boundSqlInterceptorStr.split("[;|,]");
            List<BoundSqlInterceptor> list = new ArrayList<BoundSqlInterceptor>();
            for (String boundSqlInterceptor : boundSqlInterceptors) {
                try {
                    list.add(ToolBytecode.createInstance(ToolBytecode.forClassName(boundSqlInterceptor)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (list.size() > 0) {
                return new BoundSqlInterceptor.ChainImpl(null, list);
            }
        }
        return null;
    }
}
