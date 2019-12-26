package com.lhrsite.flutter_tuikit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo


class ChatActivity : AppCompatActivity() {
    private lateinit var chatLayout: ChatLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initView()
    }


    private fun initView() {
        // 从布局文件中获取聊天面板
        chatLayout = findViewById(R.id.chat_layout)
        // 单聊面板的默认 UI 和交互初始化
        chatLayout.initDefault()
        // 传入 ChatInfo 的实例，这个实例必须包含必要的聊天信息，一般从调用方传入
        val mChatInfo: ChatInfo = this.intent.extras!!.getSerializable(Constants.CHAT_INFO) as ChatInfo
//        mChatInfo.chatName = "测试一下"
//        mChatInfo.id = "2388399752"
//        mChatInfo.isTopChat = true
//        mChatInfo.type = TIMConversationType.C2C
        chatLayout.setChatInfo(mChatInfo)
    }
}
