package cn.vip.dw.bluetoothprinterlib.parser;

/**
 * 作者";//DongYonghui
 * 邮箱";//648731994@qq.com
 * 创建时间";//2019/12/23/023 10:55
 * 描述";//格式化标签常量
 */
public class FormatTagConstants {
    public static final String TAG_BR = "BR";//换行符
    public static final String TAG_CUT = "CUT";//切刀指令(主动切纸,仅限切刀打印机使用才有效果)
    public static final String TAG_PLUGIN = "  PLUGIN";//钱箱或者外置音响指令
    public static final String TAG_CB = "CB";//居中放大
    public static final String TAG_B = "B";//放大一倍
    public static final String TAG_C = "C";//居中
    public static final String TAG_L = "L";//字体变高一倍
    public static final String TAG_W = "W";//字体变宽一倍
    public static final String TAG_QR = "QR";//二维码
    public static final String TAG_CODE = "CODE";//条形码
    public static final String TAG_RIGHT = "RIGHT";//右对齐
    public static final String TAG_BOLD = "BOLD";//加粗
    public static final String TAG_ROW = "ROW";//行 与 列共同使用
    public static final String TAG_COL = "COL";//列 与 行共同使用，每行最多支持4列
    public static final String TAG_COL_WEIGHT = "WEIGHT";//列宽 每行58小票支持32个正常大小汉字；80小票支持48个正常大小汉字
    public static final String TAG_COL_PADDING = "PADDING";//列padding
    public static final String TAG_COL_GRAVITY = "GRAVITY";//列对齐模式 :left center right 默认left
    public static final String TAG_COL_EOL = "EOL";//填充字符
}
