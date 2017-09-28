package net.cakebuild.toolwindow.view

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.ui.treeStructure.SimpleTreeBuilder
import com.intellij.ui.treeStructure.SimpleTreeStructure
import javax.swing.tree.DefaultTreeModel

class TasksStructure(val project: Project, tree: SimpleTree) : SimpleTreeStructure() {
    private val myTreeBuilder: SimpleTreeBuilder
    private val myRoot: SimpleNode

    init {
        tree.isRootVisible = false
        tree.showsRootHandles = true

        val data = getTasks(project)
        myRoot = RootNode(project, data)
        myTreeBuilder = SimpleTreeBuilder(tree, tree.model as DefaultTreeModel?, this, null)
        myTreeBuilder.initRoot()
        myTreeBuilder.expand(myRoot, null)
    }

    override fun getRootElement() = myRoot

    fun getTasks(project: Project): CakeTasks {
        return CakeTasks("Dummy cake file", arrayOf("Dummy task #1", "Dummy task #2"))
    }
}