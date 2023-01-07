package pxf.tl.lang;

import java.math.RoundingMode;

/**
 * 除法环境
 *
 * @author potatoxf
 */
public final class DivideContext {
    public static final DivideContext DEFAULT = DivideContext.build(2, RoundingMode.HALF_UP);
    public static final DivideContext PRECISE = DivideContext.build(4, RoundingMode.HALF_UP);
    private final int scale;
    private final RoundingMode roundingMode;

    private DivideContext(int scale, RoundingMode roundingMode) {
        this.scale = scale;
        this.roundingMode = roundingMode;
    }

    public static DivideContext of(int scale, RoundingMode roundingMode) {
        if (roundingMode == RoundingMode.HALF_UP) {
            if (scale == 2) {
                return DEFAULT;
            } else if (scale == 4) {
                return PRECISE;
            }
        }
        return build(scale, roundingMode);
    }

    private static DivideContext build(int scale, RoundingMode roundingMode) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be greater 0 or equal");
        }
        if (roundingMode == null) {
            throw new IllegalArgumentException("The RoundingMode must be no null");
        }
        return new DivideContext(scale, roundingMode);
    }

    public int getScale() {
        return scale;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    @Override
    public int hashCode() {
        int result = scale;
        result = 31 * result + roundingMode.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DivideContext that = (DivideContext) o;

        if (scale != that.scale) {
            return false;
        }
        return roundingMode == that.roundingMode;
    }
}
