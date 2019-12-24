package com.dyh.bluetoothprinter;

import java.util.List;

/**
 * 作者：DongYonghui
 * 日期：2019/10/30/030
 * BD订单小票模板头部Bean
 */
public class OrderPrinterBean {
    private String orderNumber;// <string>,       // 取餐码
    private String userName;// <string>,       // 用户名
    private String userPhone;// <string>,      // 用户手机
    private String userAddress;// <string>,    // 楼宇
    private String remark;// <string>,//楼层门牌号
    private List<SkuItemBean> skuList;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<SkuItemBean> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SkuItemBean> skuList) {
        this.skuList = skuList;
    }

    public static class SkuItemBean {
        private String skuName;// <string>, // 菜名
        private String skuCount;// <int>     // 数量

        public String getSkuName() {
            return skuName;
        }

        public void setSkuName(String skuName) {
            this.skuName = skuName;
        }

        public String getSkuCount() {
            return skuCount;
        }

        public void setSkuCount(String skuCount) {
            this.skuCount = skuCount;
        }
    }
}
