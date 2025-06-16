package com.gosnin.autoclick;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnPermissionCallback {
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        String[] permissions = {
                Permission.READ_PHONE_STATE,//获取手机状态
                Permission.CAMERA,//相机
                Permission.SYSTEM_ALERT_WINDOW,//悬浮窗
                Permission.SCHEDULE_EXACT_ALARM,//定时
                Permission.RECORD_AUDIO,//录音
                Permission.MANAGE_EXTERNAL_STORAGE//管理外部存储
        };

        XXPermissions.with(this)
                .permission(permissions)
                .request(this);
    }

    @Override
    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {

    }

    @Override
    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
        OnPermissionCallback.super.onDenied(permissions, doNotAskAgain);
    }
}