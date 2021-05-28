package com.example.easyphone.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.gridlayout.widget.GridLayout
import com.example.easyphone.R
import com.example.easyphone.db.entities.ProgrammedButton

class ButtonsPlacementData(
    val buttons: List<Pair<ProgrammedButton, Button>>,
    val textViews: List<Pair<Point, TextView>>
)

class ButtonsDisplayer {
    fun display(
        resources: Resources,
        buttons: List<ProgrammedButton>,
        buttonTable: GridLayout,
        context: Context?,
        onClick: (view: View, button: ProgrammedButton) -> Unit
    ): ButtonsPlacementData {
        val buttonClickAnim = AlphaAnimation(1f, 0.7f)

        val buttonViews = mutableListOf<Pair<ProgrammedButton, Button>>()
        val textViews = mutableListOf<Pair<Point, TextView>>()

        val maxColumns = resources.getInteger(R.integer.column_number)
        val maxRows = resources.getInteger(R.integer.row_number)

        val free = ButtonFreePlaceFinder.find(
            1,
            1,
            maxColumns,
            maxRows,
            buttons
        )
        buttonTable.removeAllViews()

        free.forEach {
            val textView = TextView(context)

            val layoutParams = GridLayout.LayoutParams()
            layoutParams.rowSpec = GridLayout.spec(it.x, 1, GridLayout.FILL, 1f)
            layoutParams.columnSpec = GridLayout.spec(it.y, 1, GridLayout.FILL, 1f)

            // TODO choose another way to define margins
            layoutParams.setMargins(8)

            textView.setText("")

            textViews.add(Pair(it, textView))
            buttonTable.addView(textView, layoutParams)
        }

        buttons.forEach {
            val buttonData = it
            val button = Button(context, null, R.attr.myButtonStyle)

            val layoutParams = GridLayout.LayoutParams()
            layoutParams.columnSpec = GridLayout.spec(it.column, it.width, GridLayout.FILL, if (it.height == maxColumns) { it.width.toFloat() } else { 1f })
            layoutParams.rowSpec = GridLayout.spec(it.row, it.height, GridLayout.FILL, if (it.width == maxColumns) { it.height.toFloat() } else { 1f })

            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT

            // TODO choose another way to define margins
            layoutParams.setMargins(8)

            button.setBackgroundColor(it.color)
            button.setText(it.text)
            button.contentDescription = it.text

            button.setOnClickListener {
                buttonClickAnim.setDuration(150);
                it.startAnimation(buttonClickAnim)
                onClick(it, buttonData)
            }

            buttonViews.add(Pair(it, button))
            buttonTable.addView(button, layoutParams)
        }

        return ButtonsPlacementData(
            buttonViews,
            textViews
        )
    }
}