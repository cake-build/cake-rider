package net.cakebuild.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.util.PsiUtilCore
import net.cakebuild.language.CakeLanguage

class CakeContext : TemplateContextType("Cake", "Cake") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val lang = PsiUtilCore.getLanguageAtOffset(templateActionContext.file, templateActionContext.startOffset)
        return CakeLanguage.`is`(lang)
    }
}
