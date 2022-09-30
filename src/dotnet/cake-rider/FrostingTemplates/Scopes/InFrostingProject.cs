using System;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;

namespace net.cakebuild.FrostingTemplates.Scopes;

// Defines a scope point, but has no inherent behaviour, other than to compare against
// other scope points. A template can declare that it requires this scope point, and
// the template will only be made available if a ScopeProvider "publishes" this scope
// point based on the current context (e.g. the project is a Frosting project)
// Since this scope point derives from IMandatoryScopePoint all templates
// that require this scope point will only be available if this scope point is published
// for the current context, regardless which other scope points are also required.
public class InFrostingProject : InAnyProject, IMandatoryScopePoint
{
    private static readonly Guid DefaultGuid = new Guid("D21A853C-0CCE-42D7-9581-0902075C1A46");
    private static readonly Guid QuickGuid = new Guid("46A26830-D90A-4762-9C72-7031EC31FA4E");

    public override string PresentableShortName => "Frosting projects";

    // Define the name and UID of the QuickList we'll use for Frosting projects. Any
    // scope points that are subsets will appear in this QuickList (I think)
    public override string QuickListTitle => PresentableShortName;

    public override Guid QuickListUID => QuickGuid;

    public override Guid GetDefaultUID() => DefaultGuid;

    public override string ToString() => PresentableShortName;
}
