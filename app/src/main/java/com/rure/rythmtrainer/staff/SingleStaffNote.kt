package com.rure.rythmtrainer.staff

data class SingleStaffNote(
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
