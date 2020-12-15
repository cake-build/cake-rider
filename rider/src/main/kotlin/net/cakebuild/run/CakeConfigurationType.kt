package net.cakebuild.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import icons.CakeIcons

class CakeConfigurationType :
    ConfigurationTypeBase("CAKE_CONFIGURATION", "Cake", "Cake", CakeIcons.Cake) {

    val cakeFactory = CakeConfigurationFactory(this)

    init {
        addFactory(cakeFactory)
    }
}
