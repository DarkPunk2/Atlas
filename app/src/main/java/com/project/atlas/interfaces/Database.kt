package com.project.atlas.interfaces

interface Database {
    fun add(): Boolean
    fun getAll()
    fun remove(): Boolean
}