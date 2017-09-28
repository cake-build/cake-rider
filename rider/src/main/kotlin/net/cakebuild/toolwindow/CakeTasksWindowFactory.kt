package net.cakebuild.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.content.ContentFactory

class CakeTasksWindowFactory : ToolWindowFactory, DumbAware {
    private var myToolWindow: ToolWindow? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        myToolWindow = toolWindow
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val view = CakeTasksWindow(project, toolWindow as ToolWindowEx)
        val content = contentFactory.createContent(view, "", false)
        toolWindow.contentManager.addContent(content)
    }
}