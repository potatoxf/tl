package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.date.DateUtil;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolBoolean;
import pxf.tl.util.ToolByte;
import pxf.tl.util.ToolNumber;
import pxf.tl.util.ToolString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * 数字转换器<br>
 * 支持类型为：<br>
 *
 * <ul>
 *   <li>{@code java.lang.Byte}
 *   <li>{@code java.lang.Short}
 *   <li>{@code java.lang.Integer}
 *   <li>{@code java.util.concurrent.atomic.AtomicInteger}
 *   <li>{@code java.lang.Long}
 *   <li>{@code java.util.concurrent.atomic.AtomicLong}
 *   <li>{@code java.lang.Float}
 *   <li>{@code java.lang.Double}
 *   <li>{@code java.math.BigDecimal}
 *   <li>{@code java.math.BigInteger}
 * </ul>
 *
 * @author potatoxf
 */
public class NumberConverter extends AbstractConverter<Number> {
    private static final long serialVersionUID = 1L;

    private final Class<? extends Number> targetType;

    public NumberConverter() {
        this.targetType = Number.class;
    }

    /**
     * 构造<br>
     *
     * @param clazz 需要转换的数字类型，默认 {@link Number}
     */
    public NumberConverter(Class<? extends Number> clazz) {
        this.targetType = (null == clazz) ? Number.class : clazz;
    }

    /**
     * 转换对象为数字，支持的对象包括：
     *
     * <ul>
     *   <li>Number对象
     *   <li>Boolean
     *   <li>byte[]
     *   <li>String
     * </ul>
     *
     * @param value      对象值
     * @param targetType 目标的数字类型
     * @param toStrFunc  转换为字符串的函数
     * @return 转换后的数字
     */
    protected static Number convert(
            Object value, Class<? extends Number> targetType, Function<Object, String> toStrFunc) {
        // 枚举转换为数字默认为其顺序
        if (value instanceof Enum) {
            return convert(((Enum<?>) value).ordinal(), targetType, toStrFunc);
        }

        // since 5.7.18
        if (value instanceof byte[]) {
            return ToolByte.bytesToNumber((byte[]) value, targetType, ToolByte.DEFAULT_ORDER);
        }

        if (Byte.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            } else if (value instanceof Boolean) {
                return ToolBoolean.toByteObj((Boolean) value);
            }
            final String valueStr = toStrFunc.apply(value);
            try {
                return Whether.blank(valueStr) ? null : Byte.valueOf(valueStr);
            } catch (NumberFormatException e) {
                return ToolNumber.parseNumber(valueStr).byteValue();
            }
        } else if (Short.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            } else if (value instanceof Boolean) {
                return ToolBoolean.toShortObj((Boolean) value);
            }
            final String valueStr = toStrFunc.apply((value));
            try {
                return Whether.blank(valueStr) ? null : Short.valueOf(valueStr);
            } catch (NumberFormatException e) {
                return ToolNumber.parseNumber(valueStr).shortValue();
            }
        } else if (Integer.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof Boolean) {
                return ToolBoolean.toInteger((Boolean) value);
            } else if (value instanceof Date) {
                return (int) ((Date) value).getTime();
            } else if (value instanceof Calendar) {
                return (int) ((Calendar) value).getTimeInMillis();
            } else if (value instanceof TemporalAccessor) {
                return (int) DateUtil.toInstant((TemporalAccessor) value).toEpochMilli();
            }
            final String valueStr = toStrFunc.apply((value));
            return Whether.blank(valueStr) ? null : ToolNumber.parseInt(valueStr);
        } else if (AtomicInteger.class == targetType) {
            final Number number = convert(value, Integer.class, toStrFunc);
            if (null != number) {
                return new AtomicInteger(number.intValue());
            }
        } else if (Long.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof Boolean) {
                return ToolBoolean.toLongObj((Boolean) value);
            } else if (value instanceof Date) {
                return ((Date) value).getTime();
            } else if (value instanceof Calendar) {
                return ((Calendar) value).getTimeInMillis();
            } else if (value instanceof TemporalAccessor) {
                return DateUtil.toInstant((TemporalAccessor) value).toEpochMilli();
            }
            final String valueStr = toStrFunc.apply((value));
            return Whether.blank(valueStr) ? null : ToolNumber.parseLong(valueStr);
        } else if (AtomicLong.class == targetType) {
            final Number number = convert(value, Long.class, toStrFunc);
            if (null != number) {
                return new AtomicLong(number.longValue());
            }
        } else if (LongAdder.class == targetType) {
            // jdk8 新增
            final Number number = convert(value, Long.class, toStrFunc);
            if (null != number) {
                final LongAdder longValue = new LongAdder();
                longValue.add(number.longValue());
                return longValue;
            }
        } else if (Float.class == targetType) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else if (value instanceof Boolean) {
                return ToolBoolean.toFloatObj((Boolean) value);
            }
            final String valueStr = toStrFunc.apply((value));
            return Whether.blank(valueStr) ? null : ToolNumber.parseFloat(valueStr);
        } else if (Double.class == targetType) {
            if (value instanceof Number) {
                return ToolNumber.toDouble((Number) value);
            } else if (value instanceof Boolean) {
                return ToolBoolean.toDoubleObj((Boolean) value);
            }
            final String valueStr = toStrFunc.apply((value));
            return Whether.blank(valueStr) ? null : ToolNumber.parseDouble(valueStr);
        } else if (DoubleAdder.class == targetType) {
            // jdk8 新增
            final Number number = convert(value, Double.class, toStrFunc);
            if (null != number) {
                final DoubleAdder doubleAdder = new DoubleAdder();
                doubleAdder.add(number.doubleValue());
                return doubleAdder;
            }
        } else if (BigDecimal.class == targetType) {
            return toBigDecimal(value, toStrFunc);
        } else if (BigInteger.class == targetType) {
            return toBigInteger(value, toStrFunc);
        } else if (Number.class == targetType) {
            if (value instanceof Number) {
                return (Number) value;
            } else if (value instanceof Boolean) {
                return ToolBoolean.toInteger((Boolean) value);
            }
            final String valueStr = toStrFunc.apply((value));
            return Whether.blank(valueStr) ? null : ToolNumber.parseNumber(valueStr);
        }

        throw new UnsupportedOperationException(
                ToolString.format("Unsupport Number type: {}", targetType.getName()));
    }

    /**
     * 转换为BigDecimal<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value     被转换的值
     * @param toStrFunc 转换为字符串的函数规则
     * @return 结果
     */
    private static BigDecimal toBigDecimal(Object value, Function<Object, String> toStrFunc) {
        if (value instanceof Number) {
            return ToolNumber.toBigDecimal((Number) value);
        } else if (value instanceof Boolean) {
            return ((boolean) value) ? BigDecimal.ONE : BigDecimal.ZERO;
        }

        // 对于Double类型，先要转换为String，避免精度问题
        return ToolNumber.toBigDecimal(toStrFunc.apply(value));
    }

    /**
     * 转换为BigInteger<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value     被转换的值
     * @param toStrFunc 转换为字符串的函数规则
     * @return 结果
     */
    private static BigInteger toBigInteger(Object value, Function<Object, String> toStrFunc) {
        if (value instanceof Long) {
            return BigInteger.valueOf((Long) value);
        } else if (value instanceof Boolean) {
            return (boolean) value ? BigInteger.ONE : BigInteger.ZERO;
        }

        return ToolNumber.toBigInteger(toStrFunc.apply(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Number> getTargetType() {
        return (Class<Number>) this.targetType;
    }

    @Override
    protected Number convertInternal(Object value) {
        return convert(value, this.targetType, this::convertToStr);
    }

    @Override
    protected String convertToStr(Object value) {
        String result = ToolString.trim(super.convertToStr(value));
        if (null != result && result.length() > 1) {
            final char c = Character.toUpperCase(result.charAt(result.length() - 1));
            if (c == 'D' || c == 'L' || c == 'F') {
                // 类型标识形式（例如123.6D）
                return ToolString.subPre(result, -1);
            }
        }

        return result;
    }
}
