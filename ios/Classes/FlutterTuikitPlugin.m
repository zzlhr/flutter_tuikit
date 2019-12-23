#import "FlutterTuikitPlugin.h"
#import "TUIKit.h"
#import "TUIConversationListController.h"
#import "GenerateTestUserSig.h"

@implementation FlutterTuikitPlugin{
    
}
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"plugin.lhrsite.com/flutter_tuikit"
                                     binaryMessenger:[registrar messenger]];
    FlutterTuikitPlugin* instance = [[FlutterTuikitPlugin alloc] init];

    [registrar addMethodCallDelegate:instance channel:channel];
    
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSLog(@"%@", [@"handleMethodCall........" stringByAppendingString:call.method]);
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
        return;
    }
    
    
    if ([@"init" isEqualToString:call.method]) {
        self.sdkAppId = [[call.arguments objectForKey:@"sdkAppId"] intValue];
        [[TUIKit sharedInstance] setupWithAppId:self.sdkAppId];
        result(@"ok");
        return;
    }
    
    if ([@"genTestSignature" isEqualToString:call.method]) {
        [self genTestSignature: call result:result];
        return;
    }
    
    
    if ([@"startMsgList" isEqualToString:call.method]) {
        [self startMsgList: call result:result];
        return;
    }
    
    if ([@"enabledOfflinePush" isEqualToString:call.method]) {
        // TODO: 离线消息推送.
        result(@"ok");
        return;
    }
    
    if ([@"disableOfflinePush" isEqualToString:call.method]) {
        // TODO: 离线消息推送.
        result(@"ok");
    }
    
    if ([@"login" isEqualToString:call.method]) {
        [self login: call result:result];
        return;
    }
    
    result(FlutterMethodNotImplemented);
    
}


-(void) login:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSString *name = [call.arguments objectForKey:@"identifier"];
    NSString *userSig = [call.arguments objectForKey:@"userSig"];
    TIMLoginParam * login_param = [[TIMLoginParam alloc ]init];
    // identifier 为用户名
    login_param.identifier = name;
    //userSig 为用户登录凭证
    login_param.userSig = userSig;
    //appidAt3rd App 用户使用 OAuth 授权体系分配的 Appid，在私有帐号情况下，填写与 SDKAppID 一样
    login_param.appidAt3rd = @(self.sdkAppId).stringValue;
    [[TIMManager sharedInstance] login: login_param succ:^(){
        result(@"ok");
    } fail:^(int code, NSString * err) {
        result([@"Login Failed: " stringByAppendingFormat:@(code).stringValue, err]);
    }];
}

- (void) genTestSignature:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSString *userId = [call.arguments objectForKey:@"userId"];
    NSString *sign = [GenerateTestUserSig genTestUserSig: userId];
    result(sign);
}

- (void) startMsgList:(FlutterMethodCall*)call result:(FlutterResult)result{
    TUIConversationListController *vc = [[TUIConversationListController alloc] init];
    vc.delegate = self;
    vc.title = @"聊天列表";
    [self push:vc];
    result(@"ok");
}
- (void)conversationListController:(TUIConversationListController *)conversationController didSelectConversation:(TUIConversationCell *)conversation
{
    NSLog(@"会话列表点击事件，通常是打开聊天界面");
    // 会话列表点击事件，通常是打开聊天界面
    TIMConversation *conv = [[TIMManager sharedInstance] getConversation:conversation.convData.convType receiver:conversation.convData.convId];
    TUIChatController *vc = [[TUIChatController alloc] initWithConversation:conv];
    vc.title = conversation.convData.convId;
    [self push:vc];
}

- (void) push:(UIViewController *) vc{
    if (self.navigationController == NULL) {
        UIViewController *rootViewController = [[[UIApplication sharedApplication] keyWindow] rootViewController];
        if ([rootViewController isKindOfClass:[UINavigationController class]]) {
            self.navigationController =(UINavigationController*) rootViewController;
            [(UINavigationController*) rootViewController pushViewController:vc animated:true];
        }else{
            self.navigationController = [[UINavigationController alloc] initWithRootViewController:vc];
            self.navigationController.modalPresentationStyle = UIModalPresentationFullScreen;// 设置全屏显示
            [rootViewController presentViewController:self.navigationController animated:true completion:nil];
        }
        return;
    }
    
    [self.navigationController pushViewController:vc animated:true];
}
@end
