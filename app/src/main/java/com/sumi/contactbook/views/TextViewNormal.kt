package com.sumi.contactbook.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewNormal(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    init {
        this.typeface = Typeface.createFromAsset(context.assets, "Mulish_Regular.ttf")
    }
}