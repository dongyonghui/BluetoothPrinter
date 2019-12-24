package cn.vip.dw.bluetoothprinterlib.buletooth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import cn.vip.dw.bluetoothprinterlib.BluetoothPrintManager;
import cn.vip.dw.bluetoothprinterlib.OnPrinterNotifyListener;
import cn.vip.dw.bluetoothprinterlib.buletooth.print.PrintQueue;

/**
 * Created by DongYonghui on 8/1/17.
 * <p/>
 * print ticket service
 */
public class MyPrinterService extends IntentService {

    public static final String ACTION_PRINT_DEFAULT = "action_print_default";//打印

    private int mNum;

    public MyPrinterService() {
        super("MyPrinterService");
    }


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyPrinterService(String name) {
        super(name);
    }

    private final String CHANNEL_ONE_ID = "DWPrinter";
    private final String CHANNEL_ONE_NAME = "打印服务";

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // 【适配Android8.0】设置Notification的Channel_ID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }

        // 额外添加：
        // 【适配Android8.0】给NotificationManager对象设置NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }


        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(1, notification);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String countNum = intent.getStringExtra("countNum");
        if (!TextUtils.isEmpty(countNum)) {
            mNum = Integer.parseInt(countNum);
        }
        byte[] printData = intent.getByteArrayExtra("data");
        //单次打印的总行高
        int totalLineHeight = intent.getIntExtra("totalLineHeight", 10);
        print(printData, totalLineHeight);
    }

    private void print(byte[] byteArrayExtra, int totalLineHeight) {
        if (null == byteArrayExtra || byteArrayExtra.length <= 0) {
            BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FINISH);
            return;
        }
        PrintQueue.getQueue(getApplicationContext()).add(byteArrayExtra, totalLineHeight);
    }
}
