package net.cakebuild.toolwindow

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import net.cakebuild.shared.CakeDataKeys
import net.cakebuild.shared.CakeProject
import javax.swing.tree.*

class CakeTasksWindow(private val project: Project)
    : SimpleToolWindowPanel(true, true) {

    private val tree: Tree = Tree()

    init {
        val scrollPane = ScrollPaneFactory.createScrollPane(tree)
        setContent(scrollPane)
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        refreshTree()
        initToolbar()
    }

    private fun initToolbar() {
        val actionManager = ActionManager.getInstance()
        val actionToolbar =
            actionManager.createActionToolbar(
                "Cake Tasks Toolbar",
                actionManager.getAction("CakeTasksWindow") as ActionGroup,
                true)
        actionToolbar.setTargetComponent(this)
        toolbar = actionToolbar.component
    }

    fun expandAll() {
        expandCollapse(true)
    }

    fun collapseAll() {
        expandCollapse(false)
    }

    private fun expandCollapse(expand: Boolean, path: TreePath? = null) {
        if(path == null) {
            expandCollapse(expand, TreePath(tree.model.root))
            return
        }

        val node = path.lastPathComponent as TreeNode
        node.children().asIterator().forEach {
            val nextPath = path.pathByAddingChild(it)
            expandCollapse(expand, nextPath)
        }

        if(expand) {
            if(!tree.isExpanded(path)) {
                tree.expandPath(path)
            }
        } else {
            if(!tree.isCollapsed(path)) {
                tree.collapsePath(path)
            }
        }
    }

    fun isTaskSelected(): Boolean {
        val selected = tree.getSelectedNodes(DefaultMutableTreeNode::class.java) { it.isLeaf }
        return selected.any()
    }

    fun runTask() {
        val selected = tree.getSelectedNodes(DefaultMutableTreeNode::class.java) { it.isLeaf }.firstOrNull() ?: return
        val task = selected.userObject as CakeProject.CakeTask
        task.run()
    }

    fun refreshTree() {
        val rootNode = DefaultMutableTreeNode(project.name)
        val cakeProject = CakeProject(project)

        for(cakeFile in cakeProject.getCakeFiles()) {
            val fileNode = DefaultMutableTreeNode(cakeFile.file.name)
            rootNode.add(fileNode)

            for(task in cakeFile.getTasks()) {
                val taskNode = DefaultMutableTreeNode(task)
                fileNode.add(taskNode)
            }
        }

        tree.model = DefaultTreeModel(rootNode)
    }

    override fun getData(dataId: String): Any? {
        if (CakeDataKeys.TASKS_WINDOW.`is`(dataId)) return this
        return super.getData(dataId)
    }
}