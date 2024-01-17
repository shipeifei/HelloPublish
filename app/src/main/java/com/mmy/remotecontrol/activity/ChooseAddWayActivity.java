package com.mmy.remotecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mmy.remotecontrol.R;

public class ChooseAddWayActivity extends BaseActivity implements View.OnClickListener {

    private Button smatr_tv, bround_tv, xinghao_tv;
    private String typeId;
    private String logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_add_way);
        init();
    }

    public void back(View view) {
        finish();
    }

    private void init() {
        if (null != getIntent() && getIntent().hasExtra("id")) {
            typeId = getIntent().getStringExtra("id");
        }
        if (null != getIntent() && getIntent().hasExtra("logo")) {
            logo = getIntent().getStringExtra("logo");
        }
        smatr_tv = findViewById(R.id.smart_tv);
        bround_tv = findViewById(R.id.brond_tv);
        xinghao_tv = findViewById(R.id.xinghao_tv);

        smatr_tv.setOnClickListener(this);
        bround_tv.setOnClickListener(this);
        xinghao_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.smart_tv:
                startActivity(new Intent(this, SmartMatchActivity.class)
                        .putExtra("logo",logo)
                        .putExtra("id", typeId));
                break;
            case R.id.brond_tv:
                startActivity(new Intent(this, BrandActivity.class)
                        .putExtra("logo",logo)
                        .putExtra("id", typeId));
                break;
            case R.id.xinghao_tv:
                startActivity(new Intent(this, ModelNoActivity.class)
                        .putExtra("logo",logo)
                        .putExtra("id", typeId));
                break;
        }
    }
}
