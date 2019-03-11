package se.thetc.diceroller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlin.random.Random

//MVP implementation

/*
VIEW
Who: Combination Activity/Fragment/View and its contract i.e. interface.
Purpose: Do all UI related stuff and all that needs Android context.
Job: Whenever it needs to do some action, it should trigger respective function of Presenter using object. It should not have any business logic.
*/

interface MainView {
    val onRollButtonClicked: Observable<Unit>
    val onMoreButtonClicked: Observable<Unit>
    fun rollDice()
    fun showAboutMe()
}

/*
PRESENTER
It is a separate class which should not have Android context.
Purpose: Act as a mediator between two main layers of the architecture View and Model.
Job: Trigger respective function of Model depending on the request made by View.
*/
class MainPresenter(private val view: MainView) {
    private fun bindRollDice(): Disposable {
        return view.onRollButtonClicked.subscribe {
            view.rollDice()
        }
    }

    private fun bindShowAboutMe(): Disposable {
        return view.onMoreButtonClicked.subscribe {
            view.showAboutMe()
        }
    }

    fun start(): Disposable {
        return CompositeDisposable(
            bindRollDice(),
            bindShowAboutMe()
        )
    }
}


/*
MODEL is referred to as Interactor
Who: Database, models (schema), API calls and all other business logic is part of this layer. This is where all business logic will be located.
Purpose: To separate business logic so we can test it in isolation without View. This also helps us to reuse code-base across platforms.
Job: Get/calculate data or error to shown on the UI.
*/
class MainActivity : AppCompatActivity(), MainView {
    private val rollButton: Button
        get() = findViewById(R.id.roll_button)
    private val diceImageView: ImageView
        get() = findViewById(R.id.dice_image)
    private val moreButton: AppCompatImageButton
        get() = findViewById(R.id.more_button)

    override val onRollButtonClicked: Observable<Unit> get() = rollButton.clicks()
    override val onMoreButtonClicked: Observable<Unit> get() = moreButton.clicks()

    override fun showAboutMe() {
        val intent = Intent(this, AboutMeActivity::class.java)
        startActivity(intent)
    }

    override fun rollDice() {
        val myLayout: View = findViewById(R.id.layout)
        val randomInt = Random.nextInt(1, 6) + 1
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

        diceImageView.setImageResource(drawableResource)
    }

    private val presenter: MainPresenter by lazy { MainPresenter(this) }
    private val bag: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //val moreButton: ImageButton = findViewById(R.id.more_button)

        rollButton.text = "Let's roll!"
        
        presenter.start().addTo(bag)

    }
}