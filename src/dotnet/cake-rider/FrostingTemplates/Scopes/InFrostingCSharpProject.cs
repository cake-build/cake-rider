using System;

using JetBrains.ReSharper.Feature.Services.LiveTemplates.Scope;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace net.cakebuild.FrostingTemplates.Scopes;

// Defines a scope point, but has no inherent behaviour, other than to compare against
// other scope points. A template can declare that it requires this scope point, and
// the template will only be made available if a ScopeProvider "publishes" this scope
// point based on the current context (e.g. the project is a Frosting project)
public class InFrostingCSharpProject : InLanguageSpecificProject
{
    private static readonly Guid DefaultGuid = new Guid("D21A853C-0CCE-42D7-9581-0902075C1A46");
    private static readonly Guid QuickGuid = new Guid("46A26830-D90A-4762-9C72-7031EC31FA4E");

    public InFrostingCSharpProject()
        : base(JetBrains.ProjectModel.Properties.ProjectLanguage.CSHARP)
    {
        AdditionalSuperTypes.Add(typeof(InLanguageSpecificProject));
    }

    public override string PresentableShortName => "Frosting projects";

    public override PsiLanguageType RelatedLanguage => CSharpLanguage.Instance;

    // Define the name and UID of the QuickList we'll use for Frosting projects. Any
    // scope points that are subsets will appear in this QuickList (I think)
    public override string QuickListTitle => PresentableShortName;

    public override Guid QuickListUID => QuickGuid;

    public override Guid GetDefaultUID() => DefaultGuid;

    public override string ToString() => PresentableShortName;
}
