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
import pxf.tl.api.PageList;

import java.util.Map;

/**
 * @author potatoxf
 */
public class SqlServer2012Dialect extends SqlServerDialect {

    @Override
    public Object processPageParameter(MappedStatement ms, Map<String, Object> paramMap, PageList<?> pageList, BoundSql boundSql, CacheKey pageKey) {
        paramMap.put(PAGEPARAMETER_FIRST, pageList.getStartRow());
        paramMap.put(PAGEPARAMETER_SECOND, pageList.getPageSize());
        //处理pageKey
        pageKey.update(pageList.getStartRow());
        pageKey.update(pageList.getPageSize());
        //处理参数配置
        handleParameter(boundSql, ms, long.class, int.class);
        return paramMap;
    }

    @Override
    public String getPageSql(String sql, PageList<?> pageList, CacheKey pageKey) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 64);
        sqlBuilder.append(sql);
        sqlBuilder.append("\n OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        pageKey.update(pageList.getPageSize());
        return sqlBuilder.toString();
    }

}
