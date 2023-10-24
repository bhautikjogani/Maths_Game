package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.Base.BaseDialog
import GameModule.GameSound
import android.app.Activity
import android.content.Intent
import android.util.SparseArray
import android.view.View
import androidx.databinding.ObservableBoolean
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.SquareRootPlay
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupCommonSelectionBinding

class Popup_SquareRootSelection(
    activity: Activity,
    val variationType: VariationType,
    val binding: PopupCommonSelectionBinding = PopupCommonSelectionBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {

    val isTimePlay: Boolean = variationType == VariationType.SquareRootTimeChallenge

    init {

        binding.frmTime.visibility = if (isTimePlay) View.VISIBLE else View.GONE
        binding.frmDigit.visibility = View.GONE
        binding.btnHard.visibility = View.GONE

        setBinding()
        show()


    }

    private fun setBinding() {
        binding.difficulty = SparseArray<ObservableBoolean>(2).apply {
            put(0, ObservableBoolean(true))
            put(1, ObservableBoolean(true))
        }
        binding.time = 20
        binding.clickHandle = this
        binding.setClickHandle2 {
            GameSound.play()?.clickSound()
            when (it) {
                binding.btnEasy -> changeMode(0)
                binding.btnMedium -> changeMode(1)
                binding.btnHard -> changeMode(2)

                binding.btnTime10 -> binding.time = 10
                binding.btnTime20 -> binding.time = 20
                binding.btnTime30 -> binding.time = 30
                binding.btnTime60 -> binding.time = 60
            }
        }

    }

    private fun changeMode(mode: Int) {
        binding.difficulty!![mode].set(binding.difficulty!![mode].get().not())
        for (i in 0 until binding.difficulty!!.size()) {
            if (binding.difficulty!![i].get()) return
        }
        binding.difficulty!![mode].set(true)
    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnStart -> {
                AdsManager.show()?.InterstitialAdIntervaled(
                    activity = activity,
                    parent = binding.frmParent,
                    adsListener = object : AdsListener() {
                        override fun onAdClose() {
                            super.onAdClose()
                            dismiss()
                            startActivity(
                                Intent(activity, SquareRootPlay::class.java)
                                    .putExtra("variationType", variationType)
                                    .putExtra("isTimePlay", isTimePlay)
                                    .putExtra("time", binding.time)
                                    .putExtra(
                                        "difficulty", booleanArrayOf(
                                            binding.difficulty!![0].get(),
                                            binding.difficulty!![1].get(),
                                        )
                                    )
                            )
                        }
                    }
                )
            }
        }

    }

}
