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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
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

public class ButtonActivity extends BaseActivity {
    private static final String TAG = "ButtonActivity";
    public static final int TYPE_CONTROL = 1111;
    public static final int TYPE_TEST = 2222;
    private int cur_type = TYPE_CONTROL;//默认是控制页面
    private LinearLayout buttonLayout, bottomLayout;
    private Button last, next, ok, count;
    private ScrollView scrollView;
    TextView title;
    int cur_index = 0;
    private List<BrandBean> testBeanData = new ArrayList<>();
    private String typeId = "";
    private String logo = "";
    KeyBean keyBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        init();
    }

    public void back(View view) {
        finish();
    }

    private void init() {
        buttonLayout = findViewById(R.id.button_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        scrollView = findViewById(R.id.scrollView);
        last = findViewById(R.id.last);
        next = findViewById(R.id.next);
        title = findViewById(R.id.title);
        ok = findViewById(R.id.ok);
        count = findViewById(R.id.count);
        if (getIntent().hasExtra("show_type")) {
            cur_type = getIntent().getIntExtra("show_type", TYPE_CONTROL);
        }
        if (getIntent().hasExtra("id")) {
            typeId = getIntent().getStringExtra("id");
        }
        if (getIntent().hasExtra("logo")) {
            logo = getIntent().getStringExtra("logo");
        }
        if (getIntent().hasExtra("json")) {
            String testJson = getIntent().getStringExtra("json");
            getJson(testJson);
        }
        if (getIntent().hasExtra("control_id")) {
            String control_id = getIntent().getStringExtra("control_id");
            title.setText(control_id + " 设备详情");
            getButtonData(control_id);
        }

        if (cur_type == TYPE_CONTROL) {
            bottomLayout.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.title_layout);
            scrollView.setLayoutParams(layoutParams);
        }

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_index > 0 && cur_index < testBeanData.size()) {
                    cur_index--;
                    getButtonData(testBeanData.get(cur_index).getId());
                    count.setText((cur_index + 1) + "/" + testBeanData.size());
                    getKeyEvent(1, cur_index, "");
                } else {
                    Toast.makeText(ButtonActivity.this, "已经是第一个了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_index < testBeanData.size() - 1) {
                    cur_index++;
                    getButtonData(testBeanData.get(cur_index).getId());
                    count.setText((cur_index + 1) + "/" + testBeanData.size());
                    getKeyEvent(1, cur_index, "");
                } else {
                    Toast.makeText(ButtonActivity.this, "已经是最后一个了", Toast.LENGTH_SHORT).show();
                }
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
                            deviceBean.setDeviceId(keyBean.getKfid());
                            deviceBean.setDeviceType(typeId);
                            deviceBean.setLogo(logo);
                            deviceBean.setBrand(keyBean.getBrand());
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
                title.setText(object.optString("id") + " 设备详情");
                brandBean.setBrand(object.optString("bn"));
                testBeanData.add(brandBean);
            }
            //获取第一个型号的按键
            getButtonData(testBeanData.get(0).getId());

            count.setText((0 + 1) + "/" + testBeanData.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getButtonData(String brandId) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getkeylist?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&kfid=" + brandId;
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("LY===按键json", response);
                try {
                    Gson gson = new Gson();
                    keyBean = gson.fromJson(response, KeyBean.class);
                    if (keyBean != null) {
                        title.setText(keyBean.getKfid() + " 设备详情");
                        makeButton();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        keyBean = new KeyBean();
                        keyBean.setKfid(jsonObject.optString("kfid"));
                        title.setText(keyBean.getKfid() + " 设备详情");
                        keyBean.setBrand(jsonObject.optString("brand"));
                        JSONArray jsonArray = jsonObject.optJSONArray("keylist");
                        ArrayList<String> keylist = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            keylist.add(jsonArray.optString(i));
                        }
                        keyBean.setKeylist(keylist);
                        makeButton();
                        JSONArray jsonArray1 = jsonObject.optJSONArray("keyvalue");
                        ArrayList<String> keyValue = new ArrayList<>();
                        if (null != jsonArray1) {
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                keyValue.add(jsonArray.optString(i));
                            }
                        }
                        keyBean.setKeyvalue(keyValue);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("LY===按键json", "网络异常");

            }
        }));
    }

    private void makeButton() {
        buttonLayout.removeAllViews();
        for (int i = 0; i < keyBean.getKeylist().size(); i++) {
            if (i % 4 == 0) {
                LinearLayout linearLayout = new LinearLayout(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(lp);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                buttonLayout.addView(linearLayout);
                Button button = new Button(this);
                button.setBackgroundResource(R.drawable.bg_shape);
                button.setText(keyBean.getKeylist().get(i));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                button.setLayoutParams(layoutParams);
                linearLayout.addView(button);
                final String value = keyBean.getKeyvalue() == null ? "" : keyBean.getKeyvalue().get(i);
                final int finalI = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showDataDialog(value);
                        getKeyEvent(finalI + 1, finalI, keyBean.getKfid());
                    }
                });
            } else {
                LinearLayout linearLayout = (LinearLayout) buttonLayout.getChildAt(buttonLayout.getChildCount() - 1);
                Button button = new Button(this);
                button.setText(keyBean.getKeylist().get(i));
                button.setBackgroundResource(R.drawable.bg_shape);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(10, 10, 10, 10);
                button.setLayoutParams(layoutParams);
                linearLayout.addView(button);
                final String value = keyBean.getKeyvalue() == null ? "" : keyBean.getKeyvalue().get(i);
                final int finalI = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        showDataDialog(value);
                        getKeyEvent(finalI + 1, finalI, keyBean.getKfid());
                    }
                });
            }
        }
    }

    private void showDataDialog(String data) {

        Log.e(TAG, "showDataDialog: data====" + data);
        if (TextUtils.isEmpty(data)) {
            Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
        } else {
            ConsumerIrUtil.getInstance(this).sendData(data);
        }

//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setMessage(data)
//                .create();
//        dialog.show();

    }

    String irdata;

    private void getKeyEvent(int key, int position, String control_id) {
        loadingDialog();
        String id = TextUtils.isEmpty(control_id) ? testBeanData.get(position).getId() : control_id;
        String url = Constant.getBaseUrl(this) + "keyevent?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&keyid="
                + key + "&kfid=" + id;
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                final String data = response;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                            JSONObject object = new JSONObject(data);
                            irdata = object.optString("irdata");
                            Log.e(TAG, "onResponse: irdata===" + irdata);
                            if (TextUtils.isEmpty(irdata)) {
                                Toast.makeText(ButtonActivity.this, "数据为空", Toast.LENGTH_SHORT).show();
                            } else {
                                ConsumerIrUtil.getInstance(ButtonActivity.this).sendData(irdata);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }));
    }

}
