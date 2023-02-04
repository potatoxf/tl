package pxf.tlx.pigeon.mail;


import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.Properties;

/**
 * Pop3邮件服务
 *
 * @author potatoxf
 */
public class Pop3Server extends MailReceiverServer<Pop3Server> {

    protected Pop3Server() {
        this.protocol = "pop3";
        this.file = "inbox";
    }

    public static Pop3Server of() {
        return new Pop3Server();
    }

    /**
     * 获取邮件文件夹
     *
     * @return Folder
     * @throws MessagingException 如果读取邮件发生异常
     */
    public Folder obtainFolder() throws MessagingException {
        return obtainOrCreateStore().getFolder(obtainOrCreateUrlName());
    }

    @Override
    protected int[] getPortRange() {
        return new int[]{110, 995};
    }

    @Override
    public Pop3Server set(Properties properties) {
        super.set(properties);
        String host = properties.getProperty("host");
        if (host != null) {
            setHost(host);
        }
        int port = Integer.parseInt(properties.getProperty("port"));
        if (port > 0) {
            setPort(port);
        }
        return this;
    }

    @Override
    protected boolean checkFile() {
        return false;
    }
}
