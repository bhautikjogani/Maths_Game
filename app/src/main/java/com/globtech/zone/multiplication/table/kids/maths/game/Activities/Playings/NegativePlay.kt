package com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings

import AdsUtiles.BannerAdModel
import GameModule.AdsUtiles.AdsListener
import GameModule.AdsUtiles.AdsManager
import GameModule.AdsUtiles.BannerAdListener
import GameModule.Base.BaseActivity
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.HandlerUtils.GameHandlerClass
import GameModule.Models.MutableListLiveData
import GameModule.PrefKey
import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Conformation
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_DrawView
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Result
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Setting
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityNegativePlayBinding
import me.grantland.widget.AutofitTextView
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class NegativePlay : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityNegativePlayBinding

    private var gameType: GameType = GameType.Addition
    private var questionList = MutableListLiveData<QuestionModel>()
    var ansViewList = ArrayList<AutofitTextView>(4)

    val handler = GameHandlerClass()
    var retryQuestionList = MutableListLiveData<QuestionModel>()

    private var bannerAdModel: BannerAdModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityNegativePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameType = intent.getSerializableExtra("gameType") as GameType

        setBindingData()
        generateQuestionAndAnswer()

        AdsManager.show()?.BannerAd(this, binding.adParent, object : BannerAdListener() {
            override fun onBannerAdNotShowing() {
                super.onBannerAdNotShowing()
                binding.frmAdView.visibility = View.GONE
            }

            override fun onBannerAdLoaded(bannerAdModel: BannerAdModel) {
                super.onBannerAdLoaded(bannerAdModel)
                this@NegativePlay.bannerAdModel = bannerAdModel
                if (!hasWindowFocus()) this@NegativePlay.bannerAdModel?.onPause()
            }
        })

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) bannerAdModel?.onResume() else bannerAdModel?.onPause()
    }

    private fun generateQuestionAndAnswer() {
        Log.d(TAG, "generateQuestionAndAnswer: ")
        if (questionList.size == numOfQuestion) {
            doOnGameOver()
            return
        }
        resetQuestion()

        val model = getQuestionModel()

        binding.tvQ1.text = model.getPreDigitString()
        binding.tvQ2.text = model.getPostDigitString()
        binding.tvSymbol.text = model.symbolType

        ansViewList.forEachIndexed { index, textView ->
            textView.text = model.ansList[index].toString()
        }

        binding.tvAns.text = model.getSymbolSpace()

        binding.enableAns = true
        questionList.add(model)
    }

    private fun doOnGameOver() {
        var rightAnswer = 0
        var wrongAnswer = 0
        questionList.forEach {
            if (it.correctAns) rightAnswer++
        }

        val dyKey = gameType.preKey + VariationType.Negative.preKey
        val preTime = GamePreference.getLong(PrefKey.BestTime + dyKey)
        GamePreference.addLong(PrefKey.Played + dyKey, 1)
        GamePreference.addLong(PrefKey.RightAns + dyKey, rightAnswer.toLong())
        GamePreference.addLong(PrefKey.WrongAns + dyKey, wrongAnswer.toLong())
//        if (avgTime != 0) GamePreference.addLong(PrefKey.BestTime + dyKey, Math.min(preTime, avgTime.toLong()))

        AdsManager.show()?.InterstitialAd(
            activity = this@NegativePlay,
            adsListener = object : AdsListener() {
                override fun onAdClose() {
                    super.onAdClose()

                    Popup_Result(
                        activity = this@NegativePlay,
                        rightAnswer = rightAnswer,
                        wrongAnswer = wrongAnswer,
                        avgTime = 0,
                        retryCallback = {
                            doOnRetry()
                        }
                    )

                }
            }
        )

    }

    private fun doOnRetry() {
        retryQuestionList.addAll(questionList)
        retryQuestionList.forEach {
            it.correctAns = false
        }
        retryQuestionList.shuffle()
        questionList.clear()
        resetQuestion()
        generateQuestionAndAnswer()
    }

    private fun getQuestionModel(): QuestionModel {
        val symbol = getSymbol(gameType)

        var preDigit = 0
        var postDigit = 0
        while (true) {
            preDigit = getQuestion((1..2).random())
            postDigit = getQuestion((1..2).random(), preDigit)
            if (isValidQuestion(preDigit, postDigit)) break
        }

        when (Random.nextInt(3)) {
            0 -> preDigit *= -1
            1 -> postDigit *= -1
            2 -> {
                preDigit *= -1
                postDigit *= -1
            }
        }

        val ans = getAnswer(preDigit, postDigit, symbol)
        val ansList = ArrayList<Int>(4)
        val ansStyleList = arrayListOf(2, 3, 4, 5, 6, 7)
        ansStyleList.shuffle()

        val ranAnswer = Random.nextBoolean()
        val ansString = ans.toString()
        val ansLength = ansString.length - if (ansString.contains("-")) 1 else 0
        for (i in 0 until 4) {
            ansList.add(
                getRandomAnswer(
                    ans,
                    if (i == 0 && ansLength > 1 && ranAnswer) i
                    else if (i == 1 && ansLength > 2 && ranAnswer.not()) i
                    else ansStyleList.removeLast()
                )
            )
        }

        ansList[Random.nextInt(ansList.size)] = ans
        ansList.shuffle()

        return QuestionModel(
            preDigit = preDigit,
            postDigit = postDigit,
            symbolType = symbol,
            ans = ans,
            ansList = ansList
        )
    }

    private fun getQuestion(digitLength: Int, preDigit: Int = 0): Int {
        Log.d(
            TAG,
            "getQuestion:  digitLength = $digitLength   |   preDigit = $preDigit"
        )
        // pow(10, this value result)
        // -2 -> 1 Digit (0.__)
        // -1 -> 1 Digit (2.00)
        // 0 -> 2 Digit
        // 1 -> 3 Digit
        // 2 -> 4 Digit
        // 3 -> 5 Digit

        // 10-50  |  35-75  |  60-99

        val hardness = Random.nextInt(3)

        var postDigit = 0

        if (digitLength == 1) {

            return when (hardness) {
                0 -> (0..5).random()

                1 -> (3..7).random()

                0 -> (5..9).random()

                else -> 0
            }

        }

        postDigit = when (hardness) {
            0 -> {
                val preD = (10 * (pow(10, digitLength - 2)))
                val postD = (50 * pow(10, digitLength - 2))
                (min(preD, postD)..max(preD, postD)).random()
            }

            1 -> {
                val preD = (35 * (pow(10, digitLength - 2)))
                val postD = (75 * pow(10, digitLength - 2))
                (min(preD, postD)..max(preD, postD)).random()
            }

            2 -> {
                val preD = (60 * (pow(10, digitLength - 2)))
                val postD = (99 * pow(10, digitLength - 2))
                (min(preD, postD)..max(preD, postD)).random()
            }

            else -> 0
        }

        return postDigit

    }

    private fun isValidQuestion(preDigit: Int, postDigit: Int): Boolean {
        questionList.forEach {
            if (it.preDigit == preDigit && it.postDigit == postDigit)
                return false
        }
        if (questionList.isNotEmpty()) {
            if (questionList.last().preDigit == questionList.last().postDigit &&
                preDigit == postDigit
            ) return false
        }
        if (Random.nextInt(3) != 0) {
            if (preDigit == postDigit) return false
        }
        return true
    }

    private fun getAnswer(preDigit: Int, postDigit: Int, symbol: String): Int {
        return when (symbol) {
            symbolList[0] -> preDigit + postDigit
            symbolList[1] -> preDigit - postDigit
            symbolList[2] -> preDigit * postDigit
            symbolList[3] -> preDigit / postDigit
            else -> preDigit + postDigit
        }
    }

    private fun getRandomAnswer(ans: Int, ansStyle: Int): Int {
        return when (ansStyle) {
            0 -> {
                var ansString = ans.toString()
                val isMinues = ansString.contains("-")
                ansString = ansString.replace("-", "")

                var ranNumber = ""
                val rangeList = { (0..9).random() }

                while (ranNumber.length < ansString.length - 1) {
                    val num = rangeList().toString()
                    ranNumber += num
                }

                var finalAns = (ranNumber + ansString.subSequence(
                    ansString.length - 1,
                    ansString.length
                )).toInt()
                if (isMinues) finalAns *= -1

                return if (finalAns == ans) getRandomAnswer(ans, ansStyle) else {
                    Log.d(TAG, "getRandomAnswer:   =======>  $finalAns")
                    finalAns
                }
            }

            1 -> {
                var ansString = ans.toString()
                val isMinues = ansString.contains("-")
                ansString = ansString.replace("-", "")

                var ranNumber = ""
                val rangeList = { (0..9).random() }

                while (ranNumber.length < ansString.length - 2) {
                    val num = rangeList().toString()
                    ranNumber += num
                }

                var finalAns = (ranNumber + ansString.subSequence(
                    ansString.length - 2,
                    ansString.length
                )).toInt()
                if (isMinues) finalAns *= -1
                return if (finalAns == ans) getRandomAnswer(ans, ansStyle) else {
                    Log.d(TAG, "getRandomAnswer:   =-------->  $finalAns")
                    finalAns
                }
            }

            2 -> ans + 1
            3 -> ans - 1
            4 -> ans - 2
            5 -> ans + 2
            6 -> ans - 3
            7 -> ans + 3
            else -> ((ans - Random.nextInt(10))..(ans + Random.nextInt(10))).random()
        }
    }

    private fun resetQuestion() {
        binding.tvQ1.text = ""
        binding.tvQ2.text = ""
        ansViewList.forEach {
            it.text = ""
            it.setBackgroundResource(R.drawable.bg_btn_ans)
        }
        binding.tvAns.text = ""
        binding.enableAns = false
    }

    private fun setBindingData() {
        binding.clickHandler = this
        binding.setClickHandler2 {
            when (it) {
                binding.btnKeyType -> {
                    binding.keyType = if (binding.keyType == 0) 1 else 0
                }
            }
        }
        binding.totalQue = numOfQuestion
        binding.enableAns = false
        binding.currentQue = 1
        binding.keyType = 1

        ansViewList.add(binding.btnAns1)
        ansViewList.add(binding.btnAns2)
        ansViewList.add(binding.btnAns3)
        ansViewList.add(binding.btnAns4)

        questionList.observe(this) {
            binding.currentQue = it.size
        }

        binding.tvTitle.text = gameType.title
        binding.tvTitle2.text = VariationType.Negative.title

    }

    data class QuestionModel(
        val preDigit: Int,
        val postDigit: Int,
        val symbolType: String,
        val ans: Int,
        val ansList: ArrayList<Int>,
        var correctAns: Boolean = false
    ) {

        fun getSymbol(selAnsIndex: Int): String {
            var spaceManage = ""
            val symbolSpace = getSymbolSpace()
            for (i in 0 until symbolSpace.length - ansList[selAnsIndex].toString().length - 1) {
                spaceManage += " "
            }
            return spaceManage
        }

        fun getSymbolSpace(): String {
            var spaceManage = ""
            for (i in 0 until ans.toString().toList().size - 1) {
                spaceManage += " "
            }
            return "$spaceManage?"
        }

        fun getPreDigitString(): String {
            return if (preDigit < 0) "($preDigit)" else "$preDigit"
        }

        fun getPostDigitString(): String {
            return if (postDigit < 0) "($postDigit)" else "$postDigit"
        }

    }

    private fun pow(base: Int, exponent: Int): Int {
        return Math.pow(base.toDouble(), exponent.toDouble()).toInt()
    }

    override fun onBackPressed() {
        binding.btnHome.performClick()
    }

    override fun onClick(view: View?) {
        if (isDoubleClick()) return

        when (view) {

            binding.btnHome -> {
                Popup_Conformation(this@NegativePlay)
                    .setDialogTitle("EXIT PLAYING")
                    .setDialogMsg("ARE YOU SURE,\nYOU WANT TO CLOSE PLAYING?")
                    .setButtonRight("NO")
                    .setButtonLeft("YES") {
                        resetQuestion()

                        AdsManager.show()?.InterstitialAd(
                            activity = this@NegativePlay,
                            adsListener = object : AdsListener() {
                                override fun onAdClose() {
                                    super.onAdClose()

                                    finish()

                                }
                            }
                        )

                    }
            }

            binding.btnSetting -> {
                Popup_Setting(this@NegativePlay)
            }

            binding.btnAns1 -> checkAns(0)
            binding.btnAns2 -> checkAns(1)
            binding.btnAns3 -> checkAns(2)
            binding.btnAns4 -> checkAns(3)

            binding.btnDraw -> {
                Popup_DrawView(this@NegativePlay)
            }

        }

    }

    private fun checkAns(selAnsIndex: Int, isShowAns: Boolean = false) {
        binding.enableAns = false
        val model = questionList.last()
        val isRightAnd = model.ansList[selAnsIndex] == questionList.last().ans
        if (isShowAns.not()) {
            model.correctAns = isRightAnd
        }

        ansViewList[selAnsIndex].setBackgroundResource(if (isRightAnd) R.drawable.bg_btn_ans_right else R.drawable.bg_btn_ans_wrong)
        binding.tvAns.text = "${model.getSymbol(selAnsIndex)}${model.ansList[selAnsIndex]}"

        binding.lottieView.alpha = .7f
        binding.lottieView.speed = 1.5f
        binding.lottieView.progress = 0f
        binding.lottieView.setAnimation(if (isRightAnd) R.raw.right_tick else R.raw.wrong_tick)
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                GameSound.play()
                    ?.sound(if (isRightAnd) R.raw.right_ans else R.raw.wrong_ans, volume = .6f)
            }

            override fun onAnimationEnd(animation: Animator) {
                handler.postDelayedClass({
                    binding.lottieView.removeAllAnimatorListeners()
                    binding.lottieView.visibility = View.GONE
                    if (isRightAnd.not()) {
                        for (i in 0 until model.ansList.size) {
                            if (model.ans == model.ansList[i]) {
                                checkAns(i, true)
                                break
                            }
                        }
                    } else
                        generateQuestionAndAnswer()
                }, 500)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
        binding.lottieView.visibility = View.VISIBLE
        handler.postDelayedClass({
            binding.lottieView.playAnimation()
        }, 500)

    }

}