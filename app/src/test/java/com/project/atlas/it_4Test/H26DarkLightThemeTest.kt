package com.project.atlas.it_4Test

import Calories
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.assertEquals
import org.junit.Test


class H26DarkLightThemeTest {
    val isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

    @Test
    fun H26P1Test() {
        val isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

        fun toggleTheme(state: MutableState<Boolean>) {
            state.value = !state.value
        }

        assertEquals(false, isDarkTheme.value)

        toggleTheme(isDarkTheme)

        assertEquals(true, isDarkTheme.value)

    }

}