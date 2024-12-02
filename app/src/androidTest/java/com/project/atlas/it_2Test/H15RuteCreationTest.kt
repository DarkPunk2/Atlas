package com.project.atlas.it_2Test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.atlas.Models.UserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.osmdroid.util.GeoPoint

@RunWith(AndroidJUnit4::class)
class H15RuteCreationTest {

    @Test
    fun h15P1Test() = runBlocking{
        //Given
        val start = GeoPoint(39.991791, -0.063847)
        val end = GeoPoint(39.990966, -0.063091)
        //When

        //Then

    }
}