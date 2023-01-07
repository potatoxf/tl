package pxf.tl.lang;


import pxf.tl.util.ToolObject;

import java.util.Objects;

/**
 * @author potatoxf
 */
public class HashValue implements Comparable<HashValue> {
    protected int hashcode;
    protected Object value;

    public HashValue(int hashcode, Object value) {
        this.hashcode = hashcode;
        this.value = value;
    }

    public int getHashcode() {
        return hashcode;
    }

    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 是否Key相等
     *
     * @param object 对象
     * @param other  新对象
     * @return 如果相等返回 {@code true}，否则 {@code false}
     */
    protected boolean isEquals(Object object, Object other) {
        if (other != null && object != null) {
            if (object instanceof String && other instanceof String) {
                return ((String) object).equalsIgnoreCase((String) other);
            } else {
                return object.equals(other);
            }
        }
        return false;
    }

    @Override
    public int compareTo(HashValue other) {
        Integer dir;
        if (hashcode > other.hashcode) {
            return 1;
        } else if (hashcode < other.hashcode) {
            return -1;
        } else if (isEquals(value, other.value)) {
            return 0;
        } else if ((dir = ToolObject.compareCompared(value, other.value, false)) != null) {
            return dir;
        } else {
            return ToolObject.compareSameHashcode(hashcode, value, other.value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashValue hashValue = (HashValue) o;

        if (hashcode != hashValue.hashcode) return false;
        return Objects.equals(value, hashValue.value);
    }

    @Override
    public int hashCode() {
        int result = hashcode;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
