package net.cakebuild.shared.ui

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import javax.swing.JComboBox

class VerbosityComboBox : JComboBox<String>() {
    companion object {
        const val DEFAULT: String = "Normal"
    }

    init {
        putClientProperty(DarculaUIUtil.COMPACT_PROPERTY, true)
        addItem("Quiet")
        addItem("Minimal")
        addItem(DEFAULT)
        addItem("Verbose")
        addItem("Diagnostic")
    }

    fun getVerbosity(): String {
        return ((selectedItem ?: DEFAULT) as String).lowercase()
    }

    fun setVerbosity(verbosity: String?) {
        val value = if (verbosity.isNullOrEmpty()) DEFAULT else verbosity
        var item: Any? = null
        val count: Int = itemCount
        for (i in 0 until count) {
            val o: String = getItemAt(i)
            if (value.equals(o, ignoreCase = true)) {
                item = o
                break
            }
        }
        if (null == item) {
            // add new..
            addItem(value)
            item = value
        }
        selectedItem = item
    }
}
