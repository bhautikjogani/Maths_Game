package Utility

import GameModule.AdsUtiles.AdsManager
import GameModule.GamePreference
import GameModule.PrefKey
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.SplashScreen
import com.globtech.zone.multiplication.table.kids.maths.game.R
import kotlin.random.Random

class GameNotification : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!GamePreference.getBoolean(PrefKey.isNotify)) return

        if (AdsManager.show() != null) {
            mNotificationManager.cancelAll()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                context.getString(R.string.channel_id),
                "General Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "General Notifications send by system"
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        val mPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, SplashScreen::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, Intent(context, SplashScreen::class.java), PendingIntent.FLAG_IMMUTABLE
            )
        }

        val appName = context.getString(R.string.app_name)

        val notifyText = arrayOf(
            "🌟 Attention Math Wizards! 🌟",
            "🔢 Gear up for an exhilarating maths challenge! 🔢",
            "🏆 Prepare to put your number-crunching skills to the test and train your brain with our Maths Game! 🧠💪",
            "🎉 Prizes: Fabulous rewards await the top performers! 🎁🏅",
            "📝 Mark your calendars and get ready to conquer the world of numbers! 🚀",
            "🔔 Stay tuned for more details and brace yourself for an epic math showdown! 🎯🔥",
            "Let the countdown begin! ⏳⚡️",
            "🌟 Best of luck, and may the digits be forever in your favor! 🌟",
            "Don't miss out on this exciting opportunity to showcase your mathematical prowess! 🤩💯",
            "Enroll now and be prepared to tackle challenging equations, mind-bending puzzles, and thrilling problem-solving tasks! 🧩🔍💡",
            "Sharpen your mental calculation skills, unleash your mathematical genius, and secure your spot among the elite math champions! 🧠💥👑",
            "Spread the word and invite your fellow maths enthusiasts! Together, let's embark on an exciting journey into the realm of numbers and logic! 🤝🌐🎢",
            "Remember, in the world of mathematics, you're not just a player; you're a problem-solving hero! 💪🦸‍♂️🦸‍♀️",
        )
        val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(appName)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mPendingIntent)
        if (Random.nextBoolean()) {
            builder.setContentText(notifyText.random())
            builder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.banner))
            )
        } else {
            builder.setStyle(
                NotificationCompat.BigTextStyle().bigText(notifyText.random())
            )
        }
        mNotificationManager.notify(0, builder.build())
    }

}

