package pxf.tl.lang;

import pxf.tl.api.Sized;
import pxf.tl.exception.UnsupportedException;
import pxf.tl.function.SupplierThrow;
import pxf.tl.help.Safe;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 字符构造器
 *
 * @author potatoxf
 * @see StringBuilder
 * @see StringBuffer
 */
public abstract class TextBuilder implements CharSequence, Comparable<TextBuilder>, Serializable, Sized {
    /**
     * {@code java.lang.AbstractStringBuilder#compareTo(AbstractStringBuilder)}Method
     */
    private static final Method COMPARE_TO_METHOD = ((SupplierThrow<Method, Throwable>) () -> {
        Class<?> clz = Class.forName("java.lang.AbstractStringBuilder");
        Method compareTo = clz.getDeclaredMethod("compareTo", clz);
        compareTo.setAccessible(true);
        return compareTo;
    }).get();

    private static int calculateCapacity(int nowSize) {
        if (nowSize <= 256) {
            return nowSize * 2;
        } else if (nowSize <= 512) {
            return (int) (nowSize * 1.6);
        } else if (nowSize <= 1024) {
            return (int) (nowSize * 1.2);
        } else {
            return nowSize + 256;
        }
    }

    public static TextBuilder of() {
        return of(false);
    }

    public static TextBuilder of(int capacity) {
        return of(false, capacity);
    }

    public static TextBuilder of(TextBuilder charSequence) {
        return of(false, charSequence);
    }

    public static TextBuilder of(CharSequence charSequence) {
        return of(false, charSequence);
    }

    public static TextBuilder of(boolean isConcurrent) {
        return of(isConcurrent, 128);
    }

    public static TextBuilder of(boolean isConcurrent, int capacity) {
        if (isConcurrent) {
            return new StringBufferImpl(new StringBuffer(capacity));
        } else {
            return new StringBuilderImpl(new StringBuilder(capacity));
        }
    }

    public static TextBuilder of(boolean isConcurrent, TextBuilder charSequence) {
        TextBuilder textBuilder = of(isConcurrent, calculateCapacity(charSequence.length()));
        if (charSequence instanceof StringBuilderImpl stringBuilder) {
            return textBuilder.append(stringBuilder.instance);
        } else if (charSequence instanceof StringBufferImpl stringBuffer) {
            return textBuilder.append(stringBuffer.instance);
        } else {
            return textBuilder.append(charSequence);
        }
    }

    public static TextBuilder of(boolean isConcurrent, CharSequence charSequence) {
        return of(isConcurrent, calculateCapacity(charSequence.length())).append(charSequence);
    }

    public final TextBuilder appendIfPresent(Object value) {
        if (isNoEmpty()) {
            return append(value);
        }
        return this;
    }

    public abstract TextBuilder append(Object value);

    public abstract TextBuilder append(String value);

    public abstract TextBuilder append(StringBuffer value);

    public abstract TextBuilder append(CharSequence value);

    public abstract TextBuilder append(CharSequence value, int start, int end);

    public abstract TextBuilder append(char[] value);

    public abstract TextBuilder append(char[] value, int offset, int len);

    public abstract TextBuilder append(boolean value);

    public abstract TextBuilder append(char value);

    public abstract TextBuilder append(int value);

    public abstract TextBuilder append(long value);

    public abstract TextBuilder append(float value);

    public abstract TextBuilder append(double value);

    public abstract TextBuilder appendCodePoint(int codePoint);

    public abstract TextBuilder delete(int start, int end);

    public abstract TextBuilder deleteCharAt(int index);

    public abstract TextBuilder replace(int start, int end, String value);

    public abstract TextBuilder insert(int index, char[] value, int offset, int len);

    public abstract TextBuilder insert(int offset, Object value);

    public abstract TextBuilder insert(int offset, String value);

    public abstract TextBuilder insert(int offset, char[] value);

    public abstract TextBuilder insert(int offset, CharSequence value);

    public abstract TextBuilder insert(int offset, CharSequence value, int start, int end);

    public abstract TextBuilder insert(int offset, boolean value);

    public abstract TextBuilder insert(int offset, char value);

    public abstract TextBuilder insert(int offset, int value);

    public abstract TextBuilder insert(int offset, long value);

    public abstract TextBuilder insert(int offset, float value);

    public abstract TextBuilder insert(int offset, double value);

    public abstract TextBuilder reverse();

    public abstract TextBuilder clear();

    public abstract int indexOf(String value);

    public abstract int indexOf(String value, int fromIndex);

    public abstract int lastIndexOf(String value);

    public abstract int lastIndexOf(String value, int fromIndex);

    @Override
    public abstract int length();

    public abstract TextBuilder setLength(int length);

    @Override
    public abstract char charAt(int index);

    @Override
    public abstract CharSequence subSequence(int start, int end);

    public abstract String toString();

    public final TextBuilder appendRepeat(Object value, int repeatCount) {
        for (int i = 0; i < repeatCount; i++) {
            append(value);
        }
        return this;
    }

    @Override
    public final int size() {
        return length();
    }

    @Override
    public final boolean isEmpty() {
        return Sized.super.isEmpty();
    }

    public final int compareTo(@Nonnull TextBuilder another) {
        if (this instanceof StringBuilderImpl s1) {
            if (another instanceof StringBuilderImpl s2) {
                return s1.instance.compareTo(s2.instance);
            } else if (another instanceof StringBufferImpl s2) {
                try {
                    return (int) COMPARE_TO_METHOD.invoke(s1.instance, s2.instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Error to invoke java.lang.AbstractStringBuilder#compareTo(AbstractStringBuilder)", e);
                }
            }
        } else if (this instanceof StringBufferImpl s1) {
            if (another instanceof StringBuilderImpl s2) {
                try {
                    return (int) COMPARE_TO_METHOD.invoke(s1.instance, s2.instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Error to invoke java.lang.AbstractStringBuilder#compareTo(AbstractStringBuilder)", e);
                }
            } else if (another instanceof StringBufferImpl s2) {
                return s1.instance.compareTo(s2.instance);
            }
        }
        return compareToCustom(another);
    }

    protected int compareToCustom(TextBuilder another) {
        throw new UnsupportedException("compareToCustom");
    }

    public final String toStringAndReset() {
        String result = toString();
        clear();
        return result;
    }


    private static class StringBuilderImpl extends TextBuilder {
        final StringBuilder instance;

        StringBuilderImpl(StringBuilder instance) {
            this.instance = instance;
        }

        @Override
        public TextBuilder append(Object value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(String value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(StringBuffer value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(CharSequence value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(CharSequence value, int start, int end) {
            instance.append(value, start, end);
            return this;
        }

        @Override
        public TextBuilder append(char[] value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(char[] value, int offset, int len) {
            instance.append(value, offset, len);
            return this;
        }

        @Override
        public TextBuilder append(boolean value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(char value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(int value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(long value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(float value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(double value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder appendCodePoint(int codePoint) {
            instance.appendCodePoint(codePoint);
            return this;
        }

        @Override
        public TextBuilder delete(int start, int end) {
            instance.delete(start, end);
            return this;
        }

        @Override
        public TextBuilder deleteCharAt(int index) {
            instance.deleteCharAt(index);
            return this;
        }

        @Override
        public TextBuilder replace(int start, int end, String value) {
            instance.replace(start, end, value);
            return this;
        }

        @Override
        public TextBuilder insert(int index, char[] value, int offset, int len) {
            instance.insert(index, value, offset, len);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, Object value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, String value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, char[] value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, CharSequence value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, CharSequence value, int start, int end) {
            instance.insert(offset, value, start, end);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, boolean value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, char value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, int value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, long value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, float value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, double value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder reverse() {
            instance.reverse();
            return this;
        }

        @Override
        public TextBuilder clear() {
            instance.setLength(0);
            return this;
        }

        @Override
        public int indexOf(String value) {
            return instance.indexOf(value);
        }

        @Override
        public int indexOf(String value, int fromIndex) {
            return instance.indexOf(value, fromIndex);
        }

        @Override
        public int lastIndexOf(String value) {
            return instance.lastIndexOf(value);
        }

        @Override
        public int lastIndexOf(String value, int fromIndex) {
            return instance.lastIndexOf(value, fromIndex);
        }

        @Override
        public int length() {
            return instance.length();
        }

        @Override
        public TextBuilder setLength(int length) {
            instance.setLength(length);
            return this;
        }

        @Override
        public char charAt(int index) {
            return instance.charAt(Safe.idx(index, instance.length()));
        }

        @Nonnull
        @Override
        public CharSequence subSequence(int start, int end) {
            return instance.subSequence(start, end);
        }

        @Nonnull
        @Override
        public String toString() {
            return instance.toString();
        }
    }

    public static class StringBufferImpl extends TextBuilder {
        final StringBuffer instance;

        StringBufferImpl(StringBuffer instance) {
            this.instance = instance;
        }

        @Override
        public TextBuilder append(Object value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(String value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(StringBuffer value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(CharSequence value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(CharSequence value, int start, int end) {
            instance.append(value, start, end);
            return this;
        }

        @Override
        public TextBuilder append(char[] value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(char[] value, int offset, int len) {
            instance.append(value, offset, len);
            return this;
        }

        @Override
        public TextBuilder append(boolean value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(char value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(int value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(long value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(float value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder append(double value) {
            instance.append(value);
            return this;
        }

        @Override
        public TextBuilder appendCodePoint(int codePoint) {
            instance.appendCodePoint(codePoint);
            return this;
        }

        @Override
        public TextBuilder delete(int start, int end) {
            instance.delete(start, end);
            return this;
        }

        @Override
        public TextBuilder deleteCharAt(int index) {
            instance.deleteCharAt(index);
            return this;
        }

        @Override
        public TextBuilder replace(int start, int end, String value) {
            instance.replace(start, end, value);
            return this;
        }

        @Override
        public TextBuilder insert(int index, char[] value, int offset, int len) {
            instance.insert(index, value, offset, len);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, Object value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, String value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, char[] value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, CharSequence value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, CharSequence value, int start, int end) {
            instance.insert(offset, value, start, end);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, boolean value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, char value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, int value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, long value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, float value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder insert(int offset, double value) {
            instance.insert(offset, value);
            return this;
        }

        @Override
        public TextBuilder reverse() {
            instance.reverse();
            return this;
        }

        @Override
        public TextBuilder clear() {
            instance.setLength(0);
            return this;
        }

        @Override
        public int indexOf(String value) {
            return instance.indexOf(value);
        }

        @Override
        public int indexOf(String value, int fromIndex) {
            return instance.indexOf(value, fromIndex);
        }

        @Override
        public int lastIndexOf(String value) {
            return instance.lastIndexOf(value);
        }

        @Override
        public int lastIndexOf(String value, int fromIndex) {
            return instance.lastIndexOf(value, fromIndex);
        }

        @Override
        public int length() {
            return instance.length();
        }

        @Override
        public TextBuilder setLength(int length) {
            instance.setLength(length);
            return this;
        }

        @Override
        public char charAt(int index) {
            return instance.charAt(Safe.idx(index, instance.length()));
        }

        @Nonnull
        @Override
        public CharSequence subSequence(int start, int end) {
            return instance.subSequence(start, end);
        }

        @Nonnull
        @Override
        public String toString() {
            return instance.toString();
        }
    }
}
