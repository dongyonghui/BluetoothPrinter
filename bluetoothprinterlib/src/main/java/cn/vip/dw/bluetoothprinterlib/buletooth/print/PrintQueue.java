package cn.vip.dw.bluetoothprinterlib.buletooth.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.vip.dw.bluetoothprinterlib.BluetoothPrintManager;
import cn.vip.dw.bluetoothprinterlib.OnPrinterNotifyListener;

/**
 * Created by DongYonghui on 8/1/17.
 * <p/>
 * this is print queue.
 * you can simple add print bytes to queue. and this class will send those bytes to bluetooth device
 * 这是打印队列。
 *   你可以简单地添加打印字节来排队。 并且这个类将这些字节发送到蓝牙设备
 */
public class PrintQueue {

    /**
     * instance
     */
    private static PrintQueue mInstance;
    /**
     * context
     */
    private static Context mContext;
    /**
     * print queue
     */
    private ArrayList<byte[]> mQueue;
    private ArrayList<Integer> mQueueLineHeight;
    /**
     * bluetooth adapter
     */
    private BluetoothAdapter mAdapter;
    /**
     * bluetooth service
     */
    private BtService mBtService;


    private PrintQueue() {
    }

    public static PrintQueue getQueue(Context context) {
        if (null == mInstance) {
            mInstance = new PrintQueue();
        }
        if (null == mContext) {
            mContext = context;
        }
        return mInstance;
    }

    /**
     * add print bytes to queue. and call print
     *
     * @param bytes bytes
     */
    public synchronized void add(byte[] bytes, int totalLineHeight) {
        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
            mQueueLineHeight = new ArrayList<>();
        }
        if (null != bytes) {
            mQueue.add(bytes);
            mQueueLineHeight.add(totalLineHeight);
        }
        print();
    }

    /**
     * add print bytes to queue. and call print
     */
    public synchronized void add(ArrayList<byte[]> bytesList, int totalLineHeight) {
        if (null == mQueue) {
            mQueue = new ArrayList<byte[]>();
            mQueueLineHeight = new ArrayList<>();
        }
        if (null != bytesList) {
            mQueue.addAll(bytesList);
            for (byte[] bytes : bytesList) {
                mQueueLineHeight.add(totalLineHeight);
            }
        }
        print();
    }

    /**
     * print queue
     */
    public synchronized void print() {
        try {
            if (null == mQueue || mQueue.size() <= 0) {
                return;
            }
            if (null == mAdapter) {
                mAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (null == mBtService) {
                mBtService = new BtService(mContext);
            }
            //如果设备未连接，则检查是否有已绑定过的设备，如果有则进行连接尝试
            if (mBtService.getState() != BtService.STATE_CONNECTED) {
                if (!TextUtils.isEmpty(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext))) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext));
                    mBtService.connect(device);
                    BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.WAITING_CONNECT_DEVICE);
                    return;
                }
            }
            //通知开始打印
            BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_START);
            while (mQueue.size() > 0) {
                mBtService.write(mQueue.get(0));
                mQueue.remove(0);
                //20行等待1秒，最少等待1秒
                Thread.sleep(Math.max((long) ((mQueueLineHeight.get(0) / 20.0) * 1000), 1000));
                mQueueLineHeight.remove(0);
            }

            BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FINISH);
        } catch (Exception e) {
            BluetoothPrintManager.getInstance().sendNotify(OnPrinterNotifyListener.NotifyMessage.PRINT_FAILED_PRINT_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * disconnect remote device
     */
    public void disconnect() {
        try {
            if (null != mBtService) {
                mBtService.stop();
                mBtService = null;
            }
            if (null != mAdapter) {
                mAdapter = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * when bluetooth tabId is changed, if the printer is in use,
     * connect it,else do nothing
     */
    public void tryConnect() {
        try {
            if (TextUtils.isEmpty(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext))) {
                return;
            }
            if (null == mAdapter) {
                mAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (null == mAdapter) {
                return;
            }
            if (null == mBtService) {
                mBtService = new BtService(mContext);
            }
            if (mBtService.getState() != BtService.STATE_CONNECTED) {
                if (!TextUtils.isEmpty(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext))) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext));
                    mBtService.connect(device);
                    return;
                }
            } else {


            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    /**
     * 将打印命令发送给打印机
     *
     * @param bytes bytes
     */
    public void write(byte[] bytes) {
        try {
            if (null == bytes || bytes.length <= 0) {
                return;
            }
            if (null == mAdapter) {
                mAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (null == mBtService) {
                mBtService = new BtService(mContext);
            }
            if (mBtService.getState() != BtService.STATE_CONNECTED) {
                if (!TextUtils.isEmpty(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext))) {
                    BluetoothDevice device = mAdapter.getRemoteDevice(BluetoothPrintManager.getDefaultBluethoothDeviceAddress(mContext));
                    mBtService.connect(device);
                    return;
                }
            }
            mBtService.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
