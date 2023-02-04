package pxf.tl.help;

import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.PoolOfString;
import pxf.tl.io.FileUtil;
import pxf.tl.util.ToolURL;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * @author potatoxf
 */
public final class Whether {

    private Whether() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    //------------------------------------------------------------------------------------------------------------------
    //基本判断
    //------------------------------------------------------------------------------------------------------------------

    public static boolean nvl(Object input) {
        return input == null || (input instanceof String s) && (PoolOfString.NULL.equalsIgnoreCase(s) || PoolOfString.UNDEFINED.equalsIgnoreCase(s));
    }

    public static boolean noNvl(Object input) {
        return !nvl(input);
    }

    public static boolean anyNvl(Object... input) {
        if (input == null) {
            return true;
        }
        if (input.length == 0) {
            return true;
        } else if (input.length == 1) {
            return Whether.nvl(input[0]);
        } else {
            for (Object o : input) {
                if (Whether.nvl(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean noAnyNvl(Object... input) {
        return !Whether.anyNvl(input);
    }

    public static boolean allNvl(Object... input) {
        if (input == null) {
            return true;
        }
        if (input.length == 0) {
            return true;
        } else if (input.length == 1) {
            return Whether.nvl(input[0]);
        } else {
            for (Object o : input) {
                if (Whether.noNvl(o)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean noAllNvl(Object... input) {
        return !Whether.allNvl(input);
    }

    public static boolean anyNvls(Map<?, ?> container, Object... keys) {
        if (container == null || container.isEmpty()) {
            return true;
        }
        for (Object key : keys) {
            if (Whether.nvl(container.get(key))) {
                return true;
            }
        }
        return false;
    }

    public static boolean noAnyNvls(Map<?, ?> container, Object... keys) {
        return !Whether.anyNvls(container, keys);
    }

    public static boolean allNvls(Map<?, ?> container, Object... keys) {
        if (container == null || container.isEmpty()) {
            return true;
        }
        for (Object key : keys) {
            if (Whether.noNvl(container.get(key))) {
                return false;
            }
        }
        return true;
    }

    public static boolean noAllNvls(Map<?, ?> container, Object... keys) {
        return !Whether.allNvls(container, keys);
    }

    public static <T> boolean empty(Object any) {
        if (Whether.nvl(any)) {
            return true;
        }
        switch (any) {
            case CharSequence cs -> {
                return cs.isEmpty();
            }
            case Collection collection -> {
                return collection.isEmpty();
            }
            case Map map -> {
                return map.isEmpty();
            }
            case File file -> {
                if (!file.exists()) {
                    return true;
                }
                if (file.isDirectory()) {
                    String[] subFiles = file.list();
                    return subFiles == null || subFiles.length == 0;
                } else if (file.isFile()) {
                    return file.length() <= 0;
                }
                return false;
            }
            case Path path -> {
                File file = path.toFile();
                if (!file.exists()) {
                    return true;
                }
                if (file.isDirectory()) {
                    String[] subFiles = file.list();
                    return subFiles == null || subFiles.length == 0;
                } else if (file.isFile()) {
                    return file.length() <= 0;
                }
                return false;
            }
            case Iterable it -> {
                return !it.iterator().hasNext();
            }
            case Iterator it -> {
                return !it.hasNext();
            }
            case InputStream in -> {
                if (!in.markSupported()) {
                    in = new PushbackInputStream(in, 1);
                }
                in.mark(1);
                try {
                    int read = in.read();
                    if (in instanceof PushbackInputStream pin) {
                        pin.unread(read);
                    } else {
                        in.reset();
                    }
                    return read == -1;
                } catch (IOException e) {
                    return true;
                }
            }
            default -> {
                Class<?> clz = any.getClass();
                if (clz.isArray()) {
                    int length = Array.getLength(any);
                    if (length != 0) {
                        for (int i = 0; i < length; i++) {
                            Object object = Array.get(any, i);
                            if (Whether.empty(object)) {
                                return true;
                            }
                        }
                    }
                    return length == 0;
                }
                return false;
            }
        }
    }

    public static boolean noEmpty(Object any) {
        return !Whether.empty(any);
    }

    public static <T> boolean blank(Object any) {
        if (Whether.nvl(any)) {
            return true;
        }
        switch (any) {
            case CharSequence charSequence:
                for (int i = 0; i < charSequence.length(); i++) {
                    char c = charSequence.charAt(i);
                    if (!Character.isWhitespace(c)
                            && !Character.isSpaceChar(c)
                            && c != '\ufeff'
                            && c != '\u202a') {
                        return false;
                    }
                }
                return true;
            case Character character:
                return Character.isWhitespace(character)
                        || Character.isSpaceChar(character)
                        || character == '\ufeff'
                        || character == '\u202a';
            case Number number:
                int value = number.intValue();
                return Character.isWhitespace(value)
                        || Character.isSpaceChar(value)
                        || value == '\ufeff'
                        || value == '\u202a';
            default:
                return Whether.empty(any);
        }
    }

    public static boolean noBlank(Object any) {
        return !blank(any);
    }

    /**
     * 是否包含空字符串
     *
     * @param inputs 字符串列表
     * @return 是否包含空字符串
     */
    public static boolean hasBlank(CharSequence... inputs) {
        for (CharSequence input : inputs) {
            if (Whether.blank(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 给定所有字符串是否为空白
     *
     * @param inputs 字符串
     * @return 所有字符串是否为空白
     */
    public static boolean allBlank(CharSequence... inputs) {
        for (CharSequence input : inputs) {
            if (Whether.noBlank(input)) {
                return false;
            }
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------
    //字节码修饰符判读
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 是否可访问的
     *
     * @param member 被访问成员
     * @return 如果可以被访问则 {@code true}，否则 {@code false}
     */
    public static boolean accessibleMember(Member member) {
        Class<?> dc = member.getDeclaringClass();
        int dcm = dc.getModifiers();
        if (!Modifier.isPublic(dcm)) {
            return false;
        }
        int m = member.getModifiers();
        return Modifier.isPublic(m);
    }

    public static boolean publicModifier(Member input) {
        return withModifier(input, Modifier.PUBLIC);
    }

    public static boolean publicModifier(Class<?> input) {
        return withModifier(input, Modifier.PUBLIC);
    }

    public static boolean staticModifier(Member input) {
        return withModifier(input, Modifier.STATIC);
    }

    public static boolean staticModifier(Class<?> input) {
        return withModifier(input, Modifier.STATIC);
    }

    public static boolean finalModifier(Member input) {
        return withModifier(input, Modifier.FINAL);
    }

    public static boolean finalModifier(Class<?> input) {
        return withModifier(input, Modifier.FINAL);
    }

    public static boolean abstractModifier(Member input) {
        return withModifier(input, Modifier.ABSTRACT);
    }

    public static boolean abstractModifier(Class<?> input) {
        return withModifier(input, Modifier.ABSTRACT);
    }

    public static boolean interfaceModifier(Class<?> input) {
        return withModifier(input, Modifier.INTERFACE);
    }

    public static boolean interfacePublicModifier(Class<?> input) {
        return withModifier(input, Modifier.INTERFACE, Modifier.PUBLIC);
    }

    public static boolean publicStaticModifier(Member input) {
        return withModifier(input, Modifier.PUBLIC, Modifier.STATIC);
    }

    public static boolean publicStaticModifier(Class<?> input) {
        return withModifier(input, Modifier.PUBLIC, Modifier.STATIC);
    }

    public static boolean publicStaticFinalModifier(Member input) {
        return withModifier(input, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

    public static boolean publicStaticFinalModifier(Class<?> input) {
        return withModifier(input, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
    }

    public static boolean publicSyntheticModifier(Member input) {
        return withModifier(input, 0x00001000);
    }

    public static boolean publicSyntheticModifier(Class<?> input) {
        return withModifier(input, 0x00001000);
    }

    public static boolean withModifier(Member input, int... modifiers) {
        if (input == null || modifiers.length == 0) {
            return false;
        }
        int mod = input.getModifiers();
        for (int modifier : modifiers) {
            if ((mod & modifier) != modifier) {
                return false;
            }
        }
        return true;
    }

    public static boolean withModifier(Class<?> input, int... modifiers) {
        if (input == null || modifiers.length == 0) {
            return false;
        }
        int mod = input.getModifiers();
        for (int modifier : modifiers) {
            if ((mod & modifier) != modifier) {
                return false;
            }
        }
        return true;
    }

    public static boolean matchModifiers(
            Member member, @Nullable Integer includeModifiers, @Nullable Integer excludeModifiers) {
        int modifiers = member.getModifiers();
        return (includeModifiers == null || (includeModifiers & modifiers) == includeModifiers)
                && (excludeModifiers == null || (excludeModifiers & modifiers) == 0);
    }

    public static boolean matchModifiers(
            Class<?> input, @Nullable Integer includeModifiers, @Nullable Integer excludeModifiers) {
        int modifiers = input.getModifiers();
        return (includeModifiers == null || (includeModifiers & modifiers) == includeModifiers)
                && (excludeModifiers == null || (excludeModifiers & modifiers) == 0);
    }

    //------------------------------------------------------------------------------------------------------------------
    //对象类型判断
    //------------------------------------------------------------------------------------------------------------------

    public static boolean arrayObj(Object obj) {
        return obj != null && arrayType(obj.getClass());
    }

    public static boolean basicObj(Object obj) {
        return obj != null && (obj instanceof String
                || basicNumberObj(obj)
                || obj instanceof Boolean
                || obj instanceof Character);
    }

    public static boolean basicNumberObj(Object obj) {
        return obj != null && (obj instanceof Class ? basicNumberType((Class<?>) obj) : (intObj(obj)
                || doubleObj(obj)
                || longObj(obj)
                || floatObj(obj)
                || shortObj(obj)
                || byteObj(obj)));
    }

    public static boolean boolObj(Object obj) {
        return obj instanceof Boolean;
    }

    public static boolean charObj(Object obj) {
        return obj instanceof Character;
    }

    public static boolean byteObj(Object obj) {
        return obj instanceof Byte;
    }

    public static boolean shortObj(Object obj) {
        return obj instanceof Short;
    }

    public static boolean intObj(Object obj) {
        return obj instanceof Integer;
    }

    public static boolean longObj(Object obj) {
        return obj instanceof Long;
    }

    public static boolean floatObj(Object obj) {
        return obj instanceof Float;
    }

    public static boolean doubleObj(Object obj) {
        return obj instanceof Double;
    }

    public static boolean arrayType(Class<?> clz) {
        return clz != null && clz.isArray();
    }

    public static boolean basicType(Class<?> clz) {
        return clz != null && (String.class == clz || basicNumberType(clz) || boolType(clz) || charType(clz));
    }

    public static boolean basicNumberType(Class<?> clz) {
        return intType(clz)
                || doubleType(clz)
                || longType(clz)
                || floatType(clz)
                || shortType(clz)
                || byteType(clz);
    }

    public static boolean boolType(Class<?> clz) {
        return boolean.class == clz || Boolean.class == clz;
    }

    public static boolean charType(Class<?> clz) {
        return char.class == clz || Character.class == clz;
    }

    public static boolean byteType(Class<?> clz) {
        return byte.class == clz || Byte.class == clz;
    }

    public static boolean shortType(Class<?> clz) {
        return short.class == clz || Short.class == clz;
    }

    public static boolean intType(Class<?> clz) {
        return int.class == clz || Integer.class == clz;
    }

    public static boolean longType(Class<?> clz) {
        return long.class == clz || Long.class == clz;
    }

    public static boolean floatType(Class<?> clz) {
        return float.class == clz || Float.class == clz;
    }

    public static boolean doubleType(Class<?> clz) {
        return double.class == clz || Double.class == clz;
    }

    //------------------------------------------------------------------------------------------------------------------
    //数值判断
    //------------------------------------------------------------------------------------------------------------------

    public static boolean primes(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean even(int v) {
        return v % 2 == 0;
    }

    public static boolean even(long v) {
        return v % 2 == 0;
    }

    public static boolean odd(int v) {
        return v % 2 != 0;
    }

    public static boolean odd(long v) {
        return v % 2 != 0;
    }

    public static boolean inside(int v, int lo, int hi) {
        return v >= lo && v < hi;
    }

    public static boolean insideClosed(int v, int lo, int hi) {
        return v >= lo && v <= hi;
    }

    public static boolean inside(long v, long lo, long hi) {
        return v >= lo && v < hi;
    }

    public static boolean insideClosed(long v, long lo, long hi) {
        return v >= lo && v <= hi;
    }

    public static boolean inside(double v, double lo, double hi) {
        return v >= lo && v < hi;
    }

    public static boolean insideClosed(double v, double lo, double hi) {
        return v >= lo && v <= hi;
    }

    public static boolean outside(int v, int lo, int hi) {
        return !inside(v, lo, hi);
    }

    public static boolean outsideOpened(int v, int lo, int hi) {
        return !insideClosed(v, lo, hi);
    }

    public static boolean outside(long v, long lo, long hi) {
        return !inside(v, lo, hi);
    }

    public static boolean outsideOpened(long v, long lo, long hi) {
        return !insideClosed(v, lo, hi);
    }

    public static boolean outside(double v, double lo, double hi) {
        return !inside(v, lo, hi);
    }

    public static boolean outsideOpened(double v, double lo, double hi) {
        return !insideClosed(v, lo, hi);
    }

    //------------------------------------------------------------------------------------------------------------------
    //文件判断
    //------------------------------------------------------------------------------------------------------------------

    public static boolean directory(File file) {
        return exists(file) && file.isDirectory();
    }

    public static boolean file(File file) {
        return exists(file) && file.isFile();
    }

    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    //------------------------------------------------------------------------------------------------------------------
    //url协议判断
    //------------------------------------------------------------------------------------------------------------------


    public static boolean directoryUrl(URL url) {
        return fileUrl(url) && !url.getFile().isEmpty() && pathDelimiterChar(url.getFile().charAt(url.getFile().length() - 1));
    }

    /**
     * 提供的URL是否为文件<br>
     * 文件协议包括"file", "vfsfile" 或 "vfs".
     *
     * @param url {@link URL}
     * @return 是否为文件
     */
    public static boolean fileUrl(URL url) {
        return url != null && (ToolURL.URL_PROTOCOL_FILE.equals(url.getProtocol())
                || ToolURL.URL_PROTOCOL_VFSFILE.equals(url.getProtocol())
                || ToolURL.URL_PROTOCOL_VFS.equals(url.getProtocol()));
    }

    /**
     * 提供的URL是否为jar包URL 协议包括： "jar", "zip", "vfszip" 或 "wsjar".
     *
     * @param url {@link URL}
     * @return 是否为jar包URL
     */
    public static boolean jarUrl(URL url) {
        return url != null && (ToolURL.URL_PROTOCOL_JAR.equals(url.getProtocol())
                || ToolURL.URL_PROTOCOL_ZIP.equals(url.getProtocol())
                || ToolURL.URL_PROTOCOL_VFSZIP.equals(url.getProtocol())
                || ToolURL.URL_PROTOCOL_WSJAR.equals(url.getProtocol()));
    }

    /**
     * 提供的URL是否为Jar文件URL 判断依据为file协议且扩展名为.jar
     *
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR file URL
     */
    public static boolean jarFileURL(URL url) {
        return url != null && (ToolURL.URL_PROTOCOL_FILE.equals(url.getProtocol())
                && url.getPath().toLowerCase().endsWith(FileUtil.JAR_FILE_EXT));
    }

    public static boolean httpUrl(URL url) {
        return "http".equalsIgnoreCase(url.getProtocol());
    }

    public static boolean httpsUrl(URL url) {
        return "https".equalsIgnoreCase(url.getProtocol());
    }

    public static boolean ftpUrl(URL url) {
        return "ftp".equalsIgnoreCase(url.getProtocol());
    }

    public static boolean hasJarFileInPath(URL url) {
        return url.toExternalForm().matches(".*\\.jar(!.*|$)");
    }

    public static boolean hasInnerJarFileInPath(URL url) {
        return url.toExternalForm().matches(".+\\.jar!/.+");
    }

    //------------------------------------------------------------------------------------------------------------------
    //字符判读
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 是否为ASCII字符，ASCII字符位于0~127之间
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII字符，ASCII字符位于0~127之间
     */
    public static boolean asciiChar(char input) {
        return input < 128;
    }

    /**
     * 是否为ASCII字符，ASCII字符位于0~127之间
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII字符，ASCII字符位于0~127之间
     */
    public static boolean asciiChar(int input) {
        return input < 128;
    }

    /**
     * 是否为可见ASCII字符，可见字符位于32~126之间
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII可见字符，可见字符位于32~126之间
     */
    public static boolean asciiPrintableChar(char input) {
        return input >= 32 && input < 127;
    }

    /**
     * 是否为可见ASCII字符，可见字符位于32~126之间
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII可见字符，可见字符位于32~126之间
     */
    public static boolean asciiPrintableChar(int input) {
        return input >= 32 && input < 127;
    }

    /**
     * 是否为ASCII控制符（不可见字符），控制符位于0~31和127
     *
     * @param input 被检查的字符
     * @return true表示为控制符，控制符位于0~31和127
     */
    public static boolean asciiControlChar(char input) {
        return input < 32 || input == 127;
    }

    /**
     * 是否为ASCII控制符（不可见字符），控制符位于0~31和127
     *
     * @param input 被检查的字符
     * @return true表示为控制符，控制符位于0~31和127
     */
    public static boolean asciiControlChar(int input) {
        return input < 32 || input == 127;
    }

    /**
     * 判断是否为字母（包括大写字母和小写字母）<br>
     * 字母包括A~Z和a~z
     *
     * @param input 被检查的字符
     * @return true表示为字母（包括大写字母和小写字母）字母包括A~Z和a~z
     */
    public static boolean letterChar(char input) {
        return letterUpperChar(input) || letterLowerChar(input);
    }

    /**
     * 判断是否为字母（包括大写字母和小写字母）<br>
     * 字母包括A~Z和a~z
     *
     * @param input 被检查的字符
     * @return true表示为字母（包括大写字母和小写字母）字母包括A~Z和a~z
     */
    public static boolean letterChar(int input) {
        return letterUpperChar(input) || letterLowerChar(input);
    }

    /**
     * 判断是否为大写字母，大写字母包括A~Z
     *
     * @param input 被检查的字符
     * @return true表示为大写字母，大写字母包括A~Z
     */
    public static boolean letterUpperChar(char input) {
        return Character.isUpperCase(input);
    }

    /**
     * 判断是否为大写字母，大写字母包括A~Z
     *
     * @param input 被检查的字符
     * @return true表示为大写字母，大写字母包括A~Z
     */
    public static boolean letterUpperChar(int input) {
        return Character.isUpperCase(input);
    }

    /**
     * 检查字符是否为小写字母，小写字母指a~z
     *
     * @param input 被检查的字符
     * @return true表示为小写字母，小写字母指a~z
     */
    public static boolean letterLowerChar(char input) {
        return input >= 'a' && input <= 'z';
    }

    /**
     * 检查字符是否为小写字母，小写字母指a~z
     *
     * @param input 被检查的字符
     * @return true表示为小写字母，小写字母指a~z
     */
    public static boolean letterLowerChar(int input) {
        return input >= 97 && input <= 122;
    }

    /**
     * 检查是否为数字字符，数字字符指0~9
     *
     * @param input 被检查的字符
     * @return true表示为数字字符，数字字符指0~9
     */
    public static boolean numberChar(char input) {
        return input >= '0' && input <= '9';
    }

    /**
     * 检查是否为数字字符，数字字符指0~9
     *
     * @param input 被检查的字符
     * @return true表示为数字字符，数字字符指0~9
     */
    public static boolean numberChar(int input) {
        return Character.isDigit(input);
    }

    /**
     * 是否为16进制规范的字符，判断是否为如下字符
     *
     * <pre>
     * 1. 0~9
     * 2. a~f
     * 4. A~F
     * </pre>
     *
     * @param input 字符
     * @return 是否为16进制规范的字符
     */
    public static boolean hexChar(char input) {
        return numberChar(input) || (input >= 'a' && input <= 'f') || (input >= 'A' && input <= 'F');
    }

    /**
     * 是否为16进制规范的字符，判断是否为如下字符
     *
     * <pre>
     * 1. 0~9
     * 2. a~f
     * 4. A~F
     * </pre>
     *
     * @param input 字符
     * @return 是否为16进制规范的字符
     */
    public static boolean hexChar(int input) {
        return numberChar(input) || (input >= 'a' && input <= 'f') || (input >= 'A' && input <= 'F');
    }

    /**
     * 是否为字符或数字，包括A~Z、a~z、0~9
     *
     * @param input 被检查的字符
     * @return true表示为字符或数字，包括A~Z、a~z、0~9
     */
    public static boolean letterOrNumberChar(char input) {
        return letterChar(input) || numberChar(input);
    }

    /**
     * 是否为字符或数字，包括A~Z、a~z、0~9
     *
     * @param input 被检查的字符
     * @return true表示为字符或数字，包括A~Z、a~z、0~9
     */
    public static boolean letterOrNumberChar(int input) {
        return letterChar(input) || numberChar(input);
    }

    /**
     * 是否是全角字符串
     *
     * @param codepoint unicode 码点
     * @return 如果是则返回 {@code true}，否则 {@code false}
     */
    public static boolean quadEmChar(int codepoint) {
        boolean isEm;
        if (codepoint < 0x9FFF) {
            isEm =
                    (codepoint >= 0x1100 && codepoint <= 0x11FF)
                            || (codepoint >= 0x2600 && codepoint <= 0x27BF)
                            || (codepoint >= 0x2800 && codepoint <= 0x28FF)
                            || (codepoint >= 0x2E80 && codepoint <= 0x2FDF)
                            || (codepoint >= 0x2FF0 && codepoint <= 0x318F)
                            || (codepoint >= 0x31A0 && codepoint <= 0x4DB5)
                            || (codepoint >= 0x4DC0 && codepoint <= 0x9FBB);
        } else if (codepoint < 0xFAFF) {
            isEm =
                    (codepoint >= 0xA000 && codepoint <= 0xA4CF)
                            || (codepoint >= 0xAC00 && codepoint <= 0xD7AF)
                            || (codepoint >= 0xF900 && codepoint <= 0xFA2D)
                            || (codepoint >= 0xFA30 && codepoint <= 0xFA6A)
                            || (codepoint >= 0xFA70 && codepoint <= 0xFAD9);
        } else {
            isEm =
                    (codepoint >= 0xFE10 && codepoint <= 0xFE1F)
                            || (codepoint >= 0xFE30 && codepoint <= 0xFE4F)
                            || (codepoint >= 0xFF00 && codepoint <= 0xFFEF)
                            || (codepoint >= 0x1D300 && codepoint <= 0x1D35F)
                            || (codepoint >= 0x20000 && codepoint <= 0x2A6D6)
                            || (codepoint >= 0x2F800 && codepoint <= 0x2FA1D);
        }
        return isEm;
    }

    /**
     * 比较两个字符是否相同
     *
     * @param c1         字符1
     * @param c2         字符2
     * @param ignoreCase 是否忽略大小写
     * @return 是否相同
     */
    public static boolean equalChar(char c1, char c2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(c1) == Character.toLowerCase(c2);
        }
        return c1 == c2;
    }

    /**
     * 比较字符数组和字符串是否相等
     *
     * @param chars        字符数组
     * @param csi          字符起始位置
     * @param cei          字符结束位置
     * @param string       字符串
     * @param ssi          字符串起始位置
     * @param sei          字符串结束位置
     * @param isIgnoreCase 忽略字符串
     * @return 返回是否相等
     */
    public static boolean equalCharsWithString(
            char[] chars, int csi, int cei, String string, int ssi, int sei, boolean isIgnoreCase) {
        if (sei - ssi == cei - csi) {
            boolean isEqual = true;
            for (int j = 0; j < sei; j++) {
                if (!equalChar(chars[csi + j], string.charAt(ssi + j), isIgnoreCase)) {
                    isEqual = false;
                }
            }
            return isEqual;
        }
        return false;
    }

    /**
     * 是否是路径分隔符 {@code \}或 {@code /}
     *
     * @param c 字符
     * @return 如果是返回 {@code true}，否则 {@code false}
     */
    public static boolean pathDelimiterChar(char c) {
        return c == '/' || c == '\\';
    }

    /**
     * 是否是路径分隔符 {@code \}或 {@code /}
     *
     * @param c1 字符
     * @param c2 字符
     * @return 如果是返回 {@code true}，否则 {@code false}
     */
    public static boolean pathDelimiterChar(char c1, char c2) {
        return (c1 == '/' || c1 == '\\') && (c2 == '/' || c2 == '\\');
    }


    //------------------------------------------------------------------------------------------------------------------
    //字符串判断
    //------------------------------------------------------------------------------------------------------------------

    public static boolean containsStringOneByOne(String input, String[] collection) {
        return containsStringOneByOne(input, List.of(collection));
    }

    public static boolean containsStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::contains, input, collection);
    }

    public static boolean equalsStringOneByOne(String input, String[] collection) {
        return equalsStringOneByOne(input, List.of(collection));
    }

    public static boolean equalsStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::equals, input, collection);
    }

    public static boolean equalsIgnoreCaseStringOneByOne(String input, String[] collection) {
        return equalsIgnoreCaseStringOneByOne(input, List.of(collection));
    }

    public static boolean equalsIgnoreCaseStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::equalsIgnoreCase, input, collection);
    }

    public static boolean contentEqualsStringOneByOne(String input, String[] collection) {
        return contentEqualsStringOneByOne(input, List.of(collection));
    }

    public static boolean contentEqualsStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::contentEquals, input, collection);
    }

    public static boolean startsWithStringOneByOne(String input, String[] collection) {
        return startsWithStringOneByOne(input, List.of(collection));
    }

    public static boolean startsWithStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::startsWith, input, collection);
    }

    public static boolean endsWithStringOneByOne(String input, String[] collection) {
        return endsWithStringOneByOne(input, List.of(collection));
    }

    public static boolean endsWithStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::endsWith, input, collection);
    }

    public static boolean matchesStringOneByOne(String input, String[] collection) {
        return matchesStringOneByOne(input, List.of(collection));
    }

    public static boolean matchesStringOneByOne(String input, Collection<String> collection) {
        return predicateStringOneByOne(String::matches, input, collection);
    }

    private static boolean predicateStringOneByOne(BiPredicate<String, String> predicate,
                                                   String input,
                                                   Collection<String> collection) {
        if (collection != null && collection.size() > 0) {
            for (String string : collection) {
                if (input.contains(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为ASCII字符串，ASCII字符位于0~127之间
     *
     * @param input 被检查的字符处
     * @return true表示为ASCII字符串，否则为false
     */
    public static boolean asciiString(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.codePointAt(i) > 128) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通配符匹配
     *
     * @param string  待匹配字符串
     * @param pattern 通配符模板字符串
     * @return 匹配结果
     */
    public static boolean matchWildcard(String string, String pattern) {
        final int sLen = string.length(), pLen = pattern.length();
        int si = 0, pi = 0, pci = -1;
        while (si < sLen) {
            if (pi < pLen) {
                char pc = pattern.charAt(pi);
                if (pc == '*') {
                    pci = pi;
                    pi++;
                } else if (pc == '?' || string.charAt(si) == pc) {
                    si++;
                    pi++;
                } else {
                    if (pci == -1) {
                        return false;
                    }
                    pi = pci + 1;
                    si += 1;
                }
            } else {
                if (pci == -1) {
                    return false;
                }
                pi = pci + 1;
                si += 1;
            }
        }
        while (pi < pLen && pattern.charAt(pi) == '*') {
            pi++;
        }
        return pi == pLen;
    }

    /**
     * check if all the characters of a string are same
     *
     * @param input the string to check
     * @return {@code true} if all characters of a string are same, otherwise {@code false}
     */
    public static boolean allCharSame(String input) {
        for (int i = 1, length = input.length(); i < length; ++i) {
            if (input.charAt(i) != input.charAt(0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determine if the given string are equal, returning {@code true} if both are {@code null} or
     * {@code false} if only one is {@code null}.
     *
     * @param s1 first String to compare
     * @param s2 second String to compare
     * @return whether the given objects are equal
     */
    public static boolean equalsString(String s1, String s2, boolean isIgnoredCase) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return (isIgnoredCase && s1.equalsIgnoreCase(s2)) || s1.equals(s2);
    }

    /**
     * 判断String是否是整数<br>
     * 支持10、16进制
     *
     * @param input String
     * @return 是否为整数
     */
    public static boolean integerNumber(String input) {
        boolean[] numberString = numberString(input);
        return numberString[0] && !numberString[1];
    }

    /**
     * 判断String是否是整数<br>
     * 支持10、16进制
     *
     * @param input String
     * @return 是否为整数
     */
    public static boolean integerNumberForHuman(String input) {
        boolean[] numberString = numberString(input);
        return numberString[0] && !numberString[1] && !numberString[2];
    }

    /**
     * 判断字符串是否是浮点数
     *
     * @param input String
     * @return 是否为{@link Double}类型
     */
    public static boolean doubleNumber(String input) {
        boolean[] numberString = numberString(input);
        return numberString[0] && numberString[1];
    }

    /**
     * 判断字符串是否是浮点数
     *
     * @param input String
     * @return 是否为{@link Double}类型
     */
    public static boolean doubleNumberForHuman(String input) {
        boolean[] numberString = numberString(input);
        return numberString[0] && !numberString[1] && !numberString[2];
    }

    /**
     * 是否为数字
     *
     * @param input 字符串值
     * @return 是否为数字
     */
    public static boolean number(String input) {
        return numberString(input)[0];
    }

    /**
     * 是否为人类易懂数字，排除16进制
     *
     * @param input 字符串值
     * @return 是否为数字
     */
    public static boolean numberForHuman(String input) {
        boolean[] numberString = numberString(input);
        return numberString[0] && !numberString[2];
    }

    /**
     * 是否为数字
     *
     * @param input 字符串值
     * @return 是否为数字
     */
    private static boolean[] numberString(String input) {
        // 0 isNumber 1 isDecimal 2 isHex
        boolean[] error = {false, false, false};
        if (blank(input)) {
            return error;
        }
        char[] chars = input.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && chars[start + 1] == 'x') {
                int i = start + 2;
                if (i == sz) {
                    // input == "0x"
                    return error;
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9')
                            && (chars[i] < 'a' || chars[i] > 'f')
                            && (chars[i] < 'A' || chars[i] > 'F')) {
                        return error;
                    }
                }
                return new boolean[]{true, false, true};
            }
        }
        sz--; // don't want to loopExecute to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loopExecute to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;
            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return error;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return error;
                }
                if (!foundDigit) {
                    return error;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return error;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return error;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return new boolean[]{true, hasDecPoint, false};
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return error;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return error;
                }
                // single trailing decimal point after non-exponent is ok
                return new boolean[]{foundDigit, true, false};
            }
            if (!allowSigns
                    && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return new boolean[]{foundDigit, hasDecPoint, false};
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return new boolean[]{foundDigit && !hasExp, hasDecPoint, false};
            }
            // last character is illegal
            return error;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return new boolean[]{!allowSigns && foundDigit, hasDecPoint, false};
    }

    /**
     * 是否包含换行符
     *
     * @param charSequence 字符串
     * @return 如果包含换行符则返回 {@code true}，否则 {@code false}
     */
    public static boolean containLineSeparator(CharSequence charSequence) {
        if (charSequence == null) {
            return false;
        }
        int length = charSequence.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char input = charSequence.charAt(i);
            if (input == '\r' || input == '\n') {
                return true;
            }
        }
        return false;
    }

    /**
     * 主字符串是否以什么开头
     *
     * @param main          主字符串
     * @param input         比较字符串
     * @param isIgnoredCase 是否忽略大小写
     * @return 如果是以什么开头返回true，否则返回发false
     */
    public static boolean startsWithAndCase(String main, String input, boolean isIgnoredCase) {
        if (main == null || input == null) {
            return false;
        }
        int ml = main.length();
        int il = input.length();
        if (ml < il) {
            return false;
        }
        return main.regionMatches(isIgnoredCase, 0, input, 0, il);
    }

    /**
     * 主字符串是否以什么结尾
     *
     * @param main          主字符串
     * @param input         比较字符串
     * @param isIgnoredCase 是否忽略大小写
     * @return 如果是以什么结尾返回true，否则返回发false
     */
    public static boolean endsWithAndCase(String main, String input, boolean isIgnoredCase) {
        if (main == null || input == null) {
            return false;
        }
        int ml = main.length();
        int il = input.length();
        if (ml < il) {
            return false;
        }
        return main.regionMatches(isIgnoredCase, ml - il, input, 0, il);
    }

    /**
     * 给定字符串中的字母是否全部为大写，判断依据如下：
     *
     * <pre>
     * 1. 大写字母包括A-Z
     * 2. 其它非字母的Unicode符都算作大写
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 是否全部为大写
     */
    public static boolean upperCase(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 给定字符串中的字母是否全部为小写，判断依据如下：
     *
     * <pre>
     * 1. 小写字母包括a-z
     * 2. 其它非字母的Unicode符都算作小写
     * </pre>
     *
     * @param str 被检查的字符串
     * @return 是否全部为小写
     */
    public static boolean lowerCase(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------------------

    /**
     * Return whether the given throwable is a checked exception: that is, neither a RuntimeException
     * nor an Error.
     *
     * @param ex the throwable to check
     * @return whether the throwable is a checked exception
     * @see Exception
     * @see RuntimeException
     * @see Error
     */
    public static boolean checkedException(Throwable ex) {
        return !(ex instanceof RuntimeException || ex instanceof Error);
    }

    // ---------------------------------------------------------------------------

    /**
     * Determine if the given objects are equal, returning {@code true} if both are {@code null} or
     * {@code false} if only one is {@code null}.
     *
     * <p>Compares arrays with {@code Arrays.equals}, performing an equality check based on the array
     * elements rather than the array reference.
     *
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see Object#equals(Object)
     * @see Arrays#equals
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * Compare the given arrays with {@code Arrays.equals}, performing an equality check based on the
     * array elements rather than the array reference.
     *
     * @param o1 first array to compare
     * @param o2 second array to compare
     * @return whether the given objects are equal
     * @see #equals(Object, Object)
     * @see Arrays#equals
     */
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }

    /**
     * Check whether the given array contains the given element.
     *
     * @param array   the array to check (may be {@code null}, in which case the return value will
     *                always be {@code false})
     * @param element the element to check for
     * @return whether the element has been found in the given array
     */
    public static boolean containsElement(Object[] array, Object element) {
        if (array == null) {
            return false;
        }
        for (Object arrayEle : array) {
            if (equals(arrayEle, element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether the given array of enum constants contains a constant with the given name,
     * ignoring case when determining a match.
     *
     * @param enumValues the enum values to check, typically obtained via {@code MyEnum.values()}
     * @param constant   the constant name to find (must not be null or empty string)
     * @return whether the constant has been found in the given array
     */
    public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
        return containsConstant(enumValues, constant, false);
    }

    /**
     * Check whether the given array of enum constants contains a constant with the given name.
     *
     * @param enumValues    the enum values to check, typically obtained via {@code MyEnum.values()}
     * @param constant      the constant name to find (must not be null or empty string)
     * @param caseSensitive whether case is significant in determining a match
     * @return whether the constant has been found in the given array
     */
    public static boolean containsConstant(
            Enum<?>[] enumValues, String constant, boolean caseSensitive) {
        for (Enum<?> candidate : enumValues) {
            if (caseSensitive
                    ? candidate.toString().equals(constant)
                    : candidate.toString().equalsIgnoreCase(constant)) {
                return true;
            }
        }
        return false;
    }

    public static boolean windowsSystem() {
        return JavaEnvironment.OS_NAME.contains("Windows");
    }

    public static boolean linuxSystem() {
        return JavaEnvironment.OS_NAME.contains("Linux");
    }
}
