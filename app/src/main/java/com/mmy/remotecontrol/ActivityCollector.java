package com.mmy.remotecontrol;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addAct(Activity activity) {
        activities.add(activity);
    }

    public static void removeAll() {
        for (Activity activity : activities) {
            if (!activity.getLocalClassName().contains("MainActivity")){
                activity.finish();
            }
        }
    }
}
