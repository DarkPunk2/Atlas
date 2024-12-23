import com.project.atlas.interfaces.EnergyType

class Diesel: EnergyType() {
    override val typeName = "Diesel"
    override val magnitude = "L/100 KM"
    override fun calculateCost(distance: Double, consumption: Double, fuelPrice: Double): Double {
        return distance * (consumption/100) * fuelPrice    }
}