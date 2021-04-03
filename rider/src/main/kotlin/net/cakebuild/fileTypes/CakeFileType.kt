package net.cakebuild.fileTypes

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VirtualFile
import icons.CakeIcons
import net.cakebuild.settings.CakeSettings

class CakeFileType : LanguageFileType(CakeLanguage), FileTypeIdentifiableByVirtualFile {
    override fun getName() = CakeLanguage.displayName
    override fun getDescription() = "Cake scripts"
    override fun getIcon() = CakeIcons.CakeFileType
    override fun getDefaultExtension() = EXTENSION

    override fun isMyFileType(file: VirtualFile): Boolean {
        val extension = file.extension?.toLowerCase() ?: ""

        if (extension.equals(defaultExtension, true)) {
            return true
        }

        val projects = ProjectLocator.getInstance().getProjectsForFile(file)
        val extensions = projects.map {
            CakeSettings.getInstance(it).cakeFileExtension.toLowerCase()
        }.distinct()

        return extensions.contains(extension.toLowerCase())
    }

    companion object {
        const val EXTENSION = "cake"
        val INSTANCE = CakeFileType()
    }
}
