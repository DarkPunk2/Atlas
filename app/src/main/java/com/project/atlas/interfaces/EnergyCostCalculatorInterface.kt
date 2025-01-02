package com.project.atlas.interfaces

import com.project.atlas.models.RouteModel

interface EnergyCostCalculatorInterface {
    suspend fun calculateCost(route: RouteModel): Double
}