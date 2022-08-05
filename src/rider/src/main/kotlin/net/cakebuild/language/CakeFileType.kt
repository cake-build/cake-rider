package net.cakebuild.language

import com.intellij.openapi.fileTypes.LanguageFileType
import icons.CakeIcons

class CakeFileType : LanguageFileType(CakeLanguage) {

    override fun getName() = CakeLanguage.displayName
    override fun getDescription() = "Cake scripts"
    override fun getIcon() = CakeIcons.CakeFileType
    override fun getDefaultExtension() = EXTENSION

    companion object {
        const val EXTENSION = "cake"

        @JvmField
        val INSTANCE = CakeFileType()
    }
}
