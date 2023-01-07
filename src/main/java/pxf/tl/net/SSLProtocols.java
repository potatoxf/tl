package pxf.tl.net;

import pxf.tl.exception.IORuntimeException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * SSL或TLS协议
 *
 * @author potatoxf
 */
public interface SSLProtocols {

    /**
     * Supports some version of SSL; may support other versions
     */
    String SSL = "SSL";
    /**
     * Supports SSL version 2 or later; may support other versions
     */
    String SSLv2 = "SSLv2";
    /**
     * Supports SSL version 3; may support other versions
     */
    String SSLv3 = "SSLv3";

    /**
     * Supports some version of TLS; may support other versions
     */
    String TLS = "TLS";
    /**
     * Supports RFC 2246: TLS version 1.0 ; may support other versions
     */
    String TLSv1 = "TLSv1";
    /**
     * Supports RFC 4346: TLS version 1.1 ; may support other versions
     */
    String TLSv11 = "TLSv1.1";
    /**
     * Supports RFC 5246: TLS version 1.2 ; may support other versions
     */
    String TLSv12 = "TLSv1.2";

    /**
     * 创建{@link SSLContext}，默认新人全部
     *
     * @param protocol SSL协议，例如TLS等
     * @return {@link SSLContext}
     * @throws IORuntimeException 包装 GeneralSecurityException异常
     */
    static SSLContext createSSLContext(String protocol) throws IORuntimeException {
        return SSLContextBuilder.create().setProtocol(protocol).build();
    }

    /**
     * 创建{@link SSLContext}
     *
     * @param protocol     SSL协议，例如TLS等
     * @param keyManager   密钥管理器,{@code null}表示无
     * @param trustManager 信任管理器, {@code null}表示无
     * @return {@link SSLContext}
     * @throws IORuntimeException 包装 GeneralSecurityException异常
     */
    static SSLContext createSSLContext(
            String protocol, KeyManager keyManager, TrustManager trustManager) throws IORuntimeException {
        return createSSLContext(
                protocol,
                keyManager == null ? null : new KeyManager[]{keyManager},
                trustManager == null ? null : new TrustManager[]{trustManager});
    }

    /**
     * 创建和初始化{@link SSLContext}
     *
     * @param protocol      SSL协议，例如TLS等
     * @param keyManagers   密钥管理器,{@code null}表示无
     * @param trustManagers 信任管理器, {@code null}表示无
     * @return {@link SSLContext}
     * @throws IORuntimeException 包装 GeneralSecurityException异常
     */
    static SSLContext createSSLContext(
            String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers)
            throws IORuntimeException {
        return SSLContextBuilder.create()
                .setProtocol(protocol)
                .setKeyManagers(keyManagers)
                .setTrustManagers(trustManagers)
                .build();
    }
}
