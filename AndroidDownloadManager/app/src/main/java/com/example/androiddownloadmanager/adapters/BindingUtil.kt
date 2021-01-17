package com.example.androiddownloadmanager.adapters

import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.androiddownloadmanager.RequestState
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("setVisibilityOfTextViews")
fun beHideOrVisible(view: TextView, state: RequestState) {
    if (state == RequestState.SUCCESSFUL)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE
}

@BindingAdapter("setVisibilityOfEditText")
fun beHideOrVisible(view: EditText, state: RequestState) {
    if (state == RequestState.SUCCESSFUL)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE
}

@BindingAdapter("setVisibilityOfProgressBar")
fun beHideOrVisible(view: ProgressBar, state: RequestState) {
    if (state == RequestState.LOADING)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.INVISIBLE
}

@BindingAdapter("setError")
fun setError(view: TextInputLayout, message: String?) {
    view.error = message
}

@BindingAdapter("clareError")
fun clareError(view: TextInputLayout, status: RequestState) {
    if (status != RequestState.ERROR)
        view.error = null
}
