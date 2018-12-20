package com.assassin.origins.ui.base

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.assassin.origins.R

class UIController constructor(private val context: AppCompatActivity, layoutId: Int) : UIAction {

    val rootView: View = LayoutInflater.from(context).inflate(layoutId, null)
    private var loadingProgressBar: ProgressDialog? = null
    private val emptyView: TextView
    private val errorView: TextView

    init {
        emptyView = rootView.findViewById(R.id.empty)
        errorView = rootView.findViewById(R.id.error)

        loadingProgressBar = ProgressDialog(context)
        loadingProgressBar!!.setMessage("Loading...")
    }

    override fun showLoading() {
        if (loadingProgressBar != null && !loadingProgressBar!!.isShowing) {
            loadingProgressBar?.show()
        }
        rootView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    override fun showContent() {
        if (loadingProgressBar != null && loadingProgressBar!!.isShowing) {
            loadingProgressBar!!.dismiss()
        }
        rootView.visibility = View.GONE
        errorView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    override fun showError() {
        if (loadingProgressBar != null && loadingProgressBar!!.isShowing) {
            loadingProgressBar!!.dismiss()
        }
        rootView.visibility = View.VISIBLE
        errorView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    override fun showEmpty() {
        if (loadingProgressBar != null && loadingProgressBar!!.isShowing) {
            loadingProgressBar!!.dismiss()
        }
        rootView.visibility = View.VISIBLE
        emptyView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
    }

    override fun setErrorText(error: String) {
        errorView.text = error
    }

    override fun setEmptyText(empty: String) {
        emptyView.text = empty
    }

    fun destroy() {
        if (loadingProgressBar?.isShowing!! && !context.isFinishing) {
            loadingProgressBar?.dismiss()
        }
    }

}