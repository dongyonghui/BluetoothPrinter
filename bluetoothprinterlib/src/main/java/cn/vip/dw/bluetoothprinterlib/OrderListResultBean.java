package cn.vip.dw.bluetoothprinterlib;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：GouKu_Manage
 * 类描述：
 * 创建人：mayn
 * 创建时间：2018/7/17 13:21
 * 修改人：mayn
 * 修改时间：2018/7/17 13:21
 * 修改备注：外卖订单请求成功对象
 */
public class OrderListResultBean implements Serializable {

    public String date;
    public String shopId;
    public String name;
    public String userName;
    public String logo;
    public String phone;
    public String orderType;
    public String orderId;
    public String orderIdStr;
    public String number;
    public String payWareTotal;
    public String payReduce;
    public String payFreight;
    public String payTotal;
    public String payActual;
    public String payService;
    public String orderShippingType;
    public String count;
    public String peopleCount;
    public String createTime;
    public String payTime;
    public String status;
    public String checkStatus;
    public String remark;
    public String countDown;
    public String accountPrice;
    public AddressBean address;
    private String userPhone;
    private String userAddress;
    public GroupOrderBean groupOrder;
    public String distribution;
    public String invoice;
    public String refund;
    public String refundReasons;
    public String refundAt;
    public String projectedIncome;
    public String hastenStatu;
    public String tableNo;
    public String groupUseType;
    public String refundPrice;
    //true 标识展示2条 false 全部展示
    public boolean type = true;
    public int position;

    public List<ActOutBean> actOut;
    public List<FlowBean> flow;
    public List<ItemsBean> items;
    public List<HastenFlowsBean> hastenFlows;
    public List<RefundItemListBean> refundItemList;

    /**
     * 获取交易记录详情中订单状态
     *
     * @param context
     * @return
     */
    public String getDealHistoryDetailStatusInfo(Context context) {
        if ("4".equals(checkStatus)) {
            return "已完成";
        }
        return "待对账";
    }

    public static class GroupOrderBean implements Serializable {
        private int countComplete;
        private int countRequire;
        private long createAt;
        private String deliveryAddress;
        private String deliveryManPhone;
        private int deliveryNumber;
        private int deliveryStatus;
        private int duration;
        private int groupId;
        private String openid;
        private long orderId;
        private String orderIdStr;
        private String pickupTime;
        private String shopAddress;
        private long shopId;
        private String shopIdStr;
        private String shopName;
        private String shopPhone;
        private List<FlowsBean> flows;

        public int getCountComplete() {
            return countComplete;
        }

        public void setCountComplete(int countComplete) {
            this.countComplete = countComplete;
        }

        public int getCountRequire() {
            return countRequire;
        }

        public void setCountRequire(int countRequire) {
            this.countRequire = countRequire;
        }

        public long getCreateAt() {
            return createAt;
        }

        public void setCreateAt(long createAt) {
            this.createAt = createAt;
        }

        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        public String getDeliveryManPhone() {
            return deliveryManPhone;
        }

        public void setDeliveryManPhone(String deliveryManPhone) {
            this.deliveryManPhone = deliveryManPhone;
        }

        public int getDeliveryNumber() {
            return deliveryNumber;
        }

        public void setDeliveryNumber(int deliveryNumber) {
            this.deliveryNumber = deliveryNumber;
        }

        public int getDeliveryStatus() {
            return deliveryStatus;
        }

        public void setDeliveryStatus(int deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public String getOrderIdStr() {
            return orderIdStr;
        }

        public void setOrderIdStr(String orderIdStr) {
            this.orderIdStr = orderIdStr;
        }

        public String getPickupTime() {
            return pickupTime;
        }

        public void setPickupTime(String pickupTime) {
            this.pickupTime = pickupTime;
        }

        public String getShopAddress() {
            return shopAddress;
        }

        public void setShopAddress(String shopAddress) {
            this.shopAddress = shopAddress;
        }

        public long getShopId() {
            return shopId;
        }

        public void setShopId(long shopId) {
            this.shopId = shopId;
        }

        public String getShopIdStr() {
            return shopIdStr;
        }

        public void setShopIdStr(String shopIdStr) {
            this.shopIdStr = shopIdStr;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopPhone() {
            return shopPhone;
        }

        public void setShopPhone(String shopPhone) {
            this.shopPhone = shopPhone;
        }

        public List<FlowsBean> getFlows() {
            return flows;
        }

        public void setFlows(List<FlowsBean> flows) {
            this.flows = flows;
        }

        public static class FlowsBean implements Serializable {
            private String avatar;
            private String hourTime;
            private String time;
            private String userName;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getHourTime() {
                return hourTime;
            }

            public void setHourTime(String hourTime) {
                this.hourTime = hourTime;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }

    public static class AddressBean implements Serializable {
        public String name;
        public String phone;
        public String address;
        public String userShop;
    }

    public static class ActOutBean implements Serializable {
        /**
         * name : 在线支付立减优惠
         * price : -19.0
         */

        public String name;
        public String price;

    }

    public static class RefundItemListBean implements Serializable {
        public String foodName;
        public String refundPrice;
        public String count;
    }

    public static class HastenFlowsBean implements Serializable {
        public String time;
        public String describe;
        public String hourTime;
    }

    public static class FlowBean implements Serializable {
        /**
         * time : 2018-07-16 16:25:17
         * describe : 商家已接单
         * hourTime : 16:25
         */

        public String time;
        public String describe;
        public String hourTime;
        public String riderPhone;
        public String riderName;

    }

    public static class ItemsBean implements Serializable {
        public String id;
        public String createAt;
        public String type;
        public String orderId;
        public String skuId;
        public String skuUnitId;
        public String outSkuId;
        public String unit;
        public String count;
        public String stock;
        public String name;
        public String pictures;
        public String price;
        public String pricePreferential;
        public String standards;
        public String attrs;
        public boolean isChoosed = false;

        public String getFullName() {
            return name;
        }

        public String getPrice() {
            return "￥" + pricePreferential;
        }


        public boolean isChoosed() {
            return isChoosed;
        }


        public void setChoosed(boolean choosed) {
            isChoosed = choosed;
        }

        public String getCountInfo() {
            if (TextUtils.isEmpty(unit)) {
                return "x" + count;
            }
            return count + unit;
        }
    }
}
