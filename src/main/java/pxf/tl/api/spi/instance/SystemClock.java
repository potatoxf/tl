package pxf.tl.api.spi.instance;

import pxf.tl.api.spi.SpiApi;

import java.util.Date;

/**
 * 系统时钟
 *
 * @author potatoxf
 */
public class SystemClock implements SpiApi {

    /**
     * 当前时间
     *
     * @return 时间
     */
    public Date currentDateTime() {
        return new Date(currentTimeMillis());
    }

    /**
     * 当前时间毫秒
     *
     * @return 时间
     */
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
