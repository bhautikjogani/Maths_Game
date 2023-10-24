package com.globtech.zone.multiplication.table.kids.maths.game.Activities

import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.Base.BaseActivity
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.PrefKey
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.numOfQuestion
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityReportBinding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemReport2Binding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemReportBinding

class ReportActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clickHandler = this

        setData()
        setAdapter()

    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this@ReportActivity, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = ReportAdapter(this@ReportActivity, getReportModelList())
    }

    private fun getReportModelList(): ArrayList<ReportModel> {
        return ArrayList<ReportModel>().apply {
//            add(
//                ReportModel(
//                    GameType.MixedOperations,
//                    R.drawable.ic_hm_plus,
//                    Color.parseColor("#000000")
//                )
//            )
            add(ReportModel(GameType.Addition, R.drawable.ic_hm_plus, Color.parseColor("#4F7740")))
            add(
                ReportModel(
                    GameType.Subtraction,
                    R.drawable.ic_hm_subtraction,
                    Color.parseColor("#681D16")
                )
            )
            add(
                ReportModel(
                    GameType.Multiplication,
                    R.drawable.ic_hm_multiplication,
                    Color.parseColor("#713D11")
                )
            )
            add(
                ReportModel(
                    GameType.Division,
                    R.drawable.ic_hm_division,
                    Color.parseColor("#01528A")
                )
            )
            add(
                ReportModel(
                    GameType.SquareRoot,
                    R.drawable.ic_hm_square_root,
                    Color.parseColor("#2D5335")
                )
            )
            add(
                ReportModel(
                    GameType.Decimal,
                    R.drawable.ic_hm_decimal,
                    Color.parseColor("#261A39")
                )
            )
        }
    }

    private fun setData() {

    }

    private fun doOnFinish() {
        AdsManager.show()?.InterstitialAdIntervaled(
            activity = this@ReportActivity,
            adsListener = object : AdsListener() {
                override fun onAdClose() {
                    super.onAdClose()
                    finish()
                }
            }
        )
    }

    override fun onBackPressed() {
        doOnFinish()
    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnHome -> {
                doOnFinish()
            }
        }

    }

    inner class ReportAdapter(val activity: BaseActivity, val list: ArrayList<ReportModel>) :
        RecyclerView.Adapter<ReportAdapter.HolderClass>() {

        inner class HolderClass(private val itemBinding: ItemReportBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {
            fun bindView(position: Int) {
                val reportModel = list[position]
                itemBinding.tvTitle.text = reportModel.getTitle()
                itemBinding.tvPlayed.text = reportModel.getAllTotalPlayed().toString()

                itemBinding.ivIcon.setImageResource(reportModel.icon)
                itemBinding.tvTitle.setTextColor(reportModel.textColor)

                itemBinding.ivIcon.scaleX = if (position <= 2) .9f else 1.1f
                itemBinding.ivIcon.scaleY = if (position <= 2) .9f else 1.1f

                if (reportModel.isExpanded) {
                    val lp = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    reportModel.list.forEach {
                        val b = ItemReport2Binding.inflate(activity.layoutInflater)
                        b.tvTitle.text = it.name
                        b.tvPlayed.text = reportModel.getTotalPlayed(it).toString()
                        b.tvRightAns.text = "${reportModel.getRightAndRation(it)}%"
                        b.tvBestTime.text = reportModel.getBestTime(it).toString()
                        itemBinding.viewGroup.addView(b.root, lp)
                    }
                    itemBinding.btnViewMore.text = "HIDE ALL"
                } else {
                    itemBinding.viewGroup.removeAllViews()
                    itemBinding.btnViewMore.text = "VIEW ALL"
                }

                itemBinding.root.setOnClickListener {
                    GameSound.play()?.clickSound()
                    list.forEach { if (it != reportModel) it.isExpanded = false }
                    reportModel.isExpanded = !reportModel.isExpanded
                    notifyDataSetChanged()
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClass {
            return HolderClass(
                ItemReportBinding.inflate(activity.layoutInflater, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: HolderClass, position: Int) {
            holder.bindView(position)
        }

    }

    data class ReportModel(
        val gameType: GameType,
        val icon: Int,
        val textColor: Int
    ) {
        val list: ArrayList<VariationType> = getVariationFromGameType(gameType)

        var isExpanded = false

        fun getAllTotalPlayed(): Long {
            var total = 0L
            list.forEach {
                total += getTotalPlayed(it)
            }
            return total
        }

        fun getTitle(): String {
            return gameType.title
        }

        fun getTotalPlayed(it: VariationType): Long {
            val dyKey = gameType.preKey + it.preKey
            return GamePreference.getLong(PrefKey.Played + dyKey)
        }

        fun getRightAndRation(it: VariationType): Long {
            val totalPlayed = getTotalPlayed(it) * numOfQuestion
            val dyKey = gameType.preKey + it.preKey
            return ((GamePreference.getLong(PrefKey.RightAns + dyKey) / totalPlayed.toFloat()) * 100f).toLong()
        }

        fun getBestTime(it: VariationType): Long {
            val dyKey = gameType.preKey + it.preKey
            return GamePreference.getLong(PrefKey.BestTime + dyKey)
        }
    }

}
