package net.cakebuild.shared

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.rider.projectView.hasSolution
import com.jetbrains.rider.projectView.solutionDirectory
import net.cakebuild.run.CakeConfiguration
import net.cakebuild.run.CakeConfigurationType
import net.cakebuild.settings.CakeSettings
import java.nio.file.FileSystems
import java.util.Stack

class CakeProject(private val project: Project) {

    private val log = Logger.getInstance(CakeProject::class.java)

    fun getProjectDir(): VirtualFile? {
        var projectDir = project.guessProjectDir()
        if (project.hasSolution) {
            // projectDir is weird, if a solution is loaded (probably because
            // guessProjectDir is intellij-code and does not know about rider and solutions)
            projectDir = LocalFileSystem.getInstance().findFileByIoFile(project.solutionDirectory)
        }
        return projectDir
    }

    private fun getSearchPaths(settings: CakeSettings, projectDir: VirtualFile): Collection<VirtualFile> {
        return settings.cakeScriptSearchPaths.mapNotNull {
            // VfsUtil.findRelativeFile does not work for "./foo" or "../foo"
            val parts = it.split("/", "\\")
            var path: VirtualFile? = projectDir
            for (p in parts) {
                if (path == null) {
                    break
                }
                if (p == ".") {
                    continue
                }
                if (p == "..") {
                    path = path.parent
                    continue
                }

                val child = path.findChild(p)
                path = if (child == null) {
                    log.warn("could not access $p as child of ${path.path}")
                    null
                } else {
                    child
                }
            }
            path
        }
    }

    fun getCakeFiles() = sequence {
        val settings = CakeSettings.getInstance(project)
        val extension = settings.cakeFileExtension
        val projectDir = getProjectDir()
        if (projectDir == null) {
            log.warn("Unable to find a folder to search for cake files.")
            return@sequence
        }
        val searchPaths = getSearchPaths(settings, projectDir)
        val bucket = Stack<VirtualFile>()
        bucket.addAll(searchPaths)
        val excludePatterns = settings.cakeScriptSearchIgnores.map {
            Regex(it)
        }
        while (!bucket.isEmpty()) {
            val folder = bucket.pop()
            log.trace("searching for *.$extension in folder ${folder.path}")
            children@ for (child in folder.children) {
                val normalizedPath = child.path.replace("\\", "/")
                for (exclude in excludePatterns) {
                    if (normalizedPath.matches(exclude)) {
                        log.trace("$normalizedPath excluded by pattern ${exclude.pattern}")
                        continue@children
                    }
                }
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

        fun run(mode: CakeTaskRunMode) {
            val runManager = project.getService(RunManager::class.java)
            val configurationType = ConfigurationTypeUtil.findConfigurationType(CakeConfigurationType::class.java)
            val fileSystems = FileSystems.getDefault()
            val projectPath = fileSystems.getPath(project.basePath!!)
            val path = projectPath.relativize(fileSystems.getPath(file.path))
            val cfgName = runManager.suggestUniqueName("${path.fileName}: $taskName", configurationType)
            val runConfiguration = runManager.createConfiguration(cfgName, configurationType.cakeFactory)
            val cakeConfiguration = runConfiguration.configuration as CakeConfiguration
            val settings = CakeSettings.getInstance(project)
            cakeConfiguration.setOptions(path.toString(), taskName, settings.cakeVerbosity)

            val executor = when (mode) {
                CakeTaskRunMode.Debug -> DefaultDebugExecutor.getDebugExecutorInstance()
                CakeTaskRunMode.Run -> DefaultRunExecutor.getRunExecutorInstance()
                else -> {
                    runConfiguration.storeInDotIdeaFolder()
                    runManager.addConfiguration(runConfiguration)
                    runManager.selectedConfiguration = runConfiguration
                    null
                }
            }

            if (executor != null) {
                ProgramRunnerUtil.executeConfiguration(runConfiguration, executor)
            }
        }
    }

    enum class CakeTaskRunMode {
        Run,
        Debug,
        SaveConfigOnly
    }
}
