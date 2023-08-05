package com.globtech.zone.multiplication.table.kids.maths.game.Activities.Playings

import GameModule.Base.BaseActivity
import GameModule.GamePreference
import GameModule.GameSound
import GameModule.HandlerUtils.GameHandlerClass
import GameModule.Models.MutableListLiveData
import GameModule.PrefKey
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Conformation
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_DrawView
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Result
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Setting
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityDigitPlayBinding
import me.grantland.widget.AutofitTextView
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

val symbolList = listOf("+", "-", "×", "÷")
const val numOfQuestion = 10

fun getSymbol(gameType: GameType): String {
    return when (gameType) {
        GameType.Addition -> symbolList[0]
        GameType.Subtraction -> symbolList[1]
        GameType.Multiplication -> symbolList[2]
        GameType.Division -> symbolList[3]
        else -> symbolList.random() //GameType.MixedOperations
    }
}

class DigitPlay : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityDigitPlayBinding
    var gameType: GameType = GameType.Addition
    var variationType: VariationType = VariationType.Digit11
    lateinit var digitList: ArrayList<IntArray>
    var questionList = MutableListLiveData<QuestionModel>()
    var ansViewList = ArrayList<AutofitTextView>(4)
    var isSequential = false
    var sequentialDigit = 0
    var isTimePlay = false
    var time = 20

    var colorAnim: ValueAnimator? = null
    var progressAnim: ObjectAnimator? = null

    val handler = GameHandlerClass()
    var retryQuestionList = MutableListLiveData<QuestionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityDigitPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameType = intent.getSerializableExtra("gameType") as GameType
        variationType = intent.getSerializableExtra("variationType") as VariationType
        isTimePlay = intent.getBooleanExtra("isTimePlay", false)
        isSequential = intent.getBooleanExtra("isSequential", false)
        time = intent.getIntExtra("time", 0)
        digitList = intent.getSerializableExtra("digits") as ArrayList<IntArray>

        setBindingData()
        generateQuestionAndAnswer()

    }

    private fun startProgress() {
        if (isTimePlay.not()) return
        colorAnim = ValueAnimator.ofObject(
            ArgbEvaluator(), Color.GREEN, Color.RED
        ).apply {
            this.duration = (1000 * time).toLong()
            this.interpolator = LinearInterpolator()
            this.addUpdateListener {
                if (it.animatedValue is Int) {
                    val color = it.animatedValue as Int
                    binding.progressbar.setIndicatorColor(color)
                }
            }
            this.addListener(onEnd = {

            })
            this.start()
        }

        binding.tvTime.text = time.toString()
        binding.progressbar.max = 1000
        progressAnim = ObjectAnimator.ofInt(binding.progressbar, "progress", 1000, 67).apply {
            this.duration = (1000 * time).toLong()
            this.interpolator = LinearInterpolator()
            this.addUpdateListener {
                binding.tvTime.text =
                    (((it.animatedValue as Int) / 10) / (100f / time.toFloat())).toInt().toString()
            }
            this.addListener(onEnd = {
                showToast("oops! time over")
                binding.enableAns = false
                GameSound.play()?.vibration()
                binding.lottieView.alpha = 1f
                binding.lottieView.progress = 0f
                binding.lottieView.setAnimation(R.raw.oops)
                binding.lottieView.speed = 1.4f
                binding.lottieView.scaleX = 1.3f
                binding.lottieView.scaleY = 1.3f
                binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {
                    }

                    override fun onAnimationEnd(p0: Animator) {
                        handler.postDelayedClass({
                            binding.lottieView.scaleX = 1f
                            binding.lottieView.scaleY = 1f
                            binding.lottieView.removeAllAnimatorListeners()
                            binding.lottieView.visibility = View.GONE
                            generateQuestionAndAnswer()
                        }, 600)
                    }

                    override fun onAnimationCancel(p0: Animator) {
                    }

                    override fun onAnimationRepeat(p0: Animator) {
                    }

                })
                binding.lottieView.visibility = View.VISIBLE
                handler.postDelayedClass({
                    binding.lottieView.playAnimation()
                }, 500)
            })
            this.start()
        }

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
        binding.isTimePlay = isTimePlay

        ansViewList.add(binding.btnAns1)
        ansViewList.add(binding.btnAns2)
        ansViewList.add(binding.btnAns3)
        ansViewList.add(binding.btnAns4)

        questionList.observe(this) {
            binding.currentQue = it.size
        }

        binding.tvTitle.text = gameType.title
        binding.tvTitle2.text = variationType.title

    }

    private fun generateQuestionAndAnswer() {
        Log.d(TAG, "generateQuestionAndAnswer: ")
        if (questionList.size == numOfQuestion) {
            doOnGameOver()
            return
        }
        resetQuestion()

        val model = getQuestionModel()

        binding.tvQ1.text = model.preDigit.toString()
        binding.tvQ2.text = "${model.symbolType}${model.postDigit}"

        ansViewList.forEachIndexed { index, textView ->
            textView.text = model.ansList[index].toString()
        }

        binding.tvAns.text = model.getSymbolSpace()

        startProgress()

        binding.enableAns = true
        questionList.add(model)
    }

    private fun doOnGameOver() {
        var rightAnswer = 0
        var wrongAnswer = 0
        var avgTime = 0
        questionList.forEach {
            if (it.correctAns) rightAnswer++
            else wrongAnswer++
            avgTime += it.ansTime
        }
        avgTime /= questionList.size

        val dyKey = gameType.preKey + variationType.preKey
        val preTime = GamePreference.getLong(PrefKey.BestTime + dyKey)
        GamePreference.addLong(PrefKey.Played + dyKey, 1)
        GamePreference.addLong(PrefKey.RightAns + dyKey, rightAnswer.toLong())
        GamePreference.addLong(PrefKey.WrongAns + dyKey, wrongAnswer.toLong())
        if (avgTime != 0) GamePreference.addLong(
            PrefKey.BestTime + dyKey,
            Math.min(preTime, avgTime.toLong())
        )

        Popup_Result(
            activity = this@DigitPlay,
            rightAnswer = rightAnswer,
            wrongAnswer = wrongAnswer,
            avgTime = avgTime,
            retryCallback = {
                doOnRetry()
            }
        )
    }

    private fun doOnRetry() {
        retryQuestionList.addAll(questionList)
        retryQuestionList.forEach {
            it.correctAns = false
            it.ansTime = 0
        }
        retryQuestionList.shuffle()
        questionList.clear()
        resetQuestion()
        generateQuestionAndAnswer()
    }

    private fun getQuestionModel(): QuestionModel {
        val symbol = getSymbol(gameType)

        val digitArray = if (variationType == VariationType.RandoDigit) {
            val digit1 = (1..4).random()
            val digit2 = (1..4).random()
            intArrayOf(digit1, digit2)
        } else {
            digitList.random()
        }

        var preDigit = 0
        var postDigit = 0
        if (isSequential) {
            if (sequentialDigit == 0) sequentialDigit = (1..30).random()
            preDigit = sequentialDigit
            postDigit = questionList.size + 1
        } else {
            while (true) {
                preDigit = getQuestion(digitArray[0])
                postDigit = getQuestion(digitArray[1], preDigit)

                if (isValidQuestion(preDigit, postDigit)) break
            }
        }
        val ans = getAnswer(preDigit, postDigit, symbol)
        val ansList = ArrayList<Int>(4)
        val ansStyleList = arrayListOf(2, 3, 4, 5, 6, 7)
        ansStyleList.shuffle()

        val ranAnswer = Random.nextBoolean()
        val ansString = ans.toString()
        for (i in 0 until 4) {
            ansList.add(
                getRandomAnswer(
                    ans,
                    if (i == 0 && ansString.length > 1 && ranAnswer) i
                    else if (i == 1 && ansString.length > 2 && ranAnswer.not()) i
                    else ansStyleList.removeLast()
                )
            )
        }
        ansList[Random.nextInt(ansList.size)] = ans
        ansList.shuffle()

        val model = QuestionModel(
            preDigit = preDigit,
            postDigit = postDigit,
            symbolType = symbol,
            ans = ans,
            ansList = ansList
        )
        return model
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

    private fun getRandomAnswer(ans: Int, ansStyle: Int): Int {
        return when (ansStyle) {
            0 -> {
                val ansString = ans.toString()

                var ranNumber = ""
                val rangeList = { (0..9).random() }

                while (ranNumber.length < ansString.length - 1) {
                    val num = rangeList().toString()
                    ranNumber += num
                }

                val finalAns = (ranNumber + ansString.subSequence(
                    ansString.length - 1,
                    ansString.length
                )).toInt()

                return if (finalAns == ans) getRandomAnswer(ans, ansStyle) else finalAns
            }

            1 -> {
                val ansString = ans.toString()

                var ranNumber = ""
                val rangeList = { (0..9).random() }

                while (ranNumber.length < ansString.length - 2) {
                    val num = rangeList().toString()
                    ranNumber += num
                }

                val finalAns = (ranNumber + ansString.subSequence(
                    ansString.length - 2,
                    ansString.length
                )).toInt()
                return if (finalAns == ans) getRandomAnswer(ans, ansStyle) else finalAns
            }

            2 -> ans + 1
            3 -> ans - 1
            4 -> ans - 2
            5 -> ans + 2
            6 -> ans - 3
            7 -> ans + 3
            else -> ((ans - Random.nextInt(20))..(ans + Random.nextInt(20))).random()
        }
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

        if (digitLength == 1) {
            return (0..9).random()
        }

        val preD = (10 * (pow(10, digitLength - 2)))
        val postD = (99 * pow(10, digitLength - 2))

        return (min(preD, postD)..max(preD, postD)).random()
    }

    private fun resetQuestion() {
        colorAnim?.pause()
        colorAnim?.removeAllUpdateListeners()
        colorAnim?.removeAllListeners()
        colorAnim?.cancel()
        colorAnim = null
        progressAnim?.pause()
        progressAnim?.removeAllUpdateListeners()
        progressAnim?.removeAllListeners()
        progressAnim?.cancel()
        progressAnim = null

        binding.tvQ1.text = ""
        binding.tvQ2.text = ""
        ansViewList.forEach {
            it.text = ""
            it.setBackgroundResource(R.drawable.bg_btn_ans)
        }
        binding.tvAns.text = ""
        binding.enableAns = false
    }

    private fun checkAns(selAnsIndex: Int, isShowAns: Boolean = false) {
        binding.enableAns = false
        val model = questionList.last()
        val isRightAnd = model.ansList[selAnsIndex] == questionList.last().ans
        if (isShowAns.not()) {
            model.correctAns = isRightAnd
            progressAnim?.let {
                model.ansTime = (((it.animatedValue as Int) / 10) / (100f / time.toFloat())).toInt()
            }
        }
        ansViewList[selAnsIndex].setBackgroundResource(if (isRightAnd) R.drawable.bg_btn_ans_right else R.drawable.bg_btn_ans_wrong)

        binding.tvAns.text = "${model.getSymbol(selAnsIndex)}${model.ansList[selAnsIndex]}"

        colorAnim?.pause()
        progressAnim?.pause()

        binding.lottieView.alpha = .7f
        binding.lottieView.speed = 1.5f
        binding.lottieView.progress = 0f
        binding.lottieView.setAnimation(if (isRightAnd) R.raw.right_tick else R.raw.wrong_tick)
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
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

    data class QuestionModel(
        val preDigit: Int,
        val postDigit: Int,
        val symbolType: String,
        val ans: Int,
        val ansList: ArrayList<Int>
    ) {
        var correctAns: Boolean = false
        var ansTime: Int = 0

        fun getSymbol(selAnsIndex: Int): String {
            var spaceManage = "="
            val symbolSpace = getSymbolSpace()
            for (i in 0 until symbolSpace.length - ansList[selAnsIndex].toString().length - 1) {
                spaceManage += " "
            }
            return spaceManage
        }

        fun getSymbolSpace(): String {
            var spaceManage = "="
            for (i in 0 until ans.toString().toList().size - 1) {
                spaceManage += " "
            }
            return "$spaceManage?"
        }
    }

    fun pow(base: Int, exponent: Int): Int {
        return Math.pow(base.toDouble(), exponent.toDouble()).toInt()
    }

    override fun onBackPressed() {
        binding.btnHome.performClick()
    }

    override fun onClick(view: View?) {
        if (isDoubleClick()) return

        when (view) {

            binding.btnHome -> {
                colorAnim?.pause()
                progressAnim?.pause()
                Popup_Conformation(this@DigitPlay)
                    .setDialogTitle("EXIT PLAYING")
                    .setDialogMsg("ARE YOU SURE,\nYOU WANT TO CLOSE PLAYING?")
                    .setButtonRight("NO")
                    .setButtonLeft("YES") {
                        resetQuestion()
                        finish()
                    }.setOnDismissListener {
                        colorAnim?.resume()
                        progressAnim?.resume()
                    }
            }

            binding.btnSetting -> {
                colorAnim?.pause()
                progressAnim?.pause()
                Popup_Setting(this@DigitPlay)
                    .setOnDismissListener {
                        colorAnim?.resume()
                        progressAnim?.resume()
                    }
            }

            binding.btnAns1 -> checkAns(0)
            binding.btnAns2 -> checkAns(1)
            binding.btnAns3 -> checkAns(2)
            binding.btnAns4 -> checkAns(3)

            binding.btnDraw -> {
                colorAnim?.pause()
                progressAnim?.pause()
                Popup_DrawView(this@DigitPlay)
                    .setOnDismissListener {
                        colorAnim?.resume()
                        progressAnim?.resume()
                    }
            }

        }

    }

    override fun onPause() {
        super.onPause()
        colorAnim?.pause()
        progressAnim?.pause()
    }

    override fun onResume() {
        super.onResume()
        colorAnim?.resume()
        progressAnim?.resume()
    }

}
