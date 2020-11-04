package com.example.flutter_wxlftp;

import android.Manifest;
import android.app.Activity; 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat; 
import android.widget.Toast; 
import android.content.Intent;
import android.content.SharedPreferences; 
import android.content.ClipboardManager;
import android.content.Context;
import android.app.Activity;
import android.content.ContextWrapper;

     
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log; 


import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** FlutterWxlftpPlugin */
public class FlutterWxlftpPlugin  implements MethodCallHandler, RequestPermissionsResultListener{ 
  private static final String NAMESPACE = "flutterWxlftp";
  private final Registrar registrar;
  private final Activity activity;
  private final MethodChannel channel;  

  private MethodCall pendingCall;
  private Result pendingResult;

  public static void registerWith(Registrar registrar) {
    final FlutterWxlftpPlugin instance = new FlutterWxlftpPlugin(registrar);
    registrar.addRequestPermissionsResultListener(instance);
  }

  FlutterWxlftpPlugin(Registrar r){
    this.registrar = r;
    this.activity = r.activity(); 
    this.channel = new MethodChannel(registrar.messenger(), NAMESPACE + "/methods");   
    channel.setMethodCallHandler(this); 
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {  
    switch (call.method){ 
        case "flutterWxlftpbegin":
        final Map<String, Object> args = call.arguments();
        final  String content = (String)args.get("content");
        final int  count = (int)args.get("count");
        saveData(count,content);  
        break;
        //是否开启辅助开关
        case "isAccessibilitySettingsOn":
        isAccessibilitySettingsOn();  
        break;
      default:
        result.notImplemented();
        break;
    }

  }    
  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    return true;  
  }
 
  private void saveData(int count,String content) {
        //判断是否有辅助功能权限
        if (!AccessibilityUtil.isAccessibilitySettingsOn(activity))
        {
            //没有跳转辅助开关页面
            Toast.makeText(registrar.context(), "请先开启辅助开关", Toast.LENGTH_LONG).show();
            //开启
            OpenAccessibilitySettingHelper.jumpToSettingPage(registrar.context());
            return;
        }
      // Intent intent = new Intent(activity, MainActivity.class);
      // activity.startActivity(intent);
      //默认从零开始
        int index =0; 
        ContextWrapper data= new  ContextWrapper(registrar.context());
        SharedPreferences sharedPreferences = data.getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.CONTENT, content);
        editor.putInt(Constant.INDEX, index);
        editor.putInt(Constant.COUNT, count);
        editor.putBoolean(Constant.SendBool, false);
         if (editor.commit()) {
            Toast.makeText(registrar.context(), "保存成功", Toast.LENGTH_LONG).show();
            openWeChatApplication();//打开微信应用
         } else {
             Toast.makeText(registrar.context(), "保存失败", Toast.LENGTH_LONG).show();
         }
    }

    private void openWeChatApplication() {
        PackageManager packageManager = registrar.context().getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage("com.tencent.mm");
        activity.startActivity(it);
    }


  
 
   private void isAccessibilitySettingsOn() {
        //判断是否有辅助功能权限
        if (!AccessibilityUtil.isAccessibilitySettingsOn(activity))
        {
            //没有跳转辅助开关页面
            Toast.makeText(registrar.context(), "请先开启辅助开关", Toast.LENGTH_LONG).show();
            //开启
            OpenAccessibilitySettingHelper.jumpToSettingPage(registrar.context()); 
        } 
  }
}
