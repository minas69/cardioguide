package com.example.views.utils

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import java.io.File

fun ImageView.set(
    @DrawableRes thumb: Int,
    vararg transformations: Transformation<Bitmap?>?
) {
    Glide.with(context)
        .asBitmap()
        .transition(BitmapTransitionOptions.withCrossFade(500))
        .transform(*transformations)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .load(thumb)
        .into(this)
}

fun ImageView.set(
    thumb: Bitmap,
    vararg transformations: Transformation<Bitmap?>?
) {
    Glide.with(context)
        .asBitmap()
        .transition(BitmapTransitionOptions.withCrossFade(500))
        .transform(*transformations)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .load(thumb)
        .into(this)
}

fun ImageView.set(
    thumb: File,
    vararg transformations: Transformation<Bitmap?>?
) {
    Glide.with(context)
        .asBitmap()
        .transition(BitmapTransitionOptions.withCrossFade(500))
        .transform(*transformations)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .load(thumb)
        .into(this)
}

fun ImageView.set(
    uri: Uri,
    vararg transformations: Transformation<Bitmap?>?
) {
    Glide.with(context)
        .asBitmap()
        .transition(BitmapTransitionOptions.withCrossFade(500))
        .transform(*transformations)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .load(uri)
        .into(this)
}
