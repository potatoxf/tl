package pxf.tl.lang.generator;


import pxf.tl.math.id.ObjectId;

/**
 * ObjectId生成器
 *
 * @author potatoxf
 */
public class ObjectIdGenerator implements Generator<String> {
    @Override
    public String next() {
        return ObjectId.next();
    }
}
