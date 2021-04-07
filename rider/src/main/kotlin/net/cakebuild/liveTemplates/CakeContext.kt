package net.cakebuild.liveTemplates

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.openapi.project.ProjectLocator
import com.intellij.psi.PsiFile
import net.cakebuild.settings.CakeSettings

class CakeContext : TemplateContextType("Cake", "Cake") {
    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        val virtFile = file.viewProvider.virtualFile
        val extension = virtFile.extension?.toLowerCase() ?: ""

        if (extension.equals("cake", true)) {
            return true
        }

        val projects = ProjectLocator.getInstance().getProjectsForFile(virtFile)
        val extensions = projects.map {
            CakeSettings.getInstance(it).cakeFileExtension.toLowerCase()
        }.distinct()

        return extensions.contains(extension.toLowerCase())
    }
}
