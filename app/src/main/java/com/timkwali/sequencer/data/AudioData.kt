package com.timkwali.sequencer.data

import com.timkwali.sequencer.R
import javax.inject.Inject

class AudioData @Inject constructor() {
    val audioListA = listOf(R.raw.a1, R.raw.a2, R.raw.a3)
    val imageListA = listOf(R.drawable.a1, R.drawable.a2, R.drawable.a3)

    val audioListB = listOf(R.raw.b1, R.raw.b2, R.raw.b3)
    val imageListB = listOf(R.drawable.b1, R.drawable.b2, R.drawable.b3)
}
