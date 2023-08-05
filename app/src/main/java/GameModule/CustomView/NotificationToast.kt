//package GameModule.CustomView
//
//import GameModule.ScreenUtil
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.Toast
//import com.globtech.zone.multiplication.table.kids.maths.game.R
//import java.util.*
//
//
//@SuppressLint("MissingInflatedId")
//class NotificationToast(val activity: Activity) {
//
//
//    init {
//
//        val inflater: LayoutInflater = activity.layoutInflater
//        val layout: View = inflater.inflate(
//            R.layout.layout_notification_toast, null
//        )
//        val toast = Toast(activity)
//        toast.setGravity(Gravity.TOP, 0, 0)
//        toast.duration = Toast.LENGTH_LONG
//        toast.view = layout
//        toast.show()
//
//        layout.scaleX = 0f
//        layout.scaleY = 0f
//        layout.pivotX = ScreenUtil.screenWidth / 2f
//        layout.animate().scaleY(1f).scaleX(1f).setDuration(500)
//            .withEndAction {
//                layout.animate().scaleY(0f).scaleX(0f).alpha(0f).setStartDelay(2200)
//                    .setDuration(500).start()
//            }.start()
//
//    }
//
//}