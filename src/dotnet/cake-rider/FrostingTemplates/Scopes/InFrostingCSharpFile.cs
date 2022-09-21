using System;

namespace net.cakebuild.FrostingTemplates.Scopes;

// Defines a scope point, but has no inherent behaviour, other than to compare against
// other scope points. A template can declare that it requires this scope point, and
// the template will only be made available if a ScopeProvider "publishes" this scope
// point based on the current context
public class InFrostingCSharpFile : InFrostingCSharpProject
{
    private static readonly Guid DefaultGuid = new Guid("CC631767-24BD-4412-B731-C3D8514B9821");

    public override string PresentableShortName => "Frosting C# file";

    public override Guid GetDefaultUID() => DefaultGuid;
}
