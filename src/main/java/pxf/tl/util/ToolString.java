package pxf.tl.util;


import pxf.tl.api.PoolOfArray;
import pxf.tl.api.PoolOfCharacter;
import pxf.tl.api.PoolOfPattern;
import pxf.tl.api.PoolOfString;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Valid;
import pxf.tl.help.Whether;
import pxf.tl.iter.AnyIter;
import pxf.tl.lang.AsciiTableMatcher;
import pxf.tl.lang.TextBuilder;
import pxf.tl.text.CharSequenceUtil;
import pxf.tl.text.StrFormatter;
import pxf.tl.text.TextSimilarity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author potatoxf
 */
public final class ToolString extends CharSequenceUtil implements PoolOfString {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{[^{}$]+}");

    /**
     * Represents a failed index search.
     */
    public static final int INDEX_NOT_FOUND = -1;
    /**
     * The maximum size to which the padding constant(s) can expand.
     */
    private static final int PAD_LIMIT = 8192;
    /**
     * A regex pattern for recognizing blocks of whitespace characters. The apparent convolutedness of
     * the pattern serves the purpose of ignoring "blocks" consisting of only a single space: the
     * pattern is used only to normalize whitespace, condensing "blocks" down to a single space, thus
     * matching the same would likely cause a great many noop replacements.
     */
    private static final Pattern WHITESPACE_PATTERN =
            Pattern.compile("(?: |\\u00A0|\\s|[\\s&&[^ ]])\\s*");
    /**
     * Operations on char primitives and Character objects.
     *
     * <p>This class tries to handle {@code null} input gracefully. An exception will not be thrown
     * for a {@code null} input. Each method documents its behaviour in more detail.
     */
    private static final String[] CHAR_STRING_ARRAY = new String[128];

    static {
        for (char c = 0; c < CHAR_STRING_ARRAY.length; c++) {
            CHAR_STRING_ARRAY[c] = String.valueOf(c);
        }
    }

    private ToolString() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 缩写单词
     *
     * @param string                 输入字符串
     * @param oldSplit               老分割符
     * @param newSplit               新分割符，默认空字符串
     * @param maxPrefixLength        最大前缀字符串长度，默认0
     * @param maxSegmentLength       最大段字符串长度
     * @param totalAbbreviatedLength 总计缩写长度
     * @return 返回处理后的字符串
     * @throws IllegalArgumentException {@code totalAbbreviatedLength <= 0}，{@code maxSegmentLength >=
     *                                  totalAbbreviatedLength}或totalAbbreviatedLength小于总段数
     */
    public static String abbreviatedWordByAverageLength(
            String string,
            String oldSplit,
            String newSplit,
            int maxPrefixLength,
            int maxSegmentLength,
            int totalAbbreviatedLength) {
        String[] ss = splitSimply(string, oldSplit).toArray(new String[0]);
        if (totalAbbreviatedLength <= 0) {
            throw new IllegalArgumentException(
                    "The total string is not allowed to be less than or equal to 0");
        }
        if (maxSegmentLength >= totalAbbreviatedLength) {
            throw new IllegalArgumentException(
                    "The max segment length must less total abbreviated length");
        }
        newSplit = Valid.string(newSplit);
        maxPrefixLength = Valid.gtEq(maxPrefixLength, 0, 0);
        maxSegmentLength = Valid.gtEq(maxSegmentLength, 0, 0);
        if (ss.length == 1) {
            if (ss[0].length() < totalAbbreviatedLength) {
                return ss[0];
            } else {
                return ss[0].substring(0, totalAbbreviatedLength);
            }
        } else {
            StringBuilder sb = new StringBuilder(string.length());
            if (ss.length > 1) {
                if (ss[0].length() < maxPrefixLength) {
                    String[] temp = new String[ss.length - 1];
                    System.arraycopy(ss, 1, temp, 0, temp.length);
                    ss = temp;
                }
                // segment string length
                int ssl = totalAbbreviatedLength / ss.length;
                if (ssl <= 0) {
                    throw new IllegalArgumentException(
                            "The total string must be greater than the number of divisions");
                }
                // remaining string length
                int rest = totalAbbreviatedLength - ssl * ss.length;
                for (String v : ss) {
                    sb.append(newSplit);
                    int vl = v.length();
                    // distance length
                    int dl = vl - ssl;
                    if (vl <= maxSegmentLength) {
                        sb.append(v);
                        rest -= dl;
                    } else {
                        // less per segment string length
                        if (dl <= 0) {
                            sb.append(v, 0, vl);
                            rest -= dl;
                        } else {
                            if (rest >= 0) {
                                sb.append(v, 0, ssl);
                                if (dl >= rest) {
                                    sb.append(v, ssl, ssl + rest);
                                    rest = 0;
                                } else {
                                    sb.append(v, ssl, vl);
                                    rest -= dl;
                                }
                            } else {
                                sb.append(v, 0, ssl - 1);
                                rest += 1;
                            }
                        }
                    }
                }
                return sb.substring(newSplit.length());
            } else {
                throw new IllegalArgumentException("The input string is not allowed to be empty");
            }
        }
    }

    /**
     * 用分割符连接所有元素
     *
     * @param prefix       数组，默认空字符串
     * @param suffix       数组，默认空字符串
     * @param delimiter    分割符，默认逗号
     * @param factoryThrow 映射元素值
     * @param anyIter      元素数组
     * @return 返回连接后的字符串
     */
    public static <E> String concat(
            String prefix,
            String suffix,
            String delimiter,
            FunctionThrow<E, Object, RuntimeException> factoryThrow,
            AnyIter<E> anyIter) {
        if (anyIter == null) {
            return "";
        }
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        if (delimiter == null) {
            delimiter = ",";
        }
        StringBuilder sb = new StringBuilder(anyIter.size() * 4);
        for (E e : anyIter) {
            if (e == null) {
                continue;
            }
            Object value;
            if (factoryThrow != null) {
                value = factoryThrow.apply(e);
            } else {
                value = e;
            }
            if (value != null) {
                sb.append(prefix).append(value).append(suffix).append(delimiter);
            }
        }
        if (!delimiter.isEmpty()) {
            sb.setLength(sb.length() - delimiter.length());
        }
        return sb.toString();
    }

    /**
     * 清除前缀到目标字符串
     *
     * @param string 输入字符串
     * @param target 指定字符串
     * @return {@code String}，返回清除前缀的字符串，不存在则原样返回
     */
    public static String clearPrefixToTarget(String string, String target) {
        if (string == null) {
            return "";
        }
        int i = string.indexOf(target);
        if (i > 0) {
            return string.substring(i + target.length());
        }
        return string;
    }

    /**
     * 清除前导 {@code --}，{@code -}字符串
     *
     * @param string 输入字符串
     * @return {@code String}，如果为输入字符串为 {@code null}返回空字符串
     */
    public static String clearLeadingHyphens(String string) {
        return clearLeading(string, "--", "-");
    }

    /**
     * 清除前导字符串
     *
     * @param string   输入字符串
     * @param prefixes 要删除的前缀
     * @return {@code String}，如果为输入字符串为 {@code null}返回空字符串，如果没有前缀则原样返回
     */
    public static String clearLeading(String string, String... prefixes) {
        if (string == null) {
            return "";
        }
        for (String prefix : prefixes) {
            if (string.startsWith(prefix)) {
                return string.substring(prefix.length());
            }
        }
        return string;
    }

    /**
     * Remove the leading and trailing quotes from <code>string</code>. E.g. if string is '"one two"',
     * then 'one two' is returned.
     *
     * @param string The string from which the leading and trailing quotes should be removed.
     * @return The string without the leading and trailing quotes.
     */
    public static String clearLeadingAndTrailingQuotes(String string) {
        if (string == null) {
            return "";
        }
        int length = string.length();
        if (length > 1
                && string.startsWith("\"")
                && string.endsWith("\"")
                && string.substring(1, length - 1).indexOf('"') == -1) {
            string = string.substring(1, length - 1);
        }
        return string;
    }

    /**
     * 清理路径，格式{@code /a/b/c}或{@code a/b/c}或{@code /a/b/c/}或{@code a/b/c/}
     *
     * <p>{@code isPathSplitEnd==true} 则会在路径尾部添加{@code /} {@code isPathSplitEnd==false}
     * 则会在路径尾部去除{@code /}
     *
     * <p>{@code isPathSplitStart==true} {@code file://D:/a/b/c}--->{@code D:/a/b/c/} {@code
     * file:/a/b/c/}--->{@code /a/b/c/} {@code a/b/c}--->{@code /a/b/c/} {@code a/b/c}--->{@code
     * /a/b/c/}
     *
     * <p>{@code isPathSplitStart==false} {@code file://D:/a/b/c}--->{@code D:/a/b/c/} {@code
     * file:/a/b/c/}--->{@code a/b/c/} {@code a/b/c}--->{@code a/b/c/} {@code a/b/c}--->{@codea
     * a/b/c/}
     *
     * @param isPathSplitStart 是否为路径分割符开始
     * @param isPathSplitEnd   是否为路径分割符结尾
     * @param paths            路径
     * @return 返回合法化文件路径
     */
    public static String clearPath(
            boolean isPathSplitStart, boolean isPathSplitEnd, String... paths) {
        if (paths.length == 0) {
            return isPathSplitEnd || isPathSplitStart ? "/" : "";
        }
        StringBuilder sb = new StringBuilder(256);
        char c;
        boolean isLastPathSplit = !isPathSplitStart;
        int s = paths[0].indexOf(":");
        if (s > 0) {
            String fs = trim(paths[0].substring(0, s), "\\/");
            int d = paths[0].indexOf(":", s + 1);
            if (d > 0) {
                String ss = trim(paths[0].substring(s + 1, d), "\\/");
                if (ss.length() == 1) {
                    sb.append(ss.toUpperCase()).append(":/");
                    isLastPathSplit = true;
                }
                s = d + 1;
            } else {
                if (fs.length() == 1) {
                    sb.append(fs.toUpperCase()).append(":/");
                    isLastPathSplit = true;
                }
                s += 1;
            }
            paths[0] = paths[0].substring(s);
        } else {
            isLastPathSplit = !isPathSplitStart;
        }
        for (String path : paths) {
            if (Whether.empty(path)) {
                continue;
            }
            if (!isLastPathSplit) {
                sb.append('/');
                isLastPathSplit = true;
            }
            for (int j = 0; j < path.length(); j++) {
                c = path.charAt(j);
                if (c == '\\' || c == '/') {
                    if (!isLastPathSplit) {
                        sb.append('/');
                        isLastPathSplit = true;
                    }
                } else {
                    isLastPathSplit = false;
                    sb.append(c);
                }
            }
        }
        if (isLastPathSplit != isPathSplitEnd) {
            if (isPathSplitEnd) {
                sb.append('/');
            } else {
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
            }
        }
        return sb.toString().replaceAll("%20", " ").trim();
    }

    /**
     * 计算文本显示长度，默认半角为 {@code 1}，全角为 {@code 2}
     *
     * @param str 文本
     * @return 返回计算后显示长度
     */
    public static double calculateStringLength(String str) {
        return calculateStringLength(str, 1, 2, 0);
    }

    /**
     * 计算文本显示长度
     *
     * @param text          文本
     * @param half          半角长度
     * @param full          全角长度
     * @param defaultLength 默认长度，当没有字符时
     * @return 返回计算后显示长度
     */
    public static double calculateStringLength(
            String text, double half, double full, double defaultLength) {
        if (text == null || text.length() == 0) {
            return defaultLength;
        }
        double len = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (Whether.quadEmChar(text.codePointAt(i))) {
                len = len + full;
            } else {
                len = len + half;
            }
        }
        return len;
    }

    /**
     * 计算字符串比例长度
     *
     * @param strings 字符串数组
     * @return 返回字符串占总字符串的比例长度
     */
    public static int[] calculateStringScaleLength(String[] strings) {
        double[] lengthScale = calculateStringLengthScale(strings);
        int[] result = new int[strings.length];
        int u = 0, d = 0;
        for (int j = 0; j < strings.length; j++) {
            double v = strings[j].length() * lengthScale[j];
            int i = (int) v;
            double diff = v - i;
            if (diff < 1e-10) {
                if (d > u) {
                    result[j] = i - 1;
                    d++;
                } else if (d < u) {
                    result[j] = i + 1;
                    u++;
                } else {
                    result[j] = i;
                }
            } else {
                int rd = (int) Math.round(diff);
                if (rd == 1) {
                    result[j] = i + 1;
                    u++;
                } else {
                    d++;
                }
            }
        }
        return result;
    }

    /**
     * 计算字符串长度比例
     *
     * @param strings 字符串数组
     * @return 返回字符串占总字符串长度的比例
     */
    public static double[] calculateStringLengthScale(String... strings) {
        double[] result = new double[strings.length];
        double tl = 0;
        for (String str : strings) {
            tl += str.length();
        }
        for (int i = 0; i < strings.length; i++) {
            result[i] = strings[i].length() / tl;
        }
        return result;
    }

    /**
     * 获取带有小数的数字
     *
     * @param input        输入字符串
     * @param defaultValue 找不到时的默认值
     * @return 数字字符串
     */
    public static String extractDecimalNumber(String input, String defaultValue) {
        return extractDecimalNumber(input, 1, defaultValue);
    }

    /**
     * 提取提取带有小数的数字
     *
     * @param input        输入字符串
     * @param findCount    提取第几个数字
     * @param defaultValue 找不到时的默认值
     * @return 数字字符串
     */
    public static String extractDecimalNumber(String input, int findCount, String defaultValue) {
        return extractNumber(input, findCount, true, defaultValue);
    }

    /**
     * 提取整数
     *
     * @param input        输入字符串
     * @param defaultValue 找不到时的默认值
     * @return 数字字符串
     */
    public static String extractIntegerNumber(String input, String defaultValue) {
        return extractIntegerNumber(input, 1, defaultValue);
    }

    /**
     * 提取整数
     *
     * @param input        输入字符串
     * @param findCount    提取第几个数字
     * @param defaultValue 找不到时的默认值
     * @return 数字字符串
     */
    public static String extractIntegerNumber(String input, int findCount, String defaultValue) {
        return extractNumber(input, findCount, false, defaultValue);
    }

    /**
     * 提取数字
     *
     * @param input        输入字符串
     * @param findCount    提取第几个数字
     * @param defaultValue 找不到时的默认值
     * @return 数字字符串
     */
    private static String extractNumber(
            String input, int findCount, boolean isDecimal, String defaultValue) {
        if (findCount <= 0) {
            throw new IllegalArgumentException("The find count must be greater 0");
        }
        if (input == null) {
            return defaultValue;
        }
        int len = input.length();
        if (len == 0) {
            return defaultValue;
        }
        int startIndex = -1;
        int count = 0;
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                if (startIndex == -1) {
                    startIndex = i;
                }
                continue;
            }
            if (startIndex != -1 && !(isDecimal && c == '.')) {
                count++;
                if (count != findCount) {
                    startIndex = -1;
                    continue;
                }
                char pre = input.charAt(startIndex);
                if (AsciiTableMatcher.isMatcherExceptAsciiChar(pre, AsciiTableMatcher.ARITHMETIC_ADD_SUB)) {
                    startIndex--;
                }
                return input.substring(startIndex, i);
            }
        }
        if (startIndex != -1) {
            char pre = input.charAt(startIndex - 1);
            if (AsciiTableMatcher.isMatcherExceptAsciiChar(pre, AsciiTableMatcher.ARITHMETIC_ADD_SUB)) {
                startIndex--;
            }
            return input.substring(startIndex);
        }
        return defaultValue;
    }

    /**
     * 提取文件路径中的文件名
     *
     * @param filePath 文件路径
     * @return 提取文件路径中的文件名
     */
    public static String extractFileName(String filePath) {
        int si = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (si < 0) {
            return filePath;
        }
        return filePath.substring(si + 1);
    }

    /**
     * 提取文件路径中的文件基础名
     *
     * @param filePath 文件路径
     * @return 提取文件路径中的文件基础名
     */
    public static String extractFileBaseName(String filePath) {
        int si = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        int di = filePath.lastIndexOf('.');
        if (di < si) {
            di = filePath.length();
        }
        if (si < 0) {
            si = -1;
        }
        return filePath.substring(si + 1, di);
    }

    /**
     * 提取文件路径中的文件扩展
     *
     * @param filePath 文件路径
     * @return 提取文件路径中的文件扩展
     */
    public static String extractFileExtension(String filePath) {
        int di = filePath.lastIndexOf('.');
        if (di < 0) {
            return "";
        }
        int si = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (si < 0) {
            si = 0;
        }
        if (di < si) {
            return "";
        }
        return filePath.substring(di + 1);
    }

    /**
     * 简单切分字符串，不进行其它操作
     *
     * @param input     输入字符串
     * @param splitChar 切分字符
     * @return 返回切分后的字符串数组
     */
    public static List<String> splitSimply(String input, String splitChar) {
        if (input == null) {
            return Collections.emptyList();
        }
        int length = input.length();
        if (length == 0) {
            return Collections.emptyList();
        }
        if (splitChar == null) {
            return Collections.singletonList(input);
        }
        int scLength = splitChar.length();
        if (scLength == 0) {
            return Collections.singletonList(input);
        }
        int count = 1;
        int si = 0, i;
        List<String> list = new ArrayList<String>();
        while ((i = input.indexOf(splitChar, si)) != -1) {
            list.add(input.substring(si, i));
            si = i + scLength;
        }
        list.add(input.substring(si));
        return list;
    }

    /**
     * 切分字符串，清除空字符串
     *
     * @param input     输入字符串
     * @param splitChar 切分字符
     * @return 返回切分后的字符串数组
     */
    public static List<String> splitSafely(String input, String splitChar) {
        if (input == null) {
            return Collections.emptyList();
        }
        int length = input.length();
        if (length == 0) {
            return Collections.emptyList();
        }
        if (splitChar == null) {
            return Collections.singletonList(input);
        }
        int scLength = splitChar.length();
        if (scLength == 0) {
            return Collections.singletonList(input);
        }
        int count = 1;
        int si = 0, i;
        List<String> list = new ArrayList<String>();
        while ((i = input.indexOf(splitChar, si)) != -1) {
            String substring = input.substring(si, i);
            if (Whether.empty(substring)) {
                continue;
            } else {
                list.add(substring);
            }
            si = i + scLength;
        }
        list.add(input.substring(si));
        return list;
    }

    /**
     * 切分字符串转成Integer数组
     *
     * @param input     输入字符串
     * @param splitChar 切分字符
     * @return 返回切分后的字符串数组
     */
    public static List<Integer> splitToInteger(String input, String splitChar) {
        List<String> strings = splitSafely(input, splitChar);
        List<Integer> result = new ArrayList<Integer>(strings.size());
        for (String string : strings) {
            try {
                int value = Integer.parseInt(string);
                result.add(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    /**
     * 自定义分割多层级字符串
     *
     * @param input      输入字符串
     * @param finalIndex 最终分割数组索引
     * @param splits     多个分割符
     * @return 返回字符串列表
     */
    public static List<String> splitMultilayerSegment(
            String input, int finalIndex, String... splits) {
        List<String> list = new ArrayList<String>();
        splitCustomMultilayerSegmentWithRecursion(
                String::split, list, new String[]{input}, splits, 0, finalIndex);
        return list;
    }

    /**
     * 自定义分割多层级字符串，列如：对于字符串1-2_A-B,2-3_C-D,3-4_E-F，执行该方法，分隔符为{@code , _ -}指定目标索引为{@code 1}，执行顺序如下：
     *
     * <pre>
     *     1、 1-2_A-B  2-3_C-D  3-4_E-F
     *     2、 1-2  A-B  2-3  C-D  3-4  E-F
     *     3、 1 2  A B  2 3  C D  3 4  E F
     *     结果： 2 B 3 D 4 F
     * </pre>
     *
     * @param splitCustomStringHandler 自定义分割字符串处理器
     * @param container                字符串列表容器
     * @param inputs                   输入字符串数组
     * @param splits                   多个分割符
     * @param splitIndex               分割索引
     * @param finalIndex               最终分割数组索引
     */
    private static void splitCustomMultilayerSegmentWithRecursion(
            final BiFunction<String, String, String[]> splitCustomStringHandler,
            final List<String> container,
            final String[] inputs,
            final String[] splits,
            final int splitIndex,
            final int finalIndex) {
        if (splitIndex < 0 || splitIndex >= splits.length) {
            throw new IllegalArgumentException();
        } else if (splitIndex == splits.length - 1) {
            for (String input : inputs) {
                String[] arr = splitCustomStringHandler.apply(input, splits[splitIndex]);
                if (finalIndex < arr.length) {
                    container.add(arr[finalIndex]);
                }
            }
        } else {
            for (String input : inputs) {
                splitCustomMultilayerSegmentWithRecursion(
                        splitCustomStringHandler,
                        container,
                        splitCustomStringHandler.apply(input, splits[splitIndex]),
                        splits,
                        splitIndex + 1,
                        finalIndex);
            }
        }
    }

    /**
     * @param input
     * @param middleRegexp
     * @return
     */
    public static String[] cutToTriplicate(final String input, final String middleRegexp) {
        return cutToTriplicate(input, Pattern.compile(middleRegexp));
    }

    /**
     * @param input
     * @param middleRegexp
     * @return
     */
    public static String[] cutToTriplicate(final String input, final Pattern middleRegexp) {
        Matcher matcher = middleRegexp.matcher(input);
        if (matcher.find()) {
            int start = matcher.start();
            String target = matcher.group();
            return new String[]{
                    input.substring(0, start), target, input.substring(start + target.length())
            };
        } else {
            return new String[]{input, PoolOfString.EMPTY, PoolOfString.EMPTY};
        }
    }

    /**
     * 清除前字符串
     *
     * @param input    输入字符串
     * @param trimChar 要清除字符的字符串
     * @return 返回清除后的字符串
     */
    public static String trimStartsWith(String input, String trimChar) {
        return trim(input, true, false, trimChar);
    }

    /**
     * 清除后字符串
     *
     * @param input    输入字符串
     * @param trimChar 要清除字符的字符串，如果为null则使用默认 {@code String.trim()}，否则清除前后包含该字符
     * @return 返回清除后的字符串
     */
    public static String trimEndsWith(String input, String trimChar) {
        return trim(input, false, true, trimChar);
    }

    /**
     * 清除前后字符串
     *
     * @param input    输入字符串
     * @param trimChar 要清除字符的字符串，如果为null则使用默认 {@code String.trim()}，否则清除前后包含该字符
     * @return 返回清除后的字符串
     */
    public static String trim(String input, String trimChar) {
        return trim(input, true, true, trimChar);
    }

    /**
     * 清除前后字符串
     *
     * @param input        输入字符串
     * @param isTrimPrefix 是否清除前缀
     * @param isTrimSuffix 是否清除后缀
     * @param trimChar     要清除字符的字符串，如果为null则使用默认 {@code String.trim()}，否则清除前后包含该字符
     * @return 返回清除后的字符串
     */
    private static String trim(
            String input, boolean isTrimPrefix, boolean isTrimSuffix, String trimChar) {
        if (input == null) {
            return "";
        }
        if (!isTrimPrefix && !isTrimSuffix) {
            return input;
        }
        if (trimChar == null) {
            return input.trim();
        }
        int p = 0, s = input.length() - 1;
        if (isTrimPrefix) {
            while (p <= s && trimChar.indexOf(input.codePointAt(p)) >= 0) {
                p++;
            }
        }
        if (isTrimSuffix) {
            while (p <= s && trimChar.indexOf(input.codePointAt(s)) >= 0) {
                s--;
            }
        }
        if (s < p) {
            return "";
        }
        return input.substring(p, s + 1);
    }

    /**
     * 解析特殊字符 {@code \t\n\r\f}
     *
     * @param c 字符
     * @return 返回特殊字符，如果不是则原样返回
     */
    public static char resolveSpecialCharacters(char c) {
        switch (c) {
            case 't':
                return '\t';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 'f':
                return '\f';
            case 'b':
                return '\b';
            default:
                return c;
        }
    }

    /**
     * 数组表达式解析
     *
     * @param input 数组表达式
     * @return 返回解析处理的数组
     * @throws IllegalArgumentException 当数组表达式错误时
     * @see #resolveArrayExpression(CharSequence, boolean, char, char, String, String)
     */
    public static String[] resolveArrayExpression(CharSequence input) {
        return resolveArrayExpression(input, ',');
    }

    /**
     * 数组表达式解析
     *
     * @param input 数组表达式
     * @return 返回解析处理的数组
     * @throws IllegalArgumentException 当数组表达式错误时
     * @see #resolveArrayExpression(CharSequence, boolean, char, char, String, String)
     */
    public static String[] resolveArrayExpression(CharSequence input, char splitChar) {
        return resolveArrayExpression(input, false, splitChar, '\\', "[", "]");
    }

    /**
     * 数组表达式解析
     *
     * <p>支持特殊字符 {@code \t\n\r\f}
     *
     * <p>括号可有可无
     *
     * <p>引号内除特殊字符外不用转义
     *
     * <p>引号内除特殊字符外不用转义，除开头括号
     *
     * <table>
     * <tbody>
     * <tr>
     * <td>[a,b,c]</td>
     * <td><code>a  b  c</code></td>
     * </tr>
     * <tr>
     * <td>[a a,b b,c c]</td>
     * <td><code>aa  bb  cc</code></td>
     * </tr>
     * <tr>
     * <td>["a a","b b",c c]</td>
     * <td><code>a a  b b  cc</code></td>
     * </tr>
     * <tr>
     * <td>["a 'a'","b b",'c c']</td>
     * <td><code>a 'a'  b b  c c</code></td>
     * </tr>
     * <tr>
     * <td>["a \"a\"","b b",'c c']</td>
     * <td><code>a "a"  b b  c c</code></td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param input           数组表达式
     * @param splitChar       指定分隔符
     * @param escapeChar      指定转义符
     * @param openingBrackets 左括号
     * @param closingBrackets 右括号
     * @return 返回解析处理的数组
     * @throws IllegalArgumentException 当数组表达式错误时
     */
    public static String[] resolveArrayExpression(
            final CharSequence input,
            final boolean isIgnoredQuote,
            final char splitChar,
            final char escapeChar,
            final String openingBrackets,
            final String closingBrackets) {
        if (input == null) {
            return new String[0];
        }
        int length = input.length();
        if (length == 0) {
            return new String[0];
        }
        StringBuilder sb = new StringBuilder(length);
        List<String> list = new ArrayList<String>(length / 20 + 10);
        int bracketsCount = 0;
        boolean escape = false, doubleQuotes = false, singleQuotes = false, nextSplit = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            // 跳过引号内的空白符
            if (!doubleQuotes && !singleQuotes && " \t\n\r\f".indexOf(c) != -1) {
                continue;
            }
            // 跳过开头括号
            if (Whether.empty(list) && sb.length() == 0 && openingBrackets.indexOf(c) != -1) {
                continue;
            }
            // 记录转义
            if (c == escapeChar) {
                // 记录转义
                if (escape) {
                    sb.append(escapeChar);
                }
                escape = !escape;
                continue;
            }
            // 为转义字符，直接作为普通字符串
            if (escape) {
                sb.append(resolveSpecialCharacters(c));
                escape = false;
                continue;
            }
            // 如果没有忽略引号和不在括号内则处理引号
            if (!isIgnoredQuote && bracketsCount == 0) {
                if (c == '"') {
                    if (singleQuotes) {
                        sb.append('"');
                    } else if (!(doubleQuotes = !doubleQuotes)) {
                        nextSplit = true;
                    }
                    continue;
                }
                if (!isIgnoredQuote && c == '\'') {
                    if (doubleQuotes) {
                        sb.append('\'');
                    } else if (!(singleQuotes = !singleQuotes)) {
                        nextSplit = true;
                    }
                    continue;
                }
            }
            // 如果没有在在引号内则处理括号
            if (!singleQuotes && !doubleQuotes) {
                if (openingBrackets.indexOf(c) != -1) {
                    bracketsCount++;
                } else if (closingBrackets.indexOf(c) != -1) {
                    bracketsCount--;
                }
                if (bracketsCount < 0) {
                    continue;
                }
            }

            // 不在引号内，且不在括号内，并且是分割符则获取该值
            if (!singleQuotes && !doubleQuotes && bracketsCount == 0 && c == splitChar) {
                ToolCollection.addStringAndClear(list, sb);
                nextSplit = false;
            } else {
                // 下一个期望的是分割符，则报错
                if (nextSplit) {
                    throw new IllegalArgumentException(
                            "Must be an array separator after the end of the quotation mark");
                }
                sb.append(c);
            }
        }
        if (doubleQuotes) {
            throw new IllegalArgumentException("Unterminated double quotation mark");
        }
        if (singleQuotes) {
            throw new IllegalArgumentException("unterminated single quotation mark");
        }
        if (bracketsCount != 0 && bracketsCount != -1) {
            if (bracketsCount > 0) {
                throw new IllegalArgumentException("Unterminated right brackets mark");
            } else {
                throw new IllegalArgumentException("Unterminated left brackets mark");
            }
        }
        if (sb.length() != 0) {
            ToolCollection.addStringAndClear(list, sb);
        }
        return list.toArray(new String[0]);
    }

    /**
     * Map表达式解析
     *
     * <p>支持特殊字符 {@code \t\n\r\f}
     *
     * <p>括号可有可无
     *
     * <p>引号内除特殊字符外不用转义
     *
     * <p>引号内除特殊字符外不用转义，除开头括号 格式如下：
     *
     * <pre>{a:1,b:2,c:3}</pre>
     *
     * <pre>a=1 b=2 c=3</pre>
     *
     * <pre>{a:1,b:2,c:[1,2,3,4,5]}</pre>
     *
     * <pre>a=1  b=2 c=[1,2,3,4,5]</pre>
     *
     * <pre>{a:1,b:{d:1,f:=2},c:[1,2,3,4,5]}</pre>
     *
     * <pre>a=1  b={d:1,f:=2} c=[1,2,3,4,5]</pre>
     *
     * @param input Map表达式
     * @return {@code Map<String, String>}
     * @throws IllegalArgumentException 当数组表达式错误时
     */
    public static Map<String, String> resolveMapExpression(final CharSequence input) {
        return resolveMapExpression(input, '=', ';', '\\', "{[", "]}");
    }

    /**
     * Map表达式解析
     *
     * <p>支持特殊字符 {@code \t\n\r\f}
     *
     * <p>括号可有可无
     *
     * <p>引号内除特殊字符外不用转义
     *
     * <p>引号内除特殊字符外不用转义，除开头括号 格式如下：
     *
     * <pre>{a:1,b:2,c:3}</pre>
     *
     * <pre>a=1 b=2 c=3</pre>
     *
     * <pre>{a:1,b:2,c:[1,2,3,4,5]}</pre>
     *
     * <pre>a=1  b=2 c=[1,2,3,4,5]</pre>
     *
     * <pre>{a:1,b:{d:1,f:=2},c:[1,2,3,4,5]}</pre>
     *
     * <pre>a=1  b={d:1,f:=2} c=[1,2,3,4,5]</pre>
     *
     * @param input           Map表达式
     * @param pairChar        指定键值分隔符
     * @param splitChar       指定分隔符
     * @param escapeChar      指定转义符
     * @param openingBrackets 左括号
     * @param closingBrackets 右括号
     * @return {@code Map<String, String>}
     * @throws IllegalArgumentException 当数组表达式错误时
     */
    public static Map<String, String> resolveMapExpression(
            final CharSequence input,
            final char pairChar,
            final char splitChar,
            final char escapeChar,
            final String openingBrackets,
            final String closingBrackets) {
        String[] strings =
                resolveArrayExpression(
                        input, true, splitChar, escapeChar, openingBrackets, closingBrackets);
        Map<String, String> map = new HashMap<>(strings.length, 1);
        for (String string : strings) {
            String[] pair =
                    resolveArrayExpression(
                            string, false, pairChar, escapeChar, openingBrackets, closingBrackets);
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            } else if (pair.length == 1) {
                map.put(pair[0], null);
            }
        }
        return map;
    }

    /**
     * 将给定的{@code String}表示形式解析为{@link Locale}，格式如下：
     *
     * <ul>
     *   <li>en
     *   <li>en_US
     *   <li>en US
     *   <li>en-US
     * </ul>
     *
     * @param localeName 语言环境名称
     * @return 相应的{@code Locale}实例，如果没有，则为{@code null}
     * @throws IllegalArgumentException 如果在无效的语言环境规范的情况下
     */
    public static Locale parseLocaleString(String localeName) {
        for (int i = 0; i < localeName.length(); i++) {
            char ch = localeName.charAt(i);
            if (ch != ' ' && ch != '_' && ch != '-' && ch != '#' && !Character.isLetterOrDigit(ch)) {
                throw new IllegalArgumentException("The locale must be ' ','_','-','#',letter or digit");
            }
        }
        StringTokenizer st = new StringTokenizer(localeName, "_ ");
        List<String> tokens = new LinkedList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            tokens.add(token);
        }
        int size = tokens.size();
        if (size == 1) {
            Locale resolved = Locale.forLanguageTag(tokens.get(0));
            if (Whether.noEmpty(resolved.getLanguage())) {
                return resolved;
            }
        }
        String language = (size > 0 ? tokens.get(0) : "");
        String country = (size > 1 ? tokens.get(1) : "");
        String variant = "";
        if (size > 2) {
            // There is definitely a variant, and it is everything after the country
            // code sans the separator between the country code and the variant.
            int endIndexOfCountryCode = localeName.indexOf(country, language.length()) + country.length();
            // Strip off any leading '_' and whitespace, what's left is the variant.
            variant = localeName.substring(endIndexOfCountryCode).trim();
            int index = 0;
            for (int i = 0; i < variant.length(); i++) {
                if (variant.charAt(i) == '_') {
                    index++;
                } else {
                    break;
                }
            }
            variant = variant.substring(index);
        }
        if (Whether.empty(variant) && country.startsWith("#")) {
            variant = country;
            country = "";
        }
        return (language.length() > 0 ? new Locale(language, country, variant) : null);
    }

    /**
     * 替换占位符
     *
     * @param template 模板
     * @param params   参数
     * @return 返回处理后的值
     */
    public static String replacePlaceholder(String template, Map<String, ?> params) {
        return replacePlaceholder(
                template,
                s -> {
                    if (params == null) {
                        return null;
                    }
                    Object value = params.get(s[0]);
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                });
    }

    /**
     * 替换占位符
     *
     * @param template      模板
     * @param paramSupplier 参数
     * @return 返回处理后的值
     */
    public static String replacePlaceholder(
            String template, Function<String[], String> paramSupplier) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        while (matcher.find()) {
            String ph = matcher.group();
            String name = ph.substring(2, ph.length() - 1);
            String[] values = ToolString.split(name, ':');
            String value = Valid.string(paramSupplier.apply(values));
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 替换分割符
     *
     * @param string       字符串，如果字符串为空则返回空字符串
     * @param splitChar    分割符，如果为空则直接返回输入字符串
     * @param newSplitChar 新的分割符，如果为空则默认去除分割符
     * @return 返回处理后的字符串
     */
    public static String replaceSplitChar(String string, String splitChar, String newSplitChar) {
        if (string == null) {
            return "";
        }
        int length = string.length();
        if (length == 0) {
            return "";
        }
        if (splitChar == null || splitChar.length() == 0) {
            return string;
        }
        if (newSplitChar == null) {
            newSplitChar = "";
        }
        StringBuilder sb = new StringBuilder(length);
        boolean b = false;
        for (int i = 0; i < length; i++) {
            int cp = string.codePointAt(i);
            if (splitChar.indexOf(cp) < 0) {
                if (b) {
                    sb.append(newSplitChar);
                }
                for (char c : Character.toChars(cp)) {
                    sb.append(c);
                }
                b = false;
            } else {
                b = true;
            }
        }
        return sb.toString();
    }

    /**
     * 解析起始标识字符和结束标识字符中的标识位和后面数据
     *
     * <p>标识位是由标识字符符囊括起来，起始位必须是标识字符，后标识字符
     *
     * <p>标识位必须满足{@link PoolOfPattern#TOKEN}，否则标识位返回null 数据位返回数据，如果没有标识位，标识位返回null，数据位返回整个数据，
     * 如果有标识位但不合格则则标识位返回null否则正常返回标识位，数据位返回除了标识位后的数据。
     *
     * @param charSequence 字符序列
     * @param sign         字符标识
     * @return 返回标识位和数据位
     */
    public static String[] parseStartSignAndData(CharSequence charSequence, char sign) {
        return parseStartSignAndData(charSequence, sign, sign);
    }

    /**
     * 解析起始标识字符和结束标识字符中的标识位和后面数据
     *
     * <p>标识位是由标识字符符囊括起来，起始位必须是标识字符，后标识字符
     *
     * <p>标识位必须满足{@link PoolOfPattern#TOKEN}，否则标识位返回null 数据位返回数据，如果没有标识位，标识位返回null，数据位返回整个数据，
     * 如果有标识位但不合格则则标识位返回null否则正常返回标识位，数据位返回除了标识位后的数据。
     *
     * @param charSequence 字符序列
     * @param start        开始字符标识
     * @param end          结束字符标识
     * @return 返回标识位和数据位
     */
    public static String[] parseStartSignAndData(CharSequence charSequence, char start, char end) {
        if (charSequence != null) {
            String string = charSequence.toString().trim();
            if (string.length() > 0) {
                char c = string.charAt(0);
                if (c != '\\' && c == start) {
                    int i = 0;
                    while ((i = string.indexOf(end, i + 1)) > 0) {
                        char ch;
                        boolean escape = false;
                        for (int j = i - 1; j > 0; j--) {
                            ch = string.charAt(j);
                            if (ch == '\\') {
                                escape = !escape;
                            } else {
                                break;
                            }
                        }
                        if (!escape) {
                            break;
                        }
                    }
                    if (i > 0) {
                        String sign = string.substring(1, i).trim();
                        String data = string.substring(i + 1).trim();
                        if (PoolOfPattern.TOKEN.matcher(sign).matches()) {
                            return new String[]{sign, data};
                        } else {
                            return new String[]{null, data};
                        }
                    }
                }
            }
            return new String[]{null, string};
        }
        return new String[]{null, ""};
    }

    /**
     * 按固定长度处理字符串
     *
     * @param input     显示对象
     * @param fixLength 固定长度
     * @param pad       填充字符
     * @param isLeft    是否靠左
     * @return 返回处理后的字符串
     */
    public static String fixed(Object input, int fixLength, char pad, boolean isLeft) {
        StringBuilder stringBuilder = new StringBuilder();
        fixed(stringBuilder, input, fixLength, pad, isLeft);
        return stringBuilder.toString();
    }

    /**
     * 按固定长度处理字符串
     *
     * @param container 字符串容器
     * @param input     显示对象
     * @param fixLength 固定长度
     * @param pad       填充字符
     * @param isLeft    是否靠左
     */
    public static void fixed(
            StringBuilder container, Object input, int fixLength, char pad, boolean isLeft) {
        String string = input.toString();
        if (fixLength <= string.length()) {
            container.append(string);
            return;
        }
        if (isLeft) {
            container.append(string);
            repeat(container, String.valueOf(pad), fixLength - string.length());
        } else {
            repeat(container, String.valueOf(pad), fixLength - string.length());
            container.append(string);
        }
    }

    public static String toParamCalledString(String var, int n) {
        if (n > 0) {
            StringBuilder stringBuilder = new StringBuilder(60);
            toParamCalledString(stringBuilder, var, n);
            return stringBuilder.toString();
        } else {
            return "()";
        }
    }

    public static void toParamCalledString(StringBuilder container, String var, int n) {
        if (n > 0) {
            container.append("(").append(var).append(1);
            for (int i = 1; i < n; i++) {
                container.append(", ").append(var).append(i + 1);
            }
            container.append(")");
        } else {
            container.append("()");
        }
    }

    public static String toParamCalledString(Object... args) {
        if (Whether.noEmpty(args)) {
            StringBuilder stringBuilder = new StringBuilder(60);
            toParamCalledString(stringBuilder, args);
            return stringBuilder.toString();
        }
        return "()";
    }

    public static void toParamCalledString(StringBuilder container, Object... args) {
        if (args != null && args.length > 0) {
            container.append("(").append(args[0].getClass().getSimpleName());
            for (int i = 1; i < args.length; i++) {
                container.append(", ").append(args[i].getClass().getSimpleName());
            }
            container.append(")");
        } else {
            container.append("()");
        }
    }

    public static void toParamCalledString(StringBuilder container, Class<?>... args) {
        if (args != null && args.length > 0) {
            container.append("(").append(args[0].getSimpleName());
            for (int i = 1; i < args.length; i++) {
                container.append(", ").append(args[i].getSimpleName());
            }
            container.append(")");
        } else {
            container.append("()");
        }
    }

    public static String toParamCalledString(Class<?>[] args, String[] names) {

        if (args != null) {
            if (args.length > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                toParamCalledString(stringBuilder, args, names);
                return stringBuilder.toString();
            }
        }
        return "()";
    }

    public static void toParamCalledString(StringBuilder container, Class<?>[] args, String[] names) {
        if (args != null && args.length > 0) {
            container.append("(").append(args[0].getSimpleName()).append(" ").append(names[0]);
            for (int i = 1; i < args.length; i++) {
                container.append(", ").append(args[i].getSimpleName()).append(" ").append(names[i]);
            }
            container.append(")");
        } else {
            container.append("()");
        }
    }

    // Trim
    // -----------------------------------------------------------------------

    /**
     * Removes control characters (char &lt;= 32) from both ends of this String, handling {@code null}
     * by returning {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}. Trim removes start and end characters
     * &lt;= 32. To strip whitespace use {@link #strip(String)}.
     *
     * <p>To trim your choice of characters, use the {@link #strip(String, String)} methods.
     *
     * <pre>
     * StringHelper.trim(null)          = null
     * StringHelper.trim("")            = ""
     * StringHelper.trim("     ")       = ""
     * StringHelper.trim("abc")         = "abc"
     * StringHelper.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /**
     * Removes control characters (char &lt;= 32) from both ends of this String returning {@code null}
     * if the String is empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}. Trim removes start and end characters
     * &lt;= 32. To strip whitespace use {@link #stripToNull(String)}.
     *
     * <pre>
     * StringHelper.trimToNull(null)          = null
     * StringHelper.trimToNull("")            = null
     * StringHelper.trimToNull("     ")       = null
     * StringHelper.trimToNull("abc")         = "abc"
     * StringHelper.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, {@code null} if only chars &lt;= 32, empty or null String input
     */
    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return Whether.empty(ts) ? null : ts;
    }

    /**
     * Removes control characters (char &lt;= 32) from both ends of this String returning an empty
     * String ("") if the String is empty ("") after the trim or if it is {@code null}.
     *
     * <p>The String is trimmed using {@link String#trim()}. Trim removes start and end characters
     * &lt;= 32. To strip whitespace use {@link #stripToEmpty(String)}.
     *
     * <pre>
     * StringHelper.trimToEmpty(null)          = ""
     * StringHelper.trimToEmpty("")            = ""
     * StringHelper.trimToEmpty("     ")       = ""
     * StringHelper.trimToEmpty("abc")         = "abc"
     * StringHelper.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     */
    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * Strips whitespace from the start and end of a String.
     *
     * <p>This is similar to {@link #trim(String)} but removes whitespace. Whitespace is defined by
     * {@link Character#isWhitespace(char)}.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.strip(null)     = null
     * StringHelper.strip("")       = ""
     * StringHelper.strip("   ")    = ""
     * StringHelper.strip("abc")    = "abc"
     * StringHelper.strip("  abc")  = "abc"
     * StringHelper.strip("abc  ")  = "abc"
     * StringHelper.strip(" abc ")  = "abc"
     * StringHelper.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to remove whitespace from, may be null
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(final String str) {
        return strip(str, null);
    }

    /**
     * Strips whitespace from the start and end of a String returning {@code null} if the String is
     * empty ("") after the strip.
     *
     * <p>This is similar to {@link #trimToNull(String)} but removes whitespace. Whitespace is defined
     * by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.stripToNull(null)     = null
     * StringHelper.stripToNull("")       = null
     * StringHelper.stripToNull("   ")    = null
     * StringHelper.stripToNull("abc")    = "abc"
     * StringHelper.stripToNull("  abc")  = "abc"
     * StringHelper.stripToNull("abc  ")  = "abc"
     * StringHelper.stripToNull(" abc ")  = "abc"
     * StringHelper.stripToNull(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to be stripped, may be null
     * @return the stripped String, {@code null} if whitespace, empty or null String input
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return Whether.empty(str) ? null : str;
    }

    // StripAll
    // -----------------------------------------------------------------------

    /**
     * Strips whitespace from the start and end of a String returning an empty String if {@code null}
     * input.
     *
     * <p>This is similar to {@link #trimToEmpty(String)} but removes whitespace. Whitespace is
     * defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.stripToEmpty(null)     = ""
     * StringHelper.stripToEmpty("")       = ""
     * StringHelper.stripToEmpty("   ")    = ""
     * StringHelper.stripToEmpty("abc")    = "abc"
     * StringHelper.stripToEmpty("  abc")  = "abc"
     * StringHelper.stripToEmpty("abc  ")  = "abc"
     * StringHelper.stripToEmpty(" abc ")  = "abc"
     * StringHelper.stripToEmpty(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to be stripped, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     */
    public static String stripToEmpty(final String str) {
        return str == null ? EMPTY : strip(str, null);
    }

    /**
     * Strips any of a set of characters from the start and end of a String. This is similar to {@link
     * String#trim()} but allows the characters to be stripped to be controlled.
     *
     * <p>A {@code null} input String returns {@code null}. An empty string ("") input returns the
     * empty string.
     *
     * <p>If the stripChars String is {@code null}, whitespace is stripped as defined by {@link
     * Character#isWhitespace(char)}. Alternatively use {@link #strip(String)}.
     *
     * <pre>
     * StringHelper.strip(null, *)          = null
     * StringHelper.strip("", *)            = ""
     * StringHelper.strip("abc", null)      = "abc"
     * StringHelper.strip("  abc", null)    = "abc"
     * StringHelper.strip("abc  ", null)    = "abc"
     * StringHelper.strip(" abc ", null)    = "abc"
     * StringHelper.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(String str, final String stripChars) {
        if (Whether.empty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * Strips any of a set of characters from the start of a String.
     *
     * <p>A {@code null} input String returns {@code null}. An empty string ("") input returns the
     * empty string.
     *
     * <p>If the stripChars String is {@code null}, whitespace is stripped as defined by {@link
     * Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.stripStart(null, *)          = null
     * StringHelper.stripStart("", *)            = ""
     * StringHelper.stripStart("abc", "")        = "abc"
     * StringHelper.stripStart("abc", null)      = "abc"
     * StringHelper.stripStart("  abc", null)    = "abc"
     * StringHelper.stripStart("abc  ", null)    = "abc  "
     * StringHelper.stripStart(" abc ", null)    = "abc "
     * StringHelper.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripStart(final String str, final String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (Whether.empty(stripChars)) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
                start++;
            }
        }
        return str.substring(start);
    }

    // Equals
    // -----------------------------------------------------------------------

    /**
     * Strips any of a set of characters from the end of a String.
     *
     * <p>A {@code null} input String returns {@code null}. An empty string ("") input returns the
     * empty string.
     *
     * <p>If the stripChars String is {@code null}, whitespace is stripped as defined by {@link
     * Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.stripEnd(null, *)          = null
     * StringHelper.stripEnd("", *)            = ""
     * StringHelper.stripEnd("abc", "")        = "abc"
     * StringHelper.stripEnd("abc", null)      = "abc"
     * StringHelper.stripEnd("  abc", null)    = "  abc"
     * StringHelper.stripEnd("abc  ", null)    = "abc"
     * StringHelper.stripEnd(" abc ", null)    = " abc"
     * StringHelper.stripEnd("  abcyx", "xyz") = "  abc"
     * StringHelper.stripEnd("120.00", ".0")   = "12"
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the set of characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripEnd(final String str, final String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (Whether.empty(stripChars)) {
            return str;
        } else {
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    /**
     * Strips whitespace from the start and end of every String in an array. Whitespace is defined by
     * {@link Character#isWhitespace(char)}.
     *
     * <p>A new array is returned each time, except for length zero. A {@code null} array will return
     * {@code null}. An empty array will return itself. A {@code null} array entry will be ignored.
     *
     * <pre>
     * StringHelper.stripAll(null)             = null
     * StringHelper.stripAll([])               = []
     * StringHelper.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * StringHelper.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs the array to remove whitespace from, may be null
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String... strs) {
        return stripAll(strs, null);
    }

    // IndexOf
    // -----------------------------------------------------------------------

    /**
     * Strips any of a set of characters from the start and end of every String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * <p>A new array is returned each time, except for length zero. A {@code null} array will return
     * {@code null}. An empty array will return itself. A {@code null} array entry will be ignored. A
     * {@code null} stripChars will strip whitespace as defined by {@link
     * Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.stripAll(null, *)                = null
     * StringHelper.stripAll([], *)                  = []
     * StringHelper.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * StringHelper.stripAll(["abc  ", null], null)  = ["abc", null]
     * StringHelper.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * StringHelper.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs       the array to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String[] strs, final String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        final String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    /**
     * Removes diacritics (~= accents) from a string. The case will not be altered.
     *
     * <p>For instance, '&agrave;' will be replaced by 'a'.
     *
     * <p>Note that ligatures will be left as is.
     *
     * <pre>
     * StringHelper.stripAccents(null)                = null
     * StringHelper.stripAccents("")                  = ""
     * StringHelper.stripAccents("control")           = "control"
     * StringHelper.stripAccents("&eacute;clair")     = "eclair"
     * </pre>
     *
     * @param input String to be stripped
     * @return input text with diacritics removed
     */
    // See also Lucene's ASCIIFoldingFilter (Lucene 2.9) that replaces accented characters by their
    // unaccented equivalent (and uncommitted bug fix:
    // https://issues.apache.org/jira/browse/LUCENE-1343?focusedCommentId=12858907&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_12858907).
    public static String stripAccents(final String input) {
        if (input == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); // $NON-NLS-1$
        final String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Note that this doesn't correctly remove ligatures...
        return pattern.matcher(decomposed).replaceAll(""); // $NON-NLS-1$
    }

    /**
     * Compares two CharSequences, returning {@code true} if they represent equal sequences of
     * characters.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered to
     * be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringHelper.equals(null, null)   = true
     * StringHelper.equals(null, "abc")  = false
     * StringHelper.equals("abc", null)  = false
     * StringHelper.equals("abc", "abc") = true
     * StringHelper.equals("abc", "ABC") = false
     * </pre>
     *
     * @param cs1 the first CharSequence, may be {@code null}
     * @param cs2 the second CharSequence, may be {@code null}
     * @return {@code true} if the CharSequences are equal (case-sensitive), or both {@code null}
     * @see Object#equals(Object)
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return CharSequenceUtils.regionMatches(
                cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
    }

    /**
     * Compares two CharSequences, returning {@code true} if they represent equal sequences of
     * characters, ignoring case.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered
     * equal. Comparison is case insensitive.
     *
     * <pre>
     * StringHelper.equalsIgnoreCase(null, null)   = true
     * StringHelper.equalsIgnoreCase(null, "abc")  = false
     * StringHelper.equalsIgnoreCase("abc", null)  = false
     * StringHelper.equalsIgnoreCase("abc", "abc") = true
     * StringHelper.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 the first CharSequence, may be null
     * @param str2 the second CharSequence, may be null
     * @return {@code true} if the CharSequence are equal, case insensitive, or both {@code null}
     * equalsIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        } else if (str1.length() != str2.length()) {
            return false;
        } else {
            return CharSequenceUtils.regionMatches(str1, true, 0, str2, 0, str1.length());
        }
    }

    /**
     * Finds the first index within a CharSequence, handling {@code null}. This method uses {@link
     * String#indexOf(int, int)} if possible.
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code INDEX_NOT_FOUND (-1)}.
     *
     * <pre>
     * StringHelper.indexOf(null, *)         = -1
     * StringHelper.indexOf("", *)           = -1
     * StringHelper.indexOf("aabaabaa", 'a') = 0
     * StringHelper.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @return the first index of the search character, -1 if no match or {@code null} string input
     */
    public static int indexOf(final CharSequence seq, final int searchChar) {
        if (Whether.empty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0);
    }

    /**
     * Finds the first index within a CharSequence from a start position, handling {@code null}. This
     * method uses {@link String#indexOf(int, int)} if possible.
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code (INDEX_NOT_FOUND) -1}. A
     * negative start position is treated as zero. A start position greater than the string length
     * returns {@code -1}.
     *
     * <pre>
     * StringHelper.indexOf(null, *, *)          = -1
     * StringHelper.indexOf("", *, *)            = -1
     * StringHelper.indexOf("aabaabaa", 'b', 0)  = 2
     * StringHelper.indexOf("aabaabaa", 'b', 3)  = 5
     * StringHelper.indexOf("aabaabaa", 'b', 9)  = -1
     * StringHelper.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @param startPos   the start position, negative treated as zero
     * @return the first index of the search character (always &ge; startPos), -1 if no match or
     * {@code null} string input
     */
    public static int indexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (Whether.empty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, startPos);
    }

    /**
     * Finds the first index within a CharSequence, handling {@code null}. This method uses {@link
     * String#indexOf(String, int)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     *
     * <pre>
     * StringHelper.indexOf(null, *)          = -1
     * StringHelper.indexOf(*, null)          = -1
     * StringHelper.indexOf("", "")           = 0
     * StringHelper.indexOf("", *)            = -1 (except when * = "")
     * StringHelper.indexOf("aabaabaa", "a")  = 0
     * StringHelper.indexOf("aabaabaa", "b")  = 2
     * StringHelper.indexOf("aabaabaa", "ab") = 1
     * StringHelper.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @return the first index of the search CharSequence, -1 if no match or {@code null} string input
     * CharSequence)
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0);
    }

    /**
     * Finds the first index within a CharSequence, handling {@code null}. This method uses {@link
     * String#indexOf(String, int)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position is treated as
     * zero. An empty ("") search CharSequence always matches. A start position greater than the
     * string length only matches an empty search CharSequence.
     *
     * <pre>
     * StringHelper.indexOf(null, *, *)          = -1
     * StringHelper.indexOf(*, null, *)          = -1
     * StringHelper.indexOf("", "", 0)           = 0
     * StringHelper.indexOf("", *, 0)            = -1 (except when * = "")
     * StringHelper.indexOf("aabaabaa", "a", 0)  = 0
     * StringHelper.indexOf("aabaabaa", "b", 0)  = 2
     * StringHelper.indexOf("aabaabaa", "ab", 0) = 1
     * StringHelper.indexOf("aabaabaa", "b", 3)  = 5
     * StringHelper.indexOf("aabaabaa", "b", 9)  = -1
     * StringHelper.indexOf("aabaabaa", "b", -1) = 2
     * StringHelper.indexOf("aabaabaa", "", 2)   = 2
     * StringHelper.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search CharSequence (always &ge; startPos), -1 if no match or
     * {@code null} string input
     * CharSequence, int)
     */
    public static int indexOf(
            final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, startPos);
    }

    // LastIndexOf
    // -----------------------------------------------------------------------

    /**
     * Finds the n-th index within a CharSequence, handling {@code null}. This method uses {@link
     * String#indexOf(String)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     *
     * <pre>
     * StringHelper.ordinalIndexOf(null, *, *)          = -1
     * StringHelper.ordinalIndexOf(*, null, *)          = -1
     * StringHelper.ordinalIndexOf("", "", *)           = 0
     * StringHelper.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringHelper.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringHelper.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringHelper.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringHelper.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringHelper.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringHelper.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringHelper.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * <p>Note that 'head(CharSequence str, int n)' may be implemented as:
     *
     * <pre>
     *   str.substring(0, lastOrdinalIndexOf(str, "\n", n))
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @param ordinal   the n-th {@code searchStr} to find
     * @return the n-th index of the search CharSequence, {@code -1} ({@code INDEX_NOT_FOUND}) if no
     * match or {@code null} string input
     * ordinalIndexOf(CharSequence, CharSequence, int)
     */
    public static int ordinalIndexOf(
            final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    /**
     * Finds the n-th index within a String, handling {@code null}. This method uses {@link
     * String#indexOf(String)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @param ordinal   the n-th {@code searchStr} to find
     * @param lastIndex true if lastOrdinalIndexOf() otherwise false if ordinalIndexOf()
     * @return the n-th index of the search CharSequence, {@code -1} ({@code INDEX_NOT_FOUND}) if no
     * match or {@code null} string input
     */
    // Shared code between ordinalIndexOf(String,String,int) and lastOrdinalIndexOf(String,String,int)
    private static int ordinalIndexOf(
            final CharSequence str,
            final CharSequence searchStr,
            final int ordinal,
            final boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return lastIndex ? str.length() : 0;
        }
        int found = 0;
        int index = lastIndex ? str.length() : INDEX_NOT_FOUND;
        do {
            if (lastIndex) {
                index = CharSequenceUtils.lastIndexOf(str, searchStr, index - 1);
            } else {
                index = CharSequenceUtils.indexOf(str, searchStr, index + 1);
            }
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    /**
     * Case in-sensitive find of the first index within a CharSequence.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position is treated as
     * zero. An empty ("") search CharSequence always matches. A start position greater than the
     * string length only matches an empty search CharSequence.
     *
     * <pre>
     * StringHelper.indexOfIgnoreCase(null, *)          = -1
     * StringHelper.indexOfIgnoreCase(*, null)          = -1
     * StringHelper.indexOfIgnoreCase("", "")           = 0
     * StringHelper.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * StringHelper.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * StringHelper.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @return the first index of the search CharSequence, -1 if no match or {@code null} string input
     * indexOfIgnoreCase(CharSequence, CharSequence)
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * Case in-sensitive find of the first index within a CharSequence from the specified position.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position is treated as
     * zero. An empty ("") search CharSequence always matches. A start position greater than the
     * string length only matches an empty search CharSequence.
     *
     * <pre>
     * StringHelper.indexOfIgnoreCase(null, *, *)          = -1
     * StringHelper.indexOfIgnoreCase(*, null, *)          = -1
     * StringHelper.indexOfIgnoreCase("", "", 0)           = 0
     * StringHelper.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringHelper.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringHelper.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringHelper.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringHelper.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringHelper.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringHelper.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringHelper.indexOfIgnoreCase("abc", "", 9)        = 3
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search CharSequence (always &ge; startPos), -1 if no match or
     * {@code null} string input
     * indexOfIgnoreCase(CharSequence, CharSequence, int)
     */
    public static int indexOfIgnoreCase(
            final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        final int endLimit = str.length() - searchStr.length() + 1;
        if (startPos > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Finds the last index within a CharSequence, handling {@code null}. This method uses {@link
     * String#lastIndexOf(int)} if possible.
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code -1}.
     *
     * <pre>
     * StringHelper.lastIndexOf(null, *)         = -1
     * StringHelper.lastIndexOf("", *)           = -1
     * StringHelper.lastIndexOf("aabaabaa", 'a') = 7
     * StringHelper.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @return the last index of the search character, -1 if no match or {@code null} string input
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar) {
        if (Whether.empty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
    }

    /**
     * Finds the last index within a CharSequence from a start position, handling {@code null}. This
     * method uses {@link String#lastIndexOf(int, int)} if possible.
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code -1}. A negative start position
     * returns {@code -1}. A start position greater than the string length searches the whole string.
     * The search starts at the startPos and works backwards; matches starting after the start
     * position are ignored.
     *
     * <pre>
     * StringHelper.lastIndexOf(null, *, *)          = -1
     * StringHelper.lastIndexOf("", *,  *)           = -1
     * StringHelper.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringHelper.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringHelper.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringHelper.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringHelper.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringHelper.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @param startPos   the start position
     * @return the last index of the search character (always &le; startPos), -1 if no match or {@code
     * null} string input
     * int, int)
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (Whether.empty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
    }

    /**
     * Finds the last index within a CharSequence, handling {@code null}. This method uses {@link
     * String#lastIndexOf(String)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}.
     *
     * <pre>
     * StringHelper.lastIndexOf(null, *)          = -1
     * StringHelper.lastIndexOf(*, null)          = -1
     * StringHelper.lastIndexOf("", "")           = 0
     * StringHelper.lastIndexOf("aabaabaa", "a")  = 7
     * StringHelper.lastIndexOf("aabaabaa", "b")  = 5
     * StringHelper.lastIndexOf("aabaabaa", "ab") = 4
     * StringHelper.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @return the last index of the search String, -1 if no match or {@code null} string input
     * CharSequence)
     */
    public static int lastIndexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length());
    }

    // Contains
    // -----------------------------------------------------------------------

    /**
     * Finds the n-th last index within a String, handling {@code null}. This method uses {@link
     * String#lastIndexOf(String)}.
     *
     * <p>A {@code null} String will return {@code -1}.
     *
     * <pre>
     * StringHelper.lastOrdinalIndexOf(null, *, *)          = -1
     * StringHelper.lastOrdinalIndexOf(*, null, *)          = -1
     * StringHelper.lastOrdinalIndexOf("", "", *)           = 0
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "a", 1)  = 7
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "a", 2)  = 6
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "b", 1)  = 5
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "b", 2)  = 2
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "ab", 1) = 4
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "ab", 2) = 1
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "", 1)   = 8
     * StringHelper.lastOrdinalIndexOf("aabaabaa", "", 2)   = 8
     * </pre>
     *
     * <p>Note that 'tail(CharSequence str, int n)' may be implemented as:
     *
     * <pre>
     *   str.substring(lastOrdinalIndexOf(str, "\n", n) + 1)
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @param ordinal   the n-th last {@code searchStr} to find
     * @return the n-th last index of the search CharSequence, {@code -1} ({@code INDEX_NOT_FOUND}) if
     * no match or {@code null} string input
     * lastOrdinalIndexOf(CharSequence, CharSequence, int)
     */
    public static int lastOrdinalIndexOf(
            final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    /**
     * Finds the last index within a CharSequence, handling {@code null}. This method uses {@link
     * String#lastIndexOf(String, int)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position returns {@code
     * -1}. An empty ("") search CharSequence always matches unless the start position is negative. A
     * start position greater than the string length searches the whole string. The search starts at
     * the startPos and works backwards; matches starting after the start position are ignored.
     *
     * <pre>
     * StringHelper.lastIndexOf(null, *, *)          = -1
     * StringHelper.lastIndexOf(*, null, *)          = -1
     * StringHelper.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringHelper.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringHelper.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringHelper.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringHelper.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringHelper.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringHelper.lastIndexOf("aabaabaa", "b", 0)  = -1
     * StringHelper.lastIndexOf("aabaabaa", "b", 1)  = -1
     * StringHelper.lastIndexOf("aabaabaa", "b", 2)  = 2
     * StringHelper.lastIndexOf("aabaabaa", "ba", 2)  = -1
     * StringHelper.lastIndexOf("aabaabaa", "ba", 2)  = 2
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the last index of the search CharSequence (always &le; startPos), -1 if no match or
     * {@code null} string input
     * CharSequence, int)
     */
    public static int lastIndexOf(
            final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos);
    }

    /**
     * Case in-sensitive find of the last index within a CharSequence.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position returns {@code
     * -1}. An empty ("") search CharSequence always matches unless the start position is negative. A
     * start position greater than the string length searches the whole string.
     *
     * <pre>
     * StringHelper.lastIndexOfIgnoreCase(null, *)          = -1
     * StringHelper.lastIndexOfIgnoreCase(*, null)          = -1
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "A")  = 7
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "B")  = 5
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "AB") = 4
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @return the first index of the search CharSequence, -1 if no match or {@code null} string input
     * lastIndexOfIgnoreCase(CharSequence, CharSequence)
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * Case in-sensitive find of the last index within a CharSequence from the specified position.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A negative start position returns {@code
     * -1}. An empty ("") search CharSequence always matches unless the start position is negative. A
     * start position greater than the string length searches the whole string. The search starts at
     * the startPos and works backwards; matches starting after the start position are ignored.
     *
     * <pre>
     * StringHelper.lastIndexOfIgnoreCase(null, *, *)          = -1
     * StringHelper.lastIndexOfIgnoreCase(*, null, *)          = -1
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringHelper.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @param startPos  the start position
     * @return the last index of the search CharSequence (always &le; startPos), -1 if no match or
     * {@code null} input
     * lastIndexOfIgnoreCase(CharSequence, CharSequence, int)
     */
    public static int lastIndexOfIgnoreCase(
            final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
        }
        if (startPos < 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }

        for (int i = startPos; i >= 0; i--) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    // IndexOfAny chars
    // -----------------------------------------------------------------------

    /**
     * Checks if CharSequence contains a search character, handling {@code null}. This method uses
     * {@link String#indexOf(int)} if possible.
     *
     * <p>A {@code null} or empty ("") CharSequence will return {@code false}.
     *
     * <pre>
     * StringHelper.contains(null, *)    = false
     * StringHelper.contains("", *)      = false
     * StringHelper.contains("abc", 'a') = true
     * StringHelper.contains("abc", 'z') = false
     * </pre>
     *
     * @param seq        the CharSequence to check, may be null
     * @param searchChar the character to find
     * @return true if the CharSequence contains the search character, false if not or {@code null}
     * string input
     */
    public static boolean contains(final CharSequence seq, final int searchChar) {
        if (Whether.empty(seq)) {
            return false;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
    }

    /**
     * Checks if CharSequence contains a search CharSequence, handling {@code null}. This method uses
     * {@link String#indexOf(String)} if possible.
     *
     * <p>A {@code null} CharSequence will return {@code false}.
     *
     * <pre>
     * StringHelper.contains(null, *)     = false
     * StringHelper.contains(*, null)     = false
     * StringHelper.contains("", "")      = true
     * StringHelper.contains("abc", "")   = true
     * StringHelper.contains("abc", "a")  = true
     * StringHelper.contains("abc", "z")  = false
     * </pre>
     *
     * @param seq       the CharSequence to check, may be null
     * @param searchSeq the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence, false if not or {@code null}
     * string input
     * CharSequence)
     */
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return false;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
    }

    // ContainsAny
    // -----------------------------------------------------------------------

    /**
     * Checks if CharSequence contains a search CharSequence irrespective of case, handling {@code
     * null}. Case-insensitivity is defined as by {@link String#equalsIgnoreCase(String)}.
     *
     * <p>A {@code null} CharSequence will return {@code false}.
     *
     * <pre>
     * StringHelper.contains(null, *) = false
     * StringHelper.contains(*, null) = false
     * StringHelper.contains("", "") = true
     * StringHelper.contains("abc", "") = true
     * StringHelper.contains("abc", "a") = true
     * StringHelper.contains("abc", "z") = false
     * StringHelper.contains("abc", "A") = true
     * StringHelper.contains("abc", "Z") = false
     * </pre>
     *
     * @param str       the CharSequence to check, may be null
     * @param searchStr the CharSequence to find, may be null
     * @return true if the CharSequence contains the search CharSequence irrespective of case or false
     * if not or {@code null} string input
     * containsIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given CharSequence contains any whitespace characters.
     *
     * @param seq the CharSequence to check (may be {@code null})
     * @return {@code true} if the CharSequence is not empty and contains at least 1 whitespace
     * character
     * @see Character#isWhitespace
     */
    // From org.springframework.util.StringUtils, under Apache License 2.0
    public static boolean containsWhitespace(final CharSequence seq) {
        if (Whether.empty(seq)) {
            return false;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // IndexOfAnyBut chars
    // -----------------------------------------------------------------------

    /**
     * Search a CharSequence to find the first index of any character in the given set of characters.
     *
     * <p>A {@code null} String will return {@code -1}. A {@code null} or zero length search array
     * will return {@code -1}.
     *
     * <pre>
     * StringHelper.indexOfAny(null, *)                = -1
     * StringHelper.indexOfAny("", *)                  = -1
     * StringHelper.indexOfAny(*, null)                = -1
     * StringHelper.indexOfAny(*, [])                  = -1
     * StringHelper.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringHelper.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringHelper.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * char...)
     */
    public static int indexOfAny(final CharSequence cs, final char... searchChars) {
        if (Whether.empty(cs) || Whether.empty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        // ch is a supplementary character
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    } else {
                        return i;
                    }
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Search a CharSequence to find the first index of any character in the given set of characters.
     *
     * <p>A {@code null} String will return {@code -1}. A {@code null} search string will return
     * {@code -1}.
     *
     * <pre>
     * StringHelper.indexOfAny(null, *)            = -1
     * StringHelper.indexOfAny("", *)              = -1
     * StringHelper.indexOfAny(*, null)            = -1
     * StringHelper.indexOfAny(*, "")              = -1
     * StringHelper.indexOfAny("zzabyycdxx", "za") = 0
     * StringHelper.indexOfAny("zzabyycdxx", "by") = 3
     * StringHelper.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * String)
     */
    public static int indexOfAny(final CharSequence cs, final String searchChars) {
        if (Whether.empty(cs) || Whether.empty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        return indexOfAny(cs, searchChars.toCharArray());
    }

    // ContainsOnly
    // -----------------------------------------------------------------------

    /**
     * Checks if the CharSequence contains any character in the given set of characters.
     *
     * <p>A {@code null} CharSequence will return {@code false}. A {@code null} or zero length search
     * array will return {@code false}.
     *
     * <pre>
     * StringHelper.containsAny(null, *)                = false
     * StringHelper.containsAny("", *)                  = false
     * StringHelper.containsAny(*, null)                = false
     * StringHelper.containsAny(*, [])                  = false
     * StringHelper.containsAny("zzabyycdxx",['z','a']) = true
     * StringHelper.containsAny("zzabyycdxx",['b','y']) = true
     * StringHelper.containsAny("aba", ['z'])           = false
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the {@code true} if any of the chars are found, {@code false} if no match or null input
     * char...)
     */
    public static boolean containsAny(final CharSequence cs, final char... searchChars) {
        if (Whether.empty(cs) || Whether.empty(searchChars)) {
            return false;
        }
        final int csLength = cs.length();
        final int searchLength = searchChars.length;
        final int csLast = csLength - 1;
        final int searchLast = searchLength - 1;
        for (int i = 0; i < csLength; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLength; j++) {
                if (searchChars[j] == ch) {
                    if (Character.isHighSurrogate(ch)) {
                        if (j == searchLast) {
                            // missing low surrogate, fine, like String.indexOf(String)
                            return true;
                        }
                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return true;
                        }
                    } else {
                        // ch is in the Basic Multilingual Plane
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the CharSequence contains any character in the given set of characters.
     *
     * <p>A {@code null} CharSequence will return {@code false}. A {@code null} search CharSequence
     * will return {@code false}.
     *
     * <pre>
     * StringHelper.containsAny(null, *)            = false
     * StringHelper.containsAny("", *)              = false
     * StringHelper.containsAny(*, null)            = false
     * StringHelper.containsAny(*, "")              = false
     * StringHelper.containsAny("zzabyycdxx", "za") = true
     * StringHelper.containsAny("zzabyycdxx", "by") = true
     * StringHelper.containsAny("aba","z")          = false
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the {@code true} if any of the chars are found, {@code false} if no match or null input
     * CharSequence)
     */
    public static boolean containsAny(final CharSequence cs, final CharSequence searchChars) {
        if (searchChars == null) {
            return false;
        }
        return containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
    }

    // ContainsNone
    // -----------------------------------------------------------------------

    /**
     * Searches a CharSequence to find the first index of any character not in the given set of
     * characters.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A {@code null} or zero length search
     * array will return {@code -1}.
     *
     * <pre>
     * StringHelper.indexOfAnyBut(null, *)                              = -1
     * StringHelper.indexOfAnyBut("", *)                                = -1
     * StringHelper.indexOfAnyBut(*, null)                              = -1
     * StringHelper.indexOfAnyBut(*, [])                                = -1
     * StringHelper.indexOfAnyBut("zzabyycdxx", new char[] {'z', 'a'} ) = 3
     * StringHelper.indexOfAnyBut("aba", new char[] {'z'} )             = 0
     * StringHelper.indexOfAnyBut("aba", new char[] {'a', 'b'} )        = -1
     *
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * char...)
     */
    public static int indexOfAnyBut(final CharSequence cs, final char... searchChars) {
        if (Whether.empty(cs) || Whether.empty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        outer:
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            continue outer;
                        }
                    } else {
                        continue outer;
                    }
                }
            }
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * Search a CharSequence to find the first index of any character not in the given set of
     * characters.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A {@code null} or empty search string
     * will return {@code -1}.
     *
     * <pre>
     * StringHelper.indexOfAnyBut(null, *)            = -1
     * StringHelper.indexOfAnyBut("", *)              = -1
     * StringHelper.indexOfAnyBut(*, null)            = -1
     * StringHelper.indexOfAnyBut(*, "")              = -1
     * StringHelper.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringHelper.indexOfAnyBut("zzabyycdxx", "")   = -1
     * StringHelper.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param seq         the CharSequence to check, may be null
     * @param searchChars the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * CharSequence)
     */
    public static int indexOfAnyBut(final CharSequence seq, final CharSequence searchChars) {
        if (Whether.empty(seq) || Whether.empty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            final char ch = seq.charAt(i);
            final boolean chFound = CharSequenceUtils.indexOf(searchChars, ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                final char ch2 = seq.charAt(i + 1);
                if (chFound && CharSequenceUtils.indexOf(searchChars, ch2, 0) < 0) {
                    return i;
                }
            } else {
                if (!chFound) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    // IndexOfAny strings
    // -----------------------------------------------------------------------

    /**
     * Checks if the CharSequence contains only certain characters.
     *
     * <p>A {@code null} CharSequence will return {@code false}. A {@code null} valid character array
     * will return {@code false}. An empty CharSequence (length()=0) always returns {@code true}.
     *
     * <pre>
     * StringHelper.containsOnly(null, *)       = false
     * StringHelper.containsOnly(*, null)       = false
     * StringHelper.containsOnly("", *)         = true
     * StringHelper.containsOnly("ab", '')      = false
     * StringHelper.containsOnly("abab", 'abc') = true
     * StringHelper.containsOnly("ab1", 'abc')  = false
     * StringHelper.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param cs    the String to check, may be null
     * @param valid an array of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     * char...)
     */
    public static boolean containsOnly(final CharSequence cs, final char... valid) {
        // All these pre-checks are to maintain API with an older version
        if (valid == null || cs == null) {
            return false;
        }
        if (cs.length() == 0) {
            return true;
        }
        if (valid.length == 0) {
            return false;
        }
        return indexOfAnyBut(cs, valid) == INDEX_NOT_FOUND;
    }

    /**
     * Checks if the CharSequence contains only certain characters.
     *
     * <p>A {@code null} CharSequence will return {@code false}. A {@code null} valid character String
     * will return {@code false}. An empty String (length()=0) always returns {@code true}.
     *
     * <pre>
     * StringHelper.containsOnly(null, *)       = false
     * StringHelper.containsOnly(*, null)       = false
     * StringHelper.containsOnly("", *)         = true
     * StringHelper.containsOnly("ab", "")      = false
     * StringHelper.containsOnly("abab", "abc") = true
     * StringHelper.containsOnly("ab1", "abc")  = false
     * StringHelper.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param cs         the CharSequence to check, may be null
     * @param validChars a String of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     * String)
     */
    public static boolean containsOnly(final CharSequence cs, final String validChars) {
        if (cs == null || validChars == null) {
            return false;
        }
        return containsOnly(cs, validChars.toCharArray());
    }

    // Substring
    // -----------------------------------------------------------------------

    /**
     * Checks that the CharSequence does not contain certain characters.
     *
     * <p>A {@code null} CharSequence will return {@code true}. A {@code null} invalid character array
     * will return {@code true}. An empty CharSequence (length()=0) always returns true.
     *
     * <pre>
     * StringHelper.containsNone(null, *)       = true
     * StringHelper.containsNone(*, null)       = true
     * StringHelper.containsNone("", *)         = true
     * StringHelper.containsNone("ab", '')      = true
     * StringHelper.containsNone("abab", 'xyz') = true
     * StringHelper.containsNone("ab1", 'xyz')  = true
     * StringHelper.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * char...)
     */
    public static boolean containsNone(final CharSequence cs, final char... searchChars) {
        if (cs == null || searchChars == null) {
            return true;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (Character.isHighSurrogate(ch)) {
                        if (j == searchLast) {
                            // missing low surrogate, fine, like String.indexOf(String)
                            return false;
                        }
                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return false;
                        }
                    } else {
                        // ch is in the Basic Multilingual Plane
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks that the CharSequence does not contain certain characters.
     *
     * <p>A {@code null} CharSequence will return {@code true}. A {@code null} invalid character array
     * will return {@code true}. An empty String ("") always returns true.
     *
     * <pre>
     * StringHelper.containsNone(null, *)       = true
     * StringHelper.containsNone(*, null)       = true
     * StringHelper.containsNone("", *)         = true
     * StringHelper.containsNone("ab", "")      = true
     * StringHelper.containsNone("abab", "xyz") = true
     * StringHelper.containsNone("ab1", "xyz")  = true
     * StringHelper.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param cs           the CharSequence to check, may be null
     * @param invalidChars a String of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * String)
     */
    public static boolean containsNone(final CharSequence cs, final String invalidChars) {
        if (cs == null || invalidChars == null) {
            return true;
        }
        return containsNone(cs, invalidChars.toCharArray());
    }

    // Left/Right/Mid
    // -----------------------------------------------------------------------

    /**
     * Find the first index of any of a set of potential substrings.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A {@code null} or zero length search
     * array will return {@code -1}. A {@code null} search array entry will be ignored, but a search
     * array containing "" will return {@code 0} if {@code str} is not null. This method uses {@link
     * String#indexOf(String)} if possible.
     *
     * <pre>
     * StringHelper.indexOfAny(null, *)                     = -1
     * StringHelper.indexOfAny(*, null)                     = -1
     * StringHelper.indexOfAny(*, [])                       = -1
     * StringHelper.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringHelper.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringHelper.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringHelper.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringHelper.indexOfAny("zzabyycdxx", [""])          = 0
     * StringHelper.indexOfAny("", [""])                    = 0
     * StringHelper.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str        the CharSequence to check, may be null
     * @param searchStrs the CharSequences to search for, may be null
     * @return the first index of any of the searchStrs in str, -1 if no match
     * CharSequence...)
     */
    public static int indexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }
        final int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp;
        for (final CharSequence search : searchStrs) {
            if (search == null) {
                continue;
            }
            tmp = CharSequenceUtils.indexOf(str, search, 0);
            if (tmp == INDEX_NOT_FOUND) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return ret == Integer.MAX_VALUE ? INDEX_NOT_FOUND : ret;
    }

    /**
     * Find the latest index of any of a set of potential substrings.
     *
     * <p>A {@code null} CharSequence will return {@code -1}. A {@code null} search array will return
     * {@code -1}. A {@code null} or zero length search array entry will be ignored, but a search
     * array containing "" will return the length of {@code str} if {@code str} is not null. This
     * method uses {@link String#indexOf(String)} if possible
     *
     * <pre>
     * StringHelper.lastIndexOfAny(null, *)                   = -1
     * StringHelper.lastIndexOfAny(*, null)                   = -1
     * StringHelper.lastIndexOfAny(*, [])                     = -1
     * StringHelper.lastIndexOfAny(*, [null])                 = -1
     * StringHelper.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringHelper.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringHelper.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringHelper.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringHelper.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str        the CharSequence to check, may be null
     * @param searchStrs the CharSequences to search for, may be null
     * @return the last index of any of the CharSequences, -1 if no match
     * lastIndexOfAny(CharSequence, CharSequence)
     */
    public static int lastIndexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }
        final int sz = searchStrs.length;
        int ret = INDEX_NOT_FOUND;
        int tmp;
        for (final CharSequence search : searchStrs) {
            if (search == null) {
                continue;
            }
            tmp = CharSequenceUtils.lastIndexOf(str, search, str.length());
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    /**
     * Gets a substring from the specified String avoiding exceptions.
     *
     * <p>A negative start position can be used to start {@code n} characters from the end of the
     * String.
     *
     * <p>A {@code null} String will return {@code null}. An empty ("") String will return "".
     *
     * <pre>
     * StringHelper.substring(null, *)   = null
     * StringHelper.substring("", *)     = ""
     * StringHelper.substring("abc", 0)  = "abc"
     * StringHelper.substring("abc", 2)  = "c"
     * StringHelper.substring("abc", 4)  = ""
     * StringHelper.substring("abc", -2) = "bc"
     * StringHelper.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means count back from the end of the String
     *              by this many characters
     * @return substring from start position, {@code null} if null String input
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    // SubStringAfter/SubStringBefore
    // -----------------------------------------------------------------------

    /**
     * Gets a substring from the specified String avoiding exceptions.
     *
     * <p>A negative start position can be used to start/end {@code n} characters from the end of the
     * String.
     *
     * <p>The returned substring starts with the character in the {@code start} position and ends
     * before the {@code end} position. All position counting is zero-based -- i.e., to start at the
     * beginning of the string use {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, "" is returned.
     *
     * <pre>
     * StringHelper.substring(null, *, *)    = null
     * StringHelper.substring("", * ,  *)    = "";
     * StringHelper.substring("abc", 0, 2)   = "ab"
     * StringHelper.substring("abc", 2, 0)   = ""
     * StringHelper.substring("abc", 2, 4)   = "c"
     * StringHelper.substring("abc", 4, 6)   = ""
     * StringHelper.substring("abc", 2, 2)   = ""
     * StringHelper.substring("abc", -2, -1) = "b"
     * StringHelper.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means count back from the end of the String
     *              by this many characters
     * @param end   the position to end at (exclusive), negative means count back from the end of the
     *              String by this many characters
     * @return substring from start position to end position, {@code null} if null String input
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * Gets the leftmost {@code len} characters of a String.
     *
     * <p>If {@code len} characters are not available, or the String is {@code null}, the String will
     * be returned without an exception. An empty String is returned if len is negative.
     *
     * <pre>
     * StringHelper.left(null, *)    = null
     * StringHelper.left(*, -ve)     = ""
     * StringHelper.left("", *)      = ""
     * StringHelper.left("abc", 0)   = ""
     * StringHelper.left("abc", 2)   = "ab"
     * StringHelper.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the leftmost characters from, may be null
     * @param len the length of the required String
     * @return the leftmost characters, {@code null} if null String input
     */
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * Gets the rightmost {@code len} characters of a String.
     *
     * <p>If {@code len} characters are not available, or the String is {@code null}, the String will
     * be returned without an an exception. An empty String is returned if len is negative.
     *
     * <pre>
     * StringHelper.right(null, *)    = null
     * StringHelper.right(*, -ve)     = ""
     * StringHelper.right("", *)      = ""
     * StringHelper.right("abc", 0)   = ""
     * StringHelper.right("abc", 2)   = "bc"
     * StringHelper.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the rightmost characters from, may be null
     * @param len the length of the required String
     * @return the rightmost characters, {@code null} if null String input
     */
    public static String right(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * Gets {@code len} characters from the middle of a String.
     *
     * <p>If {@code len} characters are not available, the remainder of the String will be returned
     * without an exception. If the String is {@code null}, {@code null} will be returned. An empty
     * String is returned if len is negative or exceeds the length of {@code str}.
     *
     * <pre>
     * StringHelper.mid(null, *, *)    = null
     * StringHelper.mid(*, *, -ve)     = ""
     * StringHelper.mid("", 0, *)      = ""
     * StringHelper.mid("abc", 0, 2)   = "ab"
     * StringHelper.mid("abc", 0, 4)   = "abc"
     * StringHelper.mid("abc", 2, 4)   = "c"
     * StringHelper.mid("abc", 4, 2)   = ""
     * StringHelper.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str the String to get the characters from, may be null
     * @param pos the position to start from, negative treated as zero
     * @param len the length of the required String
     * @return the middle characters, {@code null} if null String input
     */
    public static String mid(final String str, int pos, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    // Substring between
    // -----------------------------------------------------------------------

    /**
     * Gets the substring before the first occurrence of a separator. The separator is not returned.
     *
     * <p>A {@code null} string input will return {@code null}. An empty ("") string input will return
     * the empty string. A {@code null} separator will return the input string.
     *
     * <p>If nothing is found, the string input is returned.
     *
     * <pre>
     * StringHelper.substringBefore(null, *)      = null
     * StringHelper.substringBefore("", *)        = ""
     * StringHelper.substringBefore("abc", "a")   = ""
     * StringHelper.substringBefore("abcba", "b") = "a"
     * StringHelper.substringBefore("abc", "c")   = "ab"
     * StringHelper.substringBefore("abc", "d")   = "abc"
     * StringHelper.substringBefore("abc", "")    = ""
     * StringHelper.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the first occurrence of the separator, {@code null} if null String
     * input
     */
    public static String substringBefore(final String str, final String separator) {
        if (Whether.empty(str) || separator == null) {
            return str;
        }
        if (Whether.empty(separator)) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * Gets the substring after the first occurrence of a separator. The separator is not returned.
     *
     * <p>A {@code null} string input will return {@code null}. An empty ("") string input will return
     * the empty string. A {@code null} separator will return the empty string if the input string is
     * not {@code null}.
     *
     * <p>If nothing is found, the empty string is returned.
     *
     * <pre>
     * StringHelper.substringAfter(null, *)      = null
     * StringHelper.substringAfter("", *)        = ""
     * StringHelper.substringAfter(*, null)      = ""
     * StringHelper.substringAfter("abc", "a")   = "bc"
     * StringHelper.substringAfter("abcba", "b") = "cba"
     * StringHelper.substringAfter("abc", "c")   = ""
     * StringHelper.substringAfter("abc", "d")   = ""
     * StringHelper.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the first occurrence of the separator, {@code null} if null String
     * input
     */
    public static String substringAfter(final String str, final String separator) {
        if (Whether.empty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * Gets the substring before the last occurrence of a separator. The separator is not returned.
     *
     * <p>A {@code null} string input will return {@code null}. An empty ("") string input will return
     * the empty string. An empty or {@code null} separator will return the input string.
     *
     * <p>If nothing is found, the string input is returned.
     *
     * <pre>
     * StringHelper.substringBeforeLast(null, *)      = null
     * StringHelper.substringBeforeLast("", *)        = ""
     * StringHelper.substringBeforeLast("abcba", "b") = "abc"
     * StringHelper.substringBeforeLast("abc", "c")   = "ab"
     * StringHelper.substringBeforeLast("a", "a")     = ""
     * StringHelper.substringBeforeLast("a", "z")     = "a"
     * StringHelper.substringBeforeLast("a", null)    = "a"
     * StringHelper.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the last occurrence of the separator, {@code null} if null String
     * input
     */
    public static String substringBeforeLast(final String str, final String separator) {
        if (Whether.empty(str) || Whether.empty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    // Nested extraction
    // -----------------------------------------------------------------------

    // Splitting
    // -----------------------------------------------------------------------

    /**
     * Gets the substring after the last occurrence of a separator. The separator is not returned.
     *
     * <p>A {@code null} string input will return {@code null}. An empty ("") string input will return
     * the empty string. An empty or {@code null} separator will return the empty string if the input
     * string is not {@code null}.
     *
     * <p>If nothing is found, the empty string is returned.
     *
     * <pre>
     * StringHelper.substringAfterLast(null, *)      = null
     * StringHelper.substringAfterLast("", *)        = ""
     * StringHelper.substringAfterLast(*, "")        = ""
     * StringHelper.substringAfterLast(*, null)      = ""
     * StringHelper.substringAfterLast("abc", "a")   = "bc"
     * StringHelper.substringAfterLast("abcba", "b") = "a"
     * StringHelper.substringAfterLast("abc", "c")   = ""
     * StringHelper.substringAfterLast("a", "a")     = ""
     * StringHelper.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the last occurrence of the separator, {@code null} if null String
     * input
     */
    public static String substringAfterLast(final String str, final String separator) {
        if (Whether.empty(str)) {
            return str;
        }
        if (Whether.empty(separator)) {
            return EMPTY;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * Gets the String that is nested in between two instances of the same String.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} tag returns {@code null}.
     *
     * <pre>
     * StringHelper.substringBetween(null, *)            = null
     * StringHelper.substringBetween("", "")             = ""
     * StringHelper.substringBetween("", "tag")          = null
     * StringHelper.substringBetween("tagabctag", null)  = null
     * StringHelper.substringBetween("tagabctag", "")    = ""
     * StringHelper.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str the String containing the substring, may be null
     * @param tag the String before and after the substring, may be null
     * @return the substring, {@code null} if no match
     */
    public static String substringBetween(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * Gets the String that is nested in between two Strings. Only the first match is returned.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} open/close returns {@code
     * null} (no match). An empty ("") open and close returns an empty string.
     *
     * <pre>
     * StringHelper.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringHelper.substringBetween(null, *, *)          = null
     * StringHelper.substringBetween(*, null, *)          = null
     * StringHelper.substringBetween(*, *, null)          = null
     * StringHelper.substringBetween("", "", "")          = ""
     * StringHelper.substringBetween("", "", "]")         = null
     * StringHelper.substringBetween("", "[", "]")        = null
     * StringHelper.substringBetween("yabcz", "", "")     = ""
     * StringHelper.substringBetween("yabcz", "y", "z")   = "abc"
     * StringHelper.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str   the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close the String after the substring, may be null
     * @return the substring, {@code null} if no match
     */
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * Searches a String for substrings delimited by a start and end tag, returning all matching
     * substrings in an array.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} open/close returns {@code
     * null} (no match). An empty ("") open/close returns {@code null} (no match).
     *
     * <pre>
     * StringHelper.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * StringHelper.substringsBetween(null, *, *)            = null
     * StringHelper.substringsBetween(*, null, *)            = null
     * StringHelper.substringsBetween(*, *, null)            = null
     * StringHelper.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str   the String containing the substrings, null returns null, empty returns empty
     * @param open  the String identifying the start of the substring, empty returns null
     * @param close the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or {@code null} if no match
     */
    public static String[] substringsBetween(
            final String str, final String open, final String close) {
        if (str == null || Whether.empty(open) || Whether.empty(close)) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == 0) {
            return PoolOfArray.EMPTY_STRING_ARRAY;
        }
        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<String>();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            final int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (Whether.empty(list)) {
            return null;
        }
        return list.toArray(new String[0]);
    }

    /**
     * Splits the provided text into an array, using whitespace as the separator. Whitespace is
     * defined by {@link Character#isWhitespace(char)}.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as one separator. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.split(null)       = null
     * StringHelper.split("")         = []
     * StringHelper.split("abc def")  = ["abc", "def"]
     * StringHelper.split("abc  def") = ["abc", "def"]
     * StringHelper.split(" abc ")    = ["abc"]
     * </pre>
     *
     * @param str the String to parse, may be null
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str) {
        return split(str, null, -1);
    }

    /**
     * Splits the provided text into an array, separator specified. This is an alternative to using
     * StringTokenizer.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as one separator. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.split(null, *)         = null
     * StringHelper.split("", *)           = []
     * StringHelper.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringHelper.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringHelper.split("a:b:c", '.')    = ["a:b:c"]
     * StringHelper.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str           the String to parse, may be null
     * @param separatorChar the character used as the delimiter
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    /**
     * Splits the provided text into an array, separators specified. This is an alternative to using
     * StringTokenizer.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as one separator. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separatorChars splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.split(null, *)         = null
     * StringHelper.split("", *)           = []
     * StringHelper.split("abc def", null) = ["abc", "def"]
     * StringHelper.split("abc def", " ")  = ["abc", "def"]
     * StringHelper.split("abc  def", " ") = ["abc", "def"]
     * StringHelper.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * Splits the provided text into an array with a maximum length, separators specified.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as one separator.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separatorChars splits on
     * whitespace.
     *
     * <p>If more than {@code max} delimited substrings are found, the last returned string includes
     * all characters after the first {@code max - 1} returned strings (including separator
     * characters).
     *
     * <pre>
     * StringHelper.split(null, *, *)            = null
     * StringHelper.split("", *, *)              = []
     * StringHelper.split("ab cd ef", null, 0)   = ["ab", "cd", "ef"]
     * StringHelper.split("ab   cd ef", null, 0) = ["ab", "cd", "ef"]
     * StringHelper.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringHelper.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str            the String to parse, may be null
     * @param separatorChars the characters used as the delimiters, {@code null} splits on whitespace
     * @param max            the maximum number of elements to include in the array. A zero or negative value
     *                       implies no limit
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] split(final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    /**
     * Splits the provided text into an array, separator string specified.
     *
     * <p>The separator(s) will not be included in the returned String array. Adjacent separators are
     * treated as one separator.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separator splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.splitByWholeSeparator(null, *)               = null
     * StringHelper.splitByWholeSeparator("", *)                 = []
     * StringHelper.splitByWholeSeparator("ab de fg", null)      = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparator("ab   de fg", null)    = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparator("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringHelper.splitByWholeSeparator("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str       the String to parse, may be null
     * @param separator String containing the String to be used as a delimiter, {@code null} splits on
     *                  whitespace
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparator(final String str, final String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, false);
    }

    // -----------------------------------------------------------------------

    /**
     * Splits the provided text into an array, separator string specified. Returns a maximum of {@code
     * max} substrings.
     *
     * <p>The separator(s) will not be included in the returned String array. Adjacent separators are
     * treated as one separator.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separator splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.splitByWholeSeparator(null, *, *)               = null
     * StringHelper.splitByWholeSeparator("", *, *)                 = []
     * StringHelper.splitByWholeSeparator("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparator("ab   de fg", null, 0)    = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparator("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * StringHelper.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * StringHelper.splitByWholeSeparator("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str       the String to parse, may be null
     * @param separator String containing the String to be used as a delimiter, {@code null} splits on
     *                  whitespace
     * @param max       the maximum number of elements to include in the returned array. A zero or negative
     *                  value implies no limit.
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparator(
            final String str, final String separator, final int max) {
        return splitByWholeSeparatorWorker(str, separator, max, false);
    }

    /**
     * Splits the provided text into an array, separator string specified.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separator splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.splitByWholeSeparatorPreserveAllTokens(null, *)               = null
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("", *)                 = []
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab de fg", null)      = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null)    = ["ab", "", "", "de", "fg"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":")       = ["ab", "cd", "ef"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str       the String to parse, may be null
     * @param separator String containing the String to be used as a delimiter, {@code null} splits on
     *                  whitespace
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(
            final String str, final String separator) {
        return splitByWholeSeparatorWorker(str, separator, -1, true);
    }

    /**
     * Splits the provided text into an array, separator string specified. Returns a maximum of {@code
     * max} substrings.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separator splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.splitByWholeSeparatorPreserveAllTokens(null, *, *)               = null
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("", *, *)                 = []
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab de fg", null, 0)      = ["ab", "de", "fg"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab   de fg", null, 0)    = ["ab", "", "", "de", "fg"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab:cd:ef", ":", 2)       = ["ab", "cd:ef"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 5) = ["ab", "cd", "ef"]
     * StringHelper.splitByWholeSeparatorPreserveAllTokens("ab-!-cd-!-ef", "-!-", 2) = ["ab", "cd-!-ef"]
     * </pre>
     *
     * @param str       the String to parse, may be null
     * @param separator String containing the String to be used as a delimiter, {@code null} splits on
     *                  whitespace
     * @param max       the maximum number of elements to include in the returned array. A zero or negative
     *                  value implies no limit.
     * @return an array of parsed Strings, {@code null} if null String was input
     */
    public static String[] splitByWholeSeparatorPreserveAllTokens(
            final String str, final String separator, final int max) {
        return splitByWholeSeparatorWorker(str, separator, max, true);
    }

    /**
     * Performs the logic for the {@code splitByWholeSeparatorPreserveAllTokens} methods.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separator         String containing the String to be used as a delimiter, {@code null} splits on
     *                          whitespace
     * @param max               the maximum number of elements to include in the returned array. A zero or negative
     *                          value implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are treated as empty token
     *                          separators; if {@code false}, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitByWholeSeparatorWorker(
            final String str, final String separator, final int max, final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }

        final int len = str.length();

        if (len == 0) {
            return PoolOfArray.EMPTY_STRING_ARRAY;
        }

        if (separator == null || EMPTY.equals(separator)) {
            // Split on whitespace.
            return splitWorker(str, null, max, preserveAllTokens);
        }

        final int separatorLength = separator.length();

        final ArrayList<String> substrings = new ArrayList<String>();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);

            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings += 1;

                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        // The following is OK, because String.substring( beg, end ) excludes
                        // the character at the position 'end'.
                        substrings.add(str.substring(beg, end));

                        // Set the starting point for the next search.
                        // The following is equivalent to beg = end + (separatorLength - 1) + 1,
                        // which is the right calculation:
                        beg = end + separatorLength;
                    }
                } else {
                    // We found a consecutive occurrence of the separator, so skip it.
                    if (preserveAllTokens) {
                        numberOfSubstrings += 1;
                        if (numberOfSubstrings == max) {
                            end = len;
                            substrings.add(str.substring(beg));
                        } else {
                            substrings.add(EMPTY);
                        }
                    }
                    beg = end + separatorLength;
                }
            } else {
                // String.substring( beg ) goes from 'beg' to the end of the String.
                substrings.add(str.substring(beg));
                end = len;
            }
        }

        return substrings.toArray(new String[0]);
    }

    /**
     * Splits the provided text into an array, using whitespace as the separator, preserving all
     * tokens, including empty tokens created by adjacent separators. This is an alternative to using
     * StringTokenizer. Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.splitPreserveAllTokens(null)       = null
     * StringHelper.splitPreserveAllTokens("")         = []
     * StringHelper.splitPreserveAllTokens("abc def")  = ["abc", "def"]
     * StringHelper.splitPreserveAllTokens("abc  def") = ["abc", "", "def"]
     * StringHelper.splitPreserveAllTokens(" abc ")    = ["", "abc", ""]
     * </pre>
     *
     * @param str the String to parse, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitPreserveAllTokens(final String str) {
        return splitWorker(str, null, -1, true);
    }

    /**
     * Splits the provided text into an array, separator specified, preserving all tokens, including
     * empty tokens created by adjacent separators. This is an alternative to using StringTokenizer.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.splitPreserveAllTokens(null, *)         = null
     * StringHelper.splitPreserveAllTokens("", *)           = []
     * StringHelper.splitPreserveAllTokens("a.b.c", '.')    = ["a", "b", "c"]
     * StringHelper.splitPreserveAllTokens("a..b.c", '.')   = ["a", "", "b", "c"]
     * StringHelper.splitPreserveAllTokens("a:b:c", '.')    = ["a:b:c"]
     * StringHelper.splitPreserveAllTokens("a\tb\nc", null) = ["a", "b", "c"]
     * StringHelper.splitPreserveAllTokens("a b c", ' ')    = ["a", "b", "c"]
     * StringHelper.splitPreserveAllTokens("a b c ", ' ')   = ["a", "b", "c", ""]
     * StringHelper.splitPreserveAllTokens("a b c  ", ' ')   = ["a", "b", "c", "", ""]
     * StringHelper.splitPreserveAllTokens(" a b c", ' ')   = ["", a", "b", "c"]
     * StringHelper.splitPreserveAllTokens("  a b c", ' ')  = ["", "", a", "b", "c"]
     * StringHelper.splitPreserveAllTokens(" a b c ", ' ')  = ["", a", "b", "c", ""]
     * </pre>
     *
     * @param str           the String to parse, may be {@code null}
     * @param separatorChar the character used as the delimiter, {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitPreserveAllTokens(final String str, final char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    /**
     * Performs the logic for the {@code split} and {@code splitPreserveAllTokens} methods that do not
     * return a maximum array length.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separatorChar     the separate character
     * @param preserveAllTokens if {@code true}, adjacent separators are treated as empty token
     *                          separators; if {@code false}, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(
            final String str, final char separatorChar, final boolean preserveAllTokens) {

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return PoolOfArray.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<String>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[0]);
    }

    /**
     * Splits the provided text into an array, separators specified, preserving all tokens, including
     * empty tokens created by adjacent separators. This is an alternative to using StringTokenizer.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. For more control over the split use the StrTokenizer class.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separatorChars splits on
     * whitespace.
     *
     * <pre>
     * StringHelper.splitPreserveAllTokens(null, *)           = null
     * StringHelper.splitPreserveAllTokens("", *)             = []
     * StringHelper.splitPreserveAllTokens("abc def", null)   = ["abc", "def"]
     * StringHelper.splitPreserveAllTokens("abc def", " ")    = ["abc", "def"]
     * StringHelper.splitPreserveAllTokens("abc  def", " ")   = ["abc", "", def"]
     * StringHelper.splitPreserveAllTokens("ab:cd:ef", ":")   = ["ab", "cd", "ef"]
     * StringHelper.splitPreserveAllTokens("ab:cd:ef:", ":")  = ["ab", "cd", "ef", ""]
     * StringHelper.splitPreserveAllTokens("ab:cd:ef::", ":") = ["ab", "cd", "ef", "", ""]
     * StringHelper.splitPreserveAllTokens("ab::cd:ef", ":")  = ["ab", "", cd", "ef"]
     * StringHelper.splitPreserveAllTokens(":cd:ef", ":")     = ["", cd", "ef"]
     * StringHelper.splitPreserveAllTokens("::cd:ef", ":")    = ["", "", cd", "ef"]
     * StringHelper.splitPreserveAllTokens(":cd:ef:", ":")    = ["", cd", "ef", ""]
     * </pre>
     *
     * @param str            the String to parse, may be {@code null}
     * @param separatorChars the characters used as the delimiters, {@code null} splits on whitespace
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitPreserveAllTokens(final String str, final String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    /**
     * Splits the provided text into an array with a maximum length, separators specified, preserving
     * all tokens, including empty tokens created by adjacent separators.
     *
     * <p>The separator is not included in the returned String array. Adjacent separators are treated
     * as separators for empty tokens. Adjacent separators are treated as one separator.
     *
     * <p>A {@code null} input String returns {@code null}. A {@code null} separatorChars splits on
     * whitespace.
     *
     * <p>If more than {@code max} delimited substrings are found, the last returned string includes
     * all characters after the first {@code max - 1} returned strings (including separator
     * characters).
     *
     * <pre>
     * StringHelper.splitPreserveAllTokens(null, *, *)            = null
     * StringHelper.splitPreserveAllTokens("", *, *)              = []
     * StringHelper.splitPreserveAllTokens("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringHelper.splitPreserveAllTokens("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringHelper.splitPreserveAllTokens("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringHelper.splitPreserveAllTokens("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * StringHelper.splitPreserveAllTokens("ab   de fg", null, 2) = ["ab", "  de fg"]
     * StringHelper.splitPreserveAllTokens("ab   de fg", null, 3) = ["ab", "", " de fg"]
     * StringHelper.splitPreserveAllTokens("ab   de fg", null, 4) = ["ab", "", "", "de fg"]
     * </pre>
     *
     * @param str            the String to parse, may be {@code null}
     * @param separatorChars the characters used as the delimiters, {@code null} splits on whitespace
     * @param max            the maximum number of elements to include in the array. A zero or negative value
     *                       implies no limit
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitPreserveAllTokens(
            final String str, final String separatorChars, final int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    // Joining
    // -----------------------------------------------------------------------

    /**
     * Performs the logic for the {@code split} and {@code splitPreserveAllTokens} methods that return
     * a maximum array length.
     *
     * @param str               the String to parse, may be {@code null}
     * @param separatorChars    the separate character
     * @param max               the maximum number of elements to include in the array. A zero or negative value
     *                          implies no limit.
     * @param preserveAllTokens if {@code true}, adjacent separators are treated as empty token
     *                          separators; if {@code false}, adjacent separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitWorker(
            final String str,
            final String separatorChars,
            final int max,
            final boolean preserveAllTokens) {

        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return PoolOfArray.EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[0]);
    }

    /**
     * Splits a String by Character type as returned by {@code java.lang.Character.getType(char)}.
     * Groups of contiguous characters of the same type are returned as complete tokens.
     *
     * <pre>
     * StringHelper.splitByCharacterType(null)         = null
     * StringHelper.splitByCharacterType("")           = []
     * StringHelper.splitByCharacterType("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringHelper.splitByCharacterType("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringHelper.splitByCharacterType("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringHelper.splitByCharacterType("number5")    = ["number", "5"]
     * StringHelper.splitByCharacterType("fooBar")     = ["foo", "B", "ar"]
     * StringHelper.splitByCharacterType("foo200Bar")  = ["foo", "200", "B", "ar"]
     * StringHelper.splitByCharacterType("ASFRules")   = ["ASFR", "ules"]
     * </pre>
     *
     * @param str the String to split, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitByCharacterType(final String str) {
        return splitByCharacterType(str, false);
    }

    /**
     * Splits a String by Character type as returned by {@code java.lang.Character.getType(char)}.
     * Groups of contiguous characters of the same type are returned as complete tokens, with the
     * following exception: the character of type {@code Character.UPPERCASE_LETTER}, if any,
     * immediately preceding a token of type {@code Character.LOWERCASE_LETTER} will belong to the
     * following token rather than to the preceding, if any, {@code Character.UPPERCASE_LETTER} token.
     *
     * <pre>
     * StringHelper.splitByCharacterTypeCamelCase(null)         = null
     * StringHelper.splitByCharacterTypeCamelCase("")           = []
     * StringHelper.splitByCharacterTypeCamelCase("ab de fg")   = ["ab", " ", "de", " ", "fg"]
     * StringHelper.splitByCharacterTypeCamelCase("ab   de fg") = ["ab", "   ", "de", " ", "fg"]
     * StringHelper.splitByCharacterTypeCamelCase("ab:cd:ef")   = ["ab", ":", "cd", ":", "ef"]
     * StringHelper.splitByCharacterTypeCamelCase("number5")    = ["number", "5"]
     * StringHelper.splitByCharacterTypeCamelCase("fooBar")     = ["foo", "Bar"]
     * StringHelper.splitByCharacterTypeCamelCase("foo200Bar")  = ["foo", "200", "Bar"]
     * StringHelper.splitByCharacterTypeCamelCase("ASFRules")   = ["ASF", "Rules"]
     * </pre>
     *
     * @param str the String to split, may be {@code null}
     * @return an array of parsed Strings, {@code null} if null String input
     */
    public static String[] splitByCharacterTypeCamelCase(final String str) {
        return splitByCharacterType(str, true);
    }

    /**
     * Splits a String by Character type as returned by {@code java.lang.Character.getType(char)}.
     * Groups of contiguous characters of the same type are returned as complete tokens, with the
     * following exception: if {@code camelCase} is {@code true}, the character of type {@code
     * Character.UPPERCASE_LETTER}, if any, immediately preceding a token of type {@code
     * Character.LOWERCASE_LETTER} will belong to the following token rather than to the preceding, if
     * any, {@code Character.UPPERCASE_LETTER} token.
     *
     * @param str       the String to split, may be {@code null}
     * @param camelCase whether to use so-called "camel-case" for letter types
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private static String[] splitByCharacterType(final String str, final boolean camelCase) {
        if (str == null) {
            return null;
        }
        if (Whether.empty(str)) {
            return PoolOfArray.EMPTY_STRING_ARRAY;
        }
        final char[] c = str.toCharArray();
        final List<String> list = new ArrayList<String>();
        int tokenStart = 0;
        int currentType = Character.getType(c[tokenStart]);
        for (int pos = tokenStart + 1; pos < c.length; pos++) {
            final int type = Character.getType(c[pos]);
            if (type == currentType) {
                continue;
            }
            if (camelCase
                    && type == Character.LOWERCASE_LETTER
                    && currentType == Character.UPPERCASE_LETTER) {
                final int newTokenStart = pos - 1;
                if (newTokenStart != tokenStart) {
                    list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                    tokenStart = newTokenStart;
                }
            } else {
                list.add(new String(c, tokenStart, pos - tokenStart));
                tokenStart = pos;
            }
            currentType = type;
        }
        list.add(new String(c, tokenStart, c.length - tokenStart));
        return list.toArray(new String[0]);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No separator is added to the joined String. Null objects or empty strings within the array
     * are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null)            = null
     * StringHelper.join([])              = ""
     * StringHelper.join([null])          = ""
     * StringHelper.join(["a", "b", "c"]) = "abc"
     * StringHelper.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param <T>      the specific type of values to join together
     * @param elements the values to join together, may be null
     * @return the joined String, {@code null} if null array input
     */
    @SafeVarargs
    public static <T> String join(final T... elements) {
        return join(elements, null);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringHelper.join(["a", "b", "c"], null) = "abc"
     * StringHelper.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final Object[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final long[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final int[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final short[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final byte[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final char[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final float[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final double[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringHelper.join(["a", "b", "c"], null) = "abc"
     * StringHelper.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final Object[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final long[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final int[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final byte[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final short[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final char[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final double[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * array are represented by empty strings.
     *
     * <pre>
     * StringHelper.join(null, *)               = null
     * StringHelper.join([], *)                 = ""
     * StringHelper.join([null], *)             = ""
     * StringHelper.join([1, 2, 3], ';')  = "1;2;3"
     * StringHelper.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from. It is an error to pass in an end index
     *                   past the end of the array
     * @param endIndex   the index to stop joining from (exclusive). It is an error to pass in an end
     *                   index past the end of the array
     * @return the joined String, {@code null} if null array input
     */
    public static String join(
            final float[] array, final char separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. A {@code null} separator is the same as an
     * empty String (""). Null objects or empty strings within the array are represented by empty
     * strings.
     *
     * <pre>
     * StringHelper.join(null, *)                = null
     * StringHelper.join([], *)                  = ""
     * StringHelper.join([null], *)              = ""
     * StringHelper.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringHelper.join(["a", "b", "c"], null)  = "abc"
     * StringHelper.join(["a", "b", "c"], "")    = "abc"
     * StringHelper.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array     the array of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null array input
     */
    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * Joins the elements of the provided array into a single String containing the provided list of
     * elements.
     *
     * <p>No delimiter is added before or after the list. A {@code null} separator is the same as an
     * empty String (""). Null objects or empty strings within the array are represented by empty
     * strings.
     *
     * <pre>
     * StringHelper.join(null, *, *, *)                = null
     * StringHelper.join([], *, *, *)                  = ""
     * StringHelper.join([null], *, *, *)              = ""
     * StringHelper.join(["a", "b", "c"], "--", 0, 3)  = "a--b--c"
     * StringHelper.join(["a", "b", "c"], "--", 1, 3)  = "b--c"
     * StringHelper.join(["a", "b", "c"], "--", 2, 3)  = "c"
     * StringHelper.join(["a", "b", "c"], "--", 2, 2)  = ""
     * StringHelper.join(["a", "b", "c"], null, 0, 3)  = "abc"
     * StringHelper.join(["a", "b", "c"], "", 0, 3)    = "abc"
     * StringHelper.join([null, "", "a"], ',', 0, 3)   = ",,a"
     * </pre>
     *
     * @param array      the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.
     * @param endIndex   the index to stop joining from (exclusive).
     * @return the joined String, {@code null} if null array input; or the empty string if {@code
     * endIndex - startIndex <= 0}. The number of joined entries is given by {@code endIndex -
     * startIndex}
     * @throws ArrayIndexOutOfBoundsException ife<br>
     *                                        {@code startIndex < 0} or <br>
     *                                        {@code startIndex >= array.length()} or <br>
     *                                        {@code endIndex < 0} or <br>
     *                                        {@code endIndex > array.length()}
     */
    public static String join(
            final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = new StringBuilder(noOfItems * 16);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    // Delete
    // -----------------------------------------------------------------------

    /**
     * Joins the elements of the provided {@code Iterator} into a single String containing the
     * provided elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * iteration are represented by empty strings.
     *
     * <p>See the examples here: {@link #join(Object[], char)}.
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterator<?> iterator, final char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first == null ? "" : first.toString();
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    // Remove
    // -----------------------------------------------------------------------

    /**
     * Joins the elements of the provided {@code Iterator} into a single String containing the
     * provided elements.
     *
     * <p>No delimiter is added before or after the list. A {@code null} separator is the same as an
     * empty String ("").
     *
     * <p>See the examples here: {@link #join(Object[], String)}.
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Valid.string(first);
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided {@code Iterable} into a single String containing the
     * provided elements.
     *
     * <p>No delimiter is added before or after the list. Null objects or empty strings within the
     * iteration are represented by empty strings.
     *
     * <p>See the examples here: {@link #join(Object[], char)}.
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator the separator character to use
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    /**
     * Joins the elements of the provided {@code Iterable} into a single String containing the
     * provided elements.
     *
     * <p>No delimiter is added before or after the list. A {@code null} separator is the same as an
     * empty String ("").
     *
     * <p>See the examples here: {@link #join(Object[], String)}.
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterable<?> iterable, final String separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    /**
     * Deletes all whitespaces from a String as defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringHelper.deleteWhitespace(null)         = null
     * StringHelper.deleteWhitespace("")           = ""
     * StringHelper.deleteWhitespace("abc")        = "abc"
     * StringHelper.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str the String to delete whitespace from, may be null
     * @return the String without whitespaces, {@code null} if null String input
     */
    public static String deleteWhitespace(final String str) {
        if (Whether.empty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    /**
     * Removes a substring only if it is at the beginning of a source string, otherwise returns the
     * source string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string. A {@code null} search string will return the source string.
     *
     * <pre>
     * StringHelper.removeStart(null, *)      = null
     * StringHelper.removeStart("", *)        = ""
     * StringHelper.removeStart(*, null)      = *
     * StringHelper.removeStart("www.domain.com", "www.")   = "domain.com"
     * StringHelper.removeStart("domain.com", "www.")       = "domain.com"
     * StringHelper.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringHelper.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, {@code null} if null String input
     */
    public static String removeStart(final String str, final String remove) {
        if (Whether.empty(str) || Whether.empty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * Case insensitive removal of a substring if it is at the beginning of a source string, otherwise
     * returns the source string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string. A {@code null} search string will return the source string.
     *
     * <pre>
     * StringHelper.removeStartIgnoreCase(null, *)      = null
     * StringHelper.removeStartIgnoreCase("", *)        = ""
     * StringHelper.removeStartIgnoreCase(*, null)      = *
     * StringHelper.removeStartIgnoreCase("www.domain.com", "www.")   = "domain.com"
     * StringHelper.removeStartIgnoreCase("www.domain.com", "WWW.")   = "domain.com"
     * StringHelper.removeStartIgnoreCase("domain.com", "www.")       = "domain.com"
     * StringHelper.removeStartIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringHelper.removeStartIgnoreCase("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found, {@code null} if null String input
     */
    public static String removeStartIgnoreCase(final String str, final String remove) {
        if (Whether.empty(str) || Whether.empty(remove)) {
            return str;
        }
        if (startsWithIgnoreCase(str, remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    // Replacing
    // -----------------------------------------------------------------------

    /**
     * Removes a substring only if it is at the end of a source string, otherwise returns the source
     * string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string. A {@code null} search string will return the source string.
     *
     * <pre>
     * StringHelper.removeEnd(null, *)      = null
     * StringHelper.removeEnd("", *)        = ""
     * StringHelper.removeEnd(*, null)      = *
     * StringHelper.removeEnd("www.domain.com", ".com.")  = "www.domain.com"
     * StringHelper.removeEnd("www.domain.com", ".com")   = "www.domain"
     * StringHelper.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * StringHelper.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, {@code null} if null String input
     */
    public static String removeEnd(final String str, final String remove) {
        if (Whether.empty(str) || Whether.empty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * Case insensitive removal of a substring if it is at the end of a source string, otherwise
     * returns the source string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string. A {@code null} search string will return the source string.
     *
     * <pre>
     * StringHelper.removeEndIgnoreCase(null, *)      = null
     * StringHelper.removeEndIgnoreCase("", *)        = ""
     * StringHelper.removeEndIgnoreCase(*, null)      = *
     * StringHelper.removeEndIgnoreCase("www.domain.com", ".com.")  = "www.domain.com"
     * StringHelper.removeEndIgnoreCase("www.domain.com", ".com")   = "www.domain"
     * StringHelper.removeEndIgnoreCase("www.domain.com", "domain") = "www.domain.com"
     * StringHelper.removeEndIgnoreCase("abc", "")    = "abc"
     * StringHelper.removeEndIgnoreCase("www.domain.com", ".COM") = "www.domain")
     * StringHelper.removeEndIgnoreCase("www.domain.COM", ".com") = "www.domain")
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for (case insensitive) and remove, may be null
     * @return the substring with the string removed if found, {@code null} if null String input
     */
    public static String removeEndIgnoreCase(final String str, final String remove) {
        if (Whether.empty(str) || Whether.empty(remove)) {
            return str;
        }
        if (endsWithIgnoreCase(str, remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    /**
     * Removes all occurrences of a substring from within the source string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string. A {@code null} remove string will return the source string. An empty
     * ("") remove string will return the source string.
     *
     * <pre>
     * StringHelper.remove(null, *)        = null
     * StringHelper.remove("", *)          = ""
     * StringHelper.remove(*, null)        = *
     * StringHelper.remove(*, "")          = *
     * StringHelper.remove("queued", "ue") = "qd"
     * StringHelper.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found, {@code null} if null String input
     */
    public static String remove(final String str, final String remove) {
        if (Whether.empty(str) || Whether.empty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    /**
     * Removes all occurrences of a character from within the source string.
     *
     * <p>A {@code null} source string will return {@code null}. An empty ("") source string will
     * return the empty string.
     *
     * <pre>
     * StringHelper.remove(null, *)       = null
     * StringHelper.remove("", *)         = ""
     * StringHelper.remove("queued", 'u') = "qeed"
     * StringHelper.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the char to search for and remove, may be null
     * @return the substring with the char removed if found, {@code null} if null String input
     */
    public static String remove(final String str, final char remove) {
        if (Whether.empty(str) || str.indexOf(remove) == INDEX_NOT_FOUND) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    /**
     * Replaces a String with another String inside a larger String, once.
     *
     * <p>A {@code null} reference passed to this method is a no-op.
     *
     * <pre>
     * StringHelper.replaceOnce(null, *, *)        = null
     * StringHelper.replaceOnce("", *, *)          = ""
     * StringHelper.replaceOnce("any", null, *)    = "any"
     * StringHelper.replaceOnce("any", *, null)    = "any"
     * StringHelper.replaceOnce("any", "", *)      = "any"
     * StringHelper.replaceOnce("aba", "a", null)  = "aba"
     * StringHelper.replaceOnce("aba", "a", "")    = "ba"
     * StringHelper.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace with, may be null
     * @return the text with any replacements processed, {@code null} if null String input
     * @see #replace(String text, String searchString, String replacement, int max)
     */
    public static String replaceOnce(
            final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * Replaces each substring of the source String that matches the given regular expression with the
     * given replacement using the {@link Pattern#DOTALL} option. DOTALL is also know as single-line
     * mode in Perl. This call is also equivalent to:
     *
     * <ul>
     *   <li>{@code source.replaceAll(&quot;(?s)&quot; + regex, replacement)}
     *   <li>{@code Pattern.compile(regex, Pattern.DOTALL).matcher(source).replaceAll(replacement)}
     * </ul>
     *
     * @param source      the source string
     * @param regex       the regular expression to which this string is to be matched
     * @param replacement the string to be substituted for each match
     * @return The resulting {@code String}
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     */
    public static String replacePattern(
            final String source, final String regex, final String replacement) {
        return Pattern.compile(regex, Pattern.DOTALL).matcher(source).replaceAll(replacement);
    }

    /**
     * Removes each substring of the source String that matches the given regular expression using the
     * DOTALL option.
     *
     * @param source the source string
     * @param regex  the regular expression to which this string is to be matched
     * @return The resulting {@code String}
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     */
    public static String removePattern(final String source, final String regex) {
        return replacePattern(source, regex, ToolString.EMPTY);
    }

    /**
     * Replaces all occurrences of a String within another String.
     *
     * <p>A {@code null} reference passed to this method is a no-op.
     *
     * <pre>
     * StringHelper.replace(null, *, *)        = null
     * StringHelper.replace("", *, *)          = ""
     * StringHelper.replace("any", null, *)    = "any"
     * StringHelper.replace("any", *, null)    = "any"
     * StringHelper.replace("any", "", *)      = "any"
     * StringHelper.replace("aba", "a", null)  = "aba"
     * StringHelper.replace("aba", "a", "")    = "b"
     * StringHelper.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed, {@code null} if null String input
     * @see #replace(String text, String searchString, String replacement, int max)
     */
    public static String replace(
            final String text, final String searchString, final String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    // Replace, character based
    // -----------------------------------------------------------------------

    /**
     * Replaces a String with another String inside a larger String, for the first {@code max} values
     * of the search String.
     *
     * <p>A {@code null} reference passed to this method is a no-op.
     *
     * <pre>
     * StringHelper.replace(null, *, *, *)         = null
     * StringHelper.replace("", *, *, *)           = ""
     * StringHelper.replace("any", null, *, *)     = "any"
     * StringHelper.replace("any", *, null, *)     = "any"
     * StringHelper.replace("any", "", *, *)       = "any"
     * StringHelper.replace("any", *, *, 0)        = "any"
     * StringHelper.replace("abaa", "a", null, -1) = "abaa"
     * StringHelper.replace("abaa", "a", "", -1)   = "b"
     * StringHelper.replace("abaa", "a", "z", 0)   = "abaa"
     * StringHelper.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringHelper.replace("abaa", "a", "z", 2)   = "zbza"
     * StringHelper.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text         text to search and replace in, may be null
     * @param searchString the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max          maximum number of values to replace, or {@code -1} if no maximum
     * @return the text with any replacements processed, {@code null} if null String input
     */
    public static String replace(
            final String text, final String searchString, final String replacement, int max) {
        if (Whether.empty(text) || Whether.empty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = Math.max(increase, 0);
        increase *= max < 0 ? 16 : Math.min(max, 64);
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * Replaces all occurrences of Strings within another String.
     *
     * <p>A {@code null} reference passed to this method is a no-op, or if any "search string" or
     * "string to replace" is null, that replace will be ignored. This will not repeat. For repeating
     * replaces, call the overloaded method.
     *
     * <pre>
     *  StringHelper.replaceEach(null, *, *)        = null
     *  StringHelper.replaceEach("", *, *)          = ""
     *  StringHelper.replaceEach("aba", null, null) = "aba"
     *  StringHelper.replaceEach("aba", new String[0], null) = "aba"
     *  StringHelper.replaceEach("aba", null, new String[0]) = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, null)  = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, new String[]{""})  = "b"
     *  StringHelper.replaceEach("aba", new String[]{null}, new String[]{"a"})  = "aba"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"})  = "wcte"
     *  (example of how it does not repeat)
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"})  = "dcte"
     * </pre>
     *
     * @param text            text to search and replace in, no-op if null
     * @param searchList      the Strings to search for, no-op if null
     * @param replacementList the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if null String input
     * @throws IllegalArgumentException if the lengths of the arrays are not the same (null is ok,
     *                                  and/or size 0)
     */
    public static String replaceEach(
            final String text, final String[] searchList, final String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    // Overlay
    // -----------------------------------------------------------------------

    /**
     * Replaces all occurrences of Strings within another String.
     *
     * <p>A {@code null} reference passed to this method is a no-op, or if any "search string" or
     * "string to replace" is null, that replace will be ignored.
     *
     * <pre>
     *  StringHelper.replaceEach(null, *, *, *) = null
     *  StringHelper.replaceEach("", *, *, *) = ""
     *  StringHelper.replaceEach("aba", null, null, *) = "aba"
     *  StringHelper.replaceEach("aba", new String[0], null, *) = "aba"
     *  StringHelper.replaceEach("aba", null, new String[0], *) = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  StringHelper.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, true) = IllegalStateException
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, false) = "dcabe"
     * </pre>
     *
     * @param text            text to search and replace in, no-op if null
     * @param searchList      the Strings to search for, no-op if null
     * @param replacementList the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if null String input
     * @throws IllegalStateException    if the search is repeating and there is an endless loop due to
     *                                  outputs of one being inputs to another
     * @throws IllegalArgumentException if the lengths of the arrays are not the same (null is ok,
     *                                  and/or size 0)
     */
    public static String replaceEachRepeatedly(
            final String text, final String[] searchList, final String[] replacementList) {
        // timeToLive should be 0 if not used or nothing to replace, else it's
        // the length of the replace array
        final int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    // Chomping
    // -----------------------------------------------------------------------

    /**
     * Replaces all occurrences of Strings within another String.
     *
     * <p>A {@code null} reference passed to this method is a no-op, or if any "search string" or
     * "string to replace" is null, that replace will be ignored.
     *
     * <pre>
     *  StringHelper.replaceEach(null, *, *, *) = null
     *  StringHelper.replaceEach("", *, *, *) = ""
     *  StringHelper.replaceEach("aba", null, null, *) = "aba"
     *  StringHelper.replaceEach("aba", new String[0], null, *) = "aba"
     *  StringHelper.replaceEach("aba", null, new String[0], *) = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  StringHelper.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  StringHelper.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  StringHelper.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, *) = IllegalStateException
     * </pre>
     *
     * @param text            text to search and replace in, no-op if null
     * @param searchList      the Strings to search for, no-op if null
     * @param replacementList the Strings to replace them with, no-op if null
     * @param repeat          if true, then replace repeatedly until there are no more possible replacements or
     *                        timeToLive < 0
     * @param timeToLive      if less than 0 then there is a circular reference and endless loop
     * @return the text with any replacements processed, {@code null} if null String input
     * @throws IllegalStateException    if the search is repeating and there is an endless loop due to
     *                                  outputs of one being inputs to another
     * @throws IllegalArgumentException if the lengths of the arrays are not the same (null is ok,
     *                                  and/or size 0)
     */
    private static String replaceEach(
            final String text,
            final String[] searchList,
            final String[] replacementList,
            final boolean repeat,
            final int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null
                || Whether.empty(text)
                || searchList == null
                || searchList.length == 0
                || replacementList == null
                || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException(
                    "Aborting to protect against StackOverflowError - "
                            + "output of one loop is the input of another");
        }

        final int searchLength = searchList.length;
        final int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException(
                    "Search and Replace array lengths don't match: "
                            + searchLength
                            + " vs "
                            + replacementLength);
        }

        // keep track of which still have matches
        final boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i]
                    || searchList[i] == null
                    || Whether.empty(searchList[i])
                    || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            } else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes
        // over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being
        // replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            final int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        final StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i]
                        || searchList[i] == null
                        || Whether.empty(searchList[i])
                        || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                } else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        final int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        final String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    /**
     * Replaces all occurrences of a character in a String with another. This is a null-safe version
     * of {@link String#replace(char, char)}.
     *
     * <p>A {@code null} string input returns {@code null}. An empty ("") string input returns an
     * empty string.
     *
     * <pre>
     * StringHelper.replaceChars(null, *, *)        = null
     * StringHelper.replaceChars("", *, *)          = ""
     * StringHelper.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringHelper.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str         String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar the character to replace, may be null
     * @return modified String, {@code null} if null string input
     */
    public static String replaceChars(
            final String str, final char searchChar, final char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    // Chopping
    // -----------------------------------------------------------------------

    /**
     * Replaces multiple characters in a String in one go. This method can also be used to delete
     * characters.
     *
     * <p>For example:<br>
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.
     *
     * <p>A {@code null} string input returns {@code null}. An empty ("") string input returns an
     * empty string. A null or empty set of search characters returns the input string.
     *
     * <p>The length of the search characters should normally equal the length of the replace
     * characters. If the search characters is longer, then the extra search characters are deleted.
     * If the search characters is shorter, then the extra replace characters are ignored.
     *
     * <pre>
     * StringHelper.replaceChars(null, *, *)           = null
     * StringHelper.replaceChars("", *, *)             = ""
     * StringHelper.replaceChars("abc", null, *)       = "abc"
     * StringHelper.replaceChars("abc", "", *)         = "abc"
     * StringHelper.replaceChars("abc", "b", null)     = "ac"
     * StringHelper.replaceChars("abc", "b", "")       = "ac"
     * StringHelper.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringHelper.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringHelper.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str          String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars a set of characters to replace, may be null
     * @return modified String, {@code null} if null string input
     */
    public static String replaceChars(
            final String str, final String searchChars, String replaceChars) {
        if (Whether.empty(str) || Whether.empty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = EMPTY;
        }
        boolean modified = false;
        final int replaceCharsLength = replaceChars.length();
        final int strLength = str.length();
        final StringBuilder buf = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            final char ch = str.charAt(i);
            final int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    // Conversion
    // -----------------------------------------------------------------------

    // Padding
    // -----------------------------------------------------------------------

    /**
     * Overlays part of a String with another String.
     *
     * <p>A {@code null} string input returns {@code null}. A negative index is treated as zero. An
     * index greater than the string length is treated as the string length. The start index is always
     * the smaller of the two indices.
     *
     * <pre>
     * StringHelper.overlay(null, *, *, *)            = null
     * StringHelper.overlay("", "abc", 0, 0)          = "abc"
     * StringHelper.overlay("abcdef", null, 2, 4)     = "abef"
     * StringHelper.overlay("abcdef", "", 2, 4)       = "abef"
     * StringHelper.overlay("abcdef", "", 4, 2)       = "abef"
     * StringHelper.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringHelper.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringHelper.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringHelper.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringHelper.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringHelper.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str     the String to do overlaying in, may be null
     * @param overlay the String to overlay, may be null
     * @param start   the position to start overlaying at
     * @param end     the position to stop overlaying before
     * @return overlayed String, {@code null} if null String input
     */
    public static String overlay(final String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = EMPTY;
        }
        final int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            final int temp = start;
            start = end;
            end = temp;
        }
        return str.substring(0, start) + overlay + str.substring(end);
    }

    /**
     * Removes one newline from end of a String if it's there, otherwise leave it alone. A newline is
     * &quot;{@code \n}&quot;, &quot;{@code \r}&quot;, or &quot;{@code \r\n}&quot;.
     *
     * <p>NOTE: This method changed in 2.0. It now more closely matches Perl chomp.
     *
     * <pre>
     * StringHelper.chomp(null)          = null
     * StringHelper.chomp("")            = ""
     * StringHelper.chomp("abc \r")      = "abc "
     * StringHelper.chomp("abc\n")       = "abc"
     * StringHelper.chomp("abc\r\n")     = "abc"
     * StringHelper.chomp("abc\r\n\r\n") = "abc\r\n"
     * StringHelper.chomp("abc\n\r")     = "abc\n"
     * StringHelper.chomp("abc\n\rabc")  = "abc\n\rabc"
     * StringHelper.chomp("\r")          = ""
     * StringHelper.chomp("\n")          = ""
     * StringHelper.chomp("\r\n")        = ""
     * </pre>
     *
     * @param str the String to chomp a newline from, may be null
     * @return String without newline, {@code null} if null String input
     */
    public static String chomp(final String str) {
        if (Whether.empty(str)) {
            return str;
        }

        if (str.length() == 1) {
            final char ch = str.charAt(0);
            if (ch == PoolOfCharacter.CR || ch == PoolOfCharacter.LF) {
                return EMPTY;
            }
            return str;
        }

        int lastIdx = str.length() - 1;
        final char last = str.charAt(lastIdx);

        if (last == PoolOfCharacter.LF) {
            if (str.charAt(lastIdx - 1) == PoolOfCharacter.CR) {
                lastIdx--;
            }
        } else if (last != PoolOfCharacter.CR) {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    /**
     * Remove the last character from a String.
     *
     * <p>If the String ends in {@code \r\n}, then remove both of them.
     *
     * <pre>
     * StringHelper.chop(null)          = null
     * StringHelper.chop("")            = ""
     * StringHelper.chop("abc \r")      = "abc "
     * StringHelper.chop("abc\n")       = "abc"
     * StringHelper.chop("abc\r\n")     = "abc"
     * StringHelper.chop("abc")         = "ab"
     * StringHelper.chop("abc\nabc")    = "abc\nab"
     * StringHelper.chop("a")           = ""
     * StringHelper.chop("\r")          = ""
     * StringHelper.chop("\n")          = ""
     * StringHelper.chop("\r\n")        = ""
     * </pre>
     *
     * @param str the String to chop last character from, may be null
     * @return String without last character, {@code null} if null String input
     */
    public static String chop(final String str) {
        if (str == null) {
            return null;
        }
        final int strLen = str.length();
        if (strLen < 2) {
            return EMPTY;
        }
        final int lastIdx = strLen - 1;
        final String ret = str.substring(0, lastIdx);
        final char last = str.charAt(lastIdx);
        if (last == PoolOfCharacter.LF && ret.charAt(lastIdx - 1) == PoolOfCharacter.CR) {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    /**
     * Repeat a String {@code repeat} times to form a new String.
     *
     * <pre>
     * StringHelper.repeat(null, 2) = null
     * StringHelper.repeat("", 0)   = ""
     * StringHelper.repeat("", 2)   = ""
     * StringHelper.repeat("a", 3)  = "aaa"
     * StringHelper.repeat("ab", 2) = "abab"
     * StringHelper.repeat("a", -2) = ""
     * </pre>
     *
     * @param str    the String to repeat, may be null
     * @param repeat number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static String repeat(final String str, final int repeat) {

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return repeat(str.charAt(0), repeat);
        }

        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                return repeat(str.charAt(0), repeat);
            case 2:
                final char ch0 = str.charAt(0);
                final char ch1 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default:
                final StringBuilder buf = new StringBuilder(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String.
     *
     * <pre>
     * StringHelper.repeat(null, 2) = null
     * StringHelper.repeat("", 0)   = ""
     * StringHelper.repeat("", 2)   = ""
     * StringHelper.repeat("a", 3)  = "aaa"
     * StringHelper.repeat("ab", 2) = "abab"
     * StringHelper.repeat("a", -2) = ""
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(final StringBuilder container, final String str, final int repeat) {
        try {
            repeat((Appendable) container, str, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String.
     *
     * <pre>
     * StringHelper.repeat(null, 2) = null
     * StringHelper.repeat("", 0)   = ""
     * StringHelper.repeat("", 2)   = ""
     * StringHelper.repeat("a", 3)  = "aaa"
     * StringHelper.repeat("ab", 2) = "abab"
     * StringHelper.repeat("a", -2) = ""
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(final StringBuffer container, final String str, final int repeat) {
        try {
            repeat((Appendable) container, str, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String.
     *
     * <pre>
     * StringHelper.repeat(null, 2) = null
     * StringHelper.repeat("", 0)   = ""
     * StringHelper.repeat("", 2)   = ""
     * StringHelper.repeat("a", 3)  = "aaa"
     * StringHelper.repeat("ab", 2) = "abab"
     * StringHelper.repeat("a", -2) = ""
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(final Appendable container, final String str, final int repeat)
            throws IOException {
        if (str == null || repeat <= 0) {
            return;
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            container.append(str);
        } else if (inputLength == 1 && repeat <= PAD_LIMIT) {
            repeat(container, str.charAt(0), repeat);
        } else {
            final int outputLength = inputLength * repeat;
            switch (inputLength) {
                case 1:
                    repeat(container, str.charAt(0), repeat);
                case 2:
                    final char ch0 = str.charAt(0);
                    final char ch1 = str.charAt(1);
                    for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                        container.append(ch0).append(ch1);
                    }
                default:
                    for (int i = 0; i < repeat; i++) {
                        container.append(str);
                    }
            }
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String, with a String separator injected
     * each time.
     *
     * <pre>
     * StringHelper.repeat(null, null, 2) = null
     * StringHelper.repeat(null, "x", 2)  = null
     * StringHelper.repeat("", null, 0)   = ""
     * StringHelper.repeat("", "", 2)     = ""
     * StringHelper.repeat("", "x", 3)    = "xxx"
     * StringHelper.repeat("?", ", ", 3)  = "?, ?, ?"
     * </pre>
     *
     * @param str       the String to repeat, may be null
     * @param separator the String to inject, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static String repeat(final String str, final String separator, final int repeat) {
        if (str == null || separator == null) {
            return repeat(str, repeat);
        }
        // given that repeat(String, int) is quite optimized, better to rely on it than try and splice
        // this into it
        final String result = repeat(str + separator, repeat);
        return removeEnd(result, separator);
    }

    /**
     * Repeat a String {@code repeat} times to form a new String, with a String separator injected
     * each time.
     *
     * <pre>
     * StringHelper.repeat(null, null, 2) = null
     * StringHelper.repeat(null, "x", 2)  = null
     * StringHelper.repeat("", null, 0)   = ""
     * StringHelper.repeat("", "", 2)     = ""
     * StringHelper.repeat("", "x", 3)    = "xxx"
     * StringHelper.repeat("?", ", ", 3)  = "?, ?, ?"
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param separator the String to inject, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(
            final StringBuilder container, final String str, final String separator, final int repeat) {
        try {
            repeat((Appendable) container, str, separator, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String, with a String separator injected
     * each time.
     *
     * <pre>
     * StringHelper.repeat(null, null, 2) = null
     * StringHelper.repeat(null, "x", 2)  = null
     * StringHelper.repeat("", null, 0)   = ""
     * StringHelper.repeat("", "", 2)     = ""
     * StringHelper.repeat("", "x", 3)    = "xxx"
     * StringHelper.repeat("?", ", ", 3)  = "?, ?, ?"
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param separator the String to inject, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(
            final StringBuffer container, final String str, final String separator, final int repeat) {
        try {
            repeat((Appendable) container, str, separator, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Repeat a String {@code repeat} times to form a new String, with a String separator injected
     * each time.
     *
     * <pre>
     * StringHelper.repeat(null, null, 2) = null
     * StringHelper.repeat(null, "x", 2)  = null
     * StringHelper.repeat("", null, 0)   = ""
     * StringHelper.repeat("", "", 2)     = ""
     * StringHelper.repeat("", "x", 3)    = "xxx"
     * StringHelper.repeat("?", ", ", 3)  = "?, ?, ?"
     * </pre>
     *
     * @param container The String Container
     * @param str       the String to repeat, may be null
     * @param separator the String to inject, may be null
     * @param repeat    number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static void repeat(
            final Appendable container, final String str, final String separator, final int repeat)
            throws IOException {
        if (str == null || separator == null) {
            repeat(container, str, repeat);
        }
        // given that repeat(String, int) is quite optimized, better to rely on it than try and splice
        // this into it
        String result = repeat(str + separator, repeat);
        result = removeEnd(result, separator);
        container.append(result);
    }

    /**
     * Returns padding using the specified delimiter repeated to a given length.
     *
     * <pre>
     * StringHelper.repeat('e', 0)  = ""
     * StringHelper.repeat('e', 3)  = "eee"
     * StringHelper.repeat('e', -2) = ""
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
     * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to
     * support full I18N of your applications consider using {@link #repeat(String, int)} instead.
     *
     * @param ch     character to repeat
     * @param repeat number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static String repeat(final char ch, final int repeat) {
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    /**
     * Returns padding using the specified delimiter repeated to a given length.
     *
     * <pre>
     * StringHelper.repeat('e', 0)  = ""
     * StringHelper.repeat('e', 3)  = "eee"
     * StringHelper.repeat('e', -2) = ""
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
     * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to
     * support full I18N of your applications consider using {@link #repeat(String, int)} instead.
     *
     * @param container The String Container
     * @param ch        character to repeat
     * @param repeat    number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static void repeat(final StringBuilder container, final char ch, final int repeat) {
        try {
            repeat((Appendable) container, ch, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns padding using the specified delimiter repeated to a given length.
     *
     * <pre>
     * StringHelper.repeat('e', 0)  = ""
     * StringHelper.repeat('e', 3)  = "eee"
     * StringHelper.repeat('e', -2) = ""
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
     * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to
     * support full I18N of your applications consider using {@link #repeat(String, int)} instead.
     *
     * @param container The String Container
     * @param ch        character to repeat
     * @param repeat    number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static void repeat(final StringBuffer container, final char ch, final int repeat) {
        try {
            repeat((Appendable) container, ch, repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns padding using the specified delimiter repeated to a given length.
     *
     * <pre>
     * StringHelper.repeat('e', 0)  = ""
     * StringHelper.repeat('e', 3)  = "eee"
     * StringHelper.repeat('e', -2) = ""
     * </pre>
     *
     * <p>Note: this method doesn't not support padding with <a
     * href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary
     * Characters</a> as they require a pair of {@code char}s to be represented. If you are needing to
     * support full I18N of your applications consider using {@link #repeat(String, int)} instead.
     *
     * @param container The String Container
     * @param ch        character to repeat
     * @param repeat    number of times to repeat char, negative treated as zero
     * @return String with repeated character
     * @see #repeat(String, int)
     */
    public static void repeat(final Appendable container, final char ch, final int repeat)
            throws IOException {
        for (int i = repeat - 1; i >= 0; i--) {
            container.append(ch);
        }
    }

    /**
     * Right pad a String with spaces (' ').
     *
     * <p>The String is padded to the size of {@code size}.
     *
     * <pre>
     * StringHelper.rightPad(null, *)   = null
     * StringHelper.rightPad("", 3)     = "   "
     * StringHelper.rightPad("bat", 3)  = "bat"
     * StringHelper.rightPad("bat", 5)  = "bat  "
     * StringHelper.rightPad("bat", 1)  = "bat"
     * StringHelper.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size the size to pad to
     * @return right padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * Right pad a String with a specified character.
     *
     * <p>The String is padded to the size of {@code size}.
     *
     * <pre>
     * StringHelper.rightPad(null, *, *)     = null
     * StringHelper.rightPad("", 3, 'z')     = "zzz"
     * StringHelper.rightPad("bat", 3, 'z')  = "bat"
     * StringHelper.rightPad("bat", 5, 'z')  = "batzz"
     * StringHelper.rightPad("bat", 1, 'z')  = "bat"
     * StringHelper.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return right padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * Right pad a String with a specified String.
     *
     * <p>The String is padded to the size of {@code size}.
     *
     * <pre>
     * StringHelper.rightPad(null, *, *)      = null
     * StringHelper.rightPad("", 3, "z")      = "zzz"
     * StringHelper.rightPad("bat", 3, "yz")  = "bat"
     * StringHelper.rightPad("bat", 5, "yz")  = "batyz"
     * StringHelper.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringHelper.rightPad("bat", 1, "yz")  = "bat"
     * StringHelper.rightPad("bat", -1, "yz") = "bat"
     * StringHelper.rightPad("bat", 5, null)  = "bat  "
     * StringHelper.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (Whether.empty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    // Centering
    // -----------------------------------------------------------------------

    /**
     * Left pad a String with spaces (' ').
     *
     * <p>The String is padded to the size of {@code size}.
     *
     * <pre>
     * StringHelper.leftPad(null, *)   = null
     * StringHelper.leftPad("", 3)     = "   "
     * StringHelper.leftPad("bat", 3)  = "bat"
     * StringHelper.leftPad("bat", 5)  = "  bat"
     * StringHelper.leftPad("bat", 1)  = "bat"
     * StringHelper.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size the size to pad to
     * @return left padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * Left pad a String with a specified character.
     *
     * <p>Pad to a size of {@code size}.
     *
     * <pre>
     * StringHelper.leftPad(null, *, *)     = null
     * StringHelper.leftPad("", 3, 'z')     = "zzz"
     * StringHelper.leftPad("bat", 3, 'z')  = "bat"
     * StringHelper.leftPad("bat", 5, 'z')  = "zzbat"
     * StringHelper.leftPad("bat", 1, 'z')  = "bat"
     * StringHelper.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return left padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * Left pad a String with a specified String.
     *
     * <p>Pad to a size of {@code size}.
     *
     * <pre>
     * StringHelper.leftPad(null, *, *)      = null
     * StringHelper.leftPad("", 3, "z")      = "zzz"
     * StringHelper.leftPad("bat", 3, "yz")  = "bat"
     * StringHelper.leftPad("bat", 5, "yz")  = "yzbat"
     * StringHelper.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringHelper.leftPad("bat", 1, "yz")  = "bat"
     * StringHelper.leftPad("bat", -1, "yz") = "bat"
     * StringHelper.leftPad("bat", 5, null)  = "  bat"
     * StringHelper.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary, {@code null} if null
     * String input
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (Whether.empty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    // Case conversion
    // -----------------------------------------------------------------------

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is {@code null}.
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * Centers a String in a larger String of size {@code size} using the space character (' ').
     *
     * <p>
     *
     * <p>If the size is less than the String length, the String is returned. A {@code null} String
     * returns {@code null}. A negative size is treated as zero.
     *
     * <p>Equivalent to {@code center(str, size, " ")}.
     *
     * <pre>
     * StringHelper.center(null, *)   = null
     * StringHelper.center("", 4)     = "    "
     * StringHelper.center("ab", -1)  = "ab"
     * StringHelper.center("ab", 4)   = " ab "
     * StringHelper.center("abcd", 2) = "abcd"
     * StringHelper.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size the int size of new String, negative treated as zero
     * @return centered String, {@code null} if null String input
     */
    public static String center(final String str, final int size) {
        return center(str, size, ' ');
    }

    /**
     * Centers a String in a larger String of size {@code size}. Uses a supplied character as the
     * value to pad the String with.
     *
     * <p>If the size is less than the String length, the String is returned. A {@code null} String
     * returns {@code null}. A negative size is treated as zero.
     *
     * <pre>
     * StringHelper.center(null, *, *)     = null
     * StringHelper.center("", 4, ' ')     = "    "
     * StringHelper.center("ab", -1, ' ')  = "ab"
     * StringHelper.center("ab", 4, ' ')   = " ab "
     * StringHelper.center("abcd", 2, ' ') = "abcd"
     * StringHelper.center("a", 4, ' ')    = " a  "
     * StringHelper.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str     the String to center, may be null
     * @param size    the int size of new String, negative treated as zero
     * @param padChar the character to pad the new String with
     * @return centered String, {@code null} if null String input
     */
    public static String center(String str, final int size, final char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * Centers a String in a larger String of size {@code size}. Uses a supplied String as the value
     * to pad the String with.
     *
     * <p>If the size is less than the String length, the String is returned. A {@code null} String
     * returns {@code null}. A negative size is treated as zero.
     *
     * <pre>
     * StringHelper.center(null, *, *)     = null
     * StringHelper.center("", 4, " ")     = "    "
     * StringHelper.center("ab", -1, " ")  = "ab"
     * StringHelper.center("ab", 4, " ")   = " ab "
     * StringHelper.center("abcd", 2, " ") = "abcd"
     * StringHelper.center("a", 4, " ")    = " a  "
     * StringHelper.center("a", 4, "yz")   = "yayz"
     * StringHelper.center("abc", 7, null) = "  abc  "
     * StringHelper.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    the String to center, may be null
     * @param size   the int size of new String, negative treated as zero
     * @param padStr the String to pad the new String with, must not be null or empty
     * @return centered String, {@code null} if null String input
     * @throws IllegalArgumentException if padStr is {@code null} or empty
     */
    public static String center(String str, final int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (Whether.empty(padStr)) {
            padStr = SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    /**
     * Converts a String to upper case as per {@link String#toUpperCase()}.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.upperCase(null)  = null
     * StringHelper.upperCase("")    = ""
     * StringHelper.upperCase("aBc") = "ABC"
     * </pre>
     *
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toUpperCase()},
     * the result of this method is affected by the current locale. For platform-independent case
     * transformations, the method {@link #lowerCase(String, Locale)} should be used with a specific
     * locale (e.g. {@link Locale#ENGLISH}).
     *
     * @param str the String to upper case, may be null
     * @return the upper cased String, {@code null} if null String input
     */
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * Converts a String to upper case as per {@link String#toUpperCase(Locale)}.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.upperCase(null, Locale.ENGLISH)  = null
     * StringHelper.upperCase("", Locale.ENGLISH)    = ""
     * StringHelper.upperCase("aBc", Locale.ENGLISH) = "ABC"
     * </pre>
     *
     * @param str    the String to upper case, may be null
     * @param locale the locale that defines the case transformation rules, must not be null
     * @return the upper cased String, {@code null} if null String input
     */
    public static String upperCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    /**
     * Converts a String to lower case as per {@link String#toLowerCase()}.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.lowerCase(null)  = null
     * StringHelper.lowerCase("")    = ""
     * StringHelper.lowerCase("aBc") = "abc"
     * </pre>
     *
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toLowerCase()},
     * the result of this method is affected by the current locale. For platform-independent case
     * transformations, the method {@link #lowerCase(String, Locale)} should be used with a specific
     * locale (e.g. {@link Locale#ENGLISH}).
     *
     * @param str the String to lower case, may be null
     * @return the lower cased String, {@code null} if null String input
     */
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    // Count matches
    // -----------------------------------------------------------------------

    /**
     * Converts a String to lower case as per {@link String#toLowerCase(Locale)}.
     *
     * <p>A {@code null} input String returns {@code null}.
     *
     * <pre>
     * StringHelper.lowerCase(null, Locale.ENGLISH)  = null
     * StringHelper.lowerCase("", Locale.ENGLISH)    = ""
     * StringHelper.lowerCase("aBc", Locale.ENGLISH) = "abc"
     * </pre>
     *
     * @param str    the String to lower case, may be null
     * @param locale the locale that defines the case transformation rules, must not be null
     * @return the lower cased String, {@code null} if null String input
     */
    public static String lowerCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    // Character Tests
    // -----------------------------------------------------------------------

    /**
     * Capitalizes a String changing the first letter to title case as per {@link
     * Character#toTitleCase(char)}. No other letters are changed.
     *
     * <p>For a word based algorithm, see {@link #capitalize(String)}. A {@code null} input String
     * returns {@code null}.
     *
     * <pre>
     * StringHelper.capitalize(null)  = null
     * StringHelper.capitalize("")    = ""
     * StringHelper.capitalize("cat") = "Cat"
     * StringHelper.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @see #capitalize(String)
     * @see #uncapitalize(String)
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return Character.toTitleCase(firstChar) + str.substring(1);
    }

    /**
     * Uncapitalizes a String changing the first letter to title case as per {@link
     * Character#toLowerCase(char)}. No other letters are changed.
     *
     * <p>For a word based algorithm, see {@link #uncapitalize(String)}. A {@code null} input String
     * returns {@code null}.
     *
     * <pre>
     * StringHelper.uncapitalize(null)  = null
     * StringHelper.uncapitalize("")    = ""
     * StringHelper.uncapitalize("Cat") = "cat"
     * StringHelper.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str the String to uncapitalize, may be null
     * @return the uncapitalized String, {@code null} if null String input
     * @see #uncapitalize(String)
     * @see #capitalize(String)
     */
    public static String uncapitalize(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            // already uncapitalized
            return str;
        }

        return Character.toLowerCase(firstChar) + str.substring(1);
    }

    /**
     * Swaps the case of a String changing upper and title case to lower case, and lower case to upper
     * case.
     *
     * <ul>
     *   <li>Upper case character converts to Lower case
     *   <li>Title case character converts to Lower case
     *   <li>Lower case character converts to Upper case
     * </ul>
     *
     * <p>For a word based algorithm, see {@link #swapCase(String)}. A {@code null} input String
     * returns {@code null}.
     *
     * <pre>
     * StringHelper.swapCase(null)                 = null
     * StringHelper.swapCase("")                   = ""
     * StringHelper.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0. It no longer performs a word based algorithm.
     * If you only use ASCII, you will notice no change. That functionality is available in .
     *
     * @param str the String to swap case, may be null
     * @return the changed String, {@code null} if null String input
     */
    public static String swapCase(final String str) {
        if (Whether.empty(str)) {
            return str;
        }

        final char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    /**
     * Counts how many times the substring appears in the larger string.
     *
     * <p>A {@code null} or empty ("") String input returns {@code 0}.
     *
     * <pre>
     * StringHelper.countMatches(null, *)       = 0
     * StringHelper.countMatches("", *)         = 0
     * StringHelper.countMatches("abba", null)  = 0
     * StringHelper.countMatches("abba", "")    = 0
     * StringHelper.countMatches("abba", "a")   = 2
     * StringHelper.countMatches("abba", "ab")  = 1
     * StringHelper.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str the CharSequence to check, may be null
     * @param sub the substring to count, may be null
     * @return the number of occurrences, 0 if either CharSequence is {@code null}
     * CharSequence)
     */
    public static int countMatches(final CharSequence str, final CharSequence sub) {
        if (Whether.empty(str) || Whether.empty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = CharSequenceUtils.indexOf(str, sub, idx)) != INDEX_NOT_FOUND) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * Checks if the CharSequence contains only Unicode letters.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code false}.
     *
     * <pre>
     * StringHelper.isAlpha(null)   = false
     * StringHelper.isAlpha("")     = false
     * StringHelper.isAlpha("  ")   = false
     * StringHelper.isAlpha("abc")  = true
     * StringHelper.isAlpha("ab2c") = false
     * StringHelper.isAlpha("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters, and is non-null
     */
    public static boolean isAlpha(final CharSequence cs) {
        if (Whether.empty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only Unicode letters and space (' ').
     *
     * <p>{@code null} will return {@code false} An empty CharSequence (length()=0) will return {@code
     * true}.
     *
     * <pre>
     * StringHelper.isAlphaSpace(null)   = false
     * StringHelper.isAlphaSpace("")     = true
     * StringHelper.isAlphaSpace("  ")   = true
     * StringHelper.isAlphaSpace("abc")  = true
     * StringHelper.isAlphaSpace("ab c") = true
     * StringHelper.isAlphaSpace("ab2c") = false
     * StringHelper.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters and space, and is non-null
     */
    public static boolean isAlphaSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only Unicode letters or digits.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code false}.
     *
     * <pre>
     * StringHelper.isAlphanumeric(null)   = false
     * StringHelper.isAlphanumeric("")     = false
     * StringHelper.isAlphanumeric("  ")   = false
     * StringHelper.isAlphanumeric("abc")  = true
     * StringHelper.isAlphanumeric("ab c") = false
     * StringHelper.isAlphanumeric("ab2c") = true
     * StringHelper.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters or digits, and is non-null
     */
    public static boolean isAlphanumeric(final CharSequence cs) {
        if (Whether.empty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only Unicode letters, digits or space ({@code ' '}).
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code true}.
     *
     * <pre>
     * StringHelper.isAlphanumericSpace(null)   = false
     * StringHelper.isAlphanumericSpace("")     = true
     * StringHelper.isAlphanumericSpace("  ")   = true
     * StringHelper.isAlphanumericSpace("abc")  = true
     * StringHelper.isAlphanumericSpace("ab c") = true
     * StringHelper.isAlphanumericSpace("ab2c") = true
     * StringHelper.isAlphanumericSpace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters, digits or space, and is non-null
     * isAlphanumericSpace(CharSequence)
     */
    public static boolean isAlphanumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only ASCII printable characters.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code true}.
     *
     * <pre>
     * StringHelper.isAsciiPrintable(null)     = false
     * StringHelper.isAsciiPrintable("")       = true
     * StringHelper.isAsciiPrintable(" ")      = true
     * StringHelper.isAsciiPrintable("Ceki")   = true
     * StringHelper.isAsciiPrintable("ab2c")   = true
     * StringHelper.isAsciiPrintable("!ab-c~") = true
     * StringHelper.isAsciiPrintable("\u0020") = true
     * StringHelper.isAsciiPrintable("\u0021") = true
     * StringHelper.isAsciiPrintable("\u007e") = true
     * StringHelper.isAsciiPrintable("\u007f") = false
     * StringHelper.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if every character is in the range 32 thru 126
     */
    public static boolean isAsciiPrintable(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!isAsciiPrintable(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only Unicode digits. A decimal point is not a Unicode digit
     * and returns false.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code false}.
     *
     * <p>Note that the method does not allow for a leading sign, either positive or negative. Also,
     * if a String passes the numeric test, it may still generate a NumberFormatException when parsed
     * by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range for int or long
     * respectively.
     *
     * <pre>
     * StringHelper.isNumeric(null)   = false
     * StringHelper.isNumeric("")     = false
     * StringHelper.isNumeric("  ")   = false
     * StringHelper.isNumeric("123")  = true
     * StringHelper.isNumeric("12 3") = false
     * StringHelper.isNumeric("ab2c") = false
     * StringHelper.isNumeric("12-3") = false
     * StringHelper.isNumeric("12.3") = false
     * StringHelper.isNumeric("-123") = false
     * StringHelper.isNumeric("+123") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains digits, and is non-null
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (Whether.empty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    // -----------------------------------------------------------------------

    /**
     * Checks if the CharSequence contains only Unicode digits or space ({@code ' '}). A decimal point
     * is not a Unicode digit and returns false.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code true}.
     *
     * <pre>
     * StringHelper.isNumericSpace(null)   = false
     * StringHelper.isNumericSpace("")     = true
     * StringHelper.isNumericSpace("  ")   = true
     * StringHelper.isNumericSpace("123")  = true
     * StringHelper.isNumericSpace("12 3") = true
     * StringHelper.isNumericSpace("ab2c") = false
     * StringHelper.isNumericSpace("12-3") = false
     * StringHelper.isNumericSpace("12.3") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains digits or space, and is non-null
     */
    public static boolean isNumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only whitespace.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code true}.
     *
     * <pre>
     * StringHelper.isWhitespace(null)   = false
     * StringHelper.isWhitespace("")     = true
     * StringHelper.isWhitespace("  ")   = true
     * StringHelper.isWhitespace("abc")  = false
     * StringHelper.isWhitespace("ab2c") = false
     * StringHelper.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains whitespace, and is non-null
     */
    public static boolean isWhitespace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only lowercase characters.
     *
     * <p>{@code null} will return {@code false}. An empty CharSequence (length()=0) will return
     * {@code false}.
     *
     * <pre>
     * StringHelper.isAllLowerCase(null)   = false
     * StringHelper.isAllLowerCase("")     = false
     * StringHelper.isAllLowerCase("  ")   = false
     * StringHelper.isAllLowerCase("abc")  = true
     * StringHelper.isAllLowerCase("abC") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains lowercase characters, and is non-null
     */
    public static boolean isAllLowerCase(final CharSequence cs) {
        if (cs == null || Whether.empty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLowerCase(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the CharSequence contains only uppercase characters.
     *
     * <p>{@code null} will return {@code false}. An empty String (length()=0) will return {@code
     * false}.
     *
     * <pre>
     * StringHelper.isAllUpperCase(null)   = false
     * StringHelper.isAllUpperCase("")     = false
     * StringHelper.isAllUpperCase("  ")   = false
     * StringHelper.isAllUpperCase("ABC")  = true
     * StringHelper.isAllUpperCase("aBC") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains uppercase characters, and is non-null
     */
    public static boolean isAllUpperCase(final CharSequence cs) {
        if (cs == null || Whether.empty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isUpperCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reverses a String as per {@link StringBuilder#reverse()}.
     *
     * <p>A {@code null} String returns {@code null}.
     *
     * <pre>
     * StringHelper.reverse(null)  = null
     * StringHelper.reverse("")    = ""
     * StringHelper.reverse("bat") = "tab"
     * </pre>
     *
     * @param str the String to reverse, may be null
     * @return the reversed String, {@code null} if null String input
     */
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    // Difference
    // -----------------------------------------------------------------------

    /**
     * Reverses a String that is delimited by a specific character.
     *
     * <p>The Strings between the delimiters are not reversed. Thus java.lang.String becomes
     * String.lang.java (if the delimiter is {@code '.'}).
     *
     * <pre>
     * StringHelper.reverseDelimited(null, *)      = null
     * StringHelper.reverseDelimited("", *)        = ""
     * StringHelper.reverseDelimited("a.b.c", 'x') = "a.b.c"
     * StringHelper.reverseDelimited("a.b.c", ".") = "c.b.a"
     * </pre>
     *
     * @param str           the String to reverse, may be null
     * @param separatorChar the separator character to use
     * @return the reversed String, {@code null} if null String input
     */
    public static String reverseDelimited(final String str, final char separatorChar) {
        if (str == null) {
            return null;
        }
        // could implement manually, but simple way is to reuse other,
        // probably slower, methods.
        final String[] strs = split(str, separatorChar);
        ToolArray.reverse(strs);
        return join(strs, separatorChar);
    }

    /**
     * Abbreviates a String using ellipses. This will turn "Now is the time for all good men" into
     * "Now is the time for..."
     *
     * <p>Specifically:
     *
     * <ul>
     *   <li>If {@code str} is less than {@code maxWidth} characters long, return it.
     *   <li>Else abbreviate it to {@code (substring(str, 0, max-3) + "...")}.
     *   <li>If {@code maxWidth} is less than {@code 4}, throw an {@code IllegalArgumentException}.
     *   <li>In no case will it return a String of length greater than {@code maxWidth}.
     * </ul>
     *
     * <pre>
     * StringHelper.abbreviate(null, *)      = null
     * StringHelper.abbreviate("", 4)        = ""
     * StringHelper.abbreviate("abcdefg", 6) = "abc..."
     * StringHelper.abbreviate("abcdefg", 7) = "abcdefg"
     * StringHelper.abbreviate("abcdefg", 8) = "abcdefg"
     * StringHelper.abbreviate("abcdefg", 4) = "a..."
     * StringHelper.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str      the String to check, may be null
     * @param maxWidth maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     */
    public static String abbreviate(final String str, final int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * Abbreviates a String using ellipses. This will turn "Now is the time for all good men" into
     * "...is the time for..."
     *
     * <p>Works like {@code abbreviate(String, int)}, but allows you to specify a "left edge" offset.
     * Note that this left edge is not necessarily going to be the leftmost character in the result,
     * or the first character following the ellipses, but it will appear somewhere in the result.
     *
     * <p>In no case will it return a String of length greater than {@code maxWidth}.
     *
     * <pre>
     * StringHelper.abbreviate(null, *, *)                = null
     * StringHelper.abbreviate("", 0, 4)                  = ""
     * StringHelper.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringHelper.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringHelper.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringHelper.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringHelper.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringHelper.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringHelper.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringHelper.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringHelper.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringHelper.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringHelper.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str      the String to check, may be null
     * @param offset   left edge of source String
     * @param maxWidth maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     */
    public static String abbreviate(final String str, int offset, final int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        final String abrevMarker = "...";
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + abrevMarker;
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + maxWidth - 3 < str.length()) {
            return abrevMarker + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return abrevMarker + str.substring(str.length() - (maxWidth - 3));
    }

    /**
     * Abbreviates a String to the length passed, replacing the middle characters with the supplied
     * replacement String.
     *
     * <p>This abbreviation only occurs if the following criteria is met:
     *
     * <ul>
     *   <li>Neither the String for abbreviation nor the replacement String are null or empty
     *   <li>The length to truncate to is less than the length of the supplied String
     *   <li>The length to truncate to is greater than 0
     *   <li>The abbreviated String will have enough room for the length supplied replacement String
     *       and the first and last characters of the supplied String for abbreviation
     * </ul>
     * <p>
     * Otherwise, the returned String will be the same as the supplied String for abbreviation.
     *
     * <pre>
     * StringHelper.abbreviateMiddle(null, null, 0)      = null
     * StringHelper.abbreviateMiddle("abc", null, 0)      = "abc"
     * StringHelper.abbreviateMiddle("abc", ".", 0)      = "abc"
     * StringHelper.abbreviateMiddle("abc", ".", 3)      = "abc"
     * StringHelper.abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
     * </pre>
     *
     * @param str    the String to abbreviate, may be null
     * @param middle the String to replace the middle characters with, may be null
     * @param length the length to abbreviate {@code str} to.
     * @return the abbreviated String if the above criteria is met, or the original String supplied
     * for abbreviation.
     */
    public static String abbreviateMiddle(final String str, final String middle, final int length) {
        if (Whether.empty(str) || Whether.empty(middle)) {
            return str;
        }

        if (length >= str.length() || length < middle.length() + 2) {
            return str;
        }

        final int targetSting = length - middle.length();
        final int startOffset = targetSting / 2 + targetSting % 2;
        final int endOffset = str.length() - targetSting / 2;

        return str.substring(0, startOffset) + middle + str.substring(endOffset);
    }

    // Misc
    // -----------------------------------------------------------------------

    /**
     * Compares two Strings, and returns the portion where they differ. More precisely, return the
     * remainder of the second String, starting from where it's different from the first. This means
     * that the difference between "abc" and "ab" is the empty String and not "c".
     *
     * <p>For example, {@code difference("i am a machine", "i am a robot") -> "robot"}.
     *
     * <pre>
     * StringHelper.difference(null, null) = null
     * StringHelper.difference("", "") = ""
     * StringHelper.difference("", "abc") = "abc"
     * StringHelper.difference("abc", "") = ""
     * StringHelper.difference("abc", "abc") = ""
     * StringHelper.difference("abc", "ab") = ""
     * StringHelper.difference("ab", "abxyz") = "xyz"
     * StringHelper.difference("abcde", "abxyz") = "xyz"
     * StringHelper.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return the portion of str2 where it differs from str1; returns the empty String if they are
     * equal
     * @see #indexOfDifference(CharSequence, CharSequence)
     */
    public static String difference(final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference(str1, str2);
        if (at == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * Compares two CharSequences, and returns the index at which the CharSequences begin to differ.
     *
     * <p>For example, {@code indexOfDifference("i am a machine", "i am a robot") -> 7}
     *
     * <pre>
     * StringHelper.indexOfDifference(null, null) = -1
     * StringHelper.indexOfDifference("", "") = -1
     * StringHelper.indexOfDifference("", "abc") = 0
     * StringHelper.indexOfDifference("abc", "") = 0
     * StringHelper.indexOfDifference("abc", "abc") = -1
     * StringHelper.indexOfDifference("ab", "abxyz") = 2
     * StringHelper.indexOfDifference("abcde", "abxyz") = 2
     * StringHelper.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param cs1 the first CharSequence, may be null
     * @param cs2 the second CharSequence, may be null
     * @return the index where cs1 and cs2 begin to differ; -1 if they are equal
     * indexOfDifference(CharSequence, CharSequence)
     */
    public static int indexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    // startsWith
    // -----------------------------------------------------------------------

    /**
     * Compares all CharSequences in an array and returns the index at which the CharSequences begin
     * to differ.
     *
     * <p>For example, <code>indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -> 7
     * </code>
     *
     * <pre>
     * StringHelper.indexOfDifference(null) = -1
     * StringHelper.indexOfDifference(new String[] {}) = -1
     * StringHelper.indexOfDifference(new String[] {"abc"}) = -1
     * StringHelper.indexOfDifference(new String[] {null, null}) = -1
     * StringHelper.indexOfDifference(new String[] {"", ""}) = -1
     * StringHelper.indexOfDifference(new String[] {"", null}) = 0
     * StringHelper.indexOfDifference(new String[] {"abc", null, null}) = 0
     * StringHelper.indexOfDifference(new String[] {null, null, "abc"}) = 0
     * StringHelper.indexOfDifference(new String[] {"", "abc"}) = 0
     * StringHelper.indexOfDifference(new String[] {"abc", ""}) = 0
     * StringHelper.indexOfDifference(new String[] {"abc", "abc"}) = -1
     * StringHelper.indexOfDifference(new String[] {"abc", "a"}) = 1
     * StringHelper.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
     * StringHelper.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
     * StringHelper.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
     * StringHelper.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
     * StringHelper.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
     * </pre>
     *
     * @param css array of CharSequences, entries may be null
     * @return the index where the strings begin to differ; -1 if they are all equal
     * indexOfDifference(CharSequence...)
     */
    public static int indexOfDifference(final CharSequence... css) {
        if (css == null || css.length <= 1) {
            return INDEX_NOT_FOUND;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        final int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;

        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (CharSequence charSequence : css) {
            if (charSequence == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(charSequence.length(), shortestStrLen);
                longestStrLen = Math.max(charSequence.length(), longestStrLen);
            }
        }

        // handle lists containing all nulls or all empty strings
        if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return INDEX_NOT_FOUND;
        }

        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }

        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            final char comparisonChar = css[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }

        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }

    /**
     * Compares all Strings in an array and returns the initial sequence of characters that is common
     * to all of them.
     *
     * <p>For example, <code>
     * getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) -> "i am a "</code>
     *
     * <pre>
     * StringHelper.getCommonPrefix(null) = ""
     * StringHelper.getCommonPrefix(new String[] {}) = ""
     * StringHelper.getCommonPrefix(new String[] {"abc"}) = "abc"
     * StringHelper.getCommonPrefix(new String[] {null, null}) = ""
     * StringHelper.getCommonPrefix(new String[] {"", ""}) = ""
     * StringHelper.getCommonPrefix(new String[] {"", null}) = ""
     * StringHelper.getCommonPrefix(new String[] {"abc", null, null}) = ""
     * StringHelper.getCommonPrefix(new String[] {null, null, "abc"}) = ""
     * StringHelper.getCommonPrefix(new String[] {"", "abc"}) = ""
     * StringHelper.getCommonPrefix(new String[] {"abc", ""}) = ""
     * StringHelper.getCommonPrefix(new String[] {"abc", "abc"}) = "abc"
     * StringHelper.getCommonPrefix(new String[] {"abc", "a"}) = "a"
     * StringHelper.getCommonPrefix(new String[] {"ab", "abxyz"}) = "ab"
     * StringHelper.getCommonPrefix(new String[] {"abcde", "abxyz"}) = "ab"
     * StringHelper.getCommonPrefix(new String[] {"abcde", "xyz"}) = ""
     * StringHelper.getCommonPrefix(new String[] {"xyz", "abcde"}) = ""
     * StringHelper.getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) = "i am a "
     * </pre>
     *
     * @param strs array of String objects, entries may be null
     * @return the initial sequence of characters that are common to all Strings in the array; empty
     * String if the array is null, the elements are all null or if there is no common prefix.
     */
    public static String getCommonPrefix(final String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }
        final int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == INDEX_NOT_FOUND) {
            // all strings were identical
            if (strs[0] == null) {
                return EMPTY;
            }
            return strs[0];
        } else if (smallestIndexOfDiff == 0) {
            // there were no common initial characters
            return EMPTY;
        } else {
            // we found a common initial character sequence
            return strs[0].substring(0, smallestIndexOfDiff);
        }
    }

    /**
     * Find the Levenshtein distance between two Strings.
     *
     * <p>This is the number of changes needed to change one String into another, where each change is
     * a single character modification (deletion, insertion or substitution).
     *
     * <p>The previous implementation of the Levenshtein distance algorithm was from <a
     * href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>
     *
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError which
     * can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm is from <a
     * href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a>
     *
     * <pre>
     * StringHelper.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringHelper.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringHelper.getLevenshteinDistance("","")               = 0
     * StringHelper.getLevenshteinDistance("","a")              = 1
     * StringHelper.getLevenshteinDistance("aaapppp", "")       = 7
     * StringHelper.getLevenshteinDistance("frog", "fog")       = 1
     * StringHelper.getLevenshteinDistance("fly", "ant")        = 3
     * StringHelper.getLevenshteinDistance("elephant", "hippo") = 7
     * StringHelper.getLevenshteinDistance("hippo", "elephant") = 7
     * StringHelper.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringHelper.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s the first String, must not be null
     * @param t the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     *                                  getLevenshteinDistance(CharSequence, CharSequence)
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

    /*
      The difference between this impl. and the previous is that, rather
      than creating and retaining a matrix of size s.length() + 1 by t.length() + 1,
      we maintain two single-dimensional arrays of length s.length() + 1.  The first, d,
      is the 'current working' distance array that maintains the newest distance cost
      counts as we iterate through the characters of String s.  Each time we increment
      the index of String t we are comparing, d is copied to p, the second int[].  Doing so
      allows us to retain the previous cost counts as required by the algorithm (taking
      the minimum of the cost count to the left, up one, and diagonally up and to the left
      of the current cost count being calculated).  (Note that the arrays aren't really
      copied anymore, just switched...this is clearly much better than cloning an array
      or doing a System.arraycopy() each time  through the outer loop.)

      Effectively, the difference between the two implementations is this one does not
      cause an out of memory condition when calculating the LD over two very large strings.
    */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int[] p = new int[n + 1]; // 'previous' cost array, horizontally
        int[] d = new int[n + 1]; // cost array, horizontally
        int[] _d; // placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * Find the Levenshtein distance between two Strings if it's less than or equal to a given
     * threshold.
     *
     * <p>This is the number of changes needed to change one String into another, where each change is
     * a single character modification (deletion, insertion or substitution).
     *
     * <p>This implementation follows from Algorithms on Strings, Trees and Sequences by Dan Gusfield
     * and Chas Emerick's implementation of the Levenshtein distance algorithm from <a
     * href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>
     *
     * <pre>
     * StringHelper.getLevenshteinDistance(null, *, *)             = IllegalArgumentException
     * StringHelper.getLevenshteinDistance(*, null, *)             = IllegalArgumentException
     * StringHelper.getLevenshteinDistance(*, *, -1)               = IllegalArgumentException
     * StringHelper.getLevenshteinDistance("","", 0)               = 0
     * StringHelper.getLevenshteinDistance("aaapppp", "", 8)       = 7
     * StringHelper.getLevenshteinDistance("aaapppp", "", 7)       = 7
     * StringHelper.getLevenshteinDistance("aaapppp", "", 6))      = -1
     * StringHelper.getLevenshteinDistance("elephant", "hippo", 7) = 7
     * StringHelper.getLevenshteinDistance("elephant", "hippo", 6) = -1
     * StringHelper.getLevenshteinDistance("hippo", "elephant", 7) = 7
     * StringHelper.getLevenshteinDistance("hippo", "elephant", 6) = -1
     * </pre>
     *
     * @param s         the first String, must not be null
     * @param t         the second String, must not be null
     * @param threshold the target threshold, must not be negative
     * @return result distance, or {@code -1} if the distance would be greater than the threshold
     * @throws IllegalArgumentException if either String input {@code null} or negative threshold
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, final int threshold) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }

    /*
    This implementation only computes the distance if it's less than or equal to the
    threshold value, returning -1 if it's greater.  The advantage is performance: unbounded
    distance is O(nm), but a bound of k allows us to reduce it to O(km) time by only
    computing a diagonal stripe of width 2k + 1 of the cost table.
    It is also possible to use this to compute the unbounded Levenshtein distance by starting
    the threshold at 1 and doubling each time until the distance is found; this is O(dm), where
    d is the distance.

    One subtlety comes from needing to ignore entries on the border of our stripe
    eg.
    p[] = |#|#|#|*
    d[] =  *|#|#|#|
    We must ignore the entry to the left of the leftmost member
    We must ignore the entry above the rightmost member

    Another subtlety comes from our stripe running off the matrix if the strings aren't
    of the same size.  Since string s is always swapped to be the shorter of the two,
    the stripe will always run off to the upper right instead of the lower left of the matrix.

    As a concrete example, suppose s is of length 5, t is of length 7, and our threshold is 1.
    In this case we're going to walk a stripe of length 3.  The matrix would look like so:

       1 2 3 4 5
    1 |#|#| | | |
    2 |#|#|#| | |
    3 | |#|#|#| |
    4 | | |#|#|#|
    5 | | | |#|#|
    6 | | | | |#|
    7 | | | | | |

    Note how the stripe leads off the table as there is no possible way to turn a string of length 5
    into one of length 7 in edit distance of 1.

    Additionally, this implementation decreases memory usage by using two
    single-dimensional arrays and swapping them back and forth instead of allocating
    an entire n by m matrix.  This requires a few minor changes, such as immediately returning
    when it's detected that the stripe has run off the matrix and initially filling the arrays with
    large values so that entries we don't compute are ignored.

    See Algorithms on Strings, Trees and Sequences by Dan Gusfield for some discussion.
     */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m <= threshold ? m : -1;
        } else if (m == 0) {
            return n <= threshold ? n : -1;
        }

        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int[] p = new int[n + 1]; // 'previous' cost array, horizontally
        int[] d = new int[n + 1]; // cost array, horizontally
        int[] _d; // placeholder to assist in swapping p and d

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
            final char t_j = t.charAt(j - 1); // jth character of t
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = Math.min(n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return -1;
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (s.charAt(i - 1) == t_j) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct
        // distance
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }

    // endsWith
    // -----------------------------------------------------------------------

    /**
     * Check if a CharSequence starts with a specified prefix.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered to
     * be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringHelper.startsWith(null, null)      = true
     * StringHelper.startsWith(null, "abc")     = false
     * StringHelper.startsWith("abcdef", null)  = false
     * StringHelper.startsWith("abcdef", "abc") = true
     * StringHelper.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return {@code true} if the CharSequence starts with the prefix, case sensitive, or both {@code
     * null}
     * @see String#startsWith(String)
     * CharSequence)
     */
    public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * Case insensitive check if a CharSequence starts with a specified prefix.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered to
     * be equal. The comparison is case insensitive.
     *
     * <pre>
     * StringHelper.startsWithIgnoreCase(null, null)      = true
     * StringHelper.startsWithIgnoreCase(null, "abc")     = false
     * StringHelper.startsWithIgnoreCase("abcdef", null)  = false
     * StringHelper.startsWithIgnoreCase("abcdef", "abc") = true
     * StringHelper.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param prefix the prefix to find, may be null
     * @return {@code true} if the CharSequence starts with the prefix, case insensitive, or both
     * {@code null}
     * @see String#startsWith(String)
     * startsWithIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * Check if a CharSequence starts with a specified prefix (optionally case insensitive).
     *
     * @param str        the CharSequence to check, may be null
     * @param prefix     the prefix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or both {@code null}
     * @see String#startsWith(String)
     */
    private static boolean startsWith(
            final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * Check if a CharSequence starts with any of an array of specified strings.
     *
     * <pre>
     * StringHelper.startsWithAny(null, null)      = false
     * StringHelper.startsWithAny(null, new String[] {"abc"})  = false
     * StringHelper.startsWithAny("abcxyz", null)     = false
     * StringHelper.startsWithAny("abcxyz", new String[] {""}) = false
     * StringHelper.startsWithAny("abcxyz", new String[] {"abc"}) = true
     * StringHelper.startsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string        the CharSequence to check, may be null
     * @param searchStrings the CharSequences to find, may be null or empty
     * @return {@code true} if the CharSequence starts with any of the the prefixes, case insensitive,
     * or both {@code null}
     * startsWithAny(CharSequence, CharSequence...)
     */
    public static boolean startsWithAny(
            final CharSequence string, final CharSequence... searchStrings) {
        if (Whether.empty(string) || Whether.empty(searchStrings)) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (ToolString.startsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a CharSequence ends with a specified suffix.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered to
     * be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringHelper.endsWith(null, null)      = true
     * StringHelper.endsWith(null, "def")     = false
     * StringHelper.endsWith("abcdef", null)  = false
     * StringHelper.endsWith("abcdef", "def") = true
     * StringHelper.endsWith("ABCDEF", "def") = false
     * StringHelper.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case sensitive, or both {@code
     * null}
     * @see String#endsWith(String)
     * CharSequence)
     */
    public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * Case insensitive check if a CharSequence ends with a specified suffix.
     *
     * <p>{@code null}s are handled without exceptions. Two {@code null} references are considered to
     * be equal. The comparison is case insensitive.
     *
     * <pre>
     * StringHelper.endsWithIgnoreCase(null, null)      = true
     * StringHelper.endsWithIgnoreCase(null, "def")     = false
     * StringHelper.endsWithIgnoreCase("abcdef", null)  = false
     * StringHelper.endsWithIgnoreCase("abcdef", "def") = true
     * StringHelper.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringHelper.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     *
     * @param str    the CharSequence to check, may be null
     * @param suffix the suffix to find, may be null
     * @return {@code true} if the CharSequence ends with the suffix, case insensitive, or both {@code
     * null}
     * @see String#endsWith(String)
     * endsWithIgnoreCase(CharSequence, CharSequence)
     */
    public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * Check if a CharSequence ends with a specified suffix (optionally case insensitive).
     *
     * @param str        the CharSequence to check, may be null
     * @param suffix     the suffix to find, may be null
     * @param ignoreCase indicates whether the compare should ignore case (case insensitive) or not.
     * @return {@code true} if the CharSequence starts with the prefix or both {@code null}
     * @see String#endsWith(String)
     */
    private static boolean endsWith(
            final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        final int strOffset = str.length() - suffix.length();
        return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * Similar to <a
     * href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize
     * -space</a>
     *
     * <p>The function returns the argument string with whitespace normalized by using <code>
     * {@link #trim(String)}</code> to remove leading and trailing whitespace and then replacing
     * sequences of whitespace characters by a single space. In XML Whitespace characters are the same
     * as those allowed by the <a href="http://www.w3.org/TR/REC-xml/#NT-S">S</a> production, which is
     * S ::= (#x20 | #x9 | #xD | #xA)+
     *
     * <p>Java's regexp pattern \s defines whitespace as [ \t\n\x0B\f\r]
     *
     * <p>For reference:
     *
     * <ul>
     *   <li>\x0B = vertical tab
     *   <li>\f = #xC = form feed
     *   <li>#x20 = space
     *   <li>#x9 = \t
     *   <li>#xA = \n
     *   <li>#xD = \r
     * </ul>
     *
     * <p>The difference is that Java's whitespace includes vertical tab and form feed, which this
     * functional will also normalize. Additionally <code>{@link #trim(String)}</code> removes control
     * characters (char &lt;= 32) from both ends of this String.
     *
     * @param str the source String to normalize whitespaces from, may be null
     * @return the modified string with whitespace normalized, {@code null} if null String input
     * @see Pattern
     * @see #trim(String)
     * @see <a
     * href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize-space</a>
     */
    public static String normalizeSpace(final String str) {
        if (str == null) {
            return null;
        }
        return WHITESPACE_PATTERN.matcher(trim(str)).replaceAll(SPACE);
    }

    /**
     * Check if a CharSequence ends with any of an array of specified strings.
     *
     * <pre>
     * StringHelper.endsWithAny(null, null)      = false
     * StringHelper.endsWithAny(null, new String[] {"abc"})  = false
     * StringHelper.endsWithAny("abcxyz", null)     = false
     * StringHelper.endsWithAny("abcxyz", new String[] {""}) = true
     * StringHelper.endsWithAny("abcxyz", new String[] {"xyz"}) = true
     * StringHelper.endsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @param string        the CharSequence to check, may be null
     * @param searchStrings the CharSequences to find, may be null or empty
     * @return {@code true} if the CharSequence ends with any of the the prefixes, case insensitive,
     * or both {@code null}
     */
    public static boolean endsWithAny(
            final CharSequence string, final CharSequence... searchStrings) {
        if (Whether.empty(string) || Whether.empty(searchStrings)) {
            return false;
        }
        for (final CharSequence searchString : searchStrings) {
            if (ToolString.endsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends the suffix to the end of the string if the string does not already end in the suffix.
     *
     * @param str        The string.
     * @param suffix     The suffix to append to the end of the string.
     * @param ignoreCase Indicates whether the compare should ignore case.
     * @param suffixes   Additional suffixes that are valid terminators (optional).
     * @return A new String if suffix was appened, the same string otherwise.
     */
    private static String appendIfMissing(
            final String str,
            final CharSequence suffix,
            final boolean ignoreCase,
            final CharSequence... suffixes) {
        if (str == null || Whether.empty(suffix) || endsWith(str, suffix, ignoreCase)) {
            return str;
        }
        if (suffixes != null && suffixes.length > 0) {
            for (final CharSequence s : suffixes) {
                if (endsWith(str, s, ignoreCase)) {
                    return str;
                }
            }
        }
        return str + suffix;
    }

    /**
     * Appends the suffix to the end of the string if the string does not already end with any the
     * suffixes.
     *
     * <pre>
     * StringHelper.appendIfMissing(null, null) = null
     * StringHelper.appendIfMissing("abc", null) = "abc"
     * StringHelper.appendIfMissing("", "xyz") = "xyz"
     * StringHelper.appendIfMissing("abc", "xyz") = "abcxyz"
     * StringHelper.appendIfMissing("abcxyz", "xyz") = "abcxyz"
     * StringHelper.appendIfMissing("abcXYZ", "xyz") = "abcXYZxyz"
     * </pre>
     *
     * <p>With additional suffixes,
     *
     * <pre>
     * StringHelper.appendIfMissing(null, null, null) = null
     * StringHelper.appendIfMissing("abc", null, null) = "abc"
     * StringHelper.appendIfMissing("", "xyz", null) = "xyz"
     * StringHelper.appendIfMissing("abc", "xyz", new CharSequence[]{null}) = "abcxyz"
     * StringHelper.appendIfMissing("abc", "xyz", "") = "abc"
     * StringHelper.appendIfMissing("abc", "xyz", "mno") = "abcxyz"
     * StringHelper.appendIfMissing("abcxyz", "xyz", "mno") = "abcxyz"
     * StringHelper.appendIfMissing("abcmno", "xyz", "mno") = "abcmno"
     * StringHelper.appendIfMissing("abcXYZ", "xyz", "mno") = "abcXYZxyz"
     * StringHelper.appendIfMissing("abcMNO", "xyz", "mno") = "abcMNOxyz"
     * </pre>
     *
     * @param str      The string.
     * @param suffix   The suffix to append to the end of the string.
     * @param suffixes Additional suffixes that are valid terminators.
     * @return A new String if suffix was appened, the same string otherwise.
     */
    public static String appendIfMissing(
            final String str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * Appends the suffix to the end of the string if the string does not already end, case
     * insensitive, with any of the suffixes.
     *
     * <pre>
     * StringHelper.appendIfMissingIgnoreCase(null, null) = null
     * StringHelper.appendIfMissingIgnoreCase("abc", null) = "abc"
     * StringHelper.appendIfMissingIgnoreCase("", "xyz") = "xyz"
     * StringHelper.appendIfMissingIgnoreCase("abc", "xyz") = "abcxyz"
     * StringHelper.appendIfMissingIgnoreCase("abcxyz", "xyz") = "abcxyz"
     * StringHelper.appendIfMissingIgnoreCase("abcXYZ", "xyz") = "abcXYZ"
     * </pre>
     *
     * <p>With additional suffixes,
     *
     * <pre>
     * StringHelper.appendIfMissingIgnoreCase(null, null, null) = null
     * StringHelper.appendIfMissingIgnoreCase("abc", null, null) = "abc"
     * StringHelper.appendIfMissingIgnoreCase("", "xyz", null) = "xyz"
     * StringHelper.appendIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{null}) = "abcxyz"
     * StringHelper.appendIfMissingIgnoreCase("abc", "xyz", "") = "abc"
     * StringHelper.appendIfMissingIgnoreCase("abc", "xyz", "mno") = "axyz"
     * StringHelper.appendIfMissingIgnoreCase("abcxyz", "xyz", "mno") = "abcxyz"
     * StringHelper.appendIfMissingIgnoreCase("abcmno", "xyz", "mno") = "abcmno"
     * StringHelper.appendIfMissingIgnoreCase("abcXYZ", "xyz", "mno") = "abcXYZ"
     * StringHelper.appendIfMissingIgnoreCase("abcMNO", "xyz", "mno") = "abcMNO"
     * </pre>
     *
     * @param str      The string.
     * @param suffix   The suffix to append to the end of the string.
     * @param suffixes Additional suffixes that are valid terminators.
     * @return A new String if suffix was appened, the same string otherwise.
     */
    public static String appendIfMissingIgnoreCase(
            final String str, final CharSequence suffix, final CharSequence... suffixes) {
        return appendIfMissing(str, suffix, true, suffixes);
    }

    /**
     * Prepends the prefix to the start of the string if the string does not already start with any of
     * the prefixes.
     *
     * @param str        The string.
     * @param prefix     The prefix to prepend to the start of the string.
     * @param ignoreCase Indicates whether the compare should ignore case.
     * @param prefixes   Additional prefixes that are valid (optional).
     * @return A new String if prefix was prepended, the same string otherwise.
     */
    private static String prependIfMissing(
            final String str,
            final CharSequence prefix,
            final boolean ignoreCase,
            final CharSequence... prefixes) {
        if (str == null || Whether.empty(prefix) || startsWith(str, prefix, ignoreCase)) {
            return str;
        }
        if (prefixes != null && prefixes.length > 0) {
            for (final CharSequence p : prefixes) {
                if (startsWith(str, p, ignoreCase)) {
                    return str;
                }
            }
        }
        return prefix + str;
    }

    /**
     * Prepends the prefix to the start of the string if the string does not already start with any of
     * the prefixes.
     *
     * <pre>
     * StringHelper.prependIfMissing(null, null) = null
     * StringHelper.prependIfMissing("abc", null) = "abc"
     * StringHelper.prependIfMissing("", "xyz") = "xyz"
     * StringHelper.prependIfMissing("abc", "xyz") = "xyzabc"
     * StringHelper.prependIfMissing("xyzabc", "xyz") = "xyzabc"
     * StringHelper.prependIfMissing("XYZabc", "xyz") = "xyzXYZabc"
     * </pre>
     *
     * <p>With additional prefixes,
     *
     * <pre>
     * StringHelper.prependIfMissing(null, null, null) = null
     * StringHelper.prependIfMissing("abc", null, null) = "abc"
     * StringHelper.prependIfMissing("", "xyz", null) = "xyz"
     * StringHelper.prependIfMissing("abc", "xyz", new CharSequence[]{null}) = "xyzabc"
     * StringHelper.prependIfMissing("abc", "xyz", "") = "abc"
     * StringHelper.prependIfMissing("abc", "xyz", "mno") = "xyzabc"
     * StringHelper.prependIfMissing("xyzabc", "xyz", "mno") = "xyzabc"
     * StringHelper.prependIfMissing("mnoabc", "xyz", "mno") = "mnoabc"
     * StringHelper.prependIfMissing("XYZabc", "xyz", "mno") = "xyzXYZabc"
     * StringHelper.prependIfMissing("MNOabc", "xyz", "mno") = "xyzMNOabc"
     * </pre>
     *
     * @param str      The string.
     * @param prefix   The prefix to prepend to the start of the string.
     * @param prefixes Additional prefixes that are valid.
     * @return A new String if prefix was prepended, the same string otherwise.
     */
    public static String prependIfMissing(
            final String str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    /**
     * Prepends the prefix to the start of the string if the string does not already start, case
     * insensitive, with any of the prefixes.
     *
     * <pre>
     * StringHelper.prependIfMissingIgnoreCase(null, null) = null
     * StringHelper.prependIfMissingIgnoreCase("abc", null) = "abc"
     * StringHelper.prependIfMissingIgnoreCase("", "xyz") = "xyz"
     * StringHelper.prependIfMissingIgnoreCase("abc", "xyz") = "xyzabc"
     * StringHelper.prependIfMissingIgnoreCase("xyzabc", "xyz") = "xyzabc"
     * StringHelper.prependIfMissingIgnoreCase("XYZabc", "xyz") = "XYZabc"
     * </pre>
     *
     * <p>With additional prefixes,
     *
     * <pre>
     * StringHelper.prependIfMissingIgnoreCase(null, null, null) = null
     * StringHelper.prependIfMissingIgnoreCase("abc", null, null) = "abc"
     * StringHelper.prependIfMissingIgnoreCase("", "xyz", null) = "xyz"
     * StringHelper.prependIfMissingIgnoreCase("abc", "xyz", new CharSequence[]{null}) = "xyzabc"
     * StringHelper.prependIfMissingIgnoreCase("abc", "xyz", "") = "abc"
     * StringHelper.prependIfMissingIgnoreCase("abc", "xyz", "mno") = "xyzabc"
     * StringHelper.prependIfMissingIgnoreCase("xyzabc", "xyz", "mno") = "xyzabc"
     * StringHelper.prependIfMissingIgnoreCase("mnoabc", "xyz", "mno") = "mnoabc"
     * StringHelper.prependIfMissingIgnoreCase("XYZabc", "xyz", "mno") = "XYZabc"
     * StringHelper.prependIfMissingIgnoreCase("MNOabc", "xyz", "mno") = "MNOabc"
     * </pre>
     *
     * @param str      The string.
     * @param prefix   The prefix to prepend to the start of the string.
     * @param prefixes Additional prefixes that are valid (optional).
     * @return A new String if prefix was prepended, the same string otherwise.
     */
    public static String prependIfMissingIgnoreCase(
            final String str, final CharSequence prefix, final CharSequence... prefixes) {
        return prependIfMissing(str, prefix, true, prefixes);
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes       the byte array to read from
     * @param charsetName the encoding to use, if null then use the platform default
     * @return a new String
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @throws NullPointerException         if the input is null
     * @deprecated use {@link ToolString#toEncodedString(byte[], Charset)} instead of String
     * constants in your code
     */
    @Deprecated
    public static String toString(final byte[] bytes, final String charsetName)
            throws UnsupportedEncodingException {
        return charsetName != null
                ? new String(bytes, charsetName)
                : new String(bytes, Charset.defaultCharset());
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes   the byte array to read from
     * @param charset the encoding to use, if null then use the platform default
     * @return a new String
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @throws NullPointerException         if the input is null
     */
    public static String toEncodedString(byte[] bytes, Charset charset)
            throws UnsupportedEncodingException {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

    /**
     * Converts the String to a Character using the first character, returning null for empty Strings.
     *
     * <p>For ASCII 7 bit characters, this uses a cache that will return the same Character object
     * each time.
     *
     * <pre>
     *   toCharacterObject(null) = null
     *   toCharacterObject("")   = null
     *   toCharacterObject("A")  = 'A'
     *   toCharacterObject("BA") = 'B'
     * </pre>
     *
     * @param str the character to convert
     * @return the Character value of the first letter of the String
     */
    public static Character toCharacterObject(final String str) {
        if (Whether.empty(str)) {
            return null;
        }
        return str.charAt(0);
    }

    /**
     * Converts the Character to a char throwing an exception for {@code null}.
     *
     * <pre>
     *   toChar(' ')  = ' '
     *   toChar('A')  = 'A'
     *   toChar(null) throws IllegalArgumentException
     * </pre>
     *
     * @param ch the character to convert
     * @return the char value of the Character
     * @throws IllegalArgumentException if the Character is null
     */
    public static char toChar(final Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The Character must not be null");
        }
        return ch;
    }

    // -----------------------------------------------------------------------

    /**
     * Converts the Character to a char handling {@code null}.
     *
     * <pre>
     *   toChar(null, 'X') = 'X'
     *   toChar(' ', 'X')  = ' '
     *   toChar('A', 'X')  = 'A'
     * </pre>
     *
     * @param ch           the character to convert
     * @param defaultValue the value to use if the Character is null
     * @return the char value of the Character or the default if null
     */
    public static char toChar(final Character ch, final char defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return ch;
    }

    /**
     * Converts the String to a char using the first character, throwing an exception on empty
     * Strings.
     *
     * <pre>
     *   toChar("A")  = 'A'
     *   toChar("BA") = 'B'
     *   toChar(null) throws IllegalArgumentException
     *   toChar("")   throws IllegalArgumentException
     * </pre>
     *
     * @param str the character to convert
     * @return the char value of the first letter of the String
     * @throws IllegalArgumentException if the String is empty
     */
    public static char toChar(final String str) {
        if (Whether.empty(str)) {
            throw new IllegalArgumentException("The String must not be empty");
        }
        return str.charAt(0);
    }

    // -----------------------------------------------------------------------

    /**
     * Converts the String to a char using the first character, defaulting the value on empty Strings.
     *
     * <pre>
     *   toChar(null, 'X') = 'X'
     *   toChar("", 'X')   = 'X'
     *   toChar("A", 'X')  = 'A'
     *   toChar("BA", 'X') = 'B'
     * </pre>
     *
     * @param str          the character to convert
     * @param defaultValue the value to use if the Character is null
     * @return the char value of the first letter of the String or the default if null
     */
    public static char toChar(final String str, final char defaultValue) {
        if (Whether.empty(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }

    /**
     * Converts the character to the Integer it represents, throwing an exception if the character is
     * not numeric.
     *
     * <p>This method coverts the char '1' to the int 1 and so on.
     *
     * <pre>
     *   toIntValue('3')  = 3
     *   toIntValue('A')  throws IllegalArgumentException
     * </pre>
     *
     * @param ch the character to convert
     * @return the int value of the character
     * @throws IllegalArgumentException if the character is not ASCII numeric
     */
    public static int toIntValue(final char ch) {
        if (!isAsciiNumeric(ch)) {
            throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
        }
        return ch - 48;
    }

    // -----------------------------------------------------------------------

    /**
     * Converts the character to the Integer it represents, throwing an exception if the character is
     * not numeric.
     *
     * <p>This method coverts the char '1' to the int 1 and so on.
     *
     * <pre>
     *   toIntValue('3', -1)  = 3
     *   toIntValue('A', -1)  = -1
     * </pre>
     *
     * @param ch           the character to convert
     * @param defaultValue the default value to use if the character is not numeric
     * @return the int value of the character
     */
    public static int toIntValue(final char ch, final int defaultValue) {
        if (!isAsciiNumeric(ch)) {
            return defaultValue;
        }
        return ch - 48;
    }

    /**
     * Converts the character to the Integer it represents, throwing an exception if the character is
     * not numeric.
     *
     * <p>This method coverts the char '1' to the int 1 and so on.
     *
     * <pre>
     *   toIntValue('3')  = 3
     *   toIntValue(null) throws IllegalArgumentException
     *   toIntValue('A')  throws IllegalArgumentException
     * </pre>
     *
     * @param ch the character to convert, not null
     * @return the int value of the character
     * @throws IllegalArgumentException if the Character is not ASCII numeric or is null
     */
    public static int toIntValue(final Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The character must not be null");
        }
        return toIntValue(ch.charValue());
    }

    /**
     * Converts the character to the Integer it represents, throwing an exception if the character is
     * not numeric.
     *
     * <p>This method coverts the char '1' to the int 1 and so on.
     *
     * <pre>
     *   toIntValue(null, -1) = -1
     *   toIntValue('3', -1)  = 3
     *   toIntValue('A', -1)  = -1
     * </pre>
     *
     * @param ch           the character to convert
     * @param defaultValue the default value to use if the character is not numeric
     * @return the int value of the character
     */
    public static int toIntValue(final Character ch, final int defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return toIntValue(ch.charValue(), defaultValue);
    }

    /**
     * Converts the character to a String that contains the one character.
     *
     * <p>For ASCII 7 bit characters, this uses a cache that will return the same String object each
     * time.
     *
     * <pre>
     *   toString(' ')  = " "
     *   toString('A')  = "A"
     * </pre>
     *
     * @param ch the character to convert
     * @return a String containing the one specified character
     */
    public static String toString(final char ch) {
        if (ch < 128) {
            return CHAR_STRING_ARRAY[ch];
        }
        return String.valueOf(ch);
    }

    // -----------------------------------------------------------------------

    /**
     * Converts the character to a String that contains the one character.
     *
     * <p>For ASCII 7 bit characters, this uses a cache that will return the same String object each
     * time.
     *
     * <p>If {@code null} is passed in, {@code null} will be returned.
     *
     * <pre>
     *   toString(null) = null
     *   toString(' ')  = " "
     *   toString('A')  = "A"
     * </pre>
     *
     * @param ch the character to convert
     * @return a String containing the one specified character
     */
    public static String toString(final Character ch) {
        if (ch == null) {
            return null;
        }
        return toString(ch.charValue());
    }

    /**
     * Converts the string to the Unicode format '\u0020'.
     *
     * <p>This format is the Java source code format.
     *
     * <pre>
     *   unicodeEscaped(' ') = "\u0020"
     *   unicodeEscaped('A') = "\u0041"
     * </pre>
     *
     * @param ch the character to convert
     * @return the escaped Unicode string
     */
    public static String unicodeEscaped(final char ch) {
        if (ch < 0x10) {
            return "\\u000" + Integer.toHexString(ch);
        } else if (ch < 0x100) {
            return "\\u00" + Integer.toHexString(ch);
        } else if (ch < 0x1000) {
            return "\\u0" + Integer.toHexString(ch);
        }
        return "\\u" + Integer.toHexString(ch);
    }

    // --------------------------------------------------------------------------

    /**
     * Converts the string to the Unicode format '\u0020'.
     *
     * <p>This format is the Java source code format.
     *
     * <p>If {@code null} is passed in, {@code null} will be returned.
     *
     * <pre>
     *   unicodeEscaped(null) = null
     *   unicodeEscaped(' ')  = "\u0020"
     *   unicodeEscaped('A')  = "\u0041"
     * </pre>
     *
     * @param ch the character to convert, may be null
     * @return the escaped Unicode string, null if null input
     */
    public static String unicodeEscaped(final Character ch) {
        if (ch == null) {
            return null;
        }
        return unicodeEscaped(ch.charValue());
    }

    /**
     * Checks whether the character is ASCII 7 bit.
     *
     * <pre>
     *   isAscii('a')  = true
     *   isAscii('A')  = true
     *   isAscii('3')  = true
     *   isAscii('-')  = true
     *   isAscii('\n') = true
     *   isAscii('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if less than 128
     */
    public static boolean isAscii(final char ch) {
        return ch < 128;
    }

    // --------------------------------------------------------------------------

    /**
     * Checks whether the character is ASCII 7 bit printable.
     *
     * <pre>
     *   isAsciiPrintable('a')  = true
     *   isAsciiPrintable('A')  = true
     *   isAsciiPrintable('3')  = true
     *   isAsciiPrintable('-')  = true
     *   isAsciiPrintable('\n') = false
     *   isAsciiPrintable('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 32 and 126 inclusive
     */
    public static boolean isAsciiPrintable(final char ch) {
        return ch >= 32 && ch < 127;
    }

    /**
     * Checks whether the character is ASCII 7 bit control.
     *
     * <pre>
     *   isAsciiControl('a')  = false
     *   isAsciiControl('A')  = false
     *   isAsciiControl('3')  = false
     *   isAsciiControl('-')  = false
     *   isAsciiControl('\n') = true
     *   isAsciiControl('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if less than 32 or equals 127
     */
    public static boolean isAsciiControl(final char ch) {
        return ch < 32 || ch == 127;
    }

    /**
     * Checks whether the character is ASCII 7 bit alphabetic.
     *
     * <pre>
     *   isAsciiAlpha('a')  = true
     *   isAsciiAlpha('A')  = true
     *   isAsciiAlpha('3')  = false
     *   isAsciiAlpha('-')  = false
     *   isAsciiAlpha('\n') = false
     *   isAsciiAlpha('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 65 and 90 or 97 and 122 inclusive
     */
    public static boolean isAsciiAlpha(final char ch) {
        return isAsciiAlphaUpper(ch) || isAsciiAlphaLower(ch);
    }

    /**
     * Checks whether the character is ASCII 7 bit alphabetic upper case.
     *
     * <pre>
     *   isAsciiAlphaUpper('a')  = false
     *   isAsciiAlphaUpper('A')  = true
     *   isAsciiAlphaUpper('3')  = false
     *   isAsciiAlphaUpper('-')  = false
     *   isAsciiAlphaUpper('\n') = false
     *   isAsciiAlphaUpper('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 65 and 90 inclusive
     */
    public static boolean isAsciiAlphaUpper(final char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    /**
     * Checks whether the character is ASCII 7 bit alphabetic lower case.
     *
     * <pre>
     *   isAsciiAlphaLower('a')  = true
     *   isAsciiAlphaLower('A')  = false
     *   isAsciiAlphaLower('3')  = false
     *   isAsciiAlphaLower('-')  = false
     *   isAsciiAlphaLower('\n') = false
     *   isAsciiAlphaLower('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 97 and 122 inclusive
     */
    public static boolean isAsciiAlphaLower(final char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    /**
     * Checks whether the character is ASCII 7 bit numeric.
     *
     * <pre>
     *   isAsciiNumeric('a')  = false
     *   isAsciiNumeric('A')  = false
     *   isAsciiNumeric('3')  = true
     *   isAsciiNumeric('-')  = false
     *   isAsciiNumeric('\n') = false
     *   isAsciiNumeric('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 48 and 57 inclusive
     */
    public static boolean isAsciiNumeric(final char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Checks whether the character is ASCII 7 bit numeric.
     *
     * <pre>
     *   isAsciiAlphanumeric('a')  = true
     *   isAsciiAlphanumeric('A')  = true
     *   isAsciiAlphanumeric('3')  = true
     *   isAsciiAlphanumeric('-')  = false
     *   isAsciiAlphanumeric('\n') = false
     *   isAsciiAlphanumeric('&copy;') = false
     * </pre>
     *
     * @param ch the character to check
     * @return true if between 48 and 57 or 65 and 90 or 97 and 122 inclusive
     */
    public static boolean isAsciiAlphanumeric(final char ch) {
        return isAsciiAlpha(ch) || isAsciiNumeric(ch);
    }

    /**
     * 将对象转为字符串
     *
     * <pre>
     * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 	 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String string(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return string(obj, charset);
        } else if (obj instanceof Byte[]) {
            return string(obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return string(obj, charset);
        } else if (Whether.arrayObj(obj)) {
            return ToolArray.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 按固定长度处理字符串
     *
     * @param input  显示对象
     * @param length 固定长度
     * @param pad    填充字符
     * @param isLeft 是否靠左
     */
    public static String layout(
            final CharSequence input, final int length, final char pad, final boolean isLeft) {
        TextBuilder charSequenceAppendable = TextBuilder.of();
        layout(charSequenceAppendable, input, length, pad, isLeft);
        return charSequenceAppendable.toString();
    }

    /**
     * 按固定长度处理字符串
     *
     * @param container 字符串容器
     * @param input     显示对象
     * @param length    固定长度
     * @param pad       填充字符
     * @param isLeft    是否靠左
     */
    public static void layout(
            final TextBuilder container,
            final CharSequence input,
            final int length,
            final char pad,
            final boolean isLeft) {
        if (input == null || input.length() == 0) {
            return;
        }
        if (length <= input.length()) {
            container.append(input);
            return;
        }
        if (isLeft) {
            container.append(input);
            repeat(container, pad, length - input.length());
        } else {
            repeat(container, pad, length - input.length());
            container.append(input);
        }
    }

    /**
     * Returns padding using the specified delimiter repeated to a given length.
     *
     * @param container The String Container
     * @param input     character to repeat
     * @param repeat    number of times to repeat char, negative treated as zero
     */
    public static TextBuilder repeat(
            final TextBuilder container, final char input, final int repeat) {
        for (int i = 0; i < repeat; i++) {
            container.append(input);
        }
        return container;
    }

    /**
     * Repeat a String {@code repeat} times to form a new String.
     *
     * @param container The String Container
     * @param input     the String to repeat, may be null
     * @param repeat    number of times to repeat input, negative treated as zero
     * @return a new String consisting of the original String repeated, {@code null} if null String
     * input
     */
    public static TextBuilder repeat(
            final TextBuilder container, final CharSequence input, final int repeat) {
        if (input == null || input.length() == 0 || repeat <= 0) {
            return container;
        }
        if (repeat == 1) {
            container.append(input);
        } else if (input.length() == 1) {
            repeat(container, input.charAt(0), repeat);
        } else {
            for (int i = 0; i < repeat; i++) {
                container.append(input);
            }
        }
        return container;
    }

    /**
     * 给定字符串数组全部做去首尾空格
     *
     * @param strs 字符串数组
     */
    public static void trim(String[] strs) {
        if (null == strs) {
            return;
        }
        String str;
        for (int i = 0; i < strs.length; i++) {
            str = strs[i];
            if (null != str) {
                strs[i] = CharSequenceUtil.trim(str);
            }
        }
    }

    /**
     * 创建StringBuilder对象
     *
     * @return StringBuilder对象
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 创建StringBuilder对象
     *
     * @param capacity 初始大小
     * @return StringBuilder对象
     */
    public static StringBuilder builder(int capacity) {
        return new StringBuilder(capacity);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
     * 字符填充于字符串前
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @return 填充后的字符串
     */
    public static String fillBefore(String str, char filledChar, int len) {
        return fill(str, filledChar, len, true);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
     * 字符填充于字符串后
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @return 填充后的字符串
     */
    public static String fillAfter(String str, char filledChar, int len) {
        return fill(str, filledChar, len, false);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @param isPre      是否填充在前
     * @return 填充后的字符串
     */
    public static String fill(String str, char filledChar, int len, boolean isPre) {
        final int strLen = str.length();
        if (strLen > len) {
            return str;
        }

        String filledStr = ToolString.repeat(filledChar, len - strLen);
        return isPre ? filledStr.concat(str) : str.concat(filledStr);
    }

    /**
     * 计算两个字符串的相似度
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相似度
     */
    public static double similar(String str1, String str2) {
        return TextSimilarity.similar(str1, str2);
    }

    /**
     * 计算两个字符串的相似度百分比
     *
     * @param str1  字符串1
     * @param str2  字符串2
     * @param scale 相似度
     * @return 相似度百分比
     */
    public static String similar(String str1, String str2, int scale) {
        return TextSimilarity.similar(str1, str2, scale);
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template 文本模板，被替换的部分用 {key} 表示
     * @param map      参数值对
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map) {
        return format(template, map, true);
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template   文本模板，被替换的部分用 {key} 表示
     * @param map        参数值对
     * @param ignoreNull 是否忽略 {@code null} 值，忽略则 {@code null} 值对应的变量不被替换，否则替换为""
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
        return StrFormatter.format(template, map, ignoreNull);
    }

    private static class CharSequenceUtils {

        /**
         * {@code CharSequenceUtils} instances should NOT be constructed in standard programming.
         *
         * <p>This constructor is public to permit tools that require a JavaBean instance to operate.
         */
        public CharSequenceUtils() {
            super();
        }

        // -----------------------------------------------------------------------

        /**
         * Returns a new {@code CharSequence} that is a subsequence of this sequence starting with the
         * {@code char} value at the specified index.
         *
         * <p>This provides the {@code CharSequence} equivalent to {@link String#substring(int)}. The
         * length (in {@code char}) of the returned sequence is {@code length() - start}, so if {@code
         * start == end} then an empty sequence is returned.
         *
         * @param cs    the specified subsequence, null returns null
         * @param start the start index, inclusive, valid
         * @return a new subsequence, may be null
         * @throws IndexOutOfBoundsException if {@code start} is negative or if {@code start} is greater
         *                                   than {@code length()}
         */
        public static CharSequence subSequence(final CharSequence cs, final int start) {
            return cs == null ? null : cs.subSequence(start, cs.length());
        }

        // -----------------------------------------------------------------------

        /**
         * Finds the first index in the {@code CharSequence} that matches the specified character.
         *
         * @param cs         the {@code CharSequence} to be processed, not null
         * @param searchChar the char to be searched for
         * @param start      the start index, negative starts at the string start
         * @return the index where the search char was found, -1 if not found
         */
        static int indexOf(final CharSequence cs, final int searchChar, int start) {
            if (cs instanceof String) {
                return ((String) cs).indexOf(searchChar, start);
            } else {
                final int sz = cs.length();
                if (start < 0) {
                    start = 0;
                }
                for (int i = start; i < sz; i++) {
                    if (cs.charAt(i) == searchChar) {
                        return i;
                    }
                }
                return -1;
            }
        }

        /**
         * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
         *
         * @param cs         the {@code CharSequence} to be processed
         * @param searchChar the {@code CharSequence} to be searched for
         * @param start      the start index
         * @return the index where the search sequence was found
         */
        static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
            return cs.toString().indexOf(searchChar.toString(), start);
            //        if (cs instanceof String && searchChar instanceof String) {
            //            // TODO: Do we assume searchChar is usually relatively small;
            //            //       If so then calling toString() on it is better than reverting to
            //            //       the green implementation in the else block
            //            return ((String) cs).indexOf((String) searchChar, start);
            //        } else {
            //            // TODO: Implement rather than convert to String
            //            return cs.toString().indexOf(searchChar.toString(), start);
            //        }
        }

        /**
         * Finds the last index in the {@code CharSequence} that matches the specified character.
         *
         * @param cs         the {@code CharSequence} to be processed
         * @param searchChar the char to be searched for
         * @param start      the start index, negative returns -1, beyond length starts at end
         * @return the index where the search char was found, -1 if not found
         */
        static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
            if (cs instanceof String) {
                return ((String) cs).lastIndexOf(searchChar, start);
            } else {
                final int sz = cs.length();
                if (start < 0) {
                    return -1;
                }
                if (start >= sz) {
                    start = sz - 1;
                }
                for (int i = start; i >= 0; --i) {
                    if (cs.charAt(i) == searchChar) {
                        return i;
                    }
                }
                return -1;
            }
        }

        /**
         * Used by the lastIndexOf(CharSequence methods) as a green implementation of lastIndexOf
         *
         * @param cs         the {@code CharSequence} to be processed
         * @param searchChar the {@code CharSequence} to be searched for
         * @param start      the start index
         * @return the index where the search sequence was found
         */
        static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
            return cs.toString().lastIndexOf(searchChar.toString(), start);
            //        if (cs instanceof String && searchChar instanceof String) {
            //            // TODO: Do we assume searchChar is usually relatively small;
            //            //       If so then calling toString() on it is better than reverting to
            //            //       the green implementation in the else block
            //            return ((String) cs).lastIndexOf((String) searchChar, start);
            //        } else {
            //            // TODO: Implement rather than convert to String
            //            return cs.toString().lastIndexOf(searchChar.toString(), start);
            //        }
        }

        /**
         * Green implementation of toCharArray.
         *
         * @param cs the {@code CharSequence} to be processed
         * @return the resulting char array
         */
        static char[] toCharArray(final CharSequence cs) {
            if (cs instanceof String) {
                return ((String) cs).toCharArray();
            } else {
                final int sz = cs.length();
                final char[] array = new char[cs.length()];
                for (int i = 0; i < sz; i++) {
                    array[i] = cs.charAt(i);
                }
                return array;
            }
        }

        /**
         * Green implementation of regionMatches.
         *
         * @param cs         the {@code CharSequence} to be processed
         * @param ignoreCase whether or not to be case insensitive
         * @param thisStart  the index to start on the {@code cs} CharSequence
         * @param substring  the {@code CharSequence} to be looked for
         * @param start      the index to start on the {@code substring} CharSequence
         * @param length     character length of the region
         * @return whether the region matched
         */
        static boolean regionMatches(
                final CharSequence cs,
                final boolean ignoreCase,
                final int thisStart,
                final CharSequence substring,
                final int start,
                final int length) {
            if (cs instanceof String && substring instanceof String) {
                return ((String) cs)
                        .regionMatches(ignoreCase, thisStart, (String) substring, start, length);
            } else {
                int index1 = thisStart;
                int index2 = start;
                int tmpLen = length;

                while (tmpLen-- > 0) {
                    char c1 = cs.charAt(index1++);
                    char c2 = substring.charAt(index2++);

                    if (c1 == c2) {
                        continue;
                    }

                    if (!ignoreCase) {
                        return false;
                    }

                    // The same check as in String.regionMatches():
                    if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                            && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }
}
