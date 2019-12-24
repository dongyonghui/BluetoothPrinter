package cn.vip.dw.bluetoothprinterlib.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.vip.dw.bluetoothprinterlib.BluetoothPrintManager;
import cn.vip.dw.bluetoothprinterlib.OnPrinterNotifyListener;
import cn.vip.dw.bluetoothprinterlib.PrinterConfig;
import cn.vip.dw.bluetoothprinterlib.R;
import cn.vip.dw.bluetoothprinterlib.velocity.PrinterBean;

/**
 * 打印机设置页面
 */
public class PrinterManagerActivity extends Activity {
    TextView mPrinterNameTextView;
    TextView mStatusTextView;
    CheckBox mUse80WidthCheckBox;
    EditText mPrintCountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_manager);

        mPrinterNameTextView = findViewById(R.id.mPrinterNameTextView);
        mStatusTextView = findViewById(R.id.mStatusTextView);
        mUse80WidthCheckBox = findViewById(R.id.mUse80WidthCheckBox);
        mPrintCountEditText = findViewById(R.id.mPrintCountEditText);
        //初始化打印机配置信息
        PrinterConfig printerConfig = BluetoothPrintManager.getInstance().getPrinterConfig(this);
        if (printerConfig != null) {
            mUse80WidthCheckBox.setChecked(printerConfig.getPagerWidth() == 80);
            mPrintCountEditText.setText(String.valueOf(printerConfig.getPrintCount()));
        }

        refreshDeviceInfo();
    }

    /**
     * 关闭页面
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 保存配置
     *
     * @param view
     */
    public void save(View view) {
        PrinterConfig printerConfig = new PrinterConfig();
        int count = 1;
        try {
            count = Integer.parseInt(mPrintCountEditText.getText().toString());
        } catch (Exception e) {
            count = 1;
        }
        printerConfig.setPrintCount(count);
        printerConfig.setPagerWidth(mUse80WidthCheckBox.isChecked() ? 80 : 58);
        BluetoothPrintManager.getInstance().saveConfigInfo(this, printerConfig);
        finish();
    }

    //是否已经提示过返回应用信息，每次设置只提醒一次
    private boolean isToastBackInfoAlready = false;

    /**
     * 连接设备
     *
     * @param view
     */
    public void connectDevice(View view) {
        isToastBackInfoAlready = false;
        BluetoothPrintManager.getInstance()
                .setOnPrinterNotifyListener(new OnPrinterNotifyListener() {
                    @Override
                    public void onPrinterNotify(NotifyMessage notifyMessage) {
                        refreshDeviceInfo();
                        //绑定成功，则跳转到前台
                        if (NotifyMessage.BLUETOOTH_STATE_PRINTER_BIND_ALREADY == notifyMessage && !isToastBackInfoAlready) {
                            isToastBackInfoAlready = true;
                            Toast.makeText(getApplicationContext(), R.string.printer_bindSuccess2AppInfo, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .openSettingView(this);
    }

    /**
     * 刷新设备信息
     */
    private void refreshDeviceInfo() {
        mPrinterNameTextView.setText(BluetoothPrintManager.getDefaultBluetoothDeviceName(this));
        if (TextUtils.isEmpty(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(this))) {
            mStatusTextView.setText(R.string.printer_notBind);
        } else {
            mStatusTextView.setText(R.string.printer_bindAlready);
        }
    }

    /**
     * 打印测试页
     *
     * @param view
     */
    public void printTest(View view) {
        PrinterBean printerBean = new PrinterBean();
        printerBean.templateInfo = "\n欢迎使用DW蓝牙打印系统\n蓝牙打印机已成功配置";
        BluetoothPrintManager.getInstance()
                .setAutoOpenSettingActivity(false)
                .setOnPrinterNotifyListener(new OnPrinterNotifyListener() {
                    @Override
                    public void onPrinterNotify(NotifyMessage notifyMessage) {
                        mStatusTextView.setText(notifyMessage.getInfo());
                        mPrinterNameTextView.setText(BluetoothPrintManager.getDefaultBluetoothDeviceName(getApplicationContext()));
                        if (NotifyMessage.PRINT_FINISH == notifyMessage) {
                            Toast.makeText(PrinterManagerActivity.this, "打印完成", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).print(this, printerBean);
    }
}
