package net.cakebuild.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class CakeTasksWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val view = CakeTasksWindow(project)
        val content = contentFactory.createContent(view, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun init(toolWindow: ToolWindow) {
        super.init(toolWindow)
        toolWindow.stripeTitle = "Cake Tasks"
    }
}
