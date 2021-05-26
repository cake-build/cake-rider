package net.cakebuild.language

import com.intellij.lang.Language

object CakeLanguage : Language("Cake") {
    override fun isCaseSensitive() = true
}
