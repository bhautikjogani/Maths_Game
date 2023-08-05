package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import android.app.Activity
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupResultBinding

class Popup_Result(
    activity: Activity,
    val rightAnswer: Int,
    val wrongAnswer: Int,
    val avgTime: Int,
    val retryCallback: () -> Unit,
    val binding: PopupResultBinding = PopupResultBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {


    init {

        setBinding()
        show()

    }

    private fun setBinding() {
        binding.rightAnswer = rightAnswer
        binding.wrongAnswer = wrongAnswer
        binding.avgTime = avgTime
        binding.clickHandle = this

        binding.tvTitle.text = when {
            rightAnswer > 7 -> "EXCELLENT!"
            rightAnswer > 5 -> "GOOD!"
            rightAnswer > 3 -> "AVERAGE!"
            rightAnswer >= 1 -> "POOR"
            else -> "VERY POOR"
        }

        binding.btnRetry.visibility = if (rightAnswer > 5) View.GONE else View.VISIBLE

    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnStart -> {
                dismiss()
                activity.finish()
            }

            binding.btnRetry -> {
                dismiss()
                retryCallback.invoke()
            }
        }

    }

}
