package GameModule

import android.app.Activity
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame


class LeaderBoardLogin(var activity: Activity) {

    val TAG: String = MyGame.MainTAG + javaClass.simpleName

    companion object {
        const val LOGIN_CODE = 1000
        const val RC_LEADERBOARD_UI = 9004
        const val RC_ACHIEVEMENT_UI = 9003
    }

//    var googleSignIn: GoogleSignInAccount? = null

    fun googleUI(activity: Activity) {
//        GoogleSignIn.getLastSignedInAccount(activity)?.let {
//            Games.getGamesClient(activity, it).apply {
//                setViewForPopups(activity.findViewById(android.R.id.content))
//                setGravityForPopups(Gravity.CENTER_HORIZONTAL)
//            }
//        }
    }

    fun isSignedIn(): Boolean {
//        return GoogleSignIn.getLastSignedInAccount(activity) != null
        return false
    }

    fun startSignInIntent(isFirstTimeSignIn: Boolean = false) {
        /*if (isSignedIn()) return
        Log.d(TAG, "startSignInIntent:   isFirstTimeSignIn   --->  $isFirstTimeSignIn")
        val client = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN)
        if (isFirstTimeSignIn) {
            client.silentSignIn().addOnCompleteListener {
                Log.d(TAG, "startSignInIntent:   --->  ${it.isSuccessful}")
                if (it.isSuccessful) {
                    googleSignIn = it.result
                } else
                    activity.startActivityForResult(client.signInIntent, LOGIN_CODE)
            }
        } else {
            activity.startActivityForResult(client.signInIntent, LOGIN_CODE)
        }*/
    }


    //region: Leaderboard
    fun submitScore(score: Long) {
        /*if (!isSignedIn()) return
        Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
            .submitScore(activity.resources.getString(R.string.ID_LeaderboardID), score)*/
    }

    fun showLeaderboard() {
        if (!isSignedIn()) {
            startSignInIntent()
            return
        }

        /*Games.getLeaderboardsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
            .getLeaderboardIntent(activity.resources.getString(R.string.ID_LeaderboardID))
            .addOnSuccessListener {
                activity.startActivityForResult(it, RC_LEADERBOARD_UI)
            }*/

    }
    //endregion


    //region: Achievement
    fun showAchievements() {
        /*GoogleSignIn.getLastSignedInAccount(activity)?.let { it ->
            Games.getAchievementsClient(activity, it)
                .achievementsIntent
                .addOnSuccessListener {
                    activity.startActivityForResult(it, RC_ACHIEVEMENT_UI)
                }
        }*/
    }

    fun unlockAchievement(achievementID: String) {
        /*GoogleSignIn.getLastSignedInAccount(activity)?.let { it ->
            Games.getAchievementsClient(activity, it)
                .unlockImmediate(achievementID)
                .addOnSuccessListener {
                    Log.d(TAG, "unlockAchievement:  onSuccessListener")
                }
                .addOnFailureListener {
                    Log.d(TAG, "unlockAchievement: ${it.message}")
                }
        }*/
    }

    fun incrementAchievement(achievementID: String, increment: Int) {
        /*GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Games.getAchievementsClient(activity, it)
                .incrementImmediate(achievementID, increment)
                .addOnSuccessListener {
                    Log.d(TAG, "incrementAchievement: $it")
                }
                .addOnFailureListener {
                    Log.d(TAG, "incrementAchievement: ${it.message}")
                }
        }*/
    }

    //endregion


}
