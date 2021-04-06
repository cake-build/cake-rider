package net.cakebuild.toolwindow

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import net.cakebuild.shared.CakeDataKeys
import net.cakebuild.shared.CakeProject
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.ToolTipManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class CakeTasksWindow(private val project: Project) : SimpleToolWindowPanel(true, true) {

    private val tree: Tree = Tree()

    init {
        val scrollPane = ScrollPaneFactory.createScrollPane(tree)
        setContent(scrollPane)
        ToolTipManager.sharedInstance().registerComponent(tree)
        tree.cellRenderer = MyTreeCellRenderer()
        tree.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
        refreshTree()
        initToolbar()
        tree.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e?.clickCount == 2) {
                        runTask()
                    }
                    super.mouseClicked(e)
                }
            }
        )
    }

    private fun initToolbar() {
        val actionManager = ActionManager.getInstance()
        val actionToolbar =
            actionManager.createActionToolbar(
                "Cake Tasks Toolbar",
                actionManager.getAction("CakeTasksWindow") as ActionGroup,
                true
            )
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
        if (path == null) {
            expandCollapse(expand, TreePath(tree.model.root))
            return
        }

        val node = path.lastPathComponent as TreeNode
        for (it in node.children()) {
            val nextPath = path.pathByAddingChild(it)
            expandCollapse(expand, nextPath)
        }

        if (expand) {
            if (!tree.isExpanded(path)) {
                tree.expandPath(path)
            }
        } else {
            if (!tree.isCollapsed(path)) {
                tree.collapsePath(path)
            }
        }
    }

    private fun getSelectedTask(): CakeProject.CakeTask? {
        val selected = tree.getSelectedNodes(DefaultMutableTreeNode::class.java) { it.isLeaf }.firstOrNull()
            ?: return null
        return when (selected.userObject) {
            is CakeProject.CakeTask -> selected.userObject as CakeProject.CakeTask
            else -> null
        }
    }

    fun isTaskSelected(): Boolean {
        return getSelectedTask() != null
    }

    fun runTask() {
        val task = getSelectedTask() ?: return
        task.run(CakeProject.CakeTaskRunMode.Run)
    }

    fun createRunConfig() {
        val task = getSelectedTask() ?: return
        task.run(CakeProject.CakeTaskRunMode.SaveConfigOnly)
    }

    fun refreshTree() {
        val rootNode = DefaultMutableTreeNode(project.name)
        val cakeProject = CakeProject(project)

        for (cakeFile in cakeProject.getCakeFiles()) {
            val fileNode = DefaultMutableTreeNode(cakeFile)
            rootNode.add(fileNode)

            for (task in cakeFile.getTasks()) {
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

    class MyTreeCellRenderer : DefaultTreeCellRenderer() {

        private val log = Logger.getInstance(MyTreeCellRenderer::class.java)

        init {
            setClosedIcon(null)
            setOpenIcon(null)
            setLeafIcon(null)
        }

        override fun getTreeCellRendererComponent(
            tree: JTree,
            value: Any,
            sel: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): Component {
            val cell: Component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
            if (cell is JComponent) {
                val label = cell as JLabel
                cell.toolTipText = null
                when (val data = (value as DefaultMutableTreeNode).userObject) {
                    is CakeProject.CakeTask -> {
                        label.text = data.taskName
                    }
                    is CakeProject.CakeFile -> {
                        cell.toolTipText = data.file.path
                        label.text = data.file.nameWithoutExtension
                    }
                    else -> {
                        // do not modify the label - it's probably fine the way it is.
                        log.trace(
                            "found userObject of ${
                            data?.javaClass?.name ?: "[null]"
                            } to override the label '${label.text}'"
                        )
                    }
                }
            }
            return cell
        }
    }
}
