package net.cakebuild.toolwindow.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.ui.treeStructure.SimpleTreeBuilder
import com.intellij.ui.treeStructure.SimpleTreeStructure
import net.cakebuild.util.CakeFilePath
import javax.swing.tree.DefaultTreeModel

class TasksStructure(project: Project, tree: SimpleTree) : SimpleTreeStructure() {
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
        val cakeFile = CakeFilePath.getCakeFilePath(project)
            ?: return CakeTasks(null, arrayOf("Dummy task #", "Dummy task #2"))

        val contents = VfsUtil.loadText(cakeFile)
        val regex = Regex("Task\\s*?\\(\\s*?\"(.*?)\"\\s*?\\)")
        val results = regex.findAll(contents)
        val tasks = mutableListOf<String>()
        for (result in results) {
            tasks.add(result.groups[1]!!.value)
        }
        return CakeTasks(cakeFile, tasks.toTypedArray())
    }
}