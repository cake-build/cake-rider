package net.cakebuild.language.codeInsight

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import icons.CakeIcons
import net.cakebuild.language.psi.CakeTypes

class TaskLineMarkerProvider : LineMarkerProviderDescriptor() {
    override fun getName() = "Cake task"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.elementType == CakeTypes.TASK_NAME) {
            val builder: NavigationGutterIconBuilder<PsiElement> =
                NavigationGutterIconBuilder
                    .create(CakeIcons.CakeFileType)
                    .setTargets(element.parent)
                    .setTooltipText("Task: ${element.text}")
            return builder.createLineMarkerInfo(element)
        }

        return null
    }
}
