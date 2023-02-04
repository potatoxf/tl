package pxf.tl.database.type;

/**
 * @author potatoxf
 */
public abstract class DatabaseDataType {
    /**
     * 数据库类型
     */
    private final String type;
    /**
     * java类型
     */
    private final Class<?> javaType;
    /**
     * 最小长度
     */
    private final int minLimitLength;
    /**
     * 最大长度
     */
    private final int maxLimitLength;
    /**
     * 最大精度
     */
    private final int maxLimitAccuracy;

    public DatabaseDataType(String type, Class<?> javaType) {
        this(type, javaType, -1, -1);
    }

    public DatabaseDataType(String type, Class<?> javaType, int maxLimitLength) {
        this(type, javaType, 1, maxLimitLength, -1);
    }

    public DatabaseDataType(String type, Class<?> javaType, int minLimitLength, int maxLimitLength) {
        this(type, javaType, minLimitLength, maxLimitLength, -1);
    }

    public DatabaseDataType(
            String type,
            Class<?> javaType,
            int minLimitLength,
            int maxLimitLength,
            int maxLimitAccuracy) {
        assert javaType != null : "The java type must no null";
        assert minLimitLength > 0 || minLimitLength == -1 : "The min limit len must be greater 0";
        assert maxLimitLength > minLimitLength || maxLimitLength == -1
                : "The max limit len must be greater min limit len";
        assert maxLimitAccuracy >= 0 && maxLimitLength > maxLimitAccuracy || maxLimitAccuracy == -1
                : "The min limit accuracy must greater 0 and lesser max limit len";
        this.type = type;
        this.javaType = javaType;
        this.minLimitLength = minLimitLength;
        this.maxLimitLength = maxLimitLength;
        this.maxLimitAccuracy = maxLimitAccuracy;
    }

    /**
     * 生成数据库类型字符串
     *
     * @param length   长度
     * @param accuracy 精度
     * @return 返回数据类型字符串
     */
    public String generateTypeString(int length, int accuracy) {
        length = validLength(length);
        accuracy = validAccuracy(length, accuracy);
        if (length <= 0) {
            return type;
        } else {
            if (accuracy <= 0) {
                return type + "(" + length + ")";
            } else {

                return type + "(" + length + "," + accuracy + ")";
            }
        }
    }

    /**
     * 获取java类型
     *
     * @return {@code Class<?>}
     */
    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     * 是否是正确的类型
     *
     * @param data 数据
     * @return 如果是正确的类型返回true，否则返回false
     */
    public boolean isCorrectType(Object data) {
        return javaType.isInstance(data);
    }

    @Override
    public String toString() {
        return type;
    }

    private int validLength(int length) {
        if (minLimitLength == -1 && maxLimitLength == -1) {
            return -1;
        } else {
            length = Math.max(length, 1);
            if (minLimitLength == -1) {
                return Math.min(length, maxLimitLength);
            } else if (maxLimitLength == -1) {
                return Math.max(length, minLimitLength);
            } else {
                if (length >= minLimitLength && length <= maxLimitLength) {
                    return length;
                } else if (length < minLimitLength) {
                    return minLimitLength;
                } else {
                    return maxLimitLength;
                }
            }
        }
    }

    private int validAccuracy(int length, int accuracy) {
        if (minLimitLength == -1 && maxLimitLength == -1) {
            return -1;
        } else {
            return Math.min(Math.min(maxLimitAccuracy, Math.max(accuracy, 0)), length - 1);
        }
    }
}
