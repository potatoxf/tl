package pxf.tl.util;


import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Valid;
import pxf.tl.help.Whether;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author potatoxf
 */
public final class ToolSecurity {

    private static final FunctionThrow<PrivateKey, KeySpec, Throwable> RSA_KEY_SPEC_FACTORY =
            privateKey -> {
                RSAPrivateCrtKey key = (RSAPrivateCrtKey) privateKey;
                return new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            };

    /**
     * 从PKCS12格式证书加载RSA公私钥
     *
     * @param keyAlias    key别名
     * @param keyPassword key密码
     * @param filePath    证书文件路径
     * @return 返回{@code KeyPair}，不会为null
     * @throws InvalidKeySpecException   if KeySpec cannot be obtained
     * @throws KeyException              If the key from the certificate does not include Private Key
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password
     *                                   is required but not given, or if the given password was incorrect. If the error is due to a
     *                                   wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                   be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore
     *                                   cannot be found
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is
     *                                   wrong).
     */
    public static KeyPair loadRSAKeyPairFromPKCS12InFile(
            String keyAlias, String keyPassword, String filePath)
            throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException,
            KeyStoreException, IOException, CertificateException, KeyException {
        return loadAsymmetricKeyPairInFile(
                "PKCS12", "RSA", keyAlias, keyPassword, filePath, RSA_KEY_SPEC_FACTORY);
    }

    /**
     * 从PKCS12格式证书加载RSA公私钥
     *
     * @param keyAlias    key别名
     * @param keyPassword key密码
     * @param inputStream 证书数据
     * @return 返回{@code KeyPair}，不会为null
     * @throws InvalidKeySpecException   if KeySpec cannot be obtained
     * @throws KeyException              If the key from the certificate does not include Private Key
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password
     *                                   is required but not given, or if the given password was incorrect. If the error is due to a
     *                                   wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                   be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore
     *                                   cannot be found
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is
     *                                   wrong).
     */
    public static KeyPair loadRSAKeyPairFromPKCS12(
            String keyAlias, String keyPassword, InputStream inputStream)
            throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException,
            KeyStoreException, IOException, CertificateException, KeyException {
        return loadAsymmetricKeyPair(
                "PKCS12", "RSA", keyAlias, keyPassword, inputStream, RSA_KEY_SPEC_FACTORY);
    }

    /**
     * 加载公私钥.
     *
     * @param storeType        证书存储类型
     * @param alg              加密算法
     * @param keyAlias         key别名
     * @param keyPassword      key密码
     * @param keyStoreFilePath 证书文件路径
     * @param keySpecFactory   KeySpec获取器
     * @return 返回{@code KeyPair}，不会为null
     * @throws InvalidKeySpecException   if KeySpec cannot be obtained
     * @throws KeyException              If the key from the certificate does not include Private Key
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password
     *                                   is required but not given, or if the given password was incorrect. If the error is due to a
     *                                   wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                   be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore
     *                                   cannot be found
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is
     *                                   wrong).
     */
    public static KeyPair loadAsymmetricKeyPairInFile(
            String storeType,
            String alg,
            String keyAlias,
            String keyPassword,
            String keyStoreFilePath,
            FunctionThrow<PrivateKey, KeySpec, Throwable> keySpecFactory)
            throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException,
            KeyStoreException, IOException, CertificateException, KeyException {
        try (FileInputStream fileInputStream = new FileInputStream(keyStoreFilePath)) {
            return doLoadAsymmetricKeyPair(
                    storeType,
                    alg,
                    keyAlias,
                    Valid.val(keyPassword).toCharArray(),
                    fileInputStream,
                    keySpecFactory);
        }
    }

    /**
     * 加载公私钥.
     *
     * @param storeType      证书存储类型
     * @param alg            加密算法
     * @param keyAlias       key别名
     * @param keyPassword    key密码
     * @param inputStream    证书数据流
     * @param keySpecFactory KeySpec获取器
     * @return 返回{@code KeyPair}，不会为null
     * @throws InvalidKeySpecException   if KeySpec cannot be obtained
     * @throws KeyException              If the key from the certificate does not include Private Key
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password
     *                                   is required but not given, or if the given password was incorrect. If the error is due to a
     *                                   wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                   be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore
     *                                   cannot be found
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is
     *                                   wrong).
     */
    public static KeyPair loadAsymmetricKeyPair(
            String storeType,
            String alg,
            String keyAlias,
            String keyPassword,
            InputStream inputStream,
            FunctionThrow<PrivateKey, KeySpec, Throwable> keySpecFactory)
            throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException,
            KeyStoreException, IOException, CertificateException, KeyException {
        return doLoadAsymmetricKeyPair(
                storeType,
                alg,
                keyAlias,
                Valid.val(keyPassword).toCharArray(),
                inputStream,
                keySpecFactory);
    }

    /**
     * 加载公私钥.
     *
     * @param storeType           证书存储类型
     * @param alg                 加密算法
     * @param keyAlias            key别名
     * @param keyPassword         key密码
     * @param keyStoreInputStream 证书数据流
     * @param keySpecFactory      KeySpec获取器
     * @return 返回{@code KeyPair}，不会为null
     * @throws InvalidKeySpecException   if KeySpec cannot be obtained
     * @throws KeyException              If the key from the certificate does not include Private Key
     * @throws IOException               if there is an I/O or format problem with the keystore data, if a password
     *                                   is required but not given, or if the given password was incorrect. If the error is due to a
     *                                   wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                   be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException  if the algorithm used to check the integrity of the keystore
     *                                   cannot be found
     * @throws CertificateException      if any of the certificates in the keystore could not be loaded
     * @throws KeyStoreException         if the keystore has not been initialized (loaded).
     * @throws NoSuchAlgorithmException  if the algorithm for recovering the key cannot be found
     * @throws UnrecoverableKeyException if the key cannot be recovered (e.g., the given password is
     *                                   wrong).
     */
    private static KeyPair doLoadAsymmetricKeyPair(
            String storeType,
            String alg,
            String keyAlias,
            char[] keyPassword,
            InputStream keyStoreInputStream,
            FunctionThrow<PrivateKey, KeySpec, Throwable> keySpecFactory)
            throws NoSuchAlgorithmException, InvalidKeySpecException, UnrecoverableKeyException,
            KeyStoreException, IOException, CertificateException, KeyException {
        KeyStore store = doLoadKeyStore(storeType, keyStoreInputStream, keyPassword);
        Key key = store.getKey(keyAlias, keyPassword);
        if (!(key instanceof PrivateKey privateKey)) {
            throw new KeyException("Unable to get private key from alias '" + keyAlias + "'");
        }
        KeySpec keySpec;
        try {
            keySpec = keySpecFactory.apply(privateKey);
        } catch (Throwable e) {
            throw new InvalidKeySpecException("Unable to get 'KeySpec' from private key", e);
        }
        return new KeyPair(KeyFactory.getInstance(alg).generatePublic(keySpec), privateKey);
    }

    /**
     * 获取证书存储
     *
     * @param storeType        证书存储类型
     * @param keyPassword      key密码
     * @param keyStoreFilePath 证书数据流
     * @return {@code KeyStore}
     * @throws KeyStoreException        if the keystore has not been initialized (loaded).
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws IOException              if there is an I/O or format problem with the keystore data, if a password
     *                                  is required but not given, or if the given password was incorrect. If the error is due to a
     *                                  wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                  be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore
     *                                  cannot be found
     */
    public static KeyStore loadKeyStoreInFile(
            String storeType, String keyStoreFilePath, String keyPassword)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(keyStoreFilePath)) {
            return loadKeyStore(storeType, fileInputStream, keyPassword);
        }
    }

    /**
     * 获取证书存储
     *
     * @param storeType           证书存储类型
     * @param keyPassword         key密码
     * @param keyStoreInputStream 证书数据流
     * @return {@code KeyStore}
     * @throws KeyStoreException        if the keystore has not been initialized (loaded).
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws IOException              if there is an I/O or format problem with the keystore data, if a password
     *                                  is required but not given, or if the given password was incorrect. If the error is due to a
     *                                  wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                  be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore
     *                                  cannot be found
     */
    public static KeyStore loadKeyStore(
            String storeType, InputStream keyStoreInputStream, String keyPassword)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        return doLoadKeyStore(storeType, keyStoreInputStream, keyPassword.toCharArray());
    }

    /**
     * 获取证书存储
     *
     * @param storeType           证书存储类型
     * @param keyPassword         key密码
     * @param keyStoreInputStream 证书数据流
     * @return {@code KeyStore}
     * @throws KeyStoreException        if the keystore has not been initialized (loaded).
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws IOException              if there is an I/O or format problem with the keystore data, if a password
     *                                  is required but not given, or if the given password was incorrect. If the error is due to a
     *                                  wrong password, the {@link Throwable#getCause cause} of the <code>IOException</code> should
     *                                  be an <code>UnrecoverableKeyException</code>
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore
     *                                  cannot be found
     */
    private static KeyStore doLoadKeyStore(
            String storeType, InputStream keyStoreInputStream, char[] keyPassword)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Whether.empty(storeType)) {
            storeType = KeyStore.getDefaultType();
        }
        KeyStore keyStore = KeyStore.getInstance(storeType);
        keyStore.load(keyStoreInputStream, keyPassword);
        return keyStore;
    }
}
