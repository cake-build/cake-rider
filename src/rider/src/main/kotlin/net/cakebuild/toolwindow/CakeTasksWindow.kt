package net.cakebuild.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import com.jetbrains.rd.platform.util.lifetime
import com.jetbrains.rd.util.lifetime.SequentialLifetimes
import com.jetbrains.rd.util.reactive.IViewableList
import com.jetbrains.rd.util.reactive.valueOrThrow
import com.jetbrains.rider.projectView.solution
import icons.CakeIcons
import icons.RiderIcons
import net.cakebuild.protocol.CakeFrostingProject
import net.cakebuild.protocol.cakeFrostingProjectsModel
import net.cakebuild.shared.CakeDataKeys
import net.cakebuild.shared.CakeFrostingTask
import net.cakebuild.shared.CakeScriptProject
import net.cakebuild.shared.CakeTask
import net.cakebuild.shared.CakeTaskRunMode
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
    private val sequentialLifetimes: SequentialLifetimes = SequentialLifetimes(project.lifetime)

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
        actionToolbar.targetComponent = this
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

    private fun getSelectedTask(): CakeTask? {
        val selected = tree.getSelectedNodes(DefaultMutableTreeNode::class.java) { it.isLeaf }.firstOrNull()
            ?: return null
        return when (selected.userObject) {
            is CakeTask -> selected.userObject as CakeTask
            else -> null
        }
    }

    fun isTaskSelected(): Boolean {
        return getSelectedTask() != null
    }

    private fun runTask(mode: CakeTaskRunMode) {
        val task = getSelectedTask() ?: return
        task.run(mode)
    }

    fun runTask() {
        runTask(CakeTaskRunMode.Run)
    }

    fun createRunConfig() {
        runTask(CakeTaskRunMode.SaveConfigOnly)
    }

    fun refreshTree() {
        val lifetime = sequentialLifetimes.next()
        val rootNode = DefaultMutableTreeNode(project.name)

        val cakeFrostingProjectsModel = project.solution.cakeFrostingProjectsModel
        val frostingNodes = mutableListOf<DefaultMutableTreeNode>()
        val projectLifetimes = SequentialLifetimes(lifetime)

        val updateFrostingNodes = { _: IViewableList.Event<CakeFrostingProject> ->
            frostingNodes.forEach(rootNode::remove)
            frostingNodes.clear()

            val projectLifetime = projectLifetimes.next()
            for (project in cakeFrostingProjectsModel.projects) {
                val projectNode = DefaultMutableTreeNode(project)

                val taskNodes = mutableListOf<DefaultMutableTreeNode>()

                val updateTaskNodes = { _: IViewableList.Event<String> ->
                    taskNodes.forEach(projectNode::remove)
                    taskNodes.clear()

                    for (task in project.tasks) {
                        val taskNode = DefaultMutableTreeNode(CakeFrostingTask(this.project, project, task))
                        projectNode.add(taskNode)
                        taskNodes.add(taskNode)
                    }

                    (tree.model as DefaultTreeModel).reload()
                }

                project.tasks.advise(projectLifetime, updateTaskNodes)

                rootNode.add(projectNode)
                frostingNodes.add(projectNode)
            }

            (tree.model as DefaultTreeModel).reload()
        }

        cakeFrostingProjectsModel.projects.advise(lifetime, updateFrostingNodes)

        val cakeProject = CakeScriptProject(project)
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
                    is CakeTask -> {
                        label.text = data.name
                        label.icon = AllIcons.Nodes.Editorconfig
                    }

                    is CakeScriptProject.CakeFile -> {
                        cell.toolTipText = data.file.path
                        label.text = data.file.nameWithoutExtension
                        label.icon = CakeIcons.CakeFileType
                    }

                    is CakeFrostingProject -> {
                        label.text = data.name.valueOrThrow
                        label.icon = RiderIcons.RunConfigurations.DotNetProject
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
