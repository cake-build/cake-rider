package net.cakebuild.fileTypes

import com.intellij.lang.Language

object CakeLanguage : Language("Cake") {
    override fun isCaseSensitive() = true
}
