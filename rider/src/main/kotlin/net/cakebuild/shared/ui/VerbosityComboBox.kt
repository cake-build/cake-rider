package net.cakebuild.shared.ui

import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import javax.swing.JComboBox

class VerbosityComboBox : JComboBox<String>() {

    private val normalValue = "Normal"

    init {
        putClientProperty(DarculaUIUtil.COMPACT_PROPERTY, true)
        addItem("Quiet")
        addItem("Minimal")
        addItem(normalValue)
        addItem("Verbose")
        addItem("Diagnostic")
    }

    fun getVerbosity(): String {
        return ((selectedItem ?: normalValue) as String).toLowerCase()
    }

    fun setVerbosity(verbosity: String?) {
        val value = verbosity ?: normalValue
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
