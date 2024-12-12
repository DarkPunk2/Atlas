import com.project.atlas.interfaces.EnergyType

class Calories: EnergyType() {
    override val typeName = "Calories"
    override val magnitude = "KCal"
    override fun calculateCost(distance: Double, consumption: Double) {
        TODO("Not yet implemented")
    }

}