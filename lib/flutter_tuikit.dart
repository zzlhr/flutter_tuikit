import 'dart:async';

import 'package:flutter/services.dart';

class FlutterTuikit {
  static const MethodChannel _channel = const MethodChannel('plugin.lhrsite.com/flutter_tuikit');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static int SDKAPPID = 0;

  static Future<String> init(int sdkAppId) async {
    SDKAPPID = sdkAppId;
    Map<String, dynamic> params = {"sdkAppId": SDKAPPID};
    final String result = await _channel.invokeMethod("init", params);
    return result;
//    return "already init";
  }

  static void startMsgList() async {
    await _channel.invokeMethod("startMsgList", {});
  }

  static void startChat() async {
    await _channel.invokeMethod("startChat", {});
  }

  static Future<dynamic> login(String identifier, String userSig) async {
    Map<String, dynamic> params = {
      "identifier": identifier,
      "userSig": userSig
    };
    return await _channel.invokeMethod("login", params);
  }

  /// 测试期间获取签名方法, 仅用于测试时，生产环境请谨慎使用，
  /// 原因参照 [客户端计算 UserSig](https://cloud.tencent.com/document/product/269/32688#.E5.AE.A2.E6.88.B7.E7.AB.AF.E8.AE.A1.E7.AE.97-usersig)
  /// [secretKey]密钥在[控制台查看](https://console.cloud.tencent.com/im)
  /// [userId] 用户的唯一标识即可
  static Future<String> genTestSignature(String secretKey, String userId,
      {int expireTime}) async {
    Map<String, dynamic> params = {
      "sdkAppId": SDKAPPID,
      "secretKey": secretKey,
      "expireTime": expireTime == null ? 604800 : expireTime,
      "userId": userId,
    };
    return await _channel.invokeMethod("genTestSignature", params);
  }

  ///开启离线推送
  static void enabledOfflinePush() {
    _channel.invokeMethod("enabledOfflinePush", {});
  }

  ///关闭离线推送
  static void disableOfflinePush() {
    _channel.invokeMethod("disableOfflinePush", {});
  }
}
