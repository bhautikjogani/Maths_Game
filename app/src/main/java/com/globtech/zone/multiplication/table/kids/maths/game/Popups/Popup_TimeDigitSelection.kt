package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import GameModule.GameSound
import android.app.Activity
import android.content.Intent
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.databinding.ObservableBoolean
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupCommonSelectionBinding

class Popup_TimeDigitSelection(
    activity: Activity,
    private val gameType: GameType,
    private val variationType: VariationType,
    private  val passActivity : Class<*>,
    private val binding: PopupCommonSelectionBinding = PopupCommonSelectionBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {

    private val isTimePlay: Boolean = variationType == VariationType.TimeChallenge

    init {

        binding.frmTime.visibility = if (isTimePlay) View.VISIBLE else View.GONE
        binding.frmDigit.visibility = View.VISIBLE
        binding.frmEMH.visibility = View.GONE
        binding.frmType.visibility = View.GONE

        setBinding()
        show()


    }

    private fun setBinding() {
        binding.difficulty = SparseArray<ObservableBoolean>(2).apply {
            put(0, ObservableBoolean(false))
            put(1, ObservableBoolean(true))
            put(2, ObservableBoolean(false))
        }
        binding.time = 20
        binding.digit = SparseArray<ObservableBoolean>(4).apply {
            put(0, ObservableBoolean(true))
            put(1, ObservableBoolean(true))
            put(2, ObservableBoolean(false))
            put(3, ObservableBoolean(false))
        }
        binding.type = 0
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

                binding.btnDigit1 -> changeDigit(0)
                binding.btnDigit2 -> changeDigit(1)
                binding.btnDigit3 -> changeDigit(2)
                binding.btnDigit4 -> changeDigit(3)

                binding.btnAddition -> binding.type = 0
                binding.btnSubtraction -> binding.type = 1
                binding.btnMultiplication -> binding.type = 2
            }
        }

    }

    private fun changeDigit(digit: Int) {
        binding.digit!![digit].set(binding.digit!![digit].get().not())
        for (i in 0 until binding.digit!!.size()) {
            if (binding.digit!![i].get()) return
        }
        binding.digit!![digit].set(true)
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
                dismiss()
                startActivity(
                    Intent(activity, passActivity)
                        .putExtra("gameType", gameType)
                        .putExtra("isTimePlay", isTimePlay)
                        .putExtra("time", binding.time)
                        .putExtra("variationType", variationType)
                        .putExtra("digits", getDigit())
                )
            }
        }

    }

    private fun getDigit(): ArrayList<IntArray> {
        val array = arrayListOf<IntArray>()

        binding.digit!!.forEach { index, enable ->
            if (enable.get()) array.add(intArrayOf(index + 1, index + 1))
        }

        return array
    }

}
