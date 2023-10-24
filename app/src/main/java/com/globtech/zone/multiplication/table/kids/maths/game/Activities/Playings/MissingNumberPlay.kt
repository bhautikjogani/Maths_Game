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
import GameModule.ScreenUtil
import android.animation.Animator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Conformation
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_DrawView
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Result
import com.globtech.zone.multiplication.table.kids.maths.game.Popups.Popup_Setting
import com.globtech.zone.multiplication.table.kids.maths.game.R
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.GameType
import com.globtech.zone.multiplication.table.kids.maths.game.Utility.VariationType
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivityMissingNumberPlayBinding
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class MissingNumberPlay : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityMissingNumberPlayBinding
    private var gameType: GameType = GameType.Addition

    lateinit var digitList: ArrayList<IntArray>
    private var questionList = MutableListLiveData<QuestionModel>()
    private val lastQuestionHideList = ArrayList<Int>()
    private val highLightList = ArrayList<DigitModel>()
    private var highLightSelectList = ArrayList<Int>()
    var retryQuestionList = MutableListLiveData<QuestionModel>()
    val handler = GameHandlerClass()

    private var bannerAdModel: BannerAdModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivityMissingNumberPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameType = intent.getSerializableExtra("gameType") as GameType
        digitList = intent.getSerializableExtra("digits") as ArrayList<IntArray>

        setBindingData()
        generateQuestionAndAnswer()

        AdsManager.show()?.BannerAd(this, binding.adParent, object : BannerAdListener() {
            override fun onBannerAdNotShowing() {
                super.onBannerAdNotShowing()
                binding.frmAdView.visibility = View.GONE
            }

            override fun onBannerAdLoaded(bannerAdModel: BannerAdModel) {
                super.onBannerAdLoaded(bannerAdModel)
                this@MissingNumberPlay.bannerAdModel = bannerAdModel
                if (!hasWindowFocus()) this@MissingNumberPlay.bannerAdModel?.onPause()
            }
        })

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) bannerAdModel?.onResume() else bannerAdModel?.onPause()
    }

    private fun setBindingData() {
        binding.clickHandler = this
        binding.setClickHandler2 {
            when (it) {
                binding.btnKeyType -> {
                    binding.keyType = if (binding.keyType == 0) 1 else 0
                }

                binding.btn1,
                binding.btn2,
                binding.btn3,
                binding.btn4,
                binding.btn5,
                binding.btn6,
                binding.btn7,
                binding.btn8,
                binding.btn9,
                binding.btn0 -> {
                    enterDigit(((it as TextView).text.toString()))
                }

                binding.btnClear -> {}
                binding.btnErase -> {}

            }
        }
        binding.totalQue = numOfQuestion
        binding.enableAns = false
        binding.currentQue = 1
        binding.keyType = 0

        questionList.observe(this) {
            binding.currentQue = it.size
        }

        binding.tvTitle.text = gameType.title
        binding.tvTitle2.text = VariationType.MissingNumber.title

    }

    private fun generateQuestionAndAnswer() {
        Log.d(TAG, "generateQuestionAndAnswer: ")
        if (questionList.size == numOfQuestion) {
            doOnGameOver()
            return
        }
        resetQuestion()

        val model = getQuestionModel()

        setAnsView(model)
        hideDigit(model)

        highLightList.sortWith(Comparator { o1, o2 ->
            return@Comparator o1.index.compareTo(o2.index)
        })

        highLightList.forEachIndexed { index, digitModel ->
            digitModel.tv.setOnClickListener {
                highLightList[highLightSelectList.last()].clearHighLight()
                if (highLightList[highLightSelectList.last()].tv.text.toString().isEmpty()) {
                    highLightSelectList.removeLast()
                }
                if (highLightSelectList.contains(index)) {
                    highLightSelectList.remove(index)
                }
                highLightSelectList.add(index)
                highLightList[highLightSelectList.last()].highLight()
            }
        }
        highLightSelectList.add(0)
        highLightList[highLightSelectList.last()].highLight()

        binding.enableAns = true
        questionList.add(model)
    }

    private fun doOnGameOver() {
        var rightAnswer = 0
        var wrongAnswer = 0
        questionList.forEach {
            if (it.correctAns) rightAnswer++
            else wrongAnswer++
        }

        val dyKey = gameType.preKey + VariationType.MissingNumber.preKey
        val preTime = GamePreference.getLong(PrefKey.BestTime + dyKey)
        GamePreference.addLong(PrefKey.Played + dyKey, 1)
        GamePreference.addLong(PrefKey.RightAns + dyKey, rightAnswer.toLong())
        GamePreference.addLong(PrefKey.WrongAns + dyKey, wrongAnswer.toLong())
//        GamePreference.addLong(PrefKey.BestTime + dyKey, Math.min(preTime, wrongAnswer.toLong()))

        AdsManager.show()?.InterstitialAd(
            activity = this@MissingNumberPlay,
            adsListener = object : AdsListener() {
                override fun onAdClose() {
                    super.onAdClose()

                    Popup_Result(
                        activity = this@MissingNumberPlay,
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

    private fun hideDigit(model: QuestionModel) {
        val numOfDigit = max(model.preDigit, model.postDigit).toString().length
        var hideCounter = numOfDigit
        val hideIndex = ArrayList<Int>(hideCounter)
        val list = ArrayList<ArrayList<DigitModel>>(3)
        list.add(model.preDigitStatusList)
        list.add(model.postDigitStatusList)
        list.add(model.ansDigitStatusList)
        list.forEach { statusList ->
            statusList.forEach {
                it.showAns()
            }
        }

        var randomIndex: Int

        for (i in 0 until 4) {
            while (true) {
                randomIndex = Random.nextInt(list.size)
                val temp = list[randomIndex].random()
                if (hideIndex.contains(temp.index).not() && temp.hideAns()) {
                    hideCounter--
                    hideIndex.add(temp.index)
                    highLightList.add(temp)
                    if (i > 2) list.removeAt(randomIndex)
                    break
                }
            }
            if (hideCounter == 0) {
                if (sameAsLastQuestion(hideIndex) && numOfDigit != 1) {
                    hideDigit(model)
                } else {
                    lastQuestionHideList.addAll(hideIndex)
                    return
                }
            }
        }

    }

    private fun sameAsLastQuestion(newHideList: ArrayList<Int>): Boolean {
        lastQuestionHideList.forEach { old ->
            newHideList.forEach { new ->
                if (old != new) return false
            }
        }
        return lastQuestionHideList.isNotEmpty()
    }

    /*private fun hideDigit(model: QuestionModel) {
        when (numOfDigit[0]) {
            1 -> {
                while (true) {
                    when (Random.nextInt(3)) {
                        0 -> if (model.preDigitStatusList.random().hideAns()) return
                        1 -> if (model.postDigitStatusList.random().hideAns()) return
                        2 -> if (model.ansDigitStatusList.random().hideAns()) return
                    }
                }
            }

            2 -> {
                var lastIndex = -1
                var randomIndex = -1
                if (Random.nextBoolean()) {
                    model.preDigitStatusList.apply {
                        while (true) {
                            randomIndex = Random.nextInt(this.size)
                            if (this[randomIndex].hideAns()) {
                                lastIndex =
                                    randomIndex + (model.ansDigitStatusList.size - this.size)
                                return@apply
                            }
                        }
                    }
                } else {
                    model.postDigitStatusList.apply {
                        while (true) {
                            randomIndex = Random.nextInt(this.size)
                            if (this[randomIndex].hideAns()) {
                                lastIndex =
                                    randomIndex + (model.ansDigitStatusList.size - this.size)
                                return@apply
                            }
                        }
                    }
                }
                model.ansDigitStatusList.apply {
                    while (true) {
                        randomIndex = Random.nextInt(this.size)
                        if (randomIndex != lastIndex && this[randomIndex].hideAns()) {
                            return@apply
                        }
                    }
                }
            }

            3 -> {
                var lastIndex = -1
                var randomIndex = -1

                if (Random.nextBoolean()) {
                    model.preDigitStatusList.apply {
                        while (true) {
                            randomIndex = Random.nextInt(this.size)
                            if (this[randomIndex].hideAns()) {
                                lastIndex =
                                    randomIndex + (model.ansDigitStatusList.size - this.size)
                                return@apply
                            }
                        }
                    }
                } else {
                    model.postDigitStatusList.apply {
                        while (true) {
                            randomIndex = Random.nextInt(this.size)
                            if (this[randomIndex].hideAns()) {
                                lastIndex =
                                    randomIndex + (model.ansDigitStatusList.size - this.size)
                                return@apply
                            }
                        }
                    }
                }

                model.ansDigitStatusList.apply {
                    while (true) {
                        randomIndex = Random.nextInt(this.size)
                        if (randomIndex != lastIndex && this[randomIndex].hideAns()) {
                            return@apply
                        }
                    }
                }

            }

        }
    }*/

    private fun getQuestionModel(): QuestionModel {
        val symbol = getSymbol(gameType)

        val numOfDigit = digitList.random()[0]

        var preDigit = 0
        var postDigit = 0
        while (true) {
            preDigit = getQuestion(numOfDigit)
            postDigit = getQuestion(numOfDigit, preDigit)
            if (isValidQuestion(preDigit, postDigit)) break
        }
        val ans = getAnswer(preDigit, postDigit, symbol)

        val model = QuestionModel(
            preDigit = preDigit,
            postDigit = postDigit,
            symbolType = symbol,
            ans = ans
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

    private fun getAnswer(preDigit: Int, postDigit: Int, symbol: String): Int {
        return when (symbol) {
            symbolList[0] -> preDigit + postDigit
            symbolList[1] -> preDigit - postDigit
            symbolList[2] -> preDigit * postDigit
            else -> 0
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

        val hardness = Random.nextInt(3)

        var postDigit = 0

        if (digitLength == 1) {

            return when (hardness) {
                0 -> (0..5).random()

                1 -> (3..7).random()

                2 -> (5..9).random()

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

    private fun setAnsView(model: QuestionModel) {
        val preList = model.preDigitList
        val postList = model.postDigitList
        val ansList = model.ansDigitList

        val w = ScreenUtil.getSize(35)
        val h = w * 50 / 35
        val textSize = w * 35f / 35f
        val layoutParams = ViewGroup.LayoutParams(w, h)

        binding.row1.removeAllViews()
        var counter = 0
        for (i in preList.lastIndex downTo 0) {
            val tv = getTextView(textSize)
            tv.text = preList[i].toString()
            binding.row1.addView(tv, 0, layoutParams)
            model.preDigitStatusList.add(DigitModel(tv, preList[i], counter))
            counter++
        }

        counter = 0
        binding.row2.removeAllViews()
        for (i in postList.lastIndex downTo 0) {
            val tv = getTextView(textSize)
            tv.text = postList[i].toString()
            binding.row2.addView(tv, 0, layoutParams)
            model.postDigitStatusList.add(DigitModel(tv, postList[i], counter))
            counter++
        }

        counter = 0
        binding.row3.removeAllViews()
        for (i in ansList.lastIndex downTo 0) {
            val tv = getTextView(textSize)
            tv.text = ansList[i].toString()
            binding.row3.addView(tv, 0, layoutParams)
            model.ansDigitStatusList.add(DigitModel(tv, ansList[i], counter))
            counter++
        }

    }

    private fun getTextView(textSize: Float) =
        TextView(this@MissingNumberPlay).apply {
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            this.gravity = Gravity.CENTER
            this.setTextColor(Color.parseColor("#ffffff"))
            this.typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this@MissingNumberPlay.resources.getFont(R.font.candybeans)
            } else {
                ResourcesCompat.getFont(this@MissingNumberPlay, R.font.candybeans)
            }
        }

    fun pow(base: Int, exponent: Int): Int {
        return base.toDouble().pow(exponent.toDouble()).toInt()
    }

    private fun resetQuestion() {
        highLightList.clear()
        highLightSelectList.clear()
        binding.row1.removeAllViews()
        binding.row2.removeAllViews()
        binding.row3.removeAllViews()
        binding.enableAns = false
    }

    private fun enterDigit(digit: String) {
        val currentIndex = highLightSelectList.last()
        highLightList[currentIndex].tv.text = digit
        highLightList[currentIndex].clearHighLight()
        if (highLightList.size == highLightSelectList.size) checkAns()
        else {
            var newIndex = 0
            for (i in 0 until highLightList.size) {
                if (highLightSelectList.contains(i).not()) {
                    newIndex = i
                    break
                }
            }
            highLightSelectList.add(newIndex)
            highLightList[newIndex].highLight()
        }
    }

    private fun checkAns(isShowAns: Boolean = false) {
        binding.enableAns = false
        var isRightAns = true
        highLightList.forEach {
            if (it.tv.text.toString() != it.digit.toString()) {
                isRightAns = false
            }
        }
        if (isShowAns.not()) {
            questionList.last().correctAns = isRightAns
        }

        highLightRW()
        binding.lottieView.progress = 0f
        binding.lottieView.setAnimation(if (isRightAns) R.raw.right_tick else R.raw.wrong_tick)
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                GameSound.play()
                    ?.sound(if (isRightAns) R.raw.right_ans else R.raw.wrong_ans, volume = .6f)
            }

            override fun onAnimationEnd(animation: Animator) {
                handler.postDelayedClass({
                    binding.lottieView.removeAllAnimatorListeners()
                    binding.lottieView.visibility = View.GONE
                    if (isRightAns.not()) {
                        showAnsHint()
                    } else
                        generateQuestionAndAnswer()
                }, 600)
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

    private fun highLightRW() {
        highLightList.forEach {
            it.highLightRW(it.digit.toString() == it.tv.text.toString())
        }
    }

    private fun showAnsHint() {
        highLightList.forEachIndexed { index, model ->
            if (model.digit.toString() != model.tv.text.toString()) {
                model.tv.text = ""
                model.clearHighLight()
                handler.postDelayedClass({
                    model.tv.text = model.digit.toString()
                    if (index == highLightList.lastIndex) {
                        handler.postDelayedClass({
                            checkAns(true)
                        }, 500)
                    }
                }, (350 * index) + 400L)
            }
        }

    }

    override fun onBackPressed() {
        binding.btnHome.performClick()
    }

    override fun onClick(view: View?) {
        if (isDoubleClick()) return

        when (view) {

            binding.btnHome -> {
                Popup_Conformation(this@MissingNumberPlay)
                    .setDialogTitle("EXIT PLAYING")
                    .setDialogMsg("ARE YOU SURE,\nYOU WANT TO CLOSE PLAYING?")
                    .setButtonRight("NO")
                    .setButtonLeft("YES") {
                        resetQuestion()

                        AdsManager.show()?.InterstitialAd(
                            activity = this@MissingNumberPlay,
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
                Popup_Setting(this@MissingNumberPlay)
            }

            binding.btnDraw -> {
                Popup_DrawView(this@MissingNumberPlay)
            }

        }

    }

    data class QuestionModel(
        val preDigit: Int,
        val postDigit: Int,
        val symbolType: String,
        val ans: Int,
        var correctAns: Boolean = false
    ) {

        val preDigitList = preDigit.toString().toList()
        val postDigitList = "$symbolType$postDigit".toList()
        val ansDigitList = "=$ans".toList()

        val preDigitStatusList = ArrayList<DigitModel>()
        val postDigitStatusList = ArrayList<DigitModel>()
        val ansDigitStatusList = ArrayList<DigitModel>()

    }

    inner class DigitModel(val tv: TextView, val digit: Char, val index: Int) {

        fun hideAns(): Boolean {
            if (symbolList.contains(digit.toString()) || digit.toString() == "=") return false
            tv.text = ""
            tv.setBackgroundResource(R.drawable.bg_question)
            return true
        }

        fun showAns() {
            tv.text = digit.toString()
            tv.background = null
        }

        fun isHide(): Boolean {
            return tv.text.isEmpty()
        }

        fun highLight() {
            tv.setBackgroundResource(R.drawable.bg_question_stroke)
            tv.startAnimation(
                AnimationUtils.loadAnimation(
                    this@MissingNumberPlay,
                    R.anim.scale_anim
                )
            )
        }

        fun highLightRW(isRightAns: Boolean) {
            clearHighLight()
            tv.setBackgroundResource(if (isRightAns) R.drawable.bg_question_stroke_r else R.drawable.bg_question_stroke_w)
        }

        fun clearHighLight() {
            tv.setBackgroundResource(R.drawable.bg_question)
            tv.clearAnimation()
        }

    }

}
