package com.yy.msdkquality.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.Process;

import com.hjc.SDKParam.SDKParam;
import com.ycloud.live.YCMedia;
import com.ycloud.live.YCMediaRequest;
import com.ycloud.live.YCMessage;
import com.ycloud.live.yyproto.ProtoReq;
import com.ycsignal.outlet.IProtoMgr;
import com.yy.saltonframework.util.LogUtils;

import java.io.File;
import java.util.List;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/1/10 16:45
 * Time: 16:45
 * Description:
 */
public class SDKEngine {
    private long mTerminalType = 0x20001; //android terminal type 0x20001

//    //app信息在欢聚云应用管理页面上获取
//    private int mAppKey = YConfig.mAppKey;
//    private String mAppSecret = YConfig.mAppSecret;
//    private int mAppVersion = YConfig.mAppVersion;

    private static SDKEngine instance;

    /**
     * 单例模式
     *
     * @return
     */
    public static SDKEngine getInstance() {
        if (instance == null) {
            instance = new SDKEngine();
        }
        return instance;
    }

    public Context getContext() {
        return mContext;
    }

    private Context mContext;

    public boolean init(Context context, int appKey, String appSecret, int appVersion) {
        this.mContext = context;
        if (isPkgMainProc(context)) {
            File esdf = Environment.getExternalStorageDirectory();
            String logPath = esdf.getAbsolutePath();
            if (null != logPath) {
                logPath += "/ycmedia/msdkquality";
            }
            SDKParam.AppInfo app = new SDKParam.AppInfo();
            app.appKey = appKey;
            app.terminalType = mTerminalType;
            app.logPath = logPath.getBytes();
            app.appVer = String.valueOf(appVersion).getBytes();
            IProtoMgr.instance().init(context, app, null);
            boolean mediaInit = YCMedia.getInstance().init(context, IProtoMgr.instance(), logPath);
            LogUtils.i("mediaInit:" + mediaInit);
            return true;
        }
        return false;
    }

    public boolean isPkgMainProc(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取频道内成员信息请求
     *
     * @param sid 频道标识
     */
    public static void getQueueMembers(int sid) {
        ProtoReq.SessPullUserListReq _SessPullUserListReq = new ProtoReq.SessPullUserListReq();
        _SessPullUserListReq.topSid = sid;
        handlerRequestResult(sendProtoRequest(_SessPullUserListReq.getBytes()));
    }

    public static void loginMedia(int appid, int sid, int uid, int wanIp, int isp, int areaType, byte[] token) {
        boolean isSucceed = YCMedia.getInstance().requestMethod(new YCMediaRequest.YCLogin(appid, sid, uid, wanIp, isp, areaType, token));
        LogUtils.e("登陆媒体:" + isSucceed);
    }

    public static void logoutSignal() {
        ProtoReq.LoginoutReq req = new ProtoReq.LoginoutReq();
        IProtoMgr.instance().sendRequest(req.getBytes());
    }

    /**
     * 发送信令消息
     *
     * @param data 消息体
     */
    private static int sendProtoRequest(byte[] data) {
        return IProtoMgr.instance().sendRequest(data);
    }

    /**
     * 处理信令请求返回的结果
     *
     * @param resultCode
     */
    private static void handlerRequestResult(int resultCode) {
        String currentMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        if (resultCode == com.ycsignal.outlet.SDKParam.RequestRes.REQ_SUCCESS) {
            LogUtils.e(currentMethodName + ":请求成功");
        } else if (resultCode == com.ycsignal.outlet.SDKParam.RequestRes.REQ_TOO_QUICK) {
            LogUtils.e(currentMethodName + ":请求过快");
        } else if (resultCode == com.ycsignal.outlet.SDKParam.RequestRes.REQ_FAILED) {
            LogUtils.e(currentMethodName + ":请求失败");
        } else {
            LogUtils.e(currentMethodName + ":请求出现未知错误");
        }
    }

    /**
     * 通过指定UID方式登录
     *
     * @param uid      指定uid
     * @param tokenHex token
     */
    public static void loginToProtoServerByUid(long uid, String tokenHex) {
        ProtoReq.LoginByUidReq _LoginByUidReq = new ProtoReq.LoginByUidReq(uid, tokenHex);
        handlerRequestResult(sendProtoRequest(_LoginByUidReq.getBytes()));
    }

    /**
     * 进频道请求
     *
     * @param sid      频道标识
     * @param tokenHex token
     */
    public static void joinQueue(int sid, String tokenHex) {
        ProtoReq.SessJoinReq _SessJoinReq = new ProtoReq.SessJoinReq(sid);
        _SessJoinReq.topSid = sid;
        _SessJoinReq.subSid = 0;
        _SessJoinReq.appToken = tokenHex;
        handlerRequestResult(sendProtoRequest(_SessJoinReq.getBytes()));
    }

    /**
     * 进频道请求
     *
     * @param sid      频道标识
     * @param subSid   子频道标识
     * @param tokenHex token
     */
    public static void joinQueue(int sid, int subSid, String tokenHex) {
        ProtoReq.SessJoinReq _SessJoinReq = new ProtoReq.SessJoinReq(sid);
        _SessJoinReq.topSid = sid;
        _SessJoinReq.subSid = subSid;
        _SessJoinReq.appToken = tokenHex;
        handlerRequestResult(sendProtoRequest(_SessJoinReq.getBytes()));
    }

    /**
     * 发送信令消息
     *
     * @param sid     频道标识
     * @param message 消息体
     */
    public static void sendProtoTestMsg(int sid, String message) {
        ProtoReq.SessTextChatReq textChatReq = new ProtoReq.SessTextChatReq();
        textChatReq.chat = message;
        textChatReq.context = message;
        textChatReq.topSid = sid;
        handlerRequestResult(sendProtoRequest(textChatReq.getBytes()));
    }

    /**
     * 发送信令消息,带自定义消息
     *
     * @param sid
     * @param context
     * @param message
     */
    public static void sendProtoTestMsg(int sid, String context, String message) {
        ProtoReq.SessTextChatReq textChatReq = new ProtoReq.SessTextChatReq();
        textChatReq.chat = message;
        textChatReq.topSid = sid;
        textChatReq.context = context;
        handlerRequestResult(sendProtoRequest(textChatReq.getBytes()));
    }

    public static String getNoVideoReason(int reasonType) {
        String resonStr = "";
        switch (reasonType) {
            case YCMessage.NoVideoInfo.HAS_VIDEO_PLAY:
                resonStr = "HAS_VIDEO_PLAY";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_SUBSCRIBE:
                resonStr = "NO_VIDEO_SUBSCRIBE";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_PACKET:
                resonStr = "NO_VIDEO_PACKET";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_FRAME:
                resonStr = "NO_VIDEO_FRAME";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_PUSH_TO_DECODE:
                resonStr = "NO_VIDEO_PUSH_TO_DECODE";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_ON_PLAY:
                resonStr = "NO_VIDEO_ON_PLAY";
                break;
            case YCMessage.NoVideoInfo.VIDEO_DECODE_ERROR:
                resonStr = "VIDEO_DECODE_ERROR";
                break;
            case YCMessage.NoVideoInfo.NOT_SUBSCRIBE_BY_PLUGIN:
                resonStr = "NOT_SUBSCRIBE_BY_PLUGIN";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_I_FRAME:
                resonStr = "NO_VIDEO_I_FRAME";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_VIEW:
                resonStr = "NO_VIDEO_VIEW";
                break;
            case YCMessage.NoVideoInfo.NO_FETCH_VIDEO_PROXY:
                resonStr = "NO_FETCH_VIDEO_PROXY";
                break;
            case YCMessage.NoVideoInfo.NO_VIDEO_LIVE:
                resonStr = "NO_VIDEO_LIVE";
                break;
            case YCMessage.NoVideoInfo.TCP_LOGIN_FAILED:
                resonStr = "TCP_LOGIN_FAILED";
                break;
            case YCMessage.NoVideoInfo.NO_RECV_STREAM_ID:
                resonStr = "NO_RECV_STREAM_ID";
                break;
        }
        return resonStr;
    }

    public static String getProtoEvtSessOperReason(int res){
        String resonStr = "";
        switch (res){
            case 0:
                resonStr="成功";
                break;
            case 1:
                resonStr="操作过快";
                break;
            case 2:
                resonStr="操作用户不存在";
                break;
            case 3:
            resonStr="被操作用户不存在";
            break;
            case 4:
                resonStr="频道不存在";
                break;
            case 5:
                resonStr="子频道不存在";
                break;
            case 6:
                resonStr="其他参数错误";
                break;
            case 7:
                resonStr="无权限执行操作";
                break;
            case 8:
                resonStr="后端写数据操作失败";
                break;
            case 9:
                resonStr="uid不是真实的";
                break;
            case 10:
                resonStr="appKey不是真的";
                break;
            case 11:
                resonStr="频道设置了密码，要求输入密码";
                break;
            case 12:
                resonStr="提示客户端进频道多端互踢";
                break;
            case 13:
                resonStr="多端互踢超时";
                break;
            case 14:
                resonStr="频道人数达到上限";
                break;
            case 15:
                resonStr="进频道拥塞";
                break;
            case 21:
                resonStr="操作未购买";
                break;
            case 22:
                resonStr="Token超时";
                break;
            case 23:
                resonStr="Token摘要对比失败";
                break;
            case 24:
                resonStr="Token长度不对";
                break;
            case 25:
                resonStr="Token格式不对";
                break;
            case 26:
                resonStr="未支持的AppKey";
                break;
            case 27:
                resonStr="Token未知错误";
                break;
            case 33:
                resonStr="检验flag位错误，例如不是0也不是1";
                break;
            case 35:
                resonStr="用户被踢出频道一段时间，在这段时间内用户又再次进频道";
                break;
            case 37:
                resonStr="进程没有记录该用户被踢出去";
                break;
            case 38:
                resonStr="写入在线数据库失败";
                break;
            case 40:
                resonStr="密码错误";
                break;
            case 101:
                resonStr="访问频率限制";
                break;
            case 102:
                resonStr="频道不存在";
                break;
            case 103:
                resonStr="参数错误";
                break;
            case 104:
                resonStr="数据库操作错误";
                break;
            case 105:
                resonStr="数量限制";
                break;
            case 106:
                resonStr="频道已存在";
                break;
        }
        return resonStr;
    }
}
