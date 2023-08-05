package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import android.app.Activity
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupConformationBinding

class Popup_Conformation(
    activity: Activity,
    val binding: PopupConformationBinding = PopupConformationBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root) {

    init {

        binding.btnClose.visibility = View.GONE
        binding.btnLeft.visibility = View.GONE
        binding.btnRight.visibility = View.GONE

        show()

    }

    fun setDialogTitle(title: String): Popup_Conformation {
        binding.tvTitle.text = title
        return this
    }

    fun setDialogMsg(msg: String): Popup_Conformation {
        binding.tvMsg.text = msg
        return this
    }

    fun setButtonClose(callback: (() -> Unit)? = null): Popup_Conformation {
        binding.btnClose.visibility = View.VISIBLE
        binding.btnClose.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            dismiss()
            callback?.invoke()
        }
        return this
    }

    fun setButtonLeft(text: String, callback: (() -> Unit)? = null): Popup_Conformation {
        binding.btnLeft.visibility = View.VISIBLE
        binding.btnLeft.text = text
        binding.btnLeft.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            dismiss()
            callback?.invoke()
        }
        return this
    }

    fun setButtonRight(text: String, callback: (() -> Unit)? = null): Popup_Conformation {
        binding.btnRight.visibility = View.VISIBLE
        binding.btnRight.text = text
        binding.btnRight.setOnClickListener {
            if (isDoubleClick()) return@setOnClickListener
            dismiss()
            callback?.invoke()
        }
        return this
    }

}
