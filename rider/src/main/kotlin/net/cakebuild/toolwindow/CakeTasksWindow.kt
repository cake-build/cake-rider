package net.cakebuild.toolwindow

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.SimpleTree
import net.cakebuild.model.CakeTasksDataKeys
import net.cakebuild.toolwindow.view.CakeTaskNode
import net.cakebuild.toolwindow.view.TasksStructure
import javax.swing.tree.TreeSelectionModel

class CakeTasksWindow(private val project: Project, private val toolWindowEx: ToolWindowEx)
    : SimpleToolWindowPanel(true, true) {

    private val myTree = SimpleTree()
    private var myStructure: TasksStructure? = null

    init {
        myTree.selectionModel.selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION

        val actionManager = ActionManager.getInstance()
        val actionToolbar = actionManager.createActionToolbar("Cake Tasks Toolbar",
                actionManager.getAction("CakeTasksWindow") as ActionGroup, true)
        actionToolbar.setTargetComponent(myTree)
        setToolbar(actionToolbar.component)
        setContent(ScrollPaneFactory.createScrollPane(myTree))

        scheduleStructureRequest()
    }

    override fun getData(dataId: String?): Any? {
        if (CakeTasksDataKeys.PROJECTS_TREE.`is`(dataId)) return myTree
        if (CakeTasksDataKeys.SELECTED_TASK_NODE.`is`(dataId)) return myTree.selectedNode as CakeTaskNode
        return super.getData(dataId)
    }

    private fun initStructure() {
        myStructure = TasksStructure(project, myTree)
    }

    private fun scheduleStructureRequest() {
        // TODO: Invoke later. Don't run on main thread
        if (!toolWindowEx.isVisible) return

        myTree.setPaintBusy(true)
        try {
            val shouldCreate = myStructure == null
            if (shouldCreate)
                initStructure()
        }
        finally {
            myTree.setPaintBusy(false)
        }
    }
}