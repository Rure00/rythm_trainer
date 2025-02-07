package com.rure.rythmtrainer.staff

import android.health.connect.datatypes.units.Length
import com.rure.rythmtrainer.R

data class Note(
    val beat: Int,
) {
    val length = when(beat) {
        2 -> 16
        4 -> 8
        8 -> 4
        16 -> 2
        else -> throw Exception("Wrong parameter in Note.beat) It should be 2, 4, 8 or 16.")
    }
    val imageId = when(beat) {
        2 -> R.drawable.note_2
        4 -> R.drawable.note_4
        8 -> R.drawable.note_8
        16 -> R.drawable.note_16
        else -> throw Exception("Wrong parameter in Note.beat) It should be 2, 4, 8 or 16.")
    }
}
