package net.cakebuild.installers

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.diagnostic.Logger
import net.cakebuild.shared.nuget.SearchQueryService

/**
 * Helps with installing/updating/checking for Cake.Tool
 * Beware that all here is synchronous.
 * At least wrap this in a Background Task
 * (Or you'll end up with `java.lang.Throwable: Synchronous execution on EDT`)
 */
class CakeNetToolInstaller {

    private val log = Logger.getInstance(CakeNetToolInstaller::class.java)

    fun getInstalledCakeVersion(): CakeVersion? {
        val regex = Regex(
            "^cake\\.tool\\s+([\\d.]+(-\\S+)?)",
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
        ) // https://regex101.com/r/nC8uxu/2

        val commandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withExePath("dotnet")
            .withParameters("tool", "list", "--global")
        val output = ExecUtil.execAndGetOutput(commandLine)
        if (output.exitCode != 0) {
            log.warn("Unable to detect if Cake.Tool was installed. Exitcode: ${output.exitCode}")
            return null
        }

        val match = regex.findAll(output.stdout).firstOrNull() ?: return null
        return CakeVersion.parse(match.groups[1]!!.value)
    }

    fun installCake(): Boolean {
        val commandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withExePath("dotnet")
            .withParameters("tool", "install", "Cake.Tool", "--global")
        val output = ExecUtil.execAndGetOutput(commandLine)
        return output.exitCode == 0
    }

    fun updateCake(): Boolean {
        val commandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withExePath("dotnet")
            .withParameters("tool", "update", "Cake.Tool", "--global")
        val output = ExecUtil.execAndGetOutput(commandLine)
        return output.exitCode == 0
    }

    fun getLatestAvailableVersion(): CakeVersion? {
        val nugetVersion = SearchQueryService().getVersion("Cake.Tool", false)
        if (nugetVersion == null) {
            log.warn("no version found for Cake.Tool")
            return null
        }

        log.trace("current Cake.Tool version is $nugetVersion")
        return CakeVersion.parse(nugetVersion)
    }
}
