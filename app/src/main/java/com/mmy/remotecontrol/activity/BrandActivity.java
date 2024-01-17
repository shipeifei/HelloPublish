package com.mmy.remotecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mmy.remotecontrol.Constant;
import com.mmy.remotecontrol.R;
import com.mmy.remotecontrol.bean.BrandBean;
import com.mmy.remotecontrol.util.PinyinUtils;
import com.mmy.remotecontrol.util.azlist.AZBaseAdapter;
import com.mmy.remotecontrol.util.azlist.AZItemEntity;
import com.mmy.remotecontrol.util.azlist.LettersComparatorBrandBean;
import com.mmy.remotecontrol.view.AZSideBarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BrandActivity extends BaseActivity {

    private RecyclerView brand_list;
    private AZSideBarView mBarList;
    private List<AZItemEntity<BrandBean>> brandBeanList = new ArrayList<>();
    private String typeId = "";
    private String logo = "";
    private BrandAdapter brandAdapter;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
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
    }

    private void getData(final String type) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getbrandlist?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&device_id=" + type;
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        BrandBean brandBean = new BrandBean();
                        brandBean.setBrand(object.optString("bn"));
                        brandBean.setId(object.optString("id"));

                        AZItemEntity<BrandBean> item = new AZItemEntity<>();
                        item.setValue(brandBean);
                        //汉字转换成拼音
                        String pinyin = PinyinUtils.getPingYin(brandBean.getBrand());
                        //取第一个首字母
                        String letters = pinyin.substring(0, 1).toUpperCase();
                        // 正则表达式，判断首字母是否是英文字母
                        if (letters.matches("[A-Z]")) {
                            item.setSortLetters(letters.toUpperCase());
                        } else {
                            item.setSortLetters("#");
                        }
                        brandBeanList.add(item);
                    }

                    Collections.sort(brandBeanList, new LettersComparatorBrandBean());
                    getDataEn(type);

//                    brand_list.setAdapter(brandAdapter = new BrandAdapter(brandBeanList));
//                    brandAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(BrandActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }


    private void getDataEn(String type) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getbrandlist?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&device_id=" + type + "&lang=en";
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);

                    List<AZItemEntity<BrandBean>> brandList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        BrandBean brandBean = new BrandBean();
                        brandBean.setBrand(object.optString("bn"));
                        brandBean.setId(object.optString("id"));

                        AZItemEntity<BrandBean> item = new AZItemEntity<>();
                        item.setValue(brandBean);
                        //汉字转换成拼音
                        String pinyin = PinyinUtils.getPingYin(brandBean.getBrand());
                        //取第一个首字母
                        String letters = pinyin.substring(0, 1).toUpperCase();
                        // 正则表达式，判断首字母是否是英文字母
                        if (letters.matches("[A-Z]")) {
                            item.setSortLetters(letters.toUpperCase());
                        } else {
                            item.setSortLetters("#");
                        }
                        brandList.add(item);
                    }

                    for (int i = 0; i < brandBeanList.size(); i++) {
                        AZItemEntity<BrandBean> brandBeanAZItemEntity = brandBeanList.get(i);
                        for (int j = 0; j < brandList.size(); j++) {
                            AZItemEntity<BrandBean> brandBeanAZItemEntity1 = brandList.get(j);
                            if (brandBeanAZItemEntity.getValue().getId().equals(brandBeanAZItemEntity1.getValue().getId())) {
                                brandBeanAZItemEntity.getValue().setBrandEn(brandBeanAZItemEntity1.getValue().getBrand());
                            }
                        }
                    }

                    brand_list.setAdapter(brandAdapter = new BrandAdapter(brandBeanList));
                    brandAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(BrandActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }

    class BrandHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textViewEv;
        RelativeLayout item;

        public BrandHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            textViewEv = itemView.findViewById(R.id.text_en);
            item = itemView.findViewById(R.id.item);
        }
    }

    class BrandAdapter extends AZBaseAdapter<BrandBean, BrandHolder> {

        public BrandAdapter(List<AZItemEntity<BrandBean>> dataList) {
            super(dataList);
        }

        @NonNull
        @Override
        public BrandHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new BrandHolder(View.inflate(BrandActivity.this, R.layout.item_brand, null));
        }

        @Override
        public void onBindViewHolder(@NonNull BrandHolder brandHolder, final int i) {
            brandHolder.textView.setText(brandBeanList.get(i).getValue().getBrand());
            brandHolder.textViewEv.setText(brandBeanList.get(i).getValue().getBrandEn());
            brandHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getModelNo(brandBeanList.get(i).getValue().getId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return brandBeanList.size();
        }
    }

    private void getModelNo(String brandId) {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getmodellist?ak=d4cb3fb1b53811eeb455cd4b0b3ec5c7&sn=d4cb3fb1b53811eeb455cd4b0b3ec5c7&mac=afc1d387b4ab661d&device_id=" + typeId + "&brand_id=" + brandId;
        queue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("LY", response);
                try {

                    if (typeId.equals("1")) {
                        startActivity(new Intent(BrandActivity.this, AirControlActivity.class)
                                .putExtra("show_type", ButtonActivity.TYPE_TEST)
                                .putExtra("id", typeId)
                                .putExtra("logo", logo)
                                .putExtra("json", response));
                    } else {
                        startActivity(new Intent(BrandActivity.this, ButtonActivity.class)
                                .putExtra("show_type", ButtonActivity.TYPE_TEST)
                                .putExtra("id", typeId)
                                .putExtra("logo", logo)
                                .putExtra("json", response));
                    }

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
}
