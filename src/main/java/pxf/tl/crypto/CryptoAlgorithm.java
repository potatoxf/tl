package pxf.tl.crypto;

import pxf.tl.api.PoolOfCharacter;
import pxf.tl.util.ToolString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import static pxf.tl.help.New.arr;
import static pxf.tl.util.ToolRandom.getSecureRandom;

/**
 * @author potatoxf
 */
public enum CryptoAlgorithm {
    /**
     * RSA算法
     */
    RSA("RSA", true, Map.of(512, arr(53, 64), 1024, arr(117, 128))
            , arr(512, 1024, 2048, 4096, 8192), arr()),
    /**
     * RSA算法，此算法用了默认补位方式为RSA/ECB/PKCS1Padding
     */
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding", true),
    /**
     * RSA算法，此算法用了默认补位方式为RSA/ECB/NoPadding
     */
    RSA_ECB("RSA/ECB/NoPadding", true),
    /**
     * RSA算法，此算法用了RSA/None/NoPadding
     */
    RSA_None("RSA/None/NoPadding", true),
    /**
     *
     */
    DSA("DSA", true, arr(512, 1024, 2048, 4096)),
    /**
     *
     */
    EC("EC", true, arr(128, 256)),
    /**
     *
     */
    ECIES("ECIES", true, arr(128, 256)),
    /**
     *
     */
    SM2("SM2", true),
    /**
     * 默认的DES加密方式：DES/ECB/PKCS5Padding
     */
    DES("DES", false, Map.of(56, arr(56, 64)), arr(56), arr(8)),
    /**
     * 3DES算法，默认实现为：DESede/ECB/PKCS5Padding
     */
    DESede("DESede", false, arr(112, 168), arr(16, 24)),
    /**
     * 默认的AES加密方式：AES/ECB/PKCS5Padding
     */
    AES("AES", false, arr(128, 192, 256), arr(16, 24, 32)),
    /**
     *
     */
    ARCFOUR("ARCFOUR", false),
    /**
     *
     */
    Blowfish("Blowfish", false, null, IntStream.rangeClosed(1, 56).toArray()),
    /**
     *
     */
    RC2("RC2", false, null, IntStream.rangeClosed(1, 128).toArray()),
    /**
     *
     */
    PBEWithMD5AndDES("PBEWithMD5AndDES", false),
    /**
     *
     */
    PBEWithSHA1AndDESede("PBEWithSHA1AndDESede", false),
    /**
     *
     */
    PBEWithSHA1AndRC2("PBEWithSHA1AndRC2", false);
    public final String algorithm;
    public final boolean isAsymmetric;
    private final Map<Integer, int[]> lengthBlockMap;
    private final int[] keySizeRange;
    private final int[] keySpecSize;
    @Nullable
    public static volatile Provider provider;
    @Nullable
    public static volatile String providerName;

    CryptoAlgorithm(String algorithm, boolean isAsymmetric) {
        this(algorithm, isAsymmetric, null, null, null);
    }

    CryptoAlgorithm(String algorithm, boolean isAsymmetric, int[] keySizeRange) {
        this(algorithm, isAsymmetric, null, keySizeRange);
    }

    CryptoAlgorithm(String algorithm, boolean isAsymmetric,
                    int[] keySizeRange,
                    int[] keySpecSize) {
        this(algorithm, isAsymmetric, null, keySizeRange, keySpecSize);
    }

    CryptoAlgorithm(String algorithm, boolean isAsymmetric,
                    Map<Integer, int[]> lengthBlockMap,
                    int[] keySizeRange,
                    int[] keySpecSize) {
        this.algorithm = algorithm;
        this.isAsymmetric = isAsymmetric;
        this.lengthBlockMap = lengthBlockMap == null ? Collections.emptyMap() : lengthBlockMap;
        this.keySizeRange = keySizeRange;
        this.keySpecSize = keySpecSize;
    }

    /**
     * 获取主体算法名，例如RSA/ECB/PKCS1Padding的主体算法是RSA
     * 获取XXXwithXXX算法的后半部分算法，如果为ECDSA或SM2，返回算法为EC
     *
     * @return 主体算法名
     */
    @Nonnull
    public static String handleAlgorithmName(@Nonnull String algorithm) {
        final int slashIndex = algorithm.indexOf(PoolOfCharacter.SLASH);
        if (slashIndex > 0) {
            algorithm = algorithm.substring(0, slashIndex);
        }

        if (ToolString.startWithIgnoreCase(algorithm, "ECIESWith")) {
            algorithm = "EC";
        } else {
            int indexOfWith = ToolString.lastIndexOfIgnoreCase(algorithm, "with");
            if (indexOfWith > 0) {
                algorithm = algorithm.substring(indexOfWith + 4);
            }
            if ("ECDSA".equalsIgnoreCase(algorithm)
                    || "SM2".equalsIgnoreCase(algorithm)
                    || "ECIES".equalsIgnoreCase(algorithm)) {
                algorithm = "EC";
            }
        }
        return algorithm;
    }

    @Nonnull
    public AlgorithmParameterGenerator getAlgorithmParameterGenerator() throws CryptoException {
        checkAsymmetric();
        try {
            if (provider != null) {
                return AlgorithmParameterGenerator.getInstance(algorithm, provider);
            }
            if (providerName != null) {
                return AlgorithmParameterGenerator.getInstance(algorithm, providerName);
            }
            return AlgorithmParameterGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@link KeyPairGenerator}
     *
     * @return {@link KeyPairGenerator}
     */
    @Nonnull
    public KeyPairGenerator getKeyPairGenerator() throws CryptoException {
        checkAsymmetric();
        try {
            if (provider != null) {
                return KeyPairGenerator.getInstance(algorithm, provider);
            }
            if (providerName != null) {
                return KeyPairGenerator.getInstance(algorithm, providerName);
            }
            return KeyPairGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@link KeyGenerator}
     *
     * @return {@link KeyGenerator}
     */
    @Nonnull
    public KeyGenerator getKeyGenerator() throws CryptoException {
        checkSymmetric();
        try {
            if (provider != null) {
                return KeyGenerator.getInstance(algorithm, provider);
            }
            if (providerName != null) {
                return KeyGenerator.getInstance(algorithm, providerName);
            }
            return KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@link KeyFactory}
     *
     * @return {@link KeyFactory}
     */
    @Nonnull
    public KeyFactory getKeyFactory() throws CryptoException {
        checkAsymmetric();
        try {
            if (provider != null) {
                return KeyFactory.getInstance(algorithm, provider);
            }
            if (providerName != null) {
                return KeyFactory.getInstance(algorithm, providerName);
            }
            return KeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@link SecretKeyFactory}
     *
     * @return {@link KeyFactory}
     */
    @Nonnull
    public SecretKeyFactory getSecretKeyFactory() throws CryptoException {
        checkSymmetric();
        try {
            if (provider != null) {
                return SecretKeyFactory.getInstance(algorithm, provider);
            }
            if (providerName != null) {
                return SecretKeyFactory.getInstance(algorithm, providerName);
            }
            return SecretKeyFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@code Cipher}
     *
     * @param mode {@link Cipher#ENCRYPT_MODE},{@link Cipher#DECRYPT_MODE},{@link Cipher#WRAP_MODE},{@link Cipher#UNWRAP_MODE}
     * @param key  键
     * @return {@code Cipher}
     * @throws CryptoException 如果发生异常
     */
    @Nonnull
    public Cipher getCipher(int mode, @Nonnull Key key) throws CryptoException {
        return getCipher(mode, key, null, null);
    }

    /**
     * 获取{@code Cipher}
     *
     * @param mode   {@link Cipher#ENCRYPT_MODE},{@link Cipher#DECRYPT_MODE},{@link Cipher#WRAP_MODE},{@link Cipher#UNWRAP_MODE}
     * @param key    键
     * @param params 参数
     * @param random 随机
     * @return {@code Cipher}
     * @throws CryptoException 如果发生异常
     */
    @Nonnull
    public Cipher getCipher(int mode, @Nonnull Key key, @Nullable AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        Cipher cipher = null;
        try {
            if (provider != null) {
                cipher = Cipher.getInstance(algorithm, provider);
            }
            if (cipher == null && providerName != null) {
                cipher = Cipher.getInstance(algorithm, providerName);
            }
            if (cipher == null) {
                cipher = Cipher.getInstance(algorithm);
            }
            if (null != params) {
                if (null != random) {
                    cipher.init(mode, key, params, random);
                } else {
                    cipher.init(mode, key, params);
                }
            } else {
                if (null != random) {
                    cipher.init(mode, key, random);
                } else {
                    cipher.init(mode, key);
                }
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | NoSuchProviderException
                | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
        return cipher;
    }

    /**
     * 是否支持分段处理
     *
     * @return 如果是返回true，否者返回false
     */
    public boolean isSupportSegment() {
        return !lengthBlockMap.isEmpty();
    }

    /**
     * 获取加密块大小
     *
     * @param keySize 键大小
     * @return 返回解密块大小
     */
    public int getEncryptBlock(int keySize) {
        if (!isSupportSegment()) {
            throw new CryptoException("No support " + keySize + " in " + Arrays.toString(keySizeRange));
        }
        return lengthBlockMap.get(keySize)[0];
    }

    /**
     * 获取解密块大小
     *
     * @param keySize 键大小
     * @return 返回解密块大小
     */
    public int getDecryptBlock(int keySize) {
        if (!isSupportSegment()) {
            throw new CryptoException("No support " + keySize + " in " + Arrays.toString(keySizeRange));
        }
        return lengthBlockMap.get(keySize)[1];
    }

    /**
     * 检查键大小
     *
     * @param keySize     键大小
     * @param openSegment 是否分段
     */
    public void checkKeySize(int keySize, boolean openSegment) {
        if (isSupportSegment() && openSegment && !lengthBlockMap.containsKey(keySize)) {
            throw new CryptoException(
                    "The encryption algorithm ["
                            + algorithm
                            + "] does not support key size ["
                            + keySize
                            + "],The support list in "
                            + lengthBlockMap.keySet());
        } else if (keySizeRange != null && keySizeRange.length != 0) {
            for (int value : keySizeRange) {
                if (keySize == value) {
                    return;
                }
            }
            throw new CryptoException(
                    "The encryption algorithm ["
                            + algorithm
                            + "] does not support key size ["
                            + keySize
                            + "],The support list in "
                            + Arrays.toString(keySizeRange));
        }
    }

    /**
     * 检查是否时非对称加密
     *
     * @return 如果是返回true，否则返回false
     */
    public CryptoAlgorithm checkAsymmetric() {
        if (!isAsymmetric) {
            throw new CryptoException(
                    "The encryption algorithm [" + algorithm + "] does not support asymmetric encryption");
        }
        return this;
    }

    /**
     * 检查是否时对称加密
     *
     * @return 如果是返回true，否则返回false
     */
    public CryptoAlgorithm checkSymmetric() {
        if (isAsymmetric) {
            throw new CryptoException(
                    "The encryption algorithm [" + algorithm + "] does not support symmetric encryption");
        }
        return this;
    }

    //------------------------------------------------------------------------------------------------------------------
    //生成SecretKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
     *
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey() {
        return checkSymmetric().generateSecretKey(-1);
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成<br>
     * 当指定keySize&lt;0时，AES默认长度为128，其它算法不指定。
     *
     * @param keySize 密钥长度，&lt;0表示不设定密钥长度，即使用默认长度
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(int keySize) {
        return checkSymmetric().generateSecretKey(keySize, (SecureRandom) null);
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成<br>
     * 当指定keySize&lt;0时，AES默认长度为128，其它算法不指定。
     *
     * @param keySize 密钥长度，&lt;0表示不设定密钥长度，即使用默认长度
     * @param seed    种子
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(int keySize, byte[] seed) {
        return checkSymmetric().generateSecretKey(keySize, null == seed ? new SecureRandom() : new SecureRandom(seed));
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成<br>
     * 当指定keySize&lt;0时，AES默认长度为128，其它算法不指定。
     *
     * @param keySize 密钥长度，&lt;0表示不设定密钥长度，即使用默认长度
     * @param random  随机数生成器，null表示默认
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(int keySize, SecureRandom random) {
        checkSymmetric();
        KeyGenerator keyGenerator = getKeyGenerator();
        if (keySize <= 0 && CryptoAlgorithm.AES == this) {
            // 对于AES的密钥，除非指定，否则强制使用128位
            keySize = 128;
        }

        if (keySize > 0) {
            if (null == random) {
                keyGenerator.init(keySize);
            } else {
                keyGenerator.init(keySize, random);
            }
        }
        return keyGenerator.generateKey();
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
     *
     * @param key 密钥，如果为{@code null} 自动生成随机密钥
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(String key) {
        return checkSymmetric().generateSecretKeyByKey(key.getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
     *
     * @param key 密钥，如果为{@code null} 自动生成随机密钥
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(byte[] key) {
        return checkSymmetric().generateSecretKeyByKey(generateKeySpec(key));
    }

    /**
     * 生成 {@link SecretKey}，仅用于对称加密和摘要算法
     *
     * @param keySpec {@link KeySpec}
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(@Nonnull KeySpec keySpec) {
        checkSymmetric();
        try {
            return getSecretKeyFactory().generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //生成KeyPair
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 生成用于非对称加密的公钥和私钥<br>
     * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @param keySize 密钥模（modulus ）长度
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(int keySize) {
        return checkAsymmetric().generateKeyPair(keySize, (SecureRandom) null);
    }

    /**
     * 生成用于非对称加密的公钥和私钥<br>
     * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @param keySize 密钥模（modulus ）长度
     * @param seed    种子
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(int keySize, byte[] seed) {
        return checkAsymmetric().generateKeyPair(keySize, null == seed ? new SecureRandom() : new SecureRandom(seed));
    }

    /**
     * 生成用于非对称加密的公钥和私钥<br>
     * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @param params {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(int keySize, AlgorithmParameterSpec params) {
        return checkAsymmetric().generateKeyPair(keySize, (SecureRandom) null, params);
    }

    /**
     * 生成用于非对称加密的公钥和私钥<br>
     * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * <p>对于非对称加密算法，密钥长度有严格限制，具体如下：
     *
     * <p><b>RSA：</b>
     *
     * <pre>
     * RS256、PS256：2048 bits
     * RS384、PS384：3072 bits
     * RS512、RS512：4096 bits
     * </pre>
     *
     * <p><b>EC（Elliptic Curve）：</b>
     *
     * <pre>
     * EC256：256 bits
     * EC384：384 bits
     * EC512：512 bits
     * </pre>
     *
     * @param keySize 密钥模（modulus ）长度（单位bit）
     * @param seed    种子
     * @param params  {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(int keySize, byte[] seed, AlgorithmParameterSpec... params) {
        return checkAsymmetric().generateKeyPair(keySize, (null == seed) ? new SecureRandom() : new SecureRandom(seed), params);
    }

    /**
     * 生成用于非对称加密的公钥和私钥<br>
     * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * <p>对于非对称加密算法，密钥长度有严格限制，具体如下：
     *
     * <p><b>RSA：</b>
     *
     * <pre>
     * RS256、PS256：2048 bits
     * RS384、PS384：3072 bits
     * RS512、RS512：4096 bits
     * </pre>
     *
     * <p><b>EC（Elliptic Curve）：</b>
     *
     * <pre>
     * EC256：256 bits
     * EC384：384 bits
     * EC512：512 bits
     * </pre>
     *
     * @param keySize 密钥模（modulus ）长度（单位bit）
     * @param random  {@link SecureRandom} 对象，创建时可选传入seed
     * @param params  {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
        checkAsymmetric();
        // SM2算法需要单独定义其曲线生成
        if (CryptoAlgorithm.SM2 == this) {
            // SM2默认曲线
            ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec("sm2p256v1");
            params = new AlgorithmParameterSpec[]{sm2p256v1};
        }
        // 实例化密钥生成器
        KeyPairGenerator keyPairGen = getKeyPairGenerator();

        // 密钥模（modulus ）长度初始化定义
        if (keySize > 0) {
            // key长度适配修正
            if (CryptoAlgorithm.EC == this && keySize > 256) {
                // 对于EC（EllipticCurve）算法，密钥长度有限制，在此使用默认256
                keySize = 256;
            }
            checkKeySize(keySize, false);
            if (null != random) {
                keyPairGen.initialize(keySize, random);
            } else {
                keyPairGen.initialize(keySize);
            }
        }


        // 自定义初始化参数
        try {
            if (params != null) {
                for (AlgorithmParameterSpec param : params) {
                    if (null == param) {
                        continue;
                    }
                    if (null != random) {
                        keyPairGen.initialize(param, random);
                    } else {
                        keyPairGen.initialize(param);
                    }
                }
            }
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
        // 生成密钥对
        return keyPairGen.generateKeyPair();
    }

    @Nonnull
    public <T extends AlgorithmParameterSpec> KeyPair generateKeyPair(
            int keySize, @Nonnull Class<T> paramSpec) throws CryptoException {
        checkAsymmetric();
        AlgorithmParameterGenerator algorithmParameterGenerator = getAlgorithmParameterGenerator();
        // 初始化参数生成器
        algorithmParameterGenerator.init(keySize);
        // 生成算法参数
        AlgorithmParameters algorithmParameters = algorithmParameterGenerator.generateParameters();
        // 实例化密钥生成器
        KeyPairGenerator keyPairGenerator = getKeyPairGenerator();
        try {
            // 初始化密钥生成器
            keyPairGenerator.initialize(
                    algorithmParameters.getParameterSpec(paramSpec), getSecureRandom(null));
            // 生成密钥对
            return keyPairGenerator.generateKeyPair();
        } catch (InvalidParameterSpecException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //生成PrivateKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 生成私钥，仅用于非对称加密<br>
     * 采用PKCS#8规范，此规范定义了私钥信息语法和加密私钥语法<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param key 密钥，PKCS#8格式
     * @return 私钥 {@link PrivateKey}
     */
    @Nonnull
    public PrivateKey generatePrivateKey(@Nonnull byte[] key) {
        checkAsymmetric();
        return generatePrivateKey(new PKCS8EncodedKeySpec(key));
    }

    /**
     * 生成私钥，仅用于非对称加密<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param keySpec {@link KeySpec}
     * @return 私钥 {@link PrivateKey}
     */
    @Nonnull
    public PrivateKey generatePrivateKey(@Nonnull KeySpec keySpec) {
        checkAsymmetric();
        try {
            return getKeyFactory().generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //生成PublicKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 生成公钥，仅用于非对称加密<br>
     * 采用X509证书规范<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param key 密钥，必须为DER编码存储
     * @return 公钥 {@link PublicKey}
     */
    @Nonnull
    public PublicKey generatePublicKey(@Nonnull byte[] key) {
        checkAsymmetric();
        return generatePublicKey(new X509EncodedKeySpec(key));
    }

    /**
     * 生成公钥，仅用于非对称加密<br>
     * 算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param keySpec {@link KeySpec}
     * @return 公钥 {@link PublicKey}
     */
    @Nonnull
    public PublicKey generatePublicKey(@Nonnull KeySpec keySpec) {
        checkAsymmetric();
        try {
            return getKeyFactory().generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //生成warp
    //------------------------------------------------------------------------------------------------------------------

    @Nonnull
    public byte[] wrapWithPBE(@Nonnull Key wrappedKey, String password,
                              @Nonnull AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        if (!isPBE()) {
            throw new CryptoException("");
        }
        if (!(params instanceof PBEParameterSpec)) {
            throw new CryptoException("");
        }
        return wrap(wrappedKey, generateSecretKeyByKey(new PBEKeySpec(password.toCharArray())), params, random);
    }

    @Nonnull
    public Key unwrapWithPBE(@Nonnull byte[] wrappedKey, String password,
                             @Nonnull AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        if (!isPBE()) {
            throw new CryptoException("");
        }
        if (!(params instanceof PBEParameterSpec)) {
            throw new CryptoException("");
        }
        return unwrap(wrappedKey, generateSecretKeyByKey(new PBEKeySpec(password.toCharArray())), params, random);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey) throws CryptoException {
        return wrap(wrappedKey, generateEmptySecretKeySpec(), null, null);
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey) throws CryptoException {
        return unwrap(wrappedKey, generateEmptySecretKeySpec(), null, null);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey, @Nonnull Key key) throws CryptoException {
        return wrap(wrappedKey, key, null, null);
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey, @Nonnull Key key) throws CryptoException {
        return unwrap(wrappedKey, key, null, null);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey, @Nonnull Key key,
                       @Nullable AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        try {
            return getCipher(Cipher.WRAP_MODE, key, params, random).wrap(wrappedKey);
        } catch (IllegalBlockSizeException | InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey, @Nonnull Key key,
                      @Nullable AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        try {
            return getCipher(Cipher.UNWRAP_MODE, key, params, random).unwrap(wrappedKey, algorithm, Cipher.SECRET_KEY);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //其它
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 是否是PBE算法
     *
     * @return 如果是返回true，否则返回false
     */
    public boolean isPBE() {
        return algorithm.startsWith("PBE");
    }

    /**
     * 生成{@code KeySpec}
     *
     * @param key 键
     * @return {@code KeySpec}
     */
    @Nonnull
    public KeySpec generateKeySpec(@Nonnull byte[] key) {
        checkSymmetric();
        try {
            if (isPBE()) {
                char[] chars = new char[key.length];
                for (int i = 0; i < key.length; i++) {
                    chars[i] = (char) key[i];
                }
                return new PBEKeySpec(chars);
            } else if (algorithm.startsWith("DES")) {
                KeySpec keySpec;
                if (algorithm.startsWith("DESede")) {
                    keySpec = new DESedeKeySpec(key);
                } else {
                    keySpec = new DESKeySpec(key);
                }
                return keySpec;
            }
            return new SecretKeySpec(key, algorithm);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }


    /**
     * 生成空的{@code SecretKeySpec}
     *
     * @return {@code SecretKeySpec}
     */
    @Nonnull
    public SecretKeySpec generateEmptySecretKeySpec() {
        SecretKeySpec secretKeySpec;
        if (keySpecSize == null || keySpecSize.length == 0) {
            secretKeySpec = new SecretKeySpec(new byte[16], algorithm);
        } else {
            secretKeySpec = new SecretKeySpec(new byte[keySpecSize[keySpecSize.length / 2]], algorithm);
        }
        return secretKeySpec;
    }

    @Override
    public String toString() {
        return algorithm;
    }
}
