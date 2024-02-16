package net.cakebuild.language.psi

import com.intellij.psi.tree.IElementType
import net.cakebuild.language.CakeLanguage
import org.jetbrains.annotations.NonNls

class CakeTokenType(
    @NonNls debugName: String,
) :
    IElementType(debugName, CakeLanguage) {
    override fun toString(): String {
        return "CakeTokenType.${super.toString()}"
    }
}
