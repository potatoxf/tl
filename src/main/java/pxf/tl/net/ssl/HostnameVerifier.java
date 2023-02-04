package pxf.tl.net.ssl;

import javax.net.ssl.SSLSession;

/**
 * 默认主机名验证
 *
 * @author potatoxf
 */
public class HostnameVerifier implements javax.net.ssl.HostnameVerifier {

    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }
}
