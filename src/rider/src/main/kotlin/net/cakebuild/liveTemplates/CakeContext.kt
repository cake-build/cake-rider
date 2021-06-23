package net.cakebuild.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.openapi.project.ProjectLocator
import net.cakebuild.settings.CakeSettings

class CakeContext : TemplateContextType("Cake", "Cake") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val virtFile = templateActionContext.file.viewProvider.virtualFile
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
