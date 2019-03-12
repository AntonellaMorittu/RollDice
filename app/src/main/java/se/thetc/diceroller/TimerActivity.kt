package se.thetc.diceroller

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.*


class TimerActivity : AppCompatActivity() {
    private val bag = CompositeDisposable()

    private val startTimeInMillis: Long = 600000

    private lateinit var textViewCountDown: TextView
    private lateinit var buttonStartPause: Button
    private lateinit var buttonReset: Button

    private lateinit var countDownTimer: CountDownTimer

    private var timerRunning: Boolean = false

    private var timeLeftInMillis = startTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        textViewCountDown = findViewById(R.id.text_view_countdown)

        buttonStartPause = findViewById(R.id.button_start_pause)

        buttonReset = findViewById(R.id.button_reset)

        buttonStartPause.clicks().subscribe {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }.addTo(bag)

        buttonReset.clicks().subscribe { resetTimer() }.addTo(bag)

        updateCountDownText()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                timerRunning = false
                buttonStartPause.setText("Start")
                buttonStartPause.setVisibility(View.INVISIBLE)
                buttonReset.setVisibility(View.VISIBLE)
            }
        }.start()

        timerRunning = true
        buttonStartPause.setText("pause")
        buttonReset.setVisibility(View.INVISIBLE)
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        timerRunning = false
        buttonStartPause.setText("Start")
        buttonReset.setVisibility(View.VISIBLE)
    }

    private fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        buttonReset.setVisibility(View.INVISIBLE)
        buttonStartPause.setVisibility(View.VISIBLE)
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        textViewCountDown.text = timeLeftFormatted
    }

}
