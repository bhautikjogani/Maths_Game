package GameModule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame


class GameSound {

    private val TAG: String = MyGame.MainTAG + javaClass.simpleName

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: GameSound? = null
        fun play() = instance
    }

    private lateinit var activity: Activity
    private lateinit var soundPool: SoundPool
    private var mediaPlayer: MediaPlayer? = null
    private var clickSound: Int = 0
    private var tapSoundsList = ArrayList<Int>()
    private lateinit var bgRawList: ArrayList<Int>
    private lateinit var rawList: ArrayList<Int>
    private lateinit var rawLoadList: ArrayList<Int>

    fun initialize(
        activity: Activity,
        clickSound: Int,
        backgroundMusics: ArrayList<Int> = ArrayList(),
        soundList: ArrayList<Int> = ArrayList(),
        tapSoundsList: ArrayList<Int> = ArrayList()
    ) {
        instance = this
        this@GameSound.activity = activity
        this@GameSound.bgRawList = backgroundMusics
        this@GameSound.rawList = soundList
        rawLoadList = ArrayList(rawList.size)

        if (bgRawList.isNotEmpty())
            playNextMusic()

        val MAX_STREAM = 10
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(MAX_STREAM).build()
        } else {
            SoundPool(MAX_STREAM, AudioManager.STREAM_MUSIC, 1)
        }

        rawList.forEach {
            rawLoadList.add(soundPool.load(activity, it, 1))
        }
        tapSoundsList.forEach {
            this@GameSound.tapSoundsList.add(soundPool.load(activity, it, 1))
        }

        if (clickSound != 0) {
            this@GameSound.clickSound = soundPool.load(activity, clickSound, 1)
        }

    }

    private var isPlayingOpen: Boolean = false

    fun isPlayingOpen(isPlayingOpen: Boolean) {
        this.isPlayingOpen = isPlayingOpen
        setVolume()
    }

    fun setVolume() {
        mediaPlayer?.setVolume(
            if (isPlayingOpen) .1f else .3f,
            if (isPlayingOpen) .1f else .3f
        )
    }

    private fun playNextMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.reset()
                    it.release()
                }
            }

            mediaPlayer = MediaPlayer.create(activity, bgRawList.random())
            setVolume()

            mediaPlayer?.setOnCompletionListener {
                playNextMusic()
            }

            startMusic()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startMusic() {
        if (GamePreference.getBoolean(PrefKey.isMusic))
            mediaPlayer?.start()
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true)
            mediaPlayer?.pause()
    }

    fun tapSound() {
        try {
            if (GamePreference.getBoolean(PrefKey.isSound) && tapSoundsList.isNotEmpty())
                soundPool.play(
                    tapSoundsList.random(),
                    0.8f,
                    0.8f,
                    1,
                    0,
                    1f
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sound(resourceID: Int, volume: Float = .8f, rate: Float = 1f) {
        try {
            if (GamePreference.getBoolean(PrefKey.isSound))
                soundPool.play(
                    rawLoadList[rawList.indexOf(resourceID)],
                    volume,
                    volume,
                    1,
                    0,
                    rate
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickSound() {
        try {
            if (GamePreference.getBoolean(PrefKey.isSound))
                soundPool.play(
                    clickSound,
                    0.8f,
                    0.8f,
                    1,
                    0,
                    1f
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopMusic() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun vibration() {
        if (GamePreference.getBoolean(PrefKey.isVibration).not()) return

        (activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                it.vibrate(500)
            }
        }

    }

    fun destroy() {
        stopMusic()
        instance = null
    }

}
