import com.project.atlas.exceptions.InvalidDistanceException
import com.project.atlas.interfaces.EnergyType

class Calories: EnergyType() {
    override val typeName = "Calories"
    override val magnitude = "KCal"
    override fun calculateCost(distance: Double, consumption: Double, price: Double): Double {
        if (distance<0) throw InvalidDistanceException("La distancia no puede ser negativa")
        var velocity = if (consumption == 7.0) 20 else 5
        var result = (distance * consumption * 70) / velocity
        return result
    }
}