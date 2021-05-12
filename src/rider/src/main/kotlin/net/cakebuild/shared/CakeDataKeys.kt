package net.cakebuild.shared

import com.intellij.openapi.actionSystem.DataKey
import net.cakebuild.toolwindow.CakeTasksWindow

class CakeDataKeys private constructor() {
    companion object {
        val TASKS_WINDOW: DataKey<CakeTasksWindow> = DataKey.create("cake.tasks.window")
    }
}
