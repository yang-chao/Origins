package com.assassin.origins.ui.base

interface UIAction {

    fun showLoading()

    fun showContent()

    fun showError()

    fun showEmpty()

    fun setErrorText(error: String)

    fun setEmptyText(empty: String)
}