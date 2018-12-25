package com.assassin.origins.splash.core

import android.content.Context
import android.util.Log
import com.assassin.origins.splash.R
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(2f)
            .build()
        builder.setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
        )
        builder.setMemoryCache(LruResourceCache((calculator.memoryCacheSize * 0.8f).toLong()))
        builder.setBitmapPool(LruBitmapPool((calculator.bitmapPoolSize * 0.8f).toInt().toLong()))
        builder.setLogLevel(Log.DEBUG)
    }
}