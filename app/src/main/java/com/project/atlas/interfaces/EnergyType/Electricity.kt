import com.project.atlas.interfaces.EnergyType

class Electricity: EnergyType() {
    override val typeName = "Electricity"
    override val magnitude = "KW-H/100 KM"
}