package net.cakebuild.model

import com.intellij.openapi.actionSystem.DataKey
import net.cakebuild.toolwindow.view.CakeTaskNode
import javax.swing.JTree

class CakeTasksDataKeys {
    companion object {
        val PROJECTS_TREE : DataKey<JTree> = DataKey.create("cake.tasks.tree")
        val SELECTED_TASK_NODE : DataKey<CakeTaskNode> = DataKey.create("cake.tasks.selected_task")
    }
}