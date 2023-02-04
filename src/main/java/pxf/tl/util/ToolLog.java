package pxf.tl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 日志工具类
 *
 * @author potatoxf
 */
public final class ToolLog {
    public static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger("Tools");
    public static final ThreadLocal<Logger> LOGGER_THREAD_LOCAL = ThreadLocal.withInitial(() -> DEFAULT_LOGGER);


    public static void trace(
            @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.trace(null, null, messageSupplier, args);
    }

    public static void debug(
            @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.debug(null, null, messageSupplier, args);
    }

    public static void info(
            @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.info(null, null, messageSupplier, args);
    }

    public static void warn(
            @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.warn(null, null, messageSupplier, args);
    }

    public static void error(
            @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.error(null, null, messageSupplier, args);
    }

    public static void trace(
            @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.trace(null, e, messageSupplier, args);
    }

    public static void debug(
            @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.debug(null, e, messageSupplier, args);
    }

    public static void info(
            @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.info(null, e, messageSupplier, args);
    }

    public static void warn(
            @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.warn(null, e, messageSupplier, args);
    }

    public static void error(
            @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.error(null, e, messageSupplier, args);
    }


    public static void trace(
            @Nullable Logger logger, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.trace(logger, null, messageSupplier, args);
    }

    public static void debug(
            @Nullable Logger logger, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.debug(logger, null, messageSupplier, args);
    }

    public static void info(
            @Nullable Logger logger, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.info(logger, null, messageSupplier, args);
    }

    public static void warn(
            @Nullable Logger logger, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.warn(logger, null, messageSupplier, args);
    }

    public static void error(
            @Nullable Logger logger, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        ToolLog.error(logger, null, messageSupplier, args);
    }

    public static void trace(
            @Nullable Logger logger, @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        record(1, logger, e, messageSupplier, args);
    }

    public static void debug(
            @Nullable Logger logger, @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        record(2, logger, e, messageSupplier, args);
    }

    public static void info(
            @Nullable Logger logger, @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        record(3, logger, e, messageSupplier, args);
    }

    public static void warn(
            @Nullable Logger logger, @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        record(4, logger, e, messageSupplier, args);
    }

    public static void error(
            @Nullable Logger logger, @Nullable Throwable e, @Nonnull Supplier<String> messageSupplier, @Nonnull Object... args) {
        record(5, logger, e, messageSupplier, args);
    }


    private static void record(int type,
                               @Nullable Logger logger,
                               @Nullable Throwable e,
                               @Nonnull Supplier<String> messageSupplier,
                               @Nonnull Object... args) {
        if (logger == null) {
            logger = LOGGER_THREAD_LOCAL.get();
        }
        if (logger.isErrorEnabled()) {
            String message = messageSupplier.get();
            if (args.length > 0) {
                message = String.format(message, args);
            }
            if (e != null) {
                if (type == 1) {
                    logger.trace(message, e);
                } else if (type == 2) {
                    logger.debug(message, e);
                } else if (type == 3) {
                    logger.info(message, e);
                } else if (type == 4) {
                    logger.warn(message, e);
                } else if (type == 5) {
                    logger.error(message, e);
                }
            } else {
                if (type == 1) {
                    logger.trace(message);
                } else if (type == 2) {
                    logger.debug(message);
                } else if (type == 3) {
                    logger.info(message);
                } else if (type == 4) {
                    logger.warn(message);
                } else if (type == 5) {
                    logger.error(message);
                }
            }
        }
    }
}
