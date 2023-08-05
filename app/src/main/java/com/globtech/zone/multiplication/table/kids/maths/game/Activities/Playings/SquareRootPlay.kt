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
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.ActivitySquareRootPlayBinding
import me.grantland.widget.AutofitTextView
import kotlin.random.Random

class SquareRootPlay : BaseActivity(), View.OnClickListener {

    private lateinit var variationType: VariationType
    lateinit var binding: ActivitySquareRootPlayBinding

    var colorAnim: ValueAnimator? = null
    var progressAnim: ObjectAnimator? = null

    var questionList = MutableListLiveData<QuestionModel>()
    var ansViewList = ArrayList<AutofitTextView>(4)
    lateinit var difficulty: BooleanArray
    var isTimePlay = false
    var time = 20

    val handler = GameHandlerClass()
    var retryQuestionList = MutableListLiveData<QuestionModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkAppWasKilled()) return

        binding = ActivitySquareRootPlayBinding.inflate(layoutInflater)
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
        binding.totalQue = 10
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
        Log.d(TAG, "generateQuestionAndAnswer: ")
        if (questionList.size == numOfQuestion) {
            doOnGameOver()
            return
        }
        resetQuestion()

        val model = getQuestionModel()

        binding.tvQ1.text = model.preDigit.toString()
        binding.ivSymbol.setImageResource(
            if (model.preDigit.toString().length == 1) R.drawable.ic_root2
            else R.drawable.ic_root
        )

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

        val dyKey = GameType.SquareRoot.preKey + variationType.preKey
        val preTime = GamePreference.getLong(PrefKey.BestTime + dyKey)
        GamePreference.addLong(PrefKey.Played + dyKey, 1)
        GamePreference.addLong(PrefKey.RightAns + dyKey, rightAnswer.toLong())
        GamePreference.addLong(PrefKey.WrongAns + dyKey, wrongAnswer.toLong())
        if (avgTime != 0) GamePreference.addLong(
            PrefKey.BestTime + dyKey,
            Math.min(preTime, avgTime.toLong())
        )

        Popup_Result(
            activity = this@SquareRootPlay,
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

        var range: IntRange

        range = if (difficulty[0]) 1..15
        else if (difficulty[1]) 15..30
        else 1..30

        var ans = range.random()
        while (true) {
            for (i in 0 until questionList.size) {
                if (questionList[i].ans == ans) {
                    ans = -1
                    break
                }
            }
            if (ans == -1) ans = range.random()
            else break
        }

        val preDigit = ans * ans
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

        return QuestionModel(
            preDigit = preDigit.toInt(),
            ans = ans,
            ansList = ansList
        )
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

    override fun onBackPressed() {
        binding.btnHome.performClick()
    }

    override fun onClick(view: View?) {
        if (isDoubleClick()) return

        when (view) {

            binding.btnHome -> {
                colorAnim?.pause()
                progressAnim?.pause()
                Popup_Conformation(this@SquareRootPlay)
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
                Popup_Setting(this@SquareRootPlay)
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
                Popup_DrawView(this@SquareRootPlay)
                    .setOnDismissListener {
                        colorAnim?.resume()
                        progressAnim?.resume()
                    }
            }

        }

    }

    data class QuestionModel(
        val preDigit: Int,
        val ans: Int,
        val ansList: ArrayList<Int>
    ) {

        var correctAns: Boolean = false
        var ansTime: Int = 0

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