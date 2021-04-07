package net.cakebuild.settings

import com.intellij.util.xmlb.XmlSerializer
import junit.framework.TestCase.assertEquals
import org.jdom.Element
import org.jdom.input.SAXBuilder
import org.jdom.output.Format
import org.jdom.output.XMLOutputter
import org.junit.Test
import java.io.StringWriter

class CakeSettingsTests {

    companion object {
        const val defaultSettingsXml = """
<CakeSettings>
  <option name="cakeFileExtension" value="cake" />
  <option name="cakeRunner" value="~/.dotnet/tools/dotnet-cake" />
  <option name="cakeRunnerOverrides">
    <map>
      <entry key="^.*windows.*${'$'}" value="${'$'}{USERPROFILE}\.dotnet\tools\dotnet-cake.exe" />
    </map>
  </option>
  <option name="cakeScriptSearchIgnores">
    <list>
      <option value=".*/tools/.*" />
    </list>
  </option>
  <option name="cakeScriptSearchPaths">
    <list>
      <option value="." />
    </list>
  </option>
  <option name="cakeTaskParsingRegex" value="Task\s*?\(\s*?&quot;(.*?)&quot;\s*?\)" />
  <option name="cakeVerbosity" value="normal" />
  <option name="downloadContentUrlBootstrapperNetCorePs" value="https://cakebuild.net/download/bootstrapper/dotnet-core/powershell" />
  <option name="downloadContentUrlBootstrapperNetCoreSh" value="https://cakebuild.net/download/bootstrapper/dotnet-core/bash" />
  <option name="downloadContentUrlBootstrapperNetFrameworkPs" value="https://cakebuild.net/download/bootstrapper/dotnet-framework/powershell" />
  <option name="downloadContentUrlBootstrapperNetFrameworkSh" value="https://cakebuild.net/download/bootstrapper/dotnet-framework/bash" />
  <option name="downloadContentUrlBootstrapperNetToolPs" value="https://cakebuild.net/download/bootstrapper/dotnet-tool/powershell" />
  <option name="downloadContentUrlBootstrapperNetToolSh" value="https://cakebuild.net/download/bootstrapper/dotnet-tool/bash" />
  <option name="downloadContentUrlConfigurationFile" value="https://cakebuild.net/download/configuration" />
</CakeSettings>
            """
    }

    @Test
    fun `should not throw serializing the whole settings`() {
        // given
        val sut = CakeSettings()

        // when
        val element = XmlSerializer.serialize(sut)

        // then
        assertXmlOutputEquals(defaultSettingsXml, element)
    }

    @Test
    fun `should not throw deserializing cakeRunnerOverrides`() {
        // this is the test for GH-112

        // given
        val settingsXml = """
<CakeSettings>
  <option name="cakeRunnerOverrides">
    <map>
      <entry key="someOS" value="build.ps1" />
      <entry key="someOtherOS" value="cake.bat" />
    </map>
  </option>
</CakeSettings>
            """.toXmlElement()

        // when
        val sut = XmlSerializer.deserialize(settingsXml, CakeSettings::class.java)
        val actual = sut.cakeRunnerOverrides

        // then
        val expected = mapOf(
            Pair("someOS", "build.ps1"),
            Pair("someOtherOS", "cake.bat")
        )
        assertEquals(expected.count(), actual.count())
        expected.entries.forEach {
            assertEquals(it.value, actual[it.key])
        }
    }

    private fun String.toXmlElement(): Element {
        val document = SAXBuilder().build(this.byteInputStream())
        return document.rootElement
    }

    private fun assertXmlOutputEquals(expected: String, element: Element) {
        val sw = StringWriter()
        val format = Format.getPrettyFormat()
        XMLOutputter(format).output(element, sw)
        val actual = sw.toString()

        sw.buffer.setLength(0)

        XMLOutputter(format).output(expected.toXmlElement(), sw)
        val sanitizedExpected = sw.toString()

        assertEquals(sanitizedExpected, actual)
    }
}
