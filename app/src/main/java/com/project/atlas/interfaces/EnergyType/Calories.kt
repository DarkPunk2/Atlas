import com.project.atlas.interfaces.EnergyType

class Calories: EnergyType() {
    override val typeName = "Calories"
    override val magnitude = "KCal"
    override fun calculateCost(distance: Double, consumption: Double, price: Double): Double {
        var velocity = 5
        if (consumption == 7.0) velocity = 20
        var result = (distance * consumption * 70) / velocity
        return result
    }
}