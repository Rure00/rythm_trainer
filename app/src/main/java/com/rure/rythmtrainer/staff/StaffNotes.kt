package com.rure.rythmtrainer.staff

import kotlin.jvm.Throws

data class StaffNotes(
    val noteNum: Int,
    val headNote: Note,
    val notes: List<Note>
) {
    init {
        val beatSum: Int = notes.sumOf {
            it.length
        }

        if(beatSum != headNote.length * noteNum) {
            throw Exception("Not Allowed StaffNote.")
        }
    }
}
