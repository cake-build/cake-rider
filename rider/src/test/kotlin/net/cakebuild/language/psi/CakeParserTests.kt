package net.cakebuild.language.psi

import com.intellij.testFramework.ParsingTestCase
import net.cakebuild.language.CakeParserDefinition

/*
 This class extends a jUnit.framework.testCase
 hence, to NOT use @Test annotations here,
 and DO name all tests starting with test. (i.e. testSimple())
 */
class CakeParserTests : ParsingTestCase(
    "net/cakebuild/language/psi/",
    "cake",
    CakeParserDefinition()
) {

    /**
     * @return path to test data file directory relative to root of this module.
     */
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    override fun skipSpaces(): Boolean {
        return false // if not, newlines are garbled. Need to make that better
    }

    override fun includeRanges(): Boolean {
        return true
    }

    fun testCakeSample() {
        doTest(true)
    }

    fun testSimpleSample1() {
        doTest(true)
    }

    fun testSimpleSample2() {
        doTest(true)
    }

    fun testSimpleSample3() {
        doTest(true)
    }

    fun testSimpleSample4() {
        doTest(true)
    }
}
