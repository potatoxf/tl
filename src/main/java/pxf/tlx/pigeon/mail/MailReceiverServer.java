package pxf.tlx.pigeon.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.util.Properties;

/**
 * 邮件接收服务
 *
 * @author potatoxf
 */
public abstract class MailReceiverServer<T extends MailReceiverServer<T>> extends MailServer<T> {

    /**
     * 缓存Store
     */
    private transient volatile Store cacheStore;

    @Override
    protected void initProperties(Properties properties) {
        // 设置传输协议
        properties.setProperty("mail.store.protocol", "pop3");
    }

    /**
     * 获取邮件存贮，已经连接
     *
     * @return Store
     * @throws MessagingException 如果发生异常
     */
    public final Store obtainOrCreateStore() throws MessagingException {
        if (cacheStore == null) {
            synchronized (this) {
                if (cacheStore == null) {
                    Session session = obtainOrCreateSession();
                    URLName urlName = obtainOrCreateUrlName();
                    cacheStore = session.getStore(urlName);
                    cacheStore.connect();
                }
            }
        } else {
            if (!cacheStore.isConnected()) {
                cacheStore.connect();
            }
        }
        return cacheStore;
    }

    /**
     * 获取端口范围
     *
     * @return int[]
     */
    @Override
    protected int[] getPortRange() {
        return new int[0];
    }

    @Override
    public void close() throws Exception {
        if (cacheStore != null) {
            cacheStore.close();
        }
    }
}
