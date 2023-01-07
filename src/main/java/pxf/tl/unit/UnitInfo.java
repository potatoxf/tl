package pxf.tl.unit;


import pxf.tl.api.IntScope;
import pxf.tl.api.This;

import java.util.Iterator;

/**
 * 单位信息
 *
 * @author potatoxf
 */
public interface UnitInfo<E extends UnitInfo<E>> extends Iterable<E>, Comparable<E>, This<E> {

    /**
     * 获取等级，从小到大依次增加，每相差一就代表两个单位相差一级
     *
     * @return 返回数字等级
     */
    int grade();

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    int nextIntervalExact();

    /**
     * 该单位与下一个大单位的间隔，等级是越来越高
     *
     * @return 与下一个大单位间隔数值
     */
    int nextIntervalHuman();

    /**
     * 单位分类
     *
     * @return {@code UnitCatalog<E>}
     */
    UnitCatalog<E> unitCatalog();

    /**
     * 获取迭代器
     *
     * @return {@code Iterator<E>}
     */
    @Override
    default Iterator<E> iterator() {
        return unitCatalog().iterator();
    }

    /**
     * 与目标的差值
     *
     * @param target {@code UnitEquallySpaced}
     * @return 返回数字等级
     */
    default int exactFactor(final E target) {
        IntScope intScope = gradeRange(target);
        int value = 1;
        if (intScope == null) {
            return value;
        }
        for (E e : this) {
            if (intScope.isInRange(e.grade())) {
                value *= e.nextIntervalExact();
            }
        }
        return value;
    }

    /**
     * 与目标的差值
     *
     * @param target {@code UnitEquallySpaced}
     * @return 返回数字等级
     */
    default int humanFactor(final E target) {
        IntScope intScope = gradeRange(target);
        int value = 1;
        if (intScope == null) {
            return value;
        }
        for (E e : this) {
            if (intScope.isInRange(e.grade())) {
                value *= e.nextIntervalHuman();
            }
        }
        return value;
    }

    /**
     * 等级范围
     *
     * @param target 另一个单位
     * @return 返回范围[lo, hi)，返回空则
     */
    default IntScope gradeRange(E target) {
        final int diff = poorGrade(target);
        if (diff == 0) {
            return null;
        } else if (diff > 0) {
            return IntScope.of(target.grade(), grade());
        } else {
            return IntScope.of(grade(), target.grade());
        }
    }

    /**
     * 登记与与目标的差值
     *
     * <p>默认 {@code level() - target.level()}，代表当前等级与目标等级差值
     *
     * <p>如果大于等于1说明，当前等级高于目标，转换则需要乘以
     *
     * <p>如果小于等于-1说明，当前等级低于目标，转换则需要除以
     *
     * @param target {@code E extends Enum<E> & UnitIsometric<E>}
     * @return 返回数字等级
     */
    default int poorGrade(E target) {
        return grade() - target.grade();
    }

    /**
     * 比较两个单位的等级大小
     *
     * @param target 另一个单位
     * @return 返回差值
     */
    @Override
    default int compareTo(E target) {
        return poorGrade(target);
    }
}
