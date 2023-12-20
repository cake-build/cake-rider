package net.cakebuild.run.frosting

import com.intellij.execution.configurations.RunProfile
import com.jetbrains.rider.debugger.DotNetProgramRunner

class CakeFrostingProgramRunner : DotNetProgramRunner() {
    override fun canRun(
        executorId: String,
        runConfiguration: RunProfile,
    ): Boolean {
        return executorId == "Run" && runConfiguration is CakeFrostingConfiguration
    }
}
