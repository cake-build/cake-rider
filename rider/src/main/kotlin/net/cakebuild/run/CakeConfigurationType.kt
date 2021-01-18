package net.cakebuild.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import icons.CakeIcons

class CakeConfigurationType :
    ConfigurationTypeBase("CAKE_CONFIGURATION", "Cake", "Cake", CakeIcons.CakeAction) {

    val cakeFactory = CakeConfigurationFactory(this)

    init {
        addFactory(cakeFactory)
    }
}
