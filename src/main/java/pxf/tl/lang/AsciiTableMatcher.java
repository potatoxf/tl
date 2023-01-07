package pxf.tl.lang;

/**
 * ASCII字符表匹配器
 *
 * @author potatoxf
 */
public final class AsciiTableMatcher {

    /**
     * 四则运算符号，{@code +-}
     */
    public static final int ARITHMETIC_ADD_SUB = 0x00000200;
    /**
     * 四则运算符号，{@code /+-*}
     */
    public static final int ARITHMETIC_CHAR = 0x00000400;
    /**
     * 四则运算符号，{@code /*}
     */
    public static final int ARITHMETIC_MUL_DIV = 0x00000100;
    /**
     * 反斜杠
     */
    public static final int BACKSLASH = 0x00080000;
    // ---------------------------------------------------------------------------
    /**
     * 空白符 ‘ ’，‘\t’
     */
    public static final int BLANK = 0x02000000;
    /**
     * 括号 {@code >)]}} {@code <([{}
     */
    public static final int BRACKETS = 0x00004000;
    /**
     * 比较操作符号，@{@code < > =}
     */
    public static final int COMPARISON_OPERATOR = 0x00000800;
    /**
     * 控制字符
     */
    public static final int CONTROL_CHAR = 0x40000000;
    // ---------------------------------------------------------------------------
    /**
     * 10进制
     */
    public static final int DEC = 0x00000020;
    /**
     * 一般分割符
     */
    public static final int DELIMITER = 0x00010000;
    /**
     * 设备符号
     */
    public static final int DEVICE_CHAR = 0x20000000;
    /**
     * 16进制
     */
    public static final int HEX = 0x00000040;
    // ---------------------------------------------------------------------------
    /**
     * 不可见字符 ‘ ’，‘\t’，‘\r’，‘\n’，‘\f’
     */
    public static final int INVISIBLE_CHAR = 0x08000000;
    /**
     * Java关键字
     */
    public static final int JAVA_KEYWORD_CHAR = 0x10000000;
    /**
     * 左括号 {@code <([{}
     */
    public static final int LEFT_BRACKETS = 0x00001000;
    // ---------------------------------------------------------------------------
    /**
     * 字母
     */
    public static final int LETTER = 0x00000004;
    /**
     * 小写字符
     */
    public static final int LOWER_LETTER = 0x00000001;
    /**
     * 回车换行符 ‘\r’，‘\n’
     */
    public static final int LR_LF = 0x04000000;
    /**
     * 不允许出现在文件名中的可见字符
     */
    public static final int NOT_IN_FILENAME = 0x00200000;
    // ---------------------------------------------------------------------------
    /**
     * 普通数字
     */
    public static final int NUM = 0x00000080;
    /**
     * 8进制
     */
    public static final int OCT = 0x00000010;
    /**
     * 一般键值分割符
     */
    public static final int PAIR_DELIMITER = 0x00020000;
    // ---------------------------------------------------------------------------
    /**
     * 路径分割符
     */
    public static final int PATH_SEPARATOR = 0x00100000;
    /**
     * 标点符号
     */
    public static final int PUNCTUATION = 0x01000000;
    /**
     * 引号
     */
    public static final int QUOTE = 0x00400000;
    /**
     * 右括号 {@code >)]}}
     */
    public static final int RIGHT_BRACKETS = 0x00002000;
    // ---------------------------------------------------------------------------
    /**
     * 斜杠
     */
    public static final int SLASH = 0x00040000;
    /**
     * 大写字符
     */
    public static final int UPPER_LETTER = 0x00000002;
    /**
     * 通配符
     */
    public static final int WILDCARD = 0x00000008;
    /**
     * ascii表
     *
     * @see #isMatcherExceptAsciiChar(int, int)
     * @see #isMatcherExceptAsciiChars(int, int...)
     */
    private static final int[] ASCII_TABLE = {
            // ---------------------------------------------------------------------------
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            // BS
            CONTROL_CHAR,
            // TAB
            INVISIBLE_CHAR | BLANK,
            // LF
            INVISIBLE_CHAR | LR_LF,
            // VI
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            // FF
            INVISIBLE_CHAR | BLANK,
            // CR
            INVISIBLE_CHAR | LR_LF,
            // SO
            CONTROL_CHAR,
            // SI
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            CONTROL_CHAR,
            DEVICE_CHAR,
            DEVICE_CHAR,
            DEVICE_CHAR,
            // ---------------------------------------------------------------------------
            DEVICE_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            CONTROL_CHAR,
            // ---------------------------------------------------------------------------
            // WHITESPACE
            INVISIBLE_CHAR | BLANK,
            // !
            PUNCTUATION,
            // "
            PUNCTUATION | QUOTE | NOT_IN_FILENAME,
            // #
            PUNCTUATION,
            // ---------------------------------------------------------------------------
            // $
            PUNCTUATION | JAVA_KEYWORD_CHAR,
            // %
            PUNCTUATION,
            // &
            PUNCTUATION,
            // '
            PUNCTUATION | QUOTE,
            // ---------------------------------------------------------------------------
            // (
            PUNCTUATION | BRACKETS | LEFT_BRACKETS,
            // )
            PUNCTUATION | BRACKETS | RIGHT_BRACKETS,
            // *
            PUNCTUATION | ARITHMETIC_CHAR | ARITHMETIC_MUL_DIV | WILDCARD | NOT_IN_FILENAME,
            // +
            PUNCTUATION | ARITHMETIC_CHAR | ARITHMETIC_ADD_SUB | NUM,
            // ---------------------------------------------------------------------------
            // ,
            PUNCTUATION | DELIMITER,
            // -
            PUNCTUATION | ARITHMETIC_CHAR | ARITHMETIC_ADD_SUB | NUM,
            // .
            PUNCTUATION | NUM,
            // /
            PUNCTUATION | PATH_SEPARATOR | SLASH | ARITHMETIC_CHAR | ARITHMETIC_MUL_DIV | NOT_IN_FILENAME,
            // ---------------------------------------------------------------------------
            // 0
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 1
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 2
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 3
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // ---------------------------------------------------------------------------
            // 4
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 5
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 6
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // 7
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX | OCT,
            // ---------------------------------------------------------------------------
            // 8
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX,
            // 9
            NUM | DEC | JAVA_KEYWORD_CHAR | HEX,
            // :
            PUNCTUATION | PAIR_DELIMITER | NOT_IN_FILENAME,
            // ;
            PUNCTUATION,
            // ---------------------------------------------------------------------------
            // <
            PUNCTUATION | BRACKETS | LEFT_BRACKETS | NOT_IN_FILENAME | COMPARISON_OPERATOR,
            // =
            PUNCTUATION | PAIR_DELIMITER | COMPARISON_OPERATOR,
            // >
            PUNCTUATION | BRACKETS | RIGHT_BRACKETS | NOT_IN_FILENAME | COMPARISON_OPERATOR,
            // ?
            PUNCTUATION | WILDCARD | NOT_IN_FILENAME,
            // ---------------------------------------------------------------------------
            // @
            PUNCTUATION,
            // A
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // B
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // C
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // ---------------------------------------------------------------------------
            // D
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // E
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // F
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // G
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // H
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // I
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // J
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // K
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // L
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // M
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // N
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // O
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // P
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // Q
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // R
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // BuiltIn
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // T
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // U
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // V
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // W
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // X
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // Y
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // Z
            LETTER | UPPER_LETTER | JAVA_KEYWORD_CHAR,
            // [
            PUNCTUATION | BRACKETS | LEFT_BRACKETS,
            // ---------------------------------------------------------------------------
            // \
            PUNCTUATION | PATH_SEPARATOR | BACKSLASH | NOT_IN_FILENAME,
            // ]
            PUNCTUATION | BRACKETS | RIGHT_BRACKETS,
            // ^
            PUNCTUATION,
            // _
            PUNCTUATION | JAVA_KEYWORD_CHAR,
            // ---------------------------------------------------------------------------
            // `
            PUNCTUATION,
            // a
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // b
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // c
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // 100-103
            // d
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // e
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // f
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR | HEX,
            // g
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // 104-107
            // h
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // i
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // j
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // k
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // 108-111
            // l
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // m
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // n
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // o
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // 112-115
            // p
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // q
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // r
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // s
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // 116-119
            // t
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // u
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // v
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // w
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // 120-123
            // x
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // y
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // z
            LETTER | LOWER_LETTER | JAVA_KEYWORD_CHAR,
            // {
            PUNCTUATION | BRACKETS | LEFT_BRACKETS,
            // 124-127
            // |
            PUNCTUATION | DELIMITER | NOT_IN_FILENAME,
            // }
            PUNCTUATION | BRACKETS | RIGHT_BRACKETS,
            // ~
            PUNCTUATION,
            // Backspace
            CONTROL_CHAR
    };

    private AsciiTableMatcher() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 判读是否匹配期望Ascii字符
     *
     * @param pointCode        码点
     * @param exceptAsciiChars 期待元素，{@link #ASCII_TABLE}
     * @return 如果符合返回 {@code true}，否则 {@code false}
     */
    public static boolean isMatcherExceptAsciiChars(int pointCode, int... exceptAsciiChars) {
        for (int expect : exceptAsciiChars) {
            if (isMatcherExceptAsciiChar(pointCode, expect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判读是否属于期望Ascii字符
     *
     * @param pointCode       码点
     * @param exceptAsciiChar 期待元素，{@link #ASCII_TABLE}
     * @return 如果符合返回 {@code true}，否则 {@code false}
     */
    public static boolean isMatcherExceptAsciiChar(int pointCode, int exceptAsciiChar) {
        if (isNoAsciiChar(pointCode)) {
            return false;
        }
        int value = ASCII_TABLE[pointCode];
        int andValue = value & exceptAsciiChar;
        // 与期待值相等
        return andValue == exceptAsciiChar;
    }

    /**
     * 是否不是Ascii字符
     *
     * @param pointCode 码点
     * @return 如果是ascii返回 {@code true}，否则 {@code false}
     */
    public static boolean isNoAsciiChar(int pointCode) {
        return !isAsciiChar(pointCode);
    }

    /**
     * 是否是Ascii字符
     *
     * @param pointCode 码点
     * @return 如果是ascii返回 {@code true}，否则 {@code false}
     */
    public static boolean isAsciiChar(int pointCode) {
        return pointCode > 0 && pointCode < ASCII_TABLE.length;
    }
}
