package se.thetc.diceroller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val bag = CompositeDisposable()

    lateinit var diceImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rollButton: Button = findViewById(R.id.roll_button)
        val moreButton: ImageButton = findViewById(R.id.more_button)

        rollButton.text = "Let's roll!"

        rollButton.clicks().subscribe {
            rollDice()
        }.addTo(bag)

        diceImage = findViewById(R.id.dice_image)

        moreButton.clicks().subscribe(){
            val intent = Intent(this, AboutMeActivity :: class.java)
            startActivity(intent)
        }.addTo(bag)


    }

    private fun rollDice() {

        val myLayout: View = findViewById(R.id.layout)
        val randomInt = Random.nextInt(1,6) + 1
        val drawableResource = when (randomInt) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        if (drawableResource == R.drawable.dice_6) {
            Toast.makeText(this, "Not today Satan, not today", Toast.LENGTH_SHORT).show()
            myLayout.setBackgroundColor(Color.BLACK)
        } else {
            myLayout.setBackgroundColor(Color.parseColor("#f2598c"))
        }

        diceImage.setImageResource(drawableResource)
    }


}