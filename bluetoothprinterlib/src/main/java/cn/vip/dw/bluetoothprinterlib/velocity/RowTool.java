package cn.vip.dw.bluetoothprinterlib.velocity;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 格式化小票上的第一列文字,通常为商品或套餐名
 * <p/>
 * 类名: FirstColumnTool <br/>
 * 日期: 2015年12月8日 下午4:52:30 <br/>
 *
 * @author shilei
 */
public class RowTool {
    /**
     * 每一列参数的个数
     */
    private final Integer PARAM_COUNT = 4;
    /**
     * 对齐方式居左
     */
    private final String GRAVITY_LEFT = "left";
    /**
     * 对齐方式居中
     */
    private final String GRAVITY_CENTER = "center";
    /**
     * 对齐方式居右
     */
    private final String GRAVITY_RIGHT = "right";
    /**
     * 对齐方式居左，如果超出列的宽度向右充满全行再换行，后面的列向下移动一行
     */
    private final String GRAVITY_LEFT_FILL2RIGHT = "left_fill2right";

    static class LineColumnItemBean {
        public String content;
        public String gravity;
        public int columnIndex;
    }

    /**
     * 格式化 format:(方法功能描述). <br/>
     * <p/>
     * (方法的适用场景及注意事项).<br/>
     * <p/>
     * 日期: 2015年12月8日 下午4:53:06 <br/>
     * <p/>
     * <p>
     * 显示内容
     * 换行符
     * 每行长度
     * 列长度
     *
     * @return
     * @author shilei
     */
    public String format(String eol, String... values) {
        int columnCount = values.length / PARAM_COUNT;
        List<String>[] columnContent = new List[columnCount];
        List<Integer>[] columnContentLeftWidth = new List[columnCount];
        Integer[] columnWidth = new Integer[columnCount];
        Integer[] columnPad = new Integer[columnCount];
        String[] columnGravity = new String[columnCount];
        Integer[] columnLineCount = new Integer[columnCount];
        int lineCount = 0;
        StringBuilder sb = new StringBuilder();
        List<LineColumnItemBean> fillRowContentList = new ArrayList<>();
        for (int column = 0; column < columnCount; column++) {
            columnWidth[column] = Integer.parseInt(values[PARAM_COUNT * column + 1]);
            columnPad[column] = Integer.parseInt(values[PARAM_COUNT * column + 2]);
            columnGravity[column] = values[PARAM_COUNT * column + 3];
            columnContent[column] = new ArrayList<>();
            columnContentLeftWidth[column] = new ArrayList<>();


            //充满整行的提取出来，把原值用空字符串代替
            switch (columnGravity[column]) {
                case GRAVITY_LEFT_FILL2RIGHT:
                    int contentLength = 0;
                    char[] chars = values[PARAM_COUNT * column].toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        if (isDoubleChar(chars[i])) {
                            contentLength += 2;
                        } else {
                            contentLength++;
                        }
                    }
                    //长度大于列的宽度时才整行单独显示
                    if ((contentLength + columnPad[column]) > columnWidth[column]) {
                        LineColumnItemBean columnItemBean = new LineColumnItemBean();
                        columnItemBean.content = values[PARAM_COUNT * column];
                        columnItemBean.columnIndex = column;
                        columnItemBean.gravity = columnGravity[column];
                        fillRowContentList.add(columnItemBean);
                        values[PARAM_COUNT * column] = "";
                    }
                    columnGravity[column] = GRAVITY_LEFT;
                    break;
                default:
                    break;
            }


            fillColumnContent(values[PARAM_COUNT * column], columnWidth[column] - columnPad[column],
                    columnContent[column], columnContentLeftWidth[column]);
            columnLineCount[column] = columnContent[column].size();
            if (columnLineCount[column] > lineCount) {
                lineCount = columnLineCount[column].intValue();
            }
        }

        //将提取出来的整行字符串添加到内容中
        if (fillRowContentList.size() > 0) {
            for (LineColumnItemBean columnItemBean : fillRowContentList) {
                List<String> content = new ArrayList<>();
                List<Integer> contentLeftWidth = new ArrayList<>();
                int cWidth = 0;
                for (int i = columnItemBean.columnIndex; i < columnCount; i++) {
                    cWidth += columnWidth[i];
                }
                fillColumnContent(columnItemBean.content, cWidth - columnPad[columnItemBean.columnIndex],
                        content, contentLeftWidth);
                for (int i = 0; i < content.size(); i++) {
                    switch (columnItemBean.gravity) {
                        case GRAVITY_LEFT_FILL2RIGHT:
                            sb.append(content.get(i))
                                    .append(StringUtils.rightPad("", contentLeftWidth.get(i), " "))
                                    .append(StringUtils.rightPad("", columnPad[columnItemBean.columnIndex], " "));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        for (int line = 0; line < lineCount; line++) {
            for (int column = 0; column < columnCount; column++) {
                if (line < columnLineCount[column]) {
                    switch (columnGravity[column]) {
                        case GRAVITY_LEFT:
                            sb.append(columnContent[column].get(line))
                                    .append(StringUtils.rightPad("", columnContentLeftWidth[column].get(line), " "))
                                    .append(StringUtils.rightPad("", columnPad[column], " "));
                            break;
                        case GRAVITY_CENTER:
                            int spaceCount = columnContentLeftWidth[column].get(line) + columnPad[column];
                            int leftSpaceCount = spaceCount / 2;
                            int rightSpaceCount = spaceCount - leftSpaceCount;
                            sb.append(StringUtils.rightPad("", leftSpaceCount, " "))
                                    .append(columnContent[column].get(line))
                                    .append(StringUtils.rightPad("", rightSpaceCount, " "));
                            break;
                        case GRAVITY_RIGHT:
                            sb.append(StringUtils.rightPad("", columnContentLeftWidth[column].get(line), " "))
                                    .append(StringUtils.rightPad("", columnPad[column], " "))
                                    .append(columnContent[column].get(line));
                            break;
                        default:
                            sb.append(StringUtils.rightPad("", columnContentLeftWidth[column].get(line), " "))
                                    .append(StringUtils.rightPad("", columnPad[column], " "))
                                    .append(columnContent[column].get(line));
                            break;
                    }
                } else {
                    sb.append(StringUtils.rightPad("", columnWidth[column], " "));
                }
            }
            sb.append(eol);
        }
        return sb.toString();
    }

    /**
     * 填充每行的内容
     * <p/>
     * fillColumnContent:(方法功能描述). <br/>
     * <p/>
     * (方法的适用场景及注意事项).<br/>
     * <p/>
     * 日期: 2015年12月10日 上午11:04:31 <br/>
     *
     * @param content
     * @param columnWidth
     * @param columnContent
     * @param columnContentLeftWidth
     * @author shilei
     */
    private void fillColumnContent(String content, int columnWidth, List<String> columnContent,
                                   List<Integer> columnContentLeftWidth) {
        int leftWidth = columnWidth;
        char[] chars = content.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (isDoubleChar(chars[i])) {
                leftWidth -= 2;
            } else {
                leftWidth--;
            }
            sb.append(chars[i]);
            if (leftWidth == 0 || leftWidth == 1 && (i < chars.length - 1 && isDoubleChar(chars[i + 1]))) {
                columnContent.add(sb.toString());
                sb = new StringBuilder();
                columnContentLeftWidth.add(leftWidth);
                leftWidth = columnWidth;
            }
        }
        if (leftWidth < columnWidth) {
            columnContent.add(sb.toString());
            columnContentLeftWidth.add(leftWidth);
        }
    }

    /**
     * 判断字符是否是全角.
     * <p/>
     * isDoubleChar:(方法功能描述). <br/>
     * <p/>
     * (方法的适用场景及注意事项).<br/>
     * <p/>
     * 日期: 2015年12月10日 上午11:19:32 <br/>
     *
     * @param c
     * @return
     * @author shilei
     */
    private boolean isDoubleChar(char c) {
        if (c >= 3 && c <= 126) {
            return false;
        } else {
            return true;
        }

    }
}
