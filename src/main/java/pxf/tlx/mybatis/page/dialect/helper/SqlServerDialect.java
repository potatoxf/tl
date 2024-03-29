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

package pxf.tlx.mybatis.page.dialect.helper;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import pxf.tl.help.Whether;
import pxf.tl.api.PageList;
import pxf.tlx.mybatis.page.cache.Cache;
import pxf.tlx.mybatis.page.cache.CacheFactory;
import pxf.tlx.mybatis.page.dialect.AbstractHelperDialect;
import pxf.tlx.mybatis.page.dialect.ReplaceSql;
import pxf.tlx.mybatis.page.dialect.replace.RegexWithNolockReplaceSql;
import pxf.tlx.mybatis.page.dialect.replace.SimpleWithNolockReplaceSql;
import pxf.tlx.mybatis.page.parser.OrderByParser;
import pxf.tlx.mybatis.page.parser.SqlServerParser;

import java.util.Map;
import java.util.Properties;

/**
 * @author potatoxf
 */
public class SqlServerDialect extends AbstractHelperDialect {
    protected SqlServerParser pageSql = new SqlServerParser();
    protected Cache<String, String> CACHE_COUNTSQL;
    protected Cache<String, String> CACHE_PAGESQL;
    protected ReplaceSql replaceSql;

    @Override
    public String getCountSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey countKey) {
        String sql = boundSql.getSql();
        String cacheSql = CACHE_COUNTSQL.get(sql);
        if (cacheSql != null) {
            return cacheSql;
        } else {
            cacheSql = sql;
        }
        cacheSql = replaceSql.replace(cacheSql);
        cacheSql = countSqlParser.getSmartCountSql(cacheSql);
        cacheSql = replaceSql.restore(cacheSql);
        CACHE_COUNTSQL.put(sql, cacheSql);
        return cacheSql;
    }

    @Override
    public Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, PageList<?> pageList, BoundSql boundSql, CacheKey pageKey) {
        return paramMap;
    }

    @Override
    public String getPageSql(String sql, PageList<?> pageList, CacheKey pageKey) {
        //处理pageKey
        pageKey.update(pageList.getStartRow());
        pageKey.update(pageList.getPageSize());
        String cacheSql = CACHE_PAGESQL.get(sql);
        if (cacheSql == null) {
            cacheSql = sql;
            cacheSql = replaceSql.replace(cacheSql);
            cacheSql = pageSql.convertToPageSql(cacheSql, null, null);
            cacheSql = replaceSql.restore(cacheSql);
            CACHE_PAGESQL.put(sql, cacheSql);
        }
        cacheSql = cacheSql.replace(String.valueOf(Long.MIN_VALUE), String.valueOf(pageList.getStartRow()));
        cacheSql = cacheSql.replace(String.valueOf(Long.MAX_VALUE), String.valueOf(pageList.getPageSize()));
        return cacheSql;
    }

    /**
     * 分页查询，pageHelper转换SQL时报错with(nolock)不识别的问题，
     * 重写父类AbstractHelperDialect.getPageSql转换出错的方法。
     * 1. this.replaceSql.replace(sql);先转换成假的表名
     * 2. 然后进行SQL转换
     * 3. this.replaceSql.restore(sql);最后再恢复成真的with(nolock)
     */
    @Override
    public String getPageSql(MappedStatement ms, BoundSql boundSql, Object parameterObject, RowBounds rowBounds, CacheKey pageKey) {
        String sql = boundSql.getSql();
        PageList<?> pageList = this.getLocalPage();
        String orderBy = pageList.getOrderBy();
        if (Whether.noEmpty(orderBy)) {
            pageKey.update(orderBy);
            sql = this.replaceSql.replace(sql);
            sql = OrderByParser.converToOrderBySql(sql, orderBy);
            sql = this.replaceSql.restore(sql);
        }

        return pageList.isOrderByOnly() ? sql : this.getPageSql(sql, pageList, pageKey);
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String replaceSql = properties.getProperty("replaceSql");
        if (Whether.empty(replaceSql) || "regex".equalsIgnoreCase(replaceSql)) {
            this.replaceSql = new RegexWithNolockReplaceSql();
        } else if ("simple".equalsIgnoreCase(replaceSql)) {
            this.replaceSql = new SimpleWithNolockReplaceSql();
        } else {
            try {
                this.replaceSql = (ReplaceSql) Class.forName(replaceSql).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("replaceSql 参数配置的值不符合要求，可选值为 simple 和 regex，或者是实现了 "
                        + ReplaceSql.class.getCanonicalName() + " 接口的全限定类名", e);
            }
        }
        String sqlCacheClass = properties.getProperty("sqlCacheClass");
        if (Whether.noEmpty(sqlCacheClass) && !sqlCacheClass.equalsIgnoreCase("false")) {
            CACHE_COUNTSQL = CacheFactory.createCache(sqlCacheClass, "count", properties);
            CACHE_PAGESQL = CacheFactory.createCache(sqlCacheClass, "page", properties);
        } else {
            CACHE_COUNTSQL = CacheFactory.createCache(null, "count", properties);
            CACHE_PAGESQL = CacheFactory.createCache(null, "page", properties);
        }
    }
}
