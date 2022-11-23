package net.cakebuild.installers

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CakeVersionTests {

    // Numbered parts
    @Test
    fun `should get the set parts correctly`() {
        val v = CakeVersion(listOf(1, 2, 3))

        assertEquals(1, v.getPart(0))
        assertEquals(2, v.getPart(1))
        assertEquals(3, v.getPart(2))
    }

    @Test
    fun `should have all not-set parts set to zero`() {
        val v = CakeVersion(listOf(1))

        assertEquals(0, v.getPart(1))
        assertEquals(0, v.getPart(2))
        assertEquals(0, v.getPart(3))
        assertEquals(0, v.getPart(4))
    }

    // Named parts
    @Test
    fun `should have the major version set correctly`() {
        val v = CakeVersion(listOf(8, 14, 2))

        assertEquals(8, v.major)
    }

    @Test
    fun `should have the minor version set correctly`() {
        val v = CakeVersion(listOf(8, 14, 2))

        assertEquals(14, v.minor)
    }

    @Test
    fun `should have the patch version set correctly`() {
        val v = CakeVersion(listOf(8, 14, 2))

        assertEquals(2, v.patch)
    }

    // Parsing

    @Test
    fun `should throw on empty text`() {
        assertThrows(IllegalArgumentException::class.java) { CakeVersion.parse("") }
    }

    @Test
    fun `should throw on non version`() {
        assertThrows(NumberFormatException::class.java) { CakeVersion.parse("ThisIsNoVersion") }
    }

    @Test
    fun `should throw on non number versions postfix`() {
        assertThrows(NumberFormatException::class.java) { CakeVersion.parse("1.2.3xx") }
    }

    @Test
    fun `should throw on non number versions prefix`() {
        assertThrows(NumberFormatException::class.java) { CakeVersion.parse("1.2.xx3") }
    }

    @Test
    fun `should parse a major version`() {
        val v = CakeVersion.parse("2")

        assertEquals(2, v.major)
    }

    @Test
    fun `should parse a major_minor version`() {
        val v = CakeVersion.parse("3.5")

        assertEquals(3, v.major)
        assertEquals(5, v.minor)
    }

    @Test
    fun `should parse a major_minor_patch version`() {
        val v = CakeVersion.parse("7.103.54778")

        assertEquals(7, v.major)
        assertEquals(103, v.minor)
        assertEquals(54778, v.patch)
    }

    @Test
    fun `should parse a suffixed version`() {
        val verText = "0.55.34-beta2"
        val v = CakeVersion.parse(verText)

        assertEquals(0, v.getPart(0))
        assertEquals(55, v.getPart(1))
        assertEquals(34, v.getPart(2))
        assertEquals(2, v.getPart(3))
        assertEquals(verText, v.toString())
    }

    // Comparing

    @Test
    fun `should be equal on the same version-object`() {
        val v = CakeVersion(listOf(1, 2, 3))

        val actual = v == v

        assertEquals(true, actual)
    }

    @Test
    fun `should not be equal on null`() {
        val v = CakeVersion(listOf(1, 2, 3))

        @Suppress("SENSELESS_COMPARISON")
        val actual = v == null

        assertEquals(false, actual)
    }

    @ParameterizedTest(name = "{0} == {1}")
    @MethodSource("compareEqualsSource")
    fun `should compare different versions - equals`(left: CakeVersion, right: CakeVersion, expected: Boolean) {
        println("$left == $right ? Expected: $expected")
        val actual = left == right
        assertEquals(expected, actual)
    }

    @ParameterizedTest(name = "{0} < {1}")
    @MethodSource("compareLessSource")
    fun `should compare different versions - less`(left: CakeVersion, right: CakeVersion, expected: Boolean) {
        println("$left < $right ? Expected: $expected")
        val actual = left < right
        assertEquals(expected, actual)
    }

    @ParameterizedTest(name = "{0} <= {1}")
    @MethodSource("compareLessEqualSource")
    fun `should compare different versions - less or equal`(left: CakeVersion, right: CakeVersion, expected: Boolean) {
        println("$left <= $right ? Expected: $expected")
        val actual = left <= right
        assertEquals(expected, actual)
    }

    @ParameterizedTest(name = "{0} > {1}")
    @MethodSource("compareGreaterSource")
    fun `should compare different versions - greater`(left: CakeVersion, right: CakeVersion, expected: Boolean) {
        println("$left > $right ? Expected: $expected")
        val actual = left > right
        assertEquals(expected, actual)
    }

    @ParameterizedTest(name = "{0} >= {1}")
    @MethodSource("compareGreaterEqualSource")
    fun `should compare different versions - greater or equal`(
        left: CakeVersion,
        right: CakeVersion,
        expected: Boolean
    ) {
        println("$left >= $right ? Expected: $expected")
        val actual = left >= right
        assertEquals(expected, actual)
    }

    // special cases

    @Test
    fun `v0_38_5 should be less than v1_0_0-rc0001`() {
        val left = CakeVersion.parse("0.38.5")
        val right = CakeVersion.parse("1.0.0-rc0001")

        val actual = left < right

        assertEquals(true, actual)
    }

    companion object {
        @Suppress("Unused")
        @JvmStatic
        fun compareEqualsSource(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 3)), true),
                Arguments.of(CakeVersion(listOf(1)), CakeVersion(listOf(1, 0, 0)), true),
                Arguments.of(CakeVersion(listOf(1, 0, 0, 0)), CakeVersion(listOf(1)), true),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(2)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 4)), false),
                Arguments.of(CakeVersion.parse("1.0.0-alpha1"), CakeVersion.parse("1.0.0-beta1"), true)
            )

        @Suppress("Unused")
        @JvmStatic
        fun compareLessSource(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 3)), false),
                Arguments.of(CakeVersion(listOf(1)), CakeVersion(listOf(1, 0, 0)), false),
                Arguments.of(CakeVersion(listOf(1, 0, 0, 0)), CakeVersion(listOf(1)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(2)), true),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 4)), true),
                Arguments.of(CakeVersion.parse("1.0.0-alpha1"), CakeVersion.parse("1.0.0-beta1"), false)
            )

        @Suppress("Unused")
        @JvmStatic
        fun compareLessEqualSource(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 3)), true),
                Arguments.of(CakeVersion(listOf(1)), CakeVersion(listOf(1, 0, 0)), true),
                Arguments.of(CakeVersion(listOf(1, 0, 0, 0)), CakeVersion(listOf(1)), true),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(2)), true),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 4)), true),
                Arguments.of(CakeVersion.parse("1.0.0-alpha1"), CakeVersion.parse("1.0.0-beta1"), true)
            )

        @Suppress("Unused")
        @JvmStatic
        fun compareGreaterSource(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 3)), false),
                Arguments.of(CakeVersion(listOf(1)), CakeVersion(listOf(1, 0, 0)), false),
                Arguments.of(CakeVersion(listOf(1, 0, 0, 0)), CakeVersion(listOf(1)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(2)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 4)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 4)), CakeVersion(listOf(1, 2, 3)), true),
                Arguments.of(CakeVersion.parse("1.0.0-alpha1"), CakeVersion.parse("1.0.0-beta1"), false)
            )

        @Suppress("Unused")
        @JvmStatic
        fun compareGreaterEqualSource(): Stream<Arguments> =
            Stream.of(
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 3)), true),
                Arguments.of(CakeVersion(listOf(1)), CakeVersion(listOf(1, 0, 0)), true),
                Arguments.of(CakeVersion(listOf(1, 0, 0, 0)), CakeVersion(listOf(1)), true),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(2)), false),
                Arguments.of(CakeVersion(listOf(1, 2, 3)), CakeVersion(listOf(1, 2, 4)), false),
                Arguments.of(CakeVersion.parse("1.0.0-alpha1"), CakeVersion.parse("1.0.0-beta1"), true)
            )
    }
}
