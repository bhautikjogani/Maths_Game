package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import android.app.Activity
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupDrawBinding

class Popup_DrawView(
    activity: Activity,
    val binding: PopupDrawBinding = PopupDrawBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {


    init {

        binding.isDraw = true
        binding.clickHandle = this

//        binding.btnDrawErase.setColorFilter(Color.BLACK)

        binding.slider.setLabelFormatter { value: Float ->
            value.toInt().toString()
        }

        binding.slider.addOnChangeListener { rangeSlider, value, fromUser ->
            binding.drawView.setSize(value)
        }

        show()

    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnDrawErase -> {
                binding.isDraw = binding.isDraw.not()
                binding.drawView.setPaintDraw(binding.isDraw)
                binding.slider.value =
                    if (binding.isDraw) binding.drawView.getPaintSize()
                    else binding.drawView.getClearSize()
            }
        }

    }

}
