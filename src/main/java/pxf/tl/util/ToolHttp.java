package pxf.tl.util;


import pxf.tl.api.Charsets;
import pxf.tl.help.Whether;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Http头部助手类
 *
 * @author potatoxf
 */
public final class ToolHttp {
    private static final String DISPOSITION_FORMAT = "attachment; filename=\"%s\"; filename*=%s''%s";

    /**
     * 未编码文件名转Content-Disposition值
     *
     * @param filename 未编码的文件名(包含文件后缀)
     * @return Content-Disposition值
     */
    public static String disposition(String filename, Charsets charsets)
            throws UnsupportedEncodingException {
        String codedFilename = filename;
        if (Whether.noBlank(filename)) {
            codedFilename = URLEncoder.encode(filename, charsets.toString());
        }
        return String.format(DISPOSITION_FORMAT, codedFilename, charsets, codedFilename);
    }
}
