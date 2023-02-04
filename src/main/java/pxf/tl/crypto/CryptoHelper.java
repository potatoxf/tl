package pxf.tl.crypto;


import pxf.tl.io.FileUtil;
import pxf.tl.math.Hex;
import pxf.tl.util.ToolIO;
import pxf.tl.util.ToolRegex;
import pxf.tlx.codec.codec.Base64;

import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * 密钥工具类
 *
 * <p>包括:
 *
 * <pre>
 * 1、生成密钥（单密钥、密钥对）
 * 2、读取密钥文件
 * </pre>
 *
 * @author potatoxf
 */
public class CryptoHelper {

    /**
     * Java密钥库(Java Key Store，JKS)KEY_STORE
     */
    public static final String KEY_TYPE_JKS = "JKS";
    /**
     * jceks
     */
    public static final String KEY_TYPE_JCEKS = "jceks";
    /**
     * PKCS12是公钥加密标准，它规定了可包含所有私钥、公钥和证书。其以二进制格式存储，也称为 PFX 文件
     */
    public static final String KEY_TYPE_PKCS12 = "pkcs12";
    /**
     * Certification类型：X.509
     */
    public static final String CERT_TYPE_X509 = "X.509";
    /**
     * 默认密钥字节数
     *
     * <pre>
     * RSA/DSA
     * Default Keysize 1024
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
     * </pre>
     */
    public static final int DEFAULT_KEY_SIZE = 1024;

    /**
     * 生成私钥，仅用于非对称加密
     *
     * @param keyStore {@link KeyStore}
     * @param alias    别名
     * @param password 密码
     * @return 私钥 {@link PrivateKey}
     */
    public static PrivateKey generatePrivateKey(KeyStore keyStore, String alias, char[] password) {
        try {
            return (PrivateKey) keyStore.getKey(alias, password);
        } catch (Throwable e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param keyFile  证书文件
     * @param password 密码
     * @return {@link KeyStore}
     */
    public static KeyStore readJKSKeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, keyFile, password);
    }

    /**
     * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link
     *                 FileUtil#getInputStream(File)} 读取
     * @param password 密码
     * @return {@link KeyStore}
     */
    public static KeyStore readJKSKeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_JKS, in, password);
    }

    /**
     * 读取PKCS12 KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存
     *
     * @param keyFile  证书文件
     * @param password 密码
     * @return {@link KeyStore}
     */
    public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, keyFile, password);
    }

    /**
     * 读取PKCS12 KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存
     *
     * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link
     *                 FileUtil#getInputStream(File)} 读取
     * @param password 密码
     * @return {@link KeyStore}
     */
    public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) {
        return readKeyStore(KEY_TYPE_PKCS12, in, password);
    }

    /**
     * 读取KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param type     类型
     * @param keyFile  证书文件
     * @param password 密码，null表示无密码
     * @return {@link KeyStore}
     */
    public static KeyStore readKeyStore(String type, File keyFile, char[] password) {
        InputStream in = null;
        try {
            in = FileUtil.getInputStream(keyFile);
            return readKeyStore(type, in, password);
        } finally {
            ToolIO.closes(in);
        }
    }

    /**
     * 读取KeyStore文件<br>
     * KeyStore文件用于数字证书的密钥对保存<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param type     类型
     * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link
     *                 FileUtil#getInputStream(File)} 读取
     * @param password 密码，null表示无密码
     * @return {@link KeyStore}
     */
    public static KeyStore readKeyStore(String type, InputStream in, char[] password) {
        KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(type);
            keyStore.load(in, password);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return keyStore;
    }

    /**
     * 从KeyStore中获取私钥公钥
     *
     * @param type     类型
     * @param in       {@link InputStream} 如果想从文件读取.keystore文件，使用 {@link
     *                 FileUtil#getInputStream(File)} 读取
     * @param password 密码
     * @param alias    别名
     * @return {@link KeyPair}
     */
    public static KeyPair getKeyPair(String type, InputStream in, char[] password, String alias) {
        final KeyStore keyStore = readKeyStore(type, in, password);
        return getKeyPair(keyStore, password, alias);
    }

    /**
     * 从KeyStore中获取私钥公钥
     *
     * @param keyStore {@link KeyStore}
     * @param password 密码
     * @param alias    别名
     * @return {@link KeyPair}
     */
    public static KeyPair getKeyPair(KeyStore keyStore, char[] password, String alias) {
        PublicKey publicKey;
        PrivateKey privateKey;
        try {
            publicKey = keyStore.getCertificate(alias).getPublicKey();
            privateKey = (PrivateKey) keyStore.getKey(alias, password);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 读取X.509 Certification文件<br>
     * Certification为证书文件<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param in       {@link InputStream} 如果想从文件读取.cer文件，使用 {@link FileUtil#getInputStream(File)}
     *                 读取
     * @param password 密码
     * @param alias    别名
     * @return {@link KeyStore}
     */
    public static Certificate readX509Certificate(InputStream in, char[] password, String alias) {
        return readCertificate(CERT_TYPE_X509, in, password, alias);
    }

    /**
     * 读取X.509 Certification文件中的公钥<br>
     * Certification为证书文件<br>
     * see: https://www.cnblogs.com/yinliang/p/10115519.html
     *
     * @param in {@link InputStream} 如果想从文件读取.cer文件，使用 {@link FileUtil#getInputStream(File)}
     *           读取
     * @return {@link KeyStore}
     */
    public static PublicKey readPublicKeyFromCert(InputStream in) {
        final Certificate certificate = readX509Certificate(in);
        if (null != certificate) {
            return certificate.getPublicKey();
        }
        return null;
    }

    /**
     * 读取X.509 Certification文件<br>
     * Certification为证书文件<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param in {@link InputStream} 如果想从文件读取.cer文件，使用 {@link FileUtil#getInputStream(File)}
     *           读取
     * @return {@link KeyStore}
     */
    public static Certificate readX509Certificate(InputStream in) {
        return readCertificate(CERT_TYPE_X509, in);
    }

    /**
     * 读取Certification文件<br>
     * Certification为证书文件<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param type     类型，例如X.509
     * @param in       {@link InputStream} 如果想从文件读取.cer文件，使用 {@link FileUtil#getInputStream(File)}
     *                 读取
     * @param password 密码
     * @param alias    别名
     * @return {@link KeyStore}
     */
    public static Certificate readCertificate(
            String type, InputStream in, char[] password, String alias) {
        final KeyStore keyStore = readKeyStore(type, in, password);
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 读取Certification文件<br>
     * Certification为证书文件<br>
     * see: http://snowolf.iteye.com/blog/391931
     *
     * @param type 类型，例如X.509
     * @param in   {@link InputStream} 如果想从文件读取.cer文件，使用 {@link FileUtil#getInputStream(File)}
     *             读取
     * @return {@link Certificate}
     */
    public static Certificate readCertificate(String type, InputStream in) {
        try {
            return getCertificateFactory(type).generateCertificate(in);
        } catch (CertificateException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获得 Certification
     *
     * @param keyStore {@link KeyStore}
     * @param alias    别名
     * @return {@link Certificate}
     */
    public static Certificate getCertificate(KeyStore keyStore, String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 获取{@link CertificateFactory}
     *
     * @param type 类型，例如X.509
     * @return {@link KeyPairGenerator}
     */
    public static CertificateFactory getCertificateFactory(String type) {
        final Provider provider = null;

        CertificateFactory factory;
        try {
            factory =
                    (null == provider)
                            ? CertificateFactory.getInstance(type)
                            : CertificateFactory.getInstance(type, provider);
        } catch (CertificateException e) {
            throw new CryptoException(e);
        }
        return factory;
    }


    /**
     * 通过RSA私钥生成RSA公钥
     *
     * @param privateKey RSA私钥
     * @return RSA公钥，null表示私钥不被支持
     */
    public static PublicKey getRSAPublicKey(PrivateKey privateKey) {
        if (privateKey instanceof RSAPrivateCrtKey) {
            final RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;
            return getRSAPublicKey(privk.getModulus(), privk.getPublicExponent());
        }
        return null;
    }

    /**
     * 获得RSA公钥对象
     *
     * @param modulus        Modulus
     * @param publicExponent Public Exponent
     * @return 公钥
     */
    public static PublicKey getRSAPublicKey(String modulus, String publicExponent) {
        return getRSAPublicKey(new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
    }

    /**
     * 获得RSA公钥对象
     *
     * @param modulus        Modulus
     * @param publicExponent Public Exponent
     * @return 公钥
     */
    public static PublicKey getRSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        try {
            return CryptoAlgorithm.RSA.getKeyFactory().generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 增加加密解密的算法提供者，默认优先使用，例如：
     *
     * <pre>
     * addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
     * </pre>
     *
     * @param provider 算法提供者
     */
    public static void addProvider(Provider provider) {
        Security.insertProviderAt(provider, 0);
    }

    /**
     * 解码字符串密钥，可支持的编码如下：
     *
     * <pre>
     * 1. Hex（16进制）编码
     * 1. Base64编码
     * </pre>
     *
     * @param key 被解码的密钥字符串
     * @return 密钥
     */
    public static byte[] decode(String key) {
        return ToolRegex.isHex(key) ? Hex.decodeToBytes(key) : Base64.decode(key);
    }

    /**
     * 创建{@link Signature}
     *
     * @param algorithm 算法
     * @return {@link Signature}
     */
    public static Signature createSignature(String algorithm) {
        final Provider provider = null;

        Signature signature;
        try {
            signature =
                    (null == provider)
                            ? Signature.getInstance(algorithm)
                            : Signature.getInstance(algorithm, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }

        return signature;
    }
}
