package com.assassin.origins.ui.base

import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.assassin.origins.R
import kotlinx.android.synthetic.main.ac_base.*

abstract class BaseActivity : AppCompatActivity(), UIAction {

    abstract fun getLayout(): Int

    private var mUIController: UIController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val w = window // in Activity's onCreate() for instance
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(R.layout.ac_base)

        mUIController = UIController(this, R.layout.base_ui_controller)

        val contentView = layoutInflater.inflate(getLayout(), null)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        root.addView(contentView, param)
        root.addView(mUIController?.rootView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mUIController?.destroy()
    }

    override fun showContent() {
        mUIController?.showContent()
    }

    override fun showLoading() {
        mUIController?.showLoading()
    }

    override fun showError() {
        mUIController?.showError()
    }

    override fun showEmpty() {
        mUIController?.showEmpty()
    }

    override fun setErrorText(error: String) {
        mUIController?.setErrorText(error)
    }

    override fun setEmptyText(empty: String) {
        mUIController?.setEmptyText(empty)
    }
}