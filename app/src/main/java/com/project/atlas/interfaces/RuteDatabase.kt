package com.project.atlas.interfaces

import com.project.atlas.models.RuteModel

interface RuteDatabase {
    suspend fun add(rute: RuteModel): Boolean
    suspend fun getAll(): List<RuteModel>
    fun remove(): Boolean
}