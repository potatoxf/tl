package pxf.tl.util;

import pxf.tl.api.PoolOfPattern;
import pxf.tl.comparator.ToolCompare;
import pxf.tl.convert.NumberChineseFormatter;
import pxf.tl.date.*;
import pxf.tl.date.chinese.LunarInfo;
import pxf.tl.date.format.DateParser;
import pxf.tl.date.format.FastDateParser;
import pxf.tl.date.format.GlobalCustomFormat;
import pxf.tl.help.Safe;
import pxf.tl.help.Whether;

import javax.annotation.Nullable;
import java.text.ParsePosition;
import java.time.Month;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 时间助手类
 *
 * @author potatoxf
 */
public final class ToolTime {
    private static final int SECOND = 1000;
    private static final int MINUTE = SECOND * 60;
    private static final int HOUR = MINUTE * 60;
    private static final int DAY = HOUR * 24;
    private static final long YEAR = 365L * DAY;
    private static final long BIG_YEAR = 366L * DAY;

    private static final long FOUR_YEAR = YEAR * 4;
    private static final long BIG_FOUR_YEAR = YEAR * 3 + BIG_YEAR;

    private static final long COMMON_HUNDRED_YEAR = 76 * YEAR + 24 * BIG_YEAR;
    private static final long CYCLE_FOUR_HUNDRED_YEAR = YEAR * 303 + BIG_YEAR * 97;

    private static final int[] MONTH_DAYS = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private ToolTime() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * @param timeText
     * @return
     */
    public static String parseText(String timeText) {
        String time = String.valueOf(parse(timeText));
        if (time.startsWith("99")) {
            return time.substring(2);
        }
        return time;
    }

    /**
     * @param timeText
     * @return
     */
    public static int parse(String timeText) {
        if (!PoolOfPattern.TIME.matcher(timeText).matches()) {
            throw new IllegalArgumentException("The time [" + timeText + "] does not match");
        }
        char c = timeText.charAt(0), first = c;
        int i = 0, sign = 0, time = 9900;
        if (first == 'a' || first == 'A' || first == 'p' || first == 'P') {
            i = 3;
        }
        for (; i < timeText.length() && sign < 3; i++) {
            c = timeText.charAt(i);
            if (c == ':') {
                continue;
            }
            if (sign == 0) {
                time += Character.getNumericValue(c) * 10;
                c = timeText.charAt(++i);
                time += Character.getNumericValue(c);
                if (first == 'p' || first == 'P') {
                    if (time % 100 == 12) {
                        time = time / 100 * 100;
                    } else {
                        time += 12;
                    }
                }
                time *= 100;
                sign++;
            } else if (sign == 1 || sign == 2) {
                time += Character.getNumericValue(c) * 10;
                c = timeText.charAt(++i);
                time += Character.getNumericValue(c);
                if (sign == 1) {
                    time *= 100;
                }
                sign++;
            }
        }
        return time;
    }

    /**
     * 获得当前时间戳
     *
     * @param lastTimeMillis 上次时间戳
     * @return 当前时间戳
     * @throws RuntimeException 如果发生时间回退
     */
    public static long currentTimeMillis(long lastTimeMillis) {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimeMillis) {
            String msg =
                    "SystemClock moved backwards. Last timestamp is %d milliseconds,now is %d milliseconds";
            throw new RuntimeException(String.format(msg, lastTimeMillis, timestamp));
        }
        return timestamp;
    }

    /**
     * 阻塞到下一个毫秒，即直到获得新的时间戳
     *
     * @param lastTimeMillis 上次时间戳
     * @return 获取下一个时间戳
     * @throws RuntimeException 如果发生时间回退
     */
    public static long nextTimeMillis(long lastTimeMillis) {
        long timestamp = currentTimeMillis(lastTimeMillis);
        while (timestamp <= lastTimeMillis) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     *
     * <p>返回结果为{@link Duration}对象，通过调用toXXX方法返回相差单位
     *
     * @param startTimeInclude 开始时间（包含）
     * @param endTimeExclude   结束时间（不包含）
     * @return 时间差 {@link Duration}对象
     */
    public static Duration between(Temporal startTimeInclude, Temporal endTimeExclude) {
        return Duration.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     *
     * <p>返回结果为时间差的long值
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @param unit             时间差单位
     * @return 时间差
     */
    public static long between(Temporal startTimeInclude, Temporal endTimeExclude, ChronoUnit unit) {
        return unit.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 将 {@link TimeUnit} 转换为 {@link ChronoUnit}.
     *
     * @param unit 被转换的{@link TimeUnit}单位，如果为{@code null}返回{@code null}
     * @return {@link ChronoUnit}
     */
    public static ChronoUnit toChronoUnit(TimeUnit unit) throws IllegalArgumentException {
        if (null == unit) {
            return null;
        }
        return switch (unit) {
            case NANOSECONDS -> ChronoUnit.NANOS;
            case MICROSECONDS -> ChronoUnit.MICROS;
            case MILLISECONDS -> ChronoUnit.MILLIS;
            case SECONDS -> ChronoUnit.SECONDS;
            case MINUTES -> ChronoUnit.MINUTES;
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
            default -> throw new IllegalArgumentException("Unknown TimeUnit constant");
        };
    }

    /**
     * 转换 {@link ChronoUnit} 到 {@link TimeUnit}.
     *
     * @param unit {@link ChronoUnit}，如果为{@code null}返回{@code null}
     * @return {@link TimeUnit}
     * @throws IllegalArgumentException 如果{@link TimeUnit}没有对应单位抛出
     */
    public static TimeUnit toTimeUnit(ChronoUnit unit) throws IllegalArgumentException {
        if (null == unit) {
            return null;
        }
        return switch (unit) {
            case NANOS -> TimeUnit.NANOSECONDS;
            case MICROS -> TimeUnit.MICROSECONDS;
            case MILLIS -> TimeUnit.MILLISECONDS;
            case SECONDS -> TimeUnit.SECONDS;
            case MINUTES -> TimeUnit.MINUTES;
            case HOURS -> TimeUnit.HOURS;
            case DAYS -> TimeUnit.DAYS;
            default -> throw new IllegalArgumentException("ChronoUnit cannot be converted to TimeUnit: " + unit);
        };
    }

    /**
     * 日期偏移,根据field不同加不同值（偏移会修改传入的对象）
     *
     * @param <T>    日期类型，如LocalDate或LocalDateTime
     * @param time   {@link Temporal}
     * @param number 偏移量，正数为向后偏移，负数为向前偏移
     * @param field  偏移单位，见{@link ChronoUnit}，不能为null
     * @return 偏移后的日期时间
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T offset(T time, long number, TemporalUnit field) {
        if (null == time) {
            return null;
        }

        return (T) time.plus(number, field);
    }

    /**
     * 偏移到指定的周几
     *
     * @param temporal   日期或者日期时间
     * @param dayOfWeek  周几
     * @param <T>        日期类型，如LocalDate或LocalDateTime
     * @param isPrevious 是否向前偏移，{@code true}向前偏移，{@code false}向后偏移。
     * @return 偏移后的日期
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T offset(T temporal, DayOfWeek dayOfWeek, boolean isPrevious) {
        return (T)
                temporal.with(
                        isPrevious ? TemporalAdjusters.previous(dayOfWeek) : TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * 安全获取时间的某个属性，属性不存在返回最小值，一般为0<br>
     * 注意请谨慎使用此方法，某些{@link TemporalAccessor#isSupported(TemporalField)}为{@code false}的方法返回最小值
     *
     * @param temporalAccessor 需要获取的时间对象
     * @param field            需要获取的属性
     * @return 时间的值，如果无法获取则获取最小值，一般为0
     */
    public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
        if (temporalAccessor.isSupported(field)) {
            return temporalAccessor.get(field);
        }

        return (int) field.range().getMinimum();
    }

    /**
     * 格式化日期时间为指定格式<br>
     * 如果为{@link Month}，调用{@link Month#toString()}
     *
     * @param time      {@link TemporalAccessor}
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor time, DateTimeFormatter formatter) {
        if (null == time) {
            return null;
        }

        if (time instanceof Month) {
            return time.toString();
        }

        if (null == formatter) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        try {
            return formatter.format(time);
        } catch (UnsupportedTemporalTypeException e) {
            if (time instanceof LocalDate && e.getMessage().contains("HourOfDay")) {
                // 用户传入LocalDate，但是要求格式化带有时间部分，转换为LocalDateTime重试
                return formatter.format(((LocalDate) time).atStartOfDay());
            } else if (time instanceof LocalTime && e.getMessage().contains("YearOfEra")) {
                // 用户传入LocalTime，但是要求格式化带有日期部分，转换为LocalDateTime重试
                return formatter.format(((LocalTime) time).atDate(LocalDate.now()));
            } else if (time instanceof Instant) {
                // 时间戳没有时区信息，赋予默认时区
                return formatter.format(((Instant) time).atZone(ZoneId.systemDefault()));
            }
            throw e;
        }
    }

    /**
     * 格式化日期时间为指定格式<br>
     * 如果为{@link Month}，调用{@link Month#toString()}
     *
     * @param time   {@link TemporalAccessor}
     * @param format 日期格式
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor time, String format) {
        if (null == time) {
            return null;
        }

        if (time instanceof Month) {
            return time.toString();
        }

        // 检查自定义格式
        if (GlobalCustomFormat.isCustomFormat(format)) {
            return GlobalCustomFormat.format(time, format);
        }

        final DateTimeFormatter formatter =
                Whether.blank(format) ? null : DateTimeFormatter.ofPattern(format);

        return format(time, formatter);
    }

    /**
     * {@link TemporalAccessor}转换为 时间戳（从1970-01-01T00:00:00Z开始的毫秒数）<br>
     * 如果为{@link Month}，调用{@link Month#getValue()}
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     */
    public static long toEpochMilli(TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof Month) {
            return ((Month) temporalAccessor).getValue();
        }
        return toInstant(temporalAccessor).toEpochMilli();
    }

    /**
     * {@link TemporalAccessor}转换为 {@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result =
                    ((LocalTime) temporalAccessor)
                            .atDate(LocalDate.now())
                            .atZone(ZoneId.systemDefault())
                            .toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        } else {
            // issue#1891@Github
            // Instant.from不能完成日期转换
            // result = Instant.from(temporalAccessor);
            result = toInstant(LocalDateTimeUtil.of(temporalAccessor));
        }

        return result;
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param date      被检查的日期
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 是否在范围内
     */
    public static boolean isIn(
            TemporalAccessor date, TemporalAccessor beginDate, TemporalAccessor endDate) {
        final long thisMills = toEpochMilli(date);
        final long beginMills = toEpochMilli(beginDate);
        final long endMills = toEpochMilli(endDate);

        return thisMills >= Math.min(beginMills, endMills)
                && thisMills <= Math.max(beginMills, endMills);
    }

    /**
     * {@link ZoneId}转换为{@link TimeZone}，{@code null}则返回系统默认值
     *
     * @param zoneId {@link ZoneId}，{@code null}则返回系统默认值
     * @return {@link TimeZone}
     */
    public static TimeZone toTimeZone(ZoneId zoneId) {
        if (null == zoneId) {
            return TimeZone.getDefault();
        }

        return TimeZone.getTimeZone(zoneId);
    }

    /**
     * {@link TimeZone}转换为{@link ZoneId}，{@code null}则返回系统默认值
     *
     * @param timeZone {@link TimeZone}，{@code null}则返回系统默认值
     * @return {@link ZoneId}
     */
    public static ZoneId toZoneId(TimeZone timeZone) {
        if (null == timeZone) {
            return ZoneId.systemDefault();
        }

        return timeZone.toZoneId();
    }

    //------------------------------------------------------------------------------------------------------------------
    //Calendar
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 是否为上午
     *
     * @param calendar {@link Calendar}
     * @return 是否为上午
     */
    public static boolean isAM(Calendar calendar) {
        return Calendar.AM == calendar.get(Calendar.AM_PM);
    }

    /**
     * 是否为下午
     *
     * @param calendar {@link Calendar}
     * @return 是否为下午
     */
    public static boolean isPM(Calendar calendar) {
        return Calendar.PM == calendar.get(Calendar.AM_PM);
    }

    /**
     * 比较两个日期是否为同一天
     *
     * @param oneCalendar    日期1
     * @param antherCalendar 日期2
     * @return 是否为同一天
     */
    public static boolean isSameDay(Calendar oneCalendar, Calendar antherCalendar) {
        if (oneCalendar == null || antherCalendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return oneCalendar.get(Calendar.DAY_OF_YEAR) == antherCalendar.get(Calendar.DAY_OF_YEAR)
                && //
                oneCalendar.get(Calendar.YEAR) == antherCalendar.get(Calendar.YEAR)
                && //
                oneCalendar.get(Calendar.ERA) == antherCalendar.get(Calendar.ERA);
    }

    /**
     * 比较两个日期是否为同一周
     *
     * @param oneCalendar    日期1
     * @param antherCalendar 日期2
     * @param isMonday       是否为周一。国内第一天为星期一，国外第一天为星期日
     * @return 是否为同一周
     */
    public static boolean isSameWeek(Calendar oneCalendar, Calendar antherCalendar, boolean isMonday) {
        if (oneCalendar == null || antherCalendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }

        // 防止比较前修改原始Calendar对象
        oneCalendar = (Calendar) oneCalendar.clone();
        antherCalendar = (Calendar) antherCalendar.clone();

        // 把所传日期设置为其当前周的第一天
        // 比较设置后的两个日期是否是同一天：true 代表同一周
        if (isMonday) {
            oneCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            oneCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            antherCalendar.setFirstDayOfWeek(Calendar.MONDAY);
            antherCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else {
            oneCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            oneCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            antherCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
            antherCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }
        return isSameDay(oneCalendar, antherCalendar);
    }

    /**
     * 比较两个日期是否为同一月
     *
     * @param oneCalendar    日期1
     * @param antherCalendar 日期2
     * @return 是否为同一月
     */
    public static boolean isSameMonth(Calendar oneCalendar, Calendar antherCalendar) {
        if (oneCalendar == null || antherCalendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return oneCalendar.get(Calendar.YEAR) == antherCalendar.get(Calendar.YEAR)
                && //
                oneCalendar.get(Calendar.MONTH) == antherCalendar.get(Calendar.MONTH);
    }

    /**
     * 检查两个Calendar时间戳是否相同。
     *
     * <p>此方法检查两个Calendar的毫秒数时间戳是否相同。
     *
     * @param oneCalendar    时间1
     * @param antherCalendar 时间2
     * @return 两个Calendar时间戳是否相同。如果两个时间都为{@code null}返回true，否则有{@code null}返回false
     */
    public static boolean isSameInstant(Calendar oneCalendar, Calendar antherCalendar) {
        if (null == oneCalendar) {
            return null == antherCalendar;
        }
        if (null == antherCalendar) {
            return false;
        }

        return oneCalendar.getTimeInMillis() == antherCalendar.getTimeInMillis();
    }

    /**
     * 创建Calendar对象，时间为默认时区的当前时间
     *
     * @return Calendar对象
     */
    public static Calendar calendar() {
        return Calendar.getInstance();
    }

    /**
     * 转换为Calendar对象
     *
     * @param date 日期对象
     * @return Calendar对象
     */
    public static Calendar calendar(Date date) {
        if (date instanceof DateTime) {
            return ((DateTime) date).toCalendar();
        } else {
            return calendar(date.getTime());
        }
    }

    /**
     * 转换为Calendar对象，使用当前默认时区
     *
     * @param millis 时间戳
     * @return Calendar对象
     */
    public static Calendar calendar(long millis) {
        return calendar(millis, TimeZone.getDefault());
    }

    /**
     * 转换为Calendar对象
     *
     * @param millis   时间戳
     * @param timeZone 时区
     * @return Calendar对象
     */
    public static Calendar calendar(long millis, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(millis);
        return cal;
    }

    /**
     * 修改日期为某个时间字段起始时间
     *
     * @param calendar  {@link Calendar}
     * @param dateField 保留到的时间字段，如定义为 {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部归0
     * @return 原{@link Calendar}
     */
    public static Calendar truncate(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.TRUNCATE);
    }

    /**
     * 修改日期为某个时间字段四舍五入时间
     *
     * @param calendar  {@link Calendar}
     * @param dateField 时间字段
     * @return 原{@link Calendar}
     */
    public static Calendar round(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.ROUND);
    }

    /**
     * 修改日期为某个时间字段结束时间
     *
     * @param calendar  {@link Calendar}
     * @param dateField 保留到的时间字段，如定义为 {@link DateField#SECOND}，表示这个字段不变，这个字段以下字段全部取最大值
     * @return 原{@link Calendar}
     */
    public static Calendar ceiling(Calendar calendar, DateField dateField) {
        return DateModifier.modify(calendar, dateField.getValue(), DateModifier.ModifyType.CEILING);
    }

    /**
     * 修改日期为某个时间字段结束时间<br>
     * 可选是否归零毫秒。
     *
     * <p>有时候由于毫秒部分必须为0（如MySQL数据库中），因此在此加上选项。
     *
     * @param calendar            {@link Calendar}
     * @param dateField           时间字段
     * @param truncateMillisecond 是否毫秒归零
     * @return 原{@link Calendar}
     */
    public static Calendar ceiling(
            Calendar calendar, DateField dateField, boolean truncateMillisecond) {
        return DateModifier.modify(
                calendar, dateField.getValue(), DateModifier.ModifyType.CEILING, truncateMillisecond);
    }

    /**
     * 修改秒级别的开始时间，即忽略毫秒部分
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfSecond(Calendar calendar) {
        return truncate(calendar, DateField.SECOND);
    }

    /**
     * 修改秒级别的结束时间，即毫秒设置为999
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfSecond(Calendar calendar) {
        return ceiling(calendar, DateField.SECOND);
    }

    /**
     * 修改某小时的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfHour(Calendar calendar) {
        return truncate(calendar, DateField.HOUR_OF_DAY);
    }

    /**
     * 修改某小时的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfHour(Calendar calendar) {
        return ceiling(calendar, DateField.HOUR_OF_DAY);
    }

    /**
     * 修改某分钟的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfMinute(Calendar calendar) {
        return truncate(calendar, DateField.MINUTE);
    }

    /**
     * 修改某分钟的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfMinute(Calendar calendar) {
        return ceiling(calendar, DateField.MINUTE);
    }

    /**
     * 修改某天的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfDay(Calendar calendar) {
        return truncate(calendar, DateField.DAY_OF_MONTH);
    }

    /**
     * 修改某天的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfDay(Calendar calendar) {
        return ceiling(calendar, DateField.DAY_OF_MONTH);
    }

    /**
     * 修改给定日期当前周的开始时间，周一定为一周的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfWeek(Calendar calendar) {
        return beginOfWeek(calendar, true);
    }

    /**
     * 修改给定日期当前周的开始时间
     *
     * @param calendar           日期 {@link Calendar}
     * @param isMondayAsFirstDay 是否周一做为一周的第一天（false表示周日做为第一天）
     * @return {@link Calendar}
     */
    public static Calendar beginOfWeek(Calendar calendar, boolean isMondayAsFirstDay) {
        calendar.setFirstDayOfWeek(isMondayAsFirstDay ? Calendar.MONDAY : Calendar.SUNDAY);
        // WEEK_OF_MONTH为上限的字段（不包括），实际调整的为DAY_OF_MONTH
        return truncate(calendar, DateField.WEEK_OF_MONTH);
    }

    /**
     * 修改某周的结束时间，周日定为一周的结束
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfWeek(Calendar calendar) {
        return endOfWeek(calendar, true);
    }

    /**
     * 修改某周的结束时间
     *
     * @param calendar          日期 {@link Calendar}
     * @param isSundayAsLastDay 是否周日做为一周的最后一天（false表示周六做为最后一天）
     * @return {@link Calendar}
     */
    public static Calendar endOfWeek(Calendar calendar, boolean isSundayAsLastDay) {
        calendar.setFirstDayOfWeek(isSundayAsLastDay ? Calendar.MONDAY : Calendar.SUNDAY);
        // WEEK_OF_MONTH为上限的字段（不包括），实际调整的为DAY_OF_MONTH
        return ceiling(calendar, DateField.WEEK_OF_MONTH);
    }

    /**
     * 修改某月的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfMonth(Calendar calendar) {
        return truncate(calendar, DateField.MONTH);
    }

    /**
     * 修改某月的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfMonth(Calendar calendar) {
        return ceiling(calendar, DateField.MONTH);
    }

    /**
     * 修改某季度的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfQuarter(Calendar calendar) {
        //noinspection MagicConstant
        calendar.set(Calendar.MONTH, calendar.get(DateField.MONTH.getValue()) / 3 * 3);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return beginOfDay(calendar);
    }

    /**
     * 获取某季度的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    @SuppressWarnings({"MagicConstant", "ConstantConditions"})
    public static Calendar endOfQuarter(Calendar calendar) {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(DateField.MONTH.getValue()) / 3 * 3 + 2;

        final Calendar resultCal = Calendar.getInstance(calendar.getTimeZone());
        resultCal.set(year, month, pxf.tl.date.Month.of(month).getLastDay(DateUtil.isLeapYear(year)));

        return endOfDay(resultCal);
    }

    /**
     * 修改某年的开始时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar beginOfYear(Calendar calendar) {
        return truncate(calendar, DateField.YEAR);
    }

    /**
     * 修改某年的结束时间
     *
     * @param calendar 日期 {@link Calendar}
     * @return {@link Calendar}
     */
    public static Calendar endOfYear(Calendar calendar) {
        return ceiling(calendar, DateField.YEAR);
    }

    /**
     * 获得指定日期区间内的年份和季度<br>
     *
     * @param startDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     * @return 季度列表 ，元素类似于 20132
     */
    public static LinkedHashSet<int[]> yearAndQuarter(long startDate, long endDate) {
        LinkedHashSet<int[]> quarters = new LinkedHashSet<>();
        final Calendar cal = calendar(startDate);
        while (startDate <= endDate) {
            // 如果开始时间超出结束时间，让结束时间为开始时间，处理完后结束循环
            quarters.add(yearAndQuarter(cal));

            cal.add(Calendar.MONTH, 3);
            startDate = cal.getTimeInMillis();
        }

        return quarters;
    }

    /**
     * 获得指定日期年份和季度<br>
     * 格式：[20131]表示2013年第一季度
     *
     * @param cal 日期
     * @return 年和季度，格式类似于20131
     */
    public static int[] yearAndQuarter(Calendar cal) {
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) / 3 + 1};
    }

    /**
     * 获取指定日期字段的最小值，例如分钟的最小值是0
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最小值
     * @see Calendar#getActualMinimum(int)
     */
    public static int getBeginValue(Calendar calendar, DateField dateField) {
        return getBeginValue(calendar, dateField.getValue());
    }

    /**
     * 获取指定日期字段的最小值，例如分钟的最小值是0
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最小值
     * @see Calendar#getActualMinimum(int)
     */
    public static int getBeginValue(Calendar calendar, int dateField) {
        if (Calendar.DAY_OF_WEEK == dateField) {
            return calendar.getFirstDayOfWeek();
        }
        return calendar.getActualMinimum(dateField);
    }

    /**
     * 获取指定日期字段的最大值，例如分钟的最大值是59
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最大值
     * @see Calendar#getActualMaximum(int)
     */
    public static int getEndValue(Calendar calendar, DateField dateField) {
        return getEndValue(calendar, dateField.getValue());
    }

    /**
     * 获取指定日期字段的最大值，例如分钟的最大值是59
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最大值
     * @see Calendar#getActualMaximum(int)
     */
    public static int getEndValue(Calendar calendar, int dateField) {
        if (Calendar.DAY_OF_WEEK == dateField) {
            return (calendar.getFirstDayOfWeek() + 6) % 7;
        }
        return calendar.getActualMaximum(dateField);
    }

    /**
     * {@code null}安全的{@link Calendar}比较，{@code null}小于任何日期
     *
     * @param oneCalendar    日期1
     * @param antherCalendar 日期2
     * @return 比较结果，如果calendar1 &lt; antherCalendar，返回数小于0，oneCalendar==calendar2返回0，oneCalendar &gt; antherCalendar
     * 大于0
     */
    public static int compare(Calendar oneCalendar, Calendar antherCalendar) {
        return ToolCompare.compare(oneCalendar, antherCalendar);
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int computeAge(Calendar birthday, Calendar dateToCompare) {
        return computeAge(birthday.getTimeInMillis(), dateToCompare.getTimeInMillis());
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int computeAge(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        final int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {

            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth =
                    dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            if ((!isLastDayOfMonth || !isLastDayOfMonthBirth)
                    && dayOfMonth < dayOfMonthBirth) {
                // 如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }

    /**
     * 将指定Calendar时间格式化为纯中文形式，比如：
     *
     * <pre>
     *     2018-02-24 12:13:14 转换为 二〇一八年二月二十四日（withTime为false）
     *     2018-02-24 12:13:14 转换为 二〇一八年二月二十四日十二时十三分十四秒（withTime为true）
     * </pre>
     *
     * @param calendar {@link Calendar}
     * @param withTime 是否包含时间部分
     * @return 格式化后的字符串
     */
    public static String formatChineseDate(Calendar calendar, boolean withTime) {
        final StringBuilder result = ToolString.builder();

        // 年
        final String year = String.valueOf(calendar.get(Calendar.YEAR));
        final int length = year.length();
        for (int i = 0; i < length; i++) {
            result.append(NumberChineseFormatter.numberCharToChinese(year.charAt(i), false));
        }
        result.append('年');

        // 月
        int month = calendar.get(Calendar.MONTH) + 1;
        result.append(NumberChineseFormatter.formatThousand(month, false));
        result.append('月');

        // 日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        result.append(NumberChineseFormatter.formatThousand(day, false));
        result.append('日');

        // 只替换年月日，时分秒中零不需要替换
        String temp = result.toString().replace('零', '〇');
        result.delete(0, result.length());
        result.append(temp);

        if (withTime) {
            // 时
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            result.append(NumberChineseFormatter.formatThousand(hour, false));
            result.append('时');
            // 分
            int minute = calendar.get(Calendar.MINUTE);
            result.append(NumberChineseFormatter.formatThousand(minute, false));
            result.append('分');
            // 秒
            int second = calendar.get(Calendar.SECOND);
            result.append(NumberChineseFormatter.formatThousand(second, false));
            result.append('秒');
        }

        return result.toString();
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     */
    public static Calendar parseByPatterns(String str, String... parsePatterns) throws DateException {
        return parseByPatterns(str, null, parsePatterns);
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     */
    public static Calendar parseByPatterns(String str, Locale locale, String... parsePatterns)
            throws DateException {
        return parseByPatterns(str, locale, true, parsePatterns);
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
     * @param lenient       日期时间解析是否使用严格模式
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     * @see Calendar#isLenient()
     */
    public static Calendar parseByPatterns(
            String str, Locale locale, boolean lenient, String... parsePatterns) throws DateException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        final TimeZone tz = TimeZone.getDefault();
        final Locale lcl = Safe.value(locale, Locale.getDefault());
        final ParsePosition pos = new ParsePosition(0);
        final Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);

        for (final String parsePattern : parsePatterns) {
            if (GlobalCustomFormat.isCustomFormat(parsePattern)) {
                final Date parse = GlobalCustomFormat.parse(str, parsePattern);
                if (null == parse) {
                    continue;
                }
                calendar.setTime(parse);
                return calendar;
            }

            final FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                    return calendar;
                }
            } catch (final IllegalArgumentException ignore) {
                // leniency is preventing calendar from being set
            }
            pos.setIndex(0);
        }

        throw new DateException("Unable to parse the date: {}", str);
    }

    /**
     * 使用指定{@link DateParser}解析字符串为{@link Calendar}
     *
     * @param str     日期字符串
     * @param lenient 是否宽容模式
     * @param parser  {@link DateParser}
     * @return 解析后的 {@link Calendar}，解析失败返回{@code null}
     */
    public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
        final Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
        calendar.clear();
        calendar.setLenient(lenient);

        return parser.parse(ToolString.str(str), new ParsePosition(0), calendar) ? calendar : null;
    }

    public static long currentTimeMillis() {
        return currentTimeMillis(null);
    }

    public static long currentTimeMillis(@Nullable TimeZone timeZone) {
        long time = System.currentTimeMillis();
        TimeZone zone = TimeZone.getDefault();
        if (timeZone == null || zone.equals(timeZone)) {
            return time;
        }
        time -= zone.getRawOffset();
        if (timeZone != null) {
            time += timeZone.getRawOffset();
        }
        return time;
    }

    public static int[] currentDateTimeField(@Nullable TimeZone timeZone) {
        return getDateTimeField(currentTimeMillis(timeZone));
    }

    public static int[] getDateTimeField(long timeMillis) {
        long time = timeMillis + 8 * HOUR;
        int year = 1970, month = 1, day = 1, hour = 0, minute = 0, second = 0, millis = 0;
        boolean isBigHundredYear = false, isBigYear = false;

        year += time / CYCLE_FOUR_HUNDRED_YEAR * 400;
        time %= CYCLE_FOUR_HUNDRED_YEAR;

        if (time >= COMMON_HUNDRED_YEAR + DAY) {
            year += 100;
            time -= (COMMON_HUNDRED_YEAR + DAY);

            year += time / COMMON_HUNDRED_YEAR * 100;
            time %= COMMON_HUNDRED_YEAR;

        } else {
            isBigHundredYear = true;
        }

        if (!isBigHundredYear && time >= FOUR_YEAR) {
            time -= FOUR_YEAR;
            year += 4;
        }

        year += (time / BIG_FOUR_YEAR * 4);
        time %= BIG_FOUR_YEAR;

        if (time >= YEAR) {
            year += 1;
            time -= YEAR;
            if (time >= YEAR) {
                year += 1;
                time -= YEAR;
                if (time >= BIG_YEAR) {
                    year += 1;
                    time -= BIG_YEAR;
                } else {
                    isBigYear = true;
                }
            }
        }

        for (int i = 0; i < MONTH_DAYS.length; i++) {
            long m = (long) (MONTH_DAYS[i] + (isBigYear && i == 1 ? 1 : 0)) * DAY;
            if (time >= m) {
                time -= m;
                month += 1;
            } else {
                break;
            }
        }

        day += time / DAY;
        time %= DAY;

        hour += time / HOUR;
        time %= HOUR;

        minute += time / MINUTE;
        time %= MINUTE;

        second += time / SECOND;
        time %= SECOND;

        millis += time / SECOND;

        return new int[]{year, month, day, hour, minute, second, millis};
    }

    public static int formatNumberDate(long timeMillis) {
        int[] dateTimeField = getDateTimeField(timeMillis);
        return (dateTimeField[0] * 100 + dateTimeField[1]) * 100 + dateTimeField[2];
    }

    public static int formatNumberTime(long timeMillis) {
        int[] dateTimeField = getDateTimeField(timeMillis);
        return (dateTimeField[3] * 100 + dateTimeField[4]) * 100 + dateTimeField[5];
    }

    public static String formatTime(long timeMillis) {
        return formatTime(timeMillis, ":");
    }

    public static String formatTime(long timeMillis,
                                    String timeDelimiter) {
        return formatGenerically(timeMillis, null, null, timeDelimiter, 3, 4, 5);
    }

    public static String formatDate(long timeMillis) {
        return formatDate(timeMillis, "-");
    }

    public static String formatDate(long timeMillis,
                                    String dateDelimiter) {
        return formatGenerically(timeMillis, dateDelimiter, null, null, 0, 1, 2);
    }

    public static String formatDateTime(long timeMillis) {
        return formatDateTime(timeMillis, "-", " ", ":");
    }

    public static String formatDateTime(long timeMillis,
                                        String dateDelimiter,
                                        String centerDelimiter,
                                        String timeDelimiter) {
        return formatGenerically(timeMillis, dateDelimiter, centerDelimiter, timeDelimiter, 0, 1, 2, 3, 4, 5);
    }

    private static String formatGenerically(long timeMillis,
                                            String dateDelimiter,
                                            String centerDelimiter,
                                            String timeDelimiter,
                                            int... dateTimeField) {
        dateDelimiter = Safe.value(dateDelimiter);
        centerDelimiter = Safe.value(centerDelimiter);
        timeDelimiter = Safe.value(timeDelimiter);
        int[] allTimeFieldValue = getDateTimeField(timeMillis);
        boolean lastDate = false;
        StringBuilder result = new StringBuilder();
        if (dateTimeField.length > 0) {
            result.append(allTimeFieldValue[dateTimeField[0]]);
            if (dateTimeField[0] < 3) {
                lastDate = true;
            }
        }
        for (int i = 1; i < dateTimeField.length; i++) {
            if (i == 3 && dateTimeField.length > 4) {
                result.append(centerDelimiter);
            } else if (lastDate) {
                result.append(dateDelimiter);
            } else {
                result.append(timeDelimiter);
            }
            if (dateTimeField[i] > 0 && allTimeFieldValue[dateTimeField[i]] <= 9) {
                result.append("0").append(allTimeFieldValue[dateTimeField[i]]);
            } else {
                result.append(allTimeFieldValue[dateTimeField[i]]);
            }
            if (dateTimeField[i] < 3) {
                lastDate = true;
            }
        }
        return result.toString();
    }

    /**
     * 时间的相差天数
     *
     * @param beforeDate 前一个时间
     * @param afterDate  后一个时间
     * @return 天数
     */
    public static int differentDay(Date beforeDate, Date afterDate) {
        return differentDay(beforeDate.getTime(), afterDate.getTime());
    }

    /**
     * 时间的相差天数
     *
     * @param beforeTimeMillis 前一个时间戳
     * @param afterTimeMillis  后一个时间戳
     * @return 天数
     */
    public static int differentDay(long beforeTimeMillis, long afterTimeMillis) {
        return (int) ((afterTimeMillis - beforeTimeMillis) / DAY);
    }

    /**
     * 获取系统时间后几天的时间
     *
     * @param date 时间
     * @return 日期
     */
    public static Date nextOneDate(Date date) {
        return nextDate(date, 1);
    }

    /**
     * 获取系统时间前几天的时间
     *
     * @param date 时间
     * @return 日期
     */
    public static Date priorOneDate(Date date) {
        return priorDate(date, 1);
    }

    /**
     * 获取系统时间后几天的时间
     *
     * @param date      时间
     * @param dayAmount 天数
     * @return 日期
     */
    public static Date nextDate(Date date, int dayAmount) {
        return new Date(nextDayTimeMillis(date.getTime(), dayAmount));
    }

    /**
     * 获取系统时间前几天的时间
     *
     * @param date      时间
     * @param dayAmount 天数
     * @return 日期
     */
    public static Date priorDate(Date date, int dayAmount) {
        return new Date(priorDayTimeMillis(date.getTime(), dayAmount));
    }

    /**
     * 获取系统时间后几天的时间戳
     *
     * @param timeMillis 时间戳
     * @return 日期1
     */
    public static long nextOneDayTimeMillis(long timeMillis) {
        return nextDayTimeMillis(timeMillis, 1);
    }

    /**
     * 获取系统时间前几天的时间戳
     *
     * @param timeMillis 时间戳
     * @return 日期
     */
    public static long priorOneDayTimeMillis(long timeMillis) {
        return priorDayTimeMillis(timeMillis, 1);
    }

    /**
     * 获取系统时间后几天的时间戳
     *
     * @param timeMillis 时间戳
     * @param dayAmount  天数
     * @return 日期
     */
    public static long nextDayTimeMillis(long timeMillis, int dayAmount) {
        return timeMillis + (long) DAY * dayAmount;
    }

    /**
     * 获取系统时间前几天的时间戳
     *
     * @param timeMillis 时间戳
     * @param dayAmount  天数
     * @return 日期
     */
    public static long priorDayTimeMillis(long timeMillis, int dayAmount) {
        return timeMillis - (long) DAY * dayAmount;
    }


    private static final String[] MONTH_NAME = {
            "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"
    };
    private static final String[] MONTH_NAME_TRADITIONAL = {
            "正", "二", "三", "四", "五", "六", "七", "八", "九", "寒", "冬", "腊"
    };

    /**
     * 当前农历月份是否为闰月
     *
     * @param year  农历年
     * @param month 农历月
     * @return 是否为闰月
     */
    public static boolean isLeapMonth(int year, int month) {
        return month == LunarInfo.leapMonth(year);
    }

    /**
     * 获得农历月称呼<br>
     * 当为传统表示时，表示为二月，腊月，或者润正月等 当为非传统表示时，二月，十二月，或者润一月等
     *
     * @param isLeapMonth   是否闰月
     * @param month         月份，从1开始，如果是闰月，应传入需要显示的月份
     * @param isTraditional 是否传统表示，例如一月传统表示为正月
     * @return 返回农历月份称呼
     */
    public static String getChineseMonthName(boolean isLeapMonth, int month, boolean isTraditional) {
        return (isLeapMonth ? "闰" : "")
                + (isTraditional ? MONTH_NAME_TRADITIONAL : MONTH_NAME)[month - 1]
                + "月";
    }
}
