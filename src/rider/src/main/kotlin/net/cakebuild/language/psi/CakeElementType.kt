package net.cakebuild.language.psi

import com.intellij.psi.tree.IElementType
import net.cakebuild.language.CakeLanguage
import org.jetbrains.annotations.NonNls

class CakeElementType(
    @NonNls debugName: String,
) :
    IElementType(debugName, CakeLanguage)
