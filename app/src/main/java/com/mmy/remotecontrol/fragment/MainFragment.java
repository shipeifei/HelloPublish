package com.mmy.remotecontrol.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mmy.remotecontrol.R;
import com.mmy.remotecontrol.activity.AirControlActivity;
import com.mmy.remotecontrol.activity.ButtonActivity;
import com.mmy.remotecontrol.activity.DeviceCateActivity;
import com.mmy.remotecontrol.bean.DeviceBean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {


    private RecyclerView device_list;
    private List<DeviceBean> deviceBeanData = new ArrayList<>();
    private DeviceAdapter deviceAdapter;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        init(view);
        reFresh();
        return view;
    }


    private void addDefaultPlus() {
        DeviceBean deviceBean = new DeviceBean();
        deviceBean.setDeviceId("");
        deviceBean.setName("");
        deviceBeanData.add(deviceBean);
    }

    public void reFresh() {
        getData();
    }


    private void init(View v) {
        device_list = v.findViewById(R.id.device_list);
        device_list.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        device_list.setAdapter(deviceAdapter = new DeviceAdapter());
    }

    private void getData() {
        List<DeviceBean> data = DataSupport.order("name asc").find(DeviceBean.class);
        if (data != null && data.size() > 0) {
            deviceBeanData.clear();
            deviceBeanData.addAll(data);
            addDefaultPlus();
        } else if (deviceBeanData.size() <= 0) {
            addDefaultPlus();
        }
        deviceAdapter.notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout root;

        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.cate_name);
            root = itemView.findViewById(R.id.root);
        }
    }

    public void removeDevice(final DeviceBean deviceBean) {
        new AlertDialog.Builder(getActivity()).setMessage("确定要删除此设备吗？")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.delete(DeviceBean.class, deviceBean.getId());
                        reFresh();

                    }
                }).setNeutralButton("取消", null).show();

    }

    class DeviceAdapter extends RecyclerView.Adapter<Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new Holder(View.inflate(getActivity(), R.layout.item_device, null));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, final int i) {
            holder.textView.setText(deviceBeanData.get(i).getName().isEmpty() ? "" : deviceBeanData.get(i).getName());
            Glide.with(getActivity()).load(deviceBeanData.get(i).getName().isEmpty()
                    ? R.mipmap.add_icon :
                    TextUtils.isEmpty(deviceBeanData.get(i).getLogo()) ?
                            R.mipmap.device_icon : deviceBeanData.get(i).getLogo()).into(holder.imageView);
            if (deviceBeanData.get(i).getName().isEmpty()){
                holder.imageView.setBackgroundResource(R.mipmap.add_bg);
            }else{
                holder.imageView.setBackgroundResource(0);
            }
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deviceBeanData.get(i).getDeviceId().isEmpty()) {
                        startActivity(new Intent(getActivity(), DeviceCateActivity.class));
                    } else {

                        if ("1".equals(deviceBeanData.get(i).getDeviceType())) {
                            startActivity(new Intent(getActivity(), AirControlActivity.class)
                                    .putExtra("id", deviceBeanData.get(i).getDeviceType())
                                    .putExtra("show_type", ButtonActivity.TYPE_CONTROL)
                                    .putExtra("logo", deviceBeanData.get(i).getLogo())
                                    .putExtra("control_id", deviceBeanData.get(i).getDeviceId()));
                        } else {
                            startActivity(new Intent(getActivity(), ButtonActivity.class)
                                    .putExtra("id", deviceBeanData.get(i).getDeviceType())
                                    .putExtra("show_type", ButtonActivity.TYPE_CONTROL)
                                    .putExtra("logo", deviceBeanData.get(i).getLogo())
                                    .putExtra("control_id", deviceBeanData.get(i).getDeviceId()));
                        }

                    }
                }
            });
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeDevice(deviceBeanData.get(i));
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return deviceBeanData.size();
        }
    }

}
