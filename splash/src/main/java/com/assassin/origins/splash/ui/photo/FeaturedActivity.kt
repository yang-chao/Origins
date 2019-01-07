package com.assassin.origins.splash.ui.photo

import android.os.Bundle
import androidx.core.view.LayoutInflaterCompat
import com.assassin.origins.core.viewmodel.DataViewModel
import com.assassin.origins.splash.R
import com.assassin.origins.splash.api.model.Photo
import com.assassin.origins.splash.core.GlideApp
import com.assassin.origins.splash.core.viewmodel.PhotoViewModel
import com.assassin.origins.ui.request.RequestActivity
import com.mikepenz.iconics.context.IconicsLayoutInflater2
import kotlinx.android.synthetic.main.ac_featured.*
import android.os.Build

class FeaturedActivity : RequestActivity<Photo>() {

    override fun createViewModel(): DataViewModel<Photo> {
        return PhotoViewModel(application)
    }

    override fun getLayout(): Int {
        return R.layout.ac_featured
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, IconicsLayoutInflater2(delegate))
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

        }
    }

    override fun onDataChanged(data: Photo?) {
        super.onDataChanged(data)

        if (data == null) {
            return
        }
        GlideApp.with(this)
            .load(data.urls.regular)
            .centerCrop()
            .into(image)
        GlideApp.with(this)
            .load(data.user.profileImage.medium)
            .circleCrop()
            .into(author_avatar)
        author_name.text = data.user.username
    }
}
