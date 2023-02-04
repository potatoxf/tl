package pxf.tl.crypto.digest;

import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.PoolOfArray;
import pxf.tl.exception.IORuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通用数字前面处理器
 *
 * @author potatoxf
 */
class FingerprintHandlerImpl implements FingerprintHandler {
    private final FingerprintCodec fingerprintCodec;
    /**
     * 盐值
     */
    private final byte[] salt;
    /**
     * 加盐位置，即将盐值字符串放置在数据的index数，默认0
     */
    private final int saltPosition;
    /**
     * 散列次数
     */
    private final int digestCount;

    FingerprintHandlerImpl(FingerprintCodec fingerprintCodec) {
        this(fingerprintCodec, null, -1, 0);
    }

    FingerprintHandlerImpl(FingerprintCodec fingerprintCodec, byte[] salt, int digestCount) {
        this(fingerprintCodec, salt, -1, digestCount);
    }

    FingerprintHandlerImpl(FingerprintCodec fingerprintCodec, byte[] salt, int saltPosition, int digestCount) {
        this.fingerprintCodec = fingerprintCodec;
        this.salt = salt;
        this.saltPosition = saltPosition;
        this.digestCount = digestCount;
    }

    /**
     * 生成摘要，考虑加盐和重复摘要次数
     *
     * @param data 数据bytes
     * @return 摘要bytes
     */
    @Override
    public byte[] sign(final byte[] data) {
        fingerprintCodec.reset();
        if (data == null || data.length == 0) {
            return PoolOfArray.EMPTY_BYTE_ARRAY;
        }
        byte[] result;
        if (salt == null || salt.length == 0) {
            // 无加盐
            fingerprintCodec.update(data);
        } else {
            if (saltPosition <= 0) {
                // 加盐在开头，自动忽略空盐值
                fingerprintCodec.update(salt);
                fingerprintCodec.update(data);
            } else if (saltPosition >= data.length) {
                // 加盐在末尾，自动忽略空盐值
                fingerprintCodec.update(data);
                fingerprintCodec.update(salt);
            } else {
                // 加盐在中间
                fingerprintCodec.update(data, 0, saltPosition);
                fingerprintCodec.update(salt);
                fingerprintCodec.update(data, saltPosition, data.length - saltPosition);
            }
        }
        result = fingerprintCodec.finish();
        result = repeatSign(result);
        return result;
    }

    @Override
    public byte[] sign(final ByteBuffer byteBuffer) {
        fingerprintCodec.reset();
        if (byteBuffer == null || byteBuffer.limit() == 0) {
            return PoolOfArray.EMPTY_BYTE_ARRAY;
        }
        if (byteBuffer.position() > 0) {
            byteBuffer.flip();
        }
        byte[] result;
        if (salt == null || salt.length == 0) {
            // 无加盐
            fingerprintCodec.update(byteBuffer);
        } else {
            int limit = byteBuffer.limit();
            if (saltPosition <= 0) {
                // 加盐在开头，自动忽略空盐值
                fingerprintCodec.update(salt);
                fingerprintCodec.update(byteBuffer);
            } else if (saltPosition >= limit) {
                // 加盐在末尾，自动忽略空盐值
                fingerprintCodec.update(byteBuffer);
                fingerprintCodec.update(salt);
            } else {
                // 加盐在中间
                fingerprintCodec.update(byteBuffer.limit(saltPosition));
                fingerprintCodec.update(salt);
                fingerprintCodec.update(byteBuffer.limit(limit));
            }
        }
        result = fingerprintCodec.finish();
        result = repeatSign(result);
        return result;
    }

    /**
     * 生成摘要
     *
     * @param data {@link InputStream} 数据流
     * @return 摘要bytes
     * @throws IORuntimeException IO异常
     */
    @Override
    public byte[] sign(final InputStream data) throws IOException {
        fingerprintCodec.reset();
        byte[] result;
        int readLen;
        byte[] buffer = new byte[JavaEnvironment.DEFAULT_BUFFER_SIZE];
        if (salt == null || salt.length == 0) {
            // 无加盐
            while ((readLen = data.read(buffer)) > -1) {
                fingerprintCodec.update(buffer, 0, readLen);
            }
        } else {
            if (saltPosition <= 0) {
                // 加盐在开头
                fingerprintCodec.update(salt);
            }
            int total = 0;
            while ((readLen = data.read(buffer)) > -1) {
                total += readLen;
                if (saltPosition > 0 && total >= saltPosition) {
                    int len = total - saltPosition;
                    if (len != 0) {
                        fingerprintCodec.update(buffer, 0, len);
                    }
                    // 加盐在中间
                    fingerprintCodec.update(salt);
                    fingerprintCodec.update(buffer, len, readLen - len);
                } else {
                    fingerprintCodec.update(buffer, 0, readLen);
                }
            }

            if (total < saltPosition) {
                // 加盐在末尾
                fingerprintCodec.update(salt);
            }
        }
        result = fingerprintCodec.finish();
        result = repeatSign(result);
        return result;
    }

    @Override
    public byte[] sign(final FileChannel data) throws IOException {
        fingerprintCodec.reset();
        byte[] result;
        int readLen;
        ByteBuffer byteBuffer = ByteBuffer.allocate(JavaEnvironment.DEFAULT_BUFFER_SIZE);
        if (salt == null || salt.length == 0) {
            // 无加盐
            while (data.read(byteBuffer) > 0) {
                byteBuffer.flip();
                fingerprintCodec.update(byteBuffer);
                byteBuffer.clear();
            }
        } else {
            if (saltPosition <= 0) {
                // 加盐在开头
                fingerprintCodec.update(salt);
            }
            int total = 0;
            while ((readLen = data.read(byteBuffer)) > -1) {
                total += readLen;
                byteBuffer.flip();
                if (saltPosition > 0 && total >= saltPosition) {
                    int len = total - saltPosition;
                    if (len != 0) {
                        byteBuffer.limit(len);
                        fingerprintCodec.update(byteBuffer);
                    }
                    // 加盐在中间
                    fingerprintCodec.update(salt);
                    fingerprintCodec.update(byteBuffer.limit(readLen));
                } else {
                    fingerprintCodec.update(byteBuffer);
                }
                byteBuffer.clear();
            }

            if (total < saltPosition) {
                // 加盐在末尾
                fingerprintCodec.update(salt);
            }
        }
        result = fingerprintCodec.finish();
        result = repeatSign(result);
        return result;
    }

    private byte[] repeatSign(byte[] result) {
        if (salt != null && salt.length != 0) {
            for (int i = Math.max(1, digestCount); i > 0; i--) {
                result = sign(result);
            }
        }
        return result;
    }
}
