package pxf.tl.constants;


import pxf.tl.api.Literal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.SoftReference;
import java.util.*;

/**
 * 国家枚举类，支持语言：
 *
 * <ul>
 *   <li>中文
 *   <li>英文
 * </ul>
 *
 * @author potatoxf
 */
public enum Country implements Literal<Country> {
    /**
     * 国家枚举类，按字母顺序
     */
    AFGHANISTAN("93"),
    ALBANIA("355"),
    ALGERA("213"),
    ANDORRA("376"),
    ANGOLA("244"),
    ANGUILLA("1264"),
    ANTIGUA_AND_BARBUDA("1268"),
    ARGENTINA("54"),
    ARMENIA("374"),
    ARUBA("297"),
    ASCENSION("247"),
    AUSTRALIA("61"),
    AUSTRIA("43"),
    AZERBAIJAN("994"),
    BAHAMAS("1242"),
    BAHRAIN("973"),
    BANGLADESH("880"),
    BARBADOS("1246"),
    BELARUS("375"),
    BELGIUM("32"),
    BELIZE("501"),
    BENIN("229"),
    BERMUDA("1441"),
    BHUTAN("975"),
    BOLIVIA("591"),
    BOSNIA_AND_HERZEGOVINA("387"),
    BOTWANA("267"),
    BRAZILL("55"),
    BRUNEI("673"),
    BULGARIA("359"),
    BURKINA_FASO("226"),
    BURUNDI("257"),
    CAMBODIA("855"),
    CAMEROON("237"),
    CANADA("1"),
    CAPE_VERDE("238"),
    CAYMAN_ISLANDS("1345"),
    CENTRAL_AFRICAN_REPUBLIC("236"),
    CHAD("235"),
    CHILE("56"),
    CHINA(Locale.CHINA, "86"),
    COLOMBIA("57"),
    COMOROS("269"),
    COOK_ISLANDS("682"),
    COSTA_RICA("506"),
    COTE_DIVOIRE("225"),
    CROATIA("385"),
    CUBA("53"),
    CYPRUS("357"),
    CZECH_REPUBLIC("420"),
    DEMOCRATIC_REPUBLIC_OF_THE_CONGO("243"),
    DENMARK("45"),
    DJIBOUTI("253"),
    DOMINICA("1767"),
    DOMINICAN_REPUBLIC("1809"),
    ECUADOR("593"),
    EGYPT("20"),
    EISALVADOR("503"),
    ESTONIA("372"),
    ETHIOPIA("251"),
    FAROE_ISLANDS("298"),
    FIJI("679"),
    FINLAND("358"),
    FRANCE("33"),
    FRENCH_GUIANA("594"),
    FRENCH_POLYNESIA("689"),
    GABON("241"),
    GAMBIA("220"),
    GEORGIA("995"),
    GERMANY("94"),
    GHANA("233"),
    GIBRALTAR("350"),
    GREECE("30"),
    GREENLAND("299"),
    GRENADA("1473"),
    GUADELOUPE("590"),
    GUAM("1671"),
    GUATEMALA("502"),
    GUERNSEY("44"),
    GUINEA("240", "224"),
    GUYANA("592"),
    HAITI("509"),
    HONDURAS("504"),
    HONG_KONG("852"),
    HUNGARY("36"),
    ICELAND("354"),
    INDEA("91"),
    INDONESIA("62"),
    IRAN("98"),
    IRAQ("964"),
    IRELAND("353"),
    ISLE_OF_MAN("44"),
    ISRAEL("972"),
    ITALY("93"),
    JAMAICA("1876"),
    JAPAN("81"),
    JERSEY("44"),
    JORDAN("962"),
    KAZEAKHSTAN("7"),
    KENYA("254"),
    KOREA("82"),
    KOSOVO("383"),
    KUWAIT("965"),
    KYRGYZSTAN("996"),
    LAOS("856"),
    LATVIA("371"),
    LEBANON("961"),
    LESOTHO("266"),
    LIBERIA("231"),
    LIBYA("218"),
    LIECHTENSTEIN("423"),
    LITHUANIA("370"),
    LUXEMBOURG("352"),
    MACAO("853"),
    MACEDONIA("389"),
    MADAGASCAR("261"),
    MALAWI("265"),
    MALAYSIA("60"),
    MALDIVES("960"),
    MALI("223"),
    MALTA("356"),
    MARTINIQUE("596"),
    MAURITANIA("222"),
    MAURITIUS("230"),
    MAYOTTE("262"),
    MEXICO("52"),
    MOLDOVA("373"),
    MONACO("377"),
    MONGOLIA("976"),
    MONTENEGRO("382"),
    MONTSERRAT("1664"),
    MOROCCO("212"),
    MOZAMBIQUE("258"),
    MYANMAR("95"),
    NAMIBIA("264"),
    NEPAL("977"),
    NETHERLANDS("31"),
    NETHERLANDS_ANTILLSE("599"),
    NEW_CALEDONIA("687"),
    NEWZEALAND("64"),
    NICARAGUA("505"),
    NIGER("227"),
    NIGERIA("234"),
    NORWAY("47"),
    OMAN("968"),
    PAKISTAN("92"),
    PALESTINIAN("970"),
    PANAMA("507"),
    PAPUA_NEW_GUINEA("675"),
    PARAGUAY("595"),
    PERU("51"),
    PHILIPPINES("63"),
    POLAND("48"),
    PORTUGAL("351"),
    PUERTORICO("1"),
    QOTAR("974"),
    REPUBLIC_OF_THE_CONGO("242"),
    REUNION("262"),
    ROMANIA("40"),
    RUSSIA("7"),
    RWANDA("250"),
    SAMOA_EASTERN("684"),
    SAMOA_WESTERN("685"),
    SAN_MARINO("378"),
    SAO_TOME_AND_PRINCIPE("239"),
    SAUDI_ARABIA("966"),
    SENEGAL("221"),
    SERBIA("381"),
    SEYCHELLES("248"),
    SIERRA_LEONE("232"),
    SINGAPORE("65"),
    SLOVAKIA("421"),
    SLOVENIA("386"),
    SOUTH_AFRICA("27"),
    SPAIN("34"),
    SRILANKA("94"),
    ST_KITTS_AND_NEVIS("1869"),
    ST_LUCIA("1758"),
    ST_VINCENT("1784"),
    SUDAN("249"),
    SURINAME("597"),
    SWAZILAND("268"),
    SWEDEN("46"),
    SWITZERLAND("41"),
    SYRIA("963"),
    TAIWAN("886"),
    TAJIKISTAN("992"),
    TANZANIA("255"),
    THAILAND("66"),
    TIMOR_LESTE("670"),
    TOGO("228"),
    TONGA("676"),
    TRINIDAD_AND_TOBAGO("1868"),
    TUNISIA("216"),
    TURKEY("90"),
    TURKMENISTAN("993"),
    TURKS_AND_CAICOS_ISLANDS("1649"),
    UGANDA("256"),
    UKRAINE("380"),
    UNITED_ARAB_EMIRATES("971"),
    UNITED_KINGDOM("44"),
    URUGUAY("598"),
    USA("1"),
    UZBEKISTAN("998"),
    VANUATU("678"),
    VENEZUELA("58"),
    VIETNAM("84"),
    VIRGIN_ISLANDS("1340"),
    YEMEN("967"),
    ZAMBIA("260"),
    ZIMBABWE("263");
    private static final String DELIMITER_SYMBOL = "_";
    private static final String PROPERTIES_DELIMITER_SYMBOL = ".";
    private static final int LOCALE_PART_LENGTH = 2;
    private static SoftReference<Country[]> sortedCountryByTelephonePrefixRef;
    private final Locale locale;
    private final String[] telephonePrefix;

    Country(Locale locale, String... telephonePrefix) {
        this.locale = locale;
        this.telephonePrefix = telephonePrefix;
    }

    Country(String... telephonePrefix) {
        this.locale = Locale.ENGLISH;
        this.telephonePrefix = telephonePrefix;
    }

    /**
     * 将给定的{@code String}表示形式解析为{@link Locale}，格式如下：
     *
     * <ul>
     *   <li>en
     *   <li>en_US
     *   <li>en US
     *   <li>en-US
     * </ul>
     *
     * @param localeString 语言环境{@code String}
     * @return 相应的{@code Locale}实例，如果没有，则为{@code null}
     * @throws IllegalArgumentException 如果在无效的语言环境规范的情况下
     */
    @Nullable
    public static Locale parseLocaleString(String localeString) {
        StringTokenizer st = new StringTokenizer(localeString, "_ ");
        List<String> tokens = new LinkedList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            tokens.add(token);
        }
        int size = tokens.size();
        if (size == 1) {
            Locale resolved = Locale.forLanguageTag(tokens.get(0));
            if (!resolved.getLanguage().isEmpty()) {
                return resolved;
            }
        }
        String language = (size > 0 ? tokens.get(0) : "");
        String country = (size > 1 ? tokens.get(1) : "");
        String variant = "";
        if (size > LOCALE_PART_LENGTH) {
            // There is definitely a variant, and it is everything after the country
            // code sans the separator between the country code and the variant.
            int endIndexOfCountryCode =
                    localeString.indexOf(country, language.length()) + country.length();
            // Strip off any leading '_' and whitespace, what's left is the variant.
            variant = localeString.substring(endIndexOfCountryCode).trim();
            int index = 0;
            for (int i = 0; i < variant.length(); i++) {
                if (variant.charAt(i) == '_') {
                    index++;
                } else {
                    break;
                }
            }
            variant = variant.substring(index);
        }
        if (variant.isEmpty() && country.startsWith("#")) {
            variant = country;
            country = "";
        }
        return (language.length() > 0 ? new Locale(language, country, variant) : null);
    }

    /**
     * 搜索国家通过电话号码前缀
     *
     * @param number 电话号码前缀
     * @return {@code Country}不存在返回null
     */
    @Nullable
    public static Country findCountryByTelephonePrefix(@Nullable String number) {
        Object[] results = doFindCountriesByTelephonePrefix(number);
        return results == null ? null : (Country) results[1];
    }

    /**
     * 搜索国家通过电话号码前缀
     *
     * @param number 电话号码前缀
     * @return {@code Object[]}不存在返回null，长度为2第一个为索引，第二个为{@code Country}
     */
    private static Object[] doFindCountriesByTelephonePrefix(@Nullable String number) {
        if (number == null) {
            return null;
        }
        Country[] countries = getSortedCountryCache();
        int findIndex = -1;
        boolean skip = false;
        final int nLen = number.length();
        for (int i, si = 0, s = 0, e = countries.length; s < e; ) {
            char nc = number.charAt(si);
            if ('0' <= nc && nc <= '9') {
                i = (s + e) / 2;
                String telephonePrefix = countries[i].getTelephonePrefix();
                int tLen = telephonePrefix.length();
                while (si < tLen) {
                    if (tLen <= nLen) {
                        nc = number.charAt(si);
                        char tph = telephonePrefix.charAt(si);
                        // 相等比较下一位
                        if (tph == nc) {
                            si++;
                            continue;
                        }
                        if (nc > tph) {
                            s = i + 1;
                        } else {
                            e = i - 1;
                        }
                    } else {
                        e = i - 1;
                    }
                    break;
                }
                if (si == tLen) {
                    findIndex = i;
                    break;
                }
                si = 0;
            } else {
                break;
            }
        }
        return findIndex == -1 ? null : new Object[]{findIndex, countries[findIndex]};
    }

    /**
     * 获取有序国家缓存
     *
     * @return {@code Country[]}
     */
    private static Country[] getSortedCountryCache() {
        Country[] countries = null;
        while (sortedCountryByTelephonePrefixRef == null || countries == null) {
            synchronized (Country.class) {
                if (sortedCountryByTelephonePrefixRef == null) {
                    Country[] values = values();
                    Arrays.sort(values, Comparator.comparing(Country::getTelephonePrefix));
                    sortedCountryByTelephonePrefixRef = new SoftReference<>(values);
                }
                if (countries == null) {
                    countries = sortedCountryByTelephonePrefixRef.get();
                }
            }
        }
        return countries;
    }

    /**
     * 搜索国家通过电话号码前缀
     *
     * @param number 电话号码前缀
     * @return {@code Country[]}不存在返回null
     */
    @Nullable
    public static Country[] findCountriesByTelephonePrefix(@Nullable String number) {
        Object[] results = doFindCountriesByTelephonePrefix(number);
        if (results == null) {
            return null;
        }
        Country[] countries = getSortedCountryCache();
        int findIndex = (int) results[0];
        List<Country> data = new ArrayList<>(4);
        boolean b1 = true, b2 = true;
        final String telephonePrefix = countries[findIndex].getTelephonePrefix();
        for (int i = 1, si, ei; b1 || b2; i++) {
            si = findIndex - i;
            ei = findIndex + i;
            if (si >= 0 && b1 && telephonePrefix.equals(countries[si].getTelephonePrefix())) {
                data.add(countries[si]);
            } else {
                b1 = false;
            }
            if (ei < countries.length
                    && b2
                    && telephonePrefix.equals(countries[ei].getTelephonePrefix())) {
                data.add(countries[ei]);
            } else {
                b2 = false;
            }
        }
        if (data.isEmpty()) {
            return new Country[]{countries[findIndex]};
        }
        data.add(countries[findIndex]);
        return data.toArray(new Country[0]);
    }

    /**
     * 获取国家电话前缀
     *
     * @return {@code String}
     */
    @Nonnull
    public String getTelephonePrefix() {
        return telephonePrefix[0];
    }

    /**
     * 获取国家多个电话前缀，通常只有一个
     *
     * @return {@code String[]}
     */
    @Nonnull
    public String[] getTelephonePrefixes() {
        return telephonePrefix;
    }


    @Override
    public Boolean isLowerName() {
        return true;
    }

    @Override
    public Object[][] replaceForName() {
        return new Object[][]{{"_", "."}};
    }
}
