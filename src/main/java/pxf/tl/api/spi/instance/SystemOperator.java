package pxf.tl.api.spi.instance;

import pxf.tl.api.spi.SpiApi;

/**
 * 系统操作人员
 *
 * @author potatoxf
 */
public class SystemOperator implements SpiApi {
    /**
     * 获取当前操作人员的ID
     *
     * @return {@code String}
     */
    public String currentId() {
        return "";
    }

    /**
     * 获取当前操作人员的用户名
     *
     * @return {@code String}
     */
    public String currentUsername() {
        return "UNKNOWN";
    }

    /**
     * 获取当前操作人员的名称
     *
     * @return {@code String}
     */
    public String currentName() {
        return "UNKNOWN";
    }
}
