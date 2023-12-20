package net.cakebuild.language.structure

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import icons.CakeIcons
import net.cakebuild.language.psi.CakeCodeLine
import net.cakebuild.language.psi.CakeFile
import net.cakebuild.language.psi.CakeLine
import net.cakebuild.language.psi.CakeTask
import net.cakebuild.language.psi.CakeTaskImpl
import net.cakebuild.language.psi.CakeTypes

class CakeStructureViewElement(private val myElement: NavigatablePsiElement) :
    StructureViewTreeElement,
    SortableTreeElement {
    override fun getValue(): Any {
        return myElement
    }

    override fun navigate(requestFocus: Boolean) {
        myElement.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return myElement.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return myElement.canNavigateToSource()
    }

    override fun getAlphaSortKey(): String {
        return myElement.name ?: myElement.text ?: "-"
    }

    override fun getPresentation(): ItemPresentation {
        val icon = CakeIcons.CakeAction
        val textAttributes: TextAttributesKey? = null
        val location = ""
        var presentation = myElement.presentation // presentation is set for CakeFile

        if (presentation == null && myElement is CakeTask) {
            val taskName =
                PsiTreeUtil.getChildrenOfTypeAsList(myElement, PsiElement::class.java)
                    .filter {
                        it.elementType == CakeTypes.TASK_NAME
                    }.map {
                        it.text
                    }.firstOrNull()

            if (taskName != null) {
                presentation =
                    PresentationData(
                        taskName,
                        location,
                        icon,
                        textAttributes,
                    )
            }
        }

        return presentation ?: PresentationData(
            myElement.name ?: myElement.text ?: "<unknown>",
            location,
            icon,
            textAttributes,
        )
    }

    override fun getChildren(): Array<TreeElement> {
        if (myElement is CakeFile) {
            return PsiTreeUtil.getChildrenOfTypeAsList(myElement, CakeLine::class.java).flatMap { l ->
                PsiTreeUtil.getChildrenOfTypeAsList(l, CakeCodeLine::class.java).flatMap { cl ->
                    PsiTreeUtil.getChildrenOfTypeAsList(cl, CakeTask::class.java).map {
                        CakeStructureViewElement(it as CakeTaskImpl)
                    }
                }
            }.toTypedArray()
        }

        return arrayOf()
    }
}
