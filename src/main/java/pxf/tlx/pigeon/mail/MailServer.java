package pxf.tlx.pigeon.mail;


import pxf.tl.api.PoolOfPattern;
import pxf.tl.help.Assert;
import pxf.tl.util.ToolObject;

import javax.mail.Session;
import javax.mail.URLName;
import java.util.Properties;

/**
 * 邮件服务
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public abstract class MailServer<T extends MailServer<T>> implements AutoCloseable {
    /**
     * 协议范围
     */
    private static final String[] PROTOCOLS = new String[]{"smtp", "imap", "pop3"};

    protected String protocol;
    protected String host;
    protected int port;
    protected String file;
    protected String username;
    protected String password;
    private boolean enableAuth = true;
    private boolean enableSsl = true;
    private boolean enableDebug = false;

    /**
     * 缓存URLName
     */
    private transient volatile URLName cacheUrlName;

    /**
     * 缓存Session
     */
    private transient volatile Session cacheSession;

    public boolean isErrorMailAddress(String mailAddress) {
        return !PoolOfPattern.EMAIL.matcher(mailAddress).matches();
    }

    /**
     * 获取邮件URLName
     *
     * @return URLName
     */
    public final URLName obtainOrCreateUrlName() {
        if (cacheUrlName == null) {
            check();
            synchronized (this) {
                if (cacheUrlName == null) {
                    cacheUrlName = new URLName(protocol, host, port, file, username, password);
                }
            }
        }
        return cacheUrlName;
    }

    /**
     * 获取邮件会话
     *
     * @return Session
     */
    public final Session obtainOrCreateSession() {
        if (cacheSession == null) {
            check();
            synchronized (this) {
                if (cacheSession == null) {
                    // 1、连接邮件服务器的参数配置
                    Properties properties = new Properties();
                    // 设置发件人的SMTP服务器地址
                    properties.setProperty("mail." + protocol + ".host", host);
                    // 发件人账户名
                    properties.setProperty("mail." + protocol + ".user", username);
                    // 端口号  不要使用25
                    properties.setProperty("mail." + protocol + ".port", String.valueOf(port));
                    // 设置用户的认证方式
                    properties.setProperty("mail." + protocol + ".auth", String.valueOf(enableAuth));
                    // 设置是否使用ssl安全连接 ---一般都使用
                    properties.setProperty("mail." + protocol + ".ssl.enable", String.valueOf(enableSsl));
                    // 设置是否显示debug信息 true 会在控制台显示相关信息
                    properties.setProperty("mail.debug", String.valueOf(enableDebug));
                    initProperties(properties);
                    // 2、创建定义整个应用程序所需的环境信息的 Session 对象
                    cacheSession = Session.getInstance(properties, new MailAuthenticator(password));
                    cacheSession.setDebug(enableDebug);
                }
            }
        }
        return cacheSession;
    }

    /**
     * 初始化属性参数
     *
     * @param properties Properties
     */
    protected void initProperties(Properties properties) {
    }

    public T set(Properties properties) {
        String username = properties.getProperty("username");
        if (username != null) {
            setUsername(username);
        }
        String password = properties.getProperty("password");
        if (password != null) {
            setPassword(password);
        }
        return (T) this;
    }

    /**
     * 检查属性
     */
    private void check() {
        if (checkProtocol()) {
            Assert.in(protocol, PROTOCOLS, "The protocol must be set!");
        }
        if (checkHost()) {
            Assert.nonvl(host, "The host must be set!");
        }
        if (checkHost()) {
            int[] portRange = getPortRange();
            Assert.in(port, portRange, "The port must be set and in " + ToolObject.toString(portRange));
        }
        if (checkFile()) {
            Assert.nonvl(file, "The file must be set!");
        }
        if (checkUsername()) {
            Assert.hasText(username, "The username must be set!");
        }
        if (checkPassword()) {
            Assert.hasText(password, "The password must be set!");
        }
    }

    /**
     * 获取端口范围
     *
     * @return int[]
     */
    protected abstract int[] getPortRange();

    /**
     * 是否检查协议
     *
     * @return 如果检查返回true否则返回false
     */
    protected boolean checkProtocol() {
        return true;
    }

    /**
     * 是否检查主机地址
     *
     * @return 如果检查返回true否则返回false
     */
    protected boolean checkHost() {
        return true;
    }

    /**
     * 是否检查文件
     *
     * @return 如果检查返回true否则返回false
     */
    protected boolean checkFile() {
        return true;
    }

    /**
     * 是否检查用户名
     *
     * @return 如果检查返回true否则返回false
     */
    protected boolean checkUsername() {
        return true;
    }

    /**
     * 是否检查密码
     *
     * @return 如果检查返回true否则返回false
     */
    protected boolean checkPassword() {
        return true;
    }

    /**
     * 重置服务
     */
    public void reset() {
        cacheSession = null;
        cacheUrlName = null;
    }

    @Override
    public void close() throws Exception {
    }

    public final boolean isEnableAuth() {
        return enableAuth;
    }

    public final T setEnableAuth(boolean enableAuth) {
        this.enableAuth = enableAuth;
        return (T) this;
    }

    public final boolean isEnableSsl() {
        return enableSsl;
    }

    public final T setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return (T) this;
    }

    public final boolean isEnableDebug() {
        return enableDebug;
    }

    public final T setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
        return (T) this;
    }

    public final String getProtocol() {
        return protocol;
    }

    public final String getHost() {
        return host;
    }

    public final T setHost(String host) {
        this.host = host;
        return (T) this;
    }

    public final int getPort() {
        return port;
    }

    public final T setPort(int port) {
        this.port = port;
        return (T) this;
    }

    public final String getFile() {
        return file;
    }

    public final String getUsername() {
        return username;
    }

    public final T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public final String getPassword() {
        return password;
    }

    public final T setPassword(String password) {
        this.password = password;
        return (T) this;
    }
}
