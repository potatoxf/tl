package pxf.tl.text;


import pxf.tl.api.Charsets;
import pxf.tl.api.StringCase;
import pxf.tl.collection.map.GroupStatisticalMap;
import pxf.tl.iter.LineIter;
import pxf.tl.util.ToolIO;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 中文拼音
 *
 * <p>未录入行号：4093 [巼] 行号：4945 [慐] 行号：5720 [摗] 行号：6102 [旕] 行号：6449 [朰] 行号：6565 [枤] 行号：6670 [栍] 行号：6748
 * [桛] 行号：6951 [椦] 行号：6952 [椧] 行号：7053 [榌] 行号：7292 [橻] 行号：8299 [湪] 行号：8325 [溄] 行号：8521 [潈] 行号：9022
 * [焽] 行号：9231 [爎] 行号：9505 [猠] 行号：10360 [癷] 行号：10856 [硧] 行号：10868 [硳] 行号：11347 [穒] 行号：11454 [窽]
 * 行号：12296 [縇] 行号：12365 [繌] 行号：13056 [胿] 行号：13095 [脦] 行号：13385 [艈] 行号：14020 [蓃] 行号：14405 [虄]
 * 行号：18384 [闏]
 *
 * @author potatoxf
 */
public final class ChinesePinYin {
    private static final String A = "a";
    private static final String E = "e";
    private static final String OU = "ou";
    private static final String ALL_UNMARKED_VOWEL = "aeiouv";
    private static final String ALL_MARKED_VOWEL = "āáăàaēéĕèeīíĭìiōóŏòoūúŭùuǖǘǚǜü";
    private static final List<ChinesePinYin[]> CHINESE_PINYIN = new ArrayList<>(0X9FA5 - 0X4E00);

    static {
        LineIter lineIterable =
                new LineIter(ChinesePinYin.class.getResourceAsStream("ChinesePinYin.info"), Charsets.UTF_8);
        for (String line : lineIterable) {
            String[] array = line.split(",");
            ChinesePinYin[] chinesePinYinArr = new ChinesePinYin[array.length];
            for (int i = 0; i < array.length; i++) {
                String[] pyg = array[i].split("=");
                chinesePinYinArr[i] = new ChinesePinYin(Integer.parseInt(pyg[1]), pyg[0]);
            }
            CHINESE_PINYIN.add(chinesePinYinArr);
        }
    }

    private final int tone;
    private final String pinYin;

    private ChinesePinYin(int tone, String pinYin) {
        this.tone = tone;
        this.pinYin = pinYin;
    }

    //  public static void main(String[] args) throws IOException {
    //
    //    List<String> result = new ArrayList<String>();
    //    LineIter lineIterable =
    //        new LineIter(ChinesePinYin.class.getResourceAsStream("ChinesePinYin.info"), true);
    //    for (String line : lineIterable) {
    //      StringBuilder sb = new StringBuilder();
    //      String[] array = line.split(",");
    //      List<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>(4);
    //      for (String s : array) {
    //        String[] pyg = s.split("=");
    //        list.add(
    //            new Pair<Integer, String>(Integer.parseInt(pyg[1].trim()), pyg[0].trim()));
    //        Collections.sort(
    //            list,
    //            new Comparator<Pair<Integer, String>>() {
    //              @Override
    //              public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
    //                return o1.getKey() - o2.getKey();
    //              }
    //            });
    //      }
    //      Pair<Integer, String> pair = list.get(0);
    //      sb.append(pair.getValue()).append("=").append(pair.getKey());
    //      for (int i = 1; i < list.size(); i++) {
    //        pair = list.get(i);
    //        sb.append(",").append(pair.getValue()).append("=").append(pair.getKey());
    //      }
    //      result.add(sb.toString());
    //    }
    //    IOHelper.writeTextData(new File("C:\\Users\\Potato\\Desktop\\新建文本文档.txt"), result, "\n");
    //  }
    //    public static void main(String[] args) {
    //        for (int i = 0; i < CHINESE_PINYIN.size(); i++) {
    //            ChinesePinYin[] chinesePinYins = CHINESE_PINYIN.get(i);
    //            if (chinesePinYins.length == 1 && "none".equals(chinesePinYins[0].pinYin)) {
    //
    //                System.out.print("行号：" + (i + 1) + " ");
    //                System.out.println("[" + Character.toChars(i + 0X4E00)[0] + "]");
    //            }
    //        }
    //    }

    public static void main(String[] args) throws IOException {
        List<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(30);
        int i = 0;
        for (ChinesePinYin[] chinesePinYins : CHINESE_PINYIN) {
            sb.setLength(0);
            sb.append(Character.toChars(i++ + 0x4E00)[0]).append("=");
            for (ChinesePinYin chinesePinYin : chinesePinYins) {
                if (chinesePinYin.pinYin.equals("none")) {
                    continue;
                }
                try {
                    sb.append(
                                    formatPinYin(
                                            chinesePinYin,
                                            StringCase.INSENSITIVE_CASE_DEFAULT_LOWER,
                                            VCharType.WITH_U_UNICODE,
                                            ToneType.WITH_TONE_MARK))
                            .append(",");
                } catch (Throwable ignored) {

                }
            }
            result.add(sb.toString());
        }
        System.out.println(result);

        ToolIO.writeTextData(new File("C:\\Users\\Potato\\Desktop\\aaaa.txt"), result, "\n");
    }

    /**
     * 获取所有可能的拼音序列
     *
     * @param input 输入字符串
     * @return {@code List<String>}
     */
    public static List<String> getAllPossiblePinYinString(String input) {
        return getAllPossiblePinYinString(input, true, true, null);
    }

    /**
     * 获取所有可能的拼音序列
     *
     * @param input        输入字符串
     * @param isRemoveTone 是否移除音节
     * @param isTogether   是否非中文字符放在一起
     * @param trimChar     要清除的字符
     * @return {@code List<String>}
     */
    public static List<String> getAllPossiblePinYinString(
            String input, boolean isRemoveTone, boolean isTogether, String trimChar) {
        List<List<Object>> list = getAllPossiblePinYin(input, trimChar);
        Set<String> result = new LinkedHashSet<String>(list.size());
        boolean lastChar;
        for (List<Object> objects : list) {
            StringBuilder sb = new StringBuilder();
            Object object = objects.get(0);
            if (object instanceof ChinesePinYin) {
                sb.append(isRemoveTone ? ((ChinesePinYin) object).getPinYin() : object);
                lastChar = false;
            } else {
                sb.append(object);
                lastChar = true;
            }
            for (int i = 1; i < objects.size(); i++) {
                object = objects.get(i);
                if (object instanceof ChinesePinYin) {
                    sb.append(' ');
                    if (lastChar) {
                        lastChar = false;
                    }
                    sb.append(isRemoveTone ? ((ChinesePinYin) object).getPinYin() : object);

                } else {
                    if (!isTogether || !lastChar) {
                        sb.append(' ');
                    }
                    sb.append(object);
                    lastChar = true;
                }
            }
            int i = sb.length() - 1;
            if (sb.charAt(i) == ' ') {
                sb.setLength(i);
            }
            result.add(sb.toString());
        }
        return new ArrayList<String>(result);
    }

    /**
     * 获取所有可能的拼音序列
     *
     * @param input 输入字符串
     * @return {@code List<List<Object>>}，{@code Object instanceof ChinesePinYin||Object instanceof
     * Character}
     */
    public static List<List<Object>> getAllPossiblePinYin(String input, String trimChar) {
        char[] chars = input.toCharArray();
        if (trimChar == null) {
            trimChar = "";
        }
        int count = 1;
        for (char c : chars) {
            count *= getPinYinCount(c);
        }
        if (count < 0) {
            List<Object> data = new ArrayList<Object>(chars.length);
            for (char c : chars) {
                if (trimChar.indexOf(c) >= 0) {
                    continue;
                }
                if (hasPinYin(c)) {
                    data.add(getPinYin(c));
                } else {
                    data.add(c);
                }
            }
            return Collections.singletonList(data);
        } else {
            List<List<Object>> result = new ArrayList<List<Object>>(count);
            GroupStatisticalMap<Character> statisticalMap =
                    new GroupStatisticalMap<Character>(chars.length, 1);
            for (char c : chars) {
                if (trimChar.indexOf(c) >= 0) {
                    continue;
                }
                statisticalMap.reset(c);
            }
            int headLen = getPinYinCount(chars[0]);
            boolean flag = false;
            while (statisticalMap.get(chars[0]) < headLen && !flag) {
                List<Object> data = new ArrayList<Object>(chars.length);
                for (char c : chars) {
                    if (trimChar.indexOf(c) >= 0) {
                        continue;
                    }
                    if (hasPinYin(c)) {
                        data.add(getMultiPinYin(c)[statisticalMap.get(c)]);
                    } else {
                        data.add(c);
                    }
                }
                result.add(data);
                for (int i = chars.length - 1; i >= 0; i--) {
                    char c = chars[i];
                    if (trimChar.indexOf(c) >= 0) {
                        continue;
                    }
                    int index = statisticalMap.get(c);
                    if (index + 1 >= getPinYinCount(c)) {
                        flag = true;
                        statisticalMap.reset(c);
                    } else {
                        flag = false;
                        statisticalMap.increase(c);
                        break;
                    }
                }
            }
            return result;
        }
    }

    /**
     * 获取拼音个数，如果没有默认为1
     *
     * @param c 字符
     * @return 返回拼音个数
     */
    public static int getPinYinCount(char c) {
        return hasPinYin(c) ? getMultiPinYin(c).length : 1;
    }

    /**
     * 是否有中文拼音
     *
     * @param c 中文字符
     * @return 如果有返回 {@code true}，否则 {@code false}
     */
    public static boolean hasPinYin(char c) {
        return c >= 0X4E00 && c <= 0X9FA5;
    }

    /**
     * 获取中文默认拼音
     *
     * @param c 中文字符
     * @return 拼音
     * @throws IllegalArgumentException 如果不存在抛出该异常
     */
    public static ChinesePinYin getPinYin(char c) {
        return getMultiPinYin(c)[0];
    }

    /**
     * 获取中文所有拼音
     *
     * @param c 中文字符
     * @return 拼音
     * @throws IllegalArgumentException 如果不存在抛出该异常
     */
    public static ChinesePinYin[] getMultiPinYin(char c) {
        if (hasPinYin(c)) {
            return CHINESE_PINYIN.get(c - 0X4E00);
        }
        throw new IllegalArgumentException("The [" + c + "] Chinese Pin Yin does not exist");
    }

    /**
     * 格式化拼音
     *
     * @param chinesePinYin 拼音字符粗
     * @param stringCase    字符串大小写，默认 {@code StringCase.LOWER}
     * @param vCharType     v字符类型，默认 {@code VCharType.WITH_V}
     * @param toneType      音调类型，默认 {@code ToneType.WITHOUT_TONE}
     * @return 格式化后的拼音字符串
     */
    public static String formatPinYin(
            ChinesePinYin chinesePinYin, StringCase stringCase, VCharType vCharType, ToneType toneType) {
        return format(chinesePinYin.toString(), stringCase, vCharType, toneType);
    }

    /**
     * 格式化拼音
     *
     * @param pinyinStr  拼音字符粗
     * @param stringCase 字符串大小写，默认 {@code StringCase.LOWER}
     * @param vCharType  v字符类型，默认 {@code VCharType.WITH_V}
     * @param toneType   音调类型，默认 {@code ToneType.WITHOUT_TONE}
     * @return 格式化后的拼音字符串
     */
    private static String format(
            String pinyinStr, StringCase stringCase, VCharType vCharType, ToneType toneType) {
        if (vCharType == null) {
            vCharType = VCharType.WITH_V;
        }
        if (toneType == null) {
            toneType = ToneType.WITHOUT_TONE;
        }
        if (toneType == ToneType.WITHOUT_TONE) {
            pinyinStr = pinyinStr.replaceAll("[1-5]", "");
        } else if (ToneType.WITH_TONE_MARK == toneType) {
            if (vCharType != VCharType.WITH_U_UNICODE) {
                throw new IllegalArgumentException(
                        "Please use [VCharType.WITH_U_UNICODE],because tone marks cannot be added to v or u:");
            }
            pinyinStr = pinyinStr.replaceAll("u:", "v");
            pinyinStr = formatChineseToneMark(pinyinStr);
        }
        if (vCharType == VCharType.WITH_V) {
            pinyinStr = pinyinStr.replaceAll("u:", "v");
        } else if (VCharType.WITH_U_UNICODE == vCharType) {
            pinyinStr = pinyinStr.replaceAll("u:", "ü");
        }
        if (stringCase != null) {
            pinyinStr = stringCase.handleStringCase(pinyinStr);
        }
        return pinyinStr;
    }

    /**
     * 使用Unicode将音调数字转换为音调标记
     *
     * <p>确定出现音调标记的元音的简单算法如下：
     *
     * <ol>
     *   <li>首先，寻找“a”或“e”。如果出现任何一个元音，它将带有音调标记，不可能同时包含“ a”和“ e”的拼音音节。
     *   <li>如果没有“a”或“e”，则查找“ou”。如果出现“ou”，则“ o”带有号。
     *   <li>如果以上情况均不成立，则音节中的最后一个元音将带有音调标记。
     * </ol>
     *
     * @param pinyin 带有音调编号的 {@code ascii}表示
     * @return 带音标的 {@code unicode}表示
     */
    private static String formatChineseToneMark(String pinyin) {
        pinyin = pinyin.toLowerCase();
        int pinyinLen = pinyin.length();
        if (pinyin.matches("[a-z]*[1-5]?")) {
            String unmarkedVowel = null;
            int indexOfUnmarkedVowel;
            if (pinyin.matches("[a-z]*[1-5]")) {
                if ((indexOfUnmarkedVowel = pinyin.indexOf(A)) != -1) {
                    unmarkedVowel = A;
                } else if ((indexOfUnmarkedVowel = pinyin.indexOf(E)) != -1) {
                    unmarkedVowel = E;
                } else if ((indexOfUnmarkedVowel = pinyin.indexOf(OU)) != -1) {
                    unmarkedVowel = OU.substring(0, 1);
                } else {
                    for (int i = pinyinLen - 1; i >= 0; i--) {
                        String c = pinyin.substring(i, i + 1);
                        if (ALL_UNMARKED_VOWEL.contains(c)) {
                            indexOfUnmarkedVowel = i;
                            unmarkedVowel = c;
                            break;
                        }
                    }
                }
                if (indexOfUnmarkedVowel != -1) {
                    int tuneNumber = Character.getNumericValue(pinyin.charAt(pinyinLen - 1));
                    int columnIndex = tuneNumber - 1;
                    int rowIndex = ALL_UNMARKED_VOWEL.indexOf(unmarkedVowel);
                    int vowelLocation = rowIndex * 5 + columnIndex;
                    char markedVowel = ALL_MARKED_VOWEL.charAt(vowelLocation);
                    return pinyin.substring(0, indexOfUnmarkedVowel).replace('v', 'ü')
                            + markedVowel
                            + pinyin.substring(indexOfUnmarkedVowel + 1, pinyinLen - 1).replace('v', 'ü');
                    // error happens in the procedure of locating vowel
                } else {
                    return pinyin;
                }
                // input cn.cilisi.series.infrastructure.string has no any tune number
            } else {
                // only replace v with ü (umlat) character
                return pinyin.replaceAll("v", "ü");
            }
        }
        throw new RuntimeException("Error formatting Chinese pinyin");
    }

    public int getTone() {
        return tone;
    }

    public String getPinYin() {
        return pinYin;
    }

    @Override
    public String toString() {
        return pinYin + tone;
    }

    /**
     * 定义汉语拼音输出格式
     *
     * <p>中文有四个音调和一个“无声”音调。它们分别被称为： Píng（平），Shǎng（上）， Qù（去），Rù（入）和Qing（轻）。
     *
     * <p>通常，我们使用1、2、3、4和5来*表示它们。
     */
    public enum ToneType {
        /**
         * 该选项表示汉字拼音输出有音调编号
         */
        WITH_TONE_NUMBER,
        /**
         * 该选项表示输出的汉语拼音没有音调编号或音调标记
         */
        WITHOUT_TONE,
        /**
         * 该选项表示汉语拼音输出带有音调标记
         */
        WITH_TONE_MARK
    }

    /**
     * v字符类型
     */
    public enum VCharType {
        /**
         * 该选项指示“ü”的输出为“u:”
         */
        WITH_U_AND_COLON,
        /**
         * 该选项表示Unicode格式的“ü”输出为“ü”
         */
        WITH_U_UNICODE,
        /**
         * 该选项指示“ü”的输出为“v”
         */
        WITH_V
    }
}
