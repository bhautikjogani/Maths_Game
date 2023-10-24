package com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings

import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.Base.BaseActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityTableLeaningBinding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemTableBinding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemTableNumberBinding

class TableLeaning : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityTableLeaningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityTableLeaningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickHandler = this

        binding.tableNumberRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.tableNumberRecyclerView.adapter = TableNumberAdapter(this) {
            (binding.tableRecyclerView.adapter as TableAdapter).tableNumber = it
            binding.tableRecyclerView.adapter?.notifyDataSetChanged()
        }

        binding.tableRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.tableRecyclerView.adapter = TableAdapter(this)

    }

    override fun onClick(view: View?) {
        if (isDoubleClick()) return
        when (view) {
            binding.btnHome -> {

                AdsManager.show()?.InterstitialAd(
                    activity = this@TableLeaning,
                    adsListener = object : AdsListener() {
                        override fun onAdClose() {
                            super.onAdClose()

                            finish()

                        }
                    }
                )

            }
        }
    }

    class TableAdapter(val activity: BaseActivity) :
        RecyclerView.Adapter<TableAdapter.ViewHolder>() {

        var tableNumber = 1

        inner class ViewHolder(val binding: ItemTableBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bindView(position: Int) {
                binding.tvNumber.text =
                    "$tableNumber x ${getString(position + 1)}" +
                            " = ${getString3S(tableNumber, tableNumber * (position + 1))}"
                binding.tvNumber.alpha = 0f
                binding.tvNumber.animate().alpha(1f).setDuration(400).setStartDelay(400L * position)
                    .start()
            }

            private fun getString3S(tableIndex: Int, tableNumber: Int): String {
                val retValue = tableNumber.toString()
                return if (tableIndex < 9) {
                    if (retValue.length == 2) retValue
                    else " $retValue"
                } else {
                    if (retValue.length == 3) retValue
                    else if (retValue.length == 2) " $retValue"
                    else "  $retValue"
                }
            }

            private fun getString(tableNumber: Int): String {
                val retValue = tableNumber.toString()
                return if (retValue.length == 2) retValue else " $retValue"
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemTableBinding.inflate(activity.layoutInflater, parent, false))
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }

    }

    class TableNumberAdapter(val activity: BaseActivity, val callback: (Int) -> Unit) :
        RecyclerView.Adapter<TableNumberAdapter.ViewHolder>() {

        inner class ViewHolder(private val itemBinding: ItemTableNumberBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun bindView(position: Int) {
                itemBinding.tvNumber.text = (position + 1).toString()
                itemBinding.tvNumber.setOnClickListener {
                    callback.invoke(position + 1)
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemTableNumberBinding.inflate(
                    activity.layoutInflater,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return 30
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }

    }

}
