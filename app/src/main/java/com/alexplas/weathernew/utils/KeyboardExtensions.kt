package com.alexplas.weathernew.utils

import android.app.Activity
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

fun View.showKeyboard() = (this.context as? Activity)?.showKeyboard()
fun View.hideKeyboard() = (this.context as? Activity)?.hideKeyboard()

fun Fragment.showKeyboard() = activity?.showKeyboard()
fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Activity.showKeyboard() = WindowInsetsControllerCompat(window, window.decorView).show(
    WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard() = WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
