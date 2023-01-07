package pxf.tl.help;


import pxf.tl.api.spi.SpiManager;
import pxf.tl.api.spi.instance.SystemClock;
import pxf.tl.api.spi.instance.SystemOperator;
import pxf.tl.api.spi.instance.SystemProject;

/**
 * @author potatoxf
 */
public final class Env {
    /**
     * 系统时间
     */
    public static final SystemClock SYSTEM_CLOCK =
            SpiManager.getSafeServiceInstance(SystemClock.class);
    /**
     * 系统操作人员
     */
    public static final SystemOperator SYSTEM_OPERATOR =
            SpiManager.getSafeServiceInstance(SystemOperator.class);
    /**
     * 系统项目
     */
    public static final SystemProject SYSTEM_PROJECT =
            SpiManager.getSafeServiceInstance(SystemProject.class);
}
