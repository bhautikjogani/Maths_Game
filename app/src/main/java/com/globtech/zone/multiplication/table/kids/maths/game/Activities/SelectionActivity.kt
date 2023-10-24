package com.globtech.zone.multiplication.table.kids.maths.game.Activities

import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.Base.BaseActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.DigitPlay
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.DivisionPlay
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.MissingNumberPlay
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.NegativePlay
import com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings.TableLeaning
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_DecimalSelection
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_SquareRootSelection
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_TablePractionSelection
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_TimeDigitSelection
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivitySelectionBinding
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ItemSelectionBinding
import kotlin.random.Random

fun getVariationFromGameType(gameType: GameType): ArrayList<VariationType> {
    val list = ArrayList<VariationType>()
    when (gameType.ordinal) {
        GameType.MixedOperations.ordinal -> {
            list.add(VariationType.Digit11)
            list.add(VariationType.Digit22)
            list.add(VariationType.Digit32)
            list.add(VariationType.Digit33)
            list.add(VariationType.Digit44)
            list.add(VariationType.RandoDigit)
            list.add(VariationType.TimeChallenge)
//                list.add(SelectionModel(VariationType.MissingNumber))
//                list.add(SelectionModel(VariationType.Balanced))
        }

        GameType.Addition.ordinal,
        GameType.Subtraction.ordinal -> {
            list.add(VariationType.Digit11)
            list.add(VariationType.Digit22)
            list.add(VariationType.Digit32)
            list.add(VariationType.Digit33)
            list.add(VariationType.Digit44)
            list.add(VariationType.RandoDigit)
            list.add(VariationType.TimeChallenge)
            list.add(VariationType.MissingNumber)
            list.add(VariationType.Negative)
//                list.add(SelectionModel(VariationType.Balanced))
        }

        GameType.Multiplication.ordinal -> {
            list.add(VariationType.TableLearn)
            list.add(VariationType.TablePractice)
            list.add(VariationType.Digit11)
            list.add(VariationType.Digit22)
            list.add(VariationType.Digit32)
            list.add(VariationType.Digit33)
            list.add(VariationType.Digit44)
            list.add(VariationType.RandoDigit)
            list.add(VariationType.TimeChallenge)
            list.add(VariationType.MissingNumber)
//                list.add(SelectionModel(VariationType.Balanced))
        }

        GameType.Division.ordinal -> {
            list.add(VariationType.Digit11)
            list.add(VariationType.Digit22)
            list.add(VariationType.Digit3)
            list.add(VariationType.Digit4)
            list.add(VariationType.RandoDigit)
            list.add(VariationType.TimeChallenge)
            list.add(VariationType.MissingNumber)
//                list.add(SelectionModel(VariationType.Balanced))
        }

        GameType.SquareRoot.ordinal -> {
            list.add(VariationType.SquareRootLearn)
            list.add(VariationType.SquareRootTimeChallenge)
        }

        GameType.Decimal.ordinal -> {
            list.add(VariationType.DecimalAddition)
            list.add(VariationType.DecimalSubtraction)
            list.add(VariationType.DecimalMultiplication)
            list.add(VariationType.TimeChallenge)
        }
    }
    return list
}

class SelectionActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivitySelectionBinding
    lateinit var gameType: GameType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clickHandler = this

        gameType = intent.getSerializableExtra("gameType") as GameType

        setTitle()
        setUp()

    }

    private fun setTitle() {
        binding.tvTitle.text = gameType.title
    }

    private fun setUp() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = SelectionAdapter(
            this@SelectionActivity, getSelectionList(gameType)
        )
    }

    private fun getSelectionList(gameType: GameType): ArrayList<SelectionModel> {
        val list = ArrayList<SelectionModel>()
        val variationList = getVariationFromGameType(gameType)
        variationList.forEach {
            list.add(SelectionModel(it))
        }
        return list
    }

    data class SelectionModel(val variationType: VariationType)

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnHome -> {
                finish()
            }
        }

    }

    inner class SelectionAdapter(
        private val activity: SelectionActivity,
        private val selectionList: ArrayList<SelectionModel>
    ) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

        inner class ViewHolder(private val itemBinding: ItemSelectionBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            fun onBind(position: Int) {
                val model = selectionList[position]
                itemBinding.tvText.text = model.variationType.title.uppercase()
                itemBinding.tvText.setOnClickListener {
                    if (activity.isDoubleClick()) return@setOnClickListener
                    showVariation(model.variationType)
                }
            }

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemSelectionBinding.inflate(activity.layoutInflater, parent, false))
        }

        override fun getItemCount(): Int {
            return selectionList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.onBind(position)
        }

    }


    private fun isShowAds(variationType: VariationType): Boolean {
        return when (gameType) {
            GameType.Division -> {
                when (variationType) {
                    VariationType.TimeChallenge -> false
                    VariationType.MissingNumber -> false
                    else -> true
                }
            }

            GameType.SquareRoot -> false

            GameType.Decimal -> false

            else -> {

                when (variationType) {
                    VariationType.Negative -> true
                    VariationType.TableLearn -> true
                    VariationType.TablePractice -> false
                    VariationType.TimeChallenge -> false
                    VariationType.MissingNumber -> false
                    else -> true
                }

            }

        }
    }

    fun showVariation(variationType: VariationType, isShowAd: Boolean = isShowAds(variationType)) {

        if (isShowAd) {
            AdsManager.show()?.InterstitialAdIntervaled(
                activity = this@SelectionActivity,
                adsListener = object : AdsListener() {
                    override fun onAdClose() {
                        super.onAdClose()
                        showVariation(variationType, false)
                    }
                })
            return
        }

        when (gameType) {
//            GameType.MixedOperations -> {}
//            GameType.Addition -> {}
//            GameType.Subtraction -> {}
//            GameType.Multiplication -> {}

            GameType.Division -> {
                when (variationType) {
                    VariationType.TimeChallenge -> {
                        Popup_TimeDigitSelection(
                            activity = this@SelectionActivity,
                            gameType = gameType,
                            variationType = variationType,
                            passActivity = DivisionPlay::class.java
                        )
                    }

                    VariationType.MissingNumber -> {
                        Popup_TimeDigitSelection(
                            activity = this@SelectionActivity,
                            gameType = gameType,
                            variationType = variationType,
                            passActivity = MissingNumberPlay::class.java
                        )
                    }

                    else -> {
                        startActivity(
                            Intent(this@SelectionActivity, DivisionPlay::class.java)
                                .putExtra("variationType", variationType)
                                .putExtra(
                                    "digits",
                                    arrayListOf(
                                        intArrayOf(
                                            variationType.digit1,
                                            variationType.digit2
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            GameType.SquareRoot -> {
                Popup_SquareRootSelection(
                    activity = this@SelectionActivity,
                    variationType = variationType,
                )
            }

            GameType.Decimal -> {
                Popup_DecimalSelection(
                    activity = this@SelectionActivity,
                    variationType = variationType,
                )
            }

            else -> {

                when (variationType) {
                    VariationType.Negative -> {
                        startActivity(
                            Intent(this@SelectionActivity, NegativePlay::class.java)
                                .putExtra("gameType", gameType)
                        )
                    }

                    VariationType.TableLearn -> {
                        startActivity(Intent(this, TableLeaning::class.java))
                    }

                    VariationType.TablePractice -> {
                        Popup_TablePractionSelection(this, variationType)
                    }

                    VariationType.TimeChallenge -> {
                        Popup_TimeDigitSelection(
                            activity = this@SelectionActivity,
                            gameType = gameType,
                            variationType = variationType,
                            passActivity = DigitPlay::class.java
                        )
                    }

                    VariationType.MissingNumber -> {
                        Popup_TimeDigitSelection(
                            activity = this@SelectionActivity,
                            gameType = gameType,
                            variationType = variationType,
                            passActivity = MissingNumberPlay::class.java
                        )
                    }

                    else -> {
                        startActivity(
                            Intent(
                                this@SelectionActivity,
                                if (GameType.MixedOperations == gameType && Random.nextInt(4) == 0) DivisionPlay::class.java else DigitPlay::class.java
                            ).putExtra("gameType", gameType)
                                .putExtra("variationType", variationType)
                                .putExtra(
                                    "digits",
                                    arrayListOf(
                                        intArrayOf(
                                            variationType.digit1,
                                            variationType.digit2
                                        )
                                    )
                                )
                        )
                    }
                }

            }

        }

    }

}
