package pxf.tlx.spring.web.i18n;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import pxf.tl.lang.AbstractComposite;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * 综合国际化语言解析器
 *
 * @author potatoxf
 */
public class CompositeLocalResolver
        extends AbstractComposite<Class<? extends LocaleResolver>, LocaleResolver>
        implements LocaleResolver {

    public CompositeLocalResolver() {
        registerInstance(AcceptHeaderLocaleResolver.class, new AcceptHeaderLocaleResolver());
        registerInstance(CookieLocaleResolver.class, new CookieLocaleResolver());
        registerInstance(SessionLocaleResolver.class, new SessionLocaleResolver());
        registerInstance(FixedLocaleResolver.class, new FixedLocaleResolver());
    }

    /**
     * Resolve the current locale via the given request. Can return a default locale as fallback in
     * any case.
     *
     * @param request the request to resolve the locale for
     * @return the current locale (never {@code null})
     */
    @Nonnull
    @Override
    public Locale resolveLocale(@Nonnull HttpServletRequest request) {

        List<LocaleResolver> instanceList = getInstanceList();
        for (LocaleResolver localeResolver : instanceList) {
            try {
                return localeResolver.resolveLocale(request);
            } catch (Throwable e) {
                e.printStackTrace();
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("", e);
                }
            }
        }
        return Locale.getDefault();
    }

    /**
     * Set the current locale to the given one.
     *
     * @param request  the request to be used for locale modification
     * @param response the response to be used for locale modification
     * @param locale   the new locale, or {@code null} to clear the locale
     * @throws UnsupportedOperationException if the LocaleResolver implementation does not support
     *                                       dynamic changing of the locale
     */
    @Override
    public void setLocale(@Nonnull HttpServletRequest request, HttpServletResponse response, Locale locale) {
        List<LocaleResolver> instanceList = getInstanceList();
        for (LocaleResolver localeResolver : instanceList) {
            localeResolver.setLocale(request, response, locale);
        }

    }

}
