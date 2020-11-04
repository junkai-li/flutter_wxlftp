
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterWxlftp {
   static final String NAMESPACE = "flutterWxlftp";
  static const MethodChannel _channel =   const MethodChannel('flutterWxlftp/methods');
  
  static  void   flutterWxlftpbegin(int count,String content) async {
        Map<String, Object> args = Map();
        //选择几张图片
    args['count'] = count;
    //复制文本内容
      args['content'] = content;
    await _channel.invokeMethod('flutterWxlftpbegin',args); 
  }

    static  void   isAccessibilitySettingsOn() async { 
    await _channel.invokeMethod('isAccessibilitySettingsOn'); 
  }
}
