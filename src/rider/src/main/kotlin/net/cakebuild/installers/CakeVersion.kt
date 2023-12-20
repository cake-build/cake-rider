package net.cakebuild.installers

import java.lang.Integer.parseInt
import kotlin.math.max

/**
 * represents a version that can be compared to other versions.
 */
class CakeVersion internal constructor(private val parts: List<Int>) : Comparable<CakeVersion> {
    companion object {
        private val suffixMatcher = Regex("-(\\D+)(\\d+)") // https://regex101.com/r/nrjWum/1

        /**
         * parses a version string into a @see Version.
         * @param text the sting to parse.
         */
        @Suppress("CHANGING_ARGUMENTS_EXECUTION_ORDER_FOR_NAMED_VARARGS")
        fun parse(text: String): CakeVersion {
            require(!text.isEmpty()) { "text can not be empty." }
            var suffixText: String? = null
            var suffix: Int? = null
            val suffixMatch = this.suffixMatcher.find(text)
            if (suffixMatch != null) {
                suffixText = suffixMatch.groups[1]!!.value
                suffix = parseInt(suffixMatch.groups[2]!!.value)
            }

            val txtParts = text.split("-")[0].split(".")
            val parts =
                txtParts.map {
                    parseInt(it)
                }

            val version = CakeVersion(parts)

            if (suffixText !== null) {
                version.suffixTxt = suffixText
                version.suffix = suffix
            }

            return version
        }
    }

    private var suffixTxt: String? = null
    private var suffix: Int? = null

    override fun toString(): String {
        var s = parts.joinToString(".")
        if (suffixTxt != null) {
            s += "-" + this.suffixTxt + this.suffix
        }

        return s
    }

    override operator fun compareTo(other: CakeVersion): Int {
        val count = max(this.partCount, other.partCount)
        for (i in 0 until count) {
            val left = this.getPart(i)
            val right = other.getPart(i)

            if (left > right) return 1
            if (left < right) return -1
        }

        return 0
    }

    /**
     * returns the n-th part of the version, counted from left, starting at 0.
     * (so "major" would be 0, "minor" would be 1).
     * @param i the part-number of the version, or 0 if the part does not exist.
     */
    fun getPart(i: Int): Int {
        if (i < this.parts.count()) {
            return this.parts[i]
        }
        if (i == this.parts.count() && this.suffix != null) {
            return this.suffix!!
        }

        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CakeVersion

        val count = max(parts.count(), other.parts.count())

        for (i in 0 until count) {
            val l = getPart(i)
            val r = other.getPart(i)
            if (l != r) return false
        }
        if (suffix != other.suffix) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parts.hashCode()
        result = 31 * result + (suffix ?: 0)
        return result
    }

    /**
     * gets the count of the parts of this version.
     */
    val partCount: Int
        get() = this.parts.count() + (if (this.suffix === null) 0 else 1)

    /**
     * returns the major version number. Equal to calling `getPart(0)`.
     */
    val major: Int
        get() = this.getPart(0)

    /**
     * returns the minor version number. Equal to calling `getPart(1)`.
     */
    val minor: Int
        get() = this.getPart(1)

    /**
     * returns the patch version number. Equal to calling `getPart(2)`.
     */
    val patch: Int
        get() = this.getPart(2)
}
