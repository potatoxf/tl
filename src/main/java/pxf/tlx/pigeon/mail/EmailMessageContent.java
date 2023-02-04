package pxf.tlx.pigeon.mail;


import pxf.tl.api.PoolOfPattern;
import pxf.tl.function.FunctionThrow;
import pxf.tl.help.Whether;
import pxf.tl.math.id.Snowflake;
import pxf.tl.util.ToolFile;
import pxf.tlx.pigeon.MessageContent;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author potatoxf
 */
public class EmailMessageContent extends MessageContent {

    public static final String ATTACHMENT = "A";
    private static final String TEXT = "T";
    private static final String IMAGE = "I";
    private final LinkedHashMap<String, MimeBodyPart> imageMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, MimeBodyPart> textMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, MimeBodyPart> attachmentMap = new LinkedHashMap<>();
    /**
     * MimeMessage.RecipientType.TO:发送
     */
    private final Set<InternetAddress> recipientTO = new LinkedHashSet<>();
    /**
     * MimeMessage.RecipientType.CC：抄送
     */
    private final Set<InternetAddress> recipientCC = new LinkedHashSet<>();
    /**
     * MimeMessage.RecipientType.BCC：密送
     */
    private final Set<InternetAddress> recipientBCC = new LinkedHashSet<>();

    private final List<MimeBodyPart> nodes = new LinkedList<>();
    private String title;

    public EmailMessageContent addRecipientTO(String... emails) {
        return addEmail(recipientTO, emails);
    }

    /**
     * 添加收件人
     *
     * @param emails 邮箱
     * @return this
     */
    public EmailMessageContent addRecipientCC(String... emails) {
        return addEmail(recipientCC, emails);
    }

    /**
     * 添加抄送收件人
     *
     * @param emails 邮箱
     * @return this
     */
    public EmailMessageContent addRecipientBCC(String... emails) {
        return addEmail(recipientBCC, emails);
    }

    /**
     * 添加密送收件人
     *
     * @param emails 邮箱
     * @return this
     */
    private EmailMessageContent addEmail(Set<InternetAddress> container, String... emails) {
        for (String email : emails) {
            if (PoolOfPattern.EMAIL.matcher(email).matches()) {
                try {
                    container.add(new InternetAddress(email));
                } catch (AddressException ignored) {
                }
            }
        }
        return this;
    }

    /**
     * 添加附件
     *
     * @param file 文件
     * @return 返回附件内容ID
     * @throws MessagingException 如果添加附件出现异常
     */
    public String addAttachment(File file) throws MessagingException {
        if (file.exists() && file.isFile()) {
            // 9. 创建附件"节点"
            MimeBodyPart attachment = new MimeBodyPart();
            // 读取本地文件
            DataHandler dataHandler = new DataHandler(new FileDataSource(file));
            // 将附件数据添加到"节点"
            attachment.setDataHandler(dataHandler);
            // 设置附件的文件名（需要编码）
            try {
                attachment.setFileName(MimeUtility.encodeText(dataHandler.getName()));
            } catch (UnsupportedEncodingException ignored) {
            }
            String attachmentId = Snowflake.INSTANCE.nextIdString(ATTACHMENT);
            attachment.setContentID(attachmentId);
            attachmentMap.put(attachmentId, attachment);
            return attachmentId;
        } else {
            throw new IllegalArgumentException("File does not exist");
        }
    }

    /**
     * 添加图片
     *
     * @param imageFile 图片文件
     * @return 返回ID
     * @throws MessagingException 如果添加图片出现异常
     */
    public String addImage(File imageFile) throws MessagingException {
        if (imageFile.exists() && imageFile.isFile()) {
            if (ToolFile.isCommonImageFormat(imageFile.getAbsolutePath())) {
                MimeBodyPart image = new MimeBodyPart();
                image.setDataHandler(new DataHandler(new FileDataSource(imageFile)));
                String cid = Snowflake.INSTANCE.nextIdString(IMAGE);
                image.setContentID(cid);
                imageMap.put(cid, image);
                return cid;
            } else {
                throw new IllegalArgumentException(
                        "File format not supported[" + imageFile.getName() + "]");
            }
        } else {
            throw new IllegalArgumentException("File does not exist");
        }
    }

    /**
     * 添加文本
     *
     * @param htmlContent HTML内容
     * @return 返回ID
     * @throws MessagingException 如果添加文本出现异常
     */
    public String addText(String htmlContent) throws MessagingException {
        // 6. 创建文本"节点"
        MimeBodyPart text = new MimeBodyPart();
        // 这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        text.setContent(htmlContent, "text/html;charset=UTF-8");
        String textId = Snowflake.INSTANCE.nextIdString(TEXT);
        textMap.put(textId, text);
        return textId;
    }

    /**
     * 添加图片文本结点
     *
     * @param imageFile 图片文件
     * @param content   文本提供器
     * @throws MessagingException 如果添加图片文本出现异常
     */
    public void addImageTextNode(
            File imageFile, FunctionThrow<String, String, RuntimeException> content)
            throws MessagingException {
        if (imageFile.exists() && imageFile.isFile()) {
            if (ToolFile.isCommonImageFormat(imageFile.getAbsolutePath())) {
                MimeBodyPart image = new MimeBodyPart();
                image.setDataHandler(new DataHandler(new FileDataSource(imageFile)));
                String imageId = Snowflake.INSTANCE.nextIdString(IMAGE);
                image.setContentID(imageId);
                // 6. 创建文本"节点"
                MimeBodyPart text = new MimeBodyPart();
                // 这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
                text.setContent(
                        content.apply("cid:" + imageId), "text/html;charset=" + getCharsetToken().get());
                String textId = Snowflake.INSTANCE.nextIdString(TEXT);
                text.setContentID(textId);
                MimeMultipart mimeMultipart = new MimeMultipart();
                mimeMultipart.addBodyPart(text);
                mimeMultipart.addBodyPart(image);
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(mimeMultipart);
                nodes.add(mimeBodyPart);
            } else {
                throw new IllegalArgumentException(
                        "File format not supported[" + imageFile.getName() + "]");
            }
        } else {
            throw new IllegalArgumentException("File does not exist");
        }
    }

    /**
     * 合并图片文本
     *
     * @throws MessagingException 如果出现异常
     */
    public EmailMessageContent merge() throws MessagingException {
        return merge(true);
    }

    /**
     * 合并图片文本
     *
     * @param isClear 是否清空图片和文本缓存
     * @throws MessagingException 如果出现异常
     */
    public EmailMessageContent merge(boolean isClear) throws MessagingException {
        // （文本+图片）设置 文本 和 图片"节点"的关系（将 文本 和 图片"节点"合成一个混合"节点"）
        // 没有图片
        if (Whether.empty(imageMap)) {
            if (!Whether.empty(textMap)) {
                // 只有一个文本
                if (textMap.size() == 1) {
                    nodes.addAll(textMap.values());
                } else {
                    MimeMultipart mimeMultipart = new MimeMultipart();
                    for (MimeBodyPart value : textMap.values()) {
                        mimeMultipart.addBodyPart(value);
                    }
                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                    mimeBodyPart.setContent(mimeMultipart);
                    nodes.add(mimeBodyPart);
                }
            }
        } else {
            MimeMultipart mimeMultipart = new MimeMultipart();
            for (MimeBodyPart value : textMap.values()) {
                mimeMultipart.addBodyPart(value);
            }
            for (MimeBodyPart value : imageMap.values()) {
                mimeMultipart.addBodyPart(value);
            }
            mimeMultipart.setSubType("related");
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mimeMultipart);
            nodes.add(mimeBodyPart);
        }
        if (isClear) {
            imageMap.clear();
            textMap.clear();
        }
        return this;
    }

    /**
     * 合并图片文本
     *
     * @param ids 图片和文本ID
     * @throws MessagingException 如果出现异常
     */
    public EmailMessageContent merge(String... ids) throws MessagingException {
        if (Whether.empty(ids)) {
            merge(true);
        }
        MimeMultipart mimeMultipart = new MimeMultipart();
        for (String id : ids) {
            if (id.startsWith(IMAGE)) {
                MimeBodyPart value = imageMap.get(id);
                if (value != null) {
                    mimeMultipart.addBodyPart(value);
                }
            } else if (id.startsWith(TEXT)) {
                MimeBodyPart value = textMap.get(id);
                if (value != null) {
                    mimeMultipart.addBodyPart(value);
                }
            }
        }
        mimeMultipart.setSubType("related");
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(mimeMultipart);
        nodes.add(mimeBodyPart);
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkedHashMap<String, MimeBodyPart> getImageMap() {
        return imageMap;
    }

    public LinkedHashMap<String, MimeBodyPart> getTextMap() {
        return textMap;
    }

    public LinkedHashMap<String, MimeBodyPart> getAttachmentMap() {
        return attachmentMap;
    }

    public Set<InternetAddress> getRecipientTO() {
        return recipientTO;
    }

    public Set<InternetAddress> getRecipientCC() {
        return recipientCC;
    }

    public Set<InternetAddress> getRecipientBCC() {
        return recipientBCC;
    }

    public List<MimeBodyPart> getNodes() {
        return nodes;
    }
}
