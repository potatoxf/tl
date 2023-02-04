package pxf.tl.crypto.digest;


import pxf.tl.crypto.CryptoException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * HMAC算法
 *
 * @author potatoxf
 */
public enum HmacAlgorithm {
    /**
     * HMAC算法
     */
    HmacMD5("HmacMD5"),
    HmacSHA1("HmacSHA1"),
    HmacSHA224("HmacSHA224"),
    HmacSHA256("HmacSHA256"),
    HmacSHA384("HmacSHA384"),
    HmacSHA512("HmacSHA512"),
    /**
     * HmacSM3算法实现，需要BouncyCastle库支持
     */
    HmacSM3("HmacSM3"),
    /**
     * SM4 CMAC模式实现，需要BouncyCastle库支持
     */
    SM4CMAC("SM4CMAC");
    private final String algorithm;

    HmacAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 创建{@code FingerprintHandler}
     *
     * @return {@code FingerprintHandler}
     */
    public FingerprintHandler createHandler(byte[] key) {
        return new FingerprintHandlerImpl(createCodec(key));
    }

    /**
     * 创建{@code FingerprintHandler}
     *
     * @return {@code FingerprintHandler}
     */
    public FingerprintHandler createHandler(byte[] key, byte[] salt, int digestCount) {
        return new FingerprintHandlerImpl(createCodec(key), salt, digestCount);
    }

    /**
     * 创建{@code FingerprintHandler}
     *
     * @return {@code FingerprintHandler}
     */
    public FingerprintHandler createHandler(byte[] key, byte[] salt, int saltPosition, int digestCount) {
        return new FingerprintHandlerImpl(createCodec(key), salt, saltPosition, digestCount);
    }

    /**
     * 创建{@code FingerprintCodec}
     *
     * @param key 键
     * @return {@code FingerprintCodec}
     */
    public FingerprintCodec createCodec(byte[] key) {
        return new FingerprintCodecImplForHmac(createInstance(key));
    }

    /**
     * 创建{@code Mac}
     *
     * @param key 键
     * @return {@code Mac}
     */
    public Mac createInstance(byte[] key) {
        return createInstance(key, null);
    }

    /**
     * 创建{@code Mac}
     *
     * @param key  密钥
     * @param spec {@link AlgorithmParameterSpec}
     * @return this
     * @throws CryptoException Cause by IOException
     */
    public Mac createInstance(byte[] key, AlgorithmParameterSpec spec) {
        return createInstance(new SecretKeySpec(key, algorithm), spec);
    }

    /**
     * 创建{@code Mac}
     *
     * @param key  密钥 {@link SecretKey}
     * @param spec {@link AlgorithmParameterSpec}
     * @return this
     * @throws CryptoException Cause by IOException
     */
    public Mac createInstance(Key key, AlgorithmParameterSpec spec) {
        Mac mac;
        try {
            final Provider provider = null;//todo Provider
            try {
                mac = (null == provider) ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
            } catch (NoSuchAlgorithmException e) {
                throw new CryptoException(e);
            }
            if (null != spec) {
                mac.init(key, spec);
            } else {
                mac.init(key);
            }
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
        return mac;
    }

    private static class FingerprintCodecImplForHmac implements FingerprintCodec {

        private final Mac mac;

        FingerprintCodecImplForHmac(Mac mac) {
            this.mac = mac;
        }

        @Override
        public void update(byte[] input, int offset, int len) {
            mac.update(input, offset, len);
        }

        @Override
        public void update(ByteBuffer input) {
            mac.update(input);
        }

        @Override
        public void reset() {
            mac.reset();
        }

        @Override
        public byte[] finish() {
            return mac.doFinal();
        }

        @Override
        public int getDigestLength() {
            return mac.getMacLength();
        }
    }
}
