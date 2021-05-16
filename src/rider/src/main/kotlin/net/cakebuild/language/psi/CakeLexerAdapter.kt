package net.cakebuild.language.psi

import com.intellij.lexer.FlexAdapter

class CakeLexerAdapter : FlexAdapter(CakeLexer(null))
