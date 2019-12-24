package cn.vip.dw.bluetoothprinterlib;

/**
 * 作者：DongYonghui
 * 日期：2019/10/30/030
 * 打印机状态通知监听
 */
public interface OnPrinterNotifyListener {

    enum NotifyMessage {
        PRINT_START(0x1000, "开始打印")
        , PRINT_FINISH(0x1010, "打印结束")
        , WAITING_CONNECT_DEVICE(0x1020, "正在连接打印机")
        , BLUETOOTH_STATE_CHANGED_WAITING2CONNECT(0x1030, "蓝牙状态更新：等待连接")
        , BLUETOOTH_STATE_CHANGED_CONNECTING(0x1031, "蓝牙状态更新：正在连接")
        , BLUETOOTH_STATE_CHANGED_CONNECTED(0x1032, "蓝牙状态更新：连接成功")
        , BLUETOOTH_STATE_CHANGED_NONE(0x1033, "蓝牙状态更新：连接已断开")
        , BLUETOOTH_STATE_OPENED(0x1034, "蓝牙状态更新：蓝牙已打开")
        , BLUETOOTH_STATE_CLOSED(0x1035, "蓝牙状态更新：蓝牙已关闭")
        , BLUETOOTH_STATE_PRINTER_BIND_ALREADY(0x1035, "蓝牙状态更新：设备已绑定")
        //0x1100-0x1200定义为异常消息区间
        , PRINT_FAILED_BLE_NOT_OPEN(0x1100, "打印失败，蓝牙未打开。请打开蓝牙。")
        , PRINT_FAILED_DEVICE_NOT_BIND(0x1101, "打印失败，没有绑定的打印机。请到蓝牙设置中找到可用的蓝牙打印机并配对。")
        , PRINT_FAILED_PARAMS_ERROR(0x1102, "打印失败，传参异常，请确保程序传参的必要数据。")
        , PRINT_FAILED_PRINT_ERROR(0x1103, "打印失败，打印异常，请确保打印机在正常状态下后再次重试。")
        ;
        private int code;
        private String info;

        NotifyMessage(int code, String info) {
            this.code = code;
            this.info = info;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    void onPrinterNotify(NotifyMessage notifyMessage);
}
