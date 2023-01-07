package pxf.tl.unit;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 时间单位
 *
 * @author potatoxf
 */
public enum EnumUnitForTime implements UnitEnumInfoImpl<EnumUnitForTime> {
    /**
     * 纳秒
     */
    NS(TimeUnit.NANOSECONDS, ChronoUnit.NANOS, 1000),
    /**
     * 微妙
     */
    MMS(TimeUnit.MICROSECONDS, ChronoUnit.MICROS, 1000),
    /**
     * 毫秒
     */
    MS(TimeUnit.MILLISECONDS, ChronoUnit.MILLENNIA, 1000),
    /**
     * 秒
     */
    SEC(TimeUnit.SECONDS, ChronoUnit.SECONDS, 60),
    /**
     * 分钟
     */
    MIN(TimeUnit.MINUTES, ChronoUnit.MINUTES, 60),
    /**
     * 小时
     */
    HOUR(TimeUnit.HOURS, ChronoUnit.HOURS, 24),
    /**
     * 天
     */
    DAY(TimeUnit.DAYS, ChronoUnit.DAYS, -1);
    private final TimeUnit timeUnit;
    private final ChronoUnit chronoUnit;
    private final GeneralUnitInfo<EnumUnitForTime> unitInfo;

    EnumUnitForTime(final TimeUnit timeUnit, ChronoUnit chronoUnit, final int next) {
        this.timeUnit = timeUnit;
        this.chronoUnit = chronoUnit;
        unitInfo = new GeneralUnitInfo<>(EnumUnitForTime.class, ordinal(), next);
    }

    public static EnumUnitForTime from(final TimeUnit timeUnit) {
        Optional<EnumUnitForTime> found =
                Arrays.stream(EnumUnitForTime.values())
                        .filter(timeUnits -> timeUnits.timeUnit.equals(timeUnit))
                        .findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        throw new IllegalArgumentException("No TimeUnit equivalent for " + timeUnit);
    }

    public static EnumUnitForTime from(ChronoUnit chronoUnit) {
        Optional<EnumUnitForTime> found =
                Arrays.stream(EnumUnitForTime.values())
                        .filter(timeUnits -> timeUnits.chronoUnit.equals(chronoUnit))
                        .findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        throw new IllegalArgumentException("No TimeUnit equivalent for " + chronoUnit);
    }

    /**
     * 持有 {@code GeneralUnitInfo<E> }
     *
     * @return {@code GeneralUnitInfo<E>}
     */
    @Override
    public GeneralUnitInfo<EnumUnitForTime> holdUnitInfo() {
        return unitInfo;
    }

    public final TimeUnit toTimeUnit() {
        return timeUnit;
    }

    public final ChronoUnit toChronoUnit() {
        return chronoUnit;
    }
}
