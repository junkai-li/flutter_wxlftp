package com.example.flutter_wxlftp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity{
    EditText edit, editCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainwx);
        initView();
    }

    private void initView() {
        edit = findViewById(R.id.edit);
        editCount = findViewById(R.id.edit_count);

        /*findViewById(R.id.open_accessibility_setting).setOnClickListener(clickListener);*/
//        findViewById(R.id.btn_save).setOnClickListener(clickListener);
    }

//    View.OnClickListener clickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//           /*     case R.id.open_accessibility_setting:
//                    OpenAccessibilitySettingHelper.jumpToSettingPage(getBaseContext());
//                    break;*/
//                case R.id.btn_save:
//                    saveData();
//                    break;
//            }
//        }
//    };

    public boolean checkParams() {

        if (TextUtils.isEmpty(editCount.getText().toString())) {
            Toast.makeText(getBaseContext(), "图片总数不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(editCount.getText().toString()) > 9) {
            Toast.makeText(getBaseContext(), "图片总数不能超过9张", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveData() {
        //判断是否有辅助功能权限
        if (!AccessibilityUtil.isAccessibilitySettingsOn(MainActivity.this))
        {
            //没有跳转辅助开关页面
            Toast.makeText(getBaseContext(), "请先开启辅助开关", Toast.LENGTH_LONG).show();
            //开启
            OpenAccessibilitySettingHelper.jumpToSettingPage(getBaseContext());
            return;
        }
        if (!checkParams()) {
            return;
        }
        //默认从零开始
        int index =0;
        int count = Integer.valueOf(editCount.getText().toString());

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.CONTENT, edit.getText().toString());
        editor.putInt(Constant.INDEX, index);
        editor.putInt(Constant.COUNT, count);
        editor.putBoolean(Constant.SendBool, false);
        if (editor.commit()) {
            Toast.makeText(getBaseContext(), "保存成功", Toast.LENGTH_LONG).show();
            openWeChatApplication();//打开微信应用
        } else {
            Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_LONG).show();
        }
    }

    private void openWeChatApplication() {
        PackageManager packageManager = getBaseContext().getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage("com.tencent.mm");
        startActivity(it);
    }

}