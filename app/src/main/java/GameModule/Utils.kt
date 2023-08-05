package GameModule

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.BuildConfig
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

object Utils {

    private val TAG = MyGame.MainTAG + javaClass.simpleName

    fun coinFormat(number: Number, isComaFormat: Boolean = false): String {
        if (isComaFormat && number.toLong() < 10000000) {
            return DecimalFormat("#,###,###,###").format(number.toDouble())
        }

        val value = number.toLong()
        val type: Int
        val suffix: String
        val result: Double

        when {
            abs(value) >= 1000000000000L -> {
                val d2: Double = value.toDouble()
                java.lang.Double.isNaN(d2)
                result = d2 / 1.0E12
                suffix = "T"
                type = 3
            }

            abs(value) >= 1000000000 -> {
                val d3: Double = value.toDouble()
                java.lang.Double.isNaN(d3)
                result = d3 / 1.0E9
                suffix = "B"
                type = 2
            }

            abs(value) >= 1000000 -> {
                val d4: Double = value.toDouble()
                java.lang.Double.isNaN(d4)
                result = d4 / 1000000.0
                suffix = "M"
                type = 1
            }

            abs(value) >= 1000 -> {
                val d5: Double = value.toDouble()
                java.lang.Double.isNaN(d5)
                result = d5 / 1000.0
                suffix = "K"
                type = 0
            }

            else -> {
                return numberFormat(value)
            }
        }

        val d6 = result * 10.0
        return if (d6 % 10.0 == 0.0) {
            (d6 / 10.0).toInt().toString() + "" + suffix.uppercase(Locale.getDefault())
        } else {
            roundTo2Decimals(value.toDouble(), type) + "" + suffix.uppercase(Locale.getDefault())
        }
    }

    private fun numberFormat(value: Long): String {
        return if (value == 0L) "0"
        else NumberFormat.getNumberInstance(Locale.US).format(value)
    }

    private fun roundTo2Decimals(value: Double, type: Int): String {
        val dotBeforeValue: Int
        val modulesValue: Double
        when (type) {
            0 -> {
                var temp = 100
                if (abs(value) >= 100000.0) {
                    temp = 1000
                }
                dotBeforeValue = (value / 1000.0).toInt()
                modulesValue = (value % 1000.0) / (1000 / temp.toLong()).toDouble()
            }

            1 -> {
                dotBeforeValue = (value / 1000000.0).toInt()
                modulesValue = (value % 1000000.0) / 10000.0
            }

            2 -> {
                dotBeforeValue = (value / 1.0E9).toInt()
                modulesValue = (value % 1.0E9) / 1.0E7
            }

            3 -> {
                dotBeforeValue = (value / 1.0E12).toInt()
                modulesValue = (value % 1.0E12) / 1.0E10
            }

            else -> {
                return "0.0"
            }
        }
        return if (modulesValue.toInt() == 0) "$dotBeforeValue"
        else "${dotBeforeValue}.${(modulesValue.toInt() / 10)}"
    }


    /****************************************/


    fun doOnMoreGame(activity: Activity) {
        val uri =
            Uri.parse("https://play.google.com/store/apps/dev?id=${Constants.PlayStoreAccount}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            activity.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showRateUs(activity: Activity) {
        GamePreference.setBoolean(PrefKey.isRate, true)
        val uri = Uri.parse("market://details?id=${activity.packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
        }
        try {
            activity.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=${activity.packageName}")
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showFeedback(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                val intent = Intent("android.intent.action.SEND")
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.FeedbackMail))
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    activity.resources.getString(R.string.app_name) + " v-" + BuildConfig.VERSION_CODE
                )
                intent.putExtra(Intent.EXTRA_TEXT, "")
                activity.startActivity(Intent.createChooser(intent, "Send mail..."))
            } else {
                val intent = Intent("android.intent.action.SENDTO")
                intent.data = Uri.parse(
                    "mailto:${Constants.FeedbackMail}?cc=&subject=" + Uri.encode(
                        activity.resources.getString(R.string.app_name) + " v-" + BuildConfig.VERSION_CODE
                    ) + "&body="
                )
                activity.startActivity(intent)
            }
        } catch (unused: ActivityNotFoundException) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun showAskForRating(activity: Activity, rateMessage: String) {
        if (GamePreference.getBoolean(PrefKey.isRate)) return
        GamePreference.addInteger(PrefKey.RateCounter, 1)
        if (GamePreference.getInteger(PrefKey.RateCounter) != 10) return
        GamePreference.setInteger(PrefKey.RateCounter, 0)

        PopupAlert(activity)
            .setIcon(R.drawable.ic_rateus_black)
            .setTitle(activity.resources.getString(R.string.RateUsTitle))
            .setMessage(rateMessage)
            .setPositiveButton(
                activity.resources.getString(R.string.RateUsNow),
                object : ActionEndCallback {
                    override fun onEnd() {
                        GamePreference.setBoolean(PrefKey.isRate, true)
                        showRateUs(activity)
                    }
                })
            .setNeutralButton(activity.resources.getString(R.string.RateUsLater), null)
            .show()
    }

    fun showPrivacyPolicy(activity: Activity) {
        try {
            activity.startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse(Constants.PrivacyPolicyLink)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Web app not found.", Toast.LENGTH_SHORT).show()
        }
    }

    fun showShareGame(
        activity: Activity,
        appName: String,
        shareBody: String,
        activityResultLauncher: ActivityResultLauncher<Intent>? = null
    ) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            appName
        )
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT, shareBody + "\n" +
                    "https://play.google.com/store/apps/details?id=" + activity.packageName
        )

        activityResultLauncher?.let {
            it.launch(Intent.createChooser(sharingIntent, "Share via"))
        } ?: kotlin.run {
            activity.startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

    }

    fun showRewardedAdPopup(activity: Activity, msg: String, callback: ActionEndCallback?) {
        PopupAlert(activity)
            .setTitle(activity.resources.getString(R.string.WatchAd))
            .setMessage(msg)
            .setIcon(R.drawable.ic_ad)
            .setPositiveButton(activity.resources.getString(R.string.WatchAd), callback)
            .setNeutralButton(activity.resources.getString(R.string.No), null)
            .show()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

}
