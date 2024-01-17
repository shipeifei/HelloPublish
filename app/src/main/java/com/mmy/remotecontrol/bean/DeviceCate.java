package com.mmy.remotecontrol.bean;

/**
 * @创建者 lucas
 * @创建时间 2018/11/16 0016 10:42
 * @描述 TODO
 */
public class DeviceCate {
    /**
     * [
     * {"id":"1","device_name:"电视/盒子"},
     * {"id":"2","device_name:"电视"},
     * {"id":"3","device_name:"电视机顶盒"},
     * {"id":"4","device_name:"空调"},
     * 。。。。。。
     * ]
     */

    String id, device_name, logo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
