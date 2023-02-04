package pxf.tl.setting;


import pxf.tl.help.Whether;
import pxf.tl.io.file.FileNameUtil;
import pxf.tl.io.resource.NoResourceException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Setting工具类<br>
 * 提供静态方法获取配置文件
 *
 * @author potatoxf
 */
public class SettingUtil {
    /**
     * 配置文件缓存
     */
    private static final Map<String, Setting> SETTING_MAP = new ConcurrentHashMap<>();

    /**
     * 获取当前环境下的配置文件<br>
     * name可以为不包括扩展名的文件名（默认.setting为结尾），也可以是文件名全称
     *
     * @param name 文件名，如果没有扩展名，默认为.setting
     * @return 当前环境下配置文件
     */
    public static Setting get(String name) {
        return SETTING_MAP.computeIfAbsent(
                name,
                (filePath) -> {
                    final String extName = FileNameUtil.extName(filePath);
                    if (Whether.empty(extName)) {
                        filePath = filePath + "." + Setting.EXT_NAME;
                    }
                    return new Setting(filePath, true);
                });
    }

    /**
     * 获取给定路径找到的第一个配置文件<br>
     * * name可以为不包括扩展名的文件名（默认.setting为结尾），也可以是文件名全称
     *
     * @param names 文件名，如果没有扩展名，默认为.setting
     * @return 当前环境下配置文件
     */
    public static Setting getFirstFound(String... names) {
        for (String name : names) {
            try {
                return get(name);
            } catch (NoResourceException e) {
                // ignore
            }
        }
        return null;
    }
}
