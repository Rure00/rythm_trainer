package com.rure.rythmtrainer.metronome

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rure.rythmtrainer.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun MetronomeComponent(
    context: Context = LocalContext.current
) {
    val tag = "MetronomeComponent"

    var bpmValue by remember { mutableIntStateOf(60) }
    val metronome by remember {
        derivedStateOf { Metronome(context, bpmValue) }
    }

    var startMetronome by remember { mutableStateOf(false) }

    val rotateMax = 40f
    var rotate by remember { mutableFloatStateOf(0f) }


    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = -rotateMax,
        targetValue = rotateMax,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (60_000 / bpmValue), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    LaunchedEffect(startMetronome) {
        if(!startMetronome) {
            metronome.stop()
            return@LaunchedEffect
        }

        metronome.play()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        TextField(
            value = bpmValue.toString(),
            onValueChange = {
                if(it.toInt() >= 180) {
                    Toast.makeText(context, "180 이상 불가", Toast.LENGTH_SHORT).show()
                }
                bpmValue = it.toInt()
            },
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 40.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .width(16.dp)
                    .height(200.dp)
                    .graphicsLayer(
                        transformOrigin = TransformOrigin(
                            pivotFractionX = 0.5f,
                            pivotFractionY = 1f,
                        ),
                        rotationZ = if(!startMetronome) 0f else angle,
                    ),
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            ) { }
        }

        Text(
            text = if(!startMetronome) "시작" else "중지",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .background(shape = RoundedCornerShape(8.dp), color = Purple40)
                .clickable {
                    startMetronome = !startMetronome
                },
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}