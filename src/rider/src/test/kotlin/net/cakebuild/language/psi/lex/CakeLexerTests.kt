package net.cakebuild.language.psi.lex

import com.intellij.testFramework.LexerTestCase
import net.cakebuild.language.psi.CakeLexerAdapter

/*
 This class extends a jUnit.framework.testCase
 hence, do NOT use @Test annotations here,
 and DO name all tests starting with test. (i.e. testSimple())

 Also, ktlint normally recognizes tests and allows having backtick-function names.
 However, due to the missing test-annotations, it does not. Hence the Suppression of
 the "function-naming" rule in each function.
 */
class CakeLexerTests : LexerTestCase() {
    override fun createLexer() = CakeLexerAdapter()

    override fun getDirPath() = "src/test/testData/net/cakebuild/language/psi/"

    @Suppress("ktlint:standard:function-naming")
    fun `test unknown word`() {
        doTest(
            "someWord",
            """
            CakeTokenType.UNKNOWN ('s')
            CakeTokenType.UNKNOWN ('o')
            CakeTokenType.UNKNOWN ('m')
            CakeTokenType.UNKNOWN ('e')
            CakeTokenType.UNKNOWN ('W')
            CakeTokenType.UNKNOWN ('o')
            CakeTokenType.UNKNOWN ('r')
            CakeTokenType.UNKNOWN ('d')
            """.trimIndent(),
        )
    }

    @Suppress("ktlint:standard:function-naming")
    fun `test eol-comment`() {
        doTest("// foo bar baz", "CakeTokenType.EOL_COMMENT ('// foo bar baz')")
    }

    @Suppress("ktlint:standard:function-naming")
    fun `test multi-line eol comment`() {
        val input =
            """
            ///////////////////////////////////////////////////////////////////////////////
            // ARGUMENTS
            ///////////////////////////////////////////////////////////////////////////////
            """.trimIndent()
        val expected =
            """
            CakeTokenType.EOL_COMMENT ('///////////////////////////////////////////////////////////////////////////////')
            WHITE_SPACE ('\n')
            CakeTokenType.EOL_COMMENT ('// ARGUMENTS')
            WHITE_SPACE ('\n')
            CakeTokenType.EOL_COMMENT ('///////////////////////////////////////////////////////////////////////////////')
            """.trimIndent()
        doTest(input, expected)
    }

    @Suppress("ktlint:standard:function-naming")
    fun `test multi-line block comment`() {
        val input =
            """
            /******************************************************************************
            ** ARGUMENTS
            *******************************************************************************/
            """.trimIndent()
        val expected =
            """
            CakeTokenType.BLOCK_COMMENT ('/*')
            CakeTokenType.BLOCK_COMMENT ('*****************************************************************************')
            CakeTokenType.BLOCK_COMMENT ('\n')
            CakeTokenType.BLOCK_COMMENT ('** ARGUMENTS')
            CakeTokenType.BLOCK_COMMENT ('\n')
            CakeTokenType.BLOCK_COMMENT ('*******************************************************************************/')
            """.trimIndent()
        doTest(input, expected)
    }

    @Suppress("ktlint:standard:function-naming")
    fun `test multi-empty lines`() {
        val input =
            """
            ///////////////////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////////////////



            """.trimIndent()
        val expected =
            """
            CakeTokenType.EOL_COMMENT ('///////////////////////////////////////////////////////////////////////////////')
            WHITE_SPACE ('\n\n')
            CakeTokenType.EOL_COMMENT ('///////////////////////////////////////////////////////////////////////////////')
            WHITE_SPACE ('\n\n\n')
            """.trimIndent()
        doTest(input, expected)
    }

    @Suppress("ktlint:standard:function-naming")
    fun `test task`() {
        val input = "Task(\"Default\")"
        val expected =
            """
            CakeTokenType.TASK_START ('Task("')
            CakeTokenType.TASK_NAME ('Default')
            CakeTokenType.TASK_END ('")')
            """.trimIndent()
        doTest(input, expected)
    }
}
