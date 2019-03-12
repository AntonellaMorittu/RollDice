package se.thetc.diceroller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.keys
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

interface AboutMeView {
    val onDoneButtonClicked: Observable<Unit>
    val onEnterPressedInEditText: Observable<Unit>
    val onMore2ButtonClicked: Observable<Unit>
    val editTextValue: String

    fun hideKeyboard()
    fun hideEditText()
    fun displayText(text: String)
    fun showTimer()
}

class AboutMePresenter(private val view: AboutMeView) {
    private fun addBandName() {
        view.displayText(view.editTextValue)
        view.hideEditText()
        view.hideKeyboard()
    }

    private fun bindAddBandName(): Disposable {
        return view.onDoneButtonClicked.subscribe {
            addBandName()
        }
    }

    private fun bindEnterPressedInEditText(): Disposable {
        return view.onEnterPressedInEditText.subscribe {
            addBandName()
        }
    }

    private fun bindShowColorMyViews(): Disposable {
        return view.onMore2ButtonClicked.subscribe {
            view.showTimer()
        }
    }

    fun start(): Disposable {
        return CompositeDisposable(
            bindAddBandName(),
            bindEnterPressedInEditText(),
            bindShowColorMyViews()
        )
    }
}


class AboutMeActivity : AppCompatActivity(), AboutMeView {
    override val onEnterPressedInEditText: Observable<Unit> get() = editTextView.keys()
        .filter { it.keyCode == KeyEvent.KEYCODE_ENTER }
        .map { Unit }

    private val editTextView: EditText
        get() = findViewById(R.id.band_edit)
    private val doneButton: Button
        get() = findViewById(R.id.done_button)
    private val bandNameTextView: TextView
        get() = findViewById(R.id.band_text)
    private val more2Button: AppCompatImageButton
        get() = findViewById(R.id.more_button2)

    override val editTextValue: String get() = editTextView.text.toString()
    override val onDoneButtonClicked: Observable<Unit> get() = doneButton.clicks()
    override val onMore2ButtonClicked: Observable<Unit> get() = more2Button.clicks()

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = findViewById<View>(android.R.id.content).rootView.windowToken
        imm.hideSoftInputFromWindow(token, 0)
    }

    override fun hideEditText() {
        editTextView.visibility = View.GONE
        doneButton.visibility = View.GONE
        bandNameTextView.visibility = View.VISIBLE
    }

    override fun displayText(text: String) {
        val bandNameTextView = findViewById<TextView>(R.id.band_text)
        bandNameTextView.text = text
    }

    override fun showTimer() {
        val intent = Intent(this, TimerActivity::class.java)
        startActivity(intent)
    }

    private val presenter: AboutMePresenter by lazy { AboutMePresenter(this) }
    private val bag: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)

        presenter.start().addTo(bag)
    }
}
