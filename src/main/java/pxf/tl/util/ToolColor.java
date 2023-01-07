package pxf.tl.util;


import pxf.tl.help.Whether;

import java.awt.*;
import java.lang.reflect.Field;
import java.security.SecureRandom;

/**
 * 颜色助手类
 *
 * @author potatoxf
 */
public final class ToolColor {

    /*
     *爱丽丝蓝
     *240,248,255
     */
    public static final int ALICEBLUE = 0XF0F8FF;
    /*
     *古代的白色
     *250,235,215
     */
    public static final int ANTIQUEWHITE = 0XFAEBD7;
    /*
     *水绿色
     *212,242,231
     */
    public static final int AQUA = 0XD4F2E7;
    /*
     *绿玉\碧绿色
     *127,255,170
     */
    public static final int AUQAMARIN = 0X7FFFAA;
    /*
     *蔚蓝色
     *240,255,255
     */
    public static final int AZURE = 0XF0FFFF;
    /*
     *米色(浅褐色)
     *245,245,220
     */
    public static final int BEIGE = 0XF5F5DC;
    /*
     *(浓汤)乳脂,番茄等
     *255,228,196
     */
    public static final int BISQUE = 0XFFE4C4;
    /*
     *纯黑
     *0,0,0
     */
    public static final int BLACK = 0X000000;
    /*
     *漂白的杏仁
     *255,235,205
     */
    public static final int BLANCHEDALMOND = 0XFFEBCD;
    /*
     *纯蓝
     *0,0,255
     */
    public static final int BLUE = 0X0000FF;
    /*
     *深紫罗兰的蓝色
     *138,43,226
     */
    public static final int BLUEVIOLET = 0X8A2BE2;
    /*
     *棕色
     *165,42,42
     */
    public static final int BROWN = 0XA52A2A;
    /*
     *结实的树
     *222,184,135
     */
    public static final int BRULYWOOD = 0XDEB887;
    /*
     *军校蓝
     *95,158,160
     */
    public static final int CADETBLUE = 0X5F9EA0;
    /*
     *查特酒绿
     *127,255,0
     */
    public static final int CHARTREUSE = 0X7FFF00;
    /*
     *巧克力
     *210,105,30
     */
    public static final int CHOCOLATE = 0XD2691E;
    /*
     *珊瑚
     *255,127,80
     */
    public static final int CORAL = 0XFF7F50;
    /*
     *矢车菊的蓝色
     *100,149,237
     */
    public static final int CORNFLOWERBLUE = 0X6495ED;
    /*
     *玉米色
     *255,248,220
     */
    public static final int CORNISLK = 0XFFF8DC;
    /*
     *猩红
     *220,20,60
     */
    public static final int CRIMSON = 0XDC143C;
    /*
     *青色
     *0,255,255
     */
    public static final int CYAN = 0X00FFFF;
    /*
     *深蓝色
     *0,0,139
     */
    public static final int DARKBLUE = 0X00008B;
    /*
     *深青色
     *0,139,139
     */
    public static final int DARKCYAN = 0X008B8B;
    /*
     *深灰色
     *169,169,169
     */
    public static final int DARKGRAY = 0XA9A9A9;
    /*
     *深绿色
     *0,100,0
     */
    public static final int DARKGREEN = 0X006400;
    /*
     *深卡其布
     *189,183,107
     */
    public static final int DARKKHAKI = 0XBDB76B;
    /*
     *深洋红色
     *139,0,139
     */
    public static final int DARKMAGENTA = 0X8B008B;
    /*
     *深橙色
     *255,140,0
     */
    public static final int DARKORANGE = 0XFF8C00;
    /*
     *深兰花紫
     *153,50,204
     */
    public static final int DARKORCHID = 0X9932CC;
    /*
     *深红色
     *139,0,0
     */
    public static final int DARKRED = 0X8B0000;
    /*
     *深鲜肉(鲑鱼)色
     *233,150,122
     */
    public static final int DARKSALMON = 0XE9967A;
    /*
     *深海洋绿
     *143,188,143
     */
    public static final int DARKSEAGREEN = 0X8FBC8F;
    /*
     *深岩暗蓝灰色
     *72,61,139
     */
    public static final int DARKSLATEBLUE = 0X483D8B;
    /*
     *深石板灰
     *47,79,79
     */
    public static final int DARKSLATEGRAY = 0X2F4F4F;
    /*
     *深绿宝石
     *0,206,209
     */
    public static final int DARKTURQUOISE = 0X00CED1;
    /*
     *深紫罗兰色
     *148,0,211
     */
    public static final int DARKVOILET = 0X9400D3;
    /*
     *深粉色
     *255,20,147
     */
    public static final int DEEPPINK = 0XFF1493;
    /*
     *深天蓝
     *0,191,255
     */
    public static final int DEEPSKYBLUE = 0X00BFFF;
    /*
     *暗淡的灰色
     *105,105,105
     */
    public static final int DIMGRAY = 0X696969;
    /*
     *道奇蓝
     *30,144,255
     */
    public static final int DODERBLUE = 0X1E90FF;
    /*
     *耐火砖
     *178,34,34
     */
    public static final int FIREBRICK = 0XB22222;
    /*
     *花的白色
     *255,250,240
     */
    public static final int FLORALWHITE = 0XFFFAF0;
    /*
     *森林绿
     *34,139,34
     */
    public static final int FORESTGREEN = 0X228B22;
    /*
     *灯笼海棠(紫红色)
     *255,0,255
     */
    public static final int FUCHSIA = 0XFF00FF;
    /*
     *亮灰色
     *220,220,220
     */
    public static final int GAINSBORO = 0XDCDCDC;
    /*
     *幽灵的白色
     *248,248,255
     */
    public static final int GHOSTWHITE = 0XF8F8FF;
    /*
     *金
     *255,215,0
     */
    public static final int GOLD = 0XFFD700;
    /*
     *秋麒麟
     *218,165,32
     */
    public static final int GOLDENROD = 0XDAA520;
    /*
     *灰色
     *128,128,128
     */
    public static final int GRAY = 0X808080;
    /*
     *纯绿
     *0,128,0
     */
    public static final int GREEN = 0X008000;
    /*
     *绿黄色
     *173,255,47
     */
    public static final int GREENYELLOW = 0XADFF2F;
    /*
     *蜂蜜
     *240,255,240
     */
    public static final int HONEYDEW = 0XF0FFF0;
    /*
     *热情的粉红
     *255,105,180
     */
    public static final int HOTPINK = 0XFF69B4;
    /*
     *印度红
     *205,92,92
     */
    public static final int INDIANRED = 0XCD5C5C;
    /*
     *靛青
     *75,0,130
     */
    public static final int INDIGO = 0X4B0082;
    /*
     *象牙
     *255,255,240
     */
    public static final int IVORY = 0XFFFFF0;
    /*
     *卡其布
     *240,230,140
     */
    public static final int KHAKI = 0XF0E68C;
    /*
     *熏衣草花的淡紫色
     *230,230,250
     */
    public static final int LAVENDER = 0XE6E6FA;
    /*
     *脸红的淡紫色
     *255,240,245
     */
    public static final int LAVENDERBLUSH = 0XFFF0F5;
    /*
     *草坪绿
     *124,252,0
     */
    public static final int LAWNGREEN = 0X7CFC00;
    /*
     *柠檬薄纱
     *255,250,205
     */
    public static final int LEMONCHIFFON = 0XFFFACD;
    /*
     *淡蓝
     *173,216,230
     */
    public static final int LIGHTBLUE = 0XADD8E6;
    /*
     *淡珊瑚色
     *240,128,128
     */
    public static final int LIGHTCORAL = 0XF08080;
    /*
     *淡青色
     *225,255,255
     */
    public static final int LIGHTCYAN = 0XE1FFFF;
    /*
     *浅秋麒麟黄
     *250,250,210
     */
    public static final int LIGHTGOLDENRODYELLOW = 0XFAFAD2;
    /*
     *淡绿色
     *144,238,144
     */
    public static final int LIGHTGREEN = 0X90EE90;
    /*
     *浅灰色
     *211,211,211
     */
    public static final int LIGHTGREY = 0XD3D3D3;
    /*
     *浅粉红
     *255,182,193
     */
    public static final int LIGHTPINK = 0XFFB6C1;
    /*
     *浅鲜肉(鲑鱼)色
     *255,160,122
     */
    public static final int LIGHTSALMON = 0XFFA07A;
    /*
     *浅海洋绿
     *32,178,170
     */
    public static final int LIGHTSEAGREEN = 0X20B2AA;
    /*
     *淡蓝色
     *135,206,250
     */
    public static final int LIGHTSKYBLUE = 0X87CEFA;
    /*
     *浅石板灰
     *119,136,153
     */
    public static final int LIGHTSLATEGRAY = 0X778899;
    /*
     *淡钢蓝
     *176,196,222
     */
    public static final int LIGHTSTEELBLUE = 0XB0C4DE;
    /*
     *浅黄色
     *255,255,224
     */
    public static final int LIGHTYELLOW = 0XFFFFE0;
    /*
     *酸橙色
     *0,255,0
     */
    public static final int LIME = 0X00FF00;
    /*
     *酸橙绿
     *50,205,50
     */
    public static final int LIMEGREEN = 0X32CD32;
    /*
     *亚麻布
     *250,240,230
     */
    public static final int LINEN = 0XFAF0E6;
    /*
     *洋红
     *255,0,255
     */
    public static final int MAGENTA = 0XFF00FF;
    /*
     *栗色
     *128,0,0
     */
    public static final int MAROON = 0X800000;
    /*
     *适中的碧绿色
     *0,250,154
     */
    public static final int MEDIUMAQUAMARINE = 0X00FA9A;
    /*
     *适中的蓝色
     *0,0,205
     */
    public static final int MEDIUMBLUE = 0X0000CD;
    /*
     *适中的兰花紫
     *186,85,211
     */
    public static final int MEDIUMORCHID = 0XBA55D3;
    /*
     *适中的紫色
     *147,112,219
     */
    public static final int MEDIUMPURPLE = 0X9370DB;
    /*
     *适中的板岩暗蓝灰色
     *123,104,238
     */
    public static final int MEDIUMSLATEBLUE = 0X7B68EE;
    /*
     *适中的春天的绿色
     *0,255,127
     */
    public static final int MEDIUMSPRINGGREEN = 0X00FF7F;
    /*
     *适中的绿宝石
     *72,209,204
     */
    public static final int MEDIUMTURQUOISE = 0X48D1CC;
    /*
     *适中的紫罗兰红色
     *199,21,133
     */
    public static final int MEDIUMVIOLETRED = 0XC71585;
    /*
     *午夜的蓝色
     *25,25,112
     */
    public static final int MIDNIGHTBLUE = 0X191970;
    /*
     *薄荷奶油
     *245,255,250
     */
    public static final int MINTCREAM = 0XF5FFFA;
    /*
     *薄雾玫瑰
     *255,228,225
     */
    public static final int MISTYROSE = 0XFFE4E1;
    /*
     *鹿皮鞋
     *255,228,181
     */
    public static final int MOCCASIN = 0XFFE4B5;
    /*
     *纳瓦霍白
     *255,222,173
     */
    public static final int NAVAJOWHITE = 0XFFDEAD;
    /*
     *海军蓝
     *0,0,128
     */
    public static final int NAVY = 0X000080;
    /*
     *老饰带
     *253,245,230
     */
    public static final int OLDLACE = 0XFDF5E6;
    /*
     *橄榄
     *128,128,0
     */
    public static final int OLIVE = 0X808000;
    /*
     *橄榄土褐色
     *85,107,47
     */
    public static final int OLIVEDRAB = 0X556B2F;
    /*
     *橙色
     *255,165,0
     */
    public static final int ORANGE = 0XFFA500;
    /*
     *橙红色
     *255,69,0
     */
    public static final int ORANGERED = 0XFF4500;
    /*
     *兰花的紫色
     *218,112,214
     */
    public static final int ORCHID = 0XDA70D6;
    /*
     *灰秋麒麟
     *238,232,170
     */
    public static final int PALEGODENROD = 0XEEE8AA;
    /*
     *苍白的绿色
     *152,251,152
     */
    public static final int PALEGREEN = 0X98FB98;
    /*
     *苍白的绿宝石
     *175,238,238
     */
    public static final int PALETURQUOISE = 0XAFEEEE;
    /*
     *苍白的紫罗兰红色
     *219,112,147
     */
    public static final int PALEVIOLETRED = 0XDB7093;
    /*
     *番木瓜
     *255,239,213
     */
    public static final int PAPAYAWHIP = 0XFFEFD5;
    /*
     *桃色
     *255,218,185
     */
    public static final int PEACHPUFF = 0XFFDAB9;
    /*
     *秘鲁
     *205,133,63
     */
    public static final int PERU = 0XCD853F;
    /*
     *粉红
     *255,192,203
     */
    public static final int PINK = 0XFFC0CB;
    /*
     *李子
     *221,160,221
     */
    public static final int PLUM = 0XDDA0DD;
    /*
     *火药蓝
     *176,224,230
     */
    public static final int POWDERBLUE = 0XB0E0E6;
    /*
     *紫色
     *128,0,128
     */
    public static final int PURPLE = 0X800080;
    /*
     *纯红
     *255,0,0
     */
    public static final int RED = 0XFF0000;
    /*
     *玫瑰棕色
     *188,143,143
     */
    public static final int ROSYBROWN = 0XBC8F8F;
    /*
     *皇家蓝
     *65,105,225
     */
    public static final int ROYALBLUE = 0X4169E1;
    /*
     *马鞍棕色
     *139,69,19
     */
    public static final int SADDLEBROWN = 0X8B4513;
    /*
     *鲜肉(鲑鱼)色
     *250,128,114
     */
    public static final int SALMON = 0XFA8072;
    /*
     *沙棕色
     *244,164,96
     */
    public static final int SANDYBROWN = 0XF4A460;
    /*
     *海洋绿
     *46,139,87
     */
    public static final int SEAGREEN = 0X2E8B57;
    /*
     *海贝壳
     *255,245,238
     */
    public static final int SEASHELL = 0XFFF5EE;
    /*
     *黄土赭色
     *160,82,45
     */
    public static final int SIENNA = 0XA0522D;
    /*
     *银白色
     *192,192,192
     */
    public static final int SILVER = 0XC0C0C0;
    /*
     *天蓝色
     *135,206,235
     */
    public static final int SKYBLUE = 0X87CEEB;
    /*
     *板岩暗蓝灰色
     *106,90,205
     */
    public static final int SLATEBLUE = 0X6A5ACD;
    /*
     *石板灰
     *112,128,144
     */
    public static final int SLATEGRAY = 0X708090;
    /*
     *雪
     *255,250,250
     */
    public static final int SNOW = 0XFFFAFA;
    /*
     *春天的绿色
     *60,179,113
     */
    public static final int SPRINGGREEN = 0X3CB371;
    /*
     *钢蓝
     *70,130,180
     */
    public static final int STEELBLUE = 0X4682B4;
    /*
     *晒黑
     *210,180,140
     */
    public static final int TAN = 0XD2B48C;
    /*
     *水鸭色
     *0,128,128
     */
    public static final int TEAL = 0X008080;
    /*
     *蓟
     *216,191,216
     */
    public static final int THISTLE = 0XD8BFD8;
    /*
     *番茄
     *255,99,71
     */
    public static final int TOMATO = 0XFF6347;
    /*
     *绿宝石
     *64,224,208
     */
    public static final int TURQUOISE = 0X40E0D0;
    /*
     *紫罗兰
     *238,130,238
     */
    public static final int VIOLET = 0XEE82EE;
    /*
     *小麦色
     *245,222,179
     */
    public static final int WHEAT = 0XF5DEB3;
    /*
     *纯白
     *255,255,255
     */
    public static final int WHITE = 0XFFFFFF;
    /*
     *白烟
     *245,245,245
     */
    public static final int WHITESMOKE = 0XF5F5F5;
    /*
     *纯黄
     *255,255,0
     */
    public static final int YELLOW = 0XFFFF00;
    /**
     * 内置颜色
     */
    private static final Color[] BUILT_COLORS =
            new Color[]{
                    new Color(0, 135, 255),
                    new Color(51, 153, 51),
                    new Color(255, 102, 102),
                    new Color(255, 153, 0),
                    new Color(153, 102, 0),
                    new Color(153, 102, 153),
                    new Color(51, 153, 153),
                    new Color(102, 102, 255),
                    new Color(0, 102, 204),
                    new Color(204, 51, 51),
                    new Color(128, 153, 65)
            };

    public static float MAX_BYTE_VALUE = 255f;

    private ToolColor() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * 获取浮点数ARGB值
     *
     * @param argb ARGB int值
     * @return {@code float[]}A,R,G,B
     */
    public static float[] getFloatARGBs(int argb) {
        int[] r = ToolColor.getIntARGBs(argb);
        return new float[]{
                r[0] / MAX_BYTE_VALUE, r[1] / MAX_BYTE_VALUE, r[2] / MAX_BYTE_VALUE, r[3] / MAX_BYTE_VALUE
        };
    }

    /**
     * 获取浮点数ARGB值
     *
     * @param rgb RGB int值
     * @return {@code float[]}R,G,B
     */
    public static float[] getFloatRGBs(int rgb) {
        int[] r = ToolColor.getIntRGBs(rgb);
        return new float[]{r[0] / MAX_BYTE_VALUE, r[1] / MAX_BYTE_VALUE, r[2] / MAX_BYTE_VALUE};
    }

    /**
     * 获取255内ARGB数值
     *
     * @param argb ARGB int值
     * @return {@code int[]}A,R,G,B
     */
    public static int[] getIntARGBs(int argb) {
        int[] r = ToolColor.getIntRGBs(argb);
        int a = ToolMath.extractBitHalfByteVal(argb, 4);
        return new int[]{a, r[0], r[1], r[2]};
    }

    /**
     * 获取255内ARGB数值
     *
     * @param rgb RGB int值
     * @return {@code int[]}R,G,B
     */
    public static int[] getIntRGBs(int rgb) {
        int b = ToolMath.extractBitHalfByteVal(rgb, 1);
        int g = ToolMath.extractBitHalfByteVal(rgb, 2);
        int r = ToolMath.extractBitHalfByteVal(rgb, 3);
        return new int[]{r, g, b};
    }

    /**
     * 获取255内RGB or ARGB数值
     *
     * @param value RGB or ARGB int值
     * @return {@code int[]}
     */
    public static int[] getColorIntValues(int value) {
        int b = ToolMath.extractBitHalfByteVal(value, 1);
        int g = ToolMath.extractBitHalfByteVal(value, 2);
        int r = ToolMath.extractBitHalfByteVal(value, 3);
        int a = ToolMath.extractBitHalfByteVal(value, 4);
        return a == 0 ? new int[]{r, g, b} : new int[]{a, r, g, b};
    }

    /**
     * 随机颜色ARGB值
     *
     * @return {@code int[]}A,R,G,B
     */
    public static int[] randomARGBs() {
        SecureRandom secureRandom = new SecureRandom();
        return new int[]{
                secureRandom.nextInt(255),
                secureRandom.nextInt(255),
                secureRandom.nextInt(255),
                secureRandom.nextInt(255)
        };
    }

    /**
     * 随机颜色RGB值
     *
     * @return {@code int[]}R,G,B
     */
    public static int[] randomRGBs() {
        SecureRandom secureRandom = new SecureRandom();
        return new int[]{
                secureRandom.nextInt(255), secureRandom.nextInt(255), secureRandom.nextInt(255)
        };
    }

    /**
     * 随机颜色ARGB值
     *
     * @return A, R, G, B
     */
    public static int randomARGB() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(255)
                << 24 + secureRandom.nextInt(255)
                << 16 + secureRandom.nextInt(255)
                << 8 + secureRandom.nextInt(255)
                << 1;
    }

    /**
     * 随机颜色RGB值
     *
     * @return R, G, B
     */
    public static int randomRGB() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(255)
                << 16 + secureRandom.nextInt(255)
                << 8 + secureRandom.nextInt(255)
                << 1;
    }

    /**
     * 随机颜色
     *
     * @return Color
     */
    public static Color randomColor() {
        int[] rgb = randomRGBs();
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * 随机颜色
     *
     * @return Color
     */
    public static Color randomBuiltColor() {
        SecureRandom random = new SecureRandom();
        int colorIndex = random.nextInt(BUILT_COLORS.length);
        return BUILT_COLORS[colorIndex];
    }

    /**
     *
     */
    public static Color createColor(String input, Color defaultColor) {
        Color color;
        if (Whether.empty(input)) {
            color = defaultColor;
        } else if (input.charAt(0) == '#') {
            color =
                    createColorFromValues(
                            getColorIntValues(Integer.parseInt(input.substring(1))), defaultColor);
        } else if (input.indexOf(",") > 0) {
            color = createColorFromSeparatedValues(input, ',', defaultColor);
        } else if (input.indexOf("#") > 0) {
            color = createColorFromSeparatedValues(input, '#', defaultColor);
        } else {
            color = createColorFromFieldValue(input, defaultColor);
        }
        return color;
    }

    /**
     * 创建颜色从字符串中
     *
     * @param input        输入字符串
     * @param separated    分隔符
     * @param defaultColor 默认颜色
     * @return Color
     */
    public static Color createColorFromSeparatedValues(
            String input, char separated, Color defaultColor) {
        return createColorFromValues(
                ToolString.splitSafely(input, String.valueOf(separated)).toArray(new String[0]),
                defaultColor);
    }

    /**
     * 创建颜色从字符串数值中
     *
     * @param input        输入字符串数值
     * @param defaultColor 默认颜色
     * @return Color
     */
    public static Color createColorFromValues(String[] input, Color defaultColor) {
        if (Whether.empty(input)) {
            return defaultColor;
        }
        try {
            if (input.length == 3) {
                int r = Integer.parseInt(input[0]);
                int g = Integer.parseInt(input[1]);
                int b = Integer.parseInt(input[2]);
                return new Color(r, g, b);
            } else if (input.length == 4) {
                int r = Integer.parseInt(input[0]);
                int g = Integer.parseInt(input[1]);
                int b = Integer.parseInt(input[2]);
                int a = Integer.parseInt(input[3]);
                return new Color(r, g, b, a);
            }
        } catch (NumberFormatException ignored) {
        }
        return defaultColor;
    }

    /**
     * 创建颜色从数值中
     *
     * @param input        输入数值
     * @param defaultColor 默认颜色
     * @return Color
     */
    public static Color createColorFromValues(byte[] input, Color defaultColor) {
        if (Whether.empty(input)) {
            return defaultColor;
        }
        if (input.length == 3) {
            return new Color(input[0], input[1], input[2]);
        } else if (input.length == 4) {
            return new Color(input[0], input[1], input[2], input[3]);
        }
        return defaultColor;
    }

    /**
     * 创建颜色从数值中
     *
     * @param input        输入数值
     * @param defaultColor 默认颜色
     * @return Color
     */
    public static Color createColorFromValues(int[] input, Color defaultColor) {
        if (Whether.empty(input)) {
            return defaultColor;
        }
        if (input.length == 3) {
            return new Color(input[0], input[1], input[2]);
        } else if (input.length == 4) {
            return new Color(input[0], input[1], input[2], input[3]);
        }
        return defaultColor;
    }

    /**
     * 创建颜色从Color静态域中
     *
     * @param input        输入值
     * @param defaultColor 默认颜色
     * @return Color
     */
    public static Color createColorFromFieldValue(String input, Color defaultColor) {
        try {
            Field field = Class.forName("java.awt.Color").getField(input);
            return (Color) field.get(null);
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException ignored) {
        }
        return defaultColor;
    }
}
