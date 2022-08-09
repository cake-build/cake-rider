package net.cakebuild.run.script

import com.intellij.execution.configurations.RunConfigurationOptions

class CakeScriptConfigurationOptions : RunConfigurationOptions() {
    var taskName by string("Default")
    var scriptPath by string("build.cake")
    var verbosity by string("normal")
    var additionalArguments by string("")
}
