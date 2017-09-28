package net.cakebuild.toolwindow.view

import java.util.*

data class CakeTasks(val filename: String, val tasks: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CakeTasks

        if (filename != other.filename) return false
        if (!Arrays.equals(tasks, other.tasks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + Arrays.hashCode(tasks)
        return result
    }
}