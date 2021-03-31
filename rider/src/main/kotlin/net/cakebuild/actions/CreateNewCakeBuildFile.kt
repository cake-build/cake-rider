package net.cakebuild.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import icons.CakeIcons

class CreateNewCakeBuildFile :
    CreateFileFromTemplateAction(
        "Cake File",
        "Adds a new Cake script file.",
        CakeIcons.CakeFileType
    ),
    DumbAware {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("Cake File")
            // one entry for each file template
            // .addKind("Display Name", Icon?, "template-name-from-plugin.xml"
            .addKind("Cake Build File", CakeIcons.CakeFileType, "Cake File")
    }

    override fun getActionName(directory: PsiDirectory, p1: String, p2: String) = "Cake File"
}
