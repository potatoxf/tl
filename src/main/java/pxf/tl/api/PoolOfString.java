package pxf.tl.api;

import pxf.tl.util.ToolXML;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.function.Supplier;

/**
 * @author potatoxf
 */
public interface PoolOfString {
    /**
     *
     */
    String EMPTY = "";
    /**
     * 字符串常量：{@code "null"} <br>
     * 注意：{@code "null" != null}
     */
    String NULL = "null";
    /**
     * 字符串常量：{@code "undefined"} <br>
     * 注意：{@code "null" != null}
     */
    String UNDEFINED = "undefined";
    /**
     * 字符串常量：空格符 {@code " "}
     */
    String SPACE = " ";
    /**
     * 字符串常量：制表符 {@code "\t"}
     */
    String TAB = "	";
    /**
     * 字符串常量：点 {@code "."}
     */
    String DOT = ".";
    /**
     * 字符串常量：双点 {@code ".."} <br>
     * 用途：作为指向上级文件夹的路径，如：{@code "../path"}
     */
    String DOUBLE_DOT = "..";
    /**
     * 字符串常量：斜杠 {@code "/"}
     */
    String SLASH = "/";
    /**
     * 字符串常量：反斜杠 {@code "\\"}
     */
    String BACKSLASH = "\\";
    /**
     * 字符串常量：回车符 {@code "\r"} <br>
     * 解释：该字符常用于表示 Linux 系统和 MacOS 系统下的文本换行
     */
    String CR = "\r";
    /**
     * 字符串常量：换行符 {@code "\n"}
     */
    String LF = "\n";
    /**
     * 字符串常量：Windows 换行 {@code "\r\n"} <br>
     * 解释：该字符串常用于表示 Windows 系统下的文本换行
     */
    String CRLF = "\r\n";
    /**
     * 字符串常量：下划线 {@code "_"}
     */
    String UNDERLINE = "_";
    /**
     * 字符串常量：减号（连接符） {@code "-"}
     */
    String DASHED = "-";
    /**
     * 字符串常量：逗号 {@code ","}
     */
    String COMMA = ",";
    /**
     * 字符串常量：花括号（左） <code>"{"</code>
     */
    String DELIM_START = "{";
    /**
     * 字符串常量：花括号（右） <code>"}"</code>
     */
    String DELIM_END = "}";
    /**
     * 字符串常量：中括号（左） {@code "["}
     */
    String BRACKET_START = "[";
    /**
     * 字符串常量：中括号（右） {@code "]"}
     */
    String BRACKET_END = "]";
    /**
     * 字符串常量：冒号 {@code ":"}
     */
    String COLON = ":";
    /**
     * 字符串常量：艾特 {@code "@"}
     */
    String AT = "@";
    /**
     * 字符串常量：HTML 空格转义 {@code "&nbsp;" -> " "}
     */
    String HTML_NBSP = ToolXML.NBSP;
    /**
     * 字符串常量：HTML And 符转义 {@code "&amp;" -> "&"}
     */
    String HTML_AMP = ToolXML.AMP;
    /**
     * 字符串常量：HTML 双引号转义 {@code "&quot;" -> "\""}
     */
    String HTML_QUOTE = ToolXML.QUOTE;
    /**
     * 字符串常量：HTML 单引号转义 {@code "&apos" -> "'"}
     */
    String HTML_APOS = ToolXML.APOS;
    /**
     * 字符串常量：HTML 小于号转义 {@code "&lt;" -> "<"}
     */
    String HTML_LT = ToolXML.LT;
    /**
     * 字符串常量：HTML 大于号转义 {@code "&gt;" -> ">"}
     */
    String HTML_GT = ToolXML.GT;
    /**
     * 字符串常量：空 JSON {@code "{}"}
     */
    String EMPTY_OBJECT = "{}";
    /**
     * 字符串常量：空 JSON {@code "{}"}
     */
    String EMPTY_ARRAY = "[]";
    /**
     * 无效空白符
     */
    String INVALID_BLANK = " \t\n\r\f";
    /**
     * 大字母
     */
    String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 小字母
     */
    String LOWER = "abcdefghijklmnopqrstuvwxyz";
    /**
     * 系统行分割符
     */
    String SYSTEM_LINE_SEPARATOR = ((Supplier<String>) () -> {
        // avoid security issues
        StringWriter buf = new StringWriter(2);
        PrintWriter out = new PrintWriter(buf);
        out.println();
        return buf.toString();
    }).get();
    /**
     * ASCII字符对应的字符串缓存
     */
    InstanceSupplier<String[]> ASCII_CACHE = InstanceSupplier.of(() -> {
        String[] cache = new String[128];
        for (char c = 0; c < cache.length; c++) {
            cache[c] = String.valueOf(c);
        }
        return cache;
    });

    @Nonnull
    static BitSet getLetterNumberBitSet() {
        BitSet safeChars = new BitSet(256);
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            safeChars.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            safeChars.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            safeChars.set(i);
        }
        return safeChars;
    }
}
