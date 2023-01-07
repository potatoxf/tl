package pxf.tl.lang.ansi;

/**
 * ANSI可转义节点接口，实现为ANSI颜色等
 *
 * <p>来自Spring Boot
 *
 * @author potatoxf
 */
public interface AnsiElement {

    /**
     * @return ANSI转义编码
     */
    @Override
    String toString();
}
