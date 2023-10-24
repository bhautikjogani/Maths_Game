package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import GameModule.Utils
import android.app.Activity
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupSettingBinding

class Popup_Setting(
    activity: Activity,
    val isFromPlaying: Boolean = true,
    val binding: PopupSettingBinding = PopupSettingBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {

    init {

        if(isFromPlaying){
            binding.frmNotification.visibility = View.GONE
            binding.btnFeedback.visibility = View.GONE
            binding.btnPrivacyPolicy.visibility = View.GONE
        }

        binding.isMusic = GamePreference.getBoolean(PrefKey.isMusic)
        binding.isSound = GamePreference.getBoolean(PrefKey.isSound)
        binding.isVibration = GamePreference.getBoolean(PrefKey.isVibration)
        binding.isNotification = GamePreference.getBoolean(PrefKey.isNotify)
        binding.clickHandle = this
        binding.setClickHandle2 {
            GameSound.play()?.clickSound()
            when (it) {
                binding.btnMusic -> {
                    binding.isMusic = binding.isMusic.not()
                    GamePreference.setBoolean(PrefKey.isMusic, binding.isMusic)
                    if (binding.isMusic) GameSound.play()?.startMusic()
                    else GameSound.play()?.pauseMusic()
                }

                binding.btnSound -> {
                    binding.isSound = binding.isSound.not()
                    GamePreference.setBoolean(PrefKey.isSound, binding.isSound)
                }

                binding.btnVibration -> {
                    binding.isVibration = binding.isVibration.not()
                    GamePreference.setBoolean(PrefKey.isVibration, binding.isVibration)
                }

                binding.btnNotification -> {
                    binding.isNotification = binding.isNotification.not()
                    GamePreference.setBoolean(PrefKey.isNotify, binding.isNotification)
                }
            }
        }

        show()

    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnFeedback -> Utils.showFeedback(activity)
            binding.btnPrivacyPolicy -> Utils.showPrivacyPolicy(activity)
        }

    }

}
