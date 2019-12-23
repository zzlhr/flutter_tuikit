package com.lhrsite.flutter_tuikit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat
import android.util.Log
import com.lhrsite.flutter_tuikit.timsdk.GenerateTestUserSig
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMManager
import com.tencent.imsdk.TIMOfflinePushSettings
import com.tencent.imsdk.TIMSdkConfig
import com.tencent.qcloud.tim.uikit.TUIKit
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig
import com.tencent.qcloud.tim.uikit.config.GeneralConfig
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.Console

/** FlutterTuikitPlugin */
class FlutterTuikitPlugin : FlutterPlugin, MethodCallHandler {
    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel = MethodChannel(binding.binaryMessenger, name)
        methodChannel.setMethodCallHandler(this)

        FlutterTuikitPlugin.binding = binding
    }


    companion object {
        const val tag = "FlutterTuikitPlugin"
        const val name = "plugin.lhrsite.com/flutter_tuikit";
        lateinit var methodChannel: MethodChannel
        lateinit var binding: FlutterPlugin.FlutterPluginBinding
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "getPlatformVersion" -> return result.success("Android ${android.os.Build.VERSION.RELEASE}")
            "init" -> init(call, result) // 初始化
            "startMsgList" -> startMsgList(call, result) // 打开消息列表
            "startChat" -> startChat(call, result) // 启动消息内容
            "login" -> login(call, result) // 登录
            "genTestSignature" -> genTestSignature(call, result) // 获取测试签名
            "enabledOfflinePush" -> enabledOfflinePush(call, result) // 启用离线推送
            "disableOfflinePush" -> disableOfflinePush(call, result) // 禁用离线推送
            else -> result.notImplemented()
        }
    }

    private fun disableOfflinePush(call: MethodCall, result: Result) {
        val settings = TIMOfflinePushSettings()
        //禁用离线推送
        settings.isEnabled = false
        TIMManager.getInstance().setOfflinePushSettings(settings)
    }

    private fun enabledOfflinePush(call: MethodCall, result: Result) {
        val settings = TIMOfflinePushSettings()
        //开启离线推送
        settings.isEnabled = true
        //设置收到 C2C 离线消息时的提示声音，以把声音文件放在 res/raw 文件夹下为例
//        settings.c2cMsgRemindSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dudulu)
        //设置收到群离线消息时的提示声音，以把声音文件放在 res/raw 文件夹下为例
//        settings.groupMsgRemindSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dudulu)
        TIMManager.getInstance().setOfflinePushSettings(settings)
    }


    private fun genTestSignature(call: MethodCall, result: Result) {

        val sdkAppId: Long = call.argument("sdkAppId")!!
        val secretKey: String = call.argument("secretKey")!!

        val expireTime: Long = if (call.argument<Long>("expireTime") == null) {
            604800; //默认时间：7 x 24 x 60 x 60 = 604800 = 7 天
        } else {
            call.argument<Long>("expireTime")!!
        }

        val userSig =
                GenerateTestUserSig(sdkappid = sdkAppId, secretKey = secretKey, expire = expireTime)
        val sig = userSig.genTestUserSig(call.argument<String>("userId")!!);
        result.success(sig)
    }

    private fun login(call: MethodCall, result: Result) {
        TIMManager.getInstance().login(call.argument("identifier"), call.argument("userSig"),
                object : TIMCallBack {
                    override fun onError(code: Int, desc: String) {
                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                        //错误码 code 列表请参见错误码表
                        Log.d(tag, "login failed. code: $code errmsg: $desc")
                        result.error("$code", "login failed", desc)
                    }

                    override fun onSuccess() {
                        Log.d(tag, "login succ")
                        result.success("ok");
                    }
                })
    }


    private fun init(call: MethodCall, result: Result) {

        val sdkAppId: Int = call.argument<Any>("sdkAppId") as Int

        val configs = TUIKit.getConfigs()
        configs.sdkConfig = TIMSdkConfig(sdkAppId)
        configs.customFaceConfig = CustomFaceConfig()
        configs.generalConfig = GeneralConfig()
        TUIKit.init(binding.applicationContext, sdkAppId, configs)
        result.success("ok")
    }


    private fun startMsgList(call: MethodCall, result: Result) {
        val intent = Intent(binding.applicationContext, ConversationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        binding.applicationContext.startActivity(intent)
        result.success("ok")
    }

    private fun startChat(call: MethodCall, result: Result) {
        val intent = Intent(binding.applicationContext, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(binding.applicationContext, intent, Bundle())
        result.success("ok")
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    }
}
