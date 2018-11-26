package com.assassin.origins.ui.base

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.assassin.origins.R
import kotlinx.android.synthetic.main.ac_base.*

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayout(): Int

    private var mUIController: UIController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_base)

        mUIController = UIController(this, R.layout.base_ui_controller)

        val contentView = layoutInflater.inflate(getLayout(), null)
        root.addView(contentView)
        root.addView(mUIController?.rootView)

        // test
        mUIController?.showLoading()
        Handler().postDelayed({
            mUIController?.showEmpty()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mUIController?.destroy()
    }

}