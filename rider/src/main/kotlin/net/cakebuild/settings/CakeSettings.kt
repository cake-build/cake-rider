package net.cakebuild.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import net.cakebuild.shared.Constants

@State(name = "net.cakebuild.settings.CakeSettings",
    storages = [ Storage(Constants.settingsStorageFile) ])
class CakeSettings : PersistentStateComponent<CakeSettings> {
    companion object {
        fun getInstance(project: Project): CakeSettings {
            return ServiceManager.getService(project, CakeSettings::class.java)
        }
    }

    var cakeFileExtension = "cake"
    var cakeTaskParsingRegex = "Task\\s*?\\(\\s*?\"(.*?)\"\\s*?\\)"
    var cakeVerbosity = "normal"
    var cakeRunner = "dotnet-cake"
    var cakeRunnerOverrides = mapOf(Pair("^.*windows.*$", "dotnet-cake.exe"))

    override fun getState(): CakeSettings {
        return this
    }

    override fun loadState(state: CakeSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
