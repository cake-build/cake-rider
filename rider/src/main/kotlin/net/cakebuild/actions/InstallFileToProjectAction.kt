package net.cakebuild.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.VcsShowConfirmationOption
import com.intellij.util.download.DownloadableFileService
import com.intellij.util.ui.ConfirmationDialog
import net.cakebuild.settings.CakeSettings
import net.cakebuild.shared.CakeProject

abstract class InstallFileToProjectAction : AnAction(), DumbAware {

    private val log = Logger.getInstance(InstallFileToProjectAction::class.java)

    protected abstract val fileType: FileType
    protected abstract val fileName: String
    protected abstract fun getUrl(settings: CakeSettings): String

    override fun actionPerformed(e: AnActionEvent) {
        val projectDir = CakeProject(e.project!!).getProjectDir()!!
        val existing = projectDir.findChild(fileName)
        if (null != existing && existing.exists()) {
            val dlg = ConfirmationDialog.requestForConfirmation(
                VcsShowConfirmationOption.STATIC_SHOW_CONFIRMATION,
                e.project!!,
                "File $fileName already exists. Override?",
                "Override?",
                Messages.getQuestionIcon()
            )
            if (!dlg) {
                return
            }

            log.trace("deleting $fileName, before downloading it new.")
            existing.delete(this)
        }
        val settings = CakeSettings.getInstance(e.project!!)
        val service = DownloadableFileService.getInstance()
        val fileDescription = service.createFileDescription(getUrl(settings), fileName)
        service.createDownloader(
            arrayOf(fileDescription).toMutableList(),
            fileName
        ).downloadFilesWithProgress(
            projectDir.path,
            e.project,
            null
        )
    }

    class ConfigurationFile : InstallFileToProjectAction() {
        override val fileName: String
            get() = "cake.config"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlConfigurationFile
        }
    }

    abstract class InstallBootstrapper : InstallFileToProjectAction() {
        protected abstract val extension: String

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override val fileName: String
            get() = "build.$extension"
    }

    class NetToolPs : InstallBootstrapper() {
        override val extension: String
            get() = "ps1"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetToolPs
        }
    }

    class NetToolSh : InstallBootstrapper() {
        override val extension: String
            get() = "sh"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetToolSh
        }
    }

    class NetFrameworkPs : InstallBootstrapper() {
        override val extension: String
            get() = "ps1"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetFrameworkPs
        }
    }

    class NetFrameworkSh : InstallBootstrapper() {
        override val extension: String
            get() = "sh"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetFrameworkSh
        }
    }

    class NetCorePs : InstallBootstrapper() {
        override val extension: String
            get() = "ps1"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetCorePs
        }
    }

    class NetCoreSh : InstallBootstrapper() {
        override val extension: String
            get() = "sh"

        override val fileType: FileType
            get() = FileTypes.PLAIN_TEXT

        override fun getUrl(settings: CakeSettings): String {
            return settings.downloadContentUrlBootstrapperNetCoreSh
        }
    }
}
