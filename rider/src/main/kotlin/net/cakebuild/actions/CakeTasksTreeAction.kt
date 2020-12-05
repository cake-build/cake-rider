package net.cakebuild.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import net.cakebuild.shared.CakeDataKeys
import net.cakebuild.toolwindow.CakeTasksWindow

abstract class CakeTasksTreeAction : AnAction() {

    protected fun getWindow(e: AnActionEvent): CakeTasksWindow {
        return CakeDataKeys.TASKS_WINDOW.getData(e.dataContext)!!
    }

    class CollapseAll : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent) {
            val win = getWindow(e)
            win.collapseAll()
        }
    }

    class ExpandAll : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent) {
            val win = getWindow(e)
            win.expandAll()
        }
    }

    class Refresh : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent) {
            val win = getWindow(e)
            win.refreshTree()
        }
    }

    class Execute : CakeTasksTreeAction() {
        override fun actionPerformed(e: AnActionEvent) {
            val win = getWindow(e)
            win.runTask()
        }

        override fun update(e: AnActionEvent) {
            super.update(e)
            val win = getWindow(e)
            e.presentation.isEnabled = win.isTaskSelected()
        }
    }
}
