package se.thetc.diceroller

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import java.util.*
import java.util.concurrent.TimeUnit


interface TimerView {
    val onStartButtonClicked: Observable<Unit>
    val onPauseButtonClicked: Observable<Unit>
    val onResetButtonClicked: Observable<Unit>
    fun startTimer()
    fun pauseTimer()
    fun resetTimer()
}


class TimerPresenter(private val view: TimerView) {
    private fun bindStartTimer(): Disposable {
        return view.onStartButtonClicked.subscribe {
            view.startTimer()
        }
    }

    private fun bindPauseTimer(): Disposable {
        return view.onPauseButtonClicked.subscribe {
            view.pauseTimer()
        }
    }

    private fun bindResetTimer(): Disposable {
        return view.onResetButtonClicked.subscribe {
            view.resetTimer()
        }
    }

    fun start(): Disposable {
        return CompositeDisposable(
            bindStartTimer(),
            bindPauseTimer(),
            bindResetTimer()
        )
    }
}


class TimerActivity : AppCompatActivity(), TimerView {

    //write messages in the code to degub it
    val TAG = TimerActivity::class.java.simpleName

    private val bag: CompositeDisposable = CompositeDisposable()
    private val presenter: TimerPresenter by lazy { TimerPresenter(this) }

    private val textViewCountDown: TextView
        get() = findViewById(R.id.text_view_countdown)
    private val buttonStartPause: Button
        get() = findViewById(R.id.button_start_pause)
    private val buttonReset: Button
        get() = findViewById(R.id.button_reset)

    private var countDownTimer: CountDownTimer? = null

    private val onStartPauseButtonClicked: Observable<Unit> by lazy {
        buttonStartPause.clicks().share()
    }

    override val onStartButtonClicked: Observable<Unit>
        get() =
            onStartPauseButtonClicked.filter { countDownTimer == null }
    override val onPauseButtonClicked: Observable<Unit>
        get() =
            onStartPauseButtonClicked.filter { countDownTimer != null }
    override val onResetButtonClicked: Observable<Unit>
        get() =
            buttonReset.clicks()

    override fun startTimer() {
        Log.d(TAG, "start timer")

        //Schedules the execution of the given task (countDownTimer) with the given time delay.
        AndroidSchedulers.mainThread().scheduleDirect({
            countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeftInMillis = millisUntilFinished
                    updateCountDownText()
                }

                override fun onFinish() {
                    timerRunning = false
                    buttonStartPause.setText(getString(R.string.start))
                    buttonStartPause.setVisibility(View.INVISIBLE)
                    buttonReset.setVisibility(View.VISIBLE)
                }
            }.start()
        }, 100, TimeUnit.MILLISECONDS)

        timerRunning = true
        buttonStartPause.setText(getString(R.string.pause))
        buttonReset.setVisibility(View.INVISIBLE)
    }

    override fun pauseTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        timerRunning = false
        buttonStartPause.setText(getString(R.string.resume))
        buttonReset.setVisibility(View.VISIBLE)
    }

    override fun resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        buttonStartPause.setText(R.string.start)
        buttonReset.setVisibility(View.INVISIBLE);
        buttonStartPause.setVisibility(View.VISIBLE);
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        textViewCountDown.text = timeLeftFormatted
    }

    private var timerRunning: Boolean = false

    private var timeLeftInMillis = startTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        buttonStartPause.clicks().subscribe {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }.addTo(bag)

        buttonReset.clicks().subscribe { resetTimer() }.addTo(bag)

        updateCountDownText()
        presenter.start().addTo(bag)
    }

    //An object declaration inside a class can be marked with the companion keyword
    companion object {
        //const are compile time constants. Meaning that their value has to be assigned during compile time, unlike val, where it can be done at runtime.
        private const val startTimeInMillis: Long = 600000
    }
}
