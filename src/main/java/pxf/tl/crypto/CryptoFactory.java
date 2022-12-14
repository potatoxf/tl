package pxf.tl.crypto;


import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolNumber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author potatoxf
 */
public class CryptoFactory {
    @Nonnull
    private final CryptoAlgorithm cryptoAlgorithm;
    /**
     *
     */
    private final int keySize;
    /**
     *
     */
    private final boolean isSegment;
    /**
     *
     */
    @Nullable
    private final Provider provider;
    /**
     *
     */
    @Nullable
    private final String providerName;
    /**
     *
     */
    @Nonnull
    private final CryptoService[] cryptoServices;
    /**
     *
     */
    private SecureRandom secureRandom;
    /**
     *
     */
    private AlgorithmParameterSpec algorithmParameterSpec;

    public CryptoFactory(CryptoAlgorithm cryptoAlgorithm,
                         int keySize, boolean isSegment) {
        this(cryptoAlgorithm, keySize, isSegment, null, null);
    }

    public CryptoFactory(
            @Nonnull CryptoAlgorithm cryptoAlgorithm,
            int keySize,
            boolean isSegment,
            @Nullable Provider provider) {
        this(cryptoAlgorithm, keySize, isSegment, provider, null);
    }

    public CryptoFactory(
            @Nonnull CryptoAlgorithm cryptoAlgorithm,
            int keySize,
            boolean isSegment,
            @Nullable String providerName) {
        this(cryptoAlgorithm, keySize, isSegment, null, providerName);
    }

    private CryptoFactory(
            @Nonnull CryptoAlgorithm cryptoAlgorithm,
            int keySize,
            boolean isSegment,
            @Nullable Provider provider,
            @Nullable String providerName) {
        cryptoAlgorithm.checkKeySize(keySize, isSegment);
        this.cryptoAlgorithm = cryptoAlgorithm;
        this.keySize = keySize;
        this.isSegment = cryptoAlgorithm.isSupportSegment() && isSegment;
        this.provider = provider;
        this.providerName = providerName;
        if (cryptoAlgorithm.isAsymmetric) {
            cryptoServices = new CryptoService[]{
                    new CryptoServiceImpl(this::generatePublicKey),
                    new CryptoServiceImpl(this::generatePrivateKey)
            };
        } else {
            cryptoServices = new CryptoService[]{new CryptoServiceImpl(this::generateSecretKey)};
        }
    }

    @Nonnull
    public static CryptoAlgorithm findAlgorithmType(@Nonnull String algorithm) {
        Optional<CryptoAlgorithm> first =
                Arrays.stream(CryptoAlgorithm.values())
                        .filter(cryptoAlgorithm -> cryptoAlgorithm.algorithm.equalsIgnoreCase(algorithm))
                        .findFirst();

        if (first.isPresent()) {
            return first.get();
        }
        throw new IllegalArgumentException();
    }


    //------------------------------------------------------------------------------------------------------------------
    //??????SecretKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????
     *
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey() {
        return cryptoAlgorithm.generateSecretKey();
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????<br>
     * ?????????keySize&lt;0??????AES???????????????128???????????????????????????
     *
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(int keySize) {
        return cryptoAlgorithm.generateSecretKey(keySize);
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????<br>
     * ?????????keySize&lt;0??????AES???????????????128???????????????????????????
     *
     * @param seed ??????
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(byte[] seed) {
        return cryptoAlgorithm.generateSecretKey(keySize, seed);
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????<br>
     * ?????????keySize&lt;0??????AES???????????????128???????????????????????????
     *
     * @param random ?????????????????????null????????????
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKey(SecureRandom random) {
        return cryptoAlgorithm.generateSecretKey(keySize, random);
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????
     *
     * @param key ??????????????????{@code null} ????????????????????????
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(String key) {
        return cryptoAlgorithm.generateSecretKeyByKey(key);
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????????????????
     *
     * @param key ??????????????????{@code null} ????????????????????????
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(byte[] key) {
        return cryptoAlgorithm.generateSecretKeyByKey(key);
    }

    /**
     * ?????? {@link SecretKey}???????????????????????????????????????
     *
     * @param keySpec {@link KeySpec}
     * @return {@link SecretKey}
     */
    @Nonnull
    public SecretKey generateSecretKeyByKey(@Nonnull KeySpec keySpec) {
        return cryptoAlgorithm.generateSecretKeyByKey(keySpec);
    }

    //------------------------------------------------------------------------------------------------------------------
    //??????KeyPair
    //------------------------------------------------------------------------------------------------------------------

    /**
     * ?????????????????????????????????????????????<br>
     * ???????????????????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair() {
        return cryptoAlgorithm.generateKeyPair(keySize);
    }

    /**
     * ?????????????????????????????????????????????<br>
     * ???????????????????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @param seed ??????
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(byte[] seed) {
        return cryptoAlgorithm.generateKeyPair(keySize, seed);
    }

    /**
     * ?????????????????????????????????????????????<br>
     * ???????????????????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * @param param {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(AlgorithmParameterSpec param) {
        return cryptoAlgorithm.generateKeyPair(keySize, param);
    }

    /**
     * ?????????????????????????????????????????????<br>
     * ???????????????????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * <p>???????????????????????????????????????????????????????????????????????????
     *
     * <p><b>RSA???</b>
     *
     * <pre>
     * RS256???PS256???2048 bits
     * RS384???PS384???3072 bits
     * RS512???RS512???4096 bits
     * </pre>
     *
     * <p><b>EC???Elliptic Curve??????</b>
     *
     * <pre>
     * EC256???256 bits
     * EC384???384 bits
     * EC512???512 bits
     * </pre>
     *
     * @param seed   ??????
     * @param params {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(byte[] seed, AlgorithmParameterSpec... params) {
        return cryptoAlgorithm.generateKeyPair(keySize, seed, params);
    }

    /**
     * ?????????????????????????????????????????????<br>
     * ???????????????????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
     *
     * <p>???????????????????????????????????????????????????????????????????????????
     *
     * <p><b>RSA???</b>
     *
     * <pre>
     * RS256???PS256???2048 bits
     * RS384???PS384???3072 bits
     * RS512???RS512???4096 bits
     * </pre>
     *
     * <p><b>EC???Elliptic Curve??????</b>
     *
     * <pre>
     * EC256???256 bits
     * EC384???384 bits
     * EC512???512 bits
     * </pre>
     *
     * @param random {@link SecureRandom} ??????????????????????????????seed
     * @param params {@link AlgorithmParameterSpec}
     * @return {@link KeyPair}
     */
    public KeyPair generateKeyPair(SecureRandom random, AlgorithmParameterSpec... params) {
        return cryptoAlgorithm.generateKeyPair(keySize, random, params);
    }

    @Nonnull
    public <T extends AlgorithmParameterSpec> KeyPair generateKeyPair(
            @Nonnull Class<T> paramSpec) throws CryptoException {
        return cryptoAlgorithm.generateKeyPair(keySize, paramSpec);
    }

    //------------------------------------------------------------------------------------------------------------------
    //??????PrivateKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * ???????????????????????????????????????<br>
     * ??????PKCS#8??????????????????????????????????????????????????????????????????<br>
     * ????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param key ?????????PKCS#8??????
     * @return ?????? {@link PrivateKey}
     */
    @Nonnull
    public PrivateKey generatePrivateKey(@Nonnull byte[] key) {
        return cryptoAlgorithm.generatePrivateKey(key);
    }

    /**
     * ???????????????????????????????????????<br>
     * ????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param keySpec {@link KeySpec}
     * @return ?????? {@link PrivateKey}
     */
    @Nonnull
    public PrivateKey generatePrivateKey(@Nonnull KeySpec keySpec) {
        return cryptoAlgorithm.generatePrivateKey(keySpec);
    }

    //------------------------------------------------------------------------------------------------------------------
    //??????PublicKey
    //------------------------------------------------------------------------------------------------------------------

    /**
     * ???????????????????????????????????????<br>
     * ??????X509????????????<br>
     * ????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param key ??????????????????DER????????????
     * @return ?????? {@link PublicKey}
     */
    @Nonnull
    public PublicKey generatePublicKey(@Nonnull byte[] key) {
        return cryptoAlgorithm.generatePublicKey(key);
    }

    /**
     * ???????????????????????????????????????<br>
     * ????????????https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory
     *
     * @param keySpec {@link KeySpec}
     * @return ?????? {@link PublicKey}
     */
    @Nonnull
    public PublicKey generatePublicKey(@Nonnull KeySpec keySpec) {
        return cryptoAlgorithm.generatePublicKey(keySpec);
    }

    //------------------------------------------------------------------------------------------------------------------
    //??????warp
    //------------------------------------------------------------------------------------------------------------------

    @Nonnull
    public byte[] wrapWithPBE(@Nonnull Key wrappedKey, String password,
                              @Nonnull AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        return cryptoAlgorithm.wrapWithPBE(wrappedKey, password, params, random);
    }

    @Nonnull
    public Key unwrapWithPBE(@Nonnull byte[] wrappedKey, String password,
                             @Nonnull AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        return cryptoAlgorithm.unwrapWithPBE(wrappedKey, password, params, random);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey) throws CryptoException {
        return cryptoAlgorithm.wrap(wrappedKey);
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey) throws CryptoException {
        return cryptoAlgorithm.unwrap(wrappedKey);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey, @Nonnull Key key) throws CryptoException {
        return cryptoAlgorithm.wrap(wrappedKey, key);
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey, @Nonnull Key key) throws CryptoException {
        return cryptoAlgorithm.unwrap(wrappedKey, key);
    }

    @Nonnull
    public byte[] wrap(@Nonnull Key wrappedKey, @Nonnull Key key,
                       @Nullable AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        return cryptoAlgorithm.wrap(wrappedKey, key, params, random);
    }

    @Nonnull
    public Key unwrap(@Nonnull byte[] wrappedKey, @Nonnull Key key,
                      @Nullable AlgorithmParameterSpec params, @Nullable SecureRandom random) throws CryptoException {
        return cryptoAlgorithm.unwrap(wrappedKey, key, params, random);
    }


    @Nonnull
    public String getAlgorithm() {
        return cryptoAlgorithm.algorithm;
    }

    public int getMaxEncryptBlock() {
        return cryptoAlgorithm.getEncryptBlock(keySize);
    }

    public int getMaxDecryptBlock() {
        return cryptoAlgorithm.getDecryptBlock(keySize);
    }

    @Nonnull
    public CryptoService getPublicAsymmetricEncryptionService() {
        cryptoAlgorithm.checkAsymmetric();
        return cryptoServices[0];
    }

    @Nonnull
    public CryptoService getPrivateAsymmetricEncryptionService() {
        cryptoAlgorithm.checkAsymmetric();
        return cryptoServices[1];
    }

    @Nonnull
    public CryptoService getSymmetricEncryptionService() {
        cryptoAlgorithm.checkSymmetric();
        return cryptoServices[0];
    }

    @Nullable
    protected Provider getProvider() {
        return provider;
    }

    @Nullable
    protected String getProviderName() {
        return providerName;
    }

    @Nonnull
    private byte[] handle(@Nonnull byte[] data, int mode, @Nonnull Key key) throws CryptoException {
        try {
            if (isSegment) {
                return computeSegment(data, mode, key);
            } else {
                return computeFull(data, mode, key);
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new CryptoException(e);
        }
    }

    @Nonnull
    private InputStream handle(@Nonnull InputStream data, int mode, @Nonnull Key key)
            throws CryptoException {
        try {
            if (isSegment) {
                return computeSegment(data, mode, key);
            } else {
                return computeFull(data, mode, key);
            }
        } catch (BadPaddingException | IOException | IllegalBlockSizeException e) {
            throw new CryptoException(e);
        }
    }

    @Nonnull
    private byte[] computeSegment(@Nonnull byte[] data, int mode, @Nonnull Key key)
            throws BadPaddingException, IllegalBlockSizeException, CryptoException {
        Cipher cipher = cryptoAlgorithm.getCipher(mode, key);
        final int block = getBlock(mode);
        ByteArrayOutputStream out =
                new ByteArrayOutputStream(ToolNumber.extendBinaryMultiple(data.length, block));
        byte[] temp = new byte[block];
        byte[] doFinal;
        // ?????????????????????????????????, ??????????????????
        for (int i = 0; i < data.length; i += block) {
            // ?????????????????? ??????/?????????????????????
            int dataLength = Math.min(data.length - i, block);
            System.arraycopy(data, i, temp, 0, dataLength);
            doFinal = cipher.doFinal(temp, 0, dataLength);
            out.write(doFinal, 0, doFinal.length);
        }
        return out.toByteArray();
    }

    @Nonnull
    private InputStream computeSegment(@Nonnull InputStream data, int mode, @Nonnull Key key)
            throws BadPaddingException, IllegalBlockSizeException, CryptoException, IOException {
        Cipher cipher = cryptoAlgorithm.getCipher(mode, key);
        final int block = getBlock(mode);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] temp = new byte[block];
        byte[] doFinal;
        int dataLength;
        while ((dataLength = data.read(temp)) >= 0) {
            doFinal = cipher.doFinal(temp, 0, dataLength);
            out.write(doFinal, 0, doFinal.length);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Nonnull
    private byte[] computeFull(@Nonnull byte[] data, int mode, @Nonnull Key key)
            throws BadPaddingException, IllegalBlockSizeException, CryptoException {
        return cryptoAlgorithm.getCipher(mode, key, algorithmParameterSpec, null).doFinal(data);
    }

    private InputStream computeFull(@Nonnull InputStream data, int mode, @Nonnull Key key)
            throws CryptoException, IOException {
        Cipher cipher = cryptoAlgorithm.getCipher(mode, key, algorithmParameterSpec, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (mode == Cipher.DECRYPT_MODE) {
            CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
            ToolIO.write(cipherOutputStream, data);
            cipherOutputStream.close();
            return new ByteArrayInputStream(out.toByteArray());
        } else if (mode == Cipher.ENCRYPT_MODE) {
            CipherInputStream cipherInputStream = new CipherInputStream(data, cipher);
            ToolIO.write(out, cipherInputStream);
            cipherInputStream.close();
            return new ByteArrayInputStream(out.toByteArray());
        } else {
            throw new CryptoException("");
        }
    }

    private int getBlock(int mode) {
        if (Cipher.ENCRYPT_MODE == mode || Cipher.WRAP_MODE == mode) {
            return getMaxEncryptBlock();
        } else if (Cipher.DECRYPT_MODE == mode || Cipher.UNWRAP_MODE == mode) {
            return getMaxDecryptBlock();
        }
        throw new IllegalArgumentException();
    }


    private class CryptoServiceImpl implements CryptoService {

        private final Function<byte[], Key> keyFunction;

        private CryptoServiceImpl(Function<byte[], Key> keyFunction) {
            this.keyFunction = keyFunction;
        }

        @Override
        public byte[] encrypt(byte[] data, byte[] key) throws CryptoException {
            return handle(data, Cipher.ENCRYPT_MODE, keyFunction.apply(key));
        }

        @Override
        public byte[] decrypt(byte[] data, byte[] key) throws CryptoException {
            return handle(data, Cipher.DECRYPT_MODE, keyFunction.apply(key));
        }

        @Override
        public InputStream encrypt(InputStream data, byte[] key) throws CryptoException {
            return handle(data, Cipher.ENCRYPT_MODE, keyFunction.apply(key));
        }

        @Override
        public InputStream decrypt(InputStream data, byte[] key) throws CryptoException {
            return handle(data, Cipher.DECRYPT_MODE, keyFunction.apply(key));
        }
    }

}
