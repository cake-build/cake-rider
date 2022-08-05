package net.cakebuild.run

import com.intellij.execution.configurations.RunConfigurationOptions

class CakeConfigurationOptions : RunConfigurationOptions() {
    var taskName by string("Default")
    var scriptPath by string("build.cake")
    var verbosity by string("normal")
    var additionalArguments by string("")
}
