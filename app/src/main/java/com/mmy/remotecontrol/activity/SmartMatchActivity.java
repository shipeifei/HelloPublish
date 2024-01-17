package com.mmy.remotecontrol.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.mmy.remotecontrol.bean.DeviceBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SmartMatchActivity extends BaseActivity {

    private EditText input_et;
    private Button start_match;
    private RecyclerView result_list;
    private TextView close, tips;
    private RelativeLayout result_layout;
    private List<DeviceBean> mathResultData = new ArrayList<>();
    private MatchAdapter adapter;
    private String typeId;
    private String logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_match);
        init();
    }

    public void back(View view) {
        finish();
    }

    private void init() {
        if (getIntent().hasExtra("id"))
            typeId = getIntent().getStringExtra("id");
        if (getIntent().hasExtra("logo"))
            logo = getIntent().getStringExtra("logo");
        input_et = findViewById(R.id.input_et);
        start_match = findViewById(R.id.start_match);
        close = findViewById(R.id.close);
        tips = findViewById(R.id.tips);
        result_list = findViewById(R.id.match_result_list);
        result_layout = findViewById(R.id.result_layout);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result_layout.getVisibility() == View.VISIBLE) {
                    result_layout.setVisibility(View.GONE);
                }
                if (input_et.getVisibility() != View.VISIBLE) {
                    input_et.setVisibility(View.VISIBLE);
                }
                tips.setText("匹配数据：");
            }
        });

        start_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = input_et.getText().toString().trim();
                if (!code.isEmpty()) {
                    match(code);
                } else {
                    Toast.makeText(SmartMatchActivity.this, "请输入匹配数据", Toast.LENGTH_SHORT).show();
                }
            }
        });

        result_list.setLayoutManager(new LinearLayoutManager(this));
        result_list.setAdapter(adapter = new MatchAdapter());
    }

    class MatchAdapter extends RecyclerView.Adapter<MatchHolder> {

        @NonNull
        @Override
        public MatchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MatchHolder(View.inflate(SmartMatchActivity.this, R.layout.item_match_result, null));
        }

        @Override
        public void onBindViewHolder(@NonNull MatchHolder viewHolder, final int i) {
            viewHolder.name.setText(mathResultData.get(i).getName());
            viewHolder.ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameDialog(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mathResultData.size();
        }
    }

    class MatchHolder extends RecyclerView.ViewHolder {
        TextView name, ok, test;

        public MatchHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            ok = itemView.findViewById(R.id.ok);
            test = itemView.findViewById(R.id.test);
        }
    }

    private void match(String code) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getrid?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&kcode=" + code + "&device_id=" + typeId;
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                mathResultData.clear();
                input_et.setVisibility(View.GONE);
                result_layout.setVisibility(View.VISIBLE);
                tips.setText("匹配结果：");
                try {
                    JSONObject object = new JSONObject(response);
                    String result = object.optString("result");
                    if (!result.isEmpty()) {
                        String data[] = result.split("\\|\\|");
                        for (int i = 0; i < data.length; i++) {
                            if (data[i].isEmpty()) {
                                continue;
                            }
                            String dev[] = data[i].split("=");
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setDeviceId(dev[0]);
                            deviceBean.setDeviceType(typeId);
                            deviceBean.setLogo(logo);
                            deviceBean.setModelno(dev[1].replace(" ", ""));
                            deviceBean.setName(dev[1].replace(" ", ""));
                            mathResultData.add(deviceBean);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(SmartMatchActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        }));

    }

    AlertDialog dialog;

    private void loadingDialog() {
        final ProgressBar et = new ProgressBar(this);
        et.setPadding(0, 50, 0, 50);
        dialog = new AlertDialog.Builder(this)
                .setView(et).show();
    }

    private void renameDialog(final int position) {
        final EditText et = new EditText(this);
        et.setText(mathResultData.get(position).getName().replace(" ", ""));
        new AlertDialog.Builder(this).setMessage("重命名")//setTitle()
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "名称不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            mathResultData.get(position).setName(input.replace(" ", ""));
                            mathResultData.get(position).save();
                            ActivityCollector.removeAll();
                            sendBroadcast(new Intent("add_device"));
                            finish();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
