package net.cakebuild.run

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.openapi.components.StoredProperty

class CakeConfigurationOptions : RunConfigurationOptions() {
    private val storedTaskName: StoredProperty<String?> =
        string("Default").provideDelegate(this, "taskName")
    var taskName: String?
        get() = storedTaskName.getValue(this)
        set(v) { storedTaskName.setValue(this, v) }

    private val storedScriptPath: StoredProperty<String?> =
        string("build.cake").provideDelegate(this, "scriptPath")
    var scriptPath: String?
        get() = storedScriptPath.getValue(this)
        set(v) { storedScriptPath.setValue(this, v) }

    private val storedVerbosity: StoredProperty<String?> =
        string("normal").provideDelegate(this, "verbosity")
    var verbosity: String?
        get() = storedVerbosity.getValue(this)
        set(v) { storedVerbosity.setValue(this, v) }

    private val storedAdditionalArguments: StoredProperty<String?> =
        string("").provideDelegate(this, "additionalArguments")
    var additionalArguments: String?
        get() = storedAdditionalArguments.getValue(this)
        set(v) { storedAdditionalArguments.setValue(this, v) }
}
