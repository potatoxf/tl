package pxf.tl.net.protocol;


import pxf.tl.api.PoolOfArray;

/**
 * 协议助手类
 *
 * @author potatoxf
 */
public final class ProtocolHelper {

    /**
     * 协议键
     */
    public static final String URL_PROTOCOL_KEY = "java.protocol.handler.pkgs";
    /**
     * 扩展协议，window下盘符
     */
    private static final String EXTEND_URL_PROTOCOL = ProtocolHelper.class.getPackage().getName();

    private ProtocolHelper() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 加载协议
     *
     * @see #load(String)
     */
    public static void load() {
        load(null);
    }

    /**
     * 加载协议
     *
     * @param urlProtocolPackage url协议包名
     */
    public static void load(String urlProtocolPackage) {
        String[] urlProtocolPackages;
        if (urlProtocolPackage == null) {
            urlProtocolPackages = PoolOfArray.EMPTY_STRING_ARRAY;
        } else {
            urlProtocolPackages = urlProtocolPackage.split("\\|");
        }
        String property = System.getProperty(URL_PROTOCOL_KEY);
        if (property == null) {
            StringBuilder sb = new StringBuilder(50);
            for (String protocolPackage : urlProtocolPackages) {
                sb.append(protocolPackage.trim()).append('|');
            }
            sb.setLength(sb.length() - 1);
            sb.append('|').append(EXTEND_URL_PROTOCOL);
            property = sb.toString();
        } else {
            if (property.contains(EXTEND_URL_PROTOCOL) && urlProtocolPackage == null) {
                // 不需要重复加载
                return;
            }
            StringBuilder sb = new StringBuilder(50);
            sb.append(property);
            sb.append('|').append(EXTEND_URL_PROTOCOL);
            for (String protocolPackage : urlProtocolPackages) {
                protocolPackage = protocolPackage.trim();
                if (sb.indexOf(protocolPackage) <= -1) {
                    sb.append(protocolPackage).append('|');
                }
            }
            sb.setLength(sb.length() - 1);
            property = sb.toString();
        }
        System.setProperty(URL_PROTOCOL_KEY, property);
    }
}
