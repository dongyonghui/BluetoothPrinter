package cn.vip.dw.bluetoothprinterlib;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import cn.vip.dw.bluetoothprinterlib.buletooth.BtUtil;
import cn.vip.dw.bluetoothprinterlib.buletooth.MyPrinterService;
import cn.vip.dw.bluetoothprinterlib.constants.TempleteConstants;
import cn.vip.dw.bluetoothprinterlib.parser.EscCommand;
import cn.vip.dw.bluetoothprinterlib.parser.FormatTempleteParser;
import cn.vip.dw.bluetoothprinterlib.ui.PrinterDialogHelper;
import cn.vip.dw.bluetoothprinterlib.velocity.PrinterBean;
import cn.vip.dw.bluetoothprinterlib.velocity.RowTool;
import cn.vip.dw.bluetoothprinterlib.velocity.VelocityContentRender;

/**
 * User: DongYonghui(648731994@qq.com)
 * Date: 2015-12-26
 * Time: 15:05
 * 对外开放的蓝牙打印机管理类
 */
public class BluetoothPrintManager {
    public static int currentPaperWidth = 58;//当前纸张宽度
    public static final byte CUT_PAPER_AND_FEED = 20;
    private final String TAG_LOG = "BluetoothPrintManager";
    public static final int PRINT_TYPE = 1664;
    private static final String FILENAME = "bt";
    private static final String DEFAULT_BLUETOOTH_DEVICE_ADDRESS = "default_bluetooth_device_address";//蓝牙设备地址
    private static final String DEFAULT_BLUETOOTH_DEVICE_NAME = "default_bluetooth_device_name";//蓝牙设备名称
    private static final String PRINTER_CONFIG = "printer_config";//打印机配置信息

    private final VelocityContentRender render = new VelocityContentRender();
    private boolean isNeedShowPrintingDialog = true;//是否需要在打印过程中弹框处理
    private Activity mActivity;

    //默认的事件处理监听
    private final OnPrinterNotifyListener mDefaultNotifyListener = new OnPrinterNotifyListener() {
        @Override
        public void onPrinterNotify(final NotifyMessage notifyMessage) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                handMessageOnUIThread(notifyMessage);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handMessageOnUIThread(notifyMessage);
                    }
                });
            }
        }

        private void handMessageOnUIThread(final NotifyMessage notifyMessage) {
            //弹框处理
            if (isNeedShowPrintingDialog && null != mActivity) {
                //打印失败则隐藏对话框
                if (notifyMessage.getCode() >= 0x1100 && notifyMessage.getCode() < 0x1200) {
                    PrinterDialogHelper.showDialog(mActivity, mActivity.getString(R.string.printer_printFailed), notifyMessage.getInfo());
                } else {//打印开始弹框提示，结束关闭提示
                    switch (notifyMessage) {
                        case PRINT_START:
                            PrinterDialogHelper.showDialog(mActivity, mActivity.getString(R.string.printer_printing), notifyMessage.getInfo());
                            break;
                        case PRINT_FINISH:
                            PrinterDialogHelper.hideDialog();
                            break;
                    }
                }

            }

            if (null != onPrinterNotifyListener) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    onPrinterNotifyListener.onPrinterNotify(notifyMessage);
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPrinterNotifyListener.onPrinterNotify(notifyMessage);
                        }
                    });
                }
            }
        }
    };//打印监听器
    private OnPrinterNotifyListener onPrinterNotifyListener;//打印监听器
    private boolean isAutoOpenSettingActivity = true;//碰到需要打开设置页面时是否自动打开

    private static BluetoothPrintManager instance;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private BluetoothPrintManager() {
    }

    public static BluetoothPrintManager getInstance() {
        synchronized (BluetoothPrintManager.class) {
            if (null == instance) {
                instance = new BluetoothPrintManager();
            }
        }
        return instance;
    }

    public boolean isAutoOpenSettingActivity() {
        return isAutoOpenSettingActivity;
    }

    public BluetoothPrintManager setAutoOpenSettingActivity(boolean autoOpenSettingActivity) {
        isAutoOpenSettingActivity = autoOpenSettingActivity;
        return instance;
    }

    public OnPrinterNotifyListener getOnPrinterNotifyListener() {
        return onPrinterNotifyListener;
    }

    /**
     * 设置通知回调监听器
     *
     * @param onPrinterNotifyListener 需要监听的监听器对象
     * @return
     */
    public BluetoothPrintManager setOnPrinterNotifyListener(OnPrinterNotifyListener onPrinterNotifyListener) {
        this.onPrinterNotifyListener = onPrinterNotifyListener;
        return instance;
    }

    /**
     * 打印消息
     *
     * @param context
     * @param printerBean 获取方法见demo和 getPrinterBean()方法
     * @return
     */
    public BluetoothPrintManager print(final Context context, final PrinterBean printerBean) {
        if (null == printerBean || null == context) {
            sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FAILED_PARAMS_ERROR);
            return instance;
        }
        //检查是否打开蓝牙
        if (!BtUtil.isOpen()) {
            Log.d(TAG_LOG, "蓝牙功能未开启,请打开并连接蓝牙打印机");
            sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FAILED_BLE_NOT_OPEN);
            toSettingView(context);
            return instance;
        }

        //检查是否有绑定打印机
        if (!isBondedPrinter(context)) {
            Log.d(TAG_LOG, "未找到绑定的打印机，手动连接打印机");
            sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FAILED_DEVICE_NOT_BIND);
            clearCacheBoundPrinterInfo(context);
            toSettingView(context);
            return instance;
        }


        //生成模板数据
        HashMap<String, Object> contentMap = new HashMap<>();
        contentMap.put(printerBean.templateBeanKey, printerBean.templateBean);
        contentMap.put("row", new RowTool());
        contentMap.put("number", new MyNumberTool());
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        String printInfo = render.render(printerBean.templateInfo, contentMap);
        printInfo = printInfo.replace("¤", "￥");
        Locale.setDefault(defaultLocal);

        //生成ESC命令
        EscCommand esc = getEscCommand(printInfo, printerBean.printCount, true);
        Vector<Byte> command = esc.getCommand();
        int length = esc.getCommand().size();
        byte[] commend = new byte[length];
        for (int i = 0; i < length; i++) {
            commend[i] = command.get(i);
        }

        //开启打印服务
        Intent intent = new Intent(context, MyPrinterService.class);
        intent.setAction(MyPrinterService.ACTION_PRINT_DEFAULT);
        Bundle bundle = new Bundle();
        bundle.putByteArray("data", commend);
        bundle.putInt("totalLineHeight", getLineHeight(printInfo, printerBean.printCount, true));
        intent.putExtras(bundle);
        //如果针对 Android 8.0 的应用尝试在不允许其创建后台服务的情况下使用 startService() 函数，
        // 则该函数将引发一个 IllegalStateException。
        // 新的 Context.startForegroundService() 函数将启动一个前台服务。现在，即使应用在后台运行，
        // 系统也允许其调用 Context.startForegroundService()。不过，应用必须在创建服务后的五秒内调用该服务的
        // startForeground() 函数。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        return instance;
    }


    /**
     * 发送通知给使用者
     *
     * @param msg
     */
    public void sendNotify(final OnPrinterNotifyListener.NotifyMessage msg) {
        mDefaultNotifyListener.onPrinterNotify(msg);
    }

    /**
     * 生成小票信息数据
     *
     * @param context               上下文
     * @param assesTemplateFileName assess文件夹中小票模板文件名
     * @param templateRootKeyName   小票模板中自定义数据key
     * @param data                  展示的数据
     * @return
     */
    public PrinterBean getPrinterBean(Context context, String assesTemplateFileName, String templateRootKeyName, Object data) {
        return getPrinterBean(context, assesTemplateFileName, templateRootKeyName, data, 1);
    }

    /**
     * 生成小票信息数据
     *
     * @param context               上下文
     * @param assesTemplateFileName assess文件夹中小票模板文件名
     * @param templateRootKeyName   小票模板中自定义数据key
     * @param data                  展示的数据
     * @param count                 打印数量
     * @return
     */
    public PrinterBean getPrinterBean(Context context, String assesTemplateFileName, String templateRootKeyName, Object data, int count) {
        PrinterBean printerBean = null;
        if (null != data) {
            printerBean = new PrinterBean();
            printerBean.templateInfo = TxtReader.getStringFromAssetsByFullPath(context, assesTemplateFileName);
            printerBean.printCount = count;
            printerBean.templateBeanKey = templateRootKeyName;
            printerBean.templateBean = data;
        }
        return printerBean;
    }

    private void toSettingView(Context context) {
        if (!isAutoOpenSettingActivity) {
            return;
        }
        openSettingView(context);
    }

    /**
     * 打开设置界面
     *
     * @param context
     */
    public void openSettingView(Context context) {
        context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));//直接进入手机中的蓝牙设置界面
    }

    public static void setDefaultBluetoothDeviceAddress(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, value);
        editor.apply();
    }

    public static void setDefaultBluetoothDeviceName(Context mContext, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_BLUETOOTH_DEVICE_NAME, value);
        editor.apply();
    }

    public static String getDefaultBluethoothDeviceAddress(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_ADDRESS, null);
    }


    //绑定设备的蓝牙名称
    public static String getDefaultBluetoothDeviceName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEFAULT_BLUETOOTH_DEVICE_NAME, "无设备");
    }

    /**
     * 重置打印机绑定状态<BR>
     * 先清空缓存的打印机信息，再查找已配对的蓝牙设备中是否有打印机，如果有，则进行缓存打印机信息
     *
     * @param context
     */
    public void resetCacheBoundPrinterInfo(Context context) {
        //初始化蓝牙
        clearCacheBoundPrinterInfo(context);
        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter()
                .getBondedDevices();
        for (BluetoothDevice bondedDevice : bondedDevices) {
            if (null != bondedDevice.getBluetoothClass() && bondedDevice.getBluetoothClass().getDeviceClass() == PRINT_TYPE) {
                //通知已绑定成功
                BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.BLUETOOTH_STATE_PRINTER_BIND_ALREADY);
                setDefaultBluetoothDeviceAddress(context, bondedDevice.getAddress());
                setDefaultBluetoothDeviceName(context, bondedDevice.getName());
            }
        }
    }

    /**
     * 获取打印机配置信息
     *
     * @param context
     * @return
     */
    public PrinterConfig getPrinterConfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(PRINTER_CONFIG, null);
        PrinterConfig printerConfig = new Gson().fromJson(json, PrinterConfig.class);
        if (printerConfig == null) {
            printerConfig = new PrinterConfig();
        }
        return printerConfig;
    }

    /**
     * 保存打印机配置信息
     *
     * @param context
     * @param printerConfig
     * @return
     */
    public BluetoothPrintManager saveConfigInfo(Context context, PrinterConfig printerConfig) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        currentPaperWidth = printerConfig.getPagerWidth();
        editor.putString(PRINTER_CONFIG, new Gson().toJson(printerConfig));
        editor.apply();
        return instance;
    }

    /**
     * 清空缓存的已绑定的打印机信息
     *
     * @param context
     */
    public BluetoothPrintManager clearCacheBoundPrinterInfo(Context context) {
        setDefaultBluetoothDeviceAddress(context, null);
        setDefaultBluetoothDeviceName(context, null);
        return instance;
    }


    /**
     * 检查是否已经绑定打印机<BR>
     * 本地缓存的已经绑定过的设备名称和地址与当前手机绑定设备列表遍历对比
     *
     * @param mContext
     * @return true 表示绑定了打印机 否则表示没有绑定打印机
     */
    //是否绑定了打印机设备
    public boolean isBondedPrinter(Context mContext) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BtUtil.isOpen()) {
            return false;
        }

        //先查看是否绑定过，如果没有绑定过则返回false
        String defaultBluetoothDeviceAddress = getDefaultBluethoothDeviceAddress(mContext);
        if (TextUtils.isEmpty(defaultBluetoothDeviceAddress)) {
            return false;
        }

        //如果手机已绑定的设备列表为空，即都取消配对蓝牙设备了，则认为没有绑定打印机
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        if (deviceSet == null || deviceSet.isEmpty()) {
            return false;
        }

        //如果手机找到已配对的设备列表，则一一对比，
        // 能找到缓存中的打印机信息与已配对的设备相对应，则认为已绑定成功，否则认为绑定失败
        for (BluetoothDevice bluetoothDevice : deviceSet) {
            if (bluetoothDevice.getAddress().equals(defaultBluetoothDeviceAddress)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将文本信息转换为ESC命令
     *
     * @param printInfo
     * @param printCount
     * @param isNeedCutPaper
     * @return
     */
    private EscCommand getEscCommand(final String printInfo, final int printCount, boolean isNeedCutPaper) {
        EscCommand esc = new EscCommand();
//        设置语言环境
//            Vector<Byte> vector = new Vector<>();
//            byte[] command = new byte[]{0x1B, 0x23, 0x23, 0x53, 0x4C, 0x41, 0x4E, 0x0f};
//            for (int i = 0; i < command.length; i++) {
//                vector.add(command[i]);
//            }
//            esc.getCommand().addAll(vector);

        //清空回车符
        for (int i = 0; i < printCount; i++) {
            FormatTempleteParser.addEscCommand(printInfo, esc);

            byte feedLine = 2;
            esc.addPrintAndFeedLines(feedLine);
            if (isNeedCutPaper) {
                esc.addCutPaperAndFeed(CUT_PAPER_AND_FEED);
            } else {
                esc.addCutPaper();
                esc.addPrintAndFeedLines(feedLine);
            }
        }
        return esc;
    }

    /**
     * 获取总行高（二倍高当做两行）
     *
     * @param printInfo
     * @param printCount
     * @param isNeedCutPaper
     * @return
     */
    private int getLineHeight(final String printInfo, final int printCount, boolean isNeedCutPaper) {
        int totalLineHeight = 0;

        //清空回车符
        String tPrintInfo = printInfo.replace("\r", "").replace(" ", "  ");
        for (int i = 0; i < printCount; i++) {
            String[] rows = tPrintInfo.split("\n");
            for (String row : rows) {
                if (TextUtils.isEmpty(row)) {
                    continue;
                }

                if (row.contains(TempleteConstants.FLAG_QRCODE_START)) {//二维码 8行
                    totalLineHeight += 8;
                } else if (row.contains(TempleteConstants.FLAG_CODE)) {//条形码 4行
                    totalLineHeight += 4;
                } else if (row.contains(TempleteConstants.FLAG_BIGGER_CENTER_START)
                        || row.contains(TempleteConstants.FLAG_BIGGER_START)
                        || row.contains(TempleteConstants.FLAG_HEIGHTER_START)) {//两倍高 2行
                    totalLineHeight += 2;
                } else {
                    totalLineHeight += 1;
                }
            }
            totalLineHeight += 2;
            if (isNeedCutPaper) {
                totalLineHeight += 20;
            } else {
                totalLineHeight += 12;
            }
        }
        return totalLineHeight;
    }

    public boolean isNeedShowPrintingDialog() {
        return isNeedShowPrintingDialog;
    }

    /**
     * 是否需要弹框提示正在打印
     *
     * @param activity
     * @param needShowPrintingDialog
     * @return
     */
    public BluetoothPrintManager setNeedShowPrintingDialog(Activity activity, boolean needShowPrintingDialog) {
        this.mActivity = activity;
        this.isNeedShowPrintingDialog = needShowPrintingDialog;
        return instance;
    }
}
