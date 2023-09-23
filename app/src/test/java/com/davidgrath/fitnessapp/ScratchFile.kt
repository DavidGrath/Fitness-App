package com.davidgrath.fitnessapp

import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class ScratchFile {

    @Test
    fun m() {
        for(i in 1984..2023) {
            println("$i - ${getMaxDay(i)}")
        }
    }

    fun getMaxDay(year: Int) : Int{
        val calendar = GregorianCalendar()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, 1) //0-based month list
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

}