package pxf.tl.crypto;

import java.io.InputStream;
import java.security.Key;

/**
 * @author potatoxf
 */
public interface CryptoService {

    byte[] encrypt(byte[] data, byte[] key) throws CryptoException;

    byte[] decrypt(byte[] data, byte[] key) throws CryptoException;

    InputStream encrypt(InputStream data, byte[] key) throws CryptoException;

    InputStream decrypt(InputStream data, byte[] key) throws CryptoException;

    default byte[] encrypt(byte[] data, Key key) throws CryptoException {
        return encrypt(data, key.getEncoded());
    }

    default byte[] decrypt(byte[] data, Key key) throws CryptoException {
        return decrypt(data, key.getEncoded());
    }


    default InputStream encrypt(InputStream data, Key key) throws CryptoException {
        return encrypt(data, key.getEncoded());
    }

    default InputStream decrypt(InputStream data, Key key) throws CryptoException {
        return decrypt(data, key.getEncoded());
    }
}
