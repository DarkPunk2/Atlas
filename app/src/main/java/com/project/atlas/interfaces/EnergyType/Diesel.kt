import com.project.atlas.interfaces.EnergyType

class Diesel: EnergyType() {
    override val typeName = "Diesel"
    override val magnitude = "L/100 KM"
}