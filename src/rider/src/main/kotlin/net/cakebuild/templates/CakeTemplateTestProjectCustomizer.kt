package net.cakebuild.templates

import com.jetbrains.rider.projectView.actions.projectTemplating.backend.ReSharperProjectTemplateCustomizer
import icons.CakeIcons
import javax.swing.Icon

abstract class CakeTemplateTestProjectCustomizer : ReSharperProjectTemplateCustomizer {
    override val newIcon: Icon
        get() = CakeIcons.CakeTestProjectTemplate

    override val newName: String
        get() = categoryName

    class CakeAddinTest : CakeTemplateTestProjectCustomizer() {
        override val categoryName: String
            get() = "Cake Addin testproject"
    }
}
