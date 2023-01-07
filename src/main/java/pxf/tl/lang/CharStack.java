package pxf.tl.lang;

import pxf.tl.api.Sized;

/**
 * 字符栈
 *
 * @author potatoxf
 */
public class CharStack implements Sized {

    private final StringBuilder stack;

    public CharStack() {
        this(50);
    }

    public CharStack(int capacity) {
        stack = new StringBuilder(capacity);
    }

    /**
     * 返回栈中元素个数
     *
     * @return 栈中元素个数
     */
    public int size() {
        return stack.length();
    }

    /**
     * 进栈
     *
     * @param c 字符
     */
    public void push(char c) {
        stack.append(c);
    }

    /**
     * 出栈
     *
     * @return 字符，如果栈为空则返回 {@code null}
     */
    public Character pop() {
        Character top = peek();
        if (top == null) {
            return null;
        }
        stack.setLength(stack.length() - 1);
        return top;
    }

    /**
     * 获取栈顶字符
     *
     * @return 如果为空则为 {@code null}
     */
    public Character peek() {
        if (stack.length() == 0) {
            return null;
        }
        return stack.charAt(stack.length() - 1);
    }

    /**
     * 清理字符串
     *
     * @return 返回栈内的字符串
     */
    public String clear() {
        String string = stack.toString();
        stack.setLength(0);
        return string;
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
