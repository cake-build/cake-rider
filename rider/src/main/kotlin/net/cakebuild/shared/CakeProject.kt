package net.cakebuild.shared

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import net.cakebuild.run.CakeConfiguration
import net.cakebuild.run.CakeConfigurationType
import java.util.*

class CakeProject(private val project: Project) {
    fun getCakeFiles() = sequence {
        val projectDir = project.guessProjectDir()
        val bucket = Stack<VirtualFile>()
        if (projectDir != null) {
            bucket.add(projectDir)
        }
        while (!bucket.isEmpty()) {
            val folder = bucket.pop()
            for (child in folder.children) {
                if (child.isDirectory) {
                    bucket.push(child)
                    continue
                }
                val ext = child.extension
                // TODO: Can we make the extension configurable?
                if (ext != null && ext.equals("cake", ignoreCase = true)) {
                    yield(CakeFile(project, child))
                }
            }
        }
    }

    class CakeFile(private val project: Project, val file: VirtualFile) {

        private val content by lazy { VfsUtil.loadText(file) }

        fun getTasks() = sequence {
            val regex = Regex("Task\\s*?\\(\\s*?\"(.*?)\"\\s*?\\)")
            val tasks =  regex.findAll(content).map {
                CakeTask(project, file, it.groups[1]!!.value)
            }
            yieldAll(tasks)
        }
    }

    data class CakeTask(private val project: Project, val file: VirtualFile, val taskName: String) {

        // to make the task look good when placed in a tree-node.
        override fun toString(): String {
            return taskName
        }

        fun run() {
            val runManager = project.getComponent(RunManager::class.java)
            val configurationType = ConfigurationTypeUtil.findConfigurationType(CakeConfigurationType::class.java)
            val runConfiguration = runManager.createConfiguration(taskName, configurationType.cakeFactory)
            val cakeConfiguration = runConfiguration.configuration as CakeConfiguration
            cakeConfiguration.setOptions(file, taskName)
            runManager.addConfiguration(runConfiguration, false)
        }
    }
}
