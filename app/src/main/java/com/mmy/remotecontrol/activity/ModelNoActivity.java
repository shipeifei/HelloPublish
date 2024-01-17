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
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.mmy.remotecontrol.bean.ModelNoBean;
import com.mmy.remotecontrol.util.PinyinUtils;
import com.mmy.remotecontrol.util.azlist.AZBaseAdapter;
import com.mmy.remotecontrol.util.azlist.AZItemEntity;
import com.mmy.remotecontrol.util.azlist.LettersComparatorModelNoBean;
import com.mmy.remotecontrol.view.AZSideBarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ModelNoActivity extends BaseActivity {

    private RecyclerView brand_list;
    private List<AZItemEntity<ModelNoBean>> modelNoBeans = new ArrayList<>();
    private String typeId = "";//电视、空调、DVD等等类型id
    private String logo = "";
    private BrandAdapter brandAdapter;
    private AZSideBarView mBarList;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelno);
        queue = Volley.newRequestQueue(this);
        init();
        getData(typeId);
    }

    public void back(View view) {
        finish();
    }

    AlertDialog dialog;

    private void loadingDialog() {
        final ProgressBar et = new ProgressBar(this);
        et.setPadding(0, 50, 0, 50);
        dialog = new AlertDialog.Builder(this)
                .setView(et).show();
    }

    private void init() {
        if (null != getIntent() && getIntent().hasExtra("id")) {
            typeId = getIntent().getStringExtra("id");
        }
        if (null != getIntent() && getIntent().hasExtra("logo")) {
            logo = getIntent().getStringExtra("logo");
        }
        brand_list = findViewById(R.id.brand_list);

        mBarList = findViewById(R.id.bar_list);
        brand_list.setLayoutManager(new LinearLayoutManager(this));
        mBarList.setOnLetterChangeListener(new AZSideBarView.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                int position = brandAdapter.getSortLettersFirstPosition(letter);
                if (position != -1) {
                    if (brand_list.getLayoutManager() instanceof LinearLayoutManager) {
                        LinearLayoutManager manager = (LinearLayoutManager) brand_list.getLayoutManager();
                        manager.scrollToPositionWithOffset(position, 0);
                    } else {
                        brand_list.getLayoutManager().scrollToPosition(position);
                    }
                }
            }
        });
//        brand_list.setAdapter(brandAdapter = new BrandAdapter());
    }

    private void getData(String type) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getrmodellist?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&device_id=" + type;
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        ModelNoBean brandBean = new ModelNoBean();
                        brandBean.setModelno(object.optString("bn"));
                        brandBean.setKfid(object.optString("kfid"));

                        AZItemEntity<ModelNoBean> item = new AZItemEntity<>();
                        item.setValue(brandBean);
                        //汉字转换成拼音
                        String pinyin = PinyinUtils.getPingYin(brandBean.getModelno());
                        //取第一个首字母
                        String letters = pinyin.substring(0, 1).toUpperCase();
                        // 正则表达式，判断首字母是否是英文字母
                        if (letters.matches("[A-Z]")) {
                            item.setSortLetters(letters.toUpperCase());
                        } else {
                            item.setSortLetters("#");
                        }

                        modelNoBeans.add(item);
                    }
                    Collections.sort(modelNoBeans, new LettersComparatorModelNoBean());
                    brand_list.setAdapter(brandAdapter = new BrandAdapter(modelNoBeans));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(ModelNoActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    class BrandHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public BrandHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }
    }

    class BrandAdapter extends AZBaseAdapter<ModelNoBean, BrandHolder> {

        public BrandAdapter(List<AZItemEntity<ModelNoBean>> dataList) {
            super(dataList);
        }

        @NonNull
        @Override
        public BrandHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new BrandHolder(View.inflate(ModelNoActivity.this, R.layout.item_simple_text, null));
        }


        @Override
        public void onBindViewHolder(@NonNull BrandHolder brandHolder, final int i) {
            brandHolder.textView.setText(modelNoBeans.get(i).getValue().getModelno());
            brandHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameDialog(modelNoBeans.get(i).getValue());
                }
            });
        }

        @Override
        public int getItemCount() {
            return modelNoBeans.size();
        }
    }

    private void renameDialog(final ModelNoBean bean) {
        final EditText et = new EditText(this);
        et.setText(bean.getModelno());
        new AlertDialog.Builder(this).setMessage("重命名")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(getApplicationContext(), "名称不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(input);
                            deviceBean.setDeviceId(bean.getKfid());
                            deviceBean.setDeviceType(typeId);
                            deviceBean.setLogo(logo);
                            deviceBean.setModelno(bean.getModelno());
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

}
