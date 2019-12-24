package cn.vip.dw.bluetoothprinterlib.ui;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * 作者：DongYonghui
 * 邮箱：648731994@qq.com
 * 创建时间：2019/12/2/002 10:12
 * 描述：对话框帮助类
 */
public class PrinterDialogHelper {
    private static AlertDialog alertDialog;

    public static void showDialog(Activity mActivity, String title, String message) {
        hideDialog();
        alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    public static void hideDialog() {
        if (null != alertDialog) {
            alertDialog.dismiss();
        }
    }
}
