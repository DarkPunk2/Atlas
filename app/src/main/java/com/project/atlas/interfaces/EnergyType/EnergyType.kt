package com.project.atlas.interfaces

abstract class EnergyType {
    abstract val typeName: String
    abstract val magnitude: String
    override fun toString(): String {
        return typeName
    }
}