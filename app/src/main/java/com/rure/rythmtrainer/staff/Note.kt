package com.rure.rythmtrainer.staff

import android.health.connect.datatypes.units.Length

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
}
