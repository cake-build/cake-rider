using System.IO;
using System.Reflection;

using JetBrains.Application;
using JetBrains.Application.Settings;
using JetBrains.Diagnostics;
using JetBrains.Lifetimes;

namespace net.cakebuild.FrostingTemplates;

[ShellComponent]
public class FrostingTemplatesDefaultSettings : IHaveDefaultSettingsStream
{
    public string Name => "Frosting default LiveTemplates";

    public Stream GetDefaultSettingsStream(Lifetime lifetime)
    {
        var stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("net.cakebuild.FrostingTemplates.templates.dotSettings");
        Assertion.AssertNotNull(stream);
        lifetime.AddDispose(stream);
        return stream;
    }
}
