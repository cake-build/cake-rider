package net.cakebuild.toolwindow.view

import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.SimpleNode
import icons.CakeIcons

class RootNode(project: Project, private val data: CakeTasks) : SimpleNode(project) {
    override fun getChildren(): Array<SimpleNode> {
        return arrayOf(CakeFileNode(project!!, data))
    }
}

class CakeFileNode(project: Project, private val data: CakeTasks) : SimpleNode(project) {
    init {
        templatePresentation.addText(data.cakeFile?.name ?: "Shouldn't happen", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        templatePresentation.setIcon(CakeIcons.Cake)
    }

    override fun getChildren(): Array<SimpleNode> {
        return data.tasks.map { CakeTaskNode(project!!, it, data.cakeFile!!.parent) }.toTypedArray()
    }

    override fun update(presentation: PresentationData?) {
        super.update(presentation)
        presentation?.setIcon(CakeIcons.Cake)
    }
}

class CakeTaskNode(project: Project, val task: String, val cakeFileDir: VirtualFile) : SimpleNode(project) {
    init {
        templatePresentation.addText(task, SimpleTextAttributes.REGULAR_ATTRIBUTES)
        templatePresentation.setIcon(CakeIcons.Cake)
    }

    override fun getChildren(): Array<SimpleNode> = emptyArray()

    override fun update(presentation: PresentationData?) {
        super.update(presentation)
        presentation?.setIcon(CakeIcons.Cake)
    }
}