package com.dyh.bluetoothprinter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.vip.dw.bluetoothprinterlib.BluetoothPrintManager;
import cn.vip.dw.bluetoothprinterlib.OnPrinterNotifyListener;
import cn.vip.dw.bluetoothprinterlib.PrinterConfig;
import cn.vip.dw.bluetoothprinterlib.ui.PrinterManagerActivity;
import cn.vip.dw.bluetoothprinterlib.velocity.PrinterBean;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 打印纯文本消息
     *
     * @param view
     */
    public void printText(View view) {
        PrinterBean printerBean = new PrinterBean();
        printerBean.templateInfo = "欢迎使用DYH蓝牙打印类库\n测试纯文本消息打印\n此打印方式打印无格式内容";
        BluetoothPrintManager.getInstance().print(this, printerBean);
    }

    /**
     * 打印静态格式化文本消息
     *
     * @param view
     */
    public void printStaticFormatText(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<row><col weight=\"16\">商品</col><col weight=\"8\">价格</col><col weight=\"8\" gravity=\"right\">数量</col></row><BR>");
        stringBuilder.append("<row><col weight=\"16\">红烧肉</col><col weight=\"8\">12元</col><col weight=\"8\" gravity=\"right\">3</col></row><BR>");
        stringBuilder.append("<row><col weight=\"16\">可口可乐</col><col weight=\"8\">6元</col><col weight=\"8\" gravity=\"right\">3</col></row><BR>");
        stringBuilder.append("<C><B>放大居中</B></C><BR>");
        stringBuilder.append("<C><L>变高居中</L></C><BR>");
        stringBuilder.append("<C><W>变宽居中</W></C><BR>");
        stringBuilder.append("<C>字体加粗</C><BR>");
        stringBuilder.append("<RIGHT>右对齐效果</RIGHT><BR>");
        stringBuilder.append("<RIGHT><L>变高右对齐</L></RIGHT><BR>");
        stringBuilder.append("<RIGHT><W>变宽右对齐</W></RIGHT><BR>");
        stringBuilder.append("<C><QR>www.baidu.com</QR></C><BR>");
        stringBuilder.append("<C><CODE>123456890</CODE></C><BR>");


        PrinterBean printerBean = new PrinterBean();
        printerBean.templateInfo = stringBuilder.toString();
        BluetoothPrintManager.getInstance().print(this, printerBean);
    }

    /**
     * 打印模板消息
     *
     * @param view
     */
    public void printTemplateFormatText(View view) {
        //组织打印信息
        OrderPrinterBean orderPrinterBean = getOrderPrinterBean();
        PrinterConfig printerConfig = BluetoothPrintManager.getInstance().getPrinterConfig(this);
        String tempPath = "/assets/printer_template_" + printerConfig.getPagerWidth() + "/order.vm";
        PrinterBean printerBean = BluetoothPrintManager.getInstance().getPrinterBean(this,
                tempPath,
                "printBean", orderPrinterBean
                , 1);


        BluetoothPrintManager.getInstance()
                .setAutoOpenSettingActivity(true)//如果需要操作蓝牙设备，自动跳转到设置页面
                .setNeedShowPrintingDialog(this, true)//是否展示打印中对话框
                .setOnPrinterNotifyListener(new OnPrinterNotifyListener() {
                    @Override
                    public void onPrinterNotify(NotifyMessage notifyMessage) {
                        switch (notifyMessage) {
                            case PRINT_FINISH://打印成功后回调
                                //递归打印列表项
                                Toast.makeText(getApplicationContext(), "打印成功", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .print(this, printerBean);
    }

    private OrderPrinterBean getOrderPrinterBean() {
        OrderPrinterBean orderPrinterBean = new OrderPrinterBean();
        orderPrinterBean.setRemark("不要辣");
        orderPrinterBean.setOrderNumber("36823");
        orderPrinterBean.setUserAddress("望京SOHO");
        orderPrinterBean.setUserName("王先生");
        orderPrinterBean.setUserPhone("138****3242");

        List<OrderPrinterBean.SkuItemBean> list = new ArrayList<>();
        OrderPrinterBean.SkuItemBean skuItemBean = new OrderPrinterBean.SkuItemBean();
        skuItemBean.setSkuCount("2");
        skuItemBean.setSkuName("红烧肉");
        list.add(skuItemBean);
        skuItemBean = new OrderPrinterBean.SkuItemBean();
        skuItemBean.setSkuCount("2");
        skuItemBean.setSkuName("可乐");
        list.add(skuItemBean);
        skuItemBean = new OrderPrinterBean.SkuItemBean();
        skuItemBean.setSkuCount("2");
        skuItemBean.setSkuName("炸鸡");
        list.add(skuItemBean);
        orderPrinterBean.setSkuList(list);
        return orderPrinterBean;
    }

    /**
     * 设置打印机
     *
     * @param view
     */
    public void settingPrinter(View view) {
        startActivity(new Intent(this, PrinterManagerActivity.class));
    }
}
