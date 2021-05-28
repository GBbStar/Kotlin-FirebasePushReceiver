package org.techtown.mypushreceiver.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.techtown.mypushreceiver.MainActivity
import org.techtown.mypushreceiver.R

class FirebaseMessaging : FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val type = p0.data["type"]
                ?.let{
                    NotificationType.valueOf(it)
                }
        type ?: return
        val title = p0.data["title"]
        title ?: return
        val content = p0.data["content"]
        content ?: return

        createNotificationChannel()
        createNotification(type, title, content)

        NotificationManagerCompat.from(this)
                .notify(type.id, createNotification(type, title, content))
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = CHANNEL_DESCRIPTION

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

    }

    private fun createNotification(type:NotificationType, title:String, content:String ):Notification{
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)

        val mNotificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        when (type){
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                mNotificationBuilder.setStyle(
                        NotificationCompat.BigTextStyle().
                            bigText(
                                "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺️ 😊 😇 " +
                                        "🙂 🙃 😉 😌 😍 🥰 😘 😗 😙 😚 😋 😛 " +
                                        "😝 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩 🥳 😏 😒 " +
                                        "😞 😔 😟 😕 🙁 ☹️ 😣 😖 😫 😩 🥺 😢 " +
                                        "😭 😤 😠 😡 🤬 🤯 😳 🥵 🥶 😱 😨 😰 " +
                                        "😥 😓 🤗 🤔 🤭 🤫 🤥 😶 😐 😑 😬 🙄 " +
                                        "😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 🥴 " +
                                        "🤢 🤮 🤧 😷 🤒 🤕"
                                )
                )
            }
            NotificationType.CUSTOM -> {
                mNotificationBuilder
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(
                                RemoteViews(
                                        packageName,
                                        R.layout.view_custom_notification
                                ).apply {
                                    setTextViewText(R.id.title, title)
                                    setTextViewText(R.id.message, content)
                                }
                        )
            }
        }

        return mNotificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "푸시 알람 프로젝트 채널"
        private const val CHANNEL_DESCRIPTION = "푸시 알람 프로젝트에서 만들어진 채널입니다."
        private const val CHANNEL_ID = "Channel ID"
    }
}