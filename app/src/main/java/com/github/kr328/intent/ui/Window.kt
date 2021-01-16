package com.github.kr328.intent.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableAmbient
import androidx.compose.runtime.ambientOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Insets(val start: Dp, val top: Dp, val end: Dp, val bottom: Dp)

val AmbientInsets: ProvidableAmbient<Insets> = ambientOf { Insets(0.dp, 0.dp, 0.dp, 0.dp) }