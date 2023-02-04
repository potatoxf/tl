package pxf.tl.text.cron.pattern.matcher;


import pxf.tl.util.ToolString;

/**
 * 所有值匹配，始终返回{@code true}
 *
 * @author potatoxf
 */
public class AlwaysTrueMatcher implements PartMatcher {

    public static AlwaysTrueMatcher INSTANCE = new AlwaysTrueMatcher();

    @Override
    public boolean test(Integer t) {
        return true;
    }

    @Override
    public int nextAfter(int value) {
        return value;
    }

    @Override
    public String toString() {
        return ToolString.format("[Matcher]: always true.");
    }
}
