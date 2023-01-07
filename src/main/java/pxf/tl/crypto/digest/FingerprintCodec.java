package pxf.tl.crypto.digest;

import java.nio.ByteBuffer;

/**
 * 指纹编码
 *
 * @author potatoxf
 */
public interface FingerprintCodec {

    /**
     * 更新指纹数据
     *
     * @param input 输入
     */
    default void update(byte input) {
        update(new byte[]{input});
    }

    /**
     * 更新指纹数据
     *
     * @param input 输入
     */
    default void update(byte[] input) {
        update(input, 0, input.length);
    }

    /**
     * 更新指纹数据
     *
     * @param input  输入
     * @param offset 偏移
     * @param len    长度
     */
    void update(byte[] input, int offset, int len);

    /**
     * 更新指纹数据
     *
     * @param input 输入
     */
    void update(ByteBuffer input);

    /**
     * 重置
     */
    void reset();

    /**
     * 完成
     *
     * @return 返回指纹结果
     */
    byte[] finish();

    /**
     * 获取散列长度，0表示不支持此方法
     *
     * @return 散列长度，0表示不支持此方法
     */
    int getDigestLength();
}
