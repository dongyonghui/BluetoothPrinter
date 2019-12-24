package cn.vip.dw.bluetoothprinterlib;

/**
 * 作者：DongYonghui
 * 邮箱：648731994@qq.com
 * 创建时间：2019/12/2/002 11:04
 * 描述：打印机配置信息
 */
public class PrinterConfig {
    private int printCount = 1;//每联打印的数量
    private int pagerWidth = 58;//小票宽度（58：80）

    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public int getPagerWidth() {
        return pagerWidth;
    }

    public void setPagerWidth(int pagerWidth) {
        this.pagerWidth = pagerWidth;
    }
}
