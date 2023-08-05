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
import kotlin.math.nextUp
import kotlin.random.Random

class DecimalPlay : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityDigitPlayBinding
    var variationType: VariationType = VariationType.Digit11

    lateinit var difficulty: BooleanArray
    var isTimePlay = false
    var time = 20

    private val totalQuestions = numOfQuestion
    var retryQuestionList = MutableListLiveData<QuestionModel>()
    var questionList = MutableListLiveData<QuestionModel>()
    var ansViewList = ArrayList<AutofitTextView>(4)

    var colorAnim: ValueAnimator? = null
    var progressAnim: ObjectAnimator? = null

    val handler = GameHandlerClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityDigitPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variationType = intent.getSerializableExtra("variationType") as VariationType
        difficulty = intent.getBooleanArrayExtra("difficulty")!!
        isTimePlay = intent.getBooleanExtra("isTimePlay", false)
        time = intent.getIntExtra("time", 20)

        setBindingData()
        generateQuestionAndAnswer()

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
        binding.totalQue = totalQuestions
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

        binding.tvTitle.text = GameType.Decimal.title
        binding.tvTitle2.text = variationType.title

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
                binding.lottieView.progress = 0f
                binding.lottieView.alpha = 1f
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

    private fun generateQuestionAndAnswer() {
        if (questionList.size == totalQuestions) {
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

        val dyKey = GameType.Decimal.preKey + variationType.preKey
        val preTime = GamePreference.getLong(PrefKey.BestTime + dyKey)
        GamePreference.addLong(PrefKey.Played + dyKey, 1)
        GamePreference.addLong(PrefKey.RightAns + dyKey, rightAnswer.toLong())
        GamePreference.addLong(PrefKey.WrongAns + dyKey, wrongAnswer.toLong())
        if (avgTime != 0) GamePreference.addLong(
            PrefKey.BestTime + dyKey,
            Math.min(preTime, avgTime.toLong())
        )

        Popup_Result(
            activity = this@DecimalPlay,
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

        if (retryQuestionList.isNotEmpty()) {
            return retryQuestionList[questionList.size]
        }

        var preDigit = 0.0
        var postDigit = 0.0
        while (true) {
            preDigit = getQuestion()
            postDigit = getQuestion()
            if (isValidQuestion(preDigit, postDigit)) break
        }
        val symbol = getSymbol()
        val ans = getAnswer(preDigit, postDigit, symbol)
        val ansList = ArrayList<Double>(4)
        val ansStyleList = arrayListOf(2, 3, 4, 5, 6, 7)
        ansStyleList.shuffle()

        val ranAnswer = Random.nextBoolean()
        val ansString = ans.toString()
        while (ansList.size != 4) {
            val generatedAns = getRandomAnswer(
                ans,
                if (ansList.size == 0 && ansString.length > 1 && ranAnswer) 0
                else if (ansList.size == 1 && ansString.length > 2 && ranAnswer.not()) 1
                else ansStyleList.last()
            )
            if (ansList.contains(generatedAns).not()) {
                ansStyleList.removeLast()
                ansList.add(generatedAns)
            }
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

    private fun getRandomAnswer(ans: Double, ansStyle: Int): Double {
        return (when (ansStyle) {
            /*0, 1 -> {
                val dotIndex = ans.toString().indexOf(".")
                val ansString = ans.toString()

                var ranNumber = ""
                val rangeList = { (0..9).random() }

                while (ranNumber.length < ansString.length - 1) {
                    ranNumber += if (ranNumber.length == dotIndex) "."
                    else rangeList().toString()
                }

                val finalAns = (ranNumber + ansString.subSequence(
                    ansString.length - 1,
                    ansString.length
                ))

                return finalAns.toDouble()
            }*/

            2 -> ans + 1
            3 -> ans - 1
            4 -> ans - 2
            5 -> ans + 2
            6 -> ans - 3
            7 -> ans + 3
            else -> Random.nextDouble(ans - 10.0, ans + 10.0)
        }).nextUp().decimalFormat()
    }

    private fun getSymbol(): String {
        return when (variationType) {
            VariationType.DecimalAddition -> "+"
            VariationType.DecimalSubtraction -> "-"
            VariationType.DecimalMultiplication -> "×"
            else -> ""
        }
    }

    private fun getAnswer(preDigit: Double, postDigit: Double, symbol: String): Double {
        return (when (symbol) {
            "+" -> preDigit + postDigit
            "-" -> preDigit - postDigit
            "×" -> preDigit * postDigit
            else -> 0.0
        }).nextUp().decimalFormat()
    }

    private fun getQuestion(): Double {
        // pow(10, this value result)
        // -2 -> 1 Digit (0.__)
        // -1 -> 1 Digit (2.00)
        // 0 -> 2 Digit
        // 1 -> 3 Digit
        // 2 -> 4 Digit
        // 3 -> 5 Digit

        var preDigit = 0.toDouble()
        var postDigit = 0.toDouble()
        var preQExponent = -2
        var postQExponent = 0

        when {
            difficulty[0] && difficulty[1] && difficulty[2] -> {
                preQExponent = -2
                postQExponent = 2
            }

            difficulty[0] && difficulty[1] -> {
                preQExponent = -2
                postQExponent = 1
            }

            difficulty[0] && difficulty[2] -> {
                preQExponent = if (Random.nextBoolean()) -2 else 1
                postQExponent = if (Random.nextBoolean()) 0 else 2
            }

            difficulty[1] && difficulty[2] -> {
                preQExponent = 0
                postQExponent = 2
            }

            difficulty[0] -> {
                preQExponent = -2
                postQExponent = 0
            }

            difficulty[1] -> {
                preQExponent = 0
                postQExponent = 1
            }

            difficulty[2] -> {
                preQExponent = 1
                postQExponent = 2
            }
        }

        preDigit = (10 * (pow((preQExponent..postQExponent).random())))
        postDigit = (99 * pow((preQExponent..postQExponent).random()))

        val retVal = Random.nextDouble(min(preDigit, postDigit), max(preDigit, postDigit))

//        val retVal = when (difficulty) {
//            DifficultyEasy -> {
//                val preDigit = (10 * (pow((-2..0).random())))
//                val postDigit = (99 * pow((-2..0).random()))
//                Random.nextDouble(Math.min(preDigit, postDigit), Math.max(preDigit, postDigit))
//            }
//
//            DifficultyMedium -> {
//                val preDigit = (10 * (pow((0..1).random())))
//                val postDigit = (99 * pow((0..1).random()))
//                Random.nextDouble(Math.min(preDigit, postDigit), Math.max(preDigit, postDigit))
//            }
//
//            DifficultyHard -> {
//                val preDigit = (10 * (pow(((if (Random.nextBoolean()) 0 else 1)..2).random())))
//                val postDigit = (99 * pow(((if (Random.nextBoolean()) 0 else 1)..2).random()))
//                Random.nextDouble(Math.min(preDigit, postDigit), Math.max(preDigit, postDigit))
//            }
//
//            else -> 0.0
//        }

        return retVal.decimalFormat()
    }

    private fun isValidQuestion(preDigit: Double, postDigit: Double): Boolean {
        questionList.forEach {
            if (it.preDigit == preDigit && it.postDigit == postDigit)
                return false
        }
        return true
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

        binding.tvAns.text =
            "${model.getSymbol(selAnsIndex)}${model.ansList[selAnsIndex].toString()}"

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
        val preDigit: Double,
        val postDigit: Double,
        val symbolType: String,
        val ans: Double,
        val ansList: ArrayList<Double>
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

    private fun pow(exponent: Int): Double {
        return Math.pow(10.0, exponent.toDouble())
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
                Popup_Conformation(this@DecimalPlay)
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
                Popup_Setting(this@DecimalPlay)
                    .setOnCancelListener {
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
                Popup_DrawView(this@DecimalPlay)
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

private fun Double.decimalFormat(): Double {
    return if (this.toString().contains(".")) {
        val beforeDotArray = this.toString().split(".")[0]
        val afterDotArray = this.toString().split(".")[1]
        val aa = afterDotArray.substring(0, Math.min(2, afterDotArray.length))
        val finalAns = "$beforeDotArray.$aa".toDouble()
//        print("beforeDotArray=${beforeDotArray}   |   afterDotArray=${afterDotArray}   |   finalAns=${finalAns}")
        finalAns
    } else "$this.00".toDouble()
}
