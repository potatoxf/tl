package pxf.tl.util;


import pxf.tl.api.*;
import pxf.tl.collection.map.WeakConcurrentMap;
import pxf.tl.convert.Convert;
import pxf.tl.date.DateUtil;
import pxf.tl.exception.UtilException;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;
import pxf.tl.utils.CreditCodeHelper;
import pxf.tl.utils.ForIdCard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则相关工具类<br>
 *
 * @author potatoxf
 */
public final class ToolRegex {
    /**
     * Pattern池
     */
    private static final WeakConcurrentMap<MutableObjectNumber<String>, Pattern> POOL = new WeakConcurrentMap<>();

    /**
     * 正则表达式匹配中文汉字
     */
    public static final String RE_CHINESE = PoolOfRegex.CHINESE;
    /**
     * 正则表达式匹配中文字符串
     */
    public static final String RE_CHINESES = PoolOfRegex.CHINESES;
    /**
     * 正则中需要被转义的关键字
     */
    public static final Set<Character> RE_KEYS =
            New.set(false, '$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|');
    /**
     * 正则表达式缓存
     */
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * 获得匹配的字符串，获得正则中分组0的内容
     *
     * @param regex   匹配的正则
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String getGroup0(String regex, CharSequence content) {
        return get(regex, content, 0);
    }

    /**
     * 获得匹配的字符串，获得正则中分组1的内容
     *
     * @param regex   匹配的正则
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String getGroup1(String regex, CharSequence content) {
        return get(regex, content, 1);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex      匹配的正则
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex     匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, String groupName) {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return get(pattern, content, groupName);
    }

    /**
     * 获得匹配的字符串，获得正则中分组0的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String getGroup0(Pattern pattern, CharSequence content) {
        return get(pattern, content, 0);
    }

    /**
     * 获得匹配的字符串，获得正则中分组1的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String getGroup1(Pattern pattern, CharSequence content) {
        return get(pattern, content, 1);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        final MutableObject<String> result = new MutableObject<>();
        get(pattern, content, matcher -> result.set(matcher.group(groupIndex)));
        return result.get();
    }

    /**
     * 获得匹配的字符串
     *
     * @param pattern   匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, String groupName) {
        if (null == content || null == pattern || null == groupName) {
            return null;
        }

        final MutableObject<String> result = new MutableObject<>();
        get(pattern, content, matcher -> result.set(matcher.group(groupName)));
        return result.get();
    }

    /**
     * 在给定字符串中查找给定规则的字符，如果找到则使用{@link Consumer}处理之<br>
     * 如果内容中有多个匹配项，则只处理找到的第一个结果。
     *
     * @param pattern  匹配的正则
     * @param content  被匹配的内容
     * @param consumer 匹配到的内容处理器
     */
    public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == content || null == pattern || null == consumer) {
            return;
        }
        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            consumer.accept(m);
        }
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content) {
        return getAllGroups(pattern, content, true);
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(
            Pattern pattern, CharSequence content, boolean withGroup0) {
        return getAllGroups(pattern, content, withGroup0, false);
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @param findAll    是否查找所有匹配到的内容，{@code false}表示只读取第一个匹配到的内容
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(
            Pattern pattern, CharSequence content, boolean withGroup0, boolean findAll) {
        if (null == content || null == pattern) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            final int startGroup = withGroup0 ? 0 : 1;
            final int groupCount = matcher.groupCount();
            for (int i = startGroup; i <= groupCount; i++) {
                result.add(matcher.group(i));
            }

            if (false == findAll) {
                break;
            }
        }
        return result;
    }

    /**
     * 根据给定正则查找字符串中的匹配项，返回所有匹配的分组名对应分组值<br>
     *
     * <pre>
     * pattern: (?&lt;year&gt;\\d+)-(?&lt;month&gt;\\d+)-(?&lt;day&gt;\\d+)
     * content: 2021-10-11
     * result : year: 2021, month: 10, day: 11
     * </pre>
     *
     * @param pattern 匹配的正则
     * @param content 被匹配的内容
     * @return 命名捕获组，key为分组名，value为对应值
     */
    public static Map<String, String> getAllGroupNames(Pattern pattern, CharSequence content) {
        if (null == content || null == pattern) {
            return null;
        }
        final Matcher m = pattern.matcher(content);
        final Map<String, String> result = New.map(m.groupCount());
        if (m.find()) {
            // 通过反射获取 namedGroups 方法
            final Map<String, Integer> map = ToolBytecode.invokeSilent(pattern, "namedGroups");
            map.forEach((key, value) -> result.put(key, m.group(value)));
        }
        return result;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param pattern  匹配正则
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 新字符串
     */
    public static String extractMulti(Pattern pattern, CharSequence content, String template) {
        if (null == content || null == pattern || null == template) {
            return null;
        }

        // 提取模板中的编号
        final TreeSet<Integer> varNums = new TreeSet<>((o1, o2) -> ToolObject.compare(o2, o1));
        final Matcher matcherForTemplate = PoolOfPattern.GROUP_VAR.matcher(template);
        while (matcherForTemplate.find()) {
            varNums.add(Integer.parseInt(matcherForTemplate.group(1)));
        }

        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            for (Integer group : varNums) {
                template = template.replace("$" + group, matcher.group(group));
            }
            return template;
        }
        return null;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 匹配结束后会删除匹配内容之前的内容（包括匹配内容）<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param regex    匹配正则字符串
     * @param content  被匹配的内容
     * @param template 生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 按照template拼接后的字符串
     */
    public static String extractMulti(String regex, CharSequence content, String template) {
        if (null == content || null == regex || null == template) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return extractMulti(pattern, content, template);
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 匹配结束后会删除匹配内容之前的内容（包括匹配内容）<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param pattern       匹配正则
     * @param contentHolder 被匹配的内容的Holder，value为内容正文，经过这个方法的原文将被去掉匹配之前的内容
     * @param template      生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 新字符串
     */
    public static String extractMultiAndDelPre(
            Pattern pattern, Mutable<CharSequence> contentHolder, String template) {
        if (null == contentHolder || null == pattern || null == template) {
            return null;
        }

        HashSet<String> varNums = findAll(PoolOfPattern.GROUP_VAR, template, 1, new HashSet<>());

        final CharSequence content = contentHolder.get();
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            for (String var : varNums) {
                int group = Integer.parseInt(var);
                template = template.replace("$" + var, matcher.group(group));
            }
            contentHolder.set(ToolString.sub(content, matcher.end(), content.length()));
            return template;
        }
        return null;
    }

    /**
     * 从content中匹配出多个值并根据template生成新的字符串<br>
     * 例如：<br>
     * content 2013年5月 pattern (.*?)年(.*?)月 template： $1-$2 return 2013-5
     *
     * @param regex         匹配正则字符串
     * @param contentHolder 被匹配的内容的Holder，value为内容正文，经过这个方法的原文将被去掉匹配之前的内容
     * @param template      生成内容模板，变量 $1 表示group1的内容，以此类推
     * @return 按照template拼接后的字符串
     */
    public static String extractMultiAndDelPre(
            String regex, Mutable<CharSequence> contentHolder, String template) {
        if (null == contentHolder || null == regex || null == template) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return extractMultiAndDelPre(pattern, contentHolder, template);
    }

    /**
     * 删除匹配的第一个内容
     *
     * @param regex   正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delFirst(String regex, CharSequence content) {
        if (Whether.hasBlank(regex, content)) {
            return ToolString.str(content);
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return delFirst(pattern, content);
    }

    /**
     * 删除匹配的第一个内容
     *
     * @param pattern 正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delFirst(Pattern pattern, CharSequence content) {
        return replaceFirst(pattern, content, ToolString.EMPTY);
    }

    /**
     * 替换匹配的第一个内容
     *
     * @param pattern     正则
     * @param content     被匹配的内容
     * @param replacement 替换的内容
     * @return 替换后剩余的内容
     */
    public static String replaceFirst(Pattern pattern, CharSequence content, String replacement) {
        if (null == pattern || Whether.empty(content)) {
            return ToolString.str(content);
        }

        return pattern.matcher(content).replaceFirst(replacement);
    }

    /**
     * 删除匹配的最后一个内容
     *
     * @param regex 正则
     * @param str   被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delLast(String regex, CharSequence str) {
        if (Whether.hasBlank(regex, str)) {
            return ToolString.str(str);
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return delLast(pattern, str);
    }

    /**
     * 删除匹配的最后一个内容
     *
     * @param pattern 正则
     * @param str     被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delLast(Pattern pattern, CharSequence str) {
        if (null != pattern && Whether.noEmpty(str)) {
            final MatchResult matchResult = lastIndexOf(pattern, str);
            if (null != matchResult) {
                return ToolString.subPre(str, matchResult.start())
                        + ToolString.subSuf(str, matchResult.end());
            }
        }

        return ToolString.str(str);
    }

    /**
     * 删除匹配的全部内容
     *
     * @param regex   正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delAll(String regex, CharSequence content) {
        if (Whether.hasBlank(regex, content)) {
            return ToolString.str(content);
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return delAll(pattern, content);
    }

    /**
     * 删除匹配的全部内容
     *
     * @param pattern 正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delAll(Pattern pattern, CharSequence content) {
        if (null == pattern || Whether.blank(content)) {
            return ToolString.str(content);
        }

        return pattern.matcher(content).replaceAll(ToolString.EMPTY);
    }

    /**
     * 删除正则匹配到的内容之前的字符 如果没有找到，则返回原文
     *
     * @param regex   定位正则
     * @param content 被查找的内容
     * @return 删除前缀后的新内容
     */
    public static String delPre(String regex, CharSequence content) {
        if (null == content || null == regex) {
            return ToolString.str(content);
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return delPre(pattern, content);
    }

    /**
     * 删除正则匹配到的内容之前的字符 如果没有找到，则返回原文
     *
     * @param pattern 定位正则模式
     * @param content 被查找的内容
     * @return 删除前缀后的新内容
     */
    public static String delPre(Pattern pattern, CharSequence content) {
        if (null == content || null == pattern) {
            return New.string(content);
        }

        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return ToolString.sub(content, matcher.end(), content.length());
        }
        return ToolString.str(content);
    }

    /**
     * 取得内容中匹配的所有结果，获得匹配的所有结果中正则对应分组0的内容
     *
     * @param regex   正则
     * @param content 被查找的内容
     * @return 结果列表
     */
    public static List<String> findAllGroup0(String regex, CharSequence content) {
        return findAll(regex, content, 0);
    }

    /**
     * 取得内容中匹配的所有结果，获得匹配的所有结果中正则对应分组1的内容
     *
     * @param regex   正则
     * @param content 被查找的内容
     * @return 结果列表
     */
    public static List<String> findAllGroup1(String regex, CharSequence content) {
        return findAll(regex, content, 1);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param regex   正则
     * @param content 被查找的内容
     * @param group   正则的分组
     * @return 结果列表
     */
    public static List<String> findAll(String regex, CharSequence content, int group) {
        return findAll(regex, content, group, new ArrayList<>());
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param regex      正则
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(
            String regex, CharSequence content, int group, T collection) {
        if (null == regex) {
            return collection;
        }

        return findAll(get(regex, Pattern.DOTALL), content, group, collection);
    }

    /**
     * 取得内容中匹配的所有结果，获得匹配的所有结果中正则对应分组0的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 结果列表
     */
    public static List<String> findAllGroup0(Pattern pattern, CharSequence content) {
        return findAll(pattern, content, 0);
    }

    /**
     * 取得内容中匹配的所有结果，获得匹配的所有结果中正则对应分组1的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 结果列表
     */
    public static List<String> findAllGroup1(Pattern pattern, CharSequence content) {
        return findAll(pattern, content, 1);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @param group   正则的分组
     * @return 结果列表
     */
    public static List<String> findAll(Pattern pattern, CharSequence content, int group) {
        return findAll(pattern, content, group, new ArrayList<>());
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param pattern    编译后的正则模式
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(
            Pattern pattern, CharSequence content, int group, T collection) {
        if (null == pattern || null == content) {
            return null;
        }
        Assert.notNull(collection, "Collection must be not null !");

        findAll(pattern, content, (matcher) -> collection.add(matcher.group(group)));
        return collection;
    }

    /**
     * 取得内容中匹配的所有结果，使用{@link Consumer}完成匹配结果处理
     *
     * @param pattern  编译后的正则模式
     * @param content  被查找的内容
     * @param consumer 匹配结果处理函数
     */
    public static void findAll(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == pattern || null == content) {
            return;
        }

        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            consumer.accept(matcher);
        }
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return 0;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return count(pattern, content);
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return 0;
        }

        int count = 0;
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     */
    public static boolean contains(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return false;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return contains(pattern, content);
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     */
    public static boolean contains(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return false;
        }
        return pattern.matcher(content).find();
    }

    /**
     * 找到指定正则匹配到字符串的开始位置
     *
     * @param regex   正则
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     */
    public static MatchResult indexOf(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return indexOf(pattern, content);
    }

    /**
     * 找到指定模式匹配到字符串的开始位置
     *
     * @param pattern 模式
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     */
    public static MatchResult indexOf(Pattern pattern, CharSequence content) {
        if (null != pattern && null != content) {
            final Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.toMatchResult();
            }
        }

        return null;
    }

    /**
     * 找到指定正则匹配到第一个字符串的位置
     *
     * @param regex   正则
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     */
    public static MatchResult lastIndexOf(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return null;
        }

        final Pattern pattern = get(regex, Pattern.DOTALL);
        return lastIndexOf(pattern, content);
    }

    /**
     * 找到指定模式匹配到最后一个字符串的位置
     *
     * @param pattern 模式
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     */
    public static MatchResult lastIndexOf(Pattern pattern, CharSequence content) {
        MatchResult result = null;
        if (null != pattern && null != content) {
            final Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                result = matcher.toMatchResult();
            }
        }

        return result;
    }

    /**
     * 从字符串中获得第一个整数
     *
     * @param StringWithNumber 带数字的字符串
     * @return 整数
     */
    public static Integer getFirstNumber(CharSequence StringWithNumber) {
        return Convert.toInt(get(PoolOfPattern.NUMBERS, StringWithNumber, 0), null);
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (content == null) {
            // 提供null的字符串为不匹配
            return false;
        }

        if (Whether.empty(regex)) {
            // 正则不存在则为全匹配
            return true;
        }

        // Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Pattern pattern = get(regex, Pattern.DOTALL);
        return isMatch(pattern, content);
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * <p>例如：原字符串是：中文1234，我想把1234换成(1234)，则可以：
     *
     * <pre>
     * ReUtil.replaceAll("中文1234", "(\\d+)", "($1)"))
     *
     * 结果：中文(1234)
     * </pre>
     *
     * @param content             文本
     * @param regex               正则
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return replaceAll(content, pattern, replacementTemplate);
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * @param content             文本
     * @param pattern             {@link Pattern}
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(
            CharSequence content, Pattern pattern, String replacementTemplate) {
        if (Whether.empty(content)) {
            return New.string(content);
        }

        final Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            final Set<String> varNums =
                    findAll(PoolOfPattern.GROUP_VAR, replacementTemplate, 1, new HashSet<>());
            final StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacementTemplate;
                for (String var : varNums) {
                    int group = Integer.parseInt(var);
                    replacement = replacement.replace("$" + var, matcher.group(group));
                }
                matcher.appendReplacement(sb, escape(replacement));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return ToolString.str(content);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换<br>
     * replaceFun可以通过{@link Matcher}提取出匹配到的内容的不同部分，然后经过重新处理、组装变成新的内容放回原位。
     *
     * <pre class="code">
     *     replaceAll(this.content, "(\\d+)", parameters -&gt; "-" + parameters.group(1) + "-")
     *     // 结果为："ZZZaaabbbccc中文-1234-"
     * </pre>
     *
     * @param str        要替换的字符串
     * @param regex      用于匹配的正则式
     * @param replaceFun 决定如何替换的函数
     * @return 替换后的文本
     */
    public static String replaceAll(
            CharSequence str, String regex, Function<Matcher, String> replaceFun) {
        return replaceAll(str, Pattern.compile(regex), replaceFun);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换<br>
     * replaceFun可以通过{@link Matcher}提取出匹配到的内容的不同部分，然后经过重新处理、组装变成新的内容放回原位。
     *
     * <pre class="code">
     *     replaceAll(this.content, "(\\d+)", parameters -&gt; "-" + parameters.group(1) + "-")
     *     // 结果为："ZZZaaabbbccc中文-1234-"
     * </pre>
     *
     * @param str        要替换的字符串
     * @param pattern    用于匹配的正则式
     * @param replaceFun 决定如何替换的函数,可能被多次调用（当有多个匹配时）
     * @return 替换后的字符串
     */
    public static String replaceAll(
            CharSequence str, Pattern pattern, Function<Matcher, String> replaceFun) {
        if (Whether.empty(str)) {
            return New.string(str);
        }

        final Matcher matcher = pattern.matcher(str);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(buffer, replaceFun.apply(matcher));
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 转义字符，将正则的关键字转义
     *
     * @param c 字符
     * @return 转义后的文本
     */
    public static String escape(char c) {
        final StringBuilder builder = new StringBuilder();
        if (RE_KEYS.contains(c)) {
            builder.append('\\');
        }
        builder.append(c);
        return builder.toString();
    }

    /**
     * 转义字符串，将正则的关键字转义
     *
     * @param content 文本
     * @return 转义后的文本
     */
    public static String escape(CharSequence content) {
        if (Whether.blank(content)) {
            return ToolString.str(content);
        }

        final StringBuilder builder = new StringBuilder();
        int len = content.length();
        char current;
        for (int i = 0; i < len; i++) {
            current = content.charAt(i);
            if (RE_KEYS.contains(current)) {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    /**
     * 获取年正则表达式
     *
     * @param year 年分割符号
     * @return Pattern
     */
    public static Pattern getYearPattern(String year) {
        String s = "([1-9]\\d{0,3})${year}";
        String r = Safe.replacePlaceholder(s, New.map(false, "year", year));
        return getOrCreatePattern(r);
    }

    /**
     * 获取月正则表达式
     *
     * @param month 月分割符号
     * @return Pattern
     */
    public static Pattern getMonthPattern(String month) {
        String s = "(1[012]|0?[1-9])${month}";
        String r = Safe.replacePlaceholder(s, New.map(false, "month", month));
        return getOrCreatePattern(r);
    }

    /**
     * 获取月日正则表达式
     *
     * @param month 月分割符号
     * @param day   日分割符号
     * @return Pattern
     */
    public static Pattern getMonthDayPattern(String month, String day) {
        String s =
                "((1[02]|0?[13578])${month}([12]\\d|3[01]|0[1-9])${day})|((11|0?[469])${month}([12]\\d|30|0[1-9])${day})|(0?2${month}(1\\d|2[0-9]|0[1-9])${day})";
        String r = Safe.replacePlaceholder(s, New.map(false, "month", month, "day", day));
        return getOrCreatePattern(r);
    }

    /**
     * 获取年月正则表达式
     *
     * @param year  年分割符号
     * @param month 月分割符号
     * @return Pattern
     */
    public static Pattern getYearMonthPattern(String year, String month) {
        String s = "[1-9]\\d{3}${year}(1[012]|0?[1-9])${month}";
        String r = Safe.replacePlaceholder(s, New.map(false, "year", year, "month", month));
        return getOrCreatePattern(r);
    }

    /**
     * 获取年月日正则表达式
     *
     * @param year  年分割符号
     * @param month 月分割符号
     * @param day   日分割符号
     * @return Pattern
     */
    public static Pattern getYearMonthDayPattern(String year, String month, String day) {

        String leapMonthDayTemplate =
                "(((1[02]|0[13578])${month}([12]\\d|3[01]|0[1-9])${day})|((11|0[469])${month}([12]\\d|30|0[1-9])${day})|(02${month}(1\\d|2[0-9]|0[1-9])${day}))";
        String monthDayTemplate =
                "(((1[02]|0[13578])${month}([12]\\d|3[01]|0[1-9])${day})|((11|0[469])${month}([12]\\d|30|0[1-9])${day})|(02${month}(1\\d|2[0-8]|0[1-9])${day}))";

        String lx = "[48]";
        String lxx = "[2468][048]|[13579][26]";
        String lxxx = "[1235679](0[48]|[2468][048]|[13579][26])|[48]([02468][048]|[13579][26])";
        String lxxxx =
                "([2468][048]|[13579][26])([02468][048]|[13579][26])|([2468][1235679]|[13579][01345789])(0[48]|[2468][048]|[13579][26])";
        String leapYearTemplate = "((" + lxxxx + ")|(" + lxxx + ")|(" + lxx + ")|" + lx + ")${year}";

        String nx = "[1235679]";
        String nxx = "[2468][1235679]|[13579][01345789]";
        String nxxx =
                "[1235679](0[01235679]|[2468][1235679]|[13579][01345789])|[48]([02468][1235679]|[13579][01345789])";
        String nxxxx =
                "([2468][048]|[13579][26])([02468][1235679]|[13579][01345789])|([2468][1235679]|[13579][01345789])([02468][01235679]|[13579][01345789])";
        String yearTemplate = "((" + nxxxx + ")|(" + nxxx + ")|(" + nxx + ")|" + nx + ")${year}";

        String stringBuilder =
                "("
                        + leapYearTemplate
                        + "|"
                        + yearTemplate
                        + ")("
                        + leapMonthDayTemplate
                        + "|"
                        + monthDayTemplate
                        + ")";

        String regexp =
                Safe.replacePlaceholder(
                        stringBuilder, New.map(false, "year", year, "month", month, "day", day));
        return getOrCreatePattern(regexp);
    }

    /**
     * @param input
     * @return
     */
    public static String escapeRegExp(String input) {
        return PoolOfPattern.ESCAPE_REGEX.matcher(input).replaceAll("\\\\$0");
    }

    /**
     * 要求匹配正则表达式
     *
     * @param pattern 正则表达式模式
     * @param string  输入字符串
     * @param isThrow 是否抛出异常
     * @return 如果匹配返回字符串，否则抛出异常
     */
    @Nullable
    public static String requireMatchPattern(
            @Nonnull final Pattern pattern, @Nullable final String string, final boolean isThrow) {
        if (isMatchPattern(pattern, string)) {
            return string;
        }
        if (isThrow) {
            throw new IllegalArgumentException(
                    "The [" + string + "] does not match the regular expression [" + pattern.pattern() + "]");
        }
        return null;
    }

    /**
     * 是否匹配正则表达式
     *
     * @param pattern 正则表达式模式
     * @param string  输入字符串
     * @return 如果匹配返回true，否则返回false
     */
    public static boolean isMatchPattern(
            @Nonnull final Pattern pattern, @Nullable final String string) {
        return string != null && pattern.matcher(string).matches();
    }

    /**
     * 获取或创建正则表达式
     *
     * @param regexp 正则表达式
     * @return Pattern
     */
    public static Pattern getOrCreatePattern(String regexp) {
        Pattern pattern = PATTERN_CACHE.get(regexp);
        if (pattern == null) {
            pattern = Pattern.compile(regexp);
            PATTERN_CACHE.put(regexp, pattern);
        }
        return pattern;
    }

    /**
     * 先从Pattern池中查找正则对应的{@link Pattern}，找不到则编译正则表达式并入池。
     *
     * @param regex 正则表达式
     * @return {@link Pattern}
     */
    public static Pattern get(String regex) {
        return get(regex, 0);
    }

    /**
     * 先从Pattern池中查找正则对应的{@link Pattern}，找不到则编译正则表达式并入池。
     *
     * @param regex 正则表达式
     * @param flags 正则标识位集合 {@link Pattern}
     * @return {@link Pattern}
     */
    public static Pattern get(String regex, int flags) {
        final MutableObjectNumber<String> regexWithFlag = new MutableObjectNumber<>(regex, flags);
        return POOL.computeIfAbsent(regexWithFlag, (key) -> Pattern.compile(regex, flags));
    }

    /**
     * 移除缓存
     *
     * @param regex 正则
     * @param flags 标识
     * @return 移除的{@link Pattern}，可能为{@code null}
     */
    public static Pattern remove(String regex, int flags) {
        return POOL.remove(new MutableObjectNumber<>(regex, flags));
    }

    /**
     * 清空缓存池
     */
    public static void clear() {
        POOL.clear();
    }

    /**
     * 通过正则表达式验证
     *
     * @param pattern 正则模式
     * @param value   值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(Pattern pattern, CharSequence value) {
        return isMatch(pattern, value);
    }

    /**
     * 通过正则表达式验证
     *
     * @param regex 正则
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(String regex, CharSequence value) {
        return isMatch(regex, value);
    }

    /**
     * 验证是否为英文字母 、数字和下划线
     *
     * @param value 值
     * @return 是否为英文字母 、数字和下划线
     */
    public static boolean isGeneral(CharSequence value) {
        return isMatchRegex(PoolOfPattern.GENERAL, value);
    }

    /**
     * 验证是否为给定长度范围的英文字母 、数字和下划线
     *
     * @param value 值
     * @param min   最小长度，负数自动识别为0
     * @param max   最大长度，0或负数表示不限制最大长度
     * @return 是否为给定长度范围的英文字母 、数字和下划线
     */
    public static boolean isGeneral(CharSequence value, int min, int max) {
        if (min < 0) {
            min = 0;
        }
        String reg = "^\\w{" + min + "," + max + "}$";
        if (max <= 0) {
            reg = "^\\w{" + min + ",}$";
        }
        return isMatchRegex(reg, value);
    }

    /**
     * 验证是否为给定最小长度的英文字母 、数字和下划线
     *
     * @param value 值
     * @param min   最小长度，负数自动识别为0
     * @return 是否为给定最小长度的英文字母 、数字和下划线
     */
    public static boolean isGeneral(CharSequence value, int min) {
        return isGeneral(value, min, 0);
    }

    /**
     * 判断字符串是否全部为字母组成，包括大写和小写字母和汉字
     *
     * @param value 值
     * @return 是否全部为字母组成，包括大写和小写字母和汉字
     */
    public static boolean isLetter(CharSequence value) {
        return ToolString.isAllCharMatch(value, Character::isLetter);
    }

    /**
     * 判断字符串是否全部为大写字母
     *
     * @param value 值
     * @return 是否全部为大写字母
     */
    public static boolean isUpperCase(CharSequence value) {
        return ToolString.isAllCharMatch(value, Character::isUpperCase);
    }

    /**
     * 判断字符串是否全部为小写字母
     *
     * @param value 值
     * @return 是否全部为小写字母
     */
    public static boolean isLowerCase(CharSequence value) {
        return ToolString.isAllCharMatch(value, Character::isLowerCase);
    }

    /**
     * 验证该字符串是否是数字
     *
     * @param value 字符串内容
     * @return 是否是数字
     */
    public static boolean isNumber(CharSequence value) {
        return ToolNumber.isNumber(value);
    }

    /**
     * 是否包含数字
     *
     * @param value 当前字符串
     * @return boolean 是否存在数字
     */
    public static boolean hasNumber(CharSequence value) {
        return contains(PoolOfPattern.NUMBERS, value);
    }

    /**
     * 验证该字符串是否是字母（包括大写和小写字母）
     *
     * @param value 字符串内容
     * @return 是否是字母（包括大写和小写字母）
     */
    public static boolean isWord(CharSequence value) {
        return isMatchRegex(PoolOfPattern.WORD, value);
    }

    /**
     * 验证是否为货币
     *
     * @param value 值
     * @return 是否为货币
     */
    public static boolean isMoney(CharSequence value) {
        return isMatchRegex(PoolOfPattern.MONEY, value);
    }

    /**
     * 验证是否为邮政编码（中国）
     *
     * @param value 值
     * @return 是否为邮政编码（中国）
     */
    public static boolean isZipCode(CharSequence value) {
        return isMatchRegex(PoolOfPattern.ZIP_CODE, value);
    }

    /**
     * 验证是否为可用邮箱地址
     *
     * @param value 值
     * @return true为可用邮箱地址
     */
    public static boolean isEmail(CharSequence value) {
        return isMatchRegex(PoolOfPattern.EMAIL, value);
    }

    /**
     * 验证是否为手机号码（中国）
     *
     * @param value 值
     * @return 是否为手机号码（中国）
     */
    public static boolean isMobile(CharSequence value) {
        return isMatchRegex(PoolOfPattern.MOBILE, value);
    }

    /**
     * 验证是否为身份证号码（支持18位、15位和港澳台的10位）
     *
     * @param value 身份证号，支持18位、15位和港澳台的10位
     * @return 是否为有效身份证号码
     */
    public static boolean isCitizenId(CharSequence value) {
        return ForIdCard.isValidCard(String.valueOf(value));
    }

    /**
     * 验证是否为生日
     *
     * @param year  年，从1900年开始计算
     * @param month 月，从1开始计数
     * @param day   日，从1开始计数
     * @return 是否为生日
     */
    public static boolean isBirthday(int year, int month, int day) {
        // 验证年
        int thisYear = DateUtil.thisYear();
        if (year < 1900 || year > thisYear) {
            return false;
        }

        // 验证月
        if (month < 1 || month > 12) {
            return false;
        }

        // 验证日
        if (day < 1 || day > 31) {
            return false;
        }
        // 检查几个特殊月的最大天数
        if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
            return false;
        }
        if (month == 2) {
            // 在2月，非闰年最大28，闰年最大29
            return day < 29 || (day == 29 && DateUtil.isLeapYear(year));
        }
        return true;
    }

    /**
     * 验证是否为生日<br>
     * 只支持以下几种格式：
     *
     * <ul>
     *   <li>yyyyMMdd
     *   <li>yyyy-MM-dd
     *   <li>yyyy/MM/dd
     *   <li>yyyy.MM.dd
     *   <li>yyyy年MM月dd日
     * </ul>
     *
     * @param value 值
     * @return 是否为生日
     */
    public static boolean isBirthday(CharSequence value) {
        final Matcher matcher = PoolOfPattern.BIRTHDAY.matcher(value);
        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(3));
            int day = Integer.parseInt(matcher.group(5));
            return isBirthday(year, month, day);
        }
        return false;
    }

    /**
     * 验证是否为IPV4地址
     *
     * @param value 值
     * @return 是否为IPV4地址
     */
    public static boolean isIpv4(CharSequence value) {
        return isMatchRegex(PoolOfPattern.IPV4, value);
    }

    /**
     * 验证是否为IPV6地址
     *
     * @param value 值
     * @return 是否为IPV6地址
     */
    public static boolean isIpv6(CharSequence value) {
        return isMatchRegex(PoolOfPattern.IPV6, value);
    }

    /**
     * 验证是否为MAC地址
     *
     * @param value 值
     * @return 是否为MAC地址
     */
    public static boolean isMac(CharSequence value) {
        return isMatchRegex(PoolOfPattern.MAC_ADDRESS, value);
    }

    /**
     * 验证是否为中国车牌号
     *
     * @param value 值
     * @return 是否为中国车牌号
     */
    public static boolean isPlateNumber(CharSequence value) {
        return isMatchRegex(PoolOfPattern.PLATE_NUMBER, value);
    }

    /**
     * 验证是否为URL
     *
     * @param value 值
     * @return 是否为URL
     */
    public static boolean isUrl(CharSequence value) {
        if (Whether.blank(value)) {
            return false;
        }
        try {
            new java.net.URL(ToolString.str(value));
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * 验证是否都为汉字
     *
     * @param value 值
     * @return 是否为汉字
     */
    public static boolean isChinese(CharSequence value) {
        return isMatchRegex(PoolOfPattern.CHINESES, value);
    }

    /**
     * 验证是否包含汉字
     *
     * @param value 值
     * @return 是否包含汉字
     */
    public static boolean hasChinese(CharSequence value) {
        return contains(RE_CHINESES, value);
    }

    /**
     * 验证是否为中文字、英文字母、数字和下划线
     *
     * @param value 值
     * @return 是否为中文字、英文字母、数字和下划线
     */
    public static boolean isGeneralWithChinese(CharSequence value) {
        return isMatchRegex(PoolOfPattern.GENERAL_WITH_CHINESE, value);
    }

    /**
     * 验证是否为UUID<br>
     * 包括带横线标准格式和不带横线的简单模式
     *
     * @param value 值
     * @return 是否为UUID
     */
    public static boolean isUUID(CharSequence value) {
        return isMatchRegex(PoolOfPattern.UUID, value) || isMatchRegex(PoolOfPattern.UUID_SIMPLE, value);
    }

    /**
     * 验证是否为Hex（16进制）字符串
     *
     * @param value 值
     * @return 是否为Hex（16进制）字符串
     */
    public static boolean isHex(CharSequence value) {
        return isMatchRegex(PoolOfPattern.HEX, value);
    }

    /**
     * 检查给定的数字是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 是否满足
     */
    public static boolean isBetween(Number value, Number min, Number max) {
        Assert.notNull(value);
        Assert.notNull(min);
        Assert.notNull(max);
        final double doubleValue = value.doubleValue();
        return (doubleValue >= min.doubleValue()) && (doubleValue <= max.doubleValue());
    }

    /**
     * 是否是有效的统一社会信用代码
     *
     * <pre>
     * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
     * 第二部分：机构类别代码1位 (数字或大写英文字母)
     * 第三部分：登记管理机关行政区划码6位 (数字)
     * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
     * 第五部分：校验码1位 (数字或大写英文字母)
     * </pre>
     *
     * @param creditCode 统一社会信用代码
     * @return 校验结果
     */
    public static boolean isCreditCode(CharSequence creditCode) {
        return CreditCodeHelper.isCreditCode(creditCode);
    }

    /**
     * 验证是否为车架号；别名：行驶证编号 车辆识别代号 车辆识别码
     *
     * @param value 值，17位车架号；形如：LSJA24U62JG269225、LDC613P23A1305189
     * @return 是否为车架号
     * @author potatoxf
     */
    public static boolean isCarVin(CharSequence value) {
        return isMatchRegex(PoolOfPattern.CAR_VIN, value);
    }

    /**
     * 验证是否为驾驶证 别名：驾驶证档案编号、行驶证编号 仅限：中国驾驶证档案编号
     *
     * @param value 值，12位数字字符串,eg:430101758218
     * @return 是否为档案编号
     * @author potatoxf
     */
    public static boolean isCarDrivingLicence(CharSequence value) {
        return isMatchRegex(PoolOfPattern.CAR_DRIVING_LICENCE, value);
    }

    /**
     * 是否是中文姓名 维吾尔族姓名里面的点是 · 输入法中文状态下，键盘左上角数字1前面的那个符号；<br>
     * 错误字符：{@code ．.。．.}<br>
     * 正确维吾尔族姓名：
     *
     * <pre>
     * 霍加阿卜杜拉·麦提喀斯木
     * 玛合萨提别克·哈斯木别克
     * 阿布都热依木江·艾斯卡尔
     * 阿卜杜尼亚孜·毛力尼亚孜
     * </pre>
     *
     * <pre>
     * ----------
     * 错误示例：孟  伟                reason: 有空格
     * 错误示例：连逍遥0               reason: 数字
     * 错误示例：依帕古丽-艾则孜        reason: 特殊符号
     * 错误示例：牙力空.买提萨力        reason: 新疆人的点不对
     * 错误示例：王建鹏2002-3-2        reason: 有数字、特殊符号
     * 错误示例：雷金默(雷皓添）        reason: 有括号
     * 错误示例：翟冬:亮               reason: 有特殊符号
     * 错误示例：李                   reason: 少于2位
     * ----------
     * </pre>
     * <p>
     * 总结中文姓名：2-60位，只能是中文和 ·
     *
     * @param value 中文姓名
     * @return 是否是正确的中文姓名
     * @author potatoxf
     */
    public static boolean isChineseName(CharSequence value) {
        return isMatchRegex(PoolOfPattern.CHINESE_NAME, value);
    }
}
