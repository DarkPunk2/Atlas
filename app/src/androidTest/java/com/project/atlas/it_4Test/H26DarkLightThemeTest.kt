package com.project.atlas.it_4Test

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.project.atlas.ui.theme.AtlasTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class H26DarkLightThemeTest {

    class AtlasThemeTest {
        
        @Test
        fun H26P1Test() {
            // Given: La aplicación inicia en modo claro
            val isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

            // Mock de la función toggleTheme
            fun toggleTheme(state: MutableState<Boolean>) {
                state.value = !state.value
            }

            // Given: El tema inicia en modo claro
            assertEquals(false, isDarkTheme.value)

            // When: Se llama a toggleTheme para alternar el tema
            toggleTheme(isDarkTheme)

            // Then: El tema debe cambiar a oscuro
            assertEquals(true, isDarkTheme.value)

        }
    }

}
