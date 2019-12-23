package com.lhrsite.flutter_tuikit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tencent.imsdk.TIMConversationType
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo
import java.lang.Exception


class ConversationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_conversation)
            // 从布局文件中获取会话列表面板
            val conversationLayout = findViewById<ConversationLayout>(R.id.conversation_layout)
            // 初始化聊天面板
            conversationLayout.initDefault()

            // 消息点击事件
            val conversationItemClick =
                    ConversationListLayout.OnItemClickListener { view: View, i: Int, conversationInfo: ConversationInfo ->
                        startChatActivity(conversationInfo)
                    }
            // 设置消息点击事件
            conversationLayout.conversationList.setOnItemClickListener(conversationItemClick)
        } catch (e: Exception) {
            Log.e(FlutterTuikitPlugin.tag, e.message)
        }


    }

    private fun startChatActivity(conversationInfo: ConversationInfo) {
        val chatInfo = ChatInfo()
        chatInfo.type = if (conversationInfo.isGroup) TIMConversationType.Group else TIMConversationType.C2C
        chatInfo.id = conversationInfo.id
        chatInfo.chatName = conversationInfo.title
        val intent = Intent(FlutterTuikitPlugin.binding.applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.CHAT_INFO, chatInfo)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        FlutterTuikitPlugin.binding.applicationContext.startActivity(intent)
    }
}
