package pxf.tl.lang.generator;


import pxf.tl.math.id.IDHelper;

/**
 * UUID生成器
 *
 * @author potatoxf
 */
public class UUIDGenerator implements Generator<String> {
    @Override
    public String next() {
        return IDHelper.randomUUID();
    }
}
