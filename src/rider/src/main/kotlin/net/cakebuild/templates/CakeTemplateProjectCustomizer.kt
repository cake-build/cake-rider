package net.cakebuild.templates

import com.jetbrains.rider.projectView.actions.projectTemplating.backend.ReSharperProjectTemplateCustomizer
import icons.CakeIcons
import javax.swing.Icon

abstract class CakeTemplateProjectCustomizer : ReSharperProjectTemplateCustomizer {
    override val newIcon: Icon
        get() = CakeIcons.CakeProjectTemplate

    override val newName: String
        get() = categoryName

    class CakeAddin : CakeTemplateProjectCustomizer() {
        override val categoryName: String
            get() = "Cake Addin"
    }

    class CakeModule : CakeTemplateProjectCustomizer() {
        override val categoryName: String
            get() = "Cake Module"
    }

    class CakeFrostingBuild : CakeTemplateProjectCustomizer() {
        override val categoryName: String
            get() = "Cake Frosting Build Project"
    }
}
