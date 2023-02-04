package pxf.tl.database.type.handler;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author potatoxf
 */
public class TypeConfiguration {

    private List<DateTimeFormatter> dateTimeFormatters;
    private Locale locale;

    public TypeConfiguration() {

    }

    public List<DateTimeFormatter> getDateTimeFormatters() {
        return dateTimeFormatters;
    }

    public Locale getLocale() {
        return locale;
    }

    public static final class Builder {
        private String[] dateFormatter;
        private Locale locale;

        public String[] getDateFormatter() {
            return dateFormatter;
        }

        public Builder setDateFormatter(String[] dateFormatter) {
            this.dateFormatter = dateFormatter;
            return this;
        }

        public Locale getLocale() {
            return locale;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public TypeConfiguration build() {
            TypeConfiguration typeConfiguration = new TypeConfiguration();

            if (dateFormatter == null || dateFormatter.length == 0) {
                dateFormatter = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMdd"};
            }

            typeConfiguration.dateTimeFormatters = Arrays.stream(dateFormatter).filter(s -> !s.isBlank()).map(s -> {
                try {
                    return locale == null ? DateTimeFormatter.ofPattern(s) : DateTimeFormatter.ofPattern(s, locale);
                } catch (Throwable ignored) {
                    return null;
                }
            }).filter(Objects::nonNull).toList();

            typeConfiguration.locale = locale;
            return typeConfiguration;
        }

    }
}
