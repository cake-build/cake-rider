package net.cakebuild.shared

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus.FAILED
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
import com.intellij.util.Consumer
import java.awt.Component
import java.net.URLEncoder

// Most of this was heavily inspired by
// https://github.com/exigow/intellij-gdscript/ ... /ReportSubmitter.kt
// and is there licensed under MIT by Jakub Błach (@exigow)

class GitHubErrorReporter : ErrorReportSubmitter() {
    private val log = Logger.getInstance(GitHubErrorReporter::class.java)

    override fun getReportActionText() = "Create an issue on GitHub"

    @Suppress("TooGenericExceptionCaught")
    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<SubmittedReportInfo>
    ): Boolean {

        try {
            if (events.isNotEmpty()) {
                val event = events.first()
                val issue = collectIssueDetails(event, additionalInfo)
                submitOnGithub(issue)
            }
        } catch (e: Exception) {
            log.error("Error submitting issue to GitHub.", e)
            consumer.consume(SubmittedReportInfo(FAILED))
            return false
        }
        consumer.consume(SubmittedReportInfo(NEW_ISSUE))
        return true
    }

    private fun collectIssueDetails(event: IdeaLoggingEvent, additionalInfo: String?) =
        Report(
            title = event.throwableText.lines().first(),
            pluginVersion = discoverPluginVersion(),
            ideVersion = discoverIdeaVersion(),
            os = System.getProperty("os.name"),
            additionalInfo = additionalInfo,
            stacktrace = event.throwableText.orCopyManuallyHint(maxLengthForStackTrace)
        )

    private fun String.orCopyManuallyHint(chars: Int): String {
        if (this.length < chars) {
            return this
        }

        return "\n\nStackTrace too long. Please copy the StackTrace here manually.\n\n"
    }

    private fun discoverPluginVersion() =
        (pluginDescriptor as? IdeaPluginDescriptor)?.version

    private fun discoverIdeaVersion() =
        "${ApplicationInfo.getInstance().fullVersion} (${ApplicationInfo.getInstance().build})"

    private fun submitOnGithub(report: Report) {
        val markdown = MarkdownDescriptionBaker.bake(report)
        val title = report.title ?: "no title"
        val url = GithubLinkGenerator.generateUrl(title, markdown)
        BrowserUtil.browse(url)
    }

    private object GithubLinkGenerator {
        fun generateUrl(title: String, body: String): String {
            val encodedTitle = encode(title)
            val encodedBody = encode(body)
            return "https://github.com/cake-build/cake-rider/issues/new" +
                "?labels=Bug" +
                "&title=$encodedTitle" +
                "&body=$encodedBody"
        }

        private fun encode(text: String) =
            URLEncoder.encode(text, "UTF-8")
    }

    private object MarkdownDescriptionBaker {
        fun bake(report: Report) = createEntries(report)
            .filterValues { it != null }
            .map { bakeAttribute(it.key, it.value!!) }
            .joinToString("\n")

        private fun createEntries(report: Report) = mapOf(
            "Plugin version" to report.pluginVersion,
            "IDE version" to report.ideVersion,
            "Operating system" to report.os,
            "Additional information" to report.additionalInfo,
            "Exception" to report.title,
            "Stacktrace" to report.stacktrace
        )

        private fun bakeAttribute(label: String, text: String) =
            if (isMultiline(text)) {
                "$label:\n```text\n$text\n```"
            } else {
                "$label: `$text`"
            }

        private fun isMultiline(text: String) =
            text.lines().size > 1
    }

    private data class Report(
        val title: String?,
        val pluginVersion: String?,
        val ideVersion: String?,
        val additionalInfo: String?,
        val stacktrace: String?,
        val os: String?
    )

    companion object {
        const val maxLengthForStackTrace = 5000 // GitHub does not like very long URLs
    }
}
