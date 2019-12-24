package cn.vip.dw.bluetoothprinterlib.velocity;

import java.io.Serializable;

/**
 * User: DongYonghui(404638723@qq.com)
 * Date: 2016-01-03
 * Time: 20:31
 * 打印小票
 */
public class PrinterBean implements Serializable {
    /**
     * 打印多少张小票，默认打印一张
     */
    public int printCount = 1;
    /**
     * 小票模板信息(velocity模板)
     */
    public String templateInfo;
    /**
     * 小票模板velocity中一级Bean的key
     */
    public String templateBeanKey;

    /**
     * 小票模板velocity中一级Bean
     */
    public Object templateBean;
}
