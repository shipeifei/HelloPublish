package com.mmy.remotecontrol.util;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.util.Log;
import android.widget.Toast;

public class ConsumerIrUtil {
    private static ConsumerIrUtil instance;
    private static Context context;
    private ConsumerIrManager manager;

    public static ConsumerIrUtil getInstance(Context ctx) {
        if (instance == null) instance = new ConsumerIrUtil();
        context = ctx;
        return instance;
    }

    public void init() {
        if (manager == null) {
            manager = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
        }
    }

    /**
     * 发送红外数据
     *
     * @param data
     */
    public void sendData(String data) {
        if (manager == null) {
            init();
        }

        try {
            if (!manager.hasIrEmitter()) {
                Toast.makeText(context, "您的设备没有红外线发射器", Toast.LENGTH_SHORT).show();
                return;
            }
            String irData[] = data.split(",");
            int carrierFrequency = Integer.parseInt(irData[0]);
//            int[] carrierArea = getRange();
//            if (carrierFrequency > carrierArea[0] || carrierFrequency < carrierArea[1]) {
//                Toast.makeText(context, "载波频率不在设备支持范围内", Toast.LENGTH_SHORT).show();
//                return;
//            }

            int[] bytes = new int[irData.length - 1];
            for (int i = 1; i < irData.length; i++) {
                bytes[i - 1] = Integer.parseInt(irData[i]);
            }

            manager.transmit(carrierFrequency, bytes);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(context, "参数有误", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "参数有误", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "错误", Toast.LENGTH_SHORT).show();
        }
    }


    private int[] getRange() {
        int d[] = new int[2];
        if (manager == null) {
            init();
        }
        ConsumerIrManager.CarrierFrequencyRange[] ranges = manager.getCarrierFrequencies();
        if (ranges.length > 0) {
            int max = ranges[0].getMaxFrequency();
            int min = ranges[0].getMinFrequency();
            Log.e("getCarrierFrequencies", "getRange: max===" + max + ", min===" + min);
            d[0] = max;
            d[1] = min;
        }
        return d;

    }
}
