package pxf.tl.lang;

import java.util.Objects;

/**
 * 通用引用键
 *
 * @author potatoxf
 */
public class CommonKey {
    private final String module;
    private final String type;
    private final String name;

    public CommonKey(String module, String type, String name) {
        this.module = module;
        this.type = type;
        this.name = name;
    }

    public String getModule() {
        return module;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonKey)) return false;
        CommonKey that = (CommonKey) o;
        return Objects.equals(module, that.module)
                && Objects.equals(type, that.type)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, type, name);
    }

    @Override
    public String toString() {
        return "[" + module + "]" + "(" + type + ")" + "<" + name + ">";
    }
}
