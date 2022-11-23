package net.cakebuild.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import net.cakebuild.shared.Constants

@State(
    name = "net.cakebuild.settings.CakeSettings",
    storages = [ Storage(Constants.settingsStorageFile) ]
)
class CakeSettings : PersistentStateComponent<CakeSettings> {
    companion object {
        fun getInstance(project: Project): CakeSettings {
            return project.getService(CakeSettings::class.java)
        }
    }

    private val log = Logger.getInstance(CakeSettings::class.java)

    var cakeTaskParsingRegex = "Task\\s*?\\(\\s*?\"(.*?)\"\\s*?\\)"
    var cakeVerbosity = "normal"
    var cakeUseNetTool = false
    var cakeRunner = "~/.dotnet/tools/dotnet-cake"
    var cakeRunnerOverrides = mutableMapOf(Pair("^.*windows.*$", "\${USERPROFILE}\\.dotnet\\tools\\dotnet-cake.exe"))
    var cakeScriptSearchPaths: Collection<String> = mutableListOf(".")
    var cakeScriptSearchIgnores: Collection<String> = mutableListOf(".*/tools/.*")

    // do we need a settings-UI for these?
    var downloadContentUrlConfigurationFile =
        "https://cakebuild.net/download/configuration"
    var downloadContentUrlBootstrapperNetFrameworkPs =
        "https://cakebuild.net/download/bootstrapper/dotnet-framework/powershell"
    var downloadContentUrlBootstrapperNetFrameworkSh =
        "https://cakebuild.net/download/bootstrapper/dotnet-framework/bash"
    var downloadContentUrlBootstrapperNetCorePs =
        "https://cakebuild.net/download/bootstrapper/dotnet-core/powershell"
    var downloadContentUrlBootstrapperNetCoreSh =
        "https://cakebuild.net/download/bootstrapper/dotnet-core/bash"
    var downloadContentUrlBootstrapperNetToolPs =
        "https://cakebuild.net/download/bootstrapper/dotnet-tool/powershell"
    var downloadContentUrlBootstrapperNetToolSh =
        "https://cakebuild.net/download/bootstrapper/dotnet-tool/bash"

    fun getCurrentCakeRunner(): Array<String> {
        if (cakeUseNetTool) {
            return arrayOf("dotnet", "cake")
        }

        val os = System.getProperty("os.name")
        var runner = cakeRunner
        cakeRunnerOverrides.forEach forEach@{
            val regex = Regex(it.key, RegexOption.IGNORE_CASE)
            if (regex.matches(os)) {
                log.trace("os $os matches regex ${it.key}")
                runner = it.value
                return@forEach
            }
        }

        log.trace("runner setting resolved to: $runner")

        // shell like tilde expansion
        if (runner.startsWith("~")) {
            runner = "\${HOME}${runner.substring(1)}"
            log.trace("tilde-expanded runner: $runner")
        }

        val variableRegex = Regex("\\\$\\{([^}]+)}")
        runner = runner.replace(variableRegex) {
            val varName = it.groupValues[1]
            val varValue = System.getenv(varName) ?: ""
            log.trace("resolved environment variable $varName to $varValue")
            varValue
        }

        log.trace("resolved Cake runner to $runner")
        return arrayOf(runner)
    }

    override fun getState(): CakeSettings {
        return this
    }

    override fun loadState(state: CakeSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
