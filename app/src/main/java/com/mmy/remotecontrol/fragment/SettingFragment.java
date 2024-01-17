package com.mmy.remotecontrol.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mmy.remotecontrol.Constant;
import com.mmy.remotecontrol.R;

public class SettingFragment extends Fragment {

    TextView changeIp;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        changeIp = view.findViewById(R.id.changeIp);
        changeIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIpDialog();
            }
        });
        view.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAbout();
            }
        });
        return view;
    }

    private void showAbout() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("本APP由上海数氪智能有限公司提供红外码库支持").create();
        dialog.show();
    }

    private void changeIpDialog() {
        final EditText editText = new EditText(getActivity());
        editText.setHint("http://www.baidu.com/index/");
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("请输入新的域名").setMessage("以 http 开头，以 / 结尾")
                .setView(editText)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = editText.getText().toString().trim();
                        if (TextUtils.isEmpty(url)) {
                            Toast.makeText(getActivity(), "域名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Constant.setBaseUrl(getActivity(), url);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }
}
