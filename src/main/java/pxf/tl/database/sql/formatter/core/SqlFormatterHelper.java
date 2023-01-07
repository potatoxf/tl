package pxf.tl.database.sql.formatter.core;


import pxf.tl.help.Whether;
import pxf.tl.util.ToolRegex;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlFormatterHelper {
    public static final String BACK_QUOTE = "``";
    public static final String DOUBLE_QUOTE = "\"\"";
    public static final String U_DOUBLE_QUOTE = "U&\"\"";
    public static final String U_SINGLE_QUOTE = "U&''";
    public static final String E_SINGLE_QUOTE = "E''";
    public static final String N_SINGLE_QUOTE = "N''";
    public static final String Q_SINGLE_QUOTE = "Q''";
    public static final String SINGLE_QUOTE = "''";
    public static final String BRACE = "{}";
    public static final String DOLLAR = "$$";
    public static final String BRACKET = "[]";
    public static final Map<String, String> LITERALS = new HashMap<>();

    public static String createOperatorRegex(JSLikeList<String> multiLetterOperators) {
        return String.format(
                "^(%s|.)",
                sortByLengthDesc(multiLetterOperators).map(ToolRegex::escapeRegExp).join("|"));
    }

    public static String createLineCommentRegex(JSLikeList<String> lineCommentTypes) {
        return String.format(
                "^((?:%s).*?)(?:\r\n|\r|\n|$)", lineCommentTypes.map(ToolRegex::escapeRegExp).join("|"));
    }

    public static String createReservedWordRegex(JSLikeList<String> reservedWords) {
        if (Whether.empty(reservedWords)) {
            return "^\b$";
        }
        String reservedWordsPattern =
                sortByLengthDesc(reservedWords).join("|").replaceAll(" ", "\\\\s+");
        return "(?i)" + "^(" + reservedWordsPattern + ")\\b";
    }

    public static String createWordRegex(JSLikeList<String> specialChars) {
        return "^([\\p{IsAlphabetic}\\p{Mc}\\p{Me}\\p{Mn}\\p{Nd}\\p{Pc}\\p{IsJoin_Control}"
                + specialChars.join("")
                + "]+)";
    }

    public static String createStringRegex(JSLikeList<String> stringTypes) {
        return "^(" + createStringPattern(stringTypes) + ")";
    }

    // This enables the following string patterns:
    // 1. backtick quoted string using `` to escape
    // 2. square bracket quoted string (SQL Server) using ]] to escape
    // 3. double quoted string using "" or \" to escape
    // 4. single quoted string using '' or \' to escape
    // 5. national character quoted string using N'' or N\' to escape
    public static String createStringPattern(JSLikeList<String> stringTypes) {
        return stringTypes.map(SqlFormatterHelper::getLiteral).join("|");
    }

    public static String createParenRegex(JSLikeList<String> parens) {
        return "(?i)^(" + parens.map(SqlFormatterHelper::escapeParen).join("|") + ")";
    }

    public static String escapeParen(String paren) {
        if (paren.length() == 1) {
            // A single punctuation character
            return ToolRegex.escapeRegExp(paren);
        } else {
            // longer word
            return "\\b" + paren + "\\b";
        }
    }

    public static Pattern createPlaceholderRegexPattern(JSLikeList<String> types, String pattern) {
        if (Whether.empty(types)) {
            return null;
        }
        String typesRegex = types.map(ToolRegex::escapeRegExp).join("|");

        return Pattern.compile(String.format("^((?:%s)(?:%s))", typesRegex, pattern));
    }

    public static String trimSpacesEnd(String s) {
        int endIndex = s.length();
        char[] chars = s.toCharArray();
        while (endIndex > 0 && (chars[endIndex - 1] == ' ' || chars[endIndex - 1] == '\t')) {
            endIndex--;
        }
        return new String(chars, 0, endIndex);
        // return s.replaceAll("[ \t]+$", "");
    }

    @SafeVarargs
    public static <R> R firstNotnull(Supplier<R>... sups) {
        for (Supplier<R> sup : sups) {
            R ret = sup.get();
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    @SafeVarargs
    public static <R> Optional<R> firstPresent(Supplier<Optional<R>>... sups) {
        for (Supplier<Optional<R>> sup : sups) {
            Optional<R> ret = sup.get();
            if (ret.isPresent()) {
                return ret;
            }
        }
        return Optional.empty();
    }

    public static String repeat(String s, int n) {
        return Stream.generate(() -> s).limit(n).collect(Collectors.joining());
    }

    public static <T> List<T> concat(List<T> l1, List<T> l2) {
        return Stream.of(l1, l2).flatMap(List::stream).collect(Collectors.toList());
    }

    public static JSLikeList<String> sortByLengthDesc(JSLikeList<String> strings) {
        return new JSLikeList<>(
                strings.stream()
                        .sorted(Comparator.comparingInt(String::length).reversed())
                        .collect(Collectors.toList()));
    }

    public static String getLiteral(String key) {
        return LITERALS.get(key);
    }
}
