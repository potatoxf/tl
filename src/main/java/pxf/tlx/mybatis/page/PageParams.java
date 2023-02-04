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

import lombok.Getter;
import org.apache.ibatis.session.RowBounds;
import pxf.tl.api.Page;
import pxf.tl.api.PageList;
import pxf.tl.help.Whether;
import pxf.tlx.mybatis.page.util.PageObjectUtil;

import java.util.Properties;

/**
 * PageList<?> 参数信息
 *
 * @author potatoxf
 */
@Getter
public class PageParams {
    //RowBounds参数offset作为PageNum使用 - 默认不使用
    protected boolean offsetAsPageNum = false;
    //RowBounds是否进行count查询 - 默认不查询
    protected boolean rowBoundsWithCount = false;
    //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
    protected boolean pageSizeZero = false;
    //分页合理化
    protected boolean reasonable = false;
    //是否支持接口参数来传递分页参数，默认false
    protected boolean supportMethodsArguments = false;
    //默认count(0)
    protected String countColumn = "0";

    /**
     * 获取分页参数
     *
     * @param parameterObject
     * @param rowBounds
     * @return
     */
    public PageList<?> getPage(Object parameterObject, RowBounds rowBounds) {
        PageList<?> pageList = PageHelper.getLocalPage();
        if (pageList == null) {
            if (rowBounds != RowBounds.DEFAULT) {
                if (offsetAsPageNum) {
                    pageList = new PageList<>(rowBounds.getOffset(), rowBounds.getLimit(), rowBoundsWithCount);
                } else {
                    pageList = new PageList<>(new int[]{rowBounds.getOffset(), rowBounds.getLimit()}, rowBoundsWithCount);
                    //offsetAsPageNum=false的时候，由于PageNum问题，不能使用reasonable，这里会强制为false
                    pageList.setReasonable(false);
                }
                if (rowBounds instanceof PageRowBounds pageRowBounds) {
                    pageList.setCount(pageRowBounds.getCount() == null || pageRowBounds.getCount());
                }
            } else if (parameterObject instanceof Page || supportMethodsArguments) {
                try {
                    pageList = PageObjectUtil.getPageFromObject(parameterObject, false);
                } catch (Exception e) {
                    return null;
                }
            }
            if (pageList == null) {
                return null;
            }
            PageHelper.setLocalPage(pageList);
        }
        //分页合理化
        if (pageList.getReasonable() == null) {
            pageList.setReasonable(reasonable);
        }
        //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
        if (pageList.getPageSizeZero() == null) {
            pageList.setPageSizeZero(pageSizeZero);
        }
        return pageList;
    }

    public void setProperties(Properties properties) {
        //offset作为PageNum使用
        String offsetAsPageNum = properties.getProperty("offsetAsPageNum");
        this.offsetAsPageNum = Boolean.parseBoolean(offsetAsPageNum);
        //RowBounds方式是否做count查询
        String rowBoundsWithCount = properties.getProperty("rowBoundsWithCount");
        this.rowBoundsWithCount = Boolean.parseBoolean(rowBoundsWithCount);
        //当设置为true的时候，如果pagesize设置为0（或RowBounds的limit=0），就不执行分页
        String pageSizeZero = properties.getProperty("pageSizeZero");
        this.pageSizeZero = Boolean.parseBoolean(pageSizeZero);
        //分页合理化，true开启，如果分页参数不合理会自动修正。默认false不启用
        String reasonable = properties.getProperty("reasonable");
        this.reasonable = Boolean.parseBoolean(reasonable);
        //是否支持接口参数来传递分页参数，默认false
        String supportMethodsArguments = properties.getProperty("supportMethodsArguments");
        this.supportMethodsArguments = Boolean.parseBoolean(supportMethodsArguments);
        //默认count列
        String countColumn = properties.getProperty("countColumn");
        if (Whether.noEmpty(countColumn)) {
            this.countColumn = countColumn;
        }
        //当offsetAsPageNum=false的时候，不能
        //参数映射
        PageObjectUtil.setParams(properties.getProperty("params"));
    }
}
