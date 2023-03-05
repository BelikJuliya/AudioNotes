package com.example.audionotes

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.ViewBindingProperty
import by.kirich1409.viewbindingdelegate.viewBinding

inline fun <reified T : ViewBinding> Fragment.viewBinding(): ViewBindingProperty<Fragment, T> {
    return viewBinding()
}

fun View.lightStatusBar(activity: FragmentActivity?, @ColorRes color: Int = R.color.white) {
    setStatusBarColor(activity, context.color(color))
}

fun View.setStatusBarColor(activity: FragmentActivity?, @ColorInt color: Int, isDarkColor: Boolean = false) {
    activity?.window?.apply {
        var flags = systemUiVisibility
        flags = if (isDarkColor) {
            flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        systemUiVisibility = flags
        statusBarColor = color
    }
}

fun Context.color(colorRes: Int) = ContextCompat.getColor(this, colorRes)
