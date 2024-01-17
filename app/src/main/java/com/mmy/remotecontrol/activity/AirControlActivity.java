package com.mmy.remotecontrol.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mmy.remotecontrol.ActivityCollector;
import com.mmy.remotecontrol.Constant;
import com.mmy.remotecontrol.R;
import com.mmy.remotecontrol.bean.BrandBean;
import com.mmy.remotecontrol.bean.DeviceBean;
import com.mmy.remotecontrol.bean.KeyBean;
import com.mmy.remotecontrol.util.ConsumerIrUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AirControlActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AirControlActivity";
    public static final int TYPE_CONTROL = 1111;
    public static final int TYPE_TEST = 2222;
    private int cur_type = TYPE_CONTROL;//默认是控制页面
    private LinearLayout bottomLayout;
    private Button last, next, ok, count;
    private TextView tempTv, modelShowTv, windSpeedShowTv, windFXShowTv, title;
    private Button switchBt, modelBt, windSpeedBt, windFxBt, tempUpBt, tempDownBt;
    int cur_index = 0;
    private RelativeLayout air_close;
    private List<BrandBean> testBeanData = new ArrayList<>();
    RequestQueue queue;
    KeyBean keyBean;
    private String control_id = "";
    private String typeId = "";
    private String logo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_control);
        queue = Volley.newRequestQueue(this);
        init();
    }

    public void back(View view) {
        finish();
    }

    private void init() {
        bottomLayout = findViewById(R.id.bottom_layout);
        last = findViewById(R.id.last);
        next = findViewById(R.id.next);
        ok = findViewById(R.id.ok);
        count = findViewById(R.id.count);
        air_close = findViewById(R.id.air_close);

        tempTv = findViewById(R.id.temp_tv);
        modelShowTv = findViewById(R.id.model_show);
        windSpeedShowTv = findViewById(R.id.wind_speed_show);
        windFXShowTv = findViewById(R.id.wind_fx_show);
        switchBt = findViewById(R.id.switch_bt);
        modelBt = findViewById(R.id.model_bt);
        windSpeedBt = findViewById(R.id.wind_speed_bt);
        windFxBt = findViewById(R.id.wind_fx_bt);
        tempUpBt = findViewById(R.id.timp_up_bt);
        tempDownBt = findViewById(R.id.timp_down_bt);
        title = findViewById(R.id.title);
        switchBt.setOnClickListener(this);
        modelBt.setOnClickListener(this);
        tempDownBt.setOnClickListener(this);
        tempUpBt.setOnClickListener(this);
        windFxBt.setOnClickListener(this);
        windSpeedBt.setOnClickListener(this);


        if (getIntent().hasExtra("show_type")) {
            cur_type = getIntent().getIntExtra("show_type", TYPE_CONTROL);
        }
        if (getIntent().hasExtra("json")) {
            String testJson = getIntent().getStringExtra("json");
            getJson(testJson);
        }
        if (getIntent().hasExtra("control_id")) {
            control_id = getIntent().getStringExtra("control_id");

            title.setText(control_id + " 空调");
        }
        if (getIntent().hasExtra("id")) {
            typeId = getIntent().getStringExtra("id");
        }
        if (getIntent().hasExtra("logo")) {
            logo = getIntent().getStringExtra("logo");
        }

        if (cur_type == TYPE_CONTROL) {
            bottomLayout.setVisibility(View.GONE);
        }

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_index > 0 && cur_index < testBeanData.size()) {
                    cur_index--;
//                    getButtonData(testBeanData.get(cur_index).getId());
                    count.setText((cur_index + 1) + "/" + testBeanData.size());
                    getKeyEvent(0, cur_index, "");
                } else {
                    Toast.makeText(AirControlActivity.this, "已经是第一个了", Toast.LENGTH_SHORT).show();
                }
                title.setText(testBeanData.get(cur_index).getId()+" 空调");
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_index < testBeanData.size() - 1) {
                    cur_index++;
                    count.setText((cur_index + 1) + "/" + testBeanData.size());
                    getKeyEvent(0, cur_index, "");
                } else {
                    Toast.makeText(AirControlActivity.this, "已经是最后一个了", Toast.LENGTH_SHORT).show();
                }
                title.setText(testBeanData.get(cur_index).getId()+" 空调");
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog();
            }
        });

    }


    private void renameDialog() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setMessage("重命名")//setTitle()
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "名称不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(input);
                            deviceBean.setDeviceType(typeId);
                            deviceBean.setLogo(logo);
                            deviceBean.setDeviceId(testBeanData.get(cur_index).getId());
                            deviceBean.setBrand(testBeanData.get(cur_index).getBrand());
                            deviceBean.save();
                            ActivityCollector.removeAll();
                            sendBroadcast(new Intent("add_device"));
                            finish();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    AlertDialog dialog;

    private void loadingDialog() {
        if(dialog !=null && dialog.isShowing()){
            dialog.dismiss();
        }
        final ProgressBar et = new ProgressBar(this);
        et.setPadding(0, 50, 0, 50);
        dialog = new AlertDialog.Builder(this)
                .setView(et).show();
    }

    private void getJson(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.optJSONObject(i);
                BrandBean brandBean = new BrandBean();
                brandBean.setId(object.optString("id"));
                title.setText(object.optString("id") + " 空调");
                brandBean.setBrand(object.optString("bn"));
                testBeanData.add(brandBean);
            }
            //获取第一个型号的按键
//            getButtonData(testBeanData.get(0).getId());

            count.setText((0 + 1) + "/" + testBeanData.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String irdata;

    private void getKeyEvent(int key, int position, String control_id) {
        loadingDialog();
        String id = TextUtils.isEmpty(control_id) ? testBeanData.get(position).getId() : control_id;
        String url = Constant.getBaseUrl(this) + "keyevent.asp?mac=afc1d387b4ab661d&keyid="
                + key + "&kfid=" + id;
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    switchBt.setText(object.optString("conoff"));
                    modelShowTv.setText("模式：" + object.optString("cmode"));
                    tempTv.setText(object.optString("ctemp"));
                    windSpeedShowTv.setText("风速：" + object.optString("cwind"));
                    windFXShowTv.setText("风向：" + object.optString("cwinddir"));
                    irdata = object.optString("irdata");
                    Log.e(TAG, "onResponse: irdata===" + irdata);
                    if (switchBt.getText().toString().equals("关")) {
                        air_close.setVisibility(View.VISIBLE);
                    } else {
                        air_close.setVisibility(View.GONE);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(irdata)) {
                                Toast.makeText(AirControlActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                            } else {
                                ConsumerIrUtil.getInstance(AirControlActivity.this).sendData(irdata);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(0, 0, control_id);
                } else {
                    getKeyEvent(0, cur_index, "");
                }
                break;
            case R.id.model_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(1, 0, control_id);
                } else {
                    getKeyEvent(1, cur_index, "");
                }
                break;
            case R.id.timp_up_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(2, 0, control_id);
                } else {
                    getKeyEvent(2, cur_index, "");
                }
                break;
            case R.id.timp_down_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(3, 0, control_id);
                } else {
                    getKeyEvent(3, cur_index, "");
                }
                break;
            case R.id.wind_fx_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(5, 0, control_id);
                } else {
                    getKeyEvent(5, cur_index, "");
                }
                break;
            case R.id.wind_speed_bt:
                if (cur_type == TYPE_CONTROL) {
                    getKeyEvent(4, 0, control_id);
                } else {
                    getKeyEvent(4, cur_index, "");
                }
                break;
        }
    }


}
