import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_wxlftp/flutter_wxlftp.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown'; 

  @override
  void initState() {
    super.initState();
     isAccessibilitySettingsOn(); 
  } 
  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
       FlutterWxlftp.flutterWxlftpbegin(6,"哈哈哈哈哈");
      
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }
 Future<void> isAccessibilitySettingsOn() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
       FlutterWxlftp.isAccessibilitySettingsOn();
      
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: InkWell( child: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
        onTap: (){
        initPlatformState();
        },
        ),
      ),
    );
  }
}
