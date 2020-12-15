package net.cakebuild.shared

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import net.cakebuild.run.CakeConfiguration
import net.cakebuild.run.CakeConfigurationType
import net.cakebuild.settings.CakeSettings
import java.nio.file.FileSystems
import java.util.*

class CakeProject(private val project: Project) {

    private val log = Logger.getInstance(CakeProject::class.java)

    fun getCakeFiles() = sequence {
        val extension = CakeSettings.getInstance(project).cakeFileExtension
        val projectDir: VirtualFile? = project.guessProjectDir()
        val bucket = Stack<VirtualFile>()
        if (projectDir == null) {
            return@sequence
        }
        bucket.add(projectDir)
        while (!bucket.isEmpty()) {
            val folder = bucket.pop()
            log.trace("searching for *.$extension in folder ${folder.path}")
            for (child in folder.children) {
                if (child.isDirectory) {
                    bucket.push(child)
                    continue
                }
                val ext = child.extension
                if (ext != null && ext.equals(extension, ignoreCase = true)) {
                    yield(CakeFile(project, child))
                }
            }
        }
    }

    class CakeFile(private val project: Project, val file: VirtualFile) {

        private val content by lazy { VfsUtil.loadText(file) }

        fun getTasks() = sequence {
            val regex = Regex(CakeSettings.getInstance(project).cakeTaskParsingRegex)
            val tasks = regex.findAll(content).map {
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

        fun run(mode: CakeTaskRunMode) {
            val runManager = project.getService(RunManager::class.java)
            val configurationType = ConfigurationTypeUtil.findConfigurationType(CakeConfigurationType::class.java)
            val fileSystems = FileSystems.getDefault()
            val projectPath = fileSystems.getPath(project.basePath!!)
            val path = projectPath.relativize(fileSystems.getPath(file.path))
            val cfgName = "${path.fileName}: $taskName"
            val runConfiguration = runManager.createConfiguration(cfgName, configurationType.cakeFactory)
            val cakeConfiguration = runConfiguration.configuration as CakeConfiguration
            val settings = CakeSettings.getInstance(project)
            cakeConfiguration.setOptions(path.toString(), taskName, settings.cakeVerbosity)

            val executor = when (mode) {
                CakeTaskRunMode.Debug -> DefaultDebugExecutor.getDebugExecutorInstance()
                CakeTaskRunMode.Run -> DefaultRunExecutor.getRunExecutorInstance()
                else -> {
                    runManager.addConfiguration(runConfiguration, true)
                    return
                }
            }

            ProgramRunnerUtil.executeConfiguration(runConfiguration, executor)
        }
    }

    enum class CakeTaskRunMode {
        Run,
        Debug,
        SaveConfigOnly
    }
}
