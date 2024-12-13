import com.project.atlas.interfaces.EnergyType

class Electricity: EnergyType() {
    override val typeName = "Electricity"
    override val magnitude = "KW-H/100 KM"
    override fun calculateCost(distance: Double, consumption: Double) {
        TODO("Not yet implemented")
    }
}