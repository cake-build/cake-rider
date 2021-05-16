package net.cakebuild.actions

import com.intellij.ide.fileTemplates.DefaultCreateFromTemplateHandler
import com.intellij.ide.fileTemplates.FileTemplate
import net.cakebuild.language.CakeFileType

class CakeCreateFromTemplateHandler : DefaultCreateFromTemplateHandler() {
    override fun handlesTemplate(template: FileTemplate) = template.isTemplateOfType(CakeFileType.INSTANCE)
}
