package pxf.tl.api;

/**
 * this引用
 *
 * @author potatoxf
 */
public interface This<This> {

    /**
     * 返回this
     *
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    default Class<This> thisClass$() {
        return (Class<This>) this$().getClass();
    }

    /**
     * 返回this
     *
     * @return {@code this}
     */
    @SuppressWarnings("unchecked")
    default This this$() {
        return (This) this;
    }
}
