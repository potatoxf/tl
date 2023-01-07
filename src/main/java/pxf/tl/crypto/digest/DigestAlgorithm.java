package pxf.tl.crypto.digest;

import pxf.tl.crypto.CryptoException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * 信息摘要算法
 *
 * @author potatoxf
 */
public enum DigestAlgorithm implements FingerprintHandler {
    /**
     * 信息摘要算法
     */
    MD2("MD2"),
    MD5("MD5"),
    SHA_1("SHA-1"),
    SHA_224("SHA-224"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512"),
    SHA_512_224("SHA-512/224"),
    SHA_512_256("SHA-512/256"),
    SHA3_224("SHA3-224"),
    SHA3_256("SHA3-256"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512");
    private final String name;

    DigestAlgorithm(String name) {
        this.name = name;
    }

    /**
     * 创建{@code DigitalSignatureHandler}
     *
     * @return {@code DigitalSignatureHandler}
     */
    public FingerprintHandler createHandler() {
        return new FingerprintHandlerImpl(createCodec());
    }

    /**
     * 创建{@code DigitalSignatureHandler}
     *
     * @return {@code DigitalSignatureHandler}
     */
    public FingerprintHandler createHandler(byte[] salt, int digestCount) {
        return new FingerprintHandlerImpl(createCodec(), salt, digestCount);
    }

    /**
     * 创建{@code DigitalSignatureHandler}
     *
     * @return {@code DigitalSignatureHandler}
     */
    public FingerprintHandler createHandler(byte[] salt, int saltPosition, int digestCount) {
        return new FingerprintHandlerImpl(createCodec(), salt, saltPosition, digestCount);
    }

    /**
     * 创建{@code SecretCoder}
     *
     * @return {@code SecretCoder}
     */
    public FingerprintCodec createCodec() {
        return new FingerprintCodecImplForMessageDigest(createInstance());
    }

    /**
     * 创建{@code MessageDigest}
     *
     * @return {@code MessageDigest}
     */
    public MessageDigest createInstance() {
        try {
            Provider provider = null;//todo Provider
            return (null == provider) ? MessageDigest.getInstance(name) : MessageDigest.getInstance(name, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取算法字符串表示
     *
     * @return 算法字符串表示
     */
    public String getValue() {
        return this.name;
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    @Override
    public byte[] sign(byte[] data) {
        return createHandler().sign(data);
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    @Override
    public byte[] sign(ByteBuffer data) {
        return createHandler().sign(data);
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    @Override
    public byte[] sign(InputStream data) throws IOException {
        return createHandler().sign(data);
    }

    /**
     * 生存指纹
     *
     * @param data 数据
     * @return 返回指纹数据
     */
    @Override
    public byte[] sign(FileChannel data) throws IOException {
        return createHandler().sign(data);
    }

    private static class FingerprintCodecImplForMessageDigest implements FingerprintCodec {

        private final MessageDigest messageDigest;

        public FingerprintCodecImplForMessageDigest(MessageDigest messageDigest) {
            this.messageDigest = messageDigest;
        }

        @Override
        public void update(byte[] input, int offset, int len) {
            messageDigest.update(input, offset, len);
        }

        @Override
        public void update(ByteBuffer input) {
            messageDigest.update(input);
        }

        @Override
        public void reset() {
            messageDigest.reset();
        }

        @Override
        public byte[] finish() {
            return messageDigest.digest();
        }

        @Override
        public int getDigestLength() {
            return messageDigest.getDigestLength();
        }
    }
}
