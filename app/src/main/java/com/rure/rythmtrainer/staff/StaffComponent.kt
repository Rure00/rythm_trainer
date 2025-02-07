package com.rure.rythmtrainer.staff

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun StaffComponent(
    bpm: Int,
    noteNum: Int,
    headNote: Note,
    staffNotes: List<SingleStaffNote>,
    context: Context = LocalContext.current
) {
    val tag = "StaffComponent"

    LaunchedEffect(true) {
        val soundPlayer = SoundPlayer(
            context, bpm, noteNum, headNote, mutableListOf<Note>().apply {
                staffNotes.forEach { node ->
                    node.notes.forEach {
                        this.add(it)
                    }
                }
            }
        )

        delay(3000)
        Log.d(tag, "Play!) ${soundPlayer.play()}")

    }

    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {


        Column(
            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            staffNotes.forEach {
                SingleStaffLine(it.notes)
            }
        }
    }
}

@Composable
private fun SingleStaffLine(
    notes: List<Note>
) {
    Box(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 사선지
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(4) {
                Spacer(modifier = Modifier.height(2.dp).fillMaxWidth().background(Color.Black))
            }
        }

        // 음표
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            notes.forEach {
                Image(
                    painter = painterResource(it.imageId),
                    modifier = Modifier.size(36.dp).weight(it.length.toFloat()),
                    contentDescription = null,
                    alignment = Alignment.CenterStart
                )
            }
        }
    }
}



@Composable
fun TestStaffComponent() {
    val staffNotes = listOf(
        SingleStaffNote(
            4, Note(4), listOf(
                Note(4), Note(4), Note(4), Note(4)
            )
        ),
        SingleStaffNote(
            4, Note(4), listOf(
                Note(8), Note(8), Note(4), Note(4), Note(4)
            )
        ),
        SingleStaffNote(
            4, Note(4), listOf(
                Note(2), Note(4), Note(4)
            )
        ),
    )

    StaffComponent(60, 4, Note(4), staffNotes)
}