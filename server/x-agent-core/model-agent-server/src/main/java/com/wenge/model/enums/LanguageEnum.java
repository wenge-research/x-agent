package com.wenge.model.enums;

import lombok.Getter;

/**
 * 元枢语言枚举
 */
@Getter
public enum LanguageEnum {
    ZH("中文、汉语", "zh", 1),
    EN("英语", "en", 2),
    JA("日语", "ja", 3),
    KO("朝鲜语、韩语", "ko", 4),
    RU("俄语", "ru", 5),
    ES("西班牙语", "es", 6),
    FR("法语", "fr", 7),
    AR("阿拉伯语", "ar", 8),
    PT("葡萄牙语", "pt", 9),
    DE("德语", "de", 10),
    MS("马来语", "ms", 11),
    TA("泰米尔语", "ta", 12),
    TH("泰语", "th", 13),
    KM("高棉语", "km", 14),
    ID("印尼语", "id", 15),
    VI("越南语", "vi", 16),
    TR("土耳其语", "tr", 17),
    ET("爱沙尼亚语", "et", 18),
    UK("乌克兰语", "uk", 19),
    IT("意大利语", "it", 20),
    SR("塞尔维亚语", "sr", 21),
    BE("白俄罗斯语", "be", 22),
    EU("巴斯克语", "eu", 23),
    HU("匈牙利语", "hu", 24),
    BG("保加利亚语", "bg", 25),
    UR("乌尔都语", "ur", 26),
    SO("索马里语", "so", 27),
    GU("古吉拉特语", "gu", 28),
    FA("波斯语", "fa", 29),
    PL("波兰语", "pl", 30),
    NL("荷兰语", "nl", 33),
    HR("克罗地亚语", "hr", 34),
    NE("尼泊尔语", "ne", 35),
    BN("孟加拉语", "bn", 36),
    FI("芬兰语", "fi", 37),
    LT("立陶宛语", "lt", 38),
    EL("现代希腊语", "el", 39),
    RO("罗马尼亚语", "ro", 40),
    SV("瑞典语", "sv", 41),
    HI("印地语", "hi", 42),
    GL("加利西亚语", "gl", 43),
    AF("南非语", "af", 47),
    AM("阿姆哈拉语", "am", 49),
    AN("阿拉贡语", "an", 50),
    AS("阿萨姆语", "as", 51),
    AZ("阿塞拜疆语", "az", 54),
    BR("布列塔尼语", "br", 60),
    BS("波斯尼亚语", "bs", 61),
    CA("加泰隆语", "ca", 62),
    CS("捷克语", "cs", 67),
    CY("威尔士语", "cy", 70),
    DA("丹麦语", "da", 71),
    DZ("不丹语", "dz", 73),
    EO("世界语", "eo", 75),
    FO("法罗语", "fo", 78),
    GA("爱尔兰语", "ga", 80),
    HE("希伯来语", "he", 85),
    HT("海地克里奥尔语", "ht", 87),
    HY("亚美尼亚语", "hy", 88),
    IS("冰岛语", "is", 96),
    JV("爪哇语", "jv", 98),
    KA("格鲁吉亚语", "ka", 99),
    KK("哈萨克语", "kk", 103),
    KN("卡纳达语", "kn", 105),
    KU("库尔德语", "ku", 108),
    KY("吉尔吉斯语", "ky", 111),
    LA("拉丁语", "la", 112),
    LB("卢森堡语", "lb", 113),
    LO("老挝语", "lo", 117),
    LV("拉脱维亚语", "lv", 119),
    MG("马达加斯加语", "mg", 120),
    MK("马其顿语", "mk", 123),
    ML("马拉亚拉姆语", "ml", 124),
    MN("蒙古语", "mn", 125),
    MR("马拉提语", "mr", 127),
    MT("马耳他语", "mt", 128),
    NB("书面挪威语", "nb", 131),
    NN("新挪威语", "nn", 134),
    NO("挪威语", "no", 135),
    OC("奥克语", "oc", 139),
    OR("奥利亚语", "or", 142),
    PA("旁遮普语", "pa", 144),
    PS("普什图语", "ps", 146),
    QU("凯楚亚语", "qu", 147),
    RW("卢旺达语", "rw", 150),
    SE("北萨米语", "se", 154),
    SI("僧加罗语", "si", 157),
    SK("斯洛伐克语", "sk", 158),
    SL("斯洛文尼亚语", "sl", 159),
    SQ("阿尔巴尼亚语", "sq", 162),
    SW("斯瓦希里语", "sw", 166),
    TE("泰卢固语", "te", 167),
    TL("他加禄语", "tl", 171),
    UG("维吾尔语", "ug", 178),
    VO("沃拉普克语", "vo", 181),
    WA("沃伦语", "wa", 182),
    XH("科萨语", "xh", 184),
    ZU("祖鲁语", "zu", 190);

    private final String name;
    private final String code;
    private final int value;

    LanguageEnum(String name, String code, int value) {
        this.name = name;
        this.code = code;
        this.value = value;
    }

    public static String getLanguage(int value) {
        for (LanguageEnum languageEnum : LanguageEnum.values()) {
            if (languageEnum.getValue() == value) {
                return languageEnum.getName();
            }
        }
        return null;
    }
}


