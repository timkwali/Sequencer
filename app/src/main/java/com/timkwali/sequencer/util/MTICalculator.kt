package com.timkwali.sequencer.util

import com.timkwali.sequencer.util.Constants.BPM
import javax.inject.Inject

class MTICalculator @Inject constructor() {
    var second: Int = 0

    @JvmName("getSixteenth1")
    fun getSixteenth(): Int {
        return second / 16
    }

    @JvmName("getBeat1")
    fun getBeat(): Int {
        return getSixteenth() / 4
    }

    @JvmName("getBar1")
    fun getBar(): Int {
        return getBeat() / 4
    }

    fun getMTI(): String {
        return "${getBar()}:${getBeat().rem(4)}:${getSixteenth().rem(4)}"
    }

    fun isBar(second: Int): Boolean {
        if (second.rem(8) == 0){
            return true
        }
        return false
    }

    fun isBeat(second: Int): Boolean {
        if (second.rem(2) == 0){
            return true
        }
        return false
    }

    private fun barsToMilliSeconds(bars: Int): Int {
        return bars * (15.2 * BPM).toInt()
    }

    private fun beatsToMilliSeconds(beats: Int): Int {
        return beats * (60000 / BPM)
    }

    private fun sixteenthsToMilliSeconds(sixteenths: Int): Int {
        return sixteenths * (15000 / BPM)
    }

    fun getLoopStartTime(): Int {
        val endBar = if (getBar() > 5) {
            getBar() / 5
        } else {
            getBar()
        }
        val startBar = if (endBar - 2 < 0) 0 else endBar - 2
        return barsToMilliSeconds(startBar)
    }

    fun getLoopEndTime(): Int {
        val endBar = if(getBar() > 5) {
            getBar() / 5
        } else {
            getBar()
        }

        val eb = if(endBar < 2) 2 else endBar
        val endBeat = 3
        val endSixteenth = 3

        val endBarMilli = barsToMilliSeconds(eb)
        val endBeatsMilli = beatsToMilliSeconds(endBeat)
        val endSixteenthMilli = sixteenthsToMilliSeconds(endSixteenth)
        return endBarMilli + endBeatsMilli + endSixteenthMilli
    }
}