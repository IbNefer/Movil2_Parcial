package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map


private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

enum class BuzzType(val pattern: LongArray) {
    CORRECT(CORRECT_BUZZ_PATTERN),
    GAME_OVER(GAME_OVER_BUZZ_PATTERN),
    COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
    NO_BUZZ(NO_BUZZ_PATTERN)
}

class GameViewModel: ViewModel(){


    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val _buzzArray = MutableLiveData<BuzzType>()
    val buzzArray: LiveData<BuzzType>
        get() = _buzzArray

    val currentTimeString = currentTime.map { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val timer: CountDownTimer

    private lateinit var wordList: MutableList<String>

    companion object{

        const val DONE = 0L

        const val ONE_SECOND = 1000L

        const val COUNTDOWN_TIME = 60000L

        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    init {
        Log.i("GameViewModel","GameViewModel Created")
        _eventGameFinish.value = false
        _score.value = 0
        _word.value = ""
        resetList()
        nextWord()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished/ ONE_SECOND)
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS){
                    _buzzArray.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _buzzArray.value = BuzzType.GAME_OVER
                _eventGameFinish.value = true
            }
        }

        timer.start()

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel","GameViewModel Destroyed")
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
   fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }


    fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }


    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _buzzArray.value = BuzzType.CORRECT
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    fun onGameFinishComplete(){
        _eventGameFinish.value = false
    }


    fun onBuzzCompleted(){
        _buzzArray.value = BuzzType.NO_BUZZ
    }


}