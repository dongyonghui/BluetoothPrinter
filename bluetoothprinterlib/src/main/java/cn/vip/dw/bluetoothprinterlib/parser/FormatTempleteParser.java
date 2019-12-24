package cn.vip.dw.bluetoothprinterlib.parser;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.vip.dw.bluetoothprinterlib.BluetoothPrintManager;
import cn.vip.dw.bluetoothprinterlib.ImageUtil;
import cn.vip.dw.bluetoothprinterlib.velocity.RowTool;

/**
 * 作者：DongYonghui
 * 邮箱：648731994@qq.com
 * 创建时间：2019/12/23/023 11:18
 * 描述：格式化模板解析
 */
public class FormatTempleteParser {

    /**
     * 获取ESC命令
     *
     * @param printInfo 包含标签的文本信息
     * @return 返回ESCCommand数据
     */
    public static EscCommand addEscCommand(String printInfo, EscCommand escCommand) {
        if (!TextUtils.isEmpty(printInfo)) {
            //一行一行处理命令
            //清空回车符
            String tempInfo = printInfo.replaceAll("\r", "")
                    .replaceAll(" ", "  ");
            String[] rows = tempInfo.split("\n");
            //先重置打印格式
            for (String row : rows) {
                resetTextStyle(escCommand);
                parseRow(row, escCommand);
                escCommand.addFeedLine();
            }
        }
        return escCommand;
    }


    /**
     * 二维码的宽度
     */
    public static int WIDTH_QR_IMAGE = 120;
    private static String tempNodeName;//临时解析tag
    private static List<ColumInfo> columInfoList;//每行的列数据
    private static ColumInfo columInfo;//列数据

    /**
     * 列数据
     */
    private static class ColumInfo {
        public String weight = "16";//1-32 或 1-48
        public String padding = "0";
        public String gravity = "left";
        public String eol = "";
        public String text;
    }

    private static void addQRCode(String rowInfo, EscCommand esc) {
        esc.addRastBitImage(ImageUtil.createQRImage(rowInfo, WIDTH_QR_IMAGE, WIDTH_QR_IMAGE));
    }

    private static void addBarCode(String rowInfo, EscCommand esc) {
        esc.addCODE39(rowInfo);
    }

    /**
     * 根据模板内容，将某一行的文本信息解析成打印机命令数组
     *
     * @param rowInfo 根据模板生成的文件的某一行信息
     * @param esc     解析成打印机命令数组会保存到这里
     */
    private static void parseRow(String rowInfo, EscCommand esc) {
        if (TextUtils.isEmpty(rowInfo)) {
            return;
        }
        //如果有标签
        if (Pattern.matches(".*<[a-zA-Z]+>.*", rowInfo)) {
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(rowInfo));
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = parser.getName();
                    switch (eventType) {
                        // 开始解析某个结点
                        case XmlPullParser.START_TAG: {
                            Log.d("Printer", "标签开始｛ nodeName = " + nodeName);
                            if (FormatTagConstants.TAG_C.equalsIgnoreCase(nodeName)) {//居中
                                setCenterStyle(esc);
                            } else if (FormatTagConstants.TAG_B.equalsIgnoreCase(nodeName)) {//放大2倍
                                setBiggerStyle(esc);
                            } else if (FormatTagConstants.TAG_L.equalsIgnoreCase(nodeName)) {//高放大2倍
                                setHeightMoreStyle(esc);
                            } else if (FormatTagConstants.TAG_W.equalsIgnoreCase(nodeName)) {//宽放大2倍
                                setWidthMoreStyle(esc);
                            } else if (FormatTagConstants.TAG_CB.equalsIgnoreCase(nodeName)) {//居中放大
                                setBiggerCenterStyle(esc);
                            } else if (FormatTagConstants.TAG_RIGHT.equalsIgnoreCase(nodeName)) {//右对齐
                                setTextRightStyle(esc);
                            } else if (FormatTagConstants.TAG_QR.equalsIgnoreCase(nodeName)) {//二维码
                                tempNodeName = nodeName;
                            } else if (FormatTagConstants.TAG_CODE.equalsIgnoreCase(nodeName)) {//条形码
                                tempNodeName = nodeName;
                            } else if (FormatTagConstants.TAG_BR.equalsIgnoreCase(nodeName)) {//换行
                                esc.addFeedLine();
                                setTextLeftStyle(esc);
                            } else if (FormatTagConstants.TAG_CUT.equalsIgnoreCase(nodeName)) {//切纸
                                esc.addCutPaperAndFeed(BluetoothPrintManager.CUT_PAPER_AND_FEED);
                            } else if (FormatTagConstants.TAG_PLUGIN.equalsIgnoreCase(nodeName)) {//钱箱
                                esc.addOpenCashDawer(TscCommand.FOOT.F5, (byte) 1, (byte) 0);
                            } else if (FormatTagConstants.TAG_BOLD.equalsIgnoreCase(nodeName)) {//加粗
                                setTextBoldStyle(esc);
                            } else if (FormatTagConstants.TAG_ROW.equalsIgnoreCase(nodeName)) {//行
                                columInfoList = new ArrayList<>();
                            } else if (FormatTagConstants.TAG_COL.equalsIgnoreCase(nodeName)) {//列
                                columInfo = new ColumInfo();
                                for (int i = 0; i < parser.getAttributeCount(); i++) {
                                    if (FormatTagConstants.TAG_COL_WEIGHT.equalsIgnoreCase(parser.getAttributeName(i))) {
                                        columInfo.weight = parser.getAttributeValue(i);
                                    } else if (FormatTagConstants.TAG_COL_PADDING.equalsIgnoreCase(parser.getAttributeName(i))) {
                                        columInfo.padding = parser.getAttributeValue(i);
                                    } else if (FormatTagConstants.TAG_COL_GRAVITY.equalsIgnoreCase(parser.getAttributeName(i))) {
                                        columInfo.gravity = parser.getAttributeValue(i);
                                    } else if (FormatTagConstants.TAG_COL_EOL.equalsIgnoreCase(parser.getAttributeName(i))) {
                                        columInfo.eol = parser.getAttributeValue(i);
                                    }
                                }
                            }
                            break;
                        }
                        // 完成解析某个结点
                        case XmlPullParser.END_TAG: {
                            Log.d("Printer", "标签结束｝ nodeName = " + nodeName);
                            if (FormatTagConstants.TAG_CB.equalsIgnoreCase(nodeName)//居中放大
                                    || FormatTagConstants.TAG_B.equalsIgnoreCase(nodeName)//放大2倍
                                    || FormatTagConstants.TAG_L.equalsIgnoreCase(nodeName)//高放大2倍
                                    || FormatTagConstants.TAG_W.equalsIgnoreCase(nodeName)) {//宽放大2倍
                                resetTextSize(esc);
                            } else if (FormatTagConstants.TAG_BOLD.equalsIgnoreCase(nodeName)) {//加粗
                                resetTextBoldStyle(esc);
                            } else if (FormatTagConstants.TAG_QR.equalsIgnoreCase(nodeName)) {//二维码
                                tempNodeName = null;
                            } else if (FormatTagConstants.TAG_CODE.equalsIgnoreCase(nodeName)) {//条形码
                                tempNodeName = null;
                            } else if (FormatTagConstants.TAG_ROW.equalsIgnoreCase(nodeName)) {//行
                                RowTool rowTool = new RowTool();
                                StringBuilder stringBuilder = new StringBuilder();
                                for (ColumInfo info : columInfoList) {
                                    stringBuilder.append(rowTool.format(info.eol, info.text,
                                            String.valueOf(info.weight)
                                            , info.padding, info.gravity));
                                }
                                esc.addText(stringBuilder.toString());
                                columInfoList = null;
                            } else if (FormatTagConstants.TAG_COL.equalsIgnoreCase(nodeName)) {//列
                                columInfo = null;
                            }
                            break;
                        }
                        case XmlPullParser.TEXT:
                            Log.d("Printer", "文本内容--------" + parser.getText());
                            if (FormatTagConstants.TAG_QR.equalsIgnoreCase(tempNodeName)) {
                                addQRCode(parser.getText(), esc);
                            } else if (FormatTagConstants.TAG_CODE.equalsIgnoreCase(tempNodeName)) {
                                addBarCode(parser.getText(), esc);
                            } else if (null != columInfo && null != columInfoList) {
                                columInfo.text = parser.getText();
                                columInfoList.add(columInfo);
                            } else {
                                esc.addText(parser.getText());
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            esc.addText(rowInfo);
        }
    }

    private static void setBiggerCenterStyle(EscCommand esc) {
        esc.addSetJustification(EscCommand.JUSTIFICATION.CENTER);//居中
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);//宽高放大两倍
    }

    private static void setBiggerStyle(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_2);//宽高放大两倍
    }


    private static void setBigger2Style(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_4, EscCommand.HEIGHT_ZOOM.MUL_4);//宽高放大4倍
    }

    private static void setCenterStyle(EscCommand esc) {
        esc.addSetJustification(EscCommand.JUSTIFICATION.CENTER);//居中
    }

    private static void setHeightMoreStyle(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_2);//宽高放大两倍
    }

    private static void setWidthMoreStyle(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_2, EscCommand.HEIGHT_ZOOM.MUL_1);//宽高放大两倍
    }

    private static void setImageStyle(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
    }

    public static void resetTextStyle(EscCommand esc) {
        esc.addSetLeftMargin((short) 0);
        esc.addSetJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
    }

    public static void resetTextSize(EscCommand esc) {
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
    }

    public static void setTextLeftStyle(EscCommand esc) {
        esc.addSetJustification(EscCommand.JUSTIFICATION.LEFT);
    }

    public static void setTextBoldStyle(EscCommand esc) {
        esc.addTurnDoubleStrikeOnOrOff(EscCommand.ENABLE.ON);
    }

    public static void resetTextBoldStyle(EscCommand esc) {
        esc.addTurnDoubleStrikeOnOrOff(EscCommand.ENABLE.OFF);
    }

    public static void setTextRightStyle(EscCommand esc) {
        esc.addSetLeftMargin((short) 0);
        esc.addSetJustification(EscCommand.JUSTIFICATION.RIGHT);
    }

}
