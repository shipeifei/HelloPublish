package com.mmy.remotecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mmy.remotecontrol.activity.BaseActivity;
import com.mmy.remotecontrol.fragment.MainFragment;
import com.mmy.remotecontrol.fragment.SettingFragment;
import com.mmy.remotecontrol.util.ConsumerIrUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    Button homeBt, settingBt,sceneBt;
    ViewPager pager;
    List<Fragment> fragments = new ArrayList<>();
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initEvent();
    }


    private void init() {
        homeBt = findViewById(R.id.home);
        settingBt = findViewById(R.id.setting);
        sceneBt = findViewById(R.id.scene);
        pager = findViewById(R.id.viewpager);
        fragments.add(MainFragment.newInstance());
        fragments.add(SettingFragment.newInstance());
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        pager.setCurrentItem(0);
        homeBt.setTextColor(getResources().getColor(R.color.main_black));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("add_device")) {
                    ((MainFragment) fragments.get(0)).reFresh();
                }
            }
        };
        IntentFilter filter = new IntentFilter("add_device");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initEvent() {
        homeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBt.setTextColor(getResources().getColor(R.color.main_black));
                settingBt.setTextColor(getResources().getColor(R.color.line_color));
                pager.setCurrentItem(0);
            }
        });
        settingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBt.setTextColor(getResources().getColor(R.color.line_color));
                settingBt.setTextColor(getResources().getColor(R.color.main_black));
                pager.setCurrentItem(1);
            }
        });
        sceneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String data = "5B,F3,F6,84,C5,51,17,23,60,FA,E6,BE,86,CD,0E,A4,E6,A8,18,21,77,43,10,DC,32,7C,1A,B1,D4,07,5D,66,7E,BD,AD,D5,F4,7C,03,D2,A2,66,0D,53,A2,38,DF,4A,64,83,3B,07,D6,01,77,18,23,7B,C2,D4,90,AC";
//                ConsumerIrUtil.getInstance(MainActivity.this).sendData(data);

            }
        });
    }


}
