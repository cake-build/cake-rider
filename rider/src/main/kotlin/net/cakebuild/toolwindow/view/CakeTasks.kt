package net.cakebuild.toolwindow.view

import com.intellij.openapi.vfs.VirtualFile
import java.util.*

data class CakeTasks(val cakeFile: VirtualFile?, val tasks: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CakeTasks

        if (cakeFile != other.cakeFile) return false
        if (!Arrays.equals(tasks, other.tasks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cakeFile?.hashCode() ?: 0
        result = 31 * result + Arrays.hashCode(tasks)
        return result
    }
}
