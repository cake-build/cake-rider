package net.cakebuild.run.script

import com.intellij.execution.configurations.ConfigurationTypeBase
import icons.CakeIcons

class CakeScriptConfigurationType :
    ConfigurationTypeBase(ID, "Cake Script", "Cake Script", CakeIcons.CakeAction) {
    val factory = CakeScriptConfigurationFactory(this)

    init {
        addFactory(factory)
    }

    companion object {
        const val ID = "CAKE_CONFIGURATION"
    }
}
