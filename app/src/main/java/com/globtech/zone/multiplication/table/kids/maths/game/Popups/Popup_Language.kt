package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.Languages
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemLanguageBinding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupLanguagesBinding

class Popup_Language(
    activity: Activity,
    val binding: PopupLanguagesBinding = PopupLanguagesBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener {


    init {

        binding.clickHandle = this

        setAdapter()
        show()

    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = LanguageAdapter(activity, Languages.values())
    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnDone -> dismiss()
        }

    }

    class LanguageAdapter(val activity: Activity, val langList: Array<Languages>) :
        RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

        inner class ViewHolder(private val itemBinding: ItemLanguageBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bindView(position: Int) {
                val lang = langList[position]
                itemBinding.tvText.text = lang.name
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemLanguageBinding.inflate(activity.layoutInflater, parent, false))
        }

        override fun getItemCount(): Int {
            return langList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }

    }

}
