package com.example.androiddownloadmanager.utility

import android.content.ClipboardManager
import android.content.Context

const val B = 1
const val KB = B * 1024
const val MB = KB * 1024
const val GB = MB * 1024

fun getClipBoardText(context: Context): String =
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).text.toString()


interface TextPaste {
    fun onUpdate(text: String)
}

interface ChangeText {
    fun onUpdate()
}

fun getSize(size: Long): String {

    if (size in B until KB)
        return "${(size.toFloat() / B).format(2)} B"
    else if (size in KB until MB)
        return "${(size.toFloat() / KB).format(2)} KB"
    else if (size in MB until GB)
        return "${(size.toFloat() / MB).format(2)} MB"
    else if (size >= GB)
        return "${(size.toFloat() / GB).format(2)} GB"
    else
        return size.toString()

}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
fun Float.format(digits: Int) = "%.${digits}f".format(this)