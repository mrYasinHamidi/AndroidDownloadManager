package com.example.androiddownloadmanager.utility

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class PastedEditText : TextInputEditText {
    private val pasteListeners: ArrayList<TextPaste> = arrayListOf()
    private val changeListeners: ArrayList<ChangeText>? = arrayListOf()

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
//        for (i in changeListeners?:0..0) i is .onUpdate()
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        when (id) {
            android.R.id.paste -> onPasteText()
        }
        return super.onTextContextMenuItem(id)
    }

    fun addOnPasteTextListener(textPaste: TextPaste) {
        pasteListeners.add(textPaste)
    }

    fun addOnChangeTextListener(changeText: ChangeText) {
        changeListeners?.add(changeText)
    }

    private fun onPasteText() {
        context?.let {
            for (listener in pasteListeners) listener.onUpdate(getClipBoardText(it))
        }
    }
}

