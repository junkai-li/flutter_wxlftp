package com.example.flutter_wxlftp;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Edward on 2018-01-30.
 */
@TargetApi(18)
public class AccessibilitySampleService extends AccessibilityService {
    private final int TEMP = 200;
    //当系统绑定到服务后，它会调用AccessibilityService#onServiceConnected方法
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        flag = false;
    }

    private AccessibilityNodeInfo accessibilityNodeInfo;

    /**
     * 是否已经发送过朋友圈，true已经发送，false还未发送
     */
    public static boolean flag = false;
    ///**
    //* 发生用户界面事件回调此事件
    //* @param event
    //*/
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        int eventType = event.getEventType(); 
        flag= sharedPreferences.getBoolean(Constant.SendBool, true); 
        accessibilityNodeInfo = getRootInActiveWindow();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_SCROLLED: 
                //第一次进入如果直接是朋友圈界面
                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI")) {
                    clickCircleOfFriendsBtn();//点击发送朋友圈按钮
                }
                if (!flag && event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    jumpToCircleOfFriends();//进入朋友圈页面
                }
                if (!flag && event.getClassName().equals("com.tencent.mm.ui.mogic.WxViewPager")) {
                    jumpToCircleOfFriends();//进入朋友圈页面
                }
                if (!flag && event.getClassName().equals("com.tencent.mm.ui.mogic.WxViewPager")) {
                    jumpToCircleOfFriends();//进入朋友圈页面
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: 
                if (!flag && event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    jumpToCircleOfFriends();//进入朋友圈页面
                }
                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI")) {
                    clickCircleOfFriendsBtn();//点击发送朋友圈按钮
                } 
                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (sharedPreferences != null) {
                                int index = sharedPreferences.getInt(Constant.INDEX, 0);
                                int count = sharedPreferences.getInt(Constant.COUNT, 0); 
                                choosePicture(index,count>9?9:count);
                            }
                        }
                    }, TEMP);
                }
                if (!flag && event.getClassName().equals("com.tencent.mm.plugin.sns.ui.SnsUploadUI")) {
                    String content = sharedPreferences.getString(Constant.CONTENT, "");
                    inputContentFinish(content);//写入要发送的朋友圈内容
                }

                break;
        }
    }

    /**
     * 跳进朋友圈
     */
    private void jumpToCircleOfFriends() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("朋友圈");
                if (list != null && list.size() != 0) {
                    AccessibilityNodeInfo tempInfo = list.get(0);
                    if (tempInfo != null && tempInfo.getParent() != null) {
                        tempInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }, TEMP);
    }

    /**
     * 粘贴文本
     *
     * @param tempInfo
     * @param contentStr
     * @return true 粘贴成功，false 失败
     */
    private boolean pasteContent(AccessibilityNodeInfo tempInfo, String contentStr) {
        if (tempInfo == null) {
            return false;
        }
        if (tempInfo.isEnabled() && tempInfo.isClickable() && tempInfo.isFocusable()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", contentStr);
            if (clipboard == null) {
                return false;
            }
            clipboard.setPrimaryClip(clip);
            tempInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            tempInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            return true;
        }
        return false;
    }
    ///
    private boolean sendMsg() {
        List<AccessibilityNodeInfo> list = accessibilityNodeInfo.findAccessibilityNodeInfosByText("发表");
        if (performClickBtn(list)) {
            flag = true;//标记为已发送
            return true;
        }
        return false;
    }

    /**
     * 写入朋友圈内容
     *
     * @param contentStr
     */
    private void inputContentFinish(final String contentStr) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }
                //九张图没有添加照片的按钮 则用图片 图片
                final SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, android.content.Context.MODE_MULTI_PROCESS);
                int count = sharedPreferences.getInt(Constant.COUNT, 0);
                List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(count>=9?"图片":"添加照片按钮"); 
                if (nodeInfoList == null ||
                        nodeInfoList.size() == 0 ||
                        nodeInfoList.get(0) == null ||
                        nodeInfoList.get(0).getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent().getParent() == null ||
                        nodeInfoList.get(0).getParent().getParent().getParent().getChildCount() == 0) {
                        return;
                }
                AccessibilityNodeInfo tempInfo = nodeInfoList.get(0).getParent().getParent().getParent().getChild(1);//微信6.6.6
                //不需要自动发布
                if (pasteContent(tempInfo, contentStr)) {
                   // sendMsg();
                    //已完成发布 修改状态
                    flag = true;//标记为已发送 
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constant.SendBool, true);
                    editor.commit();
                }
            }
        }, TEMP);
    }

    /**
     * @param accessibilityNodeInfoList
     * @return
     */
    private boolean performClickBtn(List<AccessibilityNodeInfo> accessibilityNodeInfoList) {
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() != 0) {
            for (int i = 0; i < accessibilityNodeInfoList.size(); i++) {
                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoList.get(i);
                if (accessibilityNodeInfo != null) {
                    if (accessibilityNodeInfo.isClickable() && accessibilityNodeInfo.isEnabled()) {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 选择图片
     *
     * @param startPicIndex 从第startPicIndex张开始选
     * @param picCount      总共选picCount张
     */
    private void choosePicture(final int startPicIndex, final int picCount) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }
                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("预览");
                if (accessibilityNodeInfoList == null ||
                        accessibilityNodeInfoList.size() == 0 ||
                        accessibilityNodeInfoList.get(0).getParent() == null ||
                        accessibilityNodeInfoList.get(0).getParent().getChildCount() == 0) {
                    return;
                }  
                AccessibilityNodeInfo tempInfo = accessibilityNodeInfoList.get(0).getParent().getChild(3);
                for (int j = startPicIndex; j < startPicIndex + picCount; j++) {
                    AccessibilityNodeInfo childNodeInfo = tempInfo.getChild(j);
                    //华为手机有搜素建位改成自动获取index的chilid
                    if(childNodeInfo==null){
                        tempInfo=accessibilityNodeInfoList.get(0).getParent().getChild(4);
                        childNodeInfo=tempInfo.getChild(j);
                    }
                    if (childNodeInfo != null) {
                        //遍历子节点
                        for (int k = 0; k < childNodeInfo.getChildCount(); k++) {
                            if (childNodeInfo.getChild(k).isEnabled() && childNodeInfo.getChild(k).isClickable()) {
                                childNodeInfo.getChild(k).performAction(AccessibilityNodeInfo.ACTION_CLICK);//选中图片
                            }
                        }
                    }
                }

                List<AccessibilityNodeInfo> finishList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("完成(" + picCount + "/9)");//点击确定
                performClickBtn(finishList);
            }
        }, TEMP);
    }


    /**
     * 点击发送朋友圈按钮
     */
    private void clickCircleOfFriendsBtn() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }

                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("拍照分享");
                performClickBtn(accessibilityNodeInfoList);
                openAlbum();
            }
        }, TEMP);
    }


    /**
     * 打开相册
     */
    private void openAlbum() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (accessibilityNodeInfo == null) {
                    return;
                }

                List<AccessibilityNodeInfo> accessibilityNodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText("从相册选择");
                traverseNode(accessibilityNodeInfoList);
            }
        }, TEMP);
    }

    private boolean traverseNode(List<AccessibilityNodeInfo> accessibilityNodeInfoList) {
        if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() != 0) {
            AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfoList.get(0).getParent();
            if (accessibilityNodeInfo != null && accessibilityNodeInfo.getChildCount() != 0) {
                accessibilityNodeInfo = accessibilityNodeInfo.getChild(0);
                if (accessibilityNodeInfo != null) {
                    accessibilityNodeInfo = accessibilityNodeInfo.getParent();
                    if (accessibilityNodeInfo != null) {
                        accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);//点击从相册中选择
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }


    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("服务被杀死!");
    }
}
