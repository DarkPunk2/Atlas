import com.project.atlas.interfaces.EnergyType

class Petrol98: EnergyType() {
    override val typeName = "Petrol 98"
    override val magnitude = "L/100 KM"
    override fun calculateCost(distance: Double, consumption: Double, fuelPrice: Double) {
        TODO("Not yet implemented")
    }
}