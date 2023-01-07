package pxf.tlx.pigeon.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 邮件认证
 *
 * @author potatoxf
 */
public final class MailAuthenticator extends Authenticator {

    private final String password;

    public MailAuthenticator(String password) {
        this.password = Objects.requireNonNull(password, "The password must be no null");
    }

    public MailAuthenticator(Supplier<String> passwordSupplier) {
        this(Objects.requireNonNull(passwordSupplier, "The passwordSupplier must be no null").get());
    }

    /**
     * Called when password authentication is needed. Subclasses should override the default
     * implementation, which returns null.
     *
     * <p>
     *
     * <p>Note that if this method uses a dialog to prompt the user for this information, the dialog
     * needs to block until the user supplies the information. This method can not simply return after
     * showing the dialog.
     *
     * @return The PasswordAuthentication collected from the user, or null if none is provided.
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(getDefaultUserName(), password);
    }
}
