package pxf.tl.lang;


import pxf.tl.iter.AnyIter;
import pxf.tl.util.ToolString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.function.BiFunction;

/**
 * @author potatoxf
 */
public class SwitchPermissions {

    /**
     *
     */
    private static final char[] RADIX_MAX_VALUE =
            new char[]{
                    ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
                    'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                    'Z'
            };

    /**
     *
     */
    private final int maxLength;

    /**
     *
     */
    private final int radix;

    /**
     *
     */
    private final BigInteger maxSize;

    /**
     * @param maxLength
     * @param radix
     */
    public SwitchPermissions(int maxLength, int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
            throw new NumberFormatException("Radix out of range");
        if (maxLength <= 0) throw new NumberFormatException("Zero length BigInteger");
        this.maxLength = maxLength;
        this.radix = radix;
        String maxSizeStr =
                ToolString.repeat(
                                TextBuilder.of(maxLength),
                                RADIX_MAX_VALUE[radix - 1],
                                maxLength)
                        .toString();
        System.out.println(maxSizeStr);
        this.maxSize = new BigInteger(maxSizeStr, radix);
    }

    /**
     * @param range
     * @param permissionValue
     * @param permissions
     * @return
     */
    @Nonnull
    public String settingPermissions(
            @Nullable String range, @Nonnull String permissionValue, @Nonnull int... permissions) {
        return compute(
                range,
                permissionValue,
                BigInteger::setBit,
                () -> AnyIter.of(true, null, permissions));
    }

    /**
     * @param range
     * @param permissionValue
     * @param permissions
     * @return
     */
    @Nonnull
    public String settingPermissions(
            @Nullable String range,
            @Nonnull String permissionValue,
            @Nonnull Iterable<Integer> permissions) {
        return compute(range, permissionValue, BigInteger::setBit, permissions);
    }

    /**
     * @param range
     * @param permissionValue
     * @param permissions
     * @return
     */
    @Nonnull
    public String cancelPermissions(
            @Nullable String range, @Nonnull String permissionValue, @Nonnull int... permissions) {
        return compute(
                range,
                permissionValue,
                BigInteger::clearBit,
                () -> AnyIter.of(true, null, permissions));
    }

    /**
     * @param range
     * @param permissionValue
     * @param permissions
     * @return
     */
    @Nonnull
    public String cancelPermissions(
            @Nullable String range,
            @Nonnull String permissionValue,
            @Nonnull Iterable<Integer> permissions) {
        return compute(range, permissionValue, BigInteger::clearBit, permissions);
    }

    /**
     * @param range
     * @param permissionValue
     * @return
     */
    @Nonnull
    public String limitPermission(@Nullable String range, @Nonnull String permissionValue) {
        return compute(range, permissionValue, BigInteger::and);
    }

    /**
     * @param range
     * @param permissionValue
     * @return
     */
    @Nonnull
    public String extendPermission(@Nullable String range, @Nonnull String permissionValue) {
        return compute(range, permissionValue, BigInteger::or);
    }

    public boolean hasPermissions(@Nonnull String permissionValue, @Nonnull int... permissions) {
        return hasPermissions(permissionValue, () -> AnyIter.of(true, null, permissions));
    }

    /**
     * @param permissionValue
     * @param permissions
     * @return
     */
    public boolean hasPermissions(
            @Nonnull String permissionValue, @Nonnull Iterable<Integer> permissions) {
        BigInteger permissionValueNumber = createBigInteger(permissionValue);
        for (Integer permission : permissions) {
            if (permission == null || permission < 0 || permission > maxLength) {
                return false;
            }
            if (!permissionValueNumber.testBit(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param range
     * @param permissionValue
     * @param function
     * @param permissions
     * @return
     */
    @Nonnull
    private String compute(
            String range,
            String permissionValue,
            BiFunction<BigInteger, Integer, BigInteger> function,
            Iterable<Integer> permissions) {
        BigInteger permissionValueNumber = createBigInteger(permissionValue);
        if (range == null) {
            for (int permission : permissions) {
                checkPermission(null, permission);
                permissionValueNumber = function.apply(permissionValueNumber, permission);
            }
        } else {
            final BigInteger rangeNumber = createBigInteger(permissionValue);
            for (int permission : permissions) {
                checkPermission(rangeNumber, permission);
                permissionValueNumber = function.apply(permissionValueNumber, permission);
            }
        }
        return permissionValueNumber.toString(radix);
    }

    /**
     * @param range
     * @param permissionValue
     * @param computeFunction
     * @return
     */
    @Nonnull
    private String compute(
            String range,
            String permissionValue,
            BiFunction<BigInteger, BigInteger, BigInteger> computeFunction) {
        BigInteger permissionValueNumber = createBigInteger(permissionValue);
        if (range == null) {
            return permissionValueNumber.toString(radix);
        } else {
            return computeFunction.apply(createBigInteger(range), permissionValueNumber).toString(radix);
        }
    }

    /**
     * @param rangeNumber
     * @param permission
     */
    private void checkPermission(BigInteger rangeNumber, int permission) {
        if (permission < 0) {
            throw new IllegalArgumentException();
        }
        if (permission > maxLength) {
            throw new IllegalArgumentException();
        }
        if (rangeNumber != null && !rangeNumber.testBit(permission)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param number
     * @return
     */
    private BigInteger createBigInteger(String number) {
        final BigInteger numberValue = new BigInteger(number, radix);
        if (numberValue.subtract(maxSize).longValue() > 0) {
            throw new IllegalArgumentException();
        }
        return numberValue;
    }
}
