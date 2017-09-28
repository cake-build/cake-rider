package net.cakebuild.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import net.cakebuild.model.CakeTasksDataKeys
import javax.swing.JTree

abstract class CakeTasksTreeAction : AnAction() {

    override fun update(e: AnActionEvent?) {
        super.update(e)
        if (e != null) {
            val p = e.presentation
            p.isEnabled = getTree(e) != null
        }
    }

    protected fun getTree(e: AnActionEvent?): JTree? {
        if (e == null) return null
        return CakeTasksDataKeys.PROJECTS_TREE.getData(e.dataContext)
    }

    class CollapseAll : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent?) {
            val tree = getTree(e) ?: return
            for (i in tree.rowCount-1 downTo 0)
                tree.collapseRow(i)
        }
    }

    class ExpandAll : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent?) {
            val tree = getTree(e) ?: return
            for (i in 0..tree.rowCount)
                tree.expandRow(i)
        }
    }

    class Refresh : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent?) {
            Messages.showMessageDialog(e!!.project, "Refresh!", "Cake", Messages.getInformationIcon())
        }
    }

    class Execute : CakeTasksTreeAction() {

        override fun actionPerformed(e: AnActionEvent?) {
            getTree(e!!) ?: return
            val selected = CakeTasksDataKeys.SELECTED_TASK_NODE.getData(e.dataContext) ?: return
            Messages.showMessageDialog(e.project, "Execute ${selected.name}!", "Cake", Messages.getInformationIcon())
        }
    }
}
