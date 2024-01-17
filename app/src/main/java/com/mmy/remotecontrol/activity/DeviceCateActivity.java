package com.mmy.remotecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mmy.remotecontrol.Constant;
import com.mmy.remotecontrol.R;
import com.mmy.remotecontrol.bean.DeviceCate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备分类列表
 */
public class DeviceCateActivity extends BaseActivity {

    RequestQueue queue;
    List<DeviceCate> deviceCateList = new ArrayList<>();
    RecyclerView cate_list;
    DeviceCateAdapter deviceCateAdapter;
    RequestOptions requestOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_type);
        init();
        requestOptions = new RequestOptions();
        requestOptions.error(R.mipmap.device_icon);
        requestOptions.placeholder(R.mipmap.device_icon);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        getData();
    }

    public void back(View v) {
        finish();
    }

    private void init() {
        queue = Volley.newRequestQueue(this);
        cate_list = findViewById(R.id.cate_list);
        cate_list.setLayoutManager(new GridLayoutManager(this, 3));
        cate_list.setAdapter(deviceCateAdapter = new DeviceCateAdapter());
    }

    AlertDialog dialog;

    private void loadingDialog() {
        final ProgressBar et = new ProgressBar(this);
        et.setPadding(0, 50, 0, 50);
        dialog = new AlertDialog.Builder(this)
                .setView(et).show();
    }

    class DeviceCateAdapter extends RecyclerView.Adapter<CateHolder> {
        @NonNull
        @Override
        public CateHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CateHolder(View.inflate(DeviceCateActivity.this, R.layout.item_cate, null));
        }

        @Override
        public void onBindViewHolder(@NonNull CateHolder viewHolder, final int i) {
            viewHolder.textView.setText(deviceCateList.get(i).getDevice_name());
            Glide.with(DeviceCateActivity.this).applyDefaultRequestOptions(requestOptions).load(deviceCateList.get(i).getLogo()).into(viewHolder.imageView);
            viewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DeviceCateActivity.this, ChooseAddWayActivity.class)
                            .putExtra("logo",deviceCateList.get(i).getLogo())
                            .putExtra("id", deviceCateList.get(i).getId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return deviceCateList.size();
        }
    }

    class CateHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout root;

        public CateHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.cate_name);
            root = itemView.findViewById(R.id.root);
        }
    }

    private void getData() {
        loadingDialog();
        String url = Constant.getBaseUrl(this) + "getdevicelist.asp?mac=afc1d387b4ab661d";
        queue.add(new StringRequest(url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
                        DeviceCate deviceCate = new DeviceCate();
                        deviceCate.setDevice_name(object.optString("device_name"));
                        deviceCate.setId(object.optString("id"));
                        deviceCate.setLogo(object.optString("logo"));
                        deviceCateList.add(deviceCate);
                    }
                    deviceCateAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(DeviceCateActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

    }
}
