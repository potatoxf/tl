package pxf.tl.lang;

/**
 * 顺序选择列表
 *
 * <p>一般会出现这种情况：
 *
 * <pre>
 *     if(val==0){
 *         ...
 *     }else if(val==1){
 *         ...
 *     }else if(val==2){
 *         ...
 *     }
 * </pre>
 *
 * <p>因此希望可以优化掉这种选择，如：
 *
 * <pre>
 *      ...
 *      private static final SequenceSelect<Supplier<String>> config = SequenceSelect.of(() -> "第1个", () -> "第2个", () -> "第3个", () -> "第4个");
 *
 *      ...
 *      public void method(){
 *          ...
 *          Supplier<String> action=config.selectAction(number);
 *          ...
 *      }
 *      ...
 * </pre>
 *
 * <p>显然第二种易于优化，代码可读性高。关于效率问题，在执行不多的情况下，第二种较慢，但是在大量执行或者分支较多的时候，第二种就比第一种有明显的效率和
 * 维护优势。如果存在大量分支，那么cpu对于分支预测的功能也将大大折扣。第二种采用数组索引的方式，数组存在堆中，一般来说，这种代码是固固定不变的，因
 * 此当系统长时间运行时，会将该对象放入老年区，这样引用该对象后期会加快。
 *
 * @author potatoxf
 */
public final class SequenceSelector<T> {

    /**
     *
     */
    private final T[] selectList;

    /**
     * @param selectList
     */
    private SequenceSelector(T[] selectList) {
        this.selectList = selectList;
    }

    /**
     * 构建 c
     *
     * @param actionList 执行对象列表
     * @param <T>对象类型
     * @return {@code SequenceSelect<T>}
     * @throws IllegalArgumentException 当{@code actionList}为空
     */
    @SafeVarargs
    public static <T> SequenceSelector<T> of(T... actionList) {
        if (actionList == null || actionList.length == 0) {
            throw new IllegalArgumentException("The action lit must be no empty");
        }
        return new SequenceSelector<>(actionList);
    }

    /**
     * 选择出目标对象
     *
     * @param selectId 序列选择号
     * @return 目标对象
     * @throws IllegalArgumentException 当{@code selectId}小于0或者大于目标长度
     */
    public T selectAction(int selectId) {
        T[] temp = selectList;
        if (selectId < 0) {
            throw new IllegalArgumentException("The selectId must be more than 0 and equal");
        }
        if (selectId >= temp.length) {
            throw new IllegalArgumentException("The selectId must be less than " + temp.length);
        }
        return temp[selectId];
    }

    /**
     * 选择出目标对象
     *
     * @param selectId 序列选择号
     * @return 目标对象
     * @throws IllegalArgumentException 当{@code selectId}字符串为空
     * @throws NumberFormatException    当{@code selectId}不是数字
     * @throws IllegalArgumentException 当{@code selectId}小于0或者大于目标长度
     */
    public T selectAction(String selectId) {
        if (selectId == null || selectId.length() == 0) {
            throw new IllegalArgumentException("The selectId string must be no empty");
        }
        return selectAction(Integer.parseInt(selectId));
    }
}
