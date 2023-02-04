package pxf.tlx.pigeon.mail;


import pxf.tl.help.Valid;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * 邮件发送服务
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public abstract class MailSenderServer<T extends MailSenderServer<T>> extends MailServer<T> {
    /**
     * 默认 Sender Address
     */
    private String defaultSenderAddress;
    /**
     * 默认 From Address
     */
    private String defaultFromAddress;
    /**
     * 缓存Transport
     */
    private transient volatile Transport cacheTransport;

    @Override
    protected void initProperties(Properties properties) {
        // 设置传输协议
        properties.setProperty("mail.transport.protocol", protocol);
    }

    /**
     * 创建新的邮件信息
     *
     * @return MimeMessage
     */
    public final MimeMessage createNewMimeMessage() throws MessagingException {
        return createNewMimeMessage(defaultFromAddress, defaultSenderAddress);
    }

    /**
     * 创建新的邮件信息
     *
     * @param fromAddress From Address
     * @return MimeMessage
     */
    public final MimeMessage createNewMimeMessage(String fromAddress) throws MessagingException {
        return createNewMimeMessage(fromAddress, defaultSenderAddress);
    }

    /**
     * 创建新的邮件信息
     *
     * @param fromAddress   From Address
     * @param senderAddress Sender Address
     * @return MimeMessage
     */
    public final MimeMessage createNewMimeMessage(String fromAddress, String senderAddress)
            throws MessagingException {
        Session session = obtainOrCreateSession();
        fromAddress = Valid.val(fromAddress, defaultFromAddress);
        senderAddress = Valid.val(senderAddress, defaultSenderAddress);
        if (checkDefaultFromAddress() && fromAddress == null) {
            throw new MessagingException("Mail from address must be set");
        } else if (fromAddress != null && isErrorMailAddress(fromAddress)) {
            throw new MessagingException("Error format for Mail from address");
        }
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            if (fromAddress != null) {
                mimeMessage.setFrom(new InternetAddress(fromAddress));
            }
            if (senderAddress != null) {
                if (isErrorMailAddress(senderAddress)) {
                    throw new MessagingException("Error format for Mail sender address");
                }
                mimeMessage.setSender(new InternetAddress(senderAddress));
            }
        } catch (MessagingException e) {
            throw new MessagingException("Error to set address", e);
        }
        mimeMessage.setSentDate(new Date());
        return mimeMessage;
    }

    /**
     * 获取邮件传输器，已经连接
     *
     * @return Transport
     * @throws MessagingException 如果发生异常
     */
    public final Transport obtainOrCreateTransport() throws MessagingException {
        if (cacheTransport == null) {
            synchronized (this) {
                if (cacheTransport == null) {
                    Session session = obtainOrCreateSession();
                    URLName urlName = obtainOrCreateUrlName();
                    cacheTransport = session.getTransport(urlName);
                    cacheTransport.connect();
                }
            }
        } else {
            if (!cacheTransport.isConnected()) {
                cacheTransport.connect();
            }
        }
        return cacheTransport;
    }

    /**
     * 发送信息
     *
     * @param emailMessageContent 邮件消息内容
     * @throws MessagingException 如果发送信息出现异常
     */
    public final void sendMessage(EmailMessageContent emailMessageContent) throws MessagingException {
        MimeMessage mimeMessage = createNewMimeMessage();
        for (InternetAddress recipient : emailMessageContent.getRecipientCC()) {
            mimeMessage.addRecipient(Message.RecipientType.TO, recipient);
        }
        for (InternetAddress recipient : emailMessageContent.getRecipientTO()) {
            mimeMessage.addRecipient(Message.RecipientType.CC, recipient);
        }
        for (InternetAddress recipient : emailMessageContent.getRecipientBCC()) {
            mimeMessage.addRecipient(Message.RecipientType.BCC, recipient);
        }
        String title = emailMessageContent.getTitle();
        if (title != null) {
            mimeMessage.setSubject(title, emailMessageContent.getCharsetToken().get().toString());
        }
        List<MimeBodyPart> nodes = emailMessageContent.getNodes();
        MimeMultipart mimeMultipart = new MimeMultipart();
        for (MimeBodyPart node : nodes) {
            mimeMultipart.addBodyPart(node);
        }
        LinkedHashMap<String, MimeBodyPart> attachmentMap = emailMessageContent.getAttachmentMap();
        for (MimeBodyPart value : attachmentMap.values()) {
            mimeMultipart.addBodyPart(value);
        }
        // 如果有多个附件，可以创建多个多次添加
        mimeMultipart.setSubType("mixed");
        mimeMessage.setContent(mimeMultipart);
        sendMessage(mimeMessage);
    }

    /**
     * 发送信息
     *
     * @param message Message
     * @throws MessagingException 如果发送信息出现异常
     */
    public final void sendMessage(Message message) throws MessagingException {
        Transport transport = null;
        try {
            // 根据session对象获取邮件传输对象Transport
            transport = obtainOrCreateTransport();
            // 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            // 5、关闭邮件连接
            if (transport != null) {
                transport.close();
            }
        }
    }

    public final String getDefaultFromAddress() {
        return defaultFromAddress;
    }

    public final T setDefaultFromAddress(String defaultFromAddress) {
        this.defaultFromAddress = defaultFromAddress;
        return (T) this;
    }

    public final String getDefaultSenderAddress() {
        return defaultSenderAddress;
    }

    public final T setDefaultSenderAddress(String defaultSenderAddress) {
        this.defaultSenderAddress = defaultSenderAddress;
        return (T) this;
    }

    protected boolean checkDefaultFromAddress() {
        return true;
    }

    @Override
    public void close() throws Exception {
        if (cacheTransport != null) {
            cacheTransport.close();
        }
    }
}
