@file:Suppress("UnstableApiUsage")

package net.cakebuild.language.codeInsight

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.InlayPresentationFactory
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.layout.panel
import net.cakebuild.language.psi.CakeTypes
import net.cakebuild.shared.CakeProject
import java.awt.Cursor
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JComponent

class TaskInlayHintsProvider : InlayHintsProvider<TaskInlayHintsProvider.Settings> {
    override val key: SettingsKey<Settings>
        get() = SettingsKey("net.cakebuild.language.codeInsight.TaskInlayHintsProvider")
    override val name
        get() = "Cake"
    override val previewText
        get() = """
            foo();
            Task("Default");
            bar();
            Task("Some Task")
              .Does(() => 
              {
              // some stuff...
              });
            baz();
        """.trimIndent()

    override fun createConfigurable(settings: Settings) = TaskInlayHintsConfigurable()

    override fun createSettings() = Settings()

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: Settings,
        sink: InlayHintsSink
    ) = TaskInlayHintsCollector(editor)

    class Settings

    class TaskInlayHintsConfigurable : ImmediateConfigurable {
        override val mainCheckboxText: String
            get() = "Cake Tasks"

        override fun createComponent(listener: ChangeListener): JComponent {
            return panel {
                row {
                    label("No settings, for now.")
                }
            }
        }

        override fun reset() {
        }
    }

    class TaskInlayHintsCollector(editor: Editor) : FactoryInlayHintsCollector(editor) {
        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            if (element.elementType == CakeTypes.TASK_NAME) {
                val taskName = element.text
                val elm = factory.mouseHandling(
                    factory.container(
                        factory.smallText("Run Task")
                    ),
                    object : (MouseEvent, Point) -> Unit {
                        override fun invoke(event: MouseEvent, point: Point) {
                            if (event.clickCount == 2) {
                                val project = element.containingFile.project
                                CakeProject.runCakeTarget(
                                    project,
                                    element.containingFile.virtualFile,
                                    taskName,
                                    CakeProject.CakeTaskRunMode.Run
                                )
                            }
                        }
                    },
                    object : InlayPresentationFactory.HoverListener {
                        var lastComponent: java.awt.Component? = null
                        var lastCursor: Cursor? = null

                        override fun onHover(event: MouseEvent, translated: Point) {
                            if (lastComponent == null) {
                                lastComponent = event.component
                                lastCursor = event.component.cursor
                            }
                            event.component.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        }

                        override fun onHoverFinished() {
                            if (lastComponent != null && lastCursor != null) {
                                lastComponent!!.cursor = lastCursor!!
                            }
                            lastComponent = null
                        }
                    }
                )
                sink.addBlockElement(
                    element.startOffset,
                    relatesToPrecedingText = false,
                    showAbove = true,
                    priority = 0,
                    presentation = elm
                )
            }

            return true
        }
    }
}
