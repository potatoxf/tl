package pxf.tl.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * 基本类型转换
 *
 * <p>Boolean Character Byte Short Integer Float Double String BigDecimal BigInteger Date 类型互转
 *
 * <p>boolean char byte short int long float double 基本类型和对应包装类型数组互转
 *
 * <p>String 转换成 Boolean Character Byte Short Integer Float Double String BigDecimal BigInteger Date
 *
 * <p>Boolean Character Byte Short Integer Float Double String BigDecimal BigInteger Date 转成对应数组
 *
 * <p>String 转换成 Boolean[] Character[] Byte[] Short[] Integer[] Float[] Double[] String[]
 * BigDecimal[] BigInteger[] Date[]
 *
 * @author potatoxf
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class BasicTypeConverter {
    /**
     * 基本类型关系映射
     */
    public static final Map<Class, Class> BASIC_TYPE =
            Map.ofEntries(
                    Map.entry(boolean.class, Boolean.class),
                    Map.entry(char.class, Character.class),
                    Map.entry(byte.class, Byte.class),
                    Map.entry(short.class, Short.class),
                    Map.entry(int.class, Integer.class),
                    Map.entry(long.class, Long.class),
                    Map.entry(float.class, Float.class),
                    Map.entry(double.class, Double.class),
                    Map.entry(Boolean.class, boolean.class),
                    Map.entry(Character.class, char.class),
                    Map.entry(Byte.class, byte.class),
                    Map.entry(Short.class, short.class),
                    Map.entry(Integer.class, int.class),
                    Map.entry(Long.class, long.class),
                    Map.entry(Float.class, float.class),
                    Map.entry(Double.class, double.class),
                    Map.entry(boolean[].class, Boolean[].class),
                    Map.entry(char[].class, Character[].class),
                    Map.entry(byte[].class, Byte[].class),
                    Map.entry(short[].class, Short[].class),
                    Map.entry(int[].class, Integer[].class),
                    Map.entry(long[].class, Long[].class),
                    Map.entry(float[].class, Float[].class),
                    Map.entry(double[].class, Double[].class),
                    Map.entry(Boolean[].class, boolean[].class),
                    Map.entry(Character[].class, char[].class),
                    Map.entry(Byte[].class, byte[].class),
                    Map.entry(Short[].class, short[].class),
                    Map.entry(Integer[].class, int[].class),
                    Map.entry(Long[].class, long[].class),
                    Map.entry(Float[].class, float[].class),
                    Map.entry(Double[].class, double[].class));
    public static final Map<Class, Class> TYPE_CONVERT_MAP =
            Map.ofEntries(
                    Map.entry(byte.class, Number.class),
                    Map.entry(short.class, Number.class),
                    Map.entry(int.class, Number.class),
                    Map.entry(long.class, Number.class),
                    Map.entry(float.class, Number.class),
                    Map.entry(double.class, Number.class),
                    Map.entry(Byte.class, Number.class),
                    Map.entry(Short.class, Number.class),
                    Map.entry(Integer.class, Number.class),
                    Map.entry(Long.class, Number.class),
                    Map.entry(Float.class, Number.class),
                    Map.entry(Double.class, Number.class),
                    Map.entry(java.sql.Date.class, Date.class),
                    Map.entry(Time.class, Date.class),
                    Map.entry(Timestamp.class, Date.class),
                    Map.entry(Instant.class, TemporalAccessor.class),
                    Map.entry(YearMonth.class, TemporalAccessor.class),
                    Map.entry(Year.class, TemporalAccessor.class),
                    Map.entry(LocalDate.class, TemporalAccessor.class),
                    Map.entry(LocalTime.class, TemporalAccessor.class),
                    Map.entry(LocalDateTime.class, TemporalAccessor.class),
                    Map.entry(OffsetTime.class, TemporalAccessor.class),
                    Map.entry(OffsetDateTime.class, TemporalAccessor.class),
                    Map.entry(ZonedDateTime.class, TemporalAccessor.class),
                    Map.entry(ZoneOffset.class, TemporalAccessor.class),
                    Map.entry(boolean[].class, Array.class),
                    Map.entry(char[].class, Array.class),
                    Map.entry(byte[].class, Array.class),
                    Map.entry(short[].class, Array.class),
                    Map.entry(int[].class, Array.class),
                    Map.entry(long[].class, Array.class),
                    Map.entry(float[].class, Array.class),
                    Map.entry(double[].class, Array.class),
                    Map.entry(Boolean[].class, Array.class),
                    Map.entry(Character[].class, Array.class),
                    Map.entry(Byte[].class, Array.class),
                    Map.entry(Short[].class, Array.class),
                    Map.entry(Integer[].class, Array.class),
                    Map.entry(Long[].class, Array.class),
                    Map.entry(Float[].class, Array.class),
                    Map.entry(Double[].class, Array.class));
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTypeConverter.class);
    private static final Class<Class> DEFAULT_KEY = Class.class;
    private static final Map<Class, Map<Class, Function>> CACHE = new ConcurrentHashMap<>();
    public static final Function<Object, String> ANY_TO_STRING =
            register(
                    null,
                    String.class,
                    o -> {
                        try {
                            return o == null ? "" : o.toString();
                        } catch (Throwable e) {
                            ToolLog.warn(LOGGER, e, () -> "Error to toString");
                        }
                        return "";
                    });

    public static final Function<Object, String> ARRAY_TO_STRING =
            register(
                    Array.class,
                    String.class,
                    o -> {
                        if (o == null) {
                            return "[]";
                        }
                        String result;
                        switch (o) {
                            case int[] a -> result = Arrays.toString(a);
                            case long[] a -> result = Arrays.toString(a);
                            case double[] a -> result = Arrays.toString(a);
                            case float[] a -> result = Arrays.toString(a);
                            case byte[] a -> result = Arrays.toString(a);
                            case short[] a -> result = Arrays.toString(a);
                            case char[] a -> result = Arrays.toString(a);
                            default -> result = Arrays.toString((Object[]) o);
                        }
                        return result;
                    });

    // ------------------------------------------------------------------------------
    // 基本类型数组互转
    // ------------------------------------------------------------------------------

    public static final Function<boolean[], Boolean[]> BOOLEANS_TO_PACK =
            register(boolean[].class, Boolean[].class, a -> switchBasicTypeArray(a, boolean[].class));

    public static final Function<char[], Character[]> CHARACTERS_TO_PACK =
            register(char[].class, Character[].class, a -> switchBasicTypeArray(a, char[].class));

    public static final Function<byte[], Byte[]> BYTES_TO_PACK =
            register(byte[].class, Byte[].class, a -> switchBasicTypeArray(a, byte[].class));

    public static final Function<short[], Short[]> SHORTS_TO_PACK =
            register(short[].class, Short[].class, a -> switchBasicTypeArray(a, short[].class));

    public static final Function<int[], Integer[]> INTEGERS_TO_PACK =
            register(int[].class, Integer[].class, a -> switchBasicTypeArray(a, int[].class));

    public static final Function<long[], Long[]> LONGS_TO_PACK =
            register(long[].class, Long[].class, a -> switchBasicTypeArray(a, long[].class));

    public static final Function<float[], Float[]> FLOATS_TO_PACK =
            register(float[].class, Float[].class, a -> switchBasicTypeArray(a, float[].class));

    public static final Function<double[], Double[]> DOUBLES_TO_PACK =
            register(double[].class, Double[].class, a -> switchBasicTypeArray(a, double[].class));

    public static final Function<Boolean[], boolean[]> BOOLEANS_TO_UNPACK =
            register(Boolean[].class, boolean[].class, a -> switchBasicTypeArray(a, Boolean[].class));

    public static final Function<Character[], char[]> CHARACTERS_TO_UNPACK =
            register(Character[].class, char[].class, a -> switchBasicTypeArray(a, Character[].class));

    public static final Function<Byte[], byte[]> BYTES_TO_UNPACK =
            register(Byte[].class, byte[].class, a -> switchBasicTypeArray(a, Byte[].class));

    public static final Function<Short[], short[]> SHORTS_TO_UNPACK =
            register(Short[].class, short[].class, a -> switchBasicTypeArray(a, Short[].class));

    public static final Function<Integer[], int[]> INTEGERS_TO_UNPACK =
            register(Integer[].class, int[].class, a -> switchBasicTypeArray(a, Integer[].class));

    public static final Function<Long[], long[]> LONGS_TO_UNPACK =
            register(Long[].class, long[].class, a -> switchBasicTypeArray(a, Long[].class));

    public static final Function<Float[], float[]> FLOATS_TO_UNPACK =
            register(Float[].class, float[].class, a -> switchBasicTypeArray(a, Float[].class));

    public static final Function<Double[], double[]> DOUBLES_TO_UNPACK =
            register(Double[].class, double[].class, a -> switchBasicTypeArray(a, Double[].class));

    public static final Function<Object, Boolean[]> TO_BOOLEANS_PACK =
            register(Array.class, Boolean[].class, a -> toBasicTypeArray(a, Boolean[].class));

    public static final Function<Object, Character[]> TO_CHARACTERS_PACK =
            register(Array.class, Character[].class, a -> toBasicTypeArray(a, Character[].class));

    public static final Function<Object, Byte[]> TO_BYTES_PACK =
            register(Array.class, Byte[].class, a -> toBasicTypeArray(a, Byte[].class));

    public static final Function<Object, Short[]> TO_SHORTS_PACK =
            register(Array.class, Short[].class, a -> toBasicTypeArray(a, Short[].class));

    public static final Function<Object, Integer[]> TO_INTEGERS_PACK =
            register(Array.class, Integer[].class, a -> toBasicTypeArray(a, Integer[].class));

    public static final Function<Object, Long[]> TO_LONGS_PACK =
            register(Array.class, Long[].class, a -> toBasicTypeArray(a, Long[].class));

    public static final Function<Object, Float[]> TO_FLOATS_PACK =
            register(Array.class, Float[].class, a -> toBasicTypeArray(a, Float[].class));

    public static final Function<Object, Double[]> TO_DOUBLES_PACK =
            register(Array.class, Double[].class, a -> toBasicTypeArray(a, Double[].class));

    public static final Function<Object, boolean[]> TO_BOOLEAN_UNPACK =
            register(Array.class, boolean[].class, a -> toBasicTypeArray(a, boolean[].class));

    public static final Function<Object, char[]> TO_CHARACTER_UNPACK =
            register(Array.class, char[].class, a -> toBasicTypeArray(a, char[].class));

    public static final Function<Object, byte[]> TO_BYTE_UNPACK =
            register(Array.class, byte[].class, a -> toBasicTypeArray(a, byte[].class));

    public static final Function<Object, short[]> TO_SHORT_UNPACK =
            register(Array.class, short[].class, a -> toBasicTypeArray(a, short[].class));

    public static final Function<Object, int[]> TO_INTEGER_UNPACK =
            register(Array.class, int[].class, a -> toBasicTypeArray(a, int[].class));

    public static final Function<Object, long[]> TO_LONG_UNPACK =
            register(Array.class, long[].class, a -> toBasicTypeArray(a, long[].class));

    public static final Function<Object, float[]> TO_FLOAT_UNPACK =
            register(Array.class, float[].class, a -> toBasicTypeArray(a, float[].class));

    public static final Function<Object, double[]> TO_DOUBLE_UNPACK =
            register(Array.class, double[].class, a -> toBasicTypeArray(a, double[].class));

    // ------------------------------------------------------------------------------
    // 日期格式互转
    // ------------------------------------------------------------------------------

    public static final Function<Date, Instant> DATE_TO_INSTANT =
            register(Date.class, Instant.class, (Convert<Date, Instant>) Date::toInstant);

    public static final Function<Date, YearMonth> DATE_TO_YEARMONTH =
            register(
                    Date.class,
                    YearMonth.class,
                    (Convert<Date, YearMonth>)
                            i -> YearMonth.from(LocalDate.ofInstant(i.toInstant(), ZoneId.systemDefault())));

    public static final Function<Date, Year> DATE_TO_YEAR =
            register(
                    Date.class,
                    Year.class,
                    (Convert<Date, Year>)
                            i -> Year.from(LocalDate.ofInstant(i.toInstant(), ZoneId.systemDefault())));

    public static final Function<Date, ZonedDateTime> DATE_TO_ZONEDDATETIME =
            register(
                    Date.class,
                    ZonedDateTime.class,
                    (Convert<Date, ZonedDateTime>)
                            i -> ZonedDateTime.ofInstant(i.toInstant(), ZoneId.systemDefault()));

    public static final Function<Date, LocalDate> DATE_TO_LOCALDATE =
            register(
                    Date.class,
                    LocalDate.class,
                    (Convert<Date, LocalDate>)
                            i -> LocalDate.ofInstant(i.toInstant(), ZoneId.systemDefault()));

    public static final Function<Date, LocalTime> DATE_TO_LOCALTIME =
            register(
                    Date.class,
                    LocalTime.class,
                    (Convert<Date, LocalTime>)
                            i -> LocalTime.ofInstant(i.toInstant(), ZoneId.systemDefault()));

    public static final Function<Date, LocalDateTime> DATE_TO_LOCALDATETIME =
            register(
                    Date.class,
                    LocalDateTime.class,
                    (Convert<Date, LocalDateTime>)
                            i -> LocalDateTime.ofInstant(i.toInstant(), ZoneId.systemDefault()));

    public static final Function<Date, OffsetTime> DATE_TO_OFFSETTIME =
            register(
                    Date.class,
                    OffsetTime.class,
                    (Convert<Date, OffsetTime>)
                            i -> OffsetTime.ofInstant(i.toInstant(), ZoneId.systemDefault()));

    public static final Function<Date, OffsetDateTime> DATE_TO_OFFSETDATETIME =
            register(
                    Date.class,
                    OffsetDateTime.class,
                    (Convert<Date, OffsetDateTime>)
                            i -> OffsetDateTime.ofInstant(i.toInstant(), ZoneId.systemDefault()));


    public static final Function<TemporalAccessor, LocalDate> TA_TO_LOCALDATE =
            register(
                    TemporalAccessor.class,
                    LocalDate.class,
                    (Convert<TemporalAccessor, LocalDate>) temporalAccessor -> switch (temporalAccessor) {
                        case LocalDate l -> l;
                        case LocalTime l -> l.atDate(LocalDate.now()).toLocalDate();
                        case LocalDateTime l -> l.toLocalDate();
                        case Instant i -> i.atZone(ZoneId.systemDefault()).toLocalDate();
                        case ZonedDateTime z -> z.toLocalDate();
                        case OffsetDateTime o -> o.toLocalDate();
                        case OffsetTime o -> o.atDate(LocalDate.now()).toLocalDate();
                        default -> LocalDate.of(
                                ToolTime.get(temporalAccessor, ChronoField.YEAR),
                                ToolTime.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
                                ToolTime.get(temporalAccessor, ChronoField.DAY_OF_MONTH));
                    });

    public static final Function<TemporalAccessor, LocalTime> TA_TO_LOCALTIME =
            register(
                    TemporalAccessor.class,
                    LocalTime.class,
                    (Convert<TemporalAccessor, LocalTime>) temporalAccessor -> switch (temporalAccessor) {
                        case LocalTime l -> l;
                        case LocalDate l -> l.atTime(LocalTime.now()).toLocalTime();
                        case LocalDateTime l -> l.toLocalTime();
                        case Instant i -> i.atZone(ZoneId.systemDefault()).toLocalTime();
                        case ZonedDateTime z -> z.toLocalTime();
                        case OffsetDateTime o -> o.toLocalTime();
                        case OffsetTime o -> o.toLocalTime();
                        default -> LocalTime.of(
                                ToolTime.get(temporalAccessor, ChronoField.HOUR_OF_DAY),
                                ToolTime.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR),
                                ToolTime.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE),
                                ToolTime.get(temporalAccessor, ChronoField.NANO_OF_SECOND));
                    });

    public static final Function<TemporalAccessor, LocalDateTime> TA_TO_LOCALDATETIME =
            register(
                    TemporalAccessor.class,
                    LocalDateTime.class,
                    (Convert<TemporalAccessor, LocalDateTime>) temporalAccessor -> switch (temporalAccessor) {
                        case LocalDateTime l -> l;
                        case LocalTime l -> l.atDate(LocalDate.now());
                        case LocalDate l -> l.atTime(LocalTime.now());
                        case Instant i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault());
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
                    });

    public static final Function<TemporalAccessor, Instant> TA_TO_INSTANT =
            register(
                    TemporalAccessor.class,
                    Instant.class,
                    (Convert<TemporalAccessor, Instant>) temporalAccessor ->
                            TA_TO_LOCALDATETIME.apply(temporalAccessor).atZone(ZoneId.systemDefault()).toInstant());

    public static final Function<TemporalAccessor, Date> TA_TO_DATE =
            register(
                    TemporalAccessor.class,
                    Date.class,
                    (Convert<TemporalAccessor, Date>) i -> Date.from(TA_TO_INSTANT.apply(i)));

    public static final Function<TemporalAccessor, YearMonth> TA_TO_YEARMONTH =
            register(
                    TemporalAccessor.class,
                    YearMonth.class,
                    (Convert<TemporalAccessor, YearMonth>) i -> YearMonth.from(LocalDate.from(i)));

    public static final Function<TemporalAccessor, Year> TA_TO_YEAR =
            register(
                    TemporalAccessor.class,
                    Year.class,
                    (Convert<TemporalAccessor, Year>) i -> Year.from(LocalDate.from(i)));

    public static final Function<TemporalAccessor, ZonedDateTime> TA_TO_ZONEDDATETIME =
            register(
                    TemporalAccessor.class,
                    ZonedDateTime.class,
                    (Convert<TemporalAccessor, ZonedDateTime>) ZonedDateTime::from);

    public static final Function<TemporalAccessor, OffsetTime> TA_TO_OFFSETTIME =
            register(
                    TemporalAccessor.class,
                    OffsetTime.class,
                    (Convert<TemporalAccessor, OffsetTime>) OffsetTime::from);

    public static final Function<TemporalAccessor, OffsetDateTime> TA_TO_OFFSETDATETIME =
            register(
                    TemporalAccessor.class,
                    OffsetDateTime.class,
                    (Convert<TemporalAccessor, OffsetDateTime>) OffsetDateTime::from);

    // ------------------------------------------------------------------------------
    // 布尔转数字和字符类型
    // ------------------------------------------------------------------------------

    public static final Function<Boolean, Character> BOOLEAN_TO_CHARACTER =
            register(
                    Boolean.class,
                    Character.class,
                    (Convert<Boolean, Character>) b -> (char) (b ? 'T' : 'F'));

    public static final Function<Boolean, Byte> BOOLEAN_TO_BYTE =
            register(Boolean.class, Byte.class, (Convert<Boolean, Byte>) b -> (byte) (b ? 1 : 0));

    public static final Function<Boolean, Short> BOOLEAN_TO_SHORT =
            register(Boolean.class, Short.class, (Convert<Boolean, Short>) b -> (short) (b ? 1 : 0));

    public static final Function<Boolean, Integer> BOOLEAN_TO_INTEGER =
            register(Boolean.class, Integer.class, (Convert<Boolean, Integer>) b -> b ? 1 : 0);

    public static final Function<Boolean, Long> BOOLEAN_TO_LONG =
            register(Boolean.class, Long.class, (Convert<Boolean, Long>) b -> b ? 1L : 0L);

    public static final Function<Boolean, Float> BOOLEAN_TO_FLOAT =
            register(Boolean.class, Float.class, (Convert<Boolean, Float>) b -> b ? 1f : 0f);

    public static final Function<Boolean, Double> BOOLEAN_TO_DOUBLE =
            register(Boolean.class, Double.class, (Convert<Boolean, Double>) b -> b ? 1d : 0d);

    public static final Function<Boolean, AtomicBoolean> BOOLEAN_TO_ATOMICBOOLEAN =
            register(Boolean.class, AtomicBoolean.class, (Convert<Boolean, AtomicBoolean>) AtomicBoolean::new);

    // ------------------------------------------------------------------------------
    // 字符转数字和布尔类型
    // ------------------------------------------------------------------------------

    public static final Function<Character, Boolean> CHARACTER_TO_BOOLEAN =
            register(Character.class, Boolean.class, new Convert<Character, Boolean>() {
                static final Set<Character> TRUE_SET =
                        Set.of('y', 't', '1', '是', '对', '真', '對', '√');
                static final Set<Character> FALSE_SET =
                        Set.of('n', 'f', '0', '否', '错', '假', '錯', '×');

                @Override
                public Boolean doApply(Character character) throws Throwable {
                    if (TRUE_SET.contains(character)) {
                        return true;
                    }
                    if (FALSE_SET.contains(character)) {
                        return true;
                    }
                    return null;
                }
            });

    public static final Function<Character, Byte> CHARACTER_TO_BYTE =
            register(Character.class, Byte.class, (Convert<Character, Byte>) c -> (byte) c.charValue());

    public static final Function<Character, Short> CHARACTER_TO_SHORT =
            register(
                    Character.class, Short.class, (Convert<Character, Short>) c -> (short) c.charValue());

    public static final Function<Character, Integer> CHARACTER_TO_INTEGER =
            register(Character.class, Integer.class, (Convert<Character, Integer>) c -> (int) c);

    public static final Function<Character, Long> CHARACTER_TO_LONG =
            register(Character.class, Long.class, (Convert<Character, Long>) c -> (long) c);

    public static final Function<Character, Float> CHARACTER_TO_FLOAT =
            register(Character.class, Float.class, (Convert<Character, Float>) c -> (float) c);

    public static final Function<Character, Double> CHARACTER_TO_DOUBLE =
            register(Character.class, Double.class, (Convert<Character, Double>) c -> (double) c);

    public static final Function<Character, AtomicBoolean> CHARACTER_TO_ATOMICBOOLEAN =
            register(Character.class, AtomicBoolean.class, (Convert<Character, AtomicBoolean>) c -> new AtomicBoolean(c == 'T'));

    // ------------------------------------------------------------------------------
    // 数字转基本类型
    // ------------------------------------------------------------------------------

    public static final Function<Number, Boolean> NUMBER_TO_BOOLEAN =
            register(Number.class, Boolean.class, (Convert<Number, Boolean>) n -> n.intValue() != 0);

    public static final Function<Number, Boolean[]> NUMBER_TO_BOOLEAN_ARRAY =
            register(Number.class, Boolean[].class, n -> toArray(n, NUMBER_TO_BOOLEAN, Boolean[]::new));

    public static final Function<Number, Character> NUMBER_TO_CHARACTER =
            register(
                    Number.class,
                    Character.class,
                    (Convert<Number, Character>) n -> Character.toChars(n.intValue())[0]);

    public static final Function<Number, Character[]> NUMBER_TO_CHARACTER_ARRAY =
            register(
                    Number.class, Character[].class, n -> toArray(n, NUMBER_TO_CHARACTER, Character[]::new));

    public static final Function<Number, Byte> NUMBER_TO_BYTE =
            register(Number.class, Byte.class, (Convert<Number, Byte>) Number::byteValue);

    public static final Function<String, Byte> STRING_TO_BYTE =
            register(
                    String.class,
                    Byte.class,
                    (Convert<String, Byte>) s -> NUMBER_TO_BYTE.apply(new BigDecimal(s)));
    public static final Function<String, Byte[]> STRING_TO_BYTE_ARRAY =
            register(String.class, Byte[].class, s -> toArray(s, STRING_TO_BYTE, Byte[]::new));
    public static final Function<Number, Byte[]> NUMBER_TO_BYTE_ARRAY =
            register(Number.class, Byte[].class, n -> toArray(n, NUMBER_TO_BYTE, Byte[]::new));
    public static final Function<Number, Short> NUMBER_TO_SHORT =
            register(Number.class, Short.class, (Convert<Number, Short>) Number::shortValue);
    public static final Function<String, Short> STRING_TO_SHORT =
            register(
                    String.class,
                    Short.class,
                    (Convert<String, Short>) s -> NUMBER_TO_SHORT.apply(new BigDecimal(s)));
    public static final Function<String, Short[]> STRING_TO_SHORT_ARRAY =
            register(String.class, Short[].class, s -> toArray(s, STRING_TO_SHORT, Short[]::new));
    public static final Function<Number, Short[]> NUMBER_TO_SHORT_ARRAY =
            register(Number.class, Short[].class, n -> toArray(n, NUMBER_TO_SHORT, Short[]::new));
    public static final Function<Number, Integer> NUMBER_TO_INTEGER =
            register(Number.class, Integer.class, (Convert<Number, Integer>) Number::intValue);
    public static final Function<String, Integer> STRING_TO_INTEGER =
            register(
                    String.class,
                    Integer.class,
                    (Convert<String, Integer>) s -> NUMBER_TO_INTEGER.apply(new BigDecimal(s)));
    public static final Function<String, Integer[]> STRING_TO_INTEGER_ARRAY =
            register(String.class, Integer[].class, s -> toArray(s, STRING_TO_INTEGER, Integer[]::new));
    public static final Function<Number, Integer[]> NUMBER_TO_INTEGER_ARRAY =
            register(Number.class, Integer[].class, n -> toArray(n, NUMBER_TO_INTEGER, Integer[]::new));
    public static final Function<Number, Long> NUMBER_TO_LONG =
            register(Number.class, Long.class, (Convert<Number, Long>) Number::longValue);
    public static final Function<String, Long> STRING_TO_LONG =
            register(
                    String.class,
                    Long.class,
                    (Convert<String, Long>) s -> NUMBER_TO_LONG.apply(new BigDecimal(s)));
    public static final Function<String, Long[]> STRING_TO_LONG_ARRAY =
            register(String.class, Long[].class, s -> toArray(s, STRING_TO_LONG, Long[]::new));
    public static final Function<Number, Long[]> NUMBER_TO_LONG_ARRAY =
            register(Number.class, Long[].class, n -> toArray(n, NUMBER_TO_LONG, Long[]::new));

    // ------------------------------------------------------------------------------
    // 字符串转基本类型
    // ------------------------------------------------------------------------------
    public static final Function<Number, Float> NUMBER_TO_FLOAT =
            register(Number.class, Float.class, (Convert<Number, Float>) Number::floatValue);
    public static final Function<String, Float> STRING_TO_FLOAT =
            register(
                    String.class,
                    Float.class,
                    (Convert<String, Float>) s -> NUMBER_TO_FLOAT.apply(new BigDecimal(s)));
    public static final Function<String, Float[]> STRING_TO_FLOAT_ARRAY =
            register(String.class, Float[].class, s -> toArray(s, STRING_TO_FLOAT, Float[]::new));
    public static final Function<Number, Float[]> NUMBER_TO_FLOAT_ARRAY =
            register(Number.class, Float[].class, n -> toArray(n, NUMBER_TO_FLOAT, Float[]::new));
    public static final Function<Number, Double> NUMBER_TO_DOUBLE =
            register(Number.class, Double.class, (Convert<Number, Double>) Number::doubleValue);
    public static final Function<String, Double> STRING_TO_DOUBLE =
            register(
                    String.class,
                    Double.class,
                    (Convert<String, Double>) s -> NUMBER_TO_DOUBLE.apply(new BigDecimal(s)));
    public static final Function<String, Double[]> STRING_TO_DOUBLE_ARRAY =
            register(String.class, Double[].class, s -> toArray(s, STRING_TO_DOUBLE, Double[]::new));
    public static final Function<Number, Double[]> NUMBER_TO_DOUBLE_ARRAY =
            register(Number.class, Double[].class, n -> toArray(n, NUMBER_TO_DOUBLE, Double[]::new));
    public static final Function<Number, BigDecimal> NUMBER_TO_BIGDECIMAL =
            register(
                    Number.class,
                    BigDecimal.class,
                    (Convert<Number, BigDecimal>) n -> BigDecimal.valueOf(n.doubleValue()));
    public static final Function<Number, BigDecimal[]> NUMBER_TO_BIGDECIMAL_ARRAY =
            register(
                    Number.class,
                    BigDecimal[].class,
                    n -> toArray(n, NUMBER_TO_BIGDECIMAL, BigDecimal[]::new));
    public static final Function<Number, BigInteger> NUMBER_TO_BIGINTEGER =
            register(
                    Number.class,
                    BigInteger.class,
                    (Convert<Number, BigInteger>) n -> BigInteger.valueOf(n.longValue()));
    public static final Function<Number, BigInteger[]> NUMBER_TO_BIGINTEGER_ARRAY =
            register(
                    Number.class,
                    BigInteger[].class,
                    n -> toArray(n, NUMBER_TO_BIGINTEGER, BigInteger[]::new));
    public static final Function<Number, Date> NUMBER_TO_DATE =
            register(Number.class, Date.class, (Convert<Number, Date>) n -> new Date(n.longValue()));
    public static final Function<Number, Instant> NUMBER_TO_INSTANT =
            register(
                    Number.class,
                    Instant.class,
                    (Convert<Number, Instant>) i -> DATE_TO_INSTANT.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, YearMonth> NUMBER_TO_YEARMONTH =
            register(
                    Number.class,
                    YearMonth.class,
                    (Convert<Number, YearMonth>) i -> DATE_TO_YEARMONTH.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, Year> NUMBER_TO_YEAR =
            register(
                    Number.class,
                    Year.class,
                    (Convert<Number, Year>) i -> DATE_TO_YEAR.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, ZonedDateTime> NUMBER_TO_ZONEDDATETIME =
            register(
                    Number.class,
                    ZonedDateTime.class,
                    (Convert<Number, ZonedDateTime>)
                            i -> DATE_TO_ZONEDDATETIME.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, LocalDate> NUMBER_TO_LOCALDATE =
            register(
                    Number.class,
                    LocalDate.class,
                    (Convert<Number, LocalDate>) i -> DATE_TO_LOCALDATE.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, LocalTime> NUMBER_TO_LOCALTIME =
            register(
                    Number.class,
                    LocalTime.class,
                    (Convert<Number, LocalTime>) i -> DATE_TO_LOCALTIME.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, LocalDateTime> NUMBER_TO_LOCALDATETIME =
            register(
                    Number.class,
                    LocalDateTime.class,
                    (Convert<Number, LocalDateTime>)
                            i -> DATE_TO_LOCALDATETIME.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, OffsetTime> NUMBER_TO_OFFSETTIME =
            register(
                    Number.class,
                    OffsetTime.class,
                    (Convert<Number, OffsetTime>) i -> DATE_TO_OFFSETTIME.apply(NUMBER_TO_DATE.apply(i)));

    // ------------------------------------------------------------------------------
    // 数字转基本类型数组
    // ------------------------------------------------------------------------------
    public static final Function<Number, OffsetDateTime> NUMBER_TO_OFFSETDATETIME =
            register(
                    Number.class,
                    OffsetDateTime.class,
                    (Convert<Number, OffsetDateTime>)
                            i -> DATE_TO_OFFSETDATETIME.apply(NUMBER_TO_DATE.apply(i)));
    public static final Function<Number, Date[]> NUMBER_TO_DATE_ARRAY =
            register(Number.class, Date[].class, n -> toArray(n, NUMBER_TO_DATE, Date[]::new));
    public static final Function<String, Class> STRING_TO_CLASS =
            register(String.class, Class.class, (Convert<String, Class>) Class::forName);
    public static final Function<String, Boolean> STRING_TO_BOOLEAN =
            register(String.class, Boolean.class, (Convert<String, Boolean>) Boolean::parseBoolean);
    public static final Function<String, Boolean[]> STRING_TO_BOOLEAN_ARRAY =
            register(String.class, Boolean[].class, s -> toArray(s, STRING_TO_BOOLEAN, Boolean[]::new));
    public static final Function<String, Character> STRING_TO_CHARACTER =
            register(String.class, Character.class, (Convert<String, Character>) s -> s.toCharArray()[0]);
    public static final Function<String, Character[]> STRING_TO_CHARACTER_ARRAY =
            register(
                    String.class, Character[].class, s -> toArray(s, STRING_TO_CHARACTER, Character[]::new));
    public static final Function<String, BigDecimal> STRING_TO_BIGDECIMAL =
            register(String.class, BigDecimal.class, (Convert<String, BigDecimal>) BigDecimal::new);
    public static final Function<String, BigDecimal[]> STRING_TO_BIGDECIMAL_ARRAY =
            register(
                    String.class,
                    BigDecimal[].class,
                    s -> toArray(s, STRING_TO_BIGDECIMAL, BigDecimal[]::new));
    public static final Function<String, BigInteger> STRING_TO_BIGINTEGER =
            register(String.class, BigInteger.class, (Convert<String, BigInteger>) BigInteger::new);
    public static final Function<String, BigInteger[]> STRING_TO_BIGINTEGER_ARRAY =
            register(
                    String.class,
                    BigInteger[].class,
                    s -> toArray(s, STRING_TO_BIGINTEGER, BigInteger[]::new));

    // ------------------------------------------------------------------------------
    // 字符串转基本类型数组
    // ------------------------------------------------------------------------------
    public static final Function<String, Date> STRING_TO_DATE =
            register(
                    String.class,
                    Date.class,
                    (Convert<String, Date>)
                            s -> {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                                try {
                                    simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                                    return simpleDateFormat.parse(s);
                                } catch (ParseException ignored) {
                                }
                                try {
                                    simpleDateFormat.applyPattern("yyyy-MM-dd");
                                    return simpleDateFormat.parse(s);
                                } catch (ParseException ignored) {
                                }
                                if (LOGGER.isWarnEnabled()) {
                                    LOGGER.warn(
                                            "Error try to parse string to date by [yyyy-MM-dd HH:mm:ss] and [yyyy-MM-dd]");
                                }
                                return null;
                            });
    public static final Function<String, Instant> STRING_TO_INSTANT =
            register(
                    String.class,
                    Instant.class,
                    (Convert<String, Instant>) i -> DATE_TO_INSTANT.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, YearMonth> STRING_TO_YEARMONTH =
            register(
                    String.class,
                    YearMonth.class,
                    (Convert<String, YearMonth>) i -> DATE_TO_YEARMONTH.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, Year> STRING_TO_YEAR =
            register(
                    String.class,
                    Year.class,
                    (Convert<String, Year>) i -> DATE_TO_YEAR.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, ZonedDateTime> STRING_TO_ZONEDDATETIME =
            register(
                    String.class,
                    ZonedDateTime.class,
                    (Convert<String, ZonedDateTime>)
                            i -> DATE_TO_ZONEDDATETIME.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, LocalDate> STRING_TO_LOCALDATE =
            register(
                    String.class,
                    LocalDate.class,
                    (Convert<String, LocalDate>) i -> DATE_TO_LOCALDATE.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, LocalTime> STRING_TO_LOCALTIME =
            register(
                    String.class,
                    LocalTime.class,
                    (Convert<String, LocalTime>) i -> DATE_TO_LOCALTIME.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, LocalDateTime> STRING_TO_LOCALDATETIME =
            register(
                    String.class,
                    LocalDateTime.class,
                    (Convert<String, LocalDateTime>)
                            i -> DATE_TO_LOCALDATETIME.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, OffsetTime> STRING_TO_OFFSETTIME =
            register(
                    String.class,
                    OffsetTime.class,
                    (Convert<String, OffsetTime>) i -> DATE_TO_OFFSETTIME.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, OffsetDateTime> STRING_TO_OFFSETDATETIME =
            register(
                    String.class,
                    OffsetDateTime.class,
                    (Convert<String, OffsetDateTime>)
                            i -> DATE_TO_OFFSETDATETIME.apply(STRING_TO_DATE.apply(i)));
    public static final Function<String, Date[]> STRING_TO_DATE_ARRAY =
            register(
                    String.class,
                    Date[].class,
                    s -> {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                        String[] array = toStringArray(s);
                        List<Date> results = new ArrayList<>(array.length + 1);
                        for (int i = 0; i < array.length; i++) {
                            String string = array[i];
                            try {
                                simpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
                                results.add(simpleDateFormat.parse(string));
                                continue;
                            } catch (ParseException ignored) {
                            }
                            try {
                                simpleDateFormat.applyPattern("yyyy-MM-dd");
                                results.add(simpleDateFormat.parse(string));
                                continue;
                            } catch (ParseException ignored) {
                            }
                            if (LOGGER.isWarnEnabled()) {
                                LOGGER.warn(
                                        "Error try to parse string["
                                                + i
                                                + "] to date by [yyyy-MM-dd HH:mm:ss] and [yyyy-MM-dd]");
                            }
                        }
                        return results.toArray(Date[]::new);
                    });

    public static <T> T convert(@Nullable Object input, @Nonnull Class<T> outputClass) {
        return convert(input, outputClass, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(
            @Nullable final Object input, @Nonnull final Class<T> outputClass, T defaultValue) {
        Object result = null;
        if (input != null) {
            final Class inputClass = input.getClass();
            Class ic = inputClass;
            Class oc = outputClass;
            if (oc == ic) {
                result = input;
            } else {
                boolean inputArray = inputClass.isArray();
                boolean outputArray = outputClass.isArray();
                boolean isConvertInputType = true;
                if (inputArray && outputArray) {
                    if (BASIC_TYPE.get(ic) == oc) {
                        isConvertInputType = false;
                    }
                } else if (outputArray) {
                    oc = outputClass.getComponentType().isPrimitive() ? BASIC_TYPE.get(oc) : oc;
                } else if (inputArray) {

                } else {
                    oc = oc.isPrimitive() ? BASIC_TYPE.get(oc) : oc;
                }
                Map<Class, Function> classFunctionMap = CACHE.get(oc);
                if (classFunctionMap == null) {
                    throw new IllegalArgumentException("不支持类型");
                }
                if (isConvertInputType && TYPE_CONVERT_MAP.containsKey(ic)) {
                    ic = TYPE_CONVERT_MAP.get(ic);
                }
                Function function = classFunctionMap.get(ic);
                if (function == null) {
                    if (classFunctionMap.containsKey(DEFAULT_KEY)) {
                        function = classFunctionMap.get(DEFAULT_KEY);
                    } else {
                        throw new IllegalArgumentException("不支持类型");
                    }
                }
                result = function.apply(input);
                if (result == null) {
                    result = defaultValue;
                } else {
                    if (result.getClass() != outputClass
                            && outputClass.isArray()
                            && outputClass.getComponentType().isPrimitive()) {
                        result = convert(result, outputClass, defaultValue);
                    }
                }
            }
        }
        return (T) result;
    }

    @Nonnull
    private static <T, R> Function<T, R> register(
            @Nullable Class<? extends T> fromClass,
            @Nonnull Class<R> toClass,
            @Nonnull Function<T, R> convertFunction) {
        Map<Class, Function> classFunctionMap =
                CACHE.computeIfAbsent(toClass, i -> new ConcurrentHashMap<>());

        classFunctionMap.put(fromClass == null ? DEFAULT_KEY : fromClass, convertFunction);
        return convertFunction;
    }

    private static <R> R[] toArray(
            String s, Function<String, R> function, IntFunction<R[]> generator) {
        return Arrays.stream(toStringArray(s)).map(function).toArray(generator);
    }

    private static <I, R> R[] toArray(I value, Function<I, R> function, IntFunction<R[]> generator) {
        return Stream.ofNullable(value).map(function).filter(Objects::nonNull).toArray(generator);
    }

    private static <I, R> R switchBasicTypeArray(I inputArray, Class<I> inputClass) {
        return (R) toArray(inputArray, BASIC_TYPE.get(inputClass), BASIC_TYPE.get(inputClass.getComponentType()), (data, clz) -> data);
    }

    private static <I, R> R toBasicTypeArray(Object inputArray, Class<R> outputClass) {
        return (R) toArray(inputArray, outputClass, null, BasicTypeConverter::convert);
    }

    private static <I, R> R toArray(
            Object inputArray,
            Class<R> outputArrayType,
            Class outputComponentType,
            BiFunction<Object, Class, Object> converterFunction) {
        if (outputComponentType == null) {
            outputComponentType = outputArrayType.getComponentType();
        }
        Object result;
        if (inputArray == null) {
            result = Array.newInstance(outputComponentType, 0);
        } else {
            Class<?> inputClass = inputArray.getClass();
            if (inputClass.isArray()) {
                int length = Array.getLength(inputArray);
                int newLength = 0;
                for (int i = 0; i < length; i++) {
                    Object data = Array.get(inputArray, i);
                    if (data != null) {
                        newLength++;
                    }
                }
                List list = new ArrayList(length);
                for (int i = 0; i < length; i++) {
                    Object data = Array.get(inputArray, i);
                    if (data != null) {
                        data = converterFunction.apply(data, outputComponentType);
                        if (data != null) {
                            list.add(data);
                        }
                    }
                }
                result = Array.newInstance(outputComponentType, list.size());
                for (int i = 0; i < list.size(); i++) {
                    Array.set(result, i, list.get(i));
                }
            } else {
                Object value = converterFunction.apply(inputArray, outputComponentType);
                if (value == null) {
                    result = Array.newInstance(outputComponentType, 0);
                } else {
                    result = Array.newInstance(outputComponentType, 1);
                    Array.set(result, 0, value);
                }
            }
        }
        return (R) result;
    }

    private static String[] toStringArray(String s) {
        return resolveArrayExpression(s, true, ',', '\\', '[', ']');
    }

    /**
     * 数组表达式解析
     *
     * <p>支持特殊字符 {@code \t\n\r\f}
     *
     * <p>括号可有可无
     *
     * <p>引号内除特殊字符外不用转义
     *
     * <p>引号内除特殊字符外不用转义，除开头括号
     *
     * <table>
     * <tbody>
     * <tr>
     * <td>[a,b,c]</td>
     * <td><code>a  b  c</code></td>
     * </tr>
     * <tr>
     * <td>[a a,b b,c c]</td>
     * <td><code>aa  bb  cc</code></td>
     * </tr>
     * <tr>
     * <td>["a a","b b",c c]</td>
     * <td><code>a a  b b  cc</code></td>
     * </tr>
     * <tr>
     * <td>["a 'a'","b b",'c c']</td>
     * <td><code>a 'a'  b b  c c</code></td>
     * </tr>
     * <tr>
     * <td>["a \"a\"","b b",'c c']</td>
     * <td><code>a "a"  b b  c c</code></td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param input           数组表达式
     * @param splitChar       指定分隔符
     * @param escapeChar      指定转义符
     * @param openingBrackets 左括号
     * @param closingBrackets 右括号
     * @return 返回解析处理的数组
     * @throws IllegalArgumentException 当数组表达式错误时
     */
    private static String[] resolveArrayExpression(
            final CharSequence input,
            final boolean isIgnoredQuote,
            final char splitChar,
            final char escapeChar,
            final char openingBrackets,
            final char closingBrackets) {
        if (input == null) {
            return new String[0];
        }
        int length = input.length();
        if (length == 0) {
            return new String[0];
        }
        Map<Character, Character> charMap =
                Map.of('t', '\t', 'n', '\n', 'r', '\r', 'f', '\f', 'b', '\b');
        StringBuilder sb = new StringBuilder(length);
        List<String> list = new ArrayList<>(length / 20 + 10);
        int bracketsCount = 0;
        boolean escape = false, doubleQuotes = false, singleQuotes = false, nextSplit = false;
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            // 跳过引号内的空白符
            if (!doubleQuotes && !singleQuotes && " \t\n\r\f".indexOf(c) != -1) {
                continue;
            }
            // 跳过开头括号
            if (Whether.empty(list) && sb.length() == 0 && openingBrackets == c) {
                continue;
            }
            // 记录转义
            if (c == escapeChar) {
                // 记录转义
                if (escape) {
                    sb.append(escapeChar);
                }
                escape = !escape;
                continue;
            }
            // 为转义字符，直接作为普通字符串
            if (escape) {
                sb.append(charMap.getOrDefault(c, c));
                escape = false;
                continue;
            }
            // 如果没有忽略引号和不在括号内则处理引号
            if (!isIgnoredQuote && bracketsCount == 0) {
                if (c == '"') {
                    if (singleQuotes) {
                        sb.append('"');
                    } else if (!(doubleQuotes = !doubleQuotes)) {
                        nextSplit = true;
                    }
                    continue;
                }
                if (c == '\'') {
                    if (doubleQuotes) {
                        sb.append('\'');
                    } else if (!(singleQuotes = !singleQuotes)) {
                        nextSplit = true;
                    }
                    continue;
                }
            }
            // 如果没有在在引号内则处理括号
            if (!singleQuotes && !doubleQuotes) {
                if (openingBrackets == c) {
                    bracketsCount++;
                } else if (closingBrackets == c) {
                    bracketsCount--;
                }
                if (bracketsCount < 0) {
                    continue;
                }
            }

            // 不在引号内，且不在括号内，并且是分割符则获取该值
            if (!singleQuotes && !doubleQuotes && bracketsCount == 0 && c == splitChar) {
                addAndClear(sb, list);
                nextSplit = false;
            } else {
                // 下一个期望的是分割符，则报错
                if (nextSplit) {
                    throw new IllegalArgumentException(
                            "Must be an array separator after the end of the quotation mark");
                }
                sb.append(c);
            }
        }
        if (doubleQuotes) {
            throw new IllegalArgumentException("Unterminated double quotation mark");
        }
        if (singleQuotes) {
            throw new IllegalArgumentException("unterminated single quotation mark");
        }
        if (bracketsCount != 0 && bracketsCount != -1) {
            if (bracketsCount > 0) {
                throw new IllegalArgumentException("Unterminated right brackets mark");
            } else {
                throw new IllegalArgumentException("Unterminated left brackets mark");
            }
        }
        if (sb.length() != 0) {
            addAndClear(sb, list);
        }
        return list.toArray(new String[0]);
    }

    private static void addAndClear(StringBuilder sb, List<String> list) {
        if (sb.length() != 0) {
            return;
        }
        list.add(sb.toString());
        sb.setLength(0);
    }

    @FunctionalInterface
    private interface Convert<T, R> extends Function<T, R> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        @Override
        default R apply(T t) {
            if (t == null) {
                return null;
            }
            try {
                return doApply(t);
            } catch (Throwable e) {
                e.printStackTrace();
                ToolLog.warn(LOGGER, e, () -> "The basic type conversion is abnormal");
            }
            return null;
        }

        R doApply(T t) throws Throwable;
    }
}
