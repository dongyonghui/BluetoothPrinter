package cn.vip.dw.bluetoothprinterlib.velocity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: DongYonghui(404638723@qq.com)
 * Date: 2016-01-04
 * Time: 09:57
 */
public class ServerDatUtils extends DateUtils {
    public static String format(String parent, Date date) {
        if (date == null) {
            return "";
        }

        return format(new SimpleDateFormat(parent), date);
    }
}
