package com.example.easyphone.utils

import android.graphics.Point
import com.example.easyphone.db.entities.ProgrammedButton

object ButtonFreePlaceFinder {
    fun find(width: Int, height: Int, tableWidth: Int, tableHeight: Int, buttons: List<ProgrammedButton>): MutableList<Point> {
        val table = Array(tableHeight) { Array(tableWidth, { false }) }
        val result: MutableList<Point> = mutableListOf()
        buttons.forEach {
            for (i in it.row..(it.row + it.height - 1)) {
                for (j in it.column..(it.column + it.width - 1)) {
                    table[i][j] = true
                }
            }
        }
        for (i in 0..(tableHeight - height)) {
            for (j in 0..(tableWidth - width)) {
                var flag = true
                for (k in i..(i + height - 1)) {
                    for (p in j..(j + width - 1)) {
                        flag = flag && !table[k][p]
                    }
                }
                if (flag) result.add(Point(i, j))
            }
        }

        return result
    }
}