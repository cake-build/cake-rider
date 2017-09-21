package net.cakebuild

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel

class CakeToolWindowFactory : ToolWindowFactory {
    private var myToolWindow: ToolWindow? = null
    private var myToolWindowContent: JPanel = ToolWindowContentForm().rootPanel

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        myToolWindow = toolWindow
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(myToolWindowContent, "", false)
        toolWindow.contentManager.addContent(content)
    }
}