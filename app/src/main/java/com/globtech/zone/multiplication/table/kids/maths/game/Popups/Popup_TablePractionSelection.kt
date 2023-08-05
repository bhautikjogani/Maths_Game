package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import GameModule.GameSound
import android.app.Activity
import android.content.Intent
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.DigitPlay
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupTablePracticeSelectionBinding

class Popup_TablePractionSelection(
    activity: Activity,
    val variationType: VariationType,
    val binding: PopupTablePracticeSelectionBinding = PopupTablePracticeSelectionBinding.inflate(
        activity.layoutInflater
    )
) : BaseDialog(activity, binding.root), View.OnClickListener {

    init {

        binding.difficulty = 0
        binding.clickHandle = this
        binding.setClickHandle2 {
            GameSound.play()?.clickSound()
            when (it) {
                binding.btnSequential -> binding.difficulty = 0
                binding.btnRadom -> binding.difficulty = 1
            }
        }

        show()


    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnStart -> {
                dismiss()
                activity.startActivity(
                    Intent(activity, DigitPlay::class.java)
                        .putExtra("gameType", GameType.Multiplication)
                        .putExtra("variationType", variationType)
                        .putExtra("isSequential", binding.difficulty == 0)
                        .putExtra("digits", arrayListOf(intArrayOf(1, 30)))
                )
                activity.finish()
            }
        }

    }

}
