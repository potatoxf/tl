package pxf.tl.api;

/**
 * @author potatoxf
 */
public interface PoolOfCharacter {
    /**
     * 特殊字符--> {@code \b}
     */
    char SC_B = 'b';
    /**
     * 特殊字符--> {@code \f}
     */
    char SC_F = 'f';
    /**
     * 特殊字符--> {@code \n}
     */
    char SC_N = 'n';
    /**
     * 特殊字符--> {@code \r}
     */
    char SC_R = 'r';
    /**
     * 特殊字符--> {@code \t}
     */
    char SC_T = 't';
    /**
     * 字符常量：空格符 {@code ' '}
     */
    char SPACE = ' ';
    /**
     * 字符常量：制表符 {@code '\t'}
     */
    char TAB = '	';
    /**
     * 字符常量：点 {@code '.'}
     */
    char DOT = '.';
    /**
     * 字符常量：斜杠 {@code '/'}
     */
    char SLASH = '/';
    /**
     * 字符常量：反斜杠 {@code '\\'}
     */
    char BACKSLASH = '\\';
    /**
     * 字符常量：回车符 {@code '\r'}
     */
    char CR = '\r';
    /**
     * 字符常量：换行符 {@code '\n'}
     */
    char LF = '\n';
    /**
     * 单引号
     */
    char QUOTE = '\'';
    /**
     * 双引号
     */
    char QUOTES = '"';
    /**
     * 字符常量：减号（连接符） {@code '-'}
     */
    char DASHED = '-';
    /**
     * 字符常量：下划线 {@code '_'}
     */
    char UNDERLINE = '_';
    /**
     * 字符常量：逗号 {@code ','}
     */
    char COMMA = ',';
    /**
     * 字符常量：花括号（左） <code>'{'</code>
     */
    char DELIM_START = '{';
    /**
     * 字符常量：花括号（右） <code>'}'</code>
     */
    char DELIM_END = '}';
    /**
     * 字符常量：中括号（左） {@code '['}
     */
    char BRACKET_START = '[';
    /**
     * 字符常量：中括号（右） {@code ']'}
     */
    char BRACKET_END = ']';
    /**
     * 字符常量：双引号 {@code '"'}
     */
    char DOUBLE_QUOTES = '"';
    /**
     * 字符常量：单引号 {@code '\''}
     */
    char SINGLE_QUOTE = '\'';
    /**
     * 字符常量：与 {@code '&'}
     */
    char AMP = '&';
    /**
     * 字符常量：冒号 {@code ':'}
     */
    char COLON = ':';
    /**
     * 字符常量：艾特 {@code '@'}
     */
    char AT = '@';
    /**
     * Ascii 小写字母编码上界
     */
    int ASCII_LOWER_LETTER_HI = 122;
    /**
     * Ascii 小写字母编码下界
     */
    int ASCII_LOWER_LETTER_LO = 97;
    /**
     * Ascii 数字编码上界
     */
    int ASCII_NUMBER_HI = 57;
    /**
     * Ascii 数字编码下界
     */
    int ASCII_NUMBER_LO = 48;
    /**
     * Ascii 大写字母编码上界
     */
    int ASCII_UPPER_LETTER_HI = 90;
    /**
     * Ascii 大写字母编码下界
     */
    int ASCII_UPPER_LETTER_LO = 65;
    /**
     * 大写HEX
     */
    char[] UPPER_HEX =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    /**
     * 小写HEX
     */
    char[] LOWER_HEX =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
