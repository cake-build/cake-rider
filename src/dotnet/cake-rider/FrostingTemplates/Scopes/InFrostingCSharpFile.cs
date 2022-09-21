using System;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;

namespace net.cakebuild.FrostingTemplates.Scopes;

// Defines a scope point, but has no inherent behaviour, other than to compare against
// other scope points. A template can declare that it requires this scope point, and
// the template will only be made available if a ScopeProvider "publishes" this scope
// point based on the current context
public class InFrostingCSharpFile : InAnyLanguageFile
{
    private static readonly Guid DefaultGuid = new Guid("9A628B74-FDA7-4B9C-9A24-2087ABC01730");

    public override string PresentableShortName => "Frosting C# file";

    public override Guid GetDefaultUID() => DefaultGuid;

    public override string ToString() => PresentableShortName;
}
