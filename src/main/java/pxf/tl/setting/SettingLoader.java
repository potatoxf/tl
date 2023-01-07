package pxf.tl.setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.api.Charsets;
import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.PoolOfCharacter;
import pxf.tl.help.Assert;
import pxf.tl.help.New;
import pxf.tl.help.Whether;
import pxf.tl.io.FileUtil;
import pxf.tl.io.resource.Resource;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolRegex;
import pxf.tl.util.ToolString;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Setting文件加载器
 *
 * @author potatoxf
 */
public class SettingLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingLoader.class);

    /**
     * 注释符号（当有此符号在行首，表示此行为注释）
     */
    private static final char COMMENT_FLAG_PRE = '#';
    /**
     * 本设置对象的字符集
     */
    private final Charsets charsets;
    /**
     * 是否使用变量
     */
    private final boolean isUseVariable;
    /**
     * GroupedMap
     */
    private final GroupedMap groupedMap;
    /**
     * 赋值分隔符（用于分隔键值对）
     */
    private char assignFlag = '=';
    /**
     * 变量名称的正则
     */
    private String varRegex = "\\$\\{(.*?)\\}";

    /**
     * 构造
     *
     * @param groupedMap GroupedMap
     */
    public SettingLoader(GroupedMap groupedMap) {
        this(groupedMap, Charsets.UTF_8, false);
    }

    /**
     * 构造
     *
     * @param groupedMap    GroupedMap
     * @param charsets      编码
     * @param isUseVariable 是否使用变量
     */
    public SettingLoader(GroupedMap groupedMap, Charsets charsets, boolean isUseVariable) {
        this.groupedMap = groupedMap;
        this.charsets = charsets;
        this.isUseVariable = isUseVariable;
    }

    /**
     * 加载设置文件
     *
     * @param resource 配置文件URL
     * @return 加载是否成功
     */
    public boolean load(Resource resource) {
        if (resource == null) {
            throw new NullPointerException("Null setting url define!");
        }
        ToolLog.debug(() -> "Load setting file [%s]", resource);
        InputStream settingStream = null;
        try {
            settingStream = resource.getStream();
            load(settingStream);
        } catch (Exception e) {
            ToolLog.error(e, () -> "Load setting error!");
            return false;
        } finally {
            ToolIO.closes(settingStream);
        }
        return true;
    }

    /**
     * 加载设置文件。 此方法不会关闭流对象
     *
     * @param settingStream 文件流
     * @return 加载成功与否
     * @throws IOException IO异常
     */
    public synchronized boolean load(InputStream settingStream) throws IOException {
        this.groupedMap.clear();
        BufferedReader reader = null;
        try {
            reader = New.bufferedReader(settingStream, this.charsets);
            // 分组
            String group = null;

            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                // 跳过注释行和空行
                if (Whether.blank(line) || ToolString.startWith(line, COMMENT_FLAG_PRE)) {
                    continue;
                }

                // 记录分组名
                if (ToolString.isSurround(line, PoolOfCharacter.BRACKET_START, PoolOfCharacter.BRACKET_END)) {
                    group = line.substring(1, line.length() - 1).trim();
                    continue;
                }

                final String[] keyValue = ToolString.splitToArray(line, this.assignFlag, 2);
                // 跳过不符合键值规范的行
                if (keyValue.length < 2) {
                    continue;
                }

                String value = keyValue[1].trim();
                // 替换值中的所有变量变量（变量必须是此行之前定义的变量，否则无法找到）
                if (this.isUseVariable) {
                    value = replaceVar(group, value);
                }
                this.groupedMap.put(group, keyValue[0].trim(), value);
            }
        } finally {
            ToolIO.closes(reader);
        }
        return true;
    }

    /**
     * 设置变量的正则<br>
     * 正则只能有一个group表示变量本身，剩余为字符 例如 \$\{(name)\}表示${name}变量名为name的一个变量表示
     *
     * @param regex 正则
     */
    public void setVarRegex(String regex) {
        this.varRegex = regex;
    }

    /**
     * 赋值分隔符（用于分隔键值对）
     *
     * @param assignFlag 正则
     */
    public void setAssignFlag(char assignFlag) {
        this.assignFlag = assignFlag;
    }

    /**
     * 持久化当前设置，会覆盖掉之前的设置<br>
     * 持久化会不会保留之前的分组
     *
     * @param absolutePath 设置文件的绝对路径
     */
    public void store(String absolutePath) {
        store(FileUtil.touch(absolutePath));
    }

    /**
     * 持久化当前设置，会覆盖掉之前的设置<br>
     * 持久化会不会保留之前的分组
     *
     * @param file 设置文件
     */
    public void store(File file) {
        Assert.notNull(file, "File to store must be not null !");
        LOGGER.debug("Store Setting to [{}]...", file.getAbsolutePath());
        PrintWriter writer = null;
        try {
            writer = FileUtil.getPrintWriter(file, charsets, false);
            store(writer);
        } finally {
            ToolIO.closes(writer);
        }
    }

    /**
     * 存储到Writer
     *
     * @param writer Writer
     */
    private synchronized void store(PrintWriter writer) {
        for (Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
            writer.println(
                    String.format(
                            "%s %s %s", PoolOfCharacter.BRACKET_START, groupEntry.getKey(), PoolOfCharacter.BRACKET_END));
            for (Entry<String, String> entry : groupEntry.getValue().entrySet()) {
                writer.println(
                        String.format("%s %s %s", entry.getKey(), this.assignFlag, entry.getValue()));
            }
        }
    }

    // ----------------------------------------------------------------------------------- Private
    // method start

    /**
     * 替换给定值中的变量标识
     *
     * @param group 所在分组
     * @param value 值
     * @return 替换后的字符串
     */
    private String replaceVar(String group, String value) {
        // 找到所有变量标识
        final Set<String> vars = ToolRegex.findAll(varRegex, value, 0, new HashSet<>());
        String key;
        for (String var : vars) {
            key = ToolRegex.get(varRegex, var, 1);
            if (Whether.noBlank(key)) {
                // 本分组中查找变量名对应的值
                String varValue = this.groupedMap.get(group, key);
                // 跨分组查找
                if (null == varValue) {
                    final List<String> groupAndKey = ToolString.split(key, PoolOfCharacter.DOT, 2);
                    if (groupAndKey.size() > 1) {
                        varValue = this.groupedMap.get(groupAndKey.get(0), groupAndKey.get(1));
                    }
                }
                // 系统参数和环境变量中查找
                if (null == varValue) {
                    varValue = JavaEnvironment.get(key);
                }

                if (null != varValue) {
                    // 替换标识
                    value = value.replace(var, varValue);
                }
            }
        }
        return value;
    }
    // ----------------------------------------------------------------------------------- Private
    // method end
}
