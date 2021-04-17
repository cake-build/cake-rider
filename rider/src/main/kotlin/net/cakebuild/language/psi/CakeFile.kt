package net.cakebuild.language.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import net.cakebuild.language.CakeFileType
import net.cakebuild.language.CakeLanguage

class CakeFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, CakeLanguage) {
    override fun getFileType(): FileType {
        return CakeFileType.INSTANCE
    }

    override fun toString(): String {
        return "Cake File"
    }
}
