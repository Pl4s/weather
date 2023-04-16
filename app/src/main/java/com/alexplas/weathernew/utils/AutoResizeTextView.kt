package com.alexplas.weathernew.utils

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

class AutoResizeTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    private fun initialize() {
        maxLines = 1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textSize = findMaxTextSize(parentWidth)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    private fun findMaxTextSize(parentWidth: Int): Float {
        val paint = Paint()
        var textSize = textSize

        while (textSize > 0) {
            paint.textSize = textSize
            val textWidth = paint.measureText(text.toString())
            if (textWidth <= parentWidth) {
                break
            }
            textSize--
        }

        return textSize
    }
}

// 1. The class extends AppCompatTextView and inherits all its functionality. We override the onMeasure() method, which is called during the layout process to determine the size and position of the view.
//
// 2. We call super.onMeasure(widthMeasureSpec, heightMeasureSpec) to let the parent class handle the default measurement process.
//
// 3. We calculate the available width of the parent (in this case, the ConstraintLayout) using MeasureSpec.getSize(widthMeasureSpec).
//
// 4. We call the findMaxTextSize(parentWidth) method to find the maximum text size that can fit within the available width. Inside this method, we use a while loop that starts with the current text size and decreases it one by one until the text width, as calculated by paint.measureText(text.toString()), is less than or equal to the available width of the parent.
//
// 5. We set the new text size using setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize).
//
// 6. As a result, if the text in the AutoResizeTextView is too long to fit the available width, the text size will automatically decrease to fit the space without overlapping the icon. This behavior ensures that the text does not override the icon, making the layout look cleaner and more visually appealing.