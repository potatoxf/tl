package pxf.tlx.pigeon.mail;


import java.util.Properties;

/**
 * smtp邮件服务
 *
 * @author potatoxf
 */
public class SmtpServer extends MailSenderServer<SmtpServer> {

    protected SmtpServer() {
        this.protocol = "smtp";
    }

    public static SmtpServer of() {
        return new SmtpServer();
    }

    @Override
    protected int[] getPortRange() {
        return new int[]{465, 587};
    }

    @Override
    public SmtpServer set(Properties properties) {
        super.set(properties);
        String host = properties.getProperty("host");
        if (host != null) {
            setHost(host);
        }
        int port = Integer.parseInt(properties.getProperty("port"));
        if (port > 0) {
            setPort(port);
        }
        String from = properties.getProperty("from");
        if (from != null) {
            setDefaultFromAddress(from);
        }
        String sender = properties.getProperty("sender");
        if (from != null) {
            setDefaultSenderAddress(sender);
        }
        return this;
    }

    @Override
    protected boolean checkFile() {
        return false;
    }
}
