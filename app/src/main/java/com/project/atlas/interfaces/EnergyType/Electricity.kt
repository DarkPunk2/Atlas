import com.project.atlas.exceptions.InvalidDistanceException
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.services.ElectricityPriceService

class Electricity: EnergyType() {
    override val typeName = "Electricity"
    override val magnitude = "KW-H/100 KM"
    private lateinit var electricityPriceService: ElectricityPriceService
    override fun calculateCost(distance: Double, consumption: Double, price: Double): Double {
        if (distance<0) throw InvalidDistanceException("La distancia no puede ser negativa")
        var result = (distance/100) * consumption * price/1000
        return result
    }
}
