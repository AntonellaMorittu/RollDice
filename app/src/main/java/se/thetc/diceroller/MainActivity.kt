package se.thetc.diceroller

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val bag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.text = "Let's roll!"

        rollButton.clicks().subscribe {
            rollDice()
        }.addTo(bag)
    }

    private fun rollDice() {
        val resultText: TextView = findViewById(R.id.result_text)

        val randomInt = Random.nextInt(0,6) + 1

        resultText.text = randomInt.toString()
    }
}