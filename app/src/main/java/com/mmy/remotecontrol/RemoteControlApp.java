package com.mmy.remotecontrol;

import com.mmy.remotecontrol.util.ConsumerIrUtil;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

public class RemoteControlApp extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Connector.getDatabase();
        ConsumerIrUtil.getInstance(this).init();
    }
}
