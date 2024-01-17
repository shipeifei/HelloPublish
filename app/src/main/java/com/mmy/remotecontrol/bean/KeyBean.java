package com.mmy.remotecontrol.bean;

import java.util.List;

public class KeyBean {
    String kfid;
    String brand;
    List<String> keylist;
    List<String> keyvalue;

    public String getKfid() {
        return kfid;
    }

    public void setKfid(String kfid) {
        this.kfid = kfid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getKeylist() {
        return keylist;
    }

    public void setKeylist(List<String> keylist) {
        this.keylist = keylist;
    }

    public List<String> getKeyvalue() {
        return keyvalue;
    }

    public void setKeyvalue(List<String> keyvalue) {
        this.keyvalue = keyvalue;
    }
}
