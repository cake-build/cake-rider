package net.cakebuild.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class CakeFilePath {
    companion object {

        fun getCakeFilePath(project: Project): VirtualFile? {
            var baseDir = project.baseDir
            while (baseDir != null) {
                val cakeFile = baseDir.findChild("build.cake")
                if (cakeFile != null && cakeFile.exists())
                    return cakeFile
                baseDir = baseDir.parent
            }
            return null
        }
    }
}