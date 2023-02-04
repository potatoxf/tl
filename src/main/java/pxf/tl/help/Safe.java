package pxf.tl.help;

import pxf.tl.api.*;
import pxf.tl.function.FunctionThrow;
import pxf.tl.lang.AsciiTableMatcher;
import pxf.tl.lang.CharStack;
import pxf.tl.lang.TextBuilder;
import pxf.tl.util.ToolTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;

/**
 * @author potatoxf
 */
public interface Safe extends PoolOfString, PoolOfCommon {

    /**
     * 表示为真的字符串
     */
    InstanceSupplier<Set<Character>> TRUE_CHARACTER_SET = InstanceSupplier.of(() -> Set.of('y', 't', '1', '是', '对', '真', '對', '√'));
    /**
     * 表示为假的字符串
     */
    InstanceSupplier<Set<Character>> FALSE_CHARACTER_SET = InstanceSupplier.of(() -> Set.of('n', 'f', '0', '否', '错', '假', '錯', '×'));
    /**
     * 表示为真的字符串
     */
    InstanceSupplier<Set<String>> TRUE_STRING_SET = InstanceSupplier.of(() -> Set.of("true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√"));
    /**
     * 表示为假的字符串
     */
    InstanceSupplier<Set<String>> FALSE_STRING_SET = InstanceSupplier.of(() -> Set.of("false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×"));

    /**
     * 返回包装类型
     *
     * @param clazz {@code Class}
     * @return 如果是原生类型，则返回包装类型，否则原样返回
     */
    static Class<?> wrapClass(Class<?> clazz) {
        return PoolOfObject.wrap(clazz);
    }

    /**
     * 返回包装类型
     *
     * @param clazz {@code Class}
     * @return 如果是原生类型，则返回包装类型，否则原样返回
     */
    static Class<?> unwrapClass(Class<?> clazz) {
        return PoolOfObject.unwrap(clazz);
    }

    /**
     * 转换成小写
     *
     * @param input 输入字符串数组
     * @return {@code String[]}
     */
    static String[] lowercase(String[] input) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] != null) {
                input[i] = input[i].toLowerCase();
            }
        }
        return input;
    }

    /**
     * 转换成小写
     *
     * @param input  输入字符串数组
     * @param locale 语言区域
     * @return {@code String[]}
     */
    static String[] lowercase(String[] input, Locale locale) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] != null) {
                input[i] = input[i].toLowerCase(locale);
            }
        }
        return input;
    }

    /**
     * 转换成大写
     *
     * @param input 输入字符串数组
     * @return {@code String[]}
     */
    static String[] uppercase(String[] input) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] != null) {
                input[i] = input[i].toUpperCase();
            }
        }
        return input;
    }

    /**
     * 转换成大写
     *
     * @param input  输入字符串数组
     * @param locale 语言区域
     * @return {@code String[]}
     */
    static String[] uppercase(String[] input, Locale locale) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] != null) {
                input[i] = input[i].toUpperCase(locale);
            }
        }
        return input;
    }

    //------------------------------------------------------------------------------------------------------------------
    //时间转换
    //------------------------------------------------------------------------------------------------------------------

    static long toTimeMillis(@Nonnull Date date) {
        return date.getTime();
    }

    static Long toTimeMillis(@Nonnull TemporalAccessor temporalAccessor,
                             @Nonnull ZoneId zoneId) {
        return toDate(temporalAccessor, zoneId).getTime();
    }

    static Date toDate(long timeMillis) {
        return new Date(timeMillis);
    }

    static Date toDate(@Nonnull TemporalAccessor temporalAccessor,
                       @Nonnull ZoneId zoneId) {
        return Date.from(toInstant(temporalAccessor, zoneId));
    }

    static Instant toInstant(long timeMillis) {
        return new Date(timeMillis).toInstant();
    }

    static Instant toInstant(@Nonnull Date date) {
        return date.toInstant();
    }

    static Instant toInstant(@Nonnull TemporalAccessor temporalAccessor,
                             @Nonnull ZoneId zoneId) {
        if (temporalAccessor instanceof Instant) {
            return (Instant) temporalAccessor;
        }
        return toLocalDateTime(temporalAccessor, zoneId).atZone(zoneId).toInstant();
    }

    static OffsetDateTime toOffsetDateTime(long timeMillis,
                                           @Nonnull ZoneId zoneId,
                                           @Nonnull ZoneOffset zoneOffset) {
        return toOffsetDateTime(toInstant(timeMillis), zoneId, zoneOffset);
    }

    static OffsetDateTime toOffsetDateTime(@Nonnull Date date,
                                           @Nonnull ZoneId zoneId,
                                           @Nonnull ZoneOffset zoneOffset) {
        return toOffsetDateTime(date.toInstant(), zoneId, zoneOffset);
    }

    static OffsetDateTime toOffsetDateTime(@Nonnull TemporalAccessor temporalAccessor,
                                           @Nonnull ZoneId zoneId,
                                           @Nonnull ZoneOffset zoneOffset) {
        if (temporalAccessor instanceof OffsetDateTime) {
            return (OffsetDateTime) temporalAccessor;
        }
        return toLocalDateTime(temporalAccessor, zoneId).atOffset(zoneOffset);
    }

    static OffsetTime toOffsetTime(long timeMillis,
                                   @Nonnull ZoneId zoneId,
                                   @Nonnull ZoneOffset zoneOffset) {
        return toOffsetTime(toInstant(timeMillis), zoneId, zoneOffset);
    }

    static OffsetTime toOffsetTime(@Nonnull Date date,
                                   @Nonnull ZoneId zoneId,
                                   @Nonnull ZoneOffset zoneOffset) {
        return toOffsetTime(date.toInstant(), zoneId, zoneOffset);
    }

    static OffsetTime toOffsetTime(@Nonnull TemporalAccessor temporalAccessor,
                                   @Nonnull ZoneId zoneId,
                                   @Nonnull ZoneOffset zoneOffset) {
        if (temporalAccessor instanceof OffsetTime) {
            return (OffsetTime) temporalAccessor;
        }
        return toLocalTime(temporalAccessor, zoneId).atOffset(zoneOffset);
    }


    static ZonedDateTime toZonedDateTime(long timeMillis,
                                         @Nonnull ZoneId zoneId,
                                         @Nonnull ZoneOffset zoneOffset) {
        return toZonedDateTime(toInstant(timeMillis), zoneId, zoneOffset);
    }


    static ZonedDateTime toZonedDateTime(@Nonnull Date date,
                                         @Nonnull ZoneId zoneId,
                                         @Nonnull ZoneOffset zoneOffset) {
        return toZonedDateTime(date.toInstant(), zoneId, zoneOffset);
    }


    static ZonedDateTime toZonedDateTime(@Nonnull TemporalAccessor temporalAccessor,
                                         @Nonnull ZoneId zoneId,
                                         @Nonnull ZoneOffset zoneOffset) {
        if (temporalAccessor instanceof ZonedDateTime) {
            return (ZonedDateTime) temporalAccessor;
        }
        return toLocalDateTime(temporalAccessor, zoneId).atZone(zoneId);
    }


    static Month toMonth(long timeMillis,
                         @Nonnull ZoneId zoneId) {
        return toMonth(toInstant(timeMillis), zoneId);
    }

    static Month toMonth(@Nonnull Date date,
                         @Nonnull ZoneId zoneId) {
        return toMonth(date.toInstant(), zoneId);
    }

    static Month toMonth(@Nonnull TemporalAccessor temporalAccessor,
                         @Nonnull ZoneId zoneId) {
        if (temporalAccessor instanceof Month) {
            return (Month) temporalAccessor;
        }
        return Month.from(toLocalDateTime(temporalAccessor, zoneId));
    }

    static YearMonth toYearMonth(long timeMillis,
                                 @Nonnull ZoneId zoneId) {
        return toYearMonth(toInstant(timeMillis), zoneId);
    }

    static YearMonth toYearMonth(@Nonnull Date date,
                                 @Nonnull ZoneId zoneId) {
        return toYearMonth(date.toInstant(), zoneId);
    }

    static YearMonth toYearMonth(@Nonnull TemporalAccessor temporalAccessor,
                                 @Nonnull ZoneId zoneId) {
        if (temporalAccessor instanceof YearMonth) {
            return (YearMonth) temporalAccessor;
        }
        return YearMonth.from(toLocalDateTime(temporalAccessor, zoneId));
    }

    static LocalTime toLocalTime(long timeMillis,
                                 @Nonnull ZoneId zoneId) {
        return toLocalTime(toInstant(timeMillis), zoneId);
    }

    static LocalTime toLocalTime(@Nonnull Date date,
                                 @Nonnull ZoneId zoneId) {
        return toLocalTime(date.toInstant(), zoneId);
    }

    static LocalTime toLocalTime(@Nonnull TemporalAccessor temporalAccessor,
                                 @Nonnull ZoneId zoneId) {
        return switch (temporalAccessor) {
            case LocalTime l -> l;
            case LocalDate l -> l.atTime(LocalTime.now()).toLocalTime();
            case LocalDateTime l -> l.toLocalTime();
            case Instant i -> i.atZone(zoneId).toLocalTime();
            case ZonedDateTime z -> z.toLocalTime();
            case OffsetDateTime o -> o.toLocalTime();
            case OffsetTime o -> o.toLocalTime();
            default -> LocalTime.of(
                    ToolTime.get(temporalAccessor, ChronoField.HOUR_OF_DAY),
                    ToolTime.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR),
                    ToolTime.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE),
                    ToolTime.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
        };
    }

    static LocalDate toLocalDate(long timeMillis,
                                 @Nonnull ZoneId zoneId) {
        return toLocalDate(toInstant(timeMillis), zoneId);
    }

    static LocalDate toLocalDate(@Nonnull Date date,
                                 @Nonnull ZoneId zoneId) {
        return toLocalDate(date.toInstant(), zoneId);
    }

    static LocalDate toLocalDate(@Nonnull TemporalAccessor temporalAccessor,
                                 @Nonnull ZoneId zoneId) {
        return switch (temporalAccessor) {
            case LocalDate l -> l;
            case LocalTime l -> l.atDate(LocalDate.now()).toLocalDate();
            case LocalDateTime l -> l.toLocalDate();
            case Instant i -> i.atZone(zoneId).toLocalDate();
            case ZonedDateTime z -> z.toLocalDate();
            case OffsetDateTime o -> o.toLocalDate();
            case OffsetTime o -> o.atDate(LocalDate.now()).toLocalDate();
            default -> LocalDate.of(
                    ToolTime.get(temporalAccessor, ChronoField.YEAR),
                    ToolTime.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
                    ToolTime.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
        };
    }

    static LocalDateTime toLocalDateTime(long timeMillis,
                                         @Nonnull ZoneId zoneId) {
        return toLocalDateTime(toInstant(timeMillis), zoneId);
    }

    static LocalDateTime toLocalDateTime(@Nonnull Date date,
                                         @Nonnull ZoneId zoneId) {
        return toLocalDateTime(date.toInstant(), zoneId);
    }

    static LocalDateTime toLocalDateTime(@Nonnull TemporalAccessor temporalAccessor,
                                         @Nonnull ZoneId zoneId) {
        return switch (temporalAccessor) {
            case LocalDateTime l -> l;
            case LocalTime l -> l.atDate(LocalDate.now());
            case LocalDate l -> l.atTime(LocalTime.now());
            case Instant i -> LocalDateTime.ofInstant(i, zoneId);
            case ZonedDateTime z -> z.toLocalDateTime();
            case OffsetDateTime o -> o.toLocalDateTime();
            case OffsetTime o -> o.atDate(LocalDate.now()).toLocalDateTime();
            default -> LocalDateTime.of(
                    ToolTime.get(temporalAccessor, ChronoField.YEAR),
                    ToolTime.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
                    ToolTime.get(temporalAccessor, ChronoField.DAY_OF_MONTH),
                    ToolTime.get(temporalAccessor, ChronoField.HOUR_OF_DAY),
                    ToolTime.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR),
                    ToolTime.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE),
                    ToolTime.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
        };
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Byte[]
    //------------------------------------------------------------------------------------------------------------------

    static Byte[] toByteArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toByte(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Short[]
    //------------------------------------------------------------------------------------------------------------------

    static Short[] toShortArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toShort(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }


    //------------------------------------------------------------------------------------------------------------------
    //转换为Integer[]
    //------------------------------------------------------------------------------------------------------------------

    static Integer[] toIntegerArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toInteger(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Long[]
    //------------------------------------------------------------------------------------------------------------------

    static Long[] toLongArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toLong(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Float[]
    //------------------------------------------------------------------------------------------------------------------

    static Float[] toFloatArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toFloat(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Double[]
    //------------------------------------------------------------------------------------------------------------------

    static Double[] toDoubleArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toDouble(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Boolean[]
    //------------------------------------------------------------------------------------------------------------------

    static Boolean[] toBooleanArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toBoolean(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Character[]
    //------------------------------------------------------------------------------------------------------------------

    static Character[] toCharacterArray(@Nullable String[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toCharacter(v, null);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }


    //------------------------------------------------------------------------------------------------------------------
    //转换为Byte[]
    //------------------------------------------------------------------------------------------------------------------

    static Byte[] toByteArray(@Nullable String[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Byte[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Integer[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Long[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Float[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Double[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Character[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Byte[] toByteArray(@Nullable Boolean[] value, Byte defaultValue) {
        if (value == null || value.length == 0) {
            return new Byte[0];
        }
        Byte[] outputs = new Byte[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toByte(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Short[]
    //------------------------------------------------------------------------------------------------------------------

    static Short[] toShortArray(@Nullable String[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Byte[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Integer[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Long[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Float[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Double[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Character[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Short[] toShortArray(@Nullable Boolean[] value, Short defaultValue) {
        if (value == null || value.length == 0) {
            return new Short[0];
        }
        Short[] outputs = new Short[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toShort(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }


    //------------------------------------------------------------------------------------------------------------------
    //转换为Integer[]
    //------------------------------------------------------------------------------------------------------------------

    static Integer[] toIntegerArray(@Nullable String[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Byte[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Short[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Long[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Float[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Double[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Character[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Integer[] toIntegerArray(@Nullable Boolean[] value, Integer defaultValue) {
        if (value == null || value.length == 0) {
            return new Integer[0];
        }
        Integer[] outputs = new Integer[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toInteger(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Long[]
    //------------------------------------------------------------------------------------------------------------------

    static Long[] toLongArray(@Nullable String[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Byte[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Short[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Integer[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Float[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Double[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Character[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Long[] toLongArray(@Nullable Boolean[] value, Long defaultValue) {
        if (value == null || value.length == 0) {
            return new Long[0];
        }
        Long[] outputs = new Long[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toLong(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Float[]
    //------------------------------------------------------------------------------------------------------------------

    static Float[] toFloatArray(@Nullable String[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Byte[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Short[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Integer[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Long[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Double[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Character[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Float[] toFloatArray(@Nullable Boolean[] value, Float defaultValue) {
        if (value == null || value.length == 0) {
            return new Float[0];
        }
        Float[] outputs = new Float[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toFloat(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Double[]
    //------------------------------------------------------------------------------------------------------------------

    static Double[] toDoubleArray(@Nullable String[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Byte[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Short[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Integer[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Long[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Float[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Character[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Double[] toDoubleArray(@Nullable Boolean[] value, Double defaultValue) {
        if (value == null || value.length == 0) {
            return new Double[0];
        }
        Double[] outputs = new Double[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toDouble(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Boolean[]
    //------------------------------------------------------------------------------------------------------------------

    static Boolean[] toBooleanArray(@Nullable String[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Byte[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Short[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Integer[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Long[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Float[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Double[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Boolean[] toBooleanArray(@Nullable Character[] value, Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return new Boolean[0];
        }
        Boolean[] outputs = new Boolean[value.length];
        int i = 0;
        for (Character v : value) {
            outputs[i] = toBoolean(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //转换为Character[]
    //------------------------------------------------------------------------------------------------------------------

    static Character[] toCharacterArray(@Nullable String[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (String v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Byte[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Byte v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Short[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Short v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Integer[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Integer v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Long[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Long v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Float[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Float v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Double[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Double v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static Character[] toCharacterArray(@Nullable Boolean[] value, Character defaultValue) {
        if (value == null || value.length == 0) {
            return new Character[0];
        }
        Character[] outputs = new Character[value.length];
        int i = 0;
        for (Boolean v : value) {
            outputs[i] = toCharacter(v, defaultValue);
            if (outputs[i] != null) {
                i++;
            }
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    //------------------------------------------------------------------------------------------------------------------
    //基本元素转换
    //------------------------------------------------------------------------------------------------------------------

    static Byte toByte(@Nullable String value, Byte defaultValue) {
        if (value != null) {
            try {
                BigInteger v = toBigIntegerNumber(value);
                if (v != null) {
                    return toByte(v, defaultValue);
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Byte toByte(@Nullable Number value, Byte defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            if (!value.equals(value.byteValue())) {
                return Byte.MAX_VALUE;
            } else {
                return value.byteValue();
            }
        }
    }

    static Byte toByte(@Nullable Character value, Byte defaultValue) {
        return value == null ? defaultValue : (Byte) (byte) value.charValue();
    }

    static Byte toByte(@Nullable Boolean value, Byte defaultValue) {
        return value == null ? defaultValue : (Byte) (byte) (value ? 1 : 0);
    }

    static Short toShort(@Nullable String value, Short defaultValue) {
        if (value != null) {
            try {
                BigInteger v = toBigIntegerNumber(value);
                if (v != null) {
                    return toShort(v, defaultValue);
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Short toShort(@Nullable Number value, Short defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            if (!value.equals(value.byteValue())) {
                return Short.MAX_VALUE;
            } else {
                return value.shortValue();
            }
        }
    }

    static Short toShort(@Nullable Character value, Short defaultValue) {
        return value == null ? defaultValue : (Short) (short) value.charValue();
    }

    static Short toShort(@Nullable Boolean value, Short defaultValue) {
        return value == null ? defaultValue : (Short) (short) (value ? 1 : 0);
    }

    static Integer toInteger(@Nullable String value, Integer defaultValue) {
        if (value != null) {
            try {
                BigInteger v = toBigIntegerNumber(value);
                if (v != null) {
                    return toInteger(v, defaultValue);
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Integer toInteger(@Nullable Number value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        } else {
            if (!value.equals(value.byteValue())) {
                return Integer.MAX_VALUE;
            } else {
                return value.intValue();
            }
        }
    }

    static Integer toInteger(@Nullable Character value, Integer defaultValue) {
        return value == null ? defaultValue : (Integer) (int) value;
    }

    static Integer toInteger(@Nullable Boolean value, Integer defaultValue) {
        return value == null ? defaultValue : (Integer) (value ? 1 : 0);
    }

    static Long toLong(@Nullable String value, Long defaultValue) {
        if (value != null) {
            try {
                BigInteger v = toBigIntegerNumber(value);
                if (v != null) {
                    if (!v.equals(BigInteger.valueOf(v.longValue()))) {
                        return Long.MAX_VALUE;
                    } else {
                        return v.longValue();
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Long toLong(@Nullable Number value, Long defaultValue) {
        return value == null ? defaultValue : (Long) value.longValue();
    }

    static Long toLong(@Nullable Character value, Long defaultValue) {
        return value == null ? defaultValue : (Long) (long) value;
    }

    static Long toLong(@Nullable Boolean value, Long defaultValue) {
        return value == null ? defaultValue : (Long) (value ? 1L : 0L);
    }

    static Float toFloat(@Nullable String value, Float defaultValue) {
        if (value != null) {
            try {
                BigDecimal v = toBigDecimalNumber(value);
                if (v != null) {
                    return v.floatValue();
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Float toFloat(@Nullable Number value, Float defaultValue) {
        return value == null ? defaultValue : (Float) value.floatValue();
    }

    static Float toFloat(@Nullable Character value, Float defaultValue) {
        return value == null ? defaultValue : (Float) (float) value;
    }

    static Float toFloat(@Nullable Boolean value, Float defaultValue) {
        return value == null ? defaultValue : (Float) (value ? 1f : 0f);
    }

    static Double toDouble(@Nullable String value, Double defaultValue) {
        if (value != null) {
            try {
                BigDecimal v = toBigDecimalNumber(value);
                if (v != null) {
                    return v.doubleValue();
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static Double toDouble(@Nullable Number value, Double defaultValue) {
        return value == null ? defaultValue : (Double) value.doubleValue();
    }

    static Double toDouble(@Nullable Character value, Double defaultValue) {
        return value == null ? defaultValue : (Double) (double) value;
    }

    static Double toDouble(@Nullable Boolean value, Double defaultValue) {
        return value == null ? defaultValue : (Double) (value ? 1d : 0d);
    }

    static BigInteger toBigInteger(@Nullable String value, BigInteger defaultValue) {
        if (value != null) {
            try {
                BigInteger v = toBigIntegerNumber(value);
                if (v != null) {
                    return v;
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static BigInteger toBigInteger(@Nullable Number value, BigInteger defaultValue) {
        return value == null ? defaultValue : BigInteger.valueOf(value.longValue());
    }

    static BigInteger toBigInteger(@Nullable Character value, BigInteger defaultValue) {
        return value == null ? defaultValue : BigInteger.valueOf(value);
    }

    static BigInteger toBigInteger(@Nullable Boolean value, BigInteger defaultValue) {
        return value == null ? defaultValue : value ? BigInteger.ONE : BigInteger.ZERO;
    }

    static BigDecimal toBigDecimal(@Nullable String value, BigDecimal defaultValue) {
        if (value != null) {
            try {
                BigDecimal v = toBigDecimalNumber(value);
                if (v != null) {
                    return v;
                }
            } catch (Throwable ignored) {
            }
        }
        return defaultValue;
    }

    static BigDecimal toBigDecimal(@Nullable Number value, BigDecimal defaultValue) {
        return value == null ? defaultValue : BigDecimal.valueOf(value.doubleValue());
    }

    static BigDecimal toBigDecimal(@Nullable Character value, BigDecimal defaultValue) {
        return value == null ? defaultValue : BigDecimal.valueOf(value);
    }

    static BigDecimal toBigDecimal(@Nullable Boolean value, BigDecimal defaultValue) {
        return value == null ? defaultValue : value ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    static BigDecimal toBigDecimalNumber(@Nullable String value) {
        try {
            return new BigDecimal(value);
        } catch (Throwable ignored) {
        }
        return null;
    }

    static BigInteger toBigIntegerNumber(@Nullable String value) {
        try {
            return new BigDecimal(value).toBigInteger();
        } catch (Throwable ignored) {
        }
        return null;
    }

    static Boolean toBoolean(@Nullable String value, Boolean defaultValue) {
        return value == null || value.length() == 0 ? defaultValue : (Boolean) (TRUE_STRING_SET.get().contains(value)
                || (FALSE_STRING_SET.get().contains(value) ? false : null));
    }

    static Boolean toBoolean(@Nullable Number value, Boolean defaultValue) {
        return value == null ? defaultValue : (Boolean) (value.intValue() != 0);
    }

    static Boolean toBoolean(@Nullable Character value, Boolean defaultValue) {
        return value == null ? defaultValue : (Boolean) (TRUE_CHARACTER_SET.get().contains(value)
                || (FALSE_CHARACTER_SET.get().contains(value) ? false : null));
    }

    static Character toCharacter(@Nullable String value, Character defaultValue) {
        return value == null || value.length() == 0 ? defaultValue : (Character) value.charAt(0);
    }

    static Character toCharacter(@Nullable Boolean value, Character defaultValue) {
        return value == null ? defaultValue : (Character) (value ? 'T' : 'F');
    }

    static Character toCharacter(@Nullable Number value, Character defaultValue) {
        return value == null ? defaultValue : (Character) (char) value.intValue();
    }

    //------------------------------------------------------------------------------------------------------------------
    //包装类型与原生类型转换
    //------------------------------------------------------------------------------------------------------------------

    static Byte[] wrap(@Nullable byte[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_BYTE_OBJECT_ARRAY;
        }
        Byte[] outputs = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Short[] wrap(@Nullable short[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_SHORT_OBJECT_ARRAY;
        }
        Short[] outputs = new Short[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Integer[] wrap(@Nullable int[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        Integer[] outputs = new Integer[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Long[] wrap(@Nullable long[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_LONG_OBJECT_ARRAY;
        }
        Long[] outputs = new Long[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Float[] wrap(@Nullable float[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        Float[] outputs = new Float[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Double[] wrap(@Nullable double[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        Double[] outputs = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Boolean[] wrap(@Nullable boolean[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        Boolean[] outputs = new Boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static Character[] wrap(@Nullable char[] value) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        Character[] outputs = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            outputs[i] = value[i];
        }
        return outputs;
    }

    static byte[] unwrap(@Nullable Byte[] value, @Nullable Byte defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_BYTE_ARRAY;
        }
        byte[] outputs = new byte[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static short[] unwrap(@Nullable Short[] value, @Nullable Short defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_SHORT_ARRAY;
        }
        short[] outputs = new short[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static int[] unwrap(@Nullable Integer[] value, @Nullable Integer defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_INT_ARRAY;
        }
        int[] outputs = new int[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static long[] unwrap(@Nullable Long[] value, @Nullable Long defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_LONG_ARRAY;
        }
        long[] outputs = new long[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static float[] unwrap(@Nullable Float[] value, @Nullable Float defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_FLOAT_ARRAY;
        }
        float[] outputs = new float[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static double[] unwrap(@Nullable Double[] value, @Nullable Double defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_DOUBLE_ARRAY;
        }
        double[] outputs = new double[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static boolean[] unwrap(@Nullable Boolean[] value, @Nullable Boolean defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_BOOLEAN_ARRAY;
        }
        boolean[] outputs = new boolean[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static char[] unwrap(@Nullable Character[] value, @Nullable Character defaultValue) {
        if (value == null || value.length == 0) {
            return PoolOfArray.EMPTY_CHAR_ARRAY;
        }
        char[] outputs = new char[value.length];
        int i = 0;
        while (i < value.length) {
            if (defaultValue != null) {
                outputs[i] = unwrap(value[i], defaultValue);
            } else if (value[i] != null) {
                outputs[i] = value[i];
            } else {
                continue;
            }
            i++;
        }
        if (i != value.length) {
            return Arrays.copyOf(outputs, i);
        }
        return outputs;
    }

    static byte unwrap(@Nullable Byte value, byte defaultValue) {
        return value == null ? defaultValue : value;
    }

    static short unwrap(@Nullable Short value, short defaultValue) {
        return value == null ? defaultValue : value;
    }

    static int unwrap(@Nullable Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    static long unwrap(@Nullable Long value, long defaultValue) {
        return value == null ? defaultValue : value;
    }

    static float unwrap(@Nullable Float value, float defaultValue) {
        return value == null ? defaultValue : value;
    }

    static double unwrap(@Nullable Double value, double defaultValue) {
        return value == null ? defaultValue : value;
    }

    static boolean unwrap(@Nullable Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    static char unwrap(@Nullable Character value, char defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * 半角转全角，{@code null}返回{@code null}
     *
     * @param value String.
     * @return 全角字符串，{@code null}返回{@code null}
     */
    static String toSBC(String value) {
        return toSBC(value, null);
    }

    /**
     * 半角转全角，{@code null}返回{@code null}
     *
     * @param value         String
     * @param notConvertSet 不替换的字符集合
     * @return 全角字符串，{@code null}返回{@code null}
     */
    static String toSBC(String value, Set<Character> notConvertSet) {
        if (Whether.empty(value)) {
            return value;
        }
        final char[] c = value.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (null != notConvertSet && notConvertSet.contains(c[i])) {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == PoolOfCharacter.SPACE) {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param value String.
     * @return 半角字符串
     */
    static String toDBC(String value) {
        return toDBC(value, null);
    }

    /**
     * 替换全角为半角
     *
     * @param value         文本
     * @param notConvertSet 不替换的字符集合
     * @return 替换后的字符
     */
    static String toDBC(String value, Set<Character> notConvertSet) {
        if (Whether.blank(value)) {
            return value;
        }
        final char[] c = value.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (null != notConvertSet && notConvertSet.contains(c[i])) {
                // 跳过不替换的字符
                continue;
            }

            if (c[i] == '\u3000' || c[i] == '\u00a0' || c[i] == '\u2007' || c[i] == '\u202F') {
                // \u3000是中文全角空格，\u00a0、\u2007、\u202F是不间断空格
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }

        return new String(c);
    }

    /**
     * 安全获取值，不会抛出异常
     *
     * @param instance 实例对象
     * @param getter   获取器
     * @param <T>      实例类型
     * @param <R>      返回类型
     * @return 返回值，失败返回null
     */
    @Nonnull
    static <T, R> R getOrDefault(
            @Nullable final T instance,
            @Nullable final T defaultInstance,
            @Nonnull final FunctionThrow<T, R, Throwable> getter,
            @Nonnull final R defaultValue) {
        R result = at(instance, getter);
        if (result == null && instance != defaultInstance) {
            result = at(defaultInstance, getter);
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * 安全获取值，不会抛出异常
     *
     * @param instance 实例对象
     * @param getter   获取器
     * @param <T>      实例类型
     * @param <R>      返回类型
     * @return 返回值，失败返回null
     */
    @Nonnull
    static <T, R> R getOrDefault(
            @Nullable final T instance,
            @Nonnull final FunctionThrow<T, R, Throwable> getter,
            @Nonnull final R defaultValue) {
        R result = null;
        if (instance != null) {
            try {
                result = getter.apply(instance);
            } catch (Throwable ignored) {
            }
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    static String value(String input) {
        return input == null ? EMPTY : input;
    }

    static String value(CharSequence input) {
        return input == null ? EMPTY : input.toString();
    }

    static String value(String input, String defaultValue) {
        return Whether.empty(input) ? defaultValue : input;
    }

    static String value(Object input, String defaultValue) {
        return Whether.empty(input) ? defaultValue : input.toString();
    }

    static <T> T value(T input, T defaultValue) {
        return input == null ? defaultValue : input;
    }

    static <T> T value(T input, Supplier<T> defaultValue) {
        return input == null ? Objects.requireNonNull(defaultValue, "").get() : input;
    }

    static <K, V> Map<K, V> value(Map<K, V> input) {
        return input == null ? Collections.emptyMap() : input;
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptyList()}
     *
     * @param <T>   集合元素类型
     * @param input 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     */
    static <T> List<T> value(List<T> input) {
        return input == null ? Collections.emptyList() : input;
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptySet()}
     *
     * @param <T>   集合元素类型
     * @param input 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     */
    static <T> Set<T> value(Set<T> input) {
        return input == null ? Collections.emptySet() : input;
    }

    static boolean equals(Object[] a, Object[] b, int specifyLength) {
        if (a == null || b == null) return false;
        if (a.length == b.length && (specifyLength < 0 || specifyLength >= a.length)) {
            return Arrays.equals(a, b);
        } else if (specifyLength <= a.length && specifyLength <= b.length) {
            for (int i = 0; i < specifyLength; i++) {
                if (!Objects.equals(a[i], b[i])) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Return as hash code for the given object; typically the value of {@code Object#hashCode()}}. If
     * the object is an array, this method will delegate to any of the {@code hashcode} methods for
     * arrays in this class. If the object is {@code null}, this method returns 0.
     *
     * @see Object#hashCode()
     * @see #hashcode(Object[], int)
     * @see #hashcode(boolean[])
     * @see #hashcode(byte[])
     * @see #hashcode(char[])
     * @see #hashcode(double[])
     * @see #hashcode(float[])
     * @see #hashcode(int[])
     * @see #hashcode(long[])
     * @see #hashcode(short[])
     */
    static int hashcode(Object object) {
        if (object == null) {
            return 0;
        }
        if (object.getClass().isArray()) {
            if (object instanceof Object[] arr) {
                return hashcode(arr);
            }
            if (object instanceof boolean[] arr) {
                return hashcode(arr);
            }
            if (object instanceof byte[] arr) {
                return hashcode(arr);
            }
            if (object instanceof char[] arr) {
                return hashcode(arr);
            }
            if (object instanceof double[] arr) {
                return hashcode(arr);
            }
            if (object instanceof float[] arr) {
                return hashcode(arr);
            }
            if (object instanceof int[] arr) {
                return hashcode(arr);
            }
            if (object instanceof long[] arr) {
                return hashcode(arr);
            }
            if (object instanceof short[] arr) {
                return hashcode(arr);
            }
        }
        return object.hashCode();
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(Object[] array) {
        return Safe.hashcode(array, -1);
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(Object[] array, int length) {
        if (array == null) return 0;
        if (length >= array.length) {
            length = array.length;
        } else if (length < 0) {
            length = 0;
        }
        int hash = INITIAL_HASH;
        for (int i = 0; i < length; i++) {
            Object element = array[i];
            hash = MULTIPLIER * hash + hashcode(element);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (boolean element : array) {
            hash = MULTIPLIER * hash + ((Boolean) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(byte[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (byte element : array) {
            hash = MULTIPLIER * hash + ((Byte) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(char[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (char element : array) {
            hash = MULTIPLIER * hash + ((Character) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(short[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (short element : array) {
            hash = MULTIPLIER * hash + ((Short) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(int[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (int element : array) {
            hash = MULTIPLIER * hash + ((Integer) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(long[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (long element : array) {
            hash = MULTIPLIER * hash + ((Long) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(float[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (float element : array) {
            hash = MULTIPLIER * hash + ((Float) element).hashCode();
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code
     * null}, this method returns 0.
     */
    static int hashcode(double[] array) {
        if (array == null) {
            return 0;
        }
        int hash = INITIAL_HASH;
        for (double element : array) {
            hash = MULTIPLIER * hash + ((Double) element).hashCode();
        }
        return hash;
    }

    /**
     * 计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度<br>
     * 支持的类型包括：
     *
     * <ul>
     *   <li>CharSequence
     *   <li>Map
     *   <li>Iterator
     *   <li>Enumeration
     *   <li>Array
     * </ul>
     *
     * @param object 被计算长度的对象
     * @return 长度
     */
    static int length(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).size();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).size();
        }

        int count;
        if (object instanceof final Iterator<?> iter) {
            count = 0;
            while (iter.hasNext()) {
                count++;
                iter.next();
            }
            return count;
        }
        if (object instanceof final Enumeration<?> enumeration) {
            count = 0;
            while (enumeration.hasMoreElements()) {
                count++;
                enumeration.nextElement();
            }
            return count;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object);
        }
        return -1;
    }

    /**
     * 安全获取值，不会抛出异常
     *
     * @param instance 实例对象
     * @param getter   获取器
     * @param <T>      实例类型
     * @param <R>      返回类型
     * @return 返回值，失败返回null
     */
    @Nullable
    static <T, R> R at(
            @Nullable final T instance,
            @Nullable final T defaultInstance,
            @Nonnull final FunctionThrow<T, R, Throwable> getter) {
        R r = at(instance, getter);
        if (r == null && instance != defaultInstance) {
            return at(defaultInstance, getter);
        }
        return r;
    }

    /**
     * 安全获取值，不会抛出异常
     *
     * @param instance 实例对象
     * @param getter   获取器
     * @param <T>      实例类型
     * @param <R>      返回类型
     * @return 返回值，失败返回null
     */
    @Nullable
    static <T, R> R at(
            @Nullable final T instance, @Nonnull final FunctionThrow<T, R, Throwable> getter) {
        if (instance != null) {
            try {
                return getter.apply(instance);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static boolean atIdx(boolean[] arr, int index, boolean defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static byte atIdx(byte[] arr, int index, byte defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static char atIdx(char[] arr, int index, char defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static short atIdx(short[] arr, int index, short defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static int atIdx(int[] arr, int index, int defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static long atIdx(long[] arr, int index, long defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static float atIdx(float[] arr, int index, float defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static double atIdx(double[] arr, int index, double defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回空字符串
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回空字符串
     */
    static String atIdx(String[] arr, int index, String defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : (defaultValue != null ? defaultValue : PoolOfString.EMPTY);
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素<br>
     * 如果元素越界，返回null
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param index      下标，支持负数
     * @return 元素值
     */
    static <T> T atIdx(Collection<T> collection, int index, T defaultValue) {
        if (collection != null) {
            int size = collection.size();
            if (size != 0) {
                if (collection instanceof final List<T> list) {
                    return list.get(idx(index, size));
                } else {
                    return at(collection.iterator(), idx(index, size), defaultValue);
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回null
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回null
     */
    static <E> E atIdx(E[] arr, int index, E defaultValue) {
        return arr != null ? arr[idx(index, arr.length)] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static boolean at(boolean[] arr, int index, boolean defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static byte at(byte[] arr, int index, byte defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static char at(char[] arr, int index, char defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static short at(short[] arr, int index, short defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static int at(int[] arr, int index, int defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static long at(long[] arr, int index, long defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static float at(float[] arr, int index, float defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回默认值
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回默认值
     */
    static double at(double[] arr, int index, double defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取数组有效值，如果不有效，则返回空字符串
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回空字符串
     */
    static String at(String[] arr, int index, String defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : (defaultValue != null ? defaultValue : PoolOfString.EMPTY);
    }

    /**
     * 获取数组有效值，如果不有效，则返回null
     *
     * @param arr          数组
     * @param index        索引
     * @param defaultValue 默认值
     * @return 获取数组有效值，如果不有效，则返回null
     */
    static <E> E at(E[] arr, int index, E defaultValue) {
        return arr != null && arr.length > index && index >= 0 ? arr[index] : defaultValue;
    }

    /**
     * 获取集合的第一个元素，如果集合为空（null或者空集合），返回{@code null}
     *
     * @param iterable     {@link Iterable}
     * @param index        位置
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取元素，找不到元素返回默认值
     */
    static <T> T at(Iterable<T> iterable, int index, T defaultValue) {
        return iterable != null ? at(iterable.iterator(), index, defaultValue) : defaultValue;
    }

    /**
     * 遍历{@link Iterator}，获取指定index位置的元素
     *
     * @param iterator     {@link Iterator}
     * @param index        位置
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取元素，找不到元素返回默认值
     */
    static <T> T at(Iterator<T> iterator, int index, T defaultValue) {
        if (iterator != null && index >= 0) {
            while (iterator.hasNext() && index-- > 0) {
                iterator.next();
            }
            if (index == 0) {
                return iterator.next();
            }
        }
        return defaultValue;
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素<br>
     * 如果元素越界，返回null
     *
     * @param collection   集合
     * @param index        位置
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取元素，找不到元素返回默认值
     */
    static <T> T at(Collection<T> collection, int index, T defaultValue) {
        if (collection != null) {
            int size = collection.size();
            if (index >= 0 && index < size) {
                if (collection instanceof final List<T> list) {
                    return list.get(index);
                } else {
                    return at(collection.iterator(), index, defaultValue);
                }
            }
        }
        return defaultValue;
    }

    /**
     * 获取集合的第一个元素，如果集合为空（null或者空集合），返回{@code null}
     *
     * @param iterable     {@link Iterable}
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取第一个元素，找不到元素返回默认值
     */
    static <T> T atFirst(Iterable<T> iterable, T defaultValue) {
        return at(iterable, 0, null);
    }

    /**
     * 获取集合的第一个元素
     *
     * @param iterator     {@link Iterator}
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取第一个元素，找不到元素返回默认值
     */
    static <T> T atFirst(Iterator<T> iterator, T defaultValue) {
        return at(iterator, 0, defaultValue);
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素<br>
     * 如果元素越界，返回null
     *
     * @param collection   集合
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 获取第一个元素，找不到元素返回默认值
     */
    static <T> T atFirst(Collection<T> collection, T defaultValue) {
        return at(collection, 0, defaultValue);
    }

    static int atRange(int lengthExponent, int start, int end) {
        return Math.min(Math.max(lengthExponent, start), end);
    }

    static long atRange(long lengthExponent, long start, long end) {
        return Math.min(Math.max(lengthExponent, start), end);
    }

    static float atRange(float lengthExponent, float start, float end) {
        return Math.min(Math.max(lengthExponent, start), end);
    }

    static double atRange(double lengthExponent, double start, double end) {
        return Math.min(Math.max(lengthExponent, start), end);
    }

    /**
     * 合法化索引
     *
     * @param index  索引
     * @param length 总长度
     * @return 返回合法化索引
     */
    static int idx(int index, int length) {
        while (index < 0) {
            index += length;
        }
        while (index >= length) {
            index -= length;
        }
        return index;
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static byte gt(Byte value, byte target, byte elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static short gt(Short value, short target, short elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static int gt(Integer value, int target, int elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static long gt(Long value, long target, long elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static float gt(Float value, float target, float elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于目标值返回原值，否则返回默认值
     */
    static double gt(Double value, double target, double elseValue) {
        return value == null ? elseValue : (value > target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static byte gtEq(Byte value, byte target, byte elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static short gtEq(Short value, short target, short elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static int gtEq(Integer value, int target, int elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static long gtEq(Long value, long target, long elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static float gtEq(Float value, float target, float elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值大于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值大于等于目标值返回原值，否则返回默认值
     */
    static double gtEq(Double value, double target, double elseValue) {
        return value == null ? elseValue : (value >= target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static byte lt(Byte value, byte target, byte elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static short lt(Short value, short target, short elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static int lt(Integer value, int target, int elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static long lt(Long value, long target, long elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static float lt(Float value, float target, float elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于目标值返回原值，否则返回默认值
     */
    static double lt(Double value, double target, double elseValue) {
        return value == null ? elseValue : (value < target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static byte ltEq(Byte value, byte target, byte elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static short ltEq(Short value, short target, short elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static int ltEq(Integer value, int target, int elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static long ltEq(Integer value, long target, long elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static float ltEq(Float value, float target, float elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
    }

    /**
     * 如果值小于等于目标值返回原值，否则返回默认值
     *
     * @param value     值
     * @param target    目标值
     * @param elseValue 否则当不满条件的值
     * @return 如果值小于等于目标值返回原值，否则返回默认值
     */
    static double ltEq(Double value, double target, double elseValue) {
        return value == null ? elseValue : (value <= target ? value : elseValue);
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
     * file:/a/b/c/}--->{@code a/b/c/} {@code a/b/c}--->{@code a/b/c/} {@code a/b/c}--->{@code a/b/c/}
     *
     * @param isPathSplitStart 是否为路径分割符开始
     * @param isPathSplitEnd   是否为路径分割符结尾
     * @param paths            路径
     * @return 返回合法化文件路径
     */
    static String formatPath(boolean isPathSplitStart, boolean isPathSplitEnd, String... paths) {
        String pathSplit = "/";
        if (paths == null || paths.length == 0) {
            return isPathSplitEnd || isPathSplitStart ? pathSplit : "";
        }
        StringBuilder sb = new StringBuilder(256);
        int i = 0;
        while (i < paths.length) {
            if (paths[i] != null && paths[i].length() != 0) {
                break;
            }
            i++;
        }
        if (i >= paths.length) {
            return isPathSplitEnd || isPathSplitStart ? pathSplit : "";
        }

        StringTokenizer stringTokenizer = new StringTokenizer(paths[i++], "\\/");

        if (!isPathSplitStart && stringTokenizer.hasMoreTokens()) {
            sb.append(stringTokenizer.nextToken());
        }
        while (stringTokenizer.hasMoreTokens()) {
            sb.append(pathSplit).append(stringTokenizer.nextToken());
        }

        while (i < paths.length) {
            if (paths[i] == null || paths[i].length() == 0) {
                continue;
            }
            stringTokenizer = new StringTokenizer(paths[i++], "\\/");
            while (stringTokenizer.hasMoreTokens()) {
                sb.append(pathSplit).append(stringTokenizer.nextToken());
            }
        }
        if (isPathSplitEnd) {
            sb.append(pathSplit);
        }
        return sb.toString();
    }

    /**
     * 格式化Url
     *
     * @param optionProtocol 可选协议
     * @param host           主机地址
     * @param port           端口号
     * @param path           路径
     * @return 返回格式化url字符串
     */
    static String formatUrl(String optionProtocol, String host, Integer port, String path) {
        StringBuilder url = new StringBuilder();
        boolean hasProtocol = false;
        if (optionProtocol != null) {
            int length = optionProtocol.length(), pc = 0;
            for (int i = 0; i < length; i++) {
                char c = optionProtocol.charAt(i);
                if (" :\\/&?+=%#\r\n\t\b".indexOf(c) > 0) continue;
                url.append(c);
                pc++;
            }
            if (pc <= 6) {
                url.append("://");
                hasProtocol = true;
            } else {
                url.setLength(0);
            }
        }
        if (host != null) {
            int length = host.length(), pc = 0;
            for (int i = 0; i < length; i++) {
                char c = host.charAt(i);
                if (" \\/&?+=%#\r\n\t\b".indexOf(c) > 0) continue;
                if (c == ':') {
                    if (hasProtocol) {
                        url.setLength(url.length() - pc);
                        continue;
                    }
                    //协议后面没有东西了
                    if (i + 2 >= length) {
                        url.setLength(0);
                        break;
                    }
                    //不符合协议字符串
                    if (host.charAt(i + 1) != '/' || host.charAt(i + 2) != '/') {
                        url.setLength(0);
                        break;
                    }
                    i += 2;
                    //非协议部分
                    if (pc > 6) {
                        url.setLength(0);
                        break;
                    }
                    url.append("://");
                    pc = 0;
                    hasProtocol = true;
                    continue;
                }
                url.append(c);
                pc++;
            }
        }
        if (port != null && port != 80) {
            url.append(":").append(port);
        }
        if (path != null) {
            url.append("/");
            boolean lastSlash = true, hasParameter = false;
            int length = path.length(), pc = 0;
            for (int i = 0; i < length; i++, pc++) {
                char c = path.charAt(i);
                if (" \r\n\t\b".indexOf(c) > 0) continue;
                if (hasParameter && "\\/?".indexOf(c) > 0) continue;
                if (c == '\\') c = '/';
                if (c == '/') {
                    if (!lastSlash) {
                        url.append('/');
                        lastSlash = true;
                    }
                    continue;
                }
                if (c == '?') {
                    if (hasParameter) {
                        throw new IllegalArgumentException("The path part already include ?,not allowed include ? again");
                    }
                    url.append('?');
                    hasParameter = true;
                    continue;
                }
                url.append(c);
            }
        }
        return url.toString();
    }

    /**
     * 清除一个或多个Map集合内的元素，每个Map调用clear()方法
     *
     * @param maps 一个或多个Map
     */
    static void clear(Map<?, ?>... maps) {
        if (null != maps) {
            for (Map<?, ?> map : maps) {
                if (map != null) {
                    map.clear();
                }
            }
        }
    }

    /**
     * 清除一个或多个集合内的元素，每个集合调用clear()方法
     *
     * @param collections 一个或多个集合
     */
    static void clear(Collection<?>... collections) {
        if (null != collections) {
            for (Collection<?> collection : collections) {
                if (Whether.noEmpty(collection)) {
                    collection.clear();
                }
            }
        }
    }

    /**
     * 清空指定{@link Iterator}，此方法遍历后调用{@link Iterator#remove()}移除每个元素
     *
     * @param iterators {@link Iterator}
     */
    static void clear(Iterator<?>... iterators) {
        if (null != iterators) {
            for (Iterator<?> iterator : iterators) {
                while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 清理字符串构建器
     *
     * @param stringBuilder {@code StringBuilder}
     * @return 返回清理之前的字符串
     */
    static String clear(StringBuilder stringBuilder) {
        if (stringBuilder != null) {
            String result = stringBuilder.toString();
            stringBuilder.setLength(0);
            return result;
        }
        return PoolOfString.EMPTY;
    }

    /**
     * 清理字符串构建器
     *
     * @param stringBuffer {@code StringBuffer}
     * @return 返回清理之前的字符串
     */
    static String clear(StringBuffer stringBuffer) {
        if (stringBuffer != null) {
            String result = stringBuffer.toString();
            stringBuffer.setLength(0);
            return result;
        }
        return PoolOfString.EMPTY;
    }

    //------------------------------------------------------------------------------------------------------------------
    //命名规则封装，主要是针对驼峰风格命名、连接符命名等的封装
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 转换首字符大小写
     *
     * @param input       字符序列
     * @param isUpperHead 如果为true则首字母大写，否则小写
     * @return {@code CharSequence}
     */
    @Nonnull
    static String capitalize(CharSequence input, boolean isUpperHead) {
        int length = input.length();
        if (length == 0) {
            return "";
        }
        String str = input.toString();
        if (length == 1) {
            return isUpperHead ? str.toLowerCase() : str.toUpperCase();
        }
        String h = str.substring(0, 1);
        return (!isUpperHead ? h.toLowerCase() : h.toUpperCase()) + str.substring(1);
    }

    /**
     * 驼峰命名
     *
     * @param input       待处理字符串
     * @param isUpperHead 如果为true则首字母大写，否则小写
     * @return 驼峰式的字符串
     */
    @Nonnull
    static String toCamelCase(CharSequence input, boolean isUpperHead) {
        return capitalize(formatNameByDelimiter(input, "", false, true), isUpperHead);
    }

    /**
     * 短横杠命名
     *
     * @param input   待处理字符串
     * @param isUpper 是否大写
     * @return 短横杠式的字符串
     */
    @Nonnull
    static String toKebabCase(CharSequence input, boolean isUpper) {
        return formatNameByDelimiter(input, "-", isUpper, isUpper);
    }

    /**
     * 下划线命名
     *
     * @param input   待处理字符串
     * @param isUpper 是否大写
     * @return 下划线式的字符串
     */
    @Nonnull
    static String toUnderlineCase(CharSequence input, boolean isUpper) {
        return formatNameByDelimiter(input, "_", isUpper, isUpper);
    }

    /**
     * 合并关键单词序列
     *
     * @param input       字符序列
     * @param delimiter   分割符
     * @param isUpper     是否大写
     * @param isUpperHead 如果为true则首字母大写，否则小写
     * @return 返回格式化后名称字符串
     */
    @Nonnull
    static String formatNameByDelimiter(@Nonnull CharSequence input,
                                        @Nonnull String delimiter,
                                        boolean isUpper,
                                        boolean isUpperHead) {
        List<String> names = findNameWord(input, false, isUpper);
        TextBuilder sb = TextBuilder.of(input.length() + 10);
        for (String name : names) {
            sb.appendIfPresent(delimiter);
            name = isUpper ? name.toUpperCase() : name.toLowerCase();
            if (isUpper != isUpperHead) {
                name = capitalize(name, isUpperHead);
            }
            sb.append(name);
        }
        return sb.toString();
    }

    /**
     * 实现找单词或单词数字形式的字符串
     *
     * <p>注意：
     * 1.如果非ascii会抛出异常
     * 2.它会将非Java关键字的字符当做分割符
     * 3.多个分割符只算一个分割符
     * 4.Java关键字字符 {@code _}当做分割符
     * 5.Java关键字字符 {@code $}当做前缀符
     * 6.单词与单词之间通过单词首字符大写来分割
     * 7.全大写专有名词与单词之间通过单词首字符大写来分割，并且全大写专有名词不会处理大小写
     * 8.当 {@code isSplitNumber}为 {@code true}时数字跟在字符或 {@code $}后，如果数字前是分割符，则单独在一起
     *
     * @param input         单词序列字符串
     * @param isSplitNumber 是否需要分割数字
     * @param isUpper       单词是否大写
     * @return 单词或单词数字列表
     * @throws IllegalArgumentException 当输入字符串为 {@code null}，或者字符串里包含非Ascii吗则抛出该异常
     */
    static List<String> findNameWord(
            @Nonnull CharSequence input, boolean isSplitNumber, boolean isUpper) {
        final int len = input.length();
        final List<String> results = new LinkedList<>();
        final CharStack stack = new CharStack();

        //如果值栈不为空的话，添加关键字到容器中，并清空值栈
        IntConsumer addKeyWord = countUpperLetter -> {
            if (!stack.isEmpty()) {
                String name = stack.clear();
                if (countUpperLetter <= 1) {
                    name = isUpper ? name.toUpperCase() : name.toLowerCase();
                }
                results.add(name);
            }
        };

        // 当不分割数字时一直为0
        int countNumber = 0, countUpperLetter = 0, countDollar = 0;
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (AsciiTableMatcher.isNoAsciiChar(c)) {
                throw new IllegalArgumentException("Non-Ascii codes are not allowed");
            }
            if (!AsciiTableMatcher.isMatcherExceptAsciiChar(c, AsciiTableMatcher.JAVA_KEYWORD_CHAR)) {
                addKeyWord.accept(countUpperLetter);
                continue;
            }
            // 遇到了需要分割的数字
            if (Character.isDigit(c)) {
                // 是否要分割数字
                if (isSplitNumber) {
                    if (countNumber == 0) {
                        addKeyWord.accept(countUpperLetter);
                    }
                    countNumber++;
                }
                stack.push(c);
            } else if (Character.isLetter(c) || c == '$') {
                if (countNumber > 0) {
                    addKeyWord.accept(countUpperLetter);
                    countNumber = 0;
                }
                if (c == '$') {
                    // 只有在第一个$时分割
                    if (countDollar == 0) {
                        addKeyWord.accept(countUpperLetter);
                        countUpperLetter = 0;
                    }
                    countDollar++;
                    stack.push(c);
                    continue;
                }
                if (stack.isEmpty()) {
                    stack.push(c);
                    countUpperLetter += (Whether.letterUpperChar(c) ? 1 : 0);
                } else {
                    char top = stack.peek();
                    // 前面是数字需要分割
                    if (Character.isDigit(top)) {
                        addKeyWord.accept(countUpperLetter);
                    }
                    if (AsciiTableMatcher.isMatcherExceptAsciiChar(c, AsciiTableMatcher.UPPER_LETTER)) {
                        if (countUpperLetter == 0 && countDollar == 0) {
                            addKeyWord.accept(countUpperLetter);
                        }
                        countUpperLetter += (Whether.letterUpperChar(c) ? 1 : 0);
                    } else {
                        // 只会出现{@code '$'} {@code '大写字符'}的情况，并且是 {@code '$'} {@code '大写字符'}是多个
                        if (countUpperLetter > 1) {
                            top = stack.pop();
                            if (countDollar == 0 || stack.peek() != '$') {
                                addKeyWord.accept(countUpperLetter);
                                stack.push(top);
                            } else {
                                stack.push(top);
                                addKeyWord.accept(countUpperLetter);
                            }
                        }
                        countDollar = 0;
                        countUpperLetter = 0;
                    }
                    stack.push(c);
                }
            } else {
                addKeyWord.accept(countUpperLetter);
            }
        }
        addKeyWord.accept(countUpperLetter);
        return results;
    }

    /**
     * 替换占位符
     *
     * @param template 模板
     * @param params   参数
     * @return 返回处理后的值
     */
    static String replacePlaceholder(String template, Map<String, String> params) {
        return replacePlaceholder(template, params::get);
    }

    /**
     * 替换占位符
     *
     * @param template      模板
     * @param paramSupplier 参数
     * @return 返回处理后的值
     */
    static String replacePlaceholder(String template, Function<String, String> paramSupplier) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PoolOfPattern.PLACEHOLDER.matcher(template);
        while (matcher.find()) {
            String ph = matcher.group();
            String name = ph.substring(2, ph.length() - 1);
            String value = paramSupplier.apply(name);
            if (value != null) {
                matcher.appendReplacement(sb, value);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
